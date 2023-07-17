package com.timgapps.Project2Boot.controllers;

import com.timgapps.Project2Boot.models.Book;
import com.timgapps.Project2Boot.models.Person;
import com.timgapps.Project2Boot.services.BooksService;
import com.timgapps.Project2Boot.services.PeopleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/books")
public class BooksController {
    private BooksService booksService;
    private PeopleService peopleService;

    @Autowired
    public BooksController(BooksService booksService, PeopleService peopleService) {
        this.booksService = booksService;
        this.peopleService = peopleService;
    }

    @GetMapping()
    public String index(Model model, @RequestParam(value = "page", required = false) Integer page,
                        @RequestParam(value = "books_per_page", required = false) Integer booksPerPage,
                        @RequestParam(value = "sort_by_year", required = false) boolean sortByYear) {

        if (page == null || booksPerPage == null) {
            model.addAttribute("books", booksService.findAll(sortByYear));  // выдача всех книг
        } else {
            model.addAttribute("books", booksService.findWithPagination(page, booksPerPage, sortByYear));
        }
        // получим все книги из DAO и передадим на отображение в представление(положим в модель)
//        model.addAttribute("books", bookDAO.index());
        return "books/index";
    }

    // если книга принадлежит какому-то человеку, то мы должны показывать этого человека
    // если же книга свободна, мы должны показывать выпадающий список из людей, чтобы с помощью select'а мы могли назначить эту книгу человеку
    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model, @ModelAttribute("person") Person person) {
        model.addAttribute("book", booksService.findOne(id));

        Person bookOwner = booksService.getBookOwner(id);


        if (bookOwner != null) {
            model.addAttribute("owner", bookOwner);  // если есть владелец, в модель кладем владельца книги
        } else {
            model.addAttribute("people", peopleService.findAll()); // если нет владельца у книги, значит книга свободна, значит в этой странице нужно
            // показать выпадающий список
            // т.е. в модель будет положен весь список людей
        }
        return "books/show";
    }

    @GetMapping("/new")
    public String newBook(@ModelAttribute("book") Book book) {
        return "books/new";
    }

    @GetMapping("/search")
    public String searchPage() {
        return "books/search";
    }

    @PostMapping("/search")
    public String makeSearch(Model model, @RequestParam("query") String query) {
        model.addAttribute("books", booksService.searchByTitle(query));
        return "books/search";
    }

    @PostMapping()
    public String create(@ModelAttribute("book") @Valid Book book
            , BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "books/new";
        }
        booksService.save(book);
        return "redirect:/books";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("book", booksService.findOne(id));  // кладем в модель
        return "books/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("book") @Valid Book book, // с помощью @ModelAttribute принимаем объект "book"
                         BindingResult bindingResult,
                         @PathVariable("id") int id) {
        if (bindingResult.hasErrors())
            return "books/edit";

        booksService.update(id, book);
        return "redirect:/books";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        booksService.delete(id);
        return "redirect:/books";
    }

    // метод освобождает книгу при нажатии на кнопку
    // этот метод получает id книги И этой книги он убирает владельца (null)
    // у книги в БД в колонке во внешнем ключе будет назначено null
    @PatchMapping("/{id}/release")
    public String release(@PathVariable("id") int id) {
        booksService.release(id);
        return "redirect:/books/" + id;  // здесь простой redirect обратно на страницу книги с id
    }

    //    // метод назначает книгу человеку при нажатии на кнопку
//    // есть на странице книги выпадающий список и кнопка назначить человека (в том случае если книга свободна)
//    // этот выпадающий список находится в форме и эта форма отправляется с помощью patch запроса на этот метод контроллера
//    // и когда мы получаем из того выпадающего списка (select'a) нужного человека (делаем это с помощью аннотации @ModelAttribute
    @PatchMapping("/{id}/assign")
    public String assign(@PathVariable("id") int id, @ModelAttribute("person") Person selectedPerson) {
        // через @ModelAttribute получаем "человека" из выпадающего списка из представления
        // у selectedPerson назначено только поле id, остальное поля - null
        booksService.assign(id, selectedPerson);
        return "redirect:/books/" + id; // здесь простой redirect обратно на страницу книги с id
    }
}
