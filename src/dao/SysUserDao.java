package dao;

import entity.SysUser;
import util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 系统用户数据访问层（SysUserDao）
 * 封装SysUser表的所有数据库操作（增删改查），依赖DBUtil工具类和SysUser实体类
 * 核心适配场景：登录验证（按账户查询）、按角色管理用户、账户唯一性约束、密码加密存储
 */
public class SysUserDao {
    /**
     * 用户注册（核心：加密原始密码）
     */
    public boolean register(SysUser sysUser, String rawPassword) {
        sysUser.setPassword(rawPassword);
        return this.addSysUser(sysUser);
    }

    /**
     * 登录验证（核心：加密输入密码，对比数据库加密密码）
     */
    public SysUser login(String account, String rawPassword) {
        SysUser sysUser = this.getSysUserByAccount(account);
        if (sysUser == null) {
            return null; // 账户不存在
        }

        return rawPassword.equals(sysUser.getPassword()) ? sysUser : null;
    }


    /**
     * 新增系统用户
     * @param sysUser 系统用户实体对象（需包含非空字段：userId、account、password、role）
     * @return boolean 新增成功返回true，失败返回false
     * 注：account唯一约束由数据库保证，重复账户会抛出异常
     */
    public boolean addSysUser(SysUser sysUser) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO SysUser (UserID, Account, Password, Role, RelID, CreateTime, Status) VALUES (?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, sysUser.getUserId());
            pstmt.setString(2, sysUser.getAccount());
            pstmt.setString(3, sysUser.getPassword());
            pstmt.setString(4, sysUser.getRole() != null ? sysUser.getRole() : "");
            pstmt.setString(5, sysUser.getRelId());
            // 若创建时间为空，设为当前时间
            pstmt.setDate(6, sysUser.getCreateTime() != null ? new java.sql.Date(sysUser.getCreateTime().getTime()) : new java.sql.Date(new Date().getTime()));
            // 若状态为空，设为1（启用）
            pstmt.setInt(7, sysUser.getStatus() != null ? sysUser.getStatus() : 1);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            if (e.getMessage().contains("uk_sysuser_account")) {
                throw new RuntimeException("新增用户失败：登录账户已存在！", e);
            }
            throw new RuntimeException("新增系统用户失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(pstmt, conn);
        }
    }

    /**
     * 根据用户ID删除系统用户
     * @param userId 系统用户ID（主键）
     * @return boolean 删除成功返回true，失败返回false
     */
    public boolean deleteSysUser(String userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "DELETE FROM SysUser WHERE UserID=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("删除系统用户失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(pstmt, conn);
        }
    }

    /**
     * 修改系统用户信息（根据用户ID更新）
     * @param sysUser 系统用户实体对象（必须包含userId，其他字段按需修改）
     * @return boolean 修改成功返回true，失败返回false
     * 注：账户（Account）建议不允许修改，避免唯一性冲突
     */
    public boolean updateSysUser(SysUser sysUser) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            // SQL：更新密码、角色、关联ID、状态，不更新账户和创建时间（账户唯一，创建时间不可改）
            String sql = "UPDATE SysUser SET Password=?, Role=?, RelID=?, Status=? WHERE UserID=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, sysUser.getPassword()); // 密码需加密后传入
            pstmt.setString(2, sysUser.getRole());
            pstmt.setString(3, sysUser.getRelId());
            pstmt.setInt(4, sysUser.getStatus() != null ? sysUser.getStatus() : 1);
            pstmt.setString(5, sysUser.getAccount());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("修改系统用户失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(pstmt, conn);
        }
    }

    /**
     * 根据用户ID查询单个系统用户
     * @param userId 系统用户ID（主键）
     * @return SysUser 系统用户实体对象（未查询到返回null）
     */
    public SysUser getSysUserByUserId(String userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM SysUser WHERE UserID=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return wrapSysUserFromResultSet(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("查询系统用户失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
    }

    /**
     * 登录核心方法：根据账户查询系统用户（验证登录）
     * @param account 登录账户（唯一）
     * @return SysUser 系统用户实体对象（未查询到返回null）
     */
    public SysUser getSysUserByAccount(String account) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM SysUser WHERE Account=? AND Status=1"; // 仅查询启用状态的用户
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, account);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return wrapSysUserFromResultSet(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("验证登录账户失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
    }

    /**
     * 按角色查询系统用户（如查询所有学生用户/教师用户/管理员）
     * @param role 角色（student/teacher/admin）
     * @return List<SysUser> 该角色的用户列表（无数据返回空列表）
     */
    public List<SysUser> getSysUsersByRole(String role) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<SysUser> sysUserList = new ArrayList<>();
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM SysUser WHERE Role=? AND Status=1"; // 仅查询启用状态
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, role);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                sysUserList.add(wrapSysUserFromResultSet(rs));
            }
            return sysUserList;
        } catch (SQLException e) {
            throw new RuntimeException("按角色查询用户失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
    }

    /**
     * 查询所有系统用户（含启用/禁用状态）
     * @return List<SysUser> 系统用户列表（无数据返回空列表）
     */
    public List<SysUser> getAllSysUsers() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<SysUser> sysUserList = new ArrayList<>();
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM SysUser";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                sysUserList.add(wrapSysUserFromResultSet(rs));
            }
            return sysUserList;
        } catch (SQLException e) {
            throw new RuntimeException("查询所有系统用户失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
    }

    /**
     * 工具方法：将ResultSet封装为SysUser对象（复用代码，减少冗余）
     * @param rs 结果集
     * @return SysUser 系统用户对象
     * @throws SQLException 数据库异常
     */
    private SysUser wrapSysUserFromResultSet(ResultSet rs) throws SQLException {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(rs.getInt("UserID"));
        sysUser.setAccount(rs.getString("Account"));
        sysUser.setPassword(rs.getString("Password")); // 注意：返回的是加密后的密码
        sysUser.setRole(rs.getString("Role"));
        sysUser.setRelId(rs.getString("RelID"));
        sysUser.setCreateTime(rs.getDate("CreateTime"));
        sysUser.setStatus(rs.getInt("Status"));
        return sysUser;
    }
}