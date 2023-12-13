<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>删除商品</title>
    <link rel="stylesheet" href="./styles/css_index.css">
</head>
<body>
<h2>删除商品</h2>
<%--从数据库中读取商品展示--%>
<div id="choseDelete"></div>
<script>
    // 使用Fetch API获取从Servlet返回的商品信息
    fetch('choseDelete')
        .then(response => response.text())
        .then(data => {
            document.getElementById('choseDelete').innerHTML = data;
        });
</script>
</body>
</html>
