package jpabook.jpashop.repository.order.query;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * 화면 종속적인 조회 위주 기능은 레포지토리를 활용하여 기능 간을 분리한다
 */
@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos() {

        // toOne 관계는 한방 쿼리로 가져오는 편이 낫다
        List<OrderQueryDto> resultList = findOrders();

        // toMany 관계는 카티션곱이 일어나기 때문에 따로 N번 돌린다.
        //  => 이 또한 1+N번의 쿼리가 일어나는 셈이다. 최적화가 필요한 지점이다.
        return resultList.stream()
                .map(oqd -> setOrderItemQueryDtoToOrderQueryDto(oqd))
                .collect(Collectors.toList());
    }

    private OrderQueryDto setOrderItemQueryDtoToOrderQueryDto(OrderQueryDto oqd) {
        oqd.setOrderItems(findOrderItems(oqd.getOrderId()));
        return oqd;
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery(
                "select "
                        + " new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, oi.item.name, oi.orderPrice, oi.count)"
                        + " from OrderItem oi "
                        + " join oi.item i "
                        + " where oi.order.id = :orderId ", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    private List<OrderQueryDto> findOrders() {
        return em.createQuery(
                        "select "
                                + " new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address) "
                                + " from Order o "
                                + " join o.member m"
                                + " join o.delivery d ", OrderQueryDto.class)
                .getResultList();
    }


    public List<OrderQueryDto> findAllByDtoOptimization() {
        List<OrderQueryDto> orders = findOrders();

        List<Long> orderIds = orders.stream()
                .map(OrderQueryDto::getOrderId)
                .collect(Collectors.toList());

        List<OrderItemQueryDto> orderItems = em.createQuery(
                        "select "
                                + " new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, oi.item.name, oi.orderPrice, oi.count)"
                                + " from OrderItem oi "
                                + " join oi.item i "
                                + " where oi.order.id in :orderIds ", OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds)
                .getResultList();

        // 두 개의 리스트에 대하여 이중 반복문이 필요할 때, 한 리스트를 맵으로 묶어서 처리할 수도 있다(돌아가는 건 똑같지만 소스코드가 조금 깔끔해보인다).
        Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream()
                .collect(Collectors.groupingBy(OrderItemQueryDto::getOrderId));

        orders.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));
        return orders;
    }
}
