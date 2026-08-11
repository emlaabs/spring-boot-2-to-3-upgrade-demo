package org.example.demo.repository;

import org.example.demo.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByTitle(String title);
    List<Book> findAllByOrderByTitleAsc();
    List<Book> findByAuthorName(String name);

}
