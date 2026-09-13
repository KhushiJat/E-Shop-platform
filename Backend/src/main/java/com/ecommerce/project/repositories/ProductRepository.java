package com.ecommerce.project.repositories;

import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor <Product> {

    Page<Product> findByCategoryOrderByPriceAsc(Category category, Pageable pageDetails);

    Page<Product> findByProductNameLikeIgnoreCase(String keyword, Pageable pageDetails);

    Page<Product> findByUser(User user, Pageable pageDetails);

    // Multi-level native query to pull items matching the category, its subcategories, and grandchild subcategories
    @Query(value = "SELECT * FROM products p WHERE p.category_id IN " +
            "(SELECT c.id FROM categories c WHERE c.id = :categoryId " +
            "UNION SELECT c1.id FROM categories c1 WHERE c1.parent_id = :categoryId " +
            "UNION SELECT c2.id FROM categories c2 JOIN categories c1 ON c2.parent_id = c1.id WHERE c1.parent_id = :categoryId)",
            nativeQuery = true)
    Page<Product> findByCategoryHierarchy(@Param("categoryId") Long categoryId, Pageable pageable);
}