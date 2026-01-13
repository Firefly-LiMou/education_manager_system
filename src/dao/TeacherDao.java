package dao;

import entity.Teacher;
import util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 教师数据访问层（TeacherDao）
 * 封装Teacher表的所有数据库操作（增删改查），依赖DBUtil工具类和Teacher实体类
 */
public class TeacherDao {
    /**
     * 新增教师信息
     * @param teacher 教师实体对象（需包含非空字段：tno、tname）
     * @return boolean 新增成功返回true，失败返回false
     */
    public boolean addTeacher(Teacher teacher) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO Teacher (Tno, Tname, Tsex, Ttitle, Tdept) VALUES (?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, teacher.getTno());
            pstmt.setString(2, teacher.getTname());
            pstmt.setString(3, teacher.getTsex());
            pstmt.setString(4, teacher.getTtitle());
            pstmt.setString(5, teacher.getTdept());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("新增教师信息失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(pstmt, conn);
        }
    }

    /**
     * 根据教师编号删除教师信息
     * @param tno 教师编号（主键）
     * @return boolean 删除成功返回true，失败返回false
     */
    public boolean deleteTeacher(String tno) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "DELETE FROM Teacher WHERE Tno=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tno);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("删除教师信息失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(pstmt, conn);
        }
    }

    /**
     * 修改教师信息（根据教师编号更新所有字段）
     * @param teacher 教师实体对象（必须包含tno，其他字段按需修改）
     * @return boolean 修改成功返回true，失败返回false
     */
    public boolean updateTeacher(Teacher teacher) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE Teacher SET Tname=?, Tsex=?, Ttitle=?, Tdept=? WHERE Tno=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, teacher.getTname());
            pstmt.setString(2, teacher.getTsex());
            pstmt.setString(3, teacher.getTtitle());
            pstmt.setString(4, teacher.getTdept());
            pstmt.setString(5, teacher.getTno()); // 主键作为更新条件
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("修改教师信息失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(pstmt, conn);
        }
    }

    /**
     * 根据教师编号查询单个教师信息
     * @param tno 教师编号（主键）
     * @return Teacher 教师实体对象（未查询到返回null）
     */
    public Teacher getTeacherByTno(String tno) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM Teacher WHERE Tno=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tno);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                Teacher teacher = new Teacher();
                teacher.setTno(rs.getString("Tno"));
                teacher.setTname(rs.getString("Tname"));
                teacher.setTsex(rs.getString("Tsex"));
                teacher.setTtitle(rs.getString("Ttitle"));
                teacher.setTdept(rs.getString("Tdept"));
                return teacher;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("查询教师信息失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
    }

    /**
     * 查询所有教师信息
     * @return List<Teacher> 教师列表（无数据返回空列表，不返回null）
     */
    public List<Teacher> getAllTeachers() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Teacher> teacherList = new ArrayList<>();
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM Teacher";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Teacher teacher = new Teacher();
                teacher.setTno(rs.getString("Tno"));
                teacher.setTname(rs.getString("Tname"));
                teacher.setTsex(rs.getString("Tsex"));
                teacher.setTtitle(rs.getString("Ttitle"));
                teacher.setTdept(rs.getString("Tdept"));
                teacherList.add(teacher);
            }
            return teacherList;
        } catch (SQLException e) {
            throw new RuntimeException("查询所有教师信息失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
    }

    /**
     * 高频业务：根据姓名模糊查询教师
     * @param tname 教师姓名（支持模糊匹配）
     * @return List<Teacher> 匹配的教师列表
     */
    public List<Teacher> getTeachersByName(String tname) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Teacher> teacherList = new ArrayList<>();
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM Teacher WHERE Tname LIKE CONCAT('%', ?, '%')";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tname);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Teacher teacher = new Teacher();
                teacher.setTno(rs.getString("Tno"));
                teacher.setTname(rs.getString("Tname"));
                teacher.setTsex(rs.getString("Tsex"));
                teacher.setTtitle(rs.getString("Ttitle"));
                teacher.setTdept(rs.getString("Tdept"));
                teacherList.add(teacher);
            }
            return teacherList;
        } catch (SQLException e) {
            throw new RuntimeException("根据姓名查询教师信息失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
    }
}