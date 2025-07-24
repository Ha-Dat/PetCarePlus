package org.example.petcareplus.dto;
import org.example.petcareplus.entity.Enum.Rating;
import org.example.petcareplus.entity.Post;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;

public class PostDTO {
    private Long postId;
    private String title;
    private String description;
    private Integer rating;
    private String image;
    private String video;
    private LocalDateTime createAt;
    private String accountName;
    private Long accountId;

    private MultipartFile imageFile;
    private MultipartFile videoFile;

    public PostDTO(Post post) {
        this.postId = post.getPostId();
        this.title = post.getTitle();
        this.description = post.getDescription();
        // tính toàn lượng upvote và downvote
        if (post.getPostRatings() != null) {
            int upvoteCount = (int) post.getPostRatings().stream()
                    .filter(r -> r.getRating() == Rating.UPVOTE)
                    .count();

            int downvoteCount = (int) post.getPostRatings().stream()
                    .filter(r -> r.getRating() == Rating.DOWNVOTE)
                    .count();

            this.rating = upvoteCount - downvoteCount;
        } else {
            this.rating = null;
        }

        this.image = post.getImage();
        this.video = post.getVideo();
        this.createAt = post.getCreatedAt();
        this.accountName = post.getAccount() != null ? post.getAccount().getName() : "Ẩn danh";
        this.accountId = post.getAccount() != null ? post.getAccount().getAccountId() : null;
    }

    public PostDTO() {

    }

    // Getter + Setter
    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }

    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }

    public String getVideo() {
        return video;
    }
    public void setVideo(String video) {
        this.video = video;
    }

    public MultipartFile getImageFile() {
        return imageFile;
    }
    public void setImageFile(MultipartFile imageFile) {
        this.imageFile = imageFile;
    }

    public MultipartFile getVideoFile() {
        return videoFile;
    }
    public void setVideoFile(MultipartFile videoFile) {
        this.videoFile = videoFile;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }
    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }
}
