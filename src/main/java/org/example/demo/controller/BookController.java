package org.example.demo.controller;

import org.example.demo.dto.BookResponse;
import org.example.demo.dto.CreateBookRequest;
import org.example.demo.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    @PostMapping
    public BookResponse createBook(@Validated @RequestBody CreateBookRequest request) {
        return service.createBook(request);
    }

    @GetMapping
    public List<BookResponse> getBooks() {
        return service.getAllBooks();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void>  deleteBook(@PathVariable Long id) {
        service.deleteBook(id);
        return ResponseEntity.ok().build();
    }

}
