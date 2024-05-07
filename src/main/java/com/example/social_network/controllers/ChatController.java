package com.example.social_network.controllers;

import com.example.social_network.dto.CreateChatDto;
import com.example.social_network.dto.CreateMessageDto;
import com.example.social_network.entity.Chat;
import com.example.social_network.entity.Message;
import com.example.social_network.entity.Profile;
import com.example.social_network.entity.User;
import com.example.social_network.repositories.ChatRepository;
import com.example.social_network.repositories.UserRepository;
import com.example.social_network.services.ChatService;
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
@CrossOrigin(origins = "http://localhost:3000")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRepository chatRepository;

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
    public ResponseEntity<List<Message>> getMessages(@PathVariable Long chatId) {
        List<Message> messages = chatService.getMessagesForChat(chatId);
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
}