package jpabook.solo_jpashop.repository.order.query;

import jpabook.solo_jpashop.domain.Address;
import jpabook.solo_jpashop.domain.OrderStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;


@Data
@EqualsAndHashCode(of = "orderId") // 컬렉션 조회 최적화섹션의 마지막에서 2번째 강의 11:00
public class OrderQueryDto {

    private Long orderId;
    private String name;
    private LocalDateTime orderDate; //주문시간
    private OrderStatus orderStatus;
    private Address address;
    private List<OrderItemQueryDto> orderItems;


    //v5
    public OrderQueryDto(Long orderId, String name, LocalDateTime orderDate, OrderStatus orderStatus, Address address) {
        this.orderId = orderId;
        this.name = name;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.address = address;
    }

    //v6
    public OrderQueryDto(Long orderId, String name, LocalDateTime orderDate, OrderStatus orderStatus, Address address, List<OrderItemQueryDto> orderItems) {
        this.orderId = orderId;
        this.name = name;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.address = address;
        this.orderItems = orderItems;
    }
}
