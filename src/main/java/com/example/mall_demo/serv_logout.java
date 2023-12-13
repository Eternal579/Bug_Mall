package com.example.mall_demo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sound.midi.SysexMessage;
import java.io.IOException;

@WebServlet("/logout")
public class serv_logout extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 将 loggedUser 属性设置为 null
        request.getSession().setAttribute("loggedUser", null);
        // 重定向到 index.jsp 页面
        response.sendRedirect("index.jsp");
    }
}