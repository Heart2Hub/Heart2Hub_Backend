package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.ImageDocument;
import com.Heart2Hub.Heart2Hub_Backend.entity.Post;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateImageDocumentException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToUpdateTreatmentPlanRecordException;
import com.Heart2Hub.Heart2Hub_Backend.repository.PostRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.StaffRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        Staff staff = postRepository.findById(postId).get().getStaff();
        return staffRepository.findById(staff.getStaffId()).get();
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id).get();
    }

    public Post createPost(Post post, Long staffId, ImageDocument imageDocument) {
        Staff staff = staffRepository.findById(staffId).get();
        post.setStaff(staff);
        if (imageDocument != null) {
            ImageDocument createdImageDocument = imageDocumentService.createImageDocument(
                    imageDocument);
            post.getListOfImageDocuments().add(createdImageDocument);
        }

        post = postRepository.save(post);
        staff.getListOfPosts().add(post);

        staffRepository.save(staff);

        return post;
    }

    public Post addImageToPost(ImageDocument imageDocument, Long postId) {
        Post post = postRepository.findById(postId).get();
        if (imageDocument != null) {
            ImageDocument createdImageDocument = imageDocumentService.createImageDocument(
                    imageDocument);
            post.getListOfImageDocuments().add(createdImageDocument);
        }
        post = postRepository.save(post);
        return post;
    }

    public Post removeImageFromPost(String imageLink, Long postId) {
        Post post = postRepository.findById(postId).get();
        List<ImageDocument> imageDocumentToRemove = post.getListOfImageDocuments()
                .stream().filter(imageDocument -> imageDocument.getImageLink().equals(imageLink)).toList();
        if (imageDocumentToRemove.isEmpty()) {
            throw new UnableToUpdateTreatmentPlanRecordException("No image exist");
        }
        ImageDocument imageDocument = imageDocumentToRemove.get(0);
        post.getListOfImageDocuments().remove(imageDocument);
        post = postRepository.save(post);
        return post;
    }

    public Post updatePost(Long postId, Post post) {
            Post oldPost = postRepository.findById(postId).get();
            oldPost.setPostTypeEnum(post.getPostTypeEnum());
            oldPost.setTitle(post.getTitle());
            oldPost.setBody(post.getBody());

            postRepository.save(oldPost);

            return oldPost;
    }

    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId).get();
        Staff staff = staffRepository.findById(post.getStaff().getStaffId()).get();
        staff.getListOfPosts().remove(post);

        postRepository.delete(post);

    }
}
