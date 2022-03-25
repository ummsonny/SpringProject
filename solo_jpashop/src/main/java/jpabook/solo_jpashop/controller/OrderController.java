package jpabook.solo_jpashop.controller;


import jpabook.solo_jpashop.domain.Member;
import jpabook.solo_jpashop.domain.Order;
import jpabook.solo_jpashop.domain.OrderSearch;
import jpabook.solo_jpashop.domain.item.Item;
import jpabook.solo_jpashop.service.ItemService;
import jpabook.solo_jpashop.service.MemberService;
import jpabook.solo_jpashop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;

    @GetMapping(value = "order")
    public String createForm(Model model) {

        List<Member> members = memberService.findMembers();
        List<Item> items = itemService.findItems();

        model.addAttribute("members", members);
        model.addAttribute("items", items);

        return "order/orderForm";
    }

    @PostMapping(value = "order")
    public String order(@RequestParam("memberId") Long memberId, @RequestParam("itemId") Long itemId, @RequestParam("count") int count) {
        orderService.order(memberId, itemId, count);
        return "redirect:/orders";
    }

    /**
     * @ModelAttribute("orderSearch") OrderSearch orderSearch 여기에는
     * 주소 ? (쿼리부분) 에서 쿼리부분의 값들이 orderSearch객체에 들어간다.
     * @ModelAttribute를 쓰면 받는것도 되고 주는 것도 되는듯
     * 하지만 model.addAttribute("orders", orders); 처럼 하면 주는 것만 되는듯
     * But
     * 여기가 잘 작동 안했는데 이유 : https://www.inflearn.com/questions/131537
     * OrderSearch에 @Setter가 없어서 잘 동작을 안했다.
     * @Setter가 없으니 @ModelAttribute("orderSearch") 에서 OrderSearch의 객체의
     * 필드들값들이 자동으로 채워지지 않지!!!!!!
     */
    @GetMapping(value = "orders") //주문 내역 목록 여기는 MVC강의 들어야 이해 될듯
    public String orderList(@ModelAttribute("orderSearch") OrderSearch orderSearch, Model model) {
        // (받는거)@ModelAttribute("orderSearch") OrderSearch orderSearch
        // (주는거)model.addAttribute("orderSearch", orderSearch);

        List<Order> orders = orderService.findOrders(orderSearch);
        model.addAttribute("orders", orders);//(주는거)
        return "order/orderList";
    }


    @PostMapping(value = "orders/{orderId}/cancel")
    public String cancelOrder(@PathVariable("orderId") Long orderId) {
        orderService.cancelOrder(orderId);
        return "redirect:/orders";
    }
}
