package com.Heart2Hub.Heart2Hub_Backend.entity;

import com.Heart2Hub.Heart2Hub_Backend.enumeration.PostTypeEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @NotNull
    @Column(unique = true)
    private UUID postNehrId = UUID.randomUUID();

    @NotNull
    private String title;

    @NotNull
    private String body;

    @NotNull
    private LocalDateTime createdDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PostTypeEnum postTypeEnum;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ImageDocument> listOfImageDocuments;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "staff_id",nullable = false)
    private Staff staff;

    public Post() {
        this.listOfImageDocuments = new ArrayList<>();
    }

    public Post(String title, String body, PostTypeEnum postTypeEnum) {
        this.listOfImageDocuments = new ArrayList<>();
        this.title = title;
        this.body = body;
        this.createdDate = LocalDateTime.now();
        this.postTypeEnum = postTypeEnum;
    }
}
