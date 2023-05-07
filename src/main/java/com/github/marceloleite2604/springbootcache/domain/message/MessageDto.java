package com.github.marceloleite2604.springbootcache.domain.message;

import com.github.marceloleite2604.springbootcache.validation.group.Post;
import com.github.marceloleite2604.springbootcache.validation.group.Put;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class MessageDto {

    private String id;

    private Long time;

    @NotBlank(groups = Put.class)
    private String user;

    @NotBlank(groups = {Put.class, Post.class})
    private String content;
}
