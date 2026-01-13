package dao;
import entity.Student;
import util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 学生数据访问层（StudentDao）
 * 封装Student表的所有数据库操作（增删改查），依赖DBUtil工具类和Student实体类
 */
public class StudentDao {
    /**
     * 新增学生信息
     * @param student 学生实体对象（需包含非空字段：sno、sname、ssex）
     * @return boolean 新增成功返回true，失败返回false
     */
    public boolean addStudent(Student student) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO Student (Sno, Sname, Ssex, Sgrade, Smajor) VALUES (?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, student.getSno());
            pstmt.setString(2, student.getSname());
            pstmt.setString(3, student.getSsex());
            pstmt.setString(4, student.getSgrade());
            pstmt.setString(5, student.getSmajor());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("新增学生信息失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(pstmt, conn);
        }
    }

    /**
     * 根据学生编号删除学生信息
     * @param sno 学生编号（主键）
     * @return boolean 删除成功返回true，失败返回false
     */
    public boolean deleteStudent(String sno) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "DELETE FROM Student WHERE Sno=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, sno);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("删除学生信息失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(pstmt, conn);
        }
    }

    /**
     * 修改学生信息（根据学生编号更新所有字段）
     * @param student 学生实体对象（必须包含sno，其他字段按需修改）
     * @return boolean 修改成功返回true，失败返回false
     */
    public boolean updateStudent(Student student) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE Student SET Sname=?, Ssex=?, Sgrade=?, Smajor=? WHERE Sno=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, student.getSname());
            pstmt.setString(2, student.getSsex());
            pstmt.setString(3, student.getSgrade());
            pstmt.setString(4, student.getSmajor());
            pstmt.setString(5, student.getSno()); // 主键作为更新条件
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("修改学生信息失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(pstmt, conn);
        }
    }

    /**
     * 根据学生编号查询单个学生信息
     * @param sno 学生编号（主键）
     * @return Student 学生实体对象（未查询到返回null）
     */
    public Student getStudentBySno(String sno) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM Student WHERE Sno=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, sno);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                // 将ResultSet数据封装为Student对象
                Student student = new Student();
                student.setSno(rs.getString("Sno"));
                student.setSname(rs.getString("Sname"));
                student.setSsex(rs.getString("Ssex"));
                student.setSgrade(rs.getString("Sgrade"));
                student.setSmajor(rs.getString("Smajor"));
                return student;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("查询学生信息失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
    }

    /**
     * 查询所有学生信息
     * @return List<Student> 学生列表（无数据返回空列表，不返回null）
     */
    public List<Student> getAllStudents() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Student> studentList = new ArrayList<>();
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM Student";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Student student = new Student();
                student.setSno(rs.getString("Sno"));
                student.setSname(rs.getString("Sname"));
                student.setSsex(rs.getString("Ssex"));
                student.setSgrade(rs.getString("Sgrade"));
                student.setSmajor(rs.getString("Smajor"));
                studentList.add(student);
            }
            return studentList;
        } catch (SQLException e) {
            throw new RuntimeException("查询所有学生信息失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
    }

    /**
     * 可选：根据姓名模糊查询学生（高频业务场景）
     * @param sname 学生姓名（支持模糊匹配）
     * @return List<Student> 匹配的学生列表
     */
    public List<Student> getStudentsByName(String sname) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Student> studentList = new ArrayList<>();
        try {
            conn = DBUtil.getConnection();
            // 使用CONCAT拼接%，避免SQL注入（禁止直接拼接字符串）
            String sql = "SELECT * FROM Student WHERE Sname LIKE CONCAT('%', ?, '%')";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, sname);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Student student = new Student();
                student.setSno(rs.getString("Sno"));
                student.setSname(rs.getString("Sname"));
                student.setSsex(rs.getString("Ssex"));
                student.setSgrade(rs.getString("Sgrade"));
                student.setSmajor(rs.getString("Smajor"));
                studentList.add(student);
            }
            return studentList;
        } catch (SQLException e) {
            throw new RuntimeException("根据姓名查询学生信息失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
    }
}