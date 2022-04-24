package jpabook.solo_jpashop.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * 이건 orderRepository에서 Jpql or JPA Criteris or Querydsl 파라미터로
 * 원하는 데이터만 뽑을라고 만든거임
 */

@Getter
@Setter
public class OrderSearch {
    private String memberName; //회원 이름
    private OrderStatus orderStatus;//주문 상태[ORDER, CANCEL]
//Getter, Setter
}