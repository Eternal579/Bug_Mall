package com.example.mall_demo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

@WebServlet("/buyNow")
public class serv_buyNow extends HttpServlet {
    static final String DB_URL = "jdbc:mysql://localhost:3306/bug_mall?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "bugeater";
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("content-type", "text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        System.out.println("buyNow servlet...");
        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("loggedUser");
        if (email == null) {
            // 未登录，重定向到登录页面
            response.sendRedirect("login.jsp");
            return;
        }
        String productId = request.getParameter("productId");
        String quantity = request.getParameter("quantity");
        if(quantity == null){
            quantity = "1";
        }

        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String query = "SELECT * FROM products WHERE id = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, productId);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            int stock = resultSet.getInt("stock");
            if(stock < Integer.parseInt(quantity)){
                // 构建带有跳转链接的HTML响应
                String htmlResponse = "<p>商品目前库存不够！点击<a href=\"index.jsp\">这里</a>跳转到商城首页。</p>";
                response.setContentType("text/html");
                response.getWriter().println(htmlResponse);
                return;
            }
            statement.close();
            conn.close();
        } catch (SQLException e) {
            // 构建带有跳转链接的HTML响应
            String htmlResponse = "<p>商品目前商品不存在！点击<a href=\"index.jsp\">这里</a>跳转到商城首页。</p>";
            response.setContentType("text/html");
            response.getWriter().println(htmlResponse);
            return;
        }

        // 创建或获取用户订单表
        String tableName = getOrCreateUserTable(email);
        // 存储订单信息在会话中
        session.setAttribute("tableName", tableName);
        session.setAttribute("productId", productId);
        session.setAttribute("quantity", quantity);
        // 跳转到确认订单页面
        response.sendRedirect("confirm.jsp");
    }

    // 创建或获取用户数据表
    private String getOrCreateUserTable(String email) {
        int atIndex = email.indexOf("@");
        String username = email.substring(0, atIndex);
        String tableName = "order_" + username;
        // 检查表是否存在，如果不存在则创建表
        if (!isTableExists(tableName)) {
            createTable(tableName);
        }
        return tableName;
    }

    // 检查表是否存在
    private boolean isTableExists(String tableName) {
        boolean tableExists = false;
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String query = "SELECT * FROM " + tableName;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            tableExists = true;
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            // 如果发生异常，说明表不存在
        }
        return tableExists;
    }

    // 创建用户数据表
    private void createTable(String tableName) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            // id在订单表中不能作为主键
            System.out.println(tableName);
            String createTableSql = "CREATE TABLE " + tableName + " (id INT, quantity INT, orderTime DATE) ";
            PreparedStatement stmt = conn.prepareStatement(createTableSql);
            stmt.executeUpdate();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
