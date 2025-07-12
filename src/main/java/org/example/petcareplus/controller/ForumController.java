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
import java.util.Optional;

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
        return "create-post"; // Tên file HTML
    }

    @PostMapping("/create-post")
    public String savePost(@ModelAttribute PostDTO postDTO, HttpSession session) {
        Account account = (Account) session.getAttribute("loggedInUser");
        forumService.savePost(postDTO, account.getAccountId());
        return "redirect:/forum"; // Redirect sau khi lưu
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
        Long fakeAccountId = 1L; // Thay bằng user ID thực tế nếu có auth
        forumService.updatePost(postDTO, fakeAccountId);
        return "redirect:/forum";
    }

    // xử lý Post delete
    @GetMapping("/delete-post/{postId}")
    public String deletePost(@PathVariable Long postId) {
        forumService.deletePostById(postId);
        return "redirect:/forum"; // Về trang chủ hoặc danh sách
    }

}
