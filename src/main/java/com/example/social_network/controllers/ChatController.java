package com.example.social_network.controllers;

import com.example.social_network.dto.CreateChatDto;
import com.example.social_network.dto.CreateMessageDto;
import com.example.social_network.dto.MessageDto;
import com.example.social_network.entity.*;
import com.example.social_network.repositories.ChatRepository;
import com.example.social_network.repositories.MessageRepository;
import com.example.social_network.repositories.UserRepository;
import com.example.social_network.repositories.ViewedMessageRepository;
import com.example.social_network.services.ChatService;
import com.example.social_network.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class ChatController {

    private final ChatService chatService;

    private final UserRepository userRepository;

    private final ChatRepository chatRepository;

    private final MessageRepository messageRepository;
    private final ViewedMessageRepository viewedMessageRepository;

    @PostMapping("/create")
    public ResponseEntity<Chat> createChat(@RequestBody CreateChatDto createChatDto, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Chat newChat = chatService.createChat(createChatDto);
        return new ResponseEntity<>(newChat, HttpStatus.CREATED);
    }
    @GetMapping
    public ResponseEntity<List<CreateChatDto>> getChats(Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<CreateChatDto> chats = chatService.getChatsForUser(user.getUserId());
        return ResponseEntity.ok(chats);
    }

    @GetMapping("/{chatId}")
    public ResponseEntity<Profile> getChatUserInfo(@PathVariable Long chatId, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Profile profile = chatService.getChatPartnerByChatId(chatId, user.getUserId());
        return ResponseEntity.ok(profile);
    }

    @PostMapping("/{chatId}/messages")
    public ResponseEntity<Message> createMessage(@PathVariable Long chatId,
                                                 @RequestBody CreateMessageDto createMessageDto,
                                                 Principal principal) {
        System.out.println(chatId);
        String currentUsername = principal.getName();
        Optional<User> sender = userRepository.findByUsername(currentUsername);
        Long senderId = sender.get().getUserId();

        Message message = chatService.createMessage(createMessageDto, senderId, chatId);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @GetMapping("/{chatId}/messages")
    public ResponseEntity<List<MessageDto>> getMessages(@PathVariable Long chatId) {
        List<MessageDto> messages = chatService.getMessagesForChat(chatId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/isExisting/{userId}")
    public ResponseEntity<?> isFollowing(@PathVariable Long userId, Principal principal) {
        String currentUsername = principal.getName();
        Optional<User> currentUser = userRepository.findByUsername(currentUsername);

        Optional<Chat> chat = chatRepository.findPrivateChatBetweenUsers(currentUser.get().getUserId(), userId);

        boolean isExisting = chat.isPresent();
        Map<Object, Object> map = new HashMap<>();
        map.put("isExisting", isExisting);
        if (isExisting)
            map.put("chatId", chat.get().getId());
        return ResponseEntity.ok(map);
    }

    @GetMapping("/unviewed/{userId}/{chatId}")
    public ResponseEntity<?> getUnviewedMessages(@PathVariable Long userId, @PathVariable Long chatId) {
        List<Message> messages = messageRepository.findUnviewedMessagesByChatIdAndUserId(chatId, userId);
        return ResponseEntity.ok(messages.size());
    }


    @PostMapping("/mark-viewed")
    public ResponseEntity<?> markPostAsViewed(@RequestBody Long messageId) {
        User user = userRepository.findByUsername(SecurityUtils.getCurrentUsername()).get();
        if (!viewedMessageRepository.existsByUserIdAndMessageId(user.getUserId(), messageId)) {
            ViewedMessage viewedMessage = new ViewedMessage(user.getUserId(), messageId);
            viewedMessageRepository.save(viewedMessage);
        }
        return new ResponseEntity<>("Message with id = " + messageId + " viewed", HttpStatus.OK);
    }
}