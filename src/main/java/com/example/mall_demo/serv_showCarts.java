package com.example.mall_demo;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Base64;

@WebServlet("/showCarts")
public class serv_showCarts extends HttpServlet {
    static final String DB_URL = "jdbc:mysql://localhost:3306/bug_mall?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "bugeater";
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("content-type", "text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        String ori_Email = (String) request.getSession().getAttribute("loggedUser");
        String hash_Email = serv_addToCart.hashEmail(ori_Email);

        boolean tableExists = false;
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String query = "SELECT * FROM " + hash_Email;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            tableExists = true;
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            // 如果发生异常，说明表不存在
        }
        if(tableExists){
            try {
                Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                String sql = "SELECT * FROM " + hash_Email;
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery();

                StringBuilder htmlBuilder = new StringBuilder();

                while (resultSet.next()) {
                    int prod_id = resultSet.getInt("id");
                    int quantity = resultSet.getInt("quantity");
                    try{
                        sql = "SELECT * FROM products WHERE id = ?";
                        statement = connection.prepareStatement(sql);
                        statement.setInt(1, prod_id);
                        ResultSet prod_info = statement.executeQuery();
                        prod_info.next();

                        String name = prod_info.getString("name");
                        double price = prod_info.getDouble("price");
                        byte[] imageBytes = prod_info.getBytes("image");
                        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

                        htmlBuilder.append("<div class=\"product\">");
                        htmlBuilder.append("<img src=\"data:image/png;base64,").append(base64Image).append("\" alt=\"").append(name).append("\">");
                        htmlBuilder.append("<h3>").append(name).append("</h3>");
                        htmlBuilder.append("<p>价格: $").append(price).append("</p>");
                        htmlBuilder.append("<p>数量: ").append(quantity).append("</p>");
                        htmlBuilder.append("<form action=\"usrIncre\" method=\"post\">");
                        htmlBuilder.append("<input type=\"hidden\" name=\"productId\" value=\"").append(prod_id).append("\">");
                        htmlBuilder.append("<button type=\"submit\">增加</button>");
                        htmlBuilder.append("</form>");
                        htmlBuilder.append("<form action=\"usrDecre\" method=\"post\">");
                        htmlBuilder.append("<input type=\"hidden\" name=\"productId\" value=\"").append(prod_id).append("\">");
                        htmlBuilder.append("<button type=\"submit\">减少</button>");
                        htmlBuilder.append("</form>");
                        htmlBuilder.append("<form action=\"buyNow\" method=\"post\">");
                        htmlBuilder.append("<input type=\"hidden\" name=\"productId\" value=\"").append(prod_id).append("\">");
                        htmlBuilder.append("<input type=\"hidden\" name=\"quantity\" value=\"").append(quantity).append("\">");
                        htmlBuilder.append("<button id=\"buyButton\" type=\"submit\">立即购买</button>");
                        htmlBuilder.append("</form>");
                        htmlBuilder.append("</div>");
                    }catch (SQLException e1) {
                    }
                }

                statement.close();
                connection.close();

                // 将生成的HTML片段写入响应
                PrintWriter out = response.getWriter();
                out.println(htmlBuilder);
            } catch (SQLException e) {
                e.printStackTrace();
                // 返回错误响应
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().println("从数据库中检索商品时发生错误");
            }
        }
    }
}