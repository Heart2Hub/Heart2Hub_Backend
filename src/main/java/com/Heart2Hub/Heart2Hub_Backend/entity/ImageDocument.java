package com.Heart2Hub.Heart2Hub_Backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "imageDocument")
public class ImageDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageDocumentId;

    @NotNull
    @Column(unique = true)
    private UUID imageDocumentNehrId = UUID.randomUUID();

    @NotNull
    private String imageLink;

    @NotNull
    private LocalDateTime createdDate;

    public ImageDocument() {
    }

    public ImageDocument(String imageLink, LocalDateTime createdDate) {
        this.imageLink = imageLink;
        this.createdDate = createdDate;
    }
}
