package org.example.demo.service.impl;

import org.example.demo.dto.BookResponse;
import org.example.demo.dto.CreateBookRequest;
import org.example.demo.mapper.BookMapper;
import org.example.demo.model.Author;
import org.example.demo.model.Book;
import org.example.demo.repository.AuthorRepository;
import org.example.demo.repository.BookRepository;
import org.example.demo.service.BookService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepo;
    private final AuthorRepository authorRepo;

    public BookServiceImpl(BookRepository bookRepo, AuthorRepository authorRepo) {
        this.bookRepo = bookRepo;
        this.authorRepo = authorRepo;
    }

    @Override
    @Transactional
    public BookResponse createBook(CreateBookRequest request) {
        Author author = authorRepo.findById(request.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("Author not found"));

        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(author);

        bookRepo.save(book);

        return BookMapper.toResponse(book);
        //return new BookResponse(book.getId(), book.getTitle(), author.getName());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookResponse> getAllBooks() {
        return bookRepo.findAll()
                .stream()
                .map(BookMapper::toResponse)
                .toList();
    }
}
