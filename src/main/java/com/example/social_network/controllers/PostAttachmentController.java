package com.example.social_network.controllers;

import com.example.social_network.entity.PostAttachment;
import com.example.social_network.repositories.PostRepository;
import com.example.social_network.services.PostAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/attachments")
public class PostAttachmentController {

    private final PostAttachmentService attachmentService;
    private final PostRepository postRepository;

    @Autowired
    public PostAttachmentController(PostAttachmentService attachmentService, PostRepository postRepository) {
        this.attachmentService = attachmentService;
        this.postRepository = postRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<PostAttachment> uploadAttachment(@RequestPart("file") MultipartFile file,
                                                           @RequestPart("postId") Long postId) {

        PostAttachment attachment = new PostAttachment();

        attachment.setPost(postRepository.findById(postId).orElse(null));
        PostAttachment savedAttachment = attachment.getPost() != null ? attachmentService.saveAttachment(attachment, file): null;
        return new ResponseEntity<>(savedAttachment, HttpStatus.CREATED);
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<PostAttachment>> getAttachmentsByPostId(@PathVariable Long postId) {
        List<PostAttachment> attachments = attachmentService.getAttachmentsByPostId(postId);
        return new ResponseEntity<>(attachments, HttpStatus.OK);
    }
}
