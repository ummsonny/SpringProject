package jpabook.solo_jpashop.service.query;

import jpabook.solo_jpashop.api.OrderApiController;
import jpabook.solo_jpashop.domain.Address;
import jpabook.solo_jpashop.domain.Order;
import jpabook.solo_jpashop.domain.OrderStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter //에러에 property가 뜨면 @Data를 하거나 @Getter를 넣어줘라
public class OrderDto {

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
