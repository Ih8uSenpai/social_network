package com.example.social_network.services;

import com.example.social_network.entity.PostAttachment;
import com.example.social_network.repositories.PostAttachmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static com.example.social_network.utils.Constants.uploadPath;

@Service
public class PostAttachmentService {

    private final PostAttachmentRepository postAttachmentRepository;

    @Autowired
    public PostAttachmentService(PostAttachmentRepository postAttachmentRepository) {
        this.postAttachmentRepository = postAttachmentRepository;
    }


    public PostAttachment saveAttachment(PostAttachment postAttachment, MultipartFile file) {
        try {
            if (!file.isEmpty()) {
                byte[] bytes = file.getBytes();
                Path path = Paths.get(uploadPath + file.getOriginalFilename());
                Files.write(path, bytes);

                // Предполагается, что path.toString() дает нам путь к файлу
                String correctPath = "uploads/" + file.getOriginalFilename();
                postAttachment.setUrl(correctPath);
            }
            return postAttachmentRepository.save(postAttachment);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public List<PostAttachment> getAttachmentsByPostId(Long postId) {
        return postAttachmentRepository.findByPostId(postId);
    }


}
