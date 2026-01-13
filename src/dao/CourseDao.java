package dao;

import entity.Course;
import util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 课程数据访问层（CourseDao）
 * 封装Course表的所有数据库操作（增删改查），依赖DBUtil工具类和Course实体类（已去掉学期字段）
 */
public class CourseDao {
    /**
     * 新增课程信息
     * @param course 课程实体对象（需包含非空字段：cno、cname、ccredit，tno可为空）
     * @return boolean 新增成功返回true，失败返回false
     * 注：学分需>0，否则Course的setCcredit会抛出非法参数异常
     */
    public boolean addCourse(Course course) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO Course (Cno, Cname, Ccredit, Tno) VALUES (?, ?, ?, ?)";
            // 设置参数（学分已由Course的setCcredit校验>0）
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, course.getCno());
            pstmt.setString(2, course.getCname());
            pstmt.setFloat(3, course.getCcredit());
            pstmt.setString(4, course.getTno());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            if (e.getMessage().contains("PRIMARY")) {
                throw new RuntimeException("新增课程失败：课程编号已存在！", e);
            }
            throw new RuntimeException("新增课程信息失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(pstmt, conn);
        }
    }

    /**
     * 根据课程编号删除课程信息
     * @param cno 课程编号（主键）
     * @return boolean 删除成功返回true，失败返回false
     * 注：若课程关联成绩数据，数据库外键约束会导致删除失败，需先删除关联成绩
     */
    public boolean deleteCourse(String cno) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "DELETE FROM Course WHERE Cno=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, cno);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            if (e.getMessage().contains("FOREIGN KEY")) {
                throw new RuntimeException("删除课程失败：该课程关联成绩数据，请先删除成绩！", e);
            }
            throw new RuntimeException("删除课程信息失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(pstmt, conn);
        }
    }

    /**
     * 修改课程信息（根据课程编号更新）
     * @param course 课程实体对象（必须包含cno，其他字段按需修改）
     * @return boolean 修改成功返回true，失败返回false
     */
    public boolean updateCourse(Course course) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            // SQL：更新课程名称、学分、授课教师编号，条件为课程编号（主键）
            String sql = "UPDATE Course SET Cname=?, Ccredit=?, Tno=? WHERE Cno=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, course.getCname());
            pstmt.setFloat(2, course.getCcredit()); // 学分>0由Course的setter校验
            pstmt.setString(3, course.getTno());
            pstmt.setString(4, course.getCno()); // 主键作为更新条件
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("修改课程信息失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(pstmt, conn);
        }
    }

    /**
     * 根据课程编号查询单个课程信息
     * @param cno 课程编号（主键）
     * @return Course 课程实体对象（未查询到返回null）
     */
    public Course getCourseByCno(String cno) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM Course WHERE Cno=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, cno);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return wrapCourseFromResultSet(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("查询课程信息失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
    }

    /**
     * 高频业务：根据教师编号查询该教师授课的所有课程
     * @param tno 教师编号（外键）
     * @return List<Course> 该教师的授课课程列表（无数据返回空列表）
     */
    public List<Course> getCoursesByTno(String tno) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Course> courseList = new ArrayList<>();
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM Course WHERE Tno=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tno);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                courseList.add(wrapCourseFromResultSet(rs));
            }
            return courseList;
        } catch (SQLException e) {
            throw new RuntimeException("查询教师授课课程失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
    }

    /**
     * 高频业务：根据课程名称模糊查询课程
     * @param cname 课程名称（支持模糊匹配）
     * @return List<Course> 匹配的课程列表（无数据返回空列表）
     */
    public List<Course> getCoursesByName(String cname) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Course> courseList = new ArrayList<>();
        try {
            conn = DBUtil.getConnection();
            // 使用CONCAT拼接%，避免SQL注入（禁止直接拼接字符串）
            String sql = "SELECT * FROM Course WHERE Cname LIKE CONCAT('%', ?, '%')";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, cname);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                courseList.add(wrapCourseFromResultSet(rs));
            }
            return courseList;
        } catch (SQLException e) {
            throw new RuntimeException("根据名称查询课程失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
    }

    /**
     * 查询所有课程信息
     * @return List<Course> 课程列表（无数据返回空列表）
     */
    public List<Course> getAllCourses() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Course> courseList = new ArrayList<>();
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM Course";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                courseList.add(wrapCourseFromResultSet(rs));
            }
            return courseList;
        } catch (SQLException e) {
            throw new RuntimeException("查询所有课程信息失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
    }

    /**
     * 工具方法：将ResultSet封装为Course对象（复用代码，减少冗余）
     * @param rs 结果集
     * @return Course 课程对象
     * @throws SQLException 数据库异常
     */
    private Course wrapCourseFromResultSet(ResultSet rs) throws SQLException {
        Course course = new Course();
        course.setCno(rs.getString("Cno"));
        course.setCname(rs.getString("Cname"));
        course.setCcredit(rs.getFloat("Ccredit"));
        course.setTno(rs.getString("Tno"));
        return course;
    }
}