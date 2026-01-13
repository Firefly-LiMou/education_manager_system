package dao;

import entity.Score;
import util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 成绩数据访问层（ScoreDao）
 * 封装Score表的所有数据库操作（增删改查），依赖DBUtil工具类和Score实体类
 * 核心适配：复合主键（sno+cno）、成绩范围约束、录入时间默认值、按学生/课程维度查询成绩
 */
public class ScoreDao {
    /**
     * 新增成绩信息
     * @param score 成绩实体对象（需包含非空字段：sno、cno，score可为空表示未录入）
     * @return boolean 新增成功返回true，失败返回false
     * 注：复合主键（sno+cno）重复会抛出异常
     */
    public boolean addScore(Score score) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO Score (Sno, Cno, Score, InputTime, InputTno) VALUES (?, ?, ?, ?, ?)";
            // 录入时间为空则填充当前时间
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, score.getSno());
            pstmt.setString(2, score.getCno());
            // 成绩可为null（未录入）
            if (score.getScore() != null) {
                pstmt.setFloat(3, score.getScore());
            } else {
                pstmt.setNull(3, java.sql.Types.FLOAT);
            }
            // 录入时间默认当前时间
            pstmt.setDate(4, score.getInputTime() != null ? new java.sql.Date(score.getInputTime().getTime()) : new java.sql.Date(new Date().getTime()));
            pstmt.setString(5, score.getInputTno());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            if (e.getMessage().contains("PRIMARY")) {
                throw new RuntimeException("新增成绩失败：该学生的该课程成绩已存在！", e);
            }
            throw new RuntimeException("新增成绩信息失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(pstmt, conn);
        }
    }

    /**
     * 根据复合主键（学生编号+课程编号）删除成绩
     * @param sno 学生编号
     * @param cno 课程编号
     * @return boolean 删除成功返回true，失败返回false
     */
    public boolean deleteScore(String sno, String cno) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "DELETE FROM Score WHERE Sno=? AND Cno=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, sno);
            pstmt.setString(2, cno);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("删除成绩信息失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(pstmt, conn);
        }
    }

    /**
     * 修改成绩信息（根据复合主键更新）
     * @param score 成绩实体对象（必须包含sno、cno，其他字段按需修改）
     * @return boolean 修改成功返回true，失败返回false
     */
    public boolean updateScore(Score score) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE Score SET Score=?, InputTime=?, InputTno=? WHERE Sno=? AND Cno=?";
            pstmt = conn.prepareStatement(sql);
            // 成绩可为null
            if (score.getScore() != null) {
                pstmt.setFloat(1, score.getScore());
            } else {
                pstmt.setNull(1, java.sql.Types.FLOAT);
            }
            // 录入时间默认当前时间
            pstmt.setDate(2, score.getInputTime() != null ? new java.sql.Date(score.getInputTime().getTime()) : new java.sql.Date(new Date().getTime()));
            pstmt.setString(3, score.getInputTno());
            // 复合主键作为更新条件
            pstmt.setString(4, score.getSno());
            pstmt.setString(5, score.getCno());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("修改成绩信息失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(pstmt, conn);
        }
    }

    /**
     * 根据复合主键（学生编号+课程编号）查询单个成绩
     * @param sno 学生编号
     * @param cno 课程编号
     * @return Score 成绩实体对象（未查询到返回null）
     */
    public Score getScoreBySnoAndCno(String sno, String cno) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM Score WHERE Sno=? AND Cno=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, sno);
            pstmt.setString(2, cno);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return wrapScoreFromResultSet(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("查询成绩信息失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
    }

    /**
     * 高频业务：根据学生编号查询该学生所有课程成绩
     * @param sno 学生编号
     * @return List<Score> 该学生的成绩列表（无数据返回空列表）
     */
    public List<Score> getScoresBySno(String sno) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Score> scoreList = new ArrayList<>();
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM Score WHERE Sno=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, sno);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                scoreList.add(wrapScoreFromResultSet(rs));
            }
            return scoreList;
        } catch (SQLException e) {
            throw new RuntimeException("查询学生成绩列表失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
    }

    /**
     * 高频业务：根据课程编号查询该课程所有学生成绩
     * @param cno 课程编号
     * @return List<Score> 该课程的成绩列表（无数据返回空列表）
     */
    public List<Score> getScoresByCno(String cno) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Score> scoreList = new ArrayList<>();
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM Score WHERE Cno=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, cno);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                scoreList.add(wrapScoreFromResultSet(rs));
            }
            return scoreList;
        } catch (SQLException e) {
            throw new RuntimeException("查询课程成绩列表失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
    }

    /**
     * 查询所有成绩信息
     * @return List<Score> 成绩列表（无数据返回空列表）
     */
    public List<Score> getAllScores() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Score> scoreList = new ArrayList<>();
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM Score";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                scoreList.add(wrapScoreFromResultSet(rs));
            }
            return scoreList;
        } catch (SQLException e) {
            throw new RuntimeException("查询所有成绩信息失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
    }

    /**
     * 工具方法：将ResultSet封装为Score对象（复用代码，减少冗余）
     * @param rs 结果集
     * @return Score 成绩对象
     * @throws SQLException 数据库异常
     */
    private Score wrapScoreFromResultSet(ResultSet rs) throws SQLException {
        Score score = new Score();
        score.setSno(rs.getString("Sno"));
        score.setCno(rs.getString("Cno"));
        // 成绩可为null，需判空
        if (rs.getObject("Score") != null) {
            score.setScore(rs.getFloat("Score"));
        }
        score.setInputTime(rs.getDate("InputTime"));
        score.setInputTno(rs.getString("InputTno"));
        return score;
    }
}