package org.example.petcareplus.controller;

import jakarta.servlet.http.HttpSession;
import org.example.petcareplus.dto.PostDTO;
import org.example.petcareplus.entity.Account;
import org.example.petcareplus.entity.CommentPost;
import org.example.petcareplus.entity.Post;
import org.example.petcareplus.service.ForumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

        Page<Post> postsPage = forumService.findAll(page,size,"rating");
        List<PostDTO> postDTOs = postsPage.getContent().stream()
                .map(PostDTO::new)
                .toList();

        model.addAttribute("posts", postDTOs);
        model.addAttribute("hasNext", postsPage.hasNext());
        return "forum";
    }

    @GetMapping("/api/posts")
    @ResponseBody
    public List<PostDTO> getMorePosts(@RequestParam int page, @RequestParam int size) {
        Page<Post> postsPage = forumService.findAll(page,size,"rating");

        return postsPage.getContent().stream()
                .map(PostDTO::new)
                .toList();
    }

    @GetMapping("/post-detail/{id}")
    public String getPostDetail(@PathVariable Long id, Model model, HttpSession session) {
        Post post = forumService.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Account account = (Account) session.getAttribute("loggedInUser");

        List<CommentPost> comments = forumService.findCommentByPostId(id);

        model.addAttribute("account", account);
        model.addAttribute("post", post);
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
    @GetMapping("/update-post/{id}")
    public String updatePostForm(@PathVariable Long id, Model model, HttpSession session) {
        Account account = (Account) session.getAttribute("loggedInUser");
        if (account == null) return "redirect:/login";
        Post post = forumService.findById(id)
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
        forumService.saveCommentPost(postId, account.getAccountId(), content);
        return "redirect:/post-detail/" + postId;
    }

    // Reply comment
    @PostMapping("/post-detail/{postId}/reply/{commentId}/reply")
    public String replyComment(@PathVariable Long postId, @PathVariable Long commentId, @RequestParam String content, HttpSession session) {
        Account account = (Account) session.getAttribute("loggedInUser");
        if (account == null) return "redirect:/login";
        forumService.saveReplyComment(commentId, account.getAccountId(), content);

        return "redirect:/post-detail/" + postId;
    }

    // Edit comment
    @PostMapping("/post-detail/{postId}/comment/{commentId}/edit")
    public String editComment(@PathVariable Long postId, @PathVariable Long commentId, @RequestParam String content, HttpSession session) {
        Account account = (Account) session.getAttribute("loggedInUser");
        if (account == null) return "redirect:/login";

        // Gọi Service
        forumService.updateCommentPost(commentId, content, account);
        return "redirect:/post-detail/" + postId;
    }

    // Edit Reply Comment
    @PostMapping("/post-detail/{postId}/reply/{replyId}/edit")
    public String editReplyComment(@PathVariable Long postId, @PathVariable Long replyId, @RequestParam String content, HttpSession session) {
        Account account = (Account) session.getAttribute("loggedInUser");
        if (account == null) return "redirect:/login";

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
}
