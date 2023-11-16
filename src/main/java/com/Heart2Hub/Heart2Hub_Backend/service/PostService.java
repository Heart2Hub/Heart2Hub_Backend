package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.ImageDocument;
import com.Heart2Hub.Heart2Hub_Backend.entity.Post;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import com.Heart2Hub.Heart2Hub_Backend.exception.*;
import com.Heart2Hub.Heart2Hub_Backend.repository.PostRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.StaffRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final ImageDocumentService imageDocumentService;
    private final StaffRepository staffRepository;

    public PostService(PostRepository postRepository, ImageDocumentService imageDocumentService, StaffRepository staffRepository) {
        this.postRepository = postRepository;
        this.imageDocumentService = imageDocumentService;
        this.staffRepository = staffRepository;
    }

    public Staff findPostAuthor(Long postId) {
        Optional<Post> postOptional = postRepository.findById(postId);

        if (postOptional.isEmpty()) {
            throw new PostNotFoundException("Post not found with id: " + postId);
        }

        Post post = postOptional.get();
        Staff staff = post.getStaff();
        return staffRepository.findById(staff.getStaffId()).orElseThrow(() -> new StaffNotFoundException("Staff not found with id: " + staff.getStaffId()));
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Post getPostById(Long id) {

        return postRepository.findById(id).orElseThrow(() -> new PostNotFoundException("Post not found with id: " + id));

    }

    public Post createPost(Post post, Long staffId, ImageDocument imageDocument) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new UnableToCreatePostException("Staff not found with id: " + staffId));

        post.setStaff(staff);

        if (imageDocument != null) {
            ImageDocument createdImageDocument = imageDocumentService.createImageDocument(imageDocument);
            post.getListOfImageDocuments().add(createdImageDocument);
        }

        post = postRepository.save(post);
        staff.getListOfPosts().add(post);

        staffRepository.save(staff);

        return post;
    }

    public Post addImageToPost(ImageDocument imageDocument, Long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            if (imageDocument != null) {
                ImageDocument createdImageDocument = imageDocumentService.createImageDocument(imageDocument);
                post.getListOfImageDocuments().add(createdImageDocument);
            }
            post = postRepository.save(post);
            return post;
        } else {
            throw new PostNotFoundException("Post not found with id: " + postId);
        }
    }

    public Post removeImageFromPost(String imageLink, Long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();

            List<ImageDocument> imageDocumentToRemove = post.getListOfImageDocuments()
                    .stream()
                    .filter(imageDocument -> {
                        String link = imageDocument.getImageLink();
                        return link != null && link.equals(imageLink);
                    })
                    .collect(Collectors.toList());

            if (imageDocumentToRemove.isEmpty()) {
                throw new UnableToRemoveImageException("No image exists");
            }

            ImageDocument imageDocument = imageDocumentToRemove.get(0);
            post.getListOfImageDocuments().remove(imageDocument);
            post = postRepository.save(post);

            return post;
        } else {
            throw new PostNotFoundException("Post not found with id: " + postId);
        }
    }



    public Post updatePost(Long postId, Post post) {
        Post oldPost = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + postId));

        oldPost.setPostTypeEnum(post.getPostTypeEnum());
        oldPost.setTitle(post.getTitle());
        oldPost.setBody(post.getBody());

        postRepository.save(oldPost);

        return oldPost;
    }

    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not found with id: " + postId));
        Staff staff = staffRepository.findById(post.getStaff().getStaffId()).get();
        staff.getListOfPosts().remove(post);

        postRepository.delete(post);
    }

}
