package com.Heart2Hub.Heart2Hub_Backend.controller;

import com.Heart2Hub.Heart2Hub_Backend.entity.ImageDocument;
import com.Heart2Hub.Heart2Hub_Backend.entity.Post;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.PostTypeEnum;
import com.Heart2Hub.Heart2Hub_Backend.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping("/getAllPosts")
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> posts = postService.getAllPosts();
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @GetMapping("/findPostAuthor/{id}")
    public ResponseEntity<Staff> findPostAuthor(@PathVariable Long id) {
        Staff staff = postService.findPostAuthor(id);
        return new ResponseEntity<>(staff, HttpStatus.OK);
    }

    @GetMapping("/getPostById/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        Post post = postService.getPostById(id);
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @PostMapping("/createPost/{staffId}")
    public ResponseEntity<Post> createPost(@RequestBody Map<String, Object> requestBody, @PathVariable Long staffId) {
        String title = requestBody.get("title").toString();
        String body = requestBody.get("body").toString();
        String enumType = requestBody.get("postType").toString();
        PostTypeEnum postTypeEnum = PostTypeEnum.valueOf(enumType);
        String image = requestBody.get("image").toString();

        Post p = new Post(title, body, postTypeEnum);

        LocalDateTime lt = LocalDateTime.now();

        Post createdPost = postService.createPost(p, staffId, new ImageDocument(image, lt));
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }


    @DeleteMapping("/deletePost/{id}")
    public ResponseEntity<HttpStatus> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/addImage/{postId}")
    public ResponseEntity<Post> addImageToPost(@RequestBody Map<String, Object> requestBody,
                                               @PathVariable Long postId) {
        String image = requestBody.get("image").toString();
        LocalDateTime lt = LocalDateTime.now();

        Post updatedPost = postService.addImageToPost(new ImageDocument(image, lt), postId);
        return ResponseEntity.ok(updatedPost);
    }

    @PostMapping("/removeImage/{postId}")
    public ResponseEntity<Post> removeImageFromPost(@RequestBody Map<String, Object> requestBody,
                                                    @PathVariable Long postId) {
        String imageLink = requestBody.get("imageLink").toString();
        Post updatedPost = postService.removeImageFromPost(imageLink, postId);
        return ResponseEntity.ok(updatedPost);
    }

    @PutMapping("/updatePost/{postId}")
    public ResponseEntity<Post> updatePost(@RequestBody Map<String, Object> requestBody,
                                           @PathVariable Long postId) {
        String title = requestBody.get("title").toString();
        String body = requestBody.get("body").toString();
        String enumType = requestBody.get("postType").toString();
        PostTypeEnum postTypeEnum = PostTypeEnum.valueOf(enumType);

        Post updatedPost = new Post(title, body, postTypeEnum);

        Post post = postService.updatePost(postId, updatedPost);
        return ResponseEntity.ok(post);
    }
}
