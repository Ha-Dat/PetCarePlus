package org.example.petcareplus.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
    private Long accountId;

    @NotBlank(message = "Họ tên không được để trống")
    @Length(min = 2, max = 100, message = "Họ tên phải từ 2 đến 100 ký tự")
    private String name;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "0[0-9]{9}", message = "Số điện thoại phải là 10 số và bắt đầu bằng 0")
    @Column(unique = true, nullable = false)
    private String phone;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
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

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public @NotBlank(message = "Họ tên không được để trống") @Length(min = 2, max = 100, message = "Họ tên phải từ 2 đến 100 ký tự") String getName() {
        return name;
    }

    public void setName(@NotBlank(message = "Họ tên không được để trống") @Length(min = 2, max = 100, message = "Họ tên phải từ 2 đến 100 ký tự") String name) {
        this.name = name;
    }

    public @NotBlank(message = "Số điện thoại không được để trống") @Pattern(regexp = "0[0-9]{9}", message = "Số điện thoại phải là 10 số và bắt đầu bằng 0") String getPhone() {
        return phone;
    }

    public void setPhone(@NotBlank(message = "Số điện thoại không được để trống") @Pattern(regexp = "0[0-9]{9}", message = "Số điện thoại phải là 10 số và bắt đầu bằng 0") String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
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
