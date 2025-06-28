package org.example.petcareplus.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Entity
@Table(name = "Accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer accountId;

    @Length(min = 2, max = 100)
    private String name;

    @Column(unique = true, nullable = false)
    private String phone;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    @Pattern(regexp = "^(?=.*[0-9])(?=\\\\S+$).{8,}$") // At least 8 characters, 1 number, no whitespaces
    private String password;

    @Column(nullable = false)
    private String status;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
    private Profile profile;

    @OneToMany(mappedBy = "account")
    private List<ProductFeedback> feedbacks;

    @OneToMany(mappedBy = "account")
    private List<Post> posts;

    @OneToMany(mappedBy = "account")
    private List<CommentPost> commentPosts;

    @OneToMany(mappedBy = "account")
    private List<ReplyComment> replyComments;

    //method thêm trong trường hợp lombok không hoạt động


    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public @Length(min = 2, max = 100) String getName() {
        return name;
    }

    public void setName(@Length(min = 2, max = 100) String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public @Pattern(regexp = "^(?=.*[0-9])(?=\\\\S+$).{8,}$") String getPassword() {
        return password;
    }

    public void setPassword(@Pattern(regexp = "^(?=.*[0-9])(?=\\\\S+$).{8,}$") String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public List<ProductFeedback> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(List<ProductFeedback> feedbacks) {
        this.feedbacks = feedbacks;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public List<CommentPost> getCommentPosts() {
        return commentPosts;
    }

    public void setCommentPosts(List<CommentPost> commentPosts) {
        this.commentPosts = commentPosts;
    }

    public List<ReplyComment> getReplyComments() {
        return replyComments;
    }

    public void setReplyComments(List<ReplyComment> replyComments) {
        this.replyComments = replyComments;
    }
}
