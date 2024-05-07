package com.example.social_network.services;

import com.example.social_network.dto.CreateChatDto;
import com.example.social_network.dto.CreateMessageDto;
import com.example.social_network.entity.*;
import com.example.social_network.repositories.ChatRepository;
import com.example.social_network.repositories.ChatUserRepository;
import com.example.social_network.repositories.MessageRepository;
import com.example.social_network.repositories.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private ChatUserRepository chatUserRepository;
    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private MessageRepository messageRepository;

    public Message createMessage(CreateMessageDto createMessageDto, Long senderId, Long chatId) {
        Message message = new Message();

        message.setSender(new User(senderId));
        message.setContent(createMessageDto.getContent());
        Optional<Chat> currentChat = chatRepository.findById(chatId);
        message.setChat(currentChat.get());
        message.setSentAt(LocalDateTime.now());


        return messageRepository.save(message);
    }

    public Chat createChat(CreateChatDto createChatDto) {
        Chat chat = new Chat();

        chat.setName(createChatDto.getName());
        chat.setChatType(createChatDto.getChatType());
        chatRepository.save(chat);


        for (Long el: createChatDto.getUserIds()){
            ChatUser newChatUser = new ChatUser();
            newChatUser.setChatId(chat.getId());

            System.out.println("element = " + el);
            newChatUser.setUserId(el);
            chatUserRepository.save(newChatUser);
        }


        return chat;
    }

    public List<CreateChatDto> getChatsForUser(Long userId) {
        // Получаем список ChatUser, где userID равен заданному userId
        List<ChatUser> chatUsers = chatUserRepository.findByUserId(userId);

        // Получаем список идентификаторов чатов
        List<Long> chatIds = chatUsers.stream()
                .map(ChatUser::getChatId)
                .collect(Collectors.toList());

        // Возвращаем список чатов, отфильтрованный по идентификаторам
        List<Chat> chats = chatRepository.findAllById(chatIds);
        List<CreateChatDto> dtoList = new ArrayList<>();
        for (Chat el : chats){
            CreateChatDto chatDto = new CreateChatDto();
            chatDto.setChatType(el.getChatType());
            chatDto.setName(el.getName());
            List<ChatUser> chatUserList = chatUserRepository.findByChatId(el.getId());
            List<Long> chatUserIds = new ArrayList<>();
            for (ChatUser el1: chatUserList){
                chatUserIds.add(el1.getUserId());
            }
            chatDto.setUserIds(chatUserIds);
            chatDto.setProfileData(getChatPartnerByChatId(el.getId(), userId));
            chatDto.setId(el.getId());
            dtoList.add(chatDto);
        }
        return dtoList;
    }

    public Profile getChatPartnerByChatId(Long chatId, Long currentUserId) {
        // Получаем список ChatUser, где userID равен заданному userId
        List<ChatUser> chatUsers = chatUserRepository.findByChatId(chatId);
        if (chatUsers.size() != 2)
            return null;
        Long chatPartnerId = 0L;
        for (ChatUser el: chatUsers)
            if (!Objects.equals(el.getUserId(), currentUserId))
                chatPartnerId = el.getUserId();
        // Возвращаем список чатов, отфильтрованный по идентификаторам
        Optional<Profile> profile = profileRepository.findByUser_UserId(chatPartnerId);
        return profile.orElse(null);
    }

    public List<Message> getMessagesForChat(Long chatId) {
        return messageRepository.findByChatId(chatId);
    }
}