package jpabook.jpashop.service;

import javax.persistence.EntityManager;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.DeliveryStatus;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;


    // 주문하기
    @Test
    void 주문하기() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "백범로", "40404"));
        em.persist(member);

        Item item = new Book();
        item.setName("자바조아");
        item.setStockQuantity(100);
        item.setPrice(10000);
        em.persist(item);

        int count = 10;

        Long orderId = orderService.order(member.getId(), item.getId(), count);
        Order find = orderRepository.findOne(orderId);

        // 상품 주문 시 상태는 ORDER
        Assertions.assertThat(find.getStatus()).isEqualTo(OrderStatus.ORDER);

        // 주문한 상품 종류 수가 정확해야 한다
        Assertions.assertThat(find.getOrderItems().size()).isEqualTo(1);

        // 주문한 금액 맞춰보기
        Assertions.assertThat(find.getTotalPrice()).isEqualTo(count * item.getPrice());

        // 주문한 만큼 수량이 줄어야 한다.가
        Assertions.assertThat(item.getStockQuantity()).isEqualTo(90);
    }

    // 주문 실패하기(재고 부족)
    @Test
    void 재고부족으로_주문실패() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "백범로", "40404"));
        em.persist(member);

        Item item = new Book();
        item.setName("자바조아");
        item.setStockQuantity(100);
        item.setPrice(10000);
        em.persist(item);

        int count = 100000000; // 초과된 주문 수량

        // 재고 수량보다 큰 주문 수량 요청이 들어오면 NotEnoughStockException 발생
        Assertions.assertThatThrownBy(() -> orderService.order(member.getId(), item.getId(), count))
                .isInstanceOf(NotEnoughStockException.class);
    }


    // 주문 취소하기
    @Test
    void 주문취소() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "백범로", "40404"));
        em.persist(member);

        Item item = new Book();
        item.setName("자바조아");
        item.setStockQuantity(100);
        item.setPrice(10000);
        em.persist(item);

        int count = 10;

        Long orderId = orderService.order(member.getId(), item.getId(), count);
        Order find = orderRepository.findOne(orderId);


        orderService.cancelOrder(orderId);

        // 상태가 취소가 됐나?
        Assertions.assertThat(orderRepository.findOne(orderId).getStatus())
                .isEqualTo(OrderStatus.CANCEL);

        // 수량이 돌아왔나?
        Assertions.assertThat(item.getStockQuantity()).isEqualTo(100);
    }

    @Test
    void 이미발송중이어서_주문취소실패() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "백범로", "40404"));
        em.persist(member);

        Item item = new Book();
        item.setName("자바조아");
        item.setStockQuantity(100);
        item.setPrice(10000);
        em.persist(item);

        int count = 10;
        Long orderId = orderService.order(member.getId(), item.getId(), count);
        Order find = orderRepository.findOne(orderId);
        find.getDelivery().setStatus(DeliveryStatus.COMP);

        // 이미 배송중이면 취소할 수 없다
        Assertions.assertThatThrownBy(() -> orderService.cancelOrder(orderId))
                .isInstanceOf(IllegalStateException.class);
    }
}