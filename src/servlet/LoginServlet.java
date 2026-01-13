package servlet;

import dao.UserDao;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

/**
 * 登录请求处理Servlet
 * 核心功能：接收登录表单参数、调用UserDao验证账号密码、处理多角色跳转、传递错误提示
 * 与login.jsp（前端）、UserDao（数据层）无缝适配
 */
@WebServlet(name = "LoginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {
    // 重写doPost方法（适配login.jsp的POST提交方式）
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. 获取登录表单提交的参数（与login.jsp的name属性一致）
        String userId = request.getParameter("userId").trim(); // 登录账号
        String password = request.getParameter("password").trim(); // 登录密码
        String userTypeStr = request.getParameter("userType"); // 选择的角色（字符串类型）

        // 2. 初始化变量
        UserDao userDao = new UserDao();
        User user = null;
        // 角色类型转换（字符串转Integer，避免类型错误）
        Integer userType = null;
        try {
            userType = Integer.parseInt(userTypeStr);
        } catch (NumberFormatException e) {
            // 角色参数非法（非数字），返回登录页提示
            request.setAttribute("errorMsg", "角色选择异常，请重新选择！");
            request.getRequestDispatcher("/pages/login/login.jsp").forward(request, response);
            return; // 终止后续逻辑
        }

        try {
            // 3. 调用UserDao的登录验证方法（按账号+密码查询用户）
            user = userDao.login(userId, password);

            // 4. 登录验证逻辑处理
            if (user == null) {
                // 场景1：账号或密码错误
                request.setAttribute("errorMsg", "账号或密码错误，请重新输入！");
                request.getRequestDispatcher("/pages/login/login.jsp").forward(request, response);
            } else if (!user.isEnabled()) {
                // 场景2：账号已禁用
                request.setAttribute("errorMsg", "账号已禁用，请联系管理员！");
                request.getRequestDispatcher("/pages/login/login.jsp").forward(request, response);
            } else if (!user.getUserType().equals(userType)) {
                // 场景3：选择的角色与账号实际角色不匹配（防前端篡改）
                String roleName = getRoleName(userType);
                request.setAttribute("errorMsg", "当前账号不属于" + roleName + "角色，请重新选择！");
                request.getRequestDispatcher("/pages/login/login.jsp").forward(request, response);
            } else {
                // 场景4：登录成功
                // 4.1 将用户信息存入Session（用于后续权限验证、页面展示）
                HttpSession session = request.getSession();
                session.setAttribute("loginUser", user);
                // 4.2 按角色跳转对应页面（重定向，避免表单重复提交）
                String contextPath = request.getContextPath(); // 项目根路径
                if (user.isAdmin()) {
                    // 管理员跳后台首页
                    response.sendRedirect(contextPath + "/pages/admin/index.jsp");
                } else if (user.isTeacher()) {
                    // 教师跳教师首页
                    response.sendRedirect(contextPath + "/pages/teacher/index.jsp");
                } else if (user.isStudent()) {
                    // 学生跳学生首页
                    response.sendRedirect(contextPath + "/pages/student/index.jsp");
                }
            }
        } catch (SQLException e) {
            // 场景5：数据库操作异常（如连接失败）
            e.printStackTrace(); // 控制台打印异常，便于调试
            request.setAttribute("errorMsg", "登录失败：数据库连接异常，请稍后重试！");
            request.getRequestDispatcher("/pages/login/login.jsp").forward(request, response);
        }
    }

    // 辅助方法：根据角色类型获取角色名称（用于错误提示）
    private String getRoleName(Integer userType) {
        switch (userType) {
            case 1:
                return "管理员";
            case 2:
                return "教师";
            case 3:
                return "学生";
            default:
                return "未知";
        }
    }

    // 兼容GET请求（防止用户直接访问LoginServlet的GET方式）
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // GET请求直接重定向到登录页
        response.sendRedirect(request.getContextPath() + "/pages/login/login.jsp");
    }
}