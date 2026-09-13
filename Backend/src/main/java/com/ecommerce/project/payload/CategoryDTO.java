package com.ecommerce.project.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    @Schema(description = "Category ID", example = "101")
    private Long categoryId;

    @Schema(description = "Category name for category you wish to create", example = "Electronics")
    private String categoryName;

    // --- Added fields for Subcategories DTO ---
    @Schema(description = "Parent Category ID if this is a subcategory", example = "1")
    private Long parentId;

    @Schema(description = "List of subcategories")
    private List<CategoryDTO> subCategories;
}




//package com.ecommerce.project.payload;
//
//import io.swagger.v3.oas.annotations.media.Schema;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class CategoryDTO {
//    @Schema(description = "Category ID", example = "101")
//    private Long categoryId;
//
//    @Schema(description = "Category name for category you wish to create", example = "iPhone 16")
//    private String categoryName;
//
//}
