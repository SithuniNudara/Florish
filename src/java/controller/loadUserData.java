package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.User;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "loadUserData", urlPatterns = {"/loadUserData"})
public class loadUserData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        HttpSession session = request.getSession(false);

        User user = (User) session.getAttribute("user");
        String fname = user.getFirstName();
        String email = user.getEmail();

        String filePath = "profile-images\\"+email+"\\image1.png";
        
        responseObject.addProperty("file",filePath);
        responseObject.addProperty("ProfileName", fname);
        
        response.setContentType("application/json");
        responseObject.addProperty("status", true);
        response.getWriter().write(responseObject.toString());
    }

}
