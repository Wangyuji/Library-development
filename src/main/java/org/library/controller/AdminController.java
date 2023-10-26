package org.library.controller;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.library.meta.BookAddRes;
import org.library.meta.BorrowRes;
import org.library.meta.ReturnRes;
import org.library.model.Book;
import org.library.model.Category;
import org.library.model.User;
import org.library.service.BookService;
import org.library.service.CategoryService;
import org.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * controller for admin operations
 */
@Controller
public class AdminController extends AbstractController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BookService bookService;

    /**
     * manage categories
     * @param session
     * @param model
     * @return
     */
    @GetMapping("/category")
    public String category(HttpSession session, Model model) {
        User user = getLoginUser(session);
        if (user != null && user.isAdmin()) {
            List<Category> allCategories = categoryService.getAllCategory();
            allCategories = allCategories == null ? new ArrayList<>() : allCategories;

            model.addAttribute("categoryList", allCategories);
            model.addAttribute("user", user);

            return "category";
        } else {
            return "redirect:/login";
        }
    }

    /**
     * post to create a new category
     * @param session
     * @param model
     * @param name
     * @return
     */
    @PostMapping("/category/add")
    public String addCategory(HttpSession session, Model model,
            @RequestParam("name") String name) {
        User user = getLoginUser(session);
        if (user != null && user.isAdmin()) {
            Category exist = categoryService.getCategoryByName(name);
            if (exist != null) {
                model.addAttribute("error", "Category already exists");
            } else {
                Category newCategory = new Category();
                newCategory.setName(name);
                newCategory = categoryService.addCategory(newCategory);
                if (newCategory == null) {
                    model.addAttribute("error", "Category add failed");
                }
            }

            return "redirect:/category";
        } else {
            return "redirect:/login";
        }
    }

    /**
     * delete a category
     * @param session
     * @param model
     * @param id
     * @return
     */
    @GetMapping("/category/delete")
    public String deleteCategory(HttpSession session, Model model,
            @RequestParam("id") Integer id) {
        User user = getLoginUser(session);
        if (user != null && user.isAdmin()) {
            Category exist = categoryService.getCategory(id);
            if (exist == null) {
                model.addAttribute("error", "Category not exists");
            } else {
                categoryService.deleteCategory(id);
            }

            return "redirect:/category";
        } else {
            return "redirect:/login";
        }
    }

    /**
     * update a category
     * @param session
     * @param model
     * @param id
     * @param name
     * @return
     */
    @PostMapping("/category/edit")
    public String updateCategory(HttpSession session, Model model,
            @RequestParam("id") Integer id,
            @RequestParam("name") String name) {
        User user = getLoginUser(session);
        if (user != null && user.isAdmin()) {
            Category exist = categoryService.getCategory(id);
            Category newNameCategory = categoryService.getCategoryByName(name);
            if (exist == null) {
                model.addAttribute("error", "Category not exists");
            } else if (newNameCategory != null) {
                model.addAttribute("error", "Category with name " + name + " exists");
            } else {
                exist.setName(name);
                exist = categoryService.addCategory(exist);
                if (exist == null) {
                    model.addAttribute("error", "Category update failed");
                }
            }

            return "redirect:/category";
        } else {
            return "redirect:/login";
        }
    }

    /**
     * edit a category
     * @param session
     * @param model
     * @param id
     * @return
     */
    @GetMapping("/category/edit")
    public String editCategory(HttpSession session, Model model,
            @RequestParam("id") Optional<Integer> id,
            @RequestParam("success") Optional<Integer> success) {
        User user = getLoginUser(session);
        Category category = categoryService.getCategory(id.orElse(null));
        if (category == null || user == null || !user.isAdmin()) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);

        model.addAttribute("category", category);
        if (success.isPresent()) {
            setError(model, "Update Success");
        }

        return "category_edit";
    }

    /**
     * edit a book
     * @param session
     * @param model
     * @param id
     * @return
     */
    @GetMapping("/book/edit")
    public String editBook(HttpSession session, Model model,
            @RequestParam("id") Optional<Integer> id,
            @RequestParam("success") Optional<Integer> success) {
        User user = getLoginUser(session);
        Book book = bookService.getBookById(id.orElse(null));
        if (book == null || user == null || !user.isAdmin()) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        setCategoryModel(categoryService, model, book.getCategory().getId(), false);

        model.addAttribute("book", book);
        if (success.isPresent()) {
            setError(model, "Update Success");
        }

        return "book_edit";
    }

    /**
     * add a book
     * @param session
     * @param model
     * @return
     */
    @GetMapping("/book/add")
    public String addBook(HttpSession session, Model model, @RequestParam("res") Optional<String> res) {
        User user = getLoginUser(session);
        if (user == null || !user.isAdmin()) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);

        if (res.isPresent()) {
            setError(model, BookAddRes.valueOf(res.get()).getMessage());
        }

        setCategoryModel(categoryService, model, null, false);

        return "book_add";
    }

    /**
     * update a book
     * @param id
     * @param title
     * @param author
     * @param publish
     * @param description
     * @param remain
     * @param categoryId
     * @param model
     * @param session
     * @return
     */
    @PostMapping("/book/edit")
    public String bookUpdate(@RequestParam("id") Integer id,
            @RequestParam("title") String title,
            @RequestParam("author") String author,
            @RequestParam("publish") Integer publish,
            @RequestParam("description") String description,
            @RequestParam("language") String language,
            @RequestParam("remain") Integer remain,
            @RequestParam("category") Integer categoryId,
            Model model,
            HttpSession session) {
        User user = getLoginUser(session);
        Book book = bookService.getBookById(id);
        if (book == null || user == null || !user.isAdmin()) {
            return "redirect:/login";
        }

        book.setTitle(title);
        book.setAuthor(author);
        book.setPublisherYear(publish);
        book.setDescription(description);
        book.setLanguage(language);
        if (remain >= 0) {
            book.setRemain(remain);
        }
        Category category = categoryService.getCategory(categoryId);
        if (category != null) {
            book.setCategory(category);
        }
        bookService.updateBook(book);

        return "redirect:/book/edit?success=1&id=" + book.getId();
    }

    /**
     * add a book
     * @param title
     * @param author
     * @param publish
     * @param description
     * @param remain
     * @param categoryId
     * @param session
     * @return
     */
    @PostMapping("/book/add")
    public String addBook(
            @RequestParam("title") String title,
            @RequestParam("author") String author,
            @RequestParam("publish") Integer publish,
            @RequestParam("description") String description,
            @RequestParam("language") String language,
            @RequestParam("remain") Integer remain,
            @RequestParam("category") Integer categoryId,
            HttpSession session) {
        User user = getLoginUser(session);
        if (user == null || !user.isAdmin()) {
            return "redirect:/login";
        }

        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setPublisherYear(publish);
        book.setDescription(description);
        book.setLanguage(language);
        if (remain >= 0) {
            book.setRemain(remain);
        } else {
            return "redirect:/book/add?res=" + BookAddRes.REMAIN_NEGATIVE;
        }

        Category category = categoryService.getCategory(categoryId);
        if (category != null) {
            book.setCategory(category);
        }
        Book newBook = bookService.addBook(book);
        if (newBook == null) {
            return "redirect:/book/add?res=" + BookAddRes.SYSTEM_ERROR;
        }

        return "redirect:/book";
    }
}
