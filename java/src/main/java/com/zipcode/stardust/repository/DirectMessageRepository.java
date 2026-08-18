package com.zipcode.stardust.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zipcode.stardust.model.DirectMessage;
import com.zipcode.stardust.model.User;

public interface DirectMessageRepository
        extends JpaRepository<DirectMessage, Long> {

        List<DirectMessage>
    findBySenderAndRecipientOrSenderAndRecipientOrderBySentAtAsc(
        User sender1,
        User recipient1,
        User sender2,
        User recipient2
    );
}