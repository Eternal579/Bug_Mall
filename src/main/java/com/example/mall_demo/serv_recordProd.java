package com.example.mall_demo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import javax.servlet.annotation.MultipartConfig;

@MultipartConfig
@WebServlet("/recordProd")
public class serv_recordProd extends HttpServlet {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/bug_mall?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "bugeater";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("content-type", "text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        // 从请求参数中获取商品信息
        String productId = request.getParameter("id");
        System.out.println("productId: " + productId);

        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("loggedUser");
        if (email == null) {
            // 未登录，重定向到登录页面
            response.sendRedirect("login.jsp");
            return;
        }

        System.out.println("serv_recordProd start!");
        String tableName = getOrCreateTable();
        addToTable(tableName, productId);
        response.sendRedirect("index.jsp");
    }

    // 创建或获取用户数据表
    private String getOrCreateTable() {
        String tableName = "viewRecords";
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
            System.out.println("##################################");
            String createTableSql = "CREATE TABLE " + tableName + " (id VARCHAR(255) PRIMARY KEY, times INT)";
            PreparedStatement stmt = conn.prepareStatement(createTableSql);
            stmt.executeUpdate();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addToTable(String tableName, String productId) {
        try {
            System.out.println("addToTable start!");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            String selectQuantitySql = "SELECT times FROM " + tableName + " WHERE id = ?";
            PreparedStatement selectStmt = conn.prepareStatement(selectQuantitySql);
            selectStmt.setString(1, productId);
            ResultSet resultSet = selectStmt.executeQuery();

            int times = 0;
            if (resultSet.next()) {
                times = resultSet.getInt("times");
                times++;

                String updateQuantitySql = "UPDATE " + tableName + " SET times = ? WHERE id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateQuantitySql);
                updateStmt.setInt(1, times);
                updateStmt.setString(2, productId);
                updateStmt.executeUpdate();
                updateStmt.close();
            } else {
                String insertDataSql = "INSERT INTO " + tableName + " (id, times) VALUES (?, 1)";
                PreparedStatement insertStmt = conn.prepareStatement(insertDataSql);
                insertStmt.setString(1, productId);
                insertStmt.executeUpdate();
                insertStmt.close();
            }

            resultSet.close();
            selectStmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}