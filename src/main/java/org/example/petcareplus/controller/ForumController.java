package org.example.petcareplus.controller;

import jakarta.servlet.http.HttpSession;
import org.example.petcareplus.dto.PostDTO;
import org.example.petcareplus.entity.Account;
import org.example.petcareplus.entity.CommentPost;
import org.example.petcareplus.enums.Rating;
import org.example.petcareplus.entity.Post;
import org.example.petcareplus.service.ForumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@Controller
public class ForumController {

    private ForumService forumService;

    @Autowired
    public ForumController(ForumService forumService) {
        this.forumService= forumService;
    }

    @GetMapping("/forum")
    public String getForumPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model, HttpSession session) {

        Account account = (Account) session.getAttribute("loggedInUser");

        List<Post> allPosts = forumService.findAll();
        List<PostDTO> sortedPosts = allPosts.stream()
                .map(PostDTO::new)
                .sorted(Comparator.comparing(PostDTO::getRating, Comparator.nullsLast(Integer::compareTo)).reversed())
                .toList();

        // Tự tính chỉ số trang
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, sortedPosts.size());

        List<PostDTO> pageContent = sortedPosts.subList(fromIndex, toIndex);

        model.addAttribute("posts", pageContent);
        model.addAttribute("hasNext", toIndex < sortedPosts.size());
        return "forum";
    }


    @GetMapping("/api/posts")
    @ResponseBody
    public List<PostDTO> getMorePosts(@RequestParam int page, @RequestParam int size) {
        List<Post> allPosts = forumService.findAll();
        List<PostDTO> sortedPosts = allPosts.stream()
                .map(PostDTO::new)
                .sorted(Comparator.comparing(PostDTO::getRating, Comparator.nullsLast(Integer::compareTo)).reversed())
                .toList();

        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, sortedPosts.size());

        if (fromIndex >= sortedPosts.size()) {
            return List.of();
        }

        return sortedPosts.subList(fromIndex, toIndex);
    }



    @GetMapping("/post-detail/{postId}")
    public String getPostDetail(@PathVariable Long postId, Model model, HttpSession session) {
        Post post = forumService.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Account account = (Account) session.getAttribute("loggedInUser");

        List<CommentPost> comments = forumService.findCommentByPostId(postId);
        PostDTO postDTO = new PostDTO(post);

        model.addAttribute("account", account);
        model.addAttribute("post", post);
        model.addAttribute("postDTO", postDTO);
        model.addAttribute("comments", comments);
        model.addAttribute("commentCount", post.getComments().size());
        return "post-detail";
    }

    @GetMapping("/create-post")
    public String createPostForm(HttpSession session, Model model) {
        Account account = (Account) session.getAttribute("loggedInUser");
        if (account == null) return "redirect:/login";
        model.addAttribute("postDTO", new PostDTO());
        return "create-post";
    }

    @PostMapping("/create-post")
    public String savePost(@ModelAttribute PostDTO postDTO, HttpSession session) {
        Account account = (Account) session.getAttribute("loggedInUser");
        forumService.savePost(postDTO, account.getAccountId());
        return "redirect:/forum";
    }

    // Hiển thị form update
    @GetMapping("/update-post/{postId}")
    public String updatePostForm(@PathVariable Long postId, Model model, HttpSession session) {
        Account account = (Account) session.getAttribute("loggedInUser");
        if (account == null) return "redirect:/login";
        Post post = forumService.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        PostDTO postDTO = new PostDTO(post);
        model.addAttribute("postDTO", postDTO);
        return "update-post"; // Tên file HTML form update
    }

    // Xử lý POST update
    @PostMapping(value = "/update-post", consumes = {"multipart/form-data"})
    public String updatePost(@ModelAttribute PostDTO postDTO) {
        forumService.updatePost(postDTO);
        return "redirect:/forum";
    }

    // xử lý Post delete
    @GetMapping("/delete-post/{postId}")
    public String deletePost(@PathVariable Long postId) {
        forumService.deletePostById(postId);
        return "redirect:/forum";
    }

    @PostMapping("/post-detail/{postId}/comment")
    public String commentPost(@PathVariable Long postId, @RequestParam String content, HttpSession session) {
        Account account = (Account) session.getAttribute("loggedInUser");
        if (account == null) return "redirect:/login";

        // Kiểm tra nội dung
        if (content == null || content.trim().isEmpty()) {
            return "redirect:/post-detail/" + postId + "?error=empty";
        }

        // Gọi Service
        forumService.saveCommentPost(postId, account.getAccountId(), content);

        return "redirect:/post-detail/" + postId;
    }

    // Reply comment
    @PostMapping("/post-detail/{postId}/reply/{commentId}/reply")
    public String replyComment(@PathVariable Long postId, @PathVariable Long commentId, @RequestParam String content, HttpSession session) {
        Account account = (Account) session.getAttribute("loggedInUser");
        if (account == null) return "redirect:/login";

        // Kiểm tra nội dung
        if (content == null || content.trim().isEmpty()) {
            return "redirect:/post-detail/" + postId + "?error=empty";
        }

        // Gọi Service
        forumService.saveReplyComment(commentId, account.getAccountId(), content);

        return "redirect:/post-detail/" + postId;
    }

    // Edit comment
    @PostMapping("/post-detail/{postId}/comment/{commentId}/edit")
    public String editComment(@PathVariable Long postId, @PathVariable Long commentId, @RequestParam String content, HttpSession session) {
        Account account = (Account) session.getAttribute("loggedInUser");
        if (account == null) return "redirect:/login";

        // Kiểm tra nội dung
        if (content == null || content.trim().isEmpty()) {
            return "redirect:/post-detail/" + postId + "?error=empty";
        }

        // Gọi Service
        forumService.updateCommentPost(commentId, content, account);
        return "redirect:/post-detail/" + postId;
    }

    // Edit Reply Comment
    @PostMapping("/post-detail/{postId}/reply/{replyId}/edit")
    public String editReplyComment(@PathVariable Long postId, @PathVariable Long replyId, @RequestParam String content, HttpSession session) {
        Account account = (Account) session.getAttribute("loggedInUser");
        if (account == null) return "redirect:/login";

        // Kiểm tra nội dung
        if (content == null || content.trim().isEmpty()) {
            return "redirect:/post-detail/" + postId + "?error=empty";
        }

        // Gọi Service
        forumService.updateReplyComment(replyId, content, account);

        return "redirect:/post-detail/" + postId;
    }

    // Delete comment
    @GetMapping("/post-detail/{postId}/comment/{commentId}/delete")
    public String deleteComment(@PathVariable Long postId ,@PathVariable Long commentId, HttpSession session) {
        Account account = (Account) session.getAttribute("loggedInUser");
        if (account == null) return "redirect:/login";

        // Gọi Service
        forumService.deleteCommentPostById(commentId);

        return "redirect:/post-detail/" + postId;
    }

    // Delete reply comment
    @GetMapping("/post-detail/{postId}/reply/{replyId}/delete")
    public String deleteReplyComment(@PathVariable Long postId ,@PathVariable Long replyId, HttpSession session) {
        Account account = (Account) session.getAttribute("loggedInUser");
        if (account == null) return "redirect:/login";

        // Gọi Service
        forumService.deleteReplyCommentById(replyId);

        return "redirect:/post-detail/" + postId;
    }

    // rate post
    @GetMapping("/forum/{postId}/rating")
    public String RatingOnPostList(@PathVariable Long postId, @RequestParam Rating rating, HttpSession session) {
        Account account = (Account) session.getAttribute("loggedInUser");
        if (account == null) return "redirect:/login";

        // Gọi Service
        forumService.saveRating(postId, account.getAccountId(),rating);
        return "redirect:/forum";
    }

    @GetMapping("/post-detail/{postId}/rating")
    public String RatingOnPostDetail(@PathVariable Long postId, @RequestParam Rating rating, HttpSession session) {
        Account account = (Account) session.getAttribute("loggedInUser");
        if (account == null) return "redirect:/login";

        // Gọi Service
        forumService.saveRating(postId, account.getAccountId(), rating);
        return "redirect:/post-detail/" + postId;
    }
}
