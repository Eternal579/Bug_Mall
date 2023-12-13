<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Bug_Mall首页</title>
    <link rel="stylesheet" href="./styles/css_index.css">
</head>
<body>
<nav>
    <%-- 根据登录状态显示不同的按钮 --%>
    <% if (session.getAttribute("loggedUser") != null) { %>
    <form action = "logout" method = "post">
        <button type = "submit">注销</button>
    </form>
    <button id="cartButton">购物车</button>
    <button id="orderButton">订单</button>
    <% } else { %>
    <button id="loginButton">登录</button>
    <button id="registerButton">注册</button>
    <% } %>
</nav>

<h2>Bug_Mall首页</h2>

<div style="display: flex; justify-content: center; align-items: center; height: 10vh;">
    <form id="searchForm" action="searchResults" method="GET">
        <div>
            <input id="searchInput" name="search" type="text" style="padding: 10px; width: 300px; border: 1px solid #ccc; border-radius: 4px; font-size: 16px;">
            <button type="submit" style="padding: 10px 20px; background-color: #4CAF50; color: white; border: none; border-radius: 4px; font-size: 16px; cursor: pointer;">搜索</button>
        </div>
    </form>
</div>

<%--从数据库中读取商品展示--%>
<div id="showProducts"></div>
<script>
    // 使用Fetch API获取从Servlet返回的商品信息
    fetch('showProds')
        .then(response => response.text())
        .then(data => {
            document.getElementById('showProducts').innerHTML = data;
        });
</script>

<!-- 处理商品详情展开/隐藏 -->
<script>
    // 用于展示商品详情
    function toggleText(id, description) {
        var textArea = document.getElementById("textArea" + id);
        if (textArea.style.display === "none") {
            textArea.style.display = "block";
            textArea.innerHTML = "<p>" + description + "</p>";
        } else {
            textArea.style.display = "none";
        }
        // 创建XMLHttpRequest对象
        var xhr = new XMLHttpRequest();
        // 设置回调函数
        xhr.onreadystatechange = function() {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                if (xhr.status === 200) {
                    // 请求成功
                    var response = xhr.responseText;
                    // 处理响应数据
                    // ...
                } else {
                    // 请求失败
                    console.error("AJAX request failed.");
                }
            }
        };
        // 构建请求URL
        var url = "recordProd";
        // 设置请求方法和URL
        xhr.open("POST", url, true);
        xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        // 构建请求数据
        console.log(id);
        var data = "id=" + encodeURIComponent(id);
        console.log(encodeURIComponent(id));
        // 发送请求
        xhr.send(data);
    }
    var buyButton = document.querySelector('#buyButton');
    buyButton.addEventListener('click', function() {
        var currentTime = Date.now();
        var timestampInput = document.querySelector('#timestampInput');
        timestampInput.value = currentTime;
    });
</script>

<%-- 根据登录状态为按钮添加监听 --%>
<% if (session.getAttribute("loggedUser") != null) { %>
<script>
    var cartButton = document.querySelector('#cartButton');
    var orderButton = document.querySelector('#orderButton');
    cartButton.addEventListener('click', function() {
        window.location.href = 'cart.jsp'; // 跳转到购物车页面
    });

    orderButton.addEventListener('click', function() {
        window.location.href = 'order.jsp'; // 跳转到订单页面
    });
</script>
<% } else { %>
<script>
    var loginButton = document.querySelector('#loginButton');
    var registerButton = document.querySelector('#registerButton');
    loginButton.addEventListener('click', function() {
        window.location.href = 'login.jsp'; // 跳转到登录页面
    });
    registerButton.addEventListener('click', function() {
        window.location.href = 'register.jsp'; // 跳转到注册页面
    });
</script>
<% } %>

</body>
</html>