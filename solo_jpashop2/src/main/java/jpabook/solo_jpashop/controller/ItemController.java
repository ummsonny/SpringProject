package jpabook.solo_jpashop.controller;

import jpabook.solo_jpashop.domain.item.Book;
import jpabook.solo_jpashop.domain.item.Item;
import jpabook.solo_jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping(value = "items/new")
    public String createForm(Model model) {
        model.addAttribute("form", new BookForm());
        return "items/createItemForm";
    }

    @PostMapping(value = "items/new")
    public String create(BookForm form) {
        Book book = new Book();
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());
        //위처럼 setter는 안좋아 앞에 배원던 것 처럼 static createBook이라는 메소드 만들어서
        //하는게 좋음. 실무에서는 setter다 날린다.

        itemService.saveItem(book);
        return "redirect:/";
    }

    /**
     * 상품 목록
     */
    @GetMapping(value = "items")
    public String list(Model model) {
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);
        return "items/itemList";
    }

    /**
     * 상품 수정 폼
     */

    @GetMapping(value = "items/{itemId}/edit")
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model) {
        Book item = (Book) itemService.findOne(itemId);

        BookForm form = new BookForm();
        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setStockQuantity(item.getStockQuantity());
        form.setAuthor(item.getAuthor());
        form.setIsbn(item.getIsbn());

        model.addAttribute("form", form);
        return "items/updateItemForm";
    }


//    /**
//     * 상품 수정
//     */
//    @PostMapping(value = "items/{itemId}/edit")
//    public String updateItem(@ModelAttribute("form"), BookForm form){
//
//        Book book = new Book();
//
//        book.setId(form.getId());
//        book.setName(form.getName());
//        book.setPrice(form.getPrice());
//        book.setStockQuantity(form.getStockQuantity());
//        book.setAuthor(form.getAuthor());
//        book.setIsbn(form.getIsbn());
//
//        itemService.saveItem(book);
//        return "redirect:/items";
//    }

    /**
     * 상품 수정 권장 코드
     */

    @PostMapping(value = "items/{itemId}/edit")
    public String updateItem(@ModelAttribute("form") BookForm form){
        //아래 코드를 보면 이름,가격만 수정되게 하였지만 내가 나머지 필드는 밑에 매개변수로 추가해줄 수 있다.
        itemService.updateItem(form.getId(), form.getName(), form.getPrice());
        return "redirect:/items";
    }
}
