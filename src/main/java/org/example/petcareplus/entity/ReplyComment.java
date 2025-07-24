package org.example.petcareplus.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ReplyComments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReplyComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long replyCommentId;

    @Column(nullable = false)
    private String comment;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_post_id")
    private CommentPost commentPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    //method thêm trong trường hợp lombok không hoạt động
    public Long getReplyCommentId() {
        return replyCommentId;
    }

    public void setReplyCommentId(Long replyCommentId) {
        this.replyCommentId = replyCommentId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public CommentPost getCommentPost() {
        return commentPost;
    }

    public void setCommentPost(CommentPost commentPost) {
        this.commentPost = commentPost;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}

