package jpabook.solo_jpashop.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jpabook.solo_jpashop.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "order_item")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) //기본생성자 protected로 함
public class OrderItem {

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice; //주문 가격
    private int count; //주문 수량

//    protected OrderItem(){ 방법1 인데 위에 처럼 롬복 이용이 좋다.
//    }

    //==생성 메서드==//
    public static OrderItem createOrderItem(Item item, int orderPrice, int count){
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        item.removeStock(count);
        return orderItem;
    }

    //==비지니스 로직==//
    /** 주문 최소 */
    public void cancel() {
        getItem().addStock(count);
    }

    //==조회 로직==//
    /** 주문상품 전체 가격 조회 */
    public int getTotalPrice(){
        return getOrderPrice() * getCount();
    }
}
