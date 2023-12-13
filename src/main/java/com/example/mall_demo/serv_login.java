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

@WebServlet("/login")
public class serv_login extends HttpServlet {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/bug_mall?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "bugeater";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("content-type", "text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if(email.equals("root@mall") && password.equals("admin")){
            response.sendRedirect("merchant.jsp");
        }

        boolean isAuthenticated = false;

        try {
            isAuthenticated = authenticateUser(email, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (isAuthenticated) {
            // 创建会话并将登录状态存储在会话中
            HttpSession session = request.getSession();
            session.setAttribute("loggedUser", email);

            response.sendRedirect("index.jsp");
        } else {
            PrintWriter out = response.getWriter();
            out.println("<html><body>");
            out.println("<h2>" + "用户尚未注册" + "</h2>");
            out.println("</body></html>");
        }
    }


//    private String authenticateUser(String email, String password) throws ClassNotFoundException {
//        // 注册 JDBC 驱动
//        Class.forName(JDBC_DRIVER);
//        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
//             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE email = ? AND password = ?")) {
//            statement.setString(1, email);
//            statement.setString(2, password);
//            ResultSet resultSet = statement.executeQuery();
//            if (resultSet.next()) {
//                return "success";
//            } else {
//                return "账户不存在或密码错误！";
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return "数据库连接错误：" + e.getMessage();
//        }
//    }

    private boolean authenticateUser(String email, String password) throws ClassNotFoundException {
        // 注册 JDBC 驱动
        Class.forName(JDBC_DRIVER);
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE email = ? AND password = ?")) {
            statement.setString(1, email);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}