package entity;
import java.io.Serializable;
import java.util.Date;

/**
 * 成绩实体类（JavaBean）
 * 对应数据库中的Score表，完整映射表字段及约束
 * 遵循JavaBean规范：私有属性、无参构造、全参构造、getter/setter、toString、序列化
 */
public class Score implements Serializable {
    // 序列化版本号，保证序列化/反序列化一致性（如网络传输、持久化）
    private static final long serialVersionUID = 1L;

    /**
     * 学生编号（复合主键/外键）
     * 数据库字段：Sno VARCHAR(10) NOT NULL
     */
    private String sno;

    /**
     * 课程编号（复合主键/外键）
     * 数据库字段：Cno VARCHAR(8) NOT NULL
     */
    private String cno;

    /**
     * 成绩
     * 数据库字段：Score FLOAT
     * 约束：成绩需在0-100范围内（包含0和100）
     */
    private Float score;

    /**
     * 成绩录入时间
     * 数据库字段：InputTime DATETIME DEFAULT CURRENT_TIMESTAMP
     * 注：也可使用java.time.LocalDateTime（JDK8+），此处用Date保证兼容性
     */
    private Date inputTime;

    /**
     * 录入教师编号（外键）
     * 数据库字段：InputTno VARCHAR(8)
     */
    private String inputTno;

    /**
     * 无参构造方法（JavaBean必需）
     * 用于MyBatis、Spring等框架通过反射实例化对象
     */
    public Score() {
    }

    /**
     * 全参构造方法
     * 用于快速创建完整的成绩对象
     * @param sno 学生编号
     * @param cno 课程编号
     * @param score 成绩
     * @param inputTime 录入时间
     * @param inputTno 录入教师编号
     */
    public Score(String sno, String cno, Float score, Date inputTime, String inputTno) {
        this.sno = sno;
        this.cno = cno;
        // 构造方法中复用setScore保证成绩约束
        setScore(score);
        this.inputTime = inputTime;
        this.inputTno = inputTno;
    }

    // Getter & Setter方法（保证属性封装性，适配数据库约束）
    public String getSno() {
        return sno;
    }

    public void setSno(String sno) {
        this.sno = sno;
    }

    public String getCno() {
        return cno;
    }

    public void setCno(String cno) {
        this.cno = cno;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        // 核心约束：校验成绩范围，与数据库CHECK (Score BETWEEN 0 AND 100)一致
        if (score == null) {
            this.score = null; // 允许成绩为空（未录入）
        } else if (score >= 0 && score <= 100) {
            this.score = score;
        } else {
            throw new IllegalArgumentException("成绩必须在0-100范围内（包含0和100）");
        }
    }

    public Date getInputTime() {
        return inputTime;
    }

    public void setInputTime(Date inputTime) {
        this.inputTime = inputTime;
    }

    public String getInputTno() {
        return inputTno;
    }

    public void setInputTno(String inputTno) {
        this.inputTno = inputTno;
    }

    /**
     * 重写toString方法
     * 方便打印对象信息，便于调试、日志输出和结果展示
     * @return 成绩对象的字符串表示
     */
    @Override
    public String toString() {
        return "Score{" +
                "sno='" + sno + '\'' +
                ", cno='" + cno + '\'' +
                ", score=" + score +
                ", inputTime=" + inputTime +
                ", inputTno='" + inputTno + '\'' +
                '}';
    }

    // 可选：重写equals和hashCode（基于复合主键），用于集合去重/比较
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Score score1 = (Score) o;
        return sno.equals(score1.sno) && cno.equals(score1.cno);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(sno, cno);
    }
}