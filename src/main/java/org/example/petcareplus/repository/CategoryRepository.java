package org.example.petcareplus.repository;
import org.example.petcareplus.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentIsNull();
    List<Category> findAll();
    void deleteById(Long id);
    Page<Category> findAll(Pageable pageable);
}
