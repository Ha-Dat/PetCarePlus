package org.example.petcareplus.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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

}
