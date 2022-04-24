package jpabook.solo_jpashop.repository.order.query;

import jpabook.solo_jpashop.domain.Address;
import jpabook.solo_jpashop.domain.OrderItem;
import jpabook.solo_jpashop.domain.OrderStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;


@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    /**
     * 컬렉션은 별도로 조회
     * Query: 루트 1번, 컬렉션 N 번
     * 단건 조회에서 많이 사용하는 방식
     */
    public List<OrderQueryDto> findOrderQueryDtos() {
        //루트 조회(toOne 코드를 모두 한번에 조회)
        List<OrderQueryDto> result = findOrders(); //쿼리 1번(결과는 2개)

        result.forEach(o->{//쿼리 총 2번
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
        });

        return result;
    }

    /**
     * 1:N 관계(컬렉션)를 제외한 나머지를 한번에 조회
     */
    private List<OrderQueryDto> findOrders(){
        return em.createQuery("select new jpabook.solo_jpashop.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                " from Order o" +
                " join o.member m" +
                " join o.delivery d", OrderQueryDto.class)
                .getResultList();
    }

    /**
     * 1:N 관계인 orderItems 조회
     * 앞의 엔티티 조회랑 다르게 item과 조인해서 조인하므로 앞에서 3번의 쿼리를 1번에 쿼리로 해결
     * 하는 점이 다르다.
     */
    private List<OrderItemQueryDto> findOrderItems(Long orderId){
        return em.createQuery("select new jpabook.solo_jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                " from OrderItem oi" +
                " join oi.item i" +
                " where oi.order.id = : orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }


    //========================================================================//
    
    /**
     * 최적화
     * Query: 루트 1번, 컬렉션 1번
     * 데이터를 한꺼번에 처리할 때 많이 사용하는 방식
     *
     */
    public List<OrderQueryDto> findAllByDto_optimization() {
        
        //루트 조회(toOne 코드를 모두 한번에 조회)
        List<OrderQueryDto> result = findOrders();
        
        //orderItem 컬렉션을 MAP 한방에 조회
        //orderId별로 orderItem리스트가 있다.
        Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemMap(toOrderIds(result));
        
        //루프를 돌면서 컬렉션 추가(추카 퀄리 실행X)
        result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));
        
        return result;
    }
    
    private List<Long> toOrderIds(List<OrderQueryDto> result){
        return result.stream()
                .map(o->o.getOrderId())
                .collect(toList());
    }
    
    private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<Long> orderIds){
        List<OrderItemQueryDto> orderItems = em.createQuery("select new jpabook.solo_jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                " from OrderItem oi" +
                " join oi.item i" +
                " where oi.order.id in :orderIds", OrderItemQueryDto.class) // 'in' 주목
                .setParameter("orderIds", orderIds)
                .getResultList();

        return orderItems.stream()
                .collect(Collectors.groupingBy(OrderItemQueryDto::getOrderId));
    }


    //========================================================================//

    /**
     * Flat방법
     */
    public List<OrderFlatDto> findAllByDto_flat() {

        return em.createQuery("select new jpabook.solo_jpashop.repository.order.query.OrderFlatDto(o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count)" +
                " from Order o" +
                " join o.member m" +
                " join o.delivery d" +
                " join o.orderItems oi" +
                " join oi.item i", OrderFlatDto.class)
                .getResultList();
    }
}
