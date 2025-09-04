package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.User;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

@MultipartConfig
@WebServlet(name = "UpdateProfilePicture", urlPatterns = {"/UpdateProfilePicture"})
public class UpdateProfilePicture extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        Part image = request.getPart("profilePicture");

        HttpSession sess = request.getSession(false);

        User user = (User) sess.getAttribute("user");
        String fname = user.getFirstName();
        String email = user.getEmail();
        

        String appPath = getServletContext().getRealPath("");
        System.out.println(appPath);

        String newPath = appPath.replace("build" + File.separator + "web", "web" + File.separator + "profile-images");
        System.out.println(newPath);

        File productFolder = new File(newPath, String.valueOf(email));
        productFolder.mkdir();

        // Save image1 if not null
        if (image != null && image.getSize() > 0) {
            File file1 = new File(productFolder, "image1.png");
            Files.copy(image.getInputStream(), file1.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Image 1 uploaded successfully!");
            responseObject.addProperty("ProfileName", fname);
            responseObject.addProperty("status", true);
        } else {
            System.out.println("Image 1 is empty or not uploaded.");
        }

        String Json = responseObject.toString();
        response.setContentType("application/json");
        response.getWriter().write(Json);

    }

}
