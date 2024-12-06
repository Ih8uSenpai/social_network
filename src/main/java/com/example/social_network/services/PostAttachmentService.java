package com.example.social_network.services;

import com.example.social_network.entity.PostAttachment;
import com.example.social_network.repositories.PostAttachmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class PostAttachmentService {

    private final PostAttachmentRepository postAttachmentRepository;
    private final StaticFileService staticFileService;

    @Autowired
    public PostAttachmentService(PostAttachmentRepository postAttachmentRepository, StaticFileService staticFileService) {
        this.postAttachmentRepository = postAttachmentRepository;
        this.staticFileService = staticFileService;
    }


    public PostAttachment saveAttachment(PostAttachment postAttachment, MultipartFile file) {
        if (!file.isEmpty()) {
            staticFileService.uploadFile(file);
            String correctPath = "uploads/" + file.getOriginalFilename();
            postAttachment.setUrl(correctPath);
        }
        return postAttachmentRepository.save(postAttachment);

    }

    public List<PostAttachment> getAttachmentsByPostId(Long postId) {
        return postAttachmentRepository.findByPostId(postId);
    }


}
