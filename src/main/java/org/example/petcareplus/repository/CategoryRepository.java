package org.example.petcareplus.repository;
import org.example.petcareplus.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> findByParentIsNull();


@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> findAll();
}
