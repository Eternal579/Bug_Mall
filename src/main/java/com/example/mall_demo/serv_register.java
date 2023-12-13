package com.example.mall_demo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/register")
public class serv_register extends HttpServlet {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/bug_mall?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "bugeater";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("content-type", "text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        boolean isRegistered = false;
        try {
            isRegistered = checkIfEmailRegistered(email);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (isRegistered) {
            PrintWriter out = response.getWriter();
            out.println("<html><body>");
            out.println("<h2>该邮箱已被注册，请使用其他邮箱！</h2>");
            out.println("</body></html>");
        } else {
            saveUser(email, password);
            PrintWriter out = response.getWriter();
            // 创建会话并将登录状态存储在会话中
            HttpSession session = request.getSession();
            session.setAttribute("loggedUser", email);
            out.println("<html><body>");
            out.println("<h2>注册成功！</h2>");
            out.println("<a href=\"index.jsp\">返回首页</a>");
            out.println("</body></html>");
        }
    }

    private boolean checkIfEmailRegistered(String email) throws ClassNotFoundException {
        // 注册 JDBC 驱动
        Class.forName(JDBC_DRIVER);
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE email = ?")) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void saveUser(String email, String password) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO users (email, password) VALUES (?, ?)")) {
            statement.setString(1, email);
            statement.setString(2, password);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
