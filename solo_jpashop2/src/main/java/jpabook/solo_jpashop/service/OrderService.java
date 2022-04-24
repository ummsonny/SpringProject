package jpabook.solo_jpashop.service;

import jpabook.solo_jpashop.domain.*;
import jpabook.solo_jpashop.domain.item.Item;
import jpabook.solo_jpashop.repository.ItemRepository;
import jpabook.solo_jpashop.repository.MemberRepository;
import jpabook.solo_jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * 서비스는 order, item, member던지
 * (엔티티의 비지니스로직) + (repository기능) 을 합쳐 놓은 것을 구현하는 부분이다.
 * 즉, 주문서비스의 주문 및 취소를 보면 비지니스 로직이 대부분 엔티티에 있다.
 * 서비스 계층을 단순히 엔티티에 필요한 요청을 위임하는 역할임
 * 엔티티가 비지니스 로직을 가지고 객체 지향의 특성을 적극 활용하는 도메인 모델 패턴이다.
 * 도메인 패턴 <-> 트랜잭션 스크립트 패턴
 */

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;

    /** 주문 */
    @Transactional
    public Long order(Long memberId, Long itemId, int count){

        //엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        //배송정보 생성
        Delivery delivery= new Delivery();
        delivery.setAddress(member.getAddress());
        delivery.setStatus(DeliveryStatus.READY);

        //주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        //주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        //주문 저장
        //이렇게 해줘도 orderitem이랑 delivery가 자동 생성됨 by Cascade
        //같이 저장될 타이밍이니까
        orderRepository.save(order);

        return order.getId();
    }

    /**
     * 주문 취소
     */
    @Transactional
    public void cancelOrder(Long orderId){
        //주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);

        //주문 취소
        order.cancel();
    }

    /**
     * 주문 검색
     */
    public List<Order> findOrders(OrderSearch orderSearch){
        return orderRepository.findAllByCriteria(orderSearch);
    }
}
