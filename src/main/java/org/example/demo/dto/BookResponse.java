package org.example.demo.dto;

public class BookResponse {

    private Long id;
    private String title;
    private String authorName;

    public BookResponse(Long id, String title, String authorName) {
        this.id = id;
        this.title = title;
        this.authorName = authorName;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthorName() {
        return authorName;
    }
}
