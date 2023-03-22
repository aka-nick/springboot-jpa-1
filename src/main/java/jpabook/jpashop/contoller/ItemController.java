package jpabook.jpashop.contoller;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/items/new")
    public String createForm(Model model) {
        model.addAttribute("form", new BookForm());
        return "items/createItemForm";
    }
    @PostMapping("/items/new")
    public String createForm(BookForm form) {
        Book book = new Book();
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());
        book.setStockQuantity(form.getStockQuantity());

        itemService.save(book);

        return "redirect:/items";
    }

    @GetMapping("/items")
    public String list(Model model) {
        model.addAttribute("items", itemService.findItems());
        return "items/itemList";
    }

    @GetMapping("/items/{itemId}/edit")
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model) {
        Book find = (Book) itemService.findOne(itemId);

        BookForm form = new BookForm();
        form.setId(find.getId());
        form.setName(find.getName());
        form.setPrice(find.getPrice());
        form.setIsbn(find.getIsbn());
        form.setAuthor(find.getAuthor());
        form.setStockQuantity(find.getStockQuantity());

        model.addAttribute("form", form);

        return "items/updateItemForm";
    }

}
