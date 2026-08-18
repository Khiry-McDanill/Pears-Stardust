package com.zipcode.stardust.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.zipcode.stardust.model.DirectMessage;
import com.zipcode.stardust.model.User;
import com.zipcode.stardust.repository.DirectMessageRepository;

@Service
public class DirectMessageService {

    private final DirectMessageRepository directMessageRepository;

    public DirectMessageService(DirectMessageRepository directMessageRepository) {
        this.directMessageRepository = directMessageRepository;
    }

    public DirectMessage sendMessage(User sender, User recipient, String content) {
        DirectMessage message = new DirectMessage(sender, recipient, content);
        return directMessageRepository.save(message);
    }

    public List<DirectMessage> getConversation(User userOne, User userTwo) {
        return directMessageRepository
                .findBySenderAndRecipientOrSenderAndRecipientOrderBySentAtAsc(
                        userOne,
                        userTwo,
                        userTwo,
                        userOne
                );
    }
}