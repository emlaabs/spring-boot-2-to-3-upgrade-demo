package org.example.demo.mapper;

import org.example.demo.dto.BookResponse;
import org.example.demo.model.Book;

public class BookMapper {

    public static BookResponse toResponse(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor().getName()
        );
    }
}
