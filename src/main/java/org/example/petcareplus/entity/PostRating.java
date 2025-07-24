package org.example.petcareplus.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.petcareplus.entity.Enum.Rating;

@Entity
@Table(name = "post_ratings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostRating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_rating_id")
    private Long postRatingId;

    @Enumerated(EnumType.STRING)
    private Rating rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    public Long getPostRatingId() {
        return postRatingId;
    }

    public void setPostRatingId(Long postRatingId) {
        this.postRatingId = postRatingId;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
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
}
