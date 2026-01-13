package dao;

import entity.User;
import util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户数据访问层（DAO）
 * 去除remark备注属性，核心适配多角色登录验证、用户信息增删改查
 * 与数据库user表（无remark字段）、User实体类无缝适配
 */
public class UserDao {

    /**
     * 登录验证核心方法（按账号+密码查询用户）
     * @param userId 用户账号（管理员ID/教师编号/学生学号）
     * @param password 登录密码
     * @return 匹配的User对象（含用户类型、状态等），无匹配返回null
     * @throws SQLException 数据库操作异常
     */
    public User login(String userId, String password) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        User user = null;

        try {
            conn = DBUtil.getConnection();
            // SQL：按账号+密码查询（登录核心逻辑，无remark字段）
            String sql = "SELECT user_id, password, user_type, real_name, status FROM user WHERE user_id = ? AND password = ?";
            if(conn == null) {
//                System.out.println("aaaaaaaaaa");
            }
            pstmt = conn.prepareStatement(sql);

            // 设置占位符参数：账号、密码
            pstmt.setString(1, userId);
            pstmt.setString(2, password);
            rs = pstmt.executeQuery();
            // 封装匹配的用户信息
            if (rs.next()) {
                user = new User();
                user.setUserId(rs.getString("user_id"));
                user.setPassword(rs.getString("password"));
                user.setUserType(rs.getInt("user_type"));
                user.setRealName(rs.getString("real_name"));
                user.setStatus(rs.getInt("status"));
            }

        } finally {
            DBUtil.close(rs, pstmt, conn);
        }

        return user;
    }

    /**
     * 查询所有用户信息（管理员后台管理用）
     * @return 用户列表（List<User>），无数据返回空列表
     * @throws SQLException 数据库操作异常
     */
    public List<User> queryAllUsers() throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<User> userList = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT user_id, password, user_type, real_name, status FROM user";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getString("user_id"));
                user.setPassword(rs.getString("password"));
                user.setUserType(rs.getInt("user_type"));
                user.setRealName(rs.getString("real_name"));
                user.setStatus(rs.getInt("status"));
                userList.add(user);
            }
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }

        return userList;
    }

    /**
     * 按用户账号查询单个用户信息
     * @param userId 用户账号（主键）
     * @return 匹配的User对象，无匹配返回null
     * @throws SQLException 数据库操作异常
     */
    public User queryUserByUserId(String userId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        User user = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT user_id, password, user_type, real_name, status FROM user WHERE user_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                user = new User();
                user.setUserId(rs.getString("user_id"));
                user.setPassword(rs.getString("password"));
                user.setUserType(rs.getInt("user_type"));
                user.setRealName(rs.getString("real_name"));
                user.setStatus(rs.getInt("status"));
            }
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }

        return user;
    }

    /**
     * 新增用户信息（管理员添加账号用）
     * @param user 待新增的User对象（无remark字段）
     * @return true-新增成功，false-新增失败
     * @throws SQLException 数据库操作异常
     */
    public boolean addUser(User user) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int affectedRows = 0;

        try {
            conn = DBUtil.getConnection();
            // SQL：插入无remark字段的用户信息
            String sql = "INSERT INTO user (user_id, password, user_type, real_name, status) VALUES (?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);

            // 设置占位符参数（与User对象字段一一对应）
            pstmt.setString(1, user.getUserId());
            pstmt.setString(2, user.getPassword());
            pstmt.setInt(3, user.getUserType());
            pstmt.setString(4, user.getRealName());
            pstmt.setInt(5, user.getStatus());

            affectedRows = pstmt.executeUpdate();
        } finally {
            DBUtil.close(pstmt, conn);
        }

        return affectedRows > 0;
    }

    /**
     * 修改用户信息（按账号修改，无remark字段）
     * @param user 待修改的User对象（userId为修改依据）
     * @return true-修改成功，false-修改失败
     * @throws SQLException 数据库操作异常
     */
    public boolean updateUser(User user) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int affectedRows = 0;

        try {
            conn = DBUtil.getConnection();
            // SQL：按账号修改密码、类型、姓名、状态（无remark）
            String sql = "UPDATE user SET password=?, user_type=?, real_name=?, status=? WHERE user_id=?";
            pstmt = conn.prepareStatement(sql);

            // 设置占位符参数（先改字段，最后是WHERE条件的账号）
            pstmt.setString(1, user.getPassword());
            pstmt.setInt(2, user.getUserType());
            pstmt.setString(3, user.getRealName());
            pstmt.setInt(4, user.getStatus());
            pstmt.setString(5, user.getUserId());

            affectedRows = pstmt.executeUpdate();
        } finally {
            DBUtil.close(pstmt, conn);
        }

        return affectedRows > 0;
    }

    /**
     * 删除用户信息（按账号删除）
     * @param userId 用户账号（主键）
     * @return true-删除成功，false-删除失败
     * @throws SQLException 数据库操作异常
     */
    public boolean deleteUser(String userId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int affectedRows = 0;

        try {
            conn = DBUtil.getConnection();
            String sql = "DELETE FROM user WHERE user_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            affectedRows = pstmt.executeUpdate();
        } finally {
            DBUtil.close(pstmt, conn);
        }

        return affectedRows > 0;
    }
}