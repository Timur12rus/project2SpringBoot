package com.timgapps.Project2Boot.dao;

import com.timgapps.Project2Boot.models.Book;
import com.timgapps.Project2Boot.models.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

// содержит всю логику работы с базой данных для модели Person
@Component
public class PersonDAO {

    private final JdbcTemplate jdbcTemplate;  // для взаимодействия с БД (низкоуровневый)

    @Autowired
    public PersonDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // возвращает список, который мы отобразим в браузере
    public List<Person> index() {
        return jdbcTemplate.query("SELECT * FROM Person", new BeanPropertyRowMapper<>(Person.class));
        // RowMapper - такой объект, который отображает строки из таблицы в наши сущности. Т.е. RowMapper каждую строку таблицы Person отобразит в объект Person()
    }

    public Person show(int id) {
        return jdbcTemplate.query("SELECT * FROM Person WHERE id=?", new Object[]{id}, new BeanPropertyRowMapper<>(Person.class)).stream().findAny().orElse(null);
    }

    public void save(Person person) {
        jdbcTemplate.update("INSERT INTO Person(full_name, year_of_birth) VALUES(?,?)", person.getFullName(), person.getYearOfBirth());
    }

    public void update(int id, Person updatedPerson) {
        jdbcTemplate.update("UPDATE Person SET full_name=?, year_of_birth=? WHERE id=?", updatedPerson.getFullName(), updatedPerson.getYearOfBirth(), id);

    }

    public void delete(int id) {
        jdbcTemplate.update("DELETE FROM Person WHERE id=?", id);
    }

    // Для валидации уникальности ФИО
    public Optional<Person> getPersonByFullName(String fullName) {
        return jdbcTemplate.query("SELECT * FROM Person WHERE full_name=?", new Object[]{fullName},
                new BeanPropertyRowMapper<>(Person.class)).stream().findAny();
    }

    // Здесь Join не нужен. И так уже получили человека с помощью отдельного метода
    // Метод нужен, чтобы получить список книг, которые взял этот человек
    public List<Book> getBooksByPersonId(int id) {
        // здесь id - это id того человека, на странице которого мы находимся
        return jdbcTemplate.query("SELECT * FROM Book WHERE person_id = ?", new Object[]{id},
                new BeanPropertyRowMapper<>(Book.class));
    }
}
