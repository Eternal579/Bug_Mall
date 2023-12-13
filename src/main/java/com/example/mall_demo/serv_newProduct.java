package com.example.mall_demo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import javax.servlet.annotation.MultipartConfig;

@MultipartConfig
@WebServlet("/addPros")
public class serv_newProduct extends HttpServlet {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/bug_mall?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "bugeater";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("content-type", "text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        // 从请求参数中获取商品信息
        Part imagePart = request.getPart("image");
        String name = request.getParameter("name");
        double price = Double.parseDouble(request.getParameter("price"));
        int stock = Integer.parseInt(request.getParameter("stock"));
        String description = request.getParameter("description");

        // 处理图片数据
        InputStream imageInputStream = imagePart.getInputStream();

        // 将图片数据转换为字节数组
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = imageInputStream.read(buffer)) != -1) {
            byteStream.write(buffer, 0, bytesRead);
        }
        byte[] imageData = byteStream.toByteArray();

        // 注册 JDBC 驱动
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String query = "SELECT * FROM products";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            try {
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                String createTableSql = "CREATE TABLE products (\n" +
                        "  id INT PRIMARY KEY AUTO_INCREMENT,\n" +
                        "  name VARCHAR(255) NOT NULL,\n" +
                        "  price DECIMAL(10, 2) NOT NULL,\n" +
                        "  stock INT NOT NULL,\n" +
                        "  description VARCHAR(255) NOT NULL,\n" +
                        "  image LONGBLOB\n" +
                        "); ";
                PreparedStatement stmt = conn.prepareStatement(createTableSql);
                stmt.executeUpdate();
                stmt.close();
                conn.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
                // 返回错误响应
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().println("还是数据库的问题");
                return;
            }
        }

        // 将商品信息和图片数据保存到数据库
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            String sql = "SELECT * FROM products WHERE name = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                String sql2 = "INSERT INTO products (name, price, stock, description, image) VALUES (?, ?, ?, ?, ?)";
                statement = connection.prepareStatement(sql2);
                statement.setString(1, name);
                statement.setDouble(2, price);
                statement.setInt(3, stock);
                statement.setString(4, description);
                statement.setBytes(5, imageData);
                statement.executeUpdate();
            } else {
                String sql3 = "UPDATE products SET price = ?, stock = ?, description = ?, image = ? WHERE name = ?";
                statement = connection.prepareStatement(sql3);
                statement.setDouble(1, price);
                statement.setInt(2, stock);
                statement.setString(3, description);
                statement.setBytes(4, imageData);
                statement.setString(5, name);
                statement.executeUpdate();
            }

            statement.close();
            connection.close();

            // 构建带有跳转链接的HTML响应
            String htmlResponse = "<p>商品保存成功！点击<a href=\"index.jsp\">这里</a>跳转到商城首页。</p>";
            response.setContentType("text/html");
            response.getWriter().println(htmlResponse);

        } catch (SQLException e) {
            e.printStackTrace();
            // 返回错误响应
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("保存商品时发生错误");
        }
    }
}