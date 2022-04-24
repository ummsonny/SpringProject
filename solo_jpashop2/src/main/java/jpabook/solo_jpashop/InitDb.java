package jpabook.solo_jpashop;

import jpabook.solo_jpashop.domain.*;
import jpabook.solo_jpashop.domain.item.Book;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.ordering.antlr.OrderingSpecification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

/**
 * 종 주문 2개
 * * userA
 * 	 * JPA1 BOOK
 * 	 * JPA2 BOOK
 * * userB
 * 	 * SPRING1 BOOK
 * 	 * SPRING2 BOOK
 */
@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    @PostConstruct
    public void init(){
        initService.dbInit1();
        initService.dbInit2();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService{
        private final EntityManager em;

        public void dbInit1(){
            System.out.println("Init1" + this.getClass());
            Member member = createMember("userA", "서울", "1", "11111");
            em.persist(member);

            Book book1 = createBook("JPA1 BOOK", 10000, 100);
            em.persist(book1);

            Book book2 = createBook("JPA2 BOOK", 20000, 100);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000, 1);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 20000, 2);

            Delivery delivery = createDelivery(member);

            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);//cascade에 의해 OrderItem과 Delivery는 persist 안해줘도 저장
        }

        public void dbInit2(){
            Member member = createMember("userB", "진주", "2", "2222");
            em.persist(member);

            Book book1 = createBook("SPRING1 BOOK", 20000, 200);
            em.persist(book1);

            Book book2 = createBook("SPRING2 BOOK", 40000, 300);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 20000, 3);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 40000, 4);

            Delivery delivery = createDelivery(member);

            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);//cascade에 의해 OrderItem과 Delivery는 persist 안해줘도 저장
        }

        private Delivery createDelivery(Member member) {
            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());
            return delivery;
        }

        private Member createMember(String userA, String 서울, String s, String s2) {
            Member member = new Member();
            member.setName(userA);
            member.setAddress(new Address(서울, s, s2));
            return member;
        }

        private Book createBook(String s, int i, int i2) {
            Book book1 = new Book();
            book1.setName(s);
            book1.setPrice(i);
            book1.setStockQuantity(i2);
            return book1;
        }
    }
}
