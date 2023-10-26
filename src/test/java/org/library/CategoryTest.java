package org.library;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import jakarta.annotation.Resource;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.library.model.Book;
import org.library.model.Category;
import org.library.service.BookService;
import org.library.service.CategoryService;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CategoryTest {
    @Resource
    private CategoryService categoryService;

    private Category category;

    @BeforeEach
    void setUp() {
        categoryService.clearAll();

        category = new Category();
        category.setName("Test");
    }

    @Test
    public void testAddAndGet() throws Exception {
        Assert.assertNull(category.getId());
        category = categoryService.addCategory(category);
        Assert.assertNotNull(category);
        Assert.assertTrue(category.getId() > 0);
    }

    @Test
    public void testUpdate() throws Exception {
        category = categoryService.addCategory(category);
        Assert.assertEquals("Test", category.getName());
        Category c1 = new Category();
        c1.setName("c1");
        Assert.assertNotNull(categoryService.addCategory(c1));
        category.setName("c1");
        Assert.assertNull(categoryService.updateCategory(category));
        category.setName("c2");
        Assert.assertNotNull(categoryService.updateCategory(category));

        Assert.assertEquals("c2", category.getName());
    }

}
