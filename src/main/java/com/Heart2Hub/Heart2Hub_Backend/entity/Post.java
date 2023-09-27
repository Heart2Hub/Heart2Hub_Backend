package com.Heart2Hub.Heart2Hub_Backend.entity;

import com.Heart2Hub.Heart2Hub_Backend.enumeration.PostTypeEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @NotNull
    private String title;

    @NotNull
    private LocalDateTime createdDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PostTypeEnum postTypeEnum;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ImageDocument> listOfImageDocuments;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "staff_id",nullable = false)
    private Staff staff;

    public Post() {
        this.listOfImageDocuments = List.of();
    }

    public Post(String title, LocalDateTime createdDate, PostTypeEnum postTypeEnum) {
        this.title = title;
        this.createdDate = createdDate;
        this.postTypeEnum = postTypeEnum;
    }
}
