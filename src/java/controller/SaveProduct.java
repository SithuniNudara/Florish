package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Category;
import hibernate.HibernateUtil;
import hibernate.Product;
import hibernate.ProductStatus;
import hibernate.User;
import hibernate.Volume;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

@MultipartConfig
@WebServlet(name = "SaveProduct", urlPatterns = {"/SaveProduct"})
public class SaveProduct extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session s = sf.openSession();

        String title = request.getParameter("title");
        String category = request.getParameter("category");
        String volume = request.getParameter("volume");
        String description = request.getParameter("description");
        String price = request.getParameter("price");
        String qty = request.getParameter("qty");
        String status = request.getParameter("status");

        //image Uploading
        Part part1 = request.getPart("img1");
        Part part2 = request.getPart("img2");
        Part part3 = request.getPart("img3");

        System.out.println(title);
        System.out.println(category);
        System.out.println(volume);
        System.out.println(description);
        System.out.println(price);
        System.out.println(qty);
        System.out.println(status);

        if (request.getSession().getAttribute("user") == null) {
            responseObject.addProperty("message", "Please Sign In!");

        } else if (title.isEmpty()) {
            responseObject.addProperty("message", "Please Enter Product Title");

        } else if (!Util.isInteger(category)) {
            responseObject.addProperty("message", "Invalied Model");

        } else if (Integer.parseInt(category) == 0) {
            responseObject.addProperty("message", "Please Select Cateogry");

        } else if (!Util.isInteger(volume)) {
            responseObject.addProperty("message", "Invalid Volume");

        } else if (Integer.parseInt(volume) == 0) {
            responseObject.addProperty("message", "Please Select Volume");

        } else if (description.isEmpty()) {
            responseObject.addProperty("message", "Please Enter Product Title");

        } else if (!Util.isDouble(price)) {
            responseObject.addProperty("message", "Price should be Floating point number");

        } else if (price.isEmpty()) {
            responseObject.addProperty("message", "Please Enter Price");

        } else if (!Util.isInteger(qty)) {
            responseObject.addProperty("message", "Please Enter Valid Quantity");

        } else if (qty.isEmpty()) {
            responseObject.addProperty("message", "Please Enter Product Quantity");

        } else if (qty.isEmpty()) {
            responseObject.addProperty("message", "Please Enter Product Quantity");

        } else if (!Util.isInteger(status)) {
            responseObject.addProperty("message", "Please Select Valid Status");

        } else if (status.isEmpty()) {
            responseObject.addProperty("message", "Please Select Product Status");

        } else if (part1.getSubmittedFileName() == null) {
            responseObject.addProperty("message", "3 Images Should Added");

        } else if (part2.getSubmittedFileName() == null) {
            responseObject.addProperty("message", "3 Images Should Added");

        } else if (part3.getSubmittedFileName() == null) {
            responseObject.addProperty("message", "3 Images Should Added");

        } else {
            //

            Criteria c1 = s.createCriteria(Product.class);
            c1.add(Restrictions.eq("title", title));
            c1.add(Restrictions.eq("category.id", Integer.parseInt(category)));
            c1.add(Restrictions.eq("volumn.id", Integer.parseInt(volume)));

            if (!c1.list().isEmpty()) {
                responseObject.addProperty("message", "This Product is Already Added!");
            } else {

                Category categoryname = (Category) s.get(Category.class, Integer.valueOf(category));

                if (categoryname == null) {
                    responseObject.addProperty("message", "Please Select A Valid Category");
                } else {
                    Volume volumename = (Volume) s.get(Volume.class, Integer.valueOf(volume));

                    if (volumename == null) {
                        responseObject.addProperty("message", "Please Select A Valid Volume");
                    } else {
                        ProductStatus productStatusname = (ProductStatus) s.get(ProductStatus.class, Integer.valueOf(status));

                        if (productStatusname == null) {
                            responseObject.addProperty("message", "Please Select A Valid Status");
                        } else {
                            Category categoryid = (Category) s.load(Category.class, Integer.parseInt(category));
                            Volume volumeId = (Volume) s.load(Volume.class, Integer.parseInt(volume));
                            ProductStatus productStatusid = (ProductStatus) s.load(ProductStatus.class, Integer.parseInt(status));

                            User user = (User) request.getSession().getAttribute("user");

                            Product p = new Product();
                            p.setTitle(title);
                            p.setDescription(description);
                            p.setCategory(categoryid);
                            p.setVolumn(volumeId);
                            p.setPrice(Double.parseDouble(price));
                            p.setProduct_status(productStatusid);
                            p.setQty(Integer.parseInt(qty));

                            int id = (int) s.save(p);
                            s.beginTransaction().commit();
                            s.close();

                            String appPath = getServletContext().getRealPath("");
                            System.out.println(appPath);

                            String newPath = appPath.replace("build" + File.separator + "web", "web" + File.separator + "product-images");
                            System.out.println(newPath);

                            File productFolder = new File(newPath, String.valueOf(id));
                            productFolder.mkdir();
                            System.out.println(productFolder);

                         //    Save image1 if not null
                            if (part1 != null && part1.getSize() > 0) {
                                File file1 = new File(productFolder, "image1.png");
                                Files.copy(part1.getInputStream(), file1.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                System.out.println("Image 1 uploaded successfully!");
                            } else {
                                System.out.println("Image 1 is empty or not uploaded.");
                            }

                            // Save image2 if not null
                            if (part2 != null && part2.getSize() > 0) {
                                File file2 = new File(productFolder, "image2.png");
                                Files.copy(part2.getInputStream(), file2.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                System.out.println("Image 2 uploaded successfully!");
                            } else {
                                System.out.println("Image 2 is empty or not uploaded.");
                            }

                            // Save image3 if not null
                            if (part3 != null && part3.getSize() > 0) {
                                File file3 = new File(productFolder, "image3.png");
                                Files.copy(part3.getInputStream(), file3.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                System.out.println("Image 3 uploaded successfully!");
                            } else {
                                System.out.println("Image 3 is empty or not uploaded.");
                            }

                            //  System.out.println(appPath);
                            responseObject.addProperty("status", true);
                            responseObject.addProperty("message", "Success");
                        }

                    }
                }

            }

            //send
            Gson gson = new Gson();
            String tojson = gson.toJson(responseObject);
            response.setContentType("application/json");
            response.getWriter().write(tojson);
        }
    }
}
