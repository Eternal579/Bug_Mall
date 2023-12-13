<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>订单界面</title>
    <link rel="stylesheet" href="./styles/css_index.css">
</head>
<body>
<h2>订单界面</h2>
<div id="showOrders"></div>
<script>
    // 使用Fetch API获取从Servlet返回的商品信息
    fetch('showOrders')
        .then(response => response.text())
        .then(data => {
            document.getElementById('showOrders').innerHTML = data;
        });
</script>
</body>
</html>
