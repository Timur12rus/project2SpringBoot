package com.timgapps.Project2Boot.controllers;

import com.timgapps.Project2Boot.models.Person;
import com.timgapps.Project2Boot.services.BooksService;
import com.timgapps.Project2Boot.services.PeopleService;
import com.timgapps.Project2Boot.util.PersonValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/people")
public class PeopleController {

    private final PeopleService peopleService;

    private final BooksService booksService;
    private final PersonValidator personValidator;


    @Autowired
    public PeopleController(PeopleService peopleService, BooksService booksService, PersonValidator personValidator) {
        this.peopleService = peopleService;
        this.booksService = booksService;
//    public PeopleController(PersonValidator personValidator, PersonDAO personDAO) {
//        this.personValidator = personValidator;

        this.personValidator = personValidator;
    }

    @GetMapping()
    public String index(Model model) {
        // получим всех людей из DAO и передадим на отображение в представление
        model.addAttribute("people", peopleService.findAll());
        return "people/index";
    }

    // получим одного человека из DAO и передадим этого человека на отображение в представление
    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model) {
        model.addAttribute("person", peopleService.findOne(id));
        // помимо человека, передаем в модель книги этого человека, т.к. должны в представлении отображать список его книг
        model.addAttribute("books", peopleService.findOne(id));

        return "people/show";
    }

    @GetMapping("/new")  //  с помощью @ModelAttribute принимаем объект "person"
    public String newPerson(@ModelAttribute("person") Person person) {
        return "people/new";
    }

    @PostMapping()
    public String create(@ModelAttribute("person") @Valid Person person, // если ошибка, то она передается в оъект bindingResult
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "people/new";
        }
        peopleService.save(person);
        return "redirect:/people";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("person", peopleService.findOne(id));   // кладем в модель объект "person"
        return "people/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("person") @Valid Person person,
                         BindingResult bindingResult,
                         @PathVariable("id") int id) { // с помощью @ModelAttribute принимаем объект "person"
        if (bindingResult.hasErrors()) {
            return "people/edit";
        }
        peopleService.update(id, person);
        return "redirect:/people";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        peopleService.delete(id);
        return "redirect:/people";
    }
}
