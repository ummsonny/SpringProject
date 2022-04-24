package jpabook.solo_jpashop.api;

import jpabook.solo_jpashop.domain.*;
import jpabook.solo_jpashop.repository.OrderRepository;
import jpabook.solo_jpashop.repository.order.query.OrderFlatDto;
import jpabook.solo_jpashop.repository.order.query.OrderItemQueryDto;
import jpabook.solo_jpashop.repository.order.query.OrderQueryDto;
import jpabook.solo_jpashop.repository.order.query.OrderQueryRepository;
import jpabook.solo_jpashop.service.query.OrderQueryService;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.ordering.antlr.OrderingSpecification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;


/**
 * V1. 엔티티 직접 노출
 * - 엔티티가 변하면 API 스펙이 변한다.
 * - 트랜잭션 안에서 지연 로딩 필요
 * - 양방향 연관관계 문제
 * <p>
 * V2. 엔티티를 조회해서 DTO로 변환(fetch join 사용X)
 * - 트랜잭션 안에서 지연 로딩 필요
 * V3. 엔티티를 조회해서 DTO로 변환(fetch join 사용O)
 * - 페이징 시에는 N 부분을 포기해야함(대신에 batch fetch size? 옵션 주면 N -> 1 쿼리로 변경 가능)
 * <p>
 * V4. JPA에서 DTO로 바로 조회, 컬렉션 N 조회 (1 + N Query)
 * - 페이징 가능
 * V5. JPA에서 DTO로 바로 조회, 컬렉션 1 조회 최적화 버전 (1 + 1 Query)
 * - 페이징 가능
 * V6. JPA에서 DTO로 바로 조회, 플랫 데이터(1Query) (1 Query)
 * - 페이징 불가능...
 */

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;


    /**
     * V1. 엔티티 직접 노출
     * - Hibernate5Module 모듈 등록, LAZY=null 처리
     * - 양방향 관계 문제 발생 -> @JsonIgnore
     */

    @GetMapping("api/v1/orders")
    public List<Order> orderV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) { //"iter" + TAB
            order.getMember().getName();//Lazy 강제 초기화
            order.getDelivery().getAddress(); //Lazy 강제 초기환

            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName()); //OrderItem(getItem())과 Item(getName()) 둘다 Lazy 강제 초기화
        }

        return all;
    }

    //================================================================//
    @GetMapping("api/v2/orders")
    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());
        return collect;
    }

    @Getter //에러에 property가 뜨면 @Data를 하거나 @Getter를 넣어줘라
    static class OrderDto {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        //private List<OrderItem> orderItems; 엔티티 직접 노출하면 안대!
        //객체 리스트도 그냥 Dto를 사용해서 복사한다고 보면 된다.
        private List<OrderItemDto> orderItems;


        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); //강제 초기화
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); //강제 초기화
            /**
             *   이것도 잘못됨. 완전히 엔티티에 대한 의존을 끊어야 한다.이것도 Dto만들어야함
             *   order.getOrderItems().stream().forEach(o -> o.getItem().getName());
             *   orderItems = order.getOrderItems();
             */
            orderItems = order.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(Collectors.toList());
        }

    }

    @Data
    static class OrderItemDto {

        //Dto는 원하는 데이터만 뽑을 수 있다!
        private String itemName;//상품 명
        private int orderPrice; //주문 가격
        private int count;      //주문 수량

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }

    //================================================================//
    /**
     * OSIV
    private final OrderQueryService orderQueryService;
    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3() {
        orderQueryService.ordersV3();
    }
     */

    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithItem();
        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());

        return result;
    }

    //================================================================//

    /**
     * V3.1 엔티티를 조회해서 DTO로 변환 페이징 고려
     * - ToOne 관계만 우선 모두 페치 조인으로 최적화
     * - 컬렉션 관계는 글로벌은-hibernate.default_batch_fetch_size, 개별은-@BatchSize로 최적화
     */
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                        @RequestParam(value = "limit", defaultValue = "100") int limit) {

        //List<Order> orders = orderRepository.findAllWithMemberDelivery();//최적화 안한 상태
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);
        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());

        return result;
    }

    @GetMapping("api/v4/orders")
    public List<OrderQueryDto> ordersV4() {
        return orderQueryRepository.findOrderQueryDtos();
    }

    @GetMapping("api/v5/orders")
    public List<OrderQueryDto> ordersV5() {
        return orderQueryRepository.findAllByDto_optimization();
    }

    @GetMapping("api/v6/orders")
    public List<OrderQueryDto> ordersV6(){
        List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();

        return flats.stream()
                .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(), o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()), //key값
                        mapping(o -> new OrderItemQueryDto(o.getOrderId(), o.getItemName(), o.getOrderPrice(), o.getCount()), toList()) // value값
                )).entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(), e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(), e.getKey().getAddress(), e.getValue()))
                .collect(toList());
    }
}
