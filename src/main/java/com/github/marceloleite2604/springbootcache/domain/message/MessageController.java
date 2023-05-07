package com.github.marceloleite2604.springbootcache.domain.message;

import com.github.marceloleite2604.springbootcache.validation.ValidUuid;
import com.github.marceloleite2604.springbootcache.validation.group.Post;
import com.github.marceloleite2604.springbootcache.validation.group.Put;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = MessageController.BASE_PATH)
@RequiredArgsConstructor
public class MessageController {

  public static final String BASE_PATH = "messages";

  private final MessageService messageService;

  private final MessageToDtoMapper messageToDtoMapper;

  @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> put(
      @RequestBody
      @Validated(Put.class)
      MessageDto messageDto) {
    final var message = messageToDtoMapper.mapFrom(messageDto);

    final var persistedMessage = messageService.save(message);

    return createResponseEntityWithStatusCreatedAndLocationHeader(persistedMessage);
  }

  @PostMapping(
      path = "/{id}",
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> post(
      @PathVariable
      @ValidUuid
      String id,

      @RequestBody
      @Validated(Post.class)
      MessageDto messageDto
  ) {
    final var uuid = UUID.fromString(id);

    final var message = messageToDtoMapper.mapFrom(messageDto);

    return messageService.update(uuid, message)
        .map(updatedMessage -> ResponseEntity.ok()
            .<Void>build())
        .orElseGet(() -> ResponseEntity.notFound()
            .build());
  }

  @GetMapping(path = "/{id}")
  public ResponseEntity<MessageDto> get(@PathVariable @ValidUuid String id) {

    final var uuid = UUID.fromString(id);

    return messageService.findById(uuid)
        .map(messageToDtoMapper::mapTo)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound()
            .build());
  }

  @GetMapping
  public ResponseEntity<List<MessageDto>> getAll() {

    final var messages = messageService.findAll();

    if (CollectionUtils.isEmpty(messages)) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT)
          .build();
    }

    final var messagesDto = messageToDtoMapper.mapAllTo(messages)
        .stream()
        .sorted(Comparator.comparing(MessageDto::getTime))
        .toList();

    return ResponseEntity.ok(messagesDto);
  }

  @DeleteMapping(path = "/{id}")
  public ResponseEntity<Void> delete(@PathVariable @ValidUuid String id) {

    final var uuid = UUID.fromString(id);

    messageService.delete(uuid);

    return ResponseEntity.ok()
        .build();
  }

  private ResponseEntity<Void> createResponseEntityWithStatusCreatedAndLocationHeader(Message message) {
    final var location = createMessageLocationUri(message);

    return ResponseEntity.status(HttpStatus.CREATED)
        .header(HttpHeaders.LOCATION, location)
        .build();
  }

  private String createMessageLocationUri(Message message) {
    return ServletUriComponentsBuilder
        .fromCurrentRequest()
        .pathSegment("{id}")
        .buildAndExpand(message.getId())
        .toUri()
        .toASCIIString();
  }
}
