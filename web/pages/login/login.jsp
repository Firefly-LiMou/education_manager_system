<%--
  Created by IntelliJ IDEA.
  User: 84813
  Date: 2026/1/12
  Time: 下午3:19
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>教务管理系统 - 登录</title>
    <!-- 引入登录页样式（放在static/css目录下） -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/login.css">
</head>
<body>
<!-- 登录容器：居中布局 -->
<div class="login-container">
    <!-- 登录标题 -->
    <div class="login-title">
        <h2>教务管理系统登录</h2>
    </div>

    <!-- 错误提示区域（登录失败时显示） -->
    <%
        String errorMsg = (String) request.getAttribute("errorMsg");
        if (errorMsg != null && !errorMsg.isEmpty()) {
    %>
    <div class="error-box">
        <span><i>⚠</i> <%= errorMsg %></span>
    </div>
    <%
        }
    %>

    <!-- 登录表单：提交到LoginServlet，POST方式（安全） -->
    <form action="${pageContext.request.contextPath}/login" method="post" onsubmit="return checkForm()">
        <!-- 账号输入框 -->
        <div class="form-item">
            <label for="userId">登录账号：</label>
            <input type="text" id="userId" name="userId" placeholder="请输入管理员ID/教师编号/学生学号"
                   value="<%= request.getParameter("userId") != null ? request.getParameter("userId") : "" %>">
        </div>

        <!-- 密码输入框 -->
        <div class="form-item">
            <label for="password">登录密码：</label>
            <input type="password" id="password" name="password" placeholder="请输入登录密码">
        </div>

        <!-- 角色选择下拉框 -->
        <div class="form-item">
            <label for="userType">用户角色：</label>
            <select id="userType" name="userType">
                <option value="1">管理员</option>
                <option value="2">教师</option>
                <option value="3">学生</option>
            </select>
        </div>

        <div class="form-btn-group">
            <!-- 登录按钮 -->
            <button type="submit" class="submit-btn">登录</button>
            <!-- 注册按钮，链接到注册页面 -->
            <a href="${pageContext.request.contextPath}/pages/login/register.jsp" class="submit-btn">注册</a>
        </div>
    </form>
</div>

<!-- 前端表单验证JS -->
<script>
    /**
     * 表单提交前验证：账号/密码不能为空
     * @returns true-验证通过，false-验证失败
     */
    function checkForm() {
        // 获取输入框值
        const userId = document.getElementById("userId").value.trim();
        const password = document.getElementById("password").value.trim();

        // 验证账号
        if (userId === "") {
            alert("请输入登录账号！");
            document.getElementById("userId").focus();
            return false;
        }

        // 验证密码
        if (password === "") {
            alert("请输入登录密码！");
            document.getElementById("password").focus();
            return false;
        }

        // 验证通过，提交表单
        return true;
    }
</script>
</body>
</html>