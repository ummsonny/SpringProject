package jpabook.solo_jpashop.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Orders")
public class Order {

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    //==연관관계 메서드==// 주문애 회원, 주문상품, 배달 다 떼려밖음
    private void setMember(Member member){
        this.member = member;
        member.getOrders().add(this);
    }

    private void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    private void setDelivery(Delivery delivery){
        this.delivery=delivery;
        delivery.setOrder(this);
    }

    /**
     * 주문은 생성이 단순한게 아니라
     *  멤버 , 주문상품, 배달,등 연관관계가 복잡한 상태이다.
     * 이럴때 createOrder처럼 별도 생성메서드가 있으면 좋다.
     *  앞으로 뭔가 생성하는 부분을 바꿔야 한다면 여기만 수정하면 되므로 아주 간단해짐
     *  setter로 복잡하게 밖에서 할 필요가 없다.
     */
    //==생성 메서드==//
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderitems){
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);

        for(OrderItem orderItem : orderitems){
            order.addOrderItem(orderItem);
        }

        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    //==비지니스 로직==//
    /**
     * 주문 취소
     */
    public void cancel(){

        //배달
        if(delivery.getStatus() == DeliveryStatus.COMP){
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
        }
        this.setStatus(OrderStatus.CANCEL);

        //주문상품
        /**
          *주문 하나당 있는 item상품(들)처리 해줘야하므로 근데 여기서는
          *item관련된 직접적인 필드가 없다. orderitem를 이용해야한다.
          *orderitem이 order랑 item중간다리 역할이니
          *로직 처리를 넘기는 듯....
        */
         for(OrderItem orderItem : orderItems){
            orderItem.cancel();
        }
    }

    //==조회 로직==//
    /**w 전체 주문 가격 조회 */
    public int getTotalPrice(){
        int totalPrice = 0;
        for (OrderItem orderItem : orderItems){
            totalPrice+=orderItem.getTotalPrice();
        }
        return totalPrice;
    }
}
