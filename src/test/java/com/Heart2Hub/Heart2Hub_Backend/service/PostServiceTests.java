package com.Heart2Hub.Heart2Hub_Backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.Heart2Hub.Heart2Hub_Backend.entity.ImageDocument;
import com.Heart2Hub.Heart2Hub_Backend.entity.Post;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToUpdateTreatmentPlanRecordException;
import com.Heart2Hub.Heart2Hub_Backend.repository.PostRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.StaffRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PostServiceTests {

    @Mock
    private PostRepository postRepository;

    @Mock
    private ImageDocumentService imageDocumentService;

    @Mock
    private StaffRepository staffRepository;

    @InjectMocks
    private PostService postService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindPostAuthor() {
        Long postId = 1L;
        Staff author = new Staff();
        Post post = new Post();
        post.setStaff(author);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(staffRepository.findById(author.getStaffId())).thenReturn(Optional.of(author));

        Staff result = postService.findPostAuthor(postId);

        assertEquals(author, result);
    }

    @Test
    void testGetAllPosts() {
        List<Post> posts = new ArrayList<>();
        when(postRepository.findAll()).thenReturn(posts);

        List<Post> result = postService.getAllPosts();

        assertEquals(posts, result);
    }

    @Test
    void testGetPostById() {
        Long postId = 1L;
        Post post = new Post();
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        Post result = postService.getPostById(postId);

        assertEquals(post, result);
    }

    @Test
    void testCreatePost() {
        Long staffId = 1L;
        Long postId = 1L;
        Staff author = new Staff();
        ImageDocument imageDocument = new ImageDocument();
        Post post = new Post();
        when(staffRepository.findById(staffId)).thenReturn(Optional.of(author));
        when(imageDocumentService.createImageDocument(imageDocument)).thenReturn(imageDocument);
        when(postRepository.save(post)).thenReturn(new Post());

        Post result = postService.createPost(post, staffId, imageDocument);

        assertEquals(postId, result.getPostId());
        assertEquals(author, result.getStaff());
        assertEquals(imageDocument, result.getListOfImageDocuments().get(0));
    }

    @Test
    void testAddImageToPost() {
        Long postId = 1L;
        ImageDocument imageDocument = new ImageDocument();
        Post post = new Post();
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(imageDocumentService.createImageDocument(imageDocument)).thenReturn(imageDocument);
        when(postRepository.save(post)).thenReturn(new Post());

        Post result = postService.addImageToPost(imageDocument, postId);

        assertEquals(post, result);
        assertEquals(imageDocument, result.getListOfImageDocuments().get(0));
    }

    @Test
    void testRemoveImageFromPost() {
        Long postId = 1L;
        String imageLink = "link";
        ImageDocument imageDocument = new ImageDocument();
        Post post = new Post();
        post.getListOfImageDocuments().add(imageDocument);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        Post result = postService.removeImageFromPost(imageLink, postId);

        assertTrue(result.getListOfImageDocuments().isEmpty());
    }

    @Test
    void testRemoveImageFromPostImageNotFound() {
        Long postId = 1L;
        String imageLink = "link";
        Post post = new Post();
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        assertThrows(UnableToUpdateTreatmentPlanRecordException.class, () -> postService.removeImageFromPost(imageLink, postId));
    }

    @Test
    void testUpdatePost() {
        Long postId = 1L;
        Post post = new Post();
        Post updatedPost = new Post();
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(updatedPost);

        Post result = postService.updatePost(postId, post);

        assertEquals(updatedPost, result);
    }

    @Test
    void testDeletePost() {
        Long postId = 1L;
        Post post = new Post();
        Staff author = new Staff();
        author.getListOfPosts().add(post);
        post.setStaff(author);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(staffRepository.findById(author.getStaffId())).thenReturn(Optional.of(author));

        postService.deletePost(postId);

        assertTrue(author.getListOfPosts().isEmpty());
        verify(postRepository, times(1)).delete(post);
    }
}
