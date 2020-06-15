package com.infoshareacademy.servlet;

import com.infoshareacademy.context.ContextHolder;
import com.infoshareacademy.domain.dto.CategoryView;
import com.infoshareacademy.domain.dto.FullDrinkView;
import com.infoshareacademy.freemarker.TemplateProvider;
import com.infoshareacademy.service.CategoryService;
import com.infoshareacademy.service.DrinkService;
import com.infoshareacademy.service.SearchType;
import com.infoshareacademy.service.UserService;
import com.infoshareacademy.service.validator.UserInputValidator;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@WebServlet("/list")
public class DrinkListServlet extends HttpServlet {

    private static final Logger packageLogger = LoggerFactory.getLogger(DrinkListServlet.class.getName());

    @EJB
    private DrinkService drinkService;

    @EJB
    private CategoryService categoryService;

    @Inject
    private TemplateProvider templateProvider;

    @Inject
    private UserInputValidator userInputValidator;

    @Inject
    private UserService userService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html; charset=UTF-8");

        String pageNumberReq = req.getParameter("page");

        int currentPage;

        if (!userInputValidator.validatePageNumber(pageNumberReq)) {
            currentPage = 1;
        } else {
            currentPage = Integer.valueOf(pageNumberReq);
        }

        final List<CategoryView> categories = categoryService.findAllCategories();

        Map<String, Object> dataModel = new HashMap<>();

        ContextHolder contextHolder = new ContextHolder(req.getSession());
        dataModel.put("name", contextHolder.getName());
        dataModel.put("role", contextHolder.getRole());

        String email = contextHolder.getEmail();


        Map<String, String[]> searchParam = req.getParameterMap();

        SearchType searchType = drinkService.checkingSearchingCase(searchParam, currentPage);

        int maxPage = searchType.getMaxPage();

        List<FullDrinkView> drinkViewList = searchType.getDrinkViewList();

        String queryName = searchType.getQueryName();

        if (email != null && !email.isEmpty()){

            List<FullDrinkView> favouritesList = userService.favouritesList(email);

            if (!favouritesList.isEmpty()){
                List<Object>favouritesListModel = favouritesList.stream()
                        .map(FullDrinkView::getId)
                        .map(aLong ->  Integer.parseInt(aLong.toString()))
                        .collect(Collectors.toList());

                dataModel.put("favourites", favouritesListModel);
            }

        }

        String servletPath = req.getServletPath();

        dataModel.put("servletPath",servletPath);
        dataModel.put("categories", categories);
        dataModel.put("maxPageSize", maxPage);
        dataModel.put("queryName", queryName);
        dataModel.put("drinkList", drinkViewList);
        dataModel.put("currentPage", currentPage);



        Template template = templateProvider.getTemplate(getServletContext(), "receipeList.ftlh");

        try {
            template.process(dataModel, resp.getWriter());
        } catch (
                TemplateException e) {
            packageLogger.error(e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String drinkId = req.getParameter("drinkId");

        ContextHolder contextHolder = new ContextHolder(req.getSession());
        String email = contextHolder.getEmail();

        if (email != null && !email.isEmpty()) {

            userService.saveOrDeleteFavourite(email, drinkId);

        }

    }
}
