package org.library.controller;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import org.library.model.Category;
import org.library.model.User;
import org.library.service.CategoryService;
import org.springframework.ui.Model;

/**
 * abstract controller for some common methods 
 */
public abstract class AbstractController {

    /**
     * get user if login
     * @param session
     * @return
     */
    public User getLoginUser(HttpSession session) {
        User user = null;
        Object tmp = session.getAttribute("user");
        if (tmp != null) {
            user = (User) tmp;
        }

        return user;
    }

    /**
     * check if login and set user to model
     * @param session
     * @param model
     * @return
     */
    public String checkLogin(HttpSession session, Model model) {
        return checkLogin(session, model, false);
    }

    /**
     * check if login and set user
     * @param session
     * @param model
     * @param requiredAdmin
     * @return
     */
    public String checkLogin(HttpSession session, Model model, boolean requiredAdmin) {
        User user = getLoginUser(session);
        if (user != null) {
            if (requiredAdmin && !user.isAdmin()) {
                return "redirect:/index";
            }

            model.addAttribute("user", user);
            return null;
        }

        return "redirect:/login";
    }

    /**
     * set error message
     * @param model
     * @param error
     */
    public void setError(Model model, String error) {
        model.addAttribute("error", error);
    }

    /**
     * query db and set category to model
     * @param model
     * @param categoryId
     * @return selected category
     */
    protected Category setCategoryModel(CategoryService categoryService, Model model, Integer categoryId, boolean needAll) {
        List<Category> categoryList = categoryService.getAllCategory();

        Category allCategory = new Category();
        allCategory.setId(0);
        allCategory.setName("All");

        if (categoryList == null) {
            categoryList = new ArrayList<>();
        }

        Category selected = null;
        if (categoryId != null) {
            Iterator<Category> iter = categoryList.iterator();
            while (iter.hasNext()) {
                Category item = iter.next();
                if (item.getId() == categoryId) {
                    iter.remove();
                    selected = item;
                    break;
                }
            }
        }

        if (selected == null) {
            if (needAll) {
                categoryList.add(0, allCategory);
            }
        } else {
            categoryList.add(0, selected);
            if (needAll) {
                categoryList.add(allCategory);
            }
        }

        model.addAttribute("categoryList", categoryList);

        return selected;
    }

}
