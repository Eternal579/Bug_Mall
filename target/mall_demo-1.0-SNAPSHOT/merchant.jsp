<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>商家界面</title>
</head>
<body>
<h1>商家界面</h1>

<h2>商品目录管理</h2>
<button onclick="addProduct()">编辑商品</button>
<button onclick="deleteProduct()">删除商品</button>

<h2>客户浏览/购买日志记录</h2>
<button onclick="viewRecords()">查看浏览记录</button>
<button onclick="viewPurchaseRecords()">查看购买记录</button>
<div id="logs"></div>

<script>
    function addProduct() {
        window.location.href = "addProducts.jsp";
    }

    function deleteProduct() {
        window.location.href = "deleteProduct.jsp";
    }

    function viewRecords() {
        window.location.href = "viewRecords.jsp";
    }

    function viewPurchaseRecords() {
        window.location.href = "viewPurchaseRecords.jsp";
    }
</script>
</body>
</html>