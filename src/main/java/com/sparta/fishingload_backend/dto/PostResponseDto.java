package com.sparta.fishingload_backend.dto;

import com.sparta.fishingload_backend.entity.Category;
import com.sparta.fishingload_backend.entity.Comment;
import com.sparta.fishingload_backend.entity.Post;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PostResponseDto {
    private Long id;
    private String title;
    private String accountId;
    private String contents;
    private int postLike;
    private LocalDateTime createdTime;
    private LocalDateTime modifiedTime;
    private String fishtype;
    private String locationdate;
    private Category category;
    private List<Comment> commentList;

    public PostResponseDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.accountId = post.getAccountId();
        this.contents = post.getContents();
        this.postLike = post.getPostLike();
        this.createdTime = post.getCreatedTime();
        this.modifiedTime = post.getModifiedTime();
        this.fishtype = post.getFishtype();
        this.locationdate = post.getLocationdate();
        this.category = post.getCategory();
        this.commentList = post.getCommentList();
    }
}
