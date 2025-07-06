package org.example.petcareplus.controller;

import org.example.petcareplus.dto.PostDTO;
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
            Model model) {

        Page<Post> postsPage = forumService.findAll(page,size,"rating");

        model.addAttribute("posts", postsPage.getContent());
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
    public String getPostDetail(@PathVariable Long id, Model model) {
        Post post = forumService.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        List<CommentPost> comments = forumService.findCommentByPostId(id);

        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        model.addAttribute("commentCount", post.getComments().size());
        return "post-detail";
    }

    @GetMapping("/create-post")
    public String createPostForm(Model model) {
        model.addAttribute("postDTO", new PostDTO());
        return "create-post"; // Tên file HTML
    }

    @PostMapping("/create-post")
    public String savePost(@ModelAttribute PostDTO postDTO) {
        Long fakeAccountId = 1L; // Lấy từ session / auth thực tế
        forumService.savePost(postDTO, fakeAccountId);
        return "redirect:/forum"; // Redirect sau khi lưu
    }
}
