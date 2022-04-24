package jpabook.solo_jpashop.service.query;

import jpabook.solo_jpashop.domain.OrderItem;
import lombok.Data;

@Data
public class OrderItemDto {

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
