package org.example.petcareplus.dto;
import org.example.petcareplus.entity.Post;

public class PostDTO {
    private Integer postId;
    private String title;
    private String description;
    private Integer rating;
    private String image;
    private String accountName;

    public PostDTO(Post post) {
        this.postId = post.getPostId();
        this.title = post.getTitle();
        this.description = post.getDescription();
        this.rating = post.getRating();
        this.image = post.getImage();
        this.accountName = post.getAccount() != null ? post.getAccount().getName() : "Ẩn danh";
    }

    // Getter + Setter
    public Integer getPostId() { return postId; }
    public void setPostId(Integer postId) { this.postId = postId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }
}
