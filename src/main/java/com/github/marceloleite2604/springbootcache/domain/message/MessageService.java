package com.github.marceloleite2604.springbootcache.domain.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

  private final MessageRepository messageRepository;

  public Message save(Message message) {

    final var messageToBePersisted = message.toBuilder()
        .id(UUID.randomUUID())
        .time(LocalDateTime.now())
        .build();

    return messageRepository.save(messageToBePersisted);
  }

  @Cacheable(
      cacheNames = "message",
      unless="#result == null")
  public Optional<Message> findById(UUID id) {
    log.info("Searching for message on repository.");
    return messageRepository.findById(id);
  }

  public Collection<Message> findAll() {
    return messageRepository.findAll();
  }

  public Optional<Message> update(UUID id, Message updatedMessage) {
    return messageRepository.findById(id)
        .map(persistedMessage -> merge(persistedMessage, updatedMessage))
        .map(messageRepository::save);
  }

  private Message merge(Message persistedMessage, Message updatedMessage) {
    return persistedMessage.toBuilder()
        .content(updatedMessage.getContent())
        .time(LocalDateTime.now())
        .build();
  }

  public void delete(UUID id) {
    messageRepository.deleteById(id);
  }
}
