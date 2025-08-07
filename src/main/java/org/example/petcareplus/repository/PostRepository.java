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

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.medias WHERE p.account.accountId = :accountId")
    List<Post> findAllWithMediasByAccountId(@Param("accountId") Long accountId);
}
