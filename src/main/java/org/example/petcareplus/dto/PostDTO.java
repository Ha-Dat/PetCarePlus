package org.example.petcareplus.dto;
import org.example.petcareplus.entity.Media;
import org.example.petcareplus.enums.MediaCategory;
import org.example.petcareplus.enums.Rating;
import org.example.petcareplus.entity.Post;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public class PostDTO {
    private Long postId;
    private String title;
    private String description;
    private Integer rating;
    private LocalDateTime createAt;
    private String accountName;
    private Long accountId;
    private List<Media> medias;

    private List<MultipartFile> imageFiles;
    private List<MultipartFile> videoFiles;

    private List<String> oldImageUrls;
    private List<String> oldVideoUrls;

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

        this.createAt = post.getCreatedAt();
        this.accountName = post.getAccount() != null ? post.getAccount().getName() : "Ẩn danh";
        this.accountId = post.getAccount() != null ? post.getAccount().getAccountId() : null;
        this.medias = post.getMedias();
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

    public List<MultipartFile> getImageFiles() {
        return imageFiles;
    }
    public void setImageFiles(List<MultipartFile> imageFiles) {
        this.imageFiles = imageFiles;
    }

    public List<MultipartFile> getVideoFiles() {
        return videoFiles;
    }
    public void setVideoFiles(List<MultipartFile> videoFiles) {
        this.videoFiles = videoFiles;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }
    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public List<Media> getMedias() {
        return medias;
    }
    public void setMedias(List<Media> medias) {
        this.medias = medias;
    }

    public List<String> getOldImageUrls() {
        return oldImageUrls;
    }
    public void setOldImageUrls(List<String> oldImageUrls) {
        this.oldImageUrls = oldImageUrls;
    }

    public List<String> getOldVideoUrls() {
        return oldVideoUrls;
    }
    public void setOldVideoUrls(List<String> oldVideoUrls) {
        this.oldVideoUrls = oldVideoUrls;
    }
}
