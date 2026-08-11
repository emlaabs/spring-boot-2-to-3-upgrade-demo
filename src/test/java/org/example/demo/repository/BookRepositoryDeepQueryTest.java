package org.example.demo.repository;

import org.example.demo.model.Author;
import org.example.demo.model.Book;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class BookRepositoryDeepQueryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private BookRepository repo;

    @Test
    void testFindByTitle() {
        Book b = new Book();
        b.setTitle("Hibernate 6 Guide");
        em.persist(b);

        List<Book> results = repo.findByTitle("Hibernate 6 Guide");
        assertEquals(1, results.size());
    }

    @Test
    void testOrdering() {
        Book b1 = new Book();
        b1.setTitle("A");
        em.persist(b1);

        Book b2 = new Book();
        b2.setTitle("B");
        em.persist(b2);

        List<Book> books = repo.findAllByOrderByTitleAsc();
        assertEquals("A", books.get(0).getTitle());
        assertEquals("B", books.get(1).getTitle());
    }

    @Test
    void testLazyLoading() {
        Book b = new Book();
        b.setTitle("Lazy Test");
        em.persist(b);

        em.flush();
        em.clear();

        Book found = repo.findAll().get(0);

        // Accessing a simple field is always safe
        assertEquals("Lazy Test", found.getTitle());
    }

    @Test
    void testFlushBehavior() {
        Book b = new Book();
        b.setTitle("Flush Test");
        em.persist(b);

        em.flush(); // forces SQL execution

        List<Book> books = repo.findAll();
        assertEquals(1, books.size());
    }

    @Test
    void testUpdate() {
        Book b = new Book();
        b.setTitle("Old Title");
        em.persist(b);

        b.setTitle("New Title");
        em.flush();

        List<Book> books = repo.findAll();
        assertEquals("New Title", books.get(0).getTitle());
    }

    @Test
    void testDelete() {
        Book b = new Book();
        b.setTitle("Delete Me");
        em.persist(b);

        repo.delete(b);
        em.flush();

        assertEquals(0, repo.findAll().size());
    }

    @Test
    void testMultipleEntities() {
        for (int i = 0; i < 5; i++) {
            Book b = new Book();
            b.setTitle("Book " + i);
            em.persist(b);
        }

        em.flush();

        List<Book> books = repo.findAll();
        assertEquals(5, books.size());
    }

    @Test
    void testCascadePersist() {
        Author author = new Author();
        author.setName("Eric");

        Book b1 = new Book();
        b1.setTitle("Book A");

        Book b2 = new Book();
        b2.setTitle("Book B");

        author.addBook(b1);
        author.addBook(b2);

        em.persist(author);
        em.flush();

        List<Book> books = repo.findAll();
        assertEquals(2, books.size());
    }

    @Test
    void testLazyLoadingAuthor() {
        Author author = new Author();
        author.setName("Lazy Author");

        Book book = new Book();
        book.setTitle("Lazy Book");
        author.addBook(book);

        em.persist(author);
        em.flush();
        em.clear();

        Book found = repo.findAll().get(0);

        // accessing LAZY field triggers a proxy load
        assertEquals("Lazy Author", found.getAuthor().getName());
    }

    @Test
    void testOrphanRemoval() {
        Author author = new Author();
        author.setName("Orphan Tester");

        Book book = new Book();
        book.setTitle("Orphan Book");
        author.addBook(book);

        em.persist(author);
        em.flush();

        author.removeBook(book);
        em.flush();

        assertEquals(0, repo.findAll().size());
    }

    @Test
    void testFindBooksByAuthorName() {
        Author author = new Author();
        author.setName("Hibernate Hero");

        Book book = new Book();
        book.setTitle("Hero Book");
        author.addBook(book);

        em.persist(author);

        List<Book> results = repo.findByAuthorName("Hibernate Hero");
        assertEquals(1, results.size());
    }

}
