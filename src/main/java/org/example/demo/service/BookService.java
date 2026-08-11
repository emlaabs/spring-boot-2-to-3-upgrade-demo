package org.example.demo.service;

import org.example.demo.dto.BookResponse;
import org.example.demo.dto.CreateBookRequest;

import java.util.List;

public interface BookService {
    BookResponse createBook(CreateBookRequest request);
    List<BookResponse> getAllBooks();
}
