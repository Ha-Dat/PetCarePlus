package org.example.petcareplus.service.impl;

import org.example.petcareplus.dto.PostDTO;
import org.example.petcareplus.entity.Account;
import org.example.petcareplus.entity.CommentPost;
import org.example.petcareplus.entity.Post;
import org.example.petcareplus.repository.AccountRepository;
import org.example.petcareplus.repository.CommentPostRepository;
import org.example.petcareplus.repository.PostRepository;
import org.example.petcareplus.repository.ReplyCommentRepository;
import org.example.petcareplus.service.ForumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ForumServiceImpl implements ForumService {
    private PostRepository postRepository;
    private CommentPostRepository commentPostRepository;
    private ReplyCommentRepository replyCommentRepository;
    private AccountRepository accountRepository;

    @Autowired
    public ForumServiceImpl(PostRepository postRepository, CommentPostRepository commentPostRepository, ReplyCommentRepository replyCommentRepository, AccountRepository accountRepository) {
        this.postRepository = postRepository;
        this.commentPostRepository = commentPostRepository;
        this.replyCommentRepository = replyCommentRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public Page<Post> findAll(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("rating").descending());
        Page<Post> postsPage = postRepository.findAll(pageable);
        return postsPage;
    }

    @Override
    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }

    @Override
    public List<CommentPost> findCommentByPostId(Long postId) {
        return commentPostRepository.findCommentByPost_PostId(postId);
    }

    @Override
    public Post savePost(PostDTO postDTO, Long accountId) {
        Post post = new Post();
        post.setTitle(postDTO.getTitle());
        post.setDescription(postDTO.getDescription());
        post.setRating(postDTO.getRating());
        post.setChecked(false);

        // Gán account
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        post.setAccount(account);

        // Xử lý image
        if (postDTO.getImage() != null && !postDTO.getImage().isEmpty()) {
            String imageName = saveFile(postDTO.getImageFile(), "uploads/images/");
            post.setImage(imageName);
        }

        // Xử lý video
        if (postDTO.getVideo() != null && !postDTO.getVideo().isEmpty()) {
            String videoName = saveFile(postDTO.getVideoFile(), "uploads/videos/");
            post.setVideo(videoName);
        }

        return postRepository.save(post);
    }

    private String saveFile(MultipartFile file, String uploadDir) {
        String fileName = file.getOriginalFilename();
        File uploadPath = new File(uploadDir);
        if (!uploadPath.exists()) {
            uploadPath.mkdirs();
        }
        try {
            file.transferTo(new File(uploadDir + fileName));
        } catch (IOException e) {
            throw new RuntimeException("Could not save file: " + fileName, e);
        }
        return fileName;
    }
}
