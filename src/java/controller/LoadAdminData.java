package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.User;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "LoadAdminData", urlPatterns = {"/LoadAdminData"})
public class LoadAdminData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        
        HttpSession sess =  request.getSession(false);
        
        User user  = (User) sess.getAttribute("user");
        String fname = user.getFirstName();
        String lname = user.getLastName();
        String email = user.getEmail();
        
        responseObject.addProperty("fname", fname);
        responseObject.addProperty("lname", lname);
        responseObject.addProperty("email", email);

        response.setContentType("application/json");
        responseObject.addProperty("status", true);
        response.getWriter().write(responseObject.toString());
    }

}
