package org.example.petcareplus.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "CommentPosts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentPostId;

    private String comment;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @OneToMany(mappedBy = "commentPost", cascade = CascadeType.ALL)
    private List<ReplyComment> replies;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    //method thêm trong trường hợp lombok không hoạt động
    public Long getCommentPostId() {
        return commentPostId;
    }

    public void setCommentPostId(Long commentPostId) {
        this.commentPostId = commentPostId;
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

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public List<ReplyComment> getReplies() {
        return replies;
    }

    public void setReplies(List<ReplyComment> replies) {
        this.replies = replies;
    }
}
