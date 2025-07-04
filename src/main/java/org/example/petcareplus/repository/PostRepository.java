package org.example.petcareplus.repository;

import org.example.petcareplus.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
