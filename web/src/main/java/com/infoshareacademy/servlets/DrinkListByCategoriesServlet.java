package com.infoshareacademy.servlets;

import com.infoshareacademy.domain.dto.CategoryView;
import com.infoshareacademy.domain.dto.FullDrinkView;
import com.infoshareacademy.freemarker.TemplateProvider;
import com.infoshareacademy.service.CategoryService;
import com.infoshareacademy.service.DrinkService;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet("/list-category")
public class DrinkListByCategoriesServlet extends HttpServlet {

    private static final Logger packageLogger = LoggerFactory.getLogger(LoggerServlet.class.getName());

    @Inject
    private DrinkService drinkService;

    @Inject
    private CategoryService categoryService;

    @Inject
    private TemplateProvider templateProvider;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html; charset=UTF-8");

        List<String> category = Arrays.stream(req.getParameterValues("category"))
                .collect(Collectors.toList());

        final List<FullDrinkView> drinkList = drinkService.findAllDrinksByCategories(category);

        final List<CategoryView> categoryList = categoryService.findAllCategories();


        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("drinkList", drinkList);


        dataModel.put("categories", categoryList);

        Template template = templateProvider.getTemplate(getServletContext(), "receipeList.ftlh");

        try {
            template.process(dataModel, resp.getWriter());
        } catch (TemplateException e) {
            packageLogger.error(e.getMessage());
        }
    }
}
