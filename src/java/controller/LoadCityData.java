package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Category;
import hibernate.City;
import hibernate.HibernateUtil;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

@WebServlet(name = "LoadCityData", urlPatterns = {"/LoadCityData"})
public class LoadCityData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        //City
        Criteria c = s.createCriteria(City.class);

        List<City> cityList = c.list();
        s.close();

        Gson gson = new Gson();
        responseObject.add("cityList", gson.toJsonTree(cityList));
        responseObject.addProperty("status", true);
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }

}
