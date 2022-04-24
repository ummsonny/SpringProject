package jpabook.solo_jpashop.api;


import jpabook.solo_jpashop.domain.Address;
import jpabook.solo_jpashop.domain.Order;
import jpabook.solo_jpashop.domain.OrderSearch;
import jpabook.solo_jpashop.domain.OrderStatus;
import jpabook.solo_jpashop.repository.OrderRepository;
import jpabook.solo_jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.solo_jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;


/**
 *
 * xToOne(ManyToOne, OneToOne) 관계 최적화
 * Order
 * Order -> Member
 * Order -> Delivery
 *
 */

@RestController //Controller가 아니라 RestController이다. 그래야 @Mapping에서 return값을 html이 아닌 데이터를 반환할 수 있다.
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    /**
     * V1. 엔티티 직접 노출
     * - Hibernate5Module 모듈 등록, LAZY=null 처리
     * - 양방향 관계 문제 발생 -> @JsonIgnore
     */

    /**
     * 양방향 관계 발생으로 인해 무한 루프 된다.
     *  해결 -> Member의 order, Delivery의 order, orderItem의 order에 @JsonIgnore걸어야함
     */
    @GetMapping("api/v1/simple-orders")
    public List<Order> orderV1(){
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName(); //Lazy 강제 초기화
            order.getDelivery().getAddress(); //Lazy 강제 초기화
        }
        return all;
    }

    //==================================================//

    /**
     * V2. 엔티티를 조회해서 DTO로 변환(fetch join 사용X)
     * - 단점: 지연로딩으로 쿼리 N번 호출
     */
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() {
        //ORDER 2개
        //N+1 -> 1 + 회원 N + 배송 N
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(toList());

        return result;
    }

    @Data
    static class SimpleOrderDto {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate; //주문시간
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); //Lazy 초기화
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); //Lazy 초기화
        }
    }

    //==================================================//

    /**
     * V3. 엔티티를 조회해서 DTO로 변환(fetch join 사용O)
     * - fetch join으로 쿼리 1번 호출
     * 참고: fetch join에 대한 자세한 내용은 JPA 기본편 참고(정말 중요함)
     */
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(toList());
        return result;
    }

    /**
     * V4. 엔티티가 아닌 **DTO로 바로 조회**
     */
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4() {
        return orderSimpleQueryRepository.findOrderDtos();
    }
}
