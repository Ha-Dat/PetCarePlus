package org.example.petcareplus.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.example.petcareplus.enums.AccountRole;
import org.example.petcareplus.enums.AccountStatus;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
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
    @Length(min = 2, max = 50, message = "Họ tên phải từ 2 đến 50 ký tự")
    private String name;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "0[0-9]{9}", message = "Số điện thoại phải là 10 số và bắt đầu bằng 0")
    @Column(unique = true, nullable = false)
    private String phone;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountRole role;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountStatus status;

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

    @OneToMany(mappedBy = "account")
    private List<PostRating> postRatings;

    private String otp;
    private LocalDateTime otpExpiry;

    //method thêm trong trường hợp lombok không hoạt động

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public @NotBlank(message = "Họ tên không được để trống") @Length(min = 2, max = 50, message = "Họ tên phải từ 2 đến 50 ký tự") String getName() {
        return name;
    }

    public void setName(@NotBlank(message = "Họ tên không được để trống") @Length(min = 2, max = 50, message = "Họ tên phải từ 2 đến 50 ký tự") String name) {
        this.name = name;
    }

    public @NotBlank(message = "Số điện thoại không được để trống") @Pattern(regexp = "0[0-9]{9}", message = "Số điện thoại phải là 10 số và bắt đầu bằng 0") String getPhone() {
        return phone;
    }

    public void setPhone(@NotBlank(message = "Số điện thoại không được để trống") @Pattern(regexp = "0[0-9]{9}", message = "Số điện thoại phải là 10 số và bắt đầu bằng 0") String phone) {
        this.phone = phone;
    }

    public AccountRole getRole() {
        return role;
    }

    public void setRole(AccountRole role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
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

    public List<PostRating> getPostRatings() {
        return postRatings;
    }

    public void setPostRatings(List<PostRating> postRatings) {
        this.postRatings = postRatings;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public LocalDateTime getOtpExpiry() {
        return otpExpiry;
    }

    public void setOtpExpiry(LocalDateTime otpExpiry) {
        this.otpExpiry = otpExpiry;
    }
}
