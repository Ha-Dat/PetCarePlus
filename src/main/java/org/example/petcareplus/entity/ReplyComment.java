package org.example.petcareplus.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reply_comments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReplyComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer replyCommentId;

    private String comment;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "comment_post_id")
    private CommentPost commentPost;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
}
