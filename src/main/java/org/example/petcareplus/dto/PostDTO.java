package org.example.petcareplus.dto;
import org.example.petcareplus.entity.Post;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public class PostDTO {
    private Long postId;
    private String title;
    private String description;
    private Integer rating;
    private String image;
    private String video;
    private String accountName;

    private MultipartFile imageFile;
    private MultipartFile videoFile;

    public PostDTO(Post post) {
        this.postId = post.getPostId();
        this.title = post.getTitle();
        this.description = post.getDescription();
        this.rating = post.getRating();
        this.image = post.getImage();
        this.video = post.getVideo();
        this.accountName = post.getAccount() != null ? post.getAccount().getName() : "Ẩn danh";
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
}
