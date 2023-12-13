package com.example.mall_demo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Base64;

@WebServlet("/choseDelete")
public class serv_choseDelete extends HttpServlet {
    // MySQL 8.0 以上版本 - JDBC 驱动名及数据库 URL
    static final String DB_URL = "jdbc:mysql://localhost:3306/bug_mall?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "bugeater";
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("content-type", "text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String sql = "SELECT * FROM products";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            StringBuilder htmlBuilder = new StringBuilder();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                double price = resultSet.getDouble("price");
                int stock = resultSet.getInt("stock");
                byte[] imageBytes = resultSet.getBytes("image");

                String base64Image = Base64.getEncoder().encodeToString(imageBytes);

                htmlBuilder.append("<div class=\"product\">");
                htmlBuilder.append("<img src=\"data:image/png;base64,").append(base64Image).append("\" alt=\"").append(name).append("\">");
                htmlBuilder.append("<h3>").append(name).append("</h3>");
                htmlBuilder.append("<p>价格: $").append(price).append("</p>");
                htmlBuilder.append("<p>库存量: ").append(stock).append("</p>");
                htmlBuilder.append("<form action=\"deleteProduct\" method=\"post\">");
                htmlBuilder.append("<input type=\"hidden\" name=\"prodId\" value=\"").append(id).append("\">");
                htmlBuilder.append("<button type=\"submit\">删除商品</button>");
                htmlBuilder.append("</form>");
                htmlBuilder.append("</div>");
            }
            statement.close();
            connection.close();

            // 将生成的HTML片段写入响应
            PrintWriter out = response.getWriter();
            out.println(htmlBuilder.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("目前没有商品");
        }
    }
}