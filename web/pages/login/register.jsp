<%--
  Created by IntelliJ IDEA.
  User: 84813
  Date: 2026/1/14
  Time: 下午7:29
  To change this template use File | Settings | File Templates.
--%>
<!-- register.jsp 核心表单 -->
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>用户注册</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/login.css">
</head>
<body>
<div class="login-container">
    <h2>用户注册</h2>
    <form action="${pageContext.request.contextPath}/register" method="post">
        <div class="form-item">
            <label for="reg-username">用户名：</label>
            <input type="text" id="reg-username" name="username" required>
        </div>
        <div class="form-item">
            <label for="reg-password">密码：</label>
            <input type="password" id="reg-password" name="password" required>
        </div>
        <div class="form-item">
            <label for="reg-repassword">确认密码：</label>
            <input type="password" id="reg-repassword" name="repassword" required>
        </div>
        <!-- 角色选择下拉框 -->
        <div class="form-item">
            <label for="userType">用户角色：</label>
            <select id="userType" name="userType">
                <option value="admin">管理员</option>
                <option value="teacher">教师</option>
                <option value="student">学生</option>
            </select>
        </div>
        <div class="form-btn-group">
            <button type="submit" class="submit-btn">注册</button>
            <a href="${pageContext.request.contextPath}/pages/login/login.jsp" class="submit-btn">返回登录</a>
        </div>
    </form>
</div>
</body>
</html>
