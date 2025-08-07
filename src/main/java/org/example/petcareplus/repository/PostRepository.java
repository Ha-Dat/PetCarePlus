package org.example.petcareplus.repository;

import org.example.petcareplus.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.medias")
    List<Post> findAllWithMedias();

    List<Post> findByIsCheckedTrue();

    List<Post> findByIsCheckedFalse();

    @Query("SELECT p FROM Post p WHERE p.isChecked = true ORDER BY p.createdAt DESC")
    List<Post> findApprovedPostsOrderByCreatedAtDesc();

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.medias WHERE p.account.accountId = :accountId")
    List<Post> findAllWithMediasByAccountId(@Param("accountId") Long accountId);
}
