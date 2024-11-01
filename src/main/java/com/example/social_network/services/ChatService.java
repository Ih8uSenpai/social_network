package com.example.social_network.services;

import com.example.social_network.dto.CreateChatDto;
import com.example.social_network.dto.CreateMessageDto;
import com.example.social_network.dto.MessageDto;
import com.example.social_network.entity.*;
import com.example.social_network.repositories.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.mapper.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;


    private final ChatUserRepository chatUserRepository;
    private final ProfileRepository profileRepository;

    private final MessageRepository messageRepository;
    private final ViewedMessageRepository viewedMessageRepository;
    private final ModelMapper mapper = new ModelMapper();

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


        for (Long el : createChatDto.getUserIds()) {
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
        for (Chat el : chats) {
            CreateChatDto chatDto = new CreateChatDto();
            chatDto.setChatType(el.getChatType());
            chatDto.setName(el.getName());
            List<ChatUser> chatUserList = chatUserRepository.findByChatId(el.getId());
            List<Long> chatUserIds = new ArrayList<>();
            for (ChatUser el1 : chatUserList) {
                chatUserIds.add(el1.getUserId());
            }
            chatDto.setUserIds(chatUserIds);
            chatDto.setProfileData(getChatPartnerByChatId(el.getId(), userId));
            chatDto.setId(el.getId());

            messageRepository.findFirstByChatIdOrderBySentAtDesc(el.getId()).ifPresent(value -> {
                chatDto.setLastMessage(value.getContent());
                chatDto.setLastMessageSenderIconUrl(profileRepository.findByUser_UserId(value.getSender().getUserId()).get().getProfilePictureUrl());
            });

            chatDto.setUnviewedMessages(messageRepository.findUnviewedMessagesByChatIdAndUserId(el.getId(), userId).size());

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
        for (ChatUser el : chatUsers)
            if (!Objects.equals(el.getUserId(), currentUserId))
                chatPartnerId = el.getUserId();
        // Возвращаем список чатов, отфильтрованный по идентификаторам
        Optional<Profile> profile = profileRepository.findByUser_UserId(chatPartnerId);
        return profile.orElse(null);
    }

    public List<MessageDto> getMessagesForChat(Long chatId) {
        List<Message> messages = messageRepository.findByChatId(chatId);
        List<MessageDto> messageDtos = new ArrayList<>();
        messages.forEach(message ->
        {
            MessageDto messageDto = mapper.map(message, MessageDto.class);
            messageDto.setViewed(viewedMessageRepository.existsByMessageId(message.getMessageId()));
            messageDtos.add(messageDto);
        });

        List<MessageDto> messageDtosSorted;
        messageDtosSorted = messageDtos.stream()
                .sorted(Comparator.comparing(MessageDto::getSentAt))
                .collect(Collectors.toList());

        for (int i = 1; i < messageDtosSorted.size(); i++) {
            MessageDto currentMessage = messageDtosSorted.get(i);
            MessageDto previousMessage = messageDtosSorted.get(i - 1);

            if (currentMessage.getSender().getUserId().equals(previousMessage.getSender().getUserId())) {
                currentMessage.setSingle(false);
            }
        }

        return messageDtosSorted;
    }

    public void deleteMessages(List<Long> messageIds){
        for (Long el : messageIds){
            messageRepository.deleteById(el);
        }
    }

    public void editMessage(Message new_message){
        messageRepository.save(new_message);
    }

    public List<CreateChatDto> getChatsForUserByMessageContent(Long userId, String searchString) {
        // Находим чаты, содержащие сообщения с поисковой строкой
        List<Chat> chatsWithMessages = messageRepository.findChatsByMessageContentContaining(searchString);

        // Фильтруем только те чаты, в которых состоит пользователь
        List<ChatUser> chatUsers = chatUserRepository.findByUserId(userId);
        List<Long> userChatIds = chatUsers.stream()
                .map(ChatUser::getChatId)
                .collect(Collectors.toList());

        // Оставляем только те чаты, которые принадлежат пользователю и содержат сообщения с поисковой строкой
        List<Chat> filteredChats = chatsWithMessages.stream()
                .filter(chat -> userChatIds.contains(chat.getId()))
                .collect(Collectors.toList());

        List<CreateChatDto> dtoList = new ArrayList<>();
        for (Chat el : filteredChats) {
            CreateChatDto chatDto = new CreateChatDto();
            chatDto.setChatType(el.getChatType());
            chatDto.setName(el.getName());

            // Получаем список участников чата
            List<ChatUser> chatUserList = chatUserRepository.findByChatId(el.getId());
            List<Long> chatUserIds = chatUserList.stream()
                    .map(ChatUser::getUserId)
                    .collect(Collectors.toList());
            chatDto.setUserIds(chatUserIds);

            // Данные о профиле собеседника
            chatDto.setProfileData(getChatPartnerByChatId(el.getId(), userId));
            chatDto.setId(el.getId());

            // Получаем последнее сообщение
            messageRepository.findFirstByChatIdOrderBySentAtDesc(el.getId()).ifPresent(value -> {
                chatDto.setLastMessage(value.getContent());
                chatDto.setLastMessageSenderIconUrl(
                        profileRepository.findByUser_UserId(value.getSender().getUserId())
                                .get()
                                .getProfilePictureUrl()
                );
            });

            // Подсчитываем количество непросмотренных сообщений
            chatDto.setUnviewedMessages(messageRepository.findUnviewedMessagesByChatIdAndUserId(el.getId(), userId).size());

            dtoList.add(chatDto);
        }
        return dtoList;
    }

}