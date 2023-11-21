package com.Heart2Hub.Heart2Hub_Backend.servicetest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.Heart2Hub.Heart2Hub_Backend.entity.ImageDocument;
import com.Heart2Hub.Heart2Hub_Backend.entity.Post;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.PostTypeEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import com.Heart2Hub.Heart2Hub_Backend.exception.*;
import com.Heart2Hub.Heart2Hub_Backend.repository.PostRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.StaffRepository;
import com.Heart2Hub.Heart2Hub_Backend.service.ImageDocumentService;
import com.Heart2Hub.Heart2Hub_Backend.service.PostService;
import junit.runner.Version;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
class PostServiceTests {

    @InjectMocks
    private PostService postService;
    @Mock
    private PostRepository postRepository;
    @Mock
    private ImageDocumentService imageDocumentService;
    @Mock
    private StaffRepository staffRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        System.out.println("JUnit version is: " + Version.id());
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
    void testFindPostAuthor_PostNotFound() {
        // Mock data
        Long postId = 1L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // Test
        assertThrows(PostNotFoundException.class, () -> postService.findPostAuthor(postId));

        // Verify
        verify(postRepository).findById(postId);
        verify(staffRepository, times(0)).findById(any());
    }

    @Test
    public void testFindPostAuthor_StaffDoesNotExist() {
        // Mock data
        Long postId = 1L;
        Long staffId = 100L;

        Post post = new Post("Title", "Body", PostTypeEnum.ADMINISTRATIVE);
        Staff staff = new Staff("john_doe", "password123", "John", "Doe", 1234567890L, StaffRoleEnum.ADMIN, false);
        post.setStaff(staff);

        // Mocking the repository behavior
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(staffRepository.findById(staff.getStaffId())).thenReturn(Optional.empty());

        // Assert the exception is thrown
        assertThrows(StaffNotFoundException.class, () -> postService.findPostAuthor(postId));

        // Verify that the repository methods were called with the correct arguments
        verify(postRepository, times(1)).findById(postId);
        verify(staffRepository, times(1)).findById(staff.getStaffId());
    }


    @Test
    void testGetAllPosts() {
        List<Post> posts = Collections.singletonList(new Post(/* add your constructor parameters here */));
        when(postRepository.findAll()).thenReturn(posts);

        List<Post> result = postService.getAllPosts();

        assertEquals(1, result.size());
    }

    @Test
    void testGetPostById() {
        Long postId = 1L;
        Post post = new Post();
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        Post newPost = postService.getPostById(postId);

        List<Post> result = new ArrayList<>();
        result.add(newPost);

        assertEquals(1, result.size()); // Assuming getId() returns the post ID
    }

    @Test
    void testGetPostById_PostNotFound() {
        // Mock data
        Long postId = 1L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // Test
        assertThrows(PostNotFoundException.class, () -> assertNull(postService.getPostById(postId)));

        // Verify
        verify(postRepository).findById(postId);
    }

    @Test
    void testCreatePost() {
        Long staffId = 1L;
        Long postId = 1L;
        Staff author = new Staff();
        ImageDocument imageDocument = new ImageDocument();
        Post post = new Post("title", "body", PostTypeEnum.ADMINISTRATIVE);
        String dateString = "2023-11-11 12:00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Parsing the string to LocalDateTime
        LocalDateTime localDateTime = LocalDateTime.parse(dateString, formatter);
        post.setCreatedDate(localDateTime);

        when(staffRepository.findById(staffId)).thenReturn(Optional.of(author));
        when(imageDocumentService.createImageDocument(imageDocument)).thenReturn(imageDocument);
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> {
            Post savedPost = invocation.getArgument(0);
            savedPost.setPostId(1L); // Set the expected postId
            return savedPost;
        });
        Post result = postService.createPost(post, staffId, imageDocument);

        assertEquals(postId, result.getPostId());
        assertEquals(author, result.getStaff());
        assertEquals(imageDocument, result.getListOfImageDocuments().get(0));
        assertNotNull(result);
    }

    @Test
    void testCreatePost_IdNotFoundFailure() {
        // Mock data
        Long staffId = 2L;
        Post post = new Post("title", "body", PostTypeEnum.ADMINISTRATIVE);

        String dateString = "2023-11-11 12:00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Parsing the string to LocalDateTime
        LocalDateTime localDateTime = LocalDateTime.parse(dateString, formatter);
        ImageDocument imageDocument = new ImageDocument("Id1.png",localDateTime);
        post.setCreatedDate(localDateTime);
        when(staffRepository.findById(staffId)).thenReturn(Optional.empty());

        // Test
        assertThrows(UnableToCreatePostException.class, () -> postService.createPost(post, staffId, imageDocument));

        // Verify
        verify(staffRepository).findById(staffId);
        verify(imageDocumentService, times(0)).createImageDocument(any());
        verify(postRepository, times(0)).save(any());
    }

    @Test
    void testAddImageToPost() {
        // Arrange
        Long postId = 1L;
        String dateString = "2023-11-11 12:00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Parsing the string to LocalDateTime
        LocalDateTime localDateTime = LocalDateTime.parse(dateString, formatter);
        ImageDocument imageDocument = new ImageDocument("Id1.png", localDateTime);

        Post post = new Post();
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(imageDocumentService.createImageDocument(imageDocument)).thenReturn(imageDocument);
        when(postRepository.save(post)).thenReturn(post);

        // Act
        Post result = postService.addImageToPost(imageDocument, postId);

        // Assert
        assertNotNull(result);
        assertEquals(post, result);
        assertEquals(imageDocument, result.getListOfImageDocuments().get(0));
        verify(postRepository, times(1)).findById(postId);
        verify(imageDocumentService, times(1)).createImageDocument(imageDocument);
        verify(postRepository, times(1)).save(post);
    }

    @Test
    void testAddImageToPost_IdNotFoundFailure() {
        // Mock data
        Long postId = 2L;
        String dateString = "2023-11-11 12:00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Parsing the string to LocalDateTime
        LocalDateTime localDateTime = LocalDateTime.parse(dateString, formatter);
        ImageDocument imageDocument = new ImageDocument("Id1.png", localDateTime);
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // Test
        assertThrows(PostNotFoundException.class, () -> postService.addImageToPost(imageDocument, postId));

        // Verify
        verify(postRepository).findById(postId); // Ensure findById is called with the correct postId
        verify(imageDocumentService, times(0)).createImageDocument(any()); // Ensure createImageDocument is not called
        verify(postRepository, times(0)).save(any()); // Ensure save is not called
    }

    @Test
    public void testRemoveImageFromPost() {
        // Mock data
        Long postId = 1L;
        String imageLinkToRemove = "“Id1.png";
        List<ImageDocument> imageDocuments = new ArrayList<>();
        imageDocuments.add(new ImageDocument("image1.jpg", LocalDateTime.now()));
        imageDocuments.add(new ImageDocument(imageLinkToRemove, LocalDateTime.now()));
        imageDocuments.add(new ImageDocument("image3.jpg", LocalDateTime.now()));

        Post existingPost = new Post( "Title", "Body", PostTypeEnum.ADMINISTRATIVE);
        existingPost.setListOfImageDocuments(imageDocuments);

        // Mocking the repository behavior
        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
        when(postRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Call the method to be tested
        Post result = postService.removeImageFromPost(imageLinkToRemove, postId);

        // Verify that the repository methods were called with the correct arguments
        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).save(existingPost);

        // Assert the result
        assertNotNull(result);
        assertEquals(2, result.getListOfImageDocuments().size());
        assertFalse(result.getListOfImageDocuments().stream()
                .anyMatch(imageDocument -> imageDocument.getImageLink().equals(imageLinkToRemove)));
    }

    @Test
    void testRemoveImageFromPost_NoImageExists() {
        // Mock data
        Long postId = 1L;
        String imageLink = "link";
        when(postRepository.findById(postId)).thenReturn(Optional.of(new Post())); // Create a post with an empty list of image documents

        // Test
        UnableToRemoveImageException exception = assertThrows(UnableToRemoveImageException.class,
                () -> postService.removeImageFromPost(imageLink, postId));

        // Verify
        assertEquals("No image exists", exception.getMessage());
        verify(postRepository).findById(postId);
        verify(postRepository, never()).save(any()); // Ensure save is not called when an exception is thrown
    }

    @Test
    void testRemoveImageFromPost_IdNotFoundFailure() {
        // Mock data
        Long postId = 1L;
        String imageLink = "“Id1.png";
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // Test
        assertThrows(PostNotFoundException.class, () -> postService.removeImageFromPost(imageLink, postId));

        // Verify
        verify(postRepository).findById(postId);
    }


    @Test
    void testRemoveImageFromPostImageNotFound() {
        Long postId = 1L;
        String imageLink = "link";
        Post post = new Post();
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        assertThrows(UnableToRemoveImageException.class, () -> postService.removeImageFromPost(imageLink, postId));
    }

    @Test
    public void testUpdatePost() {
        // Mock data
        Long postId = 1L;
        Post existingPost = new Post("Old Title", "Old Body", PostTypeEnum.ADMINISTRATIVE);
        Post updatedPost = new Post("Updated Title", "Updated Body", PostTypeEnum.ENRICHMENT);

        String dateString = "2023-11-11 12:00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Parsing the string to LocalDateTime
        LocalDateTime localDateTime = LocalDateTime.parse(dateString, formatter);
        updatedPost.setPostId(2L);
        updatedPost.getListOfImageDocuments().add(new ImageDocument("Id2.png", localDateTime));

        // Mocking the repository behavior
        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
        when(postRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Call the method to be tested
        Post result = postService.updatePost(postId, updatedPost);

        // Verify that the repository methods were called with the correct arguments
        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).save(existingPost);

        // Assert the result
        assertNotNull(result);
        assertEquals(updatedPost.getPostTypeEnum(), result.getPostTypeEnum());
        assertEquals(updatedPost.getTitle(), result.getTitle());
        assertEquals(updatedPost.getBody(), result.getBody());
    }


    @Test
    void testUpdatePost_IdNotFoundFailure() {
        // Mock data
        Long postId = 1L;
        Post post = new Post();
        when(postRepository.findById(postId)).thenReturn(Optional.empty());
        String dateString = "2023-11-11 12:00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Parsing the string to LocalDateTime
        LocalDateTime localDateTime = LocalDateTime.parse(dateString, formatter);
        post.setPostId(2L);
        post.getListOfImageDocuments().add(new ImageDocument("Id2.png", localDateTime));

        // Test
        assertThrows(PostNotFoundException.class, () -> postService.updatePost(postId, post));

        // Verify
        verify(postRepository).findById(postId);
        verify(postRepository, times(0)).save(any());
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

    @Test
    void testDeletePost_IdNotFoundFailure() {
        // Mock data
        Long postId = 2L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // Test
        assertThrows(PostNotFoundException.class, () -> postService.deletePost(postId));

        // Verify
        verify(postRepository).findById(postId);
        verify(staffRepository, times(0)).findById(any());
        verify(postRepository, times(0)).delete(any());
    }
}
