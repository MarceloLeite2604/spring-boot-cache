package com.github.marceloleite2604.springbootcache.domain.message;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Entity
@NoArgsConstructor
public class Message {

    @Id
    private UUID id;

    private LocalDateTime time;

    private String user;

    private String content;
}
