package org.example.petcareplus.controller;

import org.example.petcareplus.dto.PostDTO;
import org.example.petcareplus.entity.CommentPost;
import org.example.petcareplus.entity.Post;
import org.example.petcareplus.repository.CommentPostRepository;
import org.example.petcareplus.repository.PostRepository;
import org.example.petcareplus.repository.ReplyCommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class ForumController {

    private PostRepository postRepository;
    private CommentPostRepository commentPostRepository;
    private ReplyCommentRepository replyCommentRepository;

    @Autowired
    public ForumController(PostRepository postRepository, CommentPostRepository commentPostRepository, ReplyCommentRepository replyCommentRepository) {
        this.postRepository = postRepository;
        this.commentPostRepository = commentPostRepository;
        this.replyCommentRepository = replyCommentRepository;
    }

    @GetMapping("/forum")
    public String getForumPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("rating").descending());
        Page<Post> postsPage = postRepository.findAll(pageable);

        model.addAttribute("posts", postsPage.getContent());
        model.addAttribute("hasNext", postsPage.hasNext());
        return "forum";
    }

    @GetMapping("/api/posts")
    @ResponseBody
    public List<PostDTO> getMorePosts(@RequestParam int page, @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("rating").descending());
        Page<Post> postsPage = postRepository.findAll(pageable);

        return postsPage.getContent().stream()
                .map(PostDTO::new)
                .toList();
    }

    @GetMapping("/post-detail/{id}")
    public String getPostDetail(@PathVariable Long id, Model model) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        List<CommentPost> comments = commentPostRepository.findByPost_PostId(id);

        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        model.addAttribute("commentCount", post.getComments().size());
        return "post-detail";
    }

}
