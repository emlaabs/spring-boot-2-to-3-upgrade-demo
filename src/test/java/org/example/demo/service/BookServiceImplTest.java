package org.example.demo.service;

import org.example.demo.dto.BookResponse;
import org.example.demo.dto.CreateBookRequest;
import org.example.demo.model.Author;
import org.example.demo.repository.AuthorRepository;
import org.example.demo.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepo;

    @Mock
    private AuthorRepository authorRepo;

    @InjectMocks
    private org.example.demo.service.impl.BookServiceImpl service;

    @Test
    void testCreateBook() {
        Author author = new Author();
        author.setId(1L);
        author.setName("Eric");

        when(authorRepo.findById(1L)).thenReturn(Optional.of(author));

        CreateBookRequest req = new CreateBookRequest();
        req.setTitle("New Book");
        req.setAuthorId(1L);

        BookResponse response = service.createBook(req);

        assertEquals("New Book", response.getTitle());
        assertEquals("Eric", response.getAuthorName());
    }
}
