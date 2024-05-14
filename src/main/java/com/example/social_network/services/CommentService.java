package com.example.social_network.services;

import com.example.social_network.dto.CommentDto;
import com.example.social_network.entity.Comment;
import com.example.social_network.repositories.CommentRepository;
import com.example.social_network.repositories.PostRepository;
import com.example.social_network.repositories.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final ModelMapper mapper = new ModelMapper();
    private final ProfileRepository profileRepository;


    public int getCommentsByPostAndDateRange(Long postId, String fromDate, String toDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDateTime = LocalDateTime.parse(fromDate, formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(toDate, formatter);

        return commentRepository.findCommentByProfileIdAndDateRange(postId, startDateTime, endDateTime).size();
    }

    public CommentDto convertCommentToDto(Comment comment){
        CommentDto commentDto = mapper.map(comment, CommentDto.class);
        List<CommentDto> commentDtoReplies = new ArrayList<>();
        comment.getReplies().forEach((reply) ->
        {
            CommentDto commentDto1 = convertCommentToDto(reply);
            commentDto1.setParentTag(profileRepository.findByUser_UserId(commentDto.getUserId()).get().getTag());
            commentDtoReplies.add(commentDto1);
        });
        commentDto.setReplies(commentDtoReplies);

        return commentDto;
    }
}
