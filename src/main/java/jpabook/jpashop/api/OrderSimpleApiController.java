package jpabook.jpashop.api;

import java.util.List;
import java.util.stream.Collectors;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * [ManyToOne, OneToOne 성능 최적화]
 * Order
 * Order -> Member
 * Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        return orderRepository.findAll(new OrderSearch()).stream()
                .map(o -> {
                    o.getMember().getName();        // 지연로딩을 해오도록 하기 위한
                    o.getDelivery().getAddress();   // 강제 조회
                    return o;
                }).collect(Collectors.toList());


    }


}
