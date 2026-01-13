package entity;
import java.io.Serializable;

/**
 * 课程实体类（JavaBean）
 * 对应数据库中的Course表
 * 遵循JavaBean规范：私有属性、无参构造、全参构造、getter/setter、toString、序列化
 */
public class Course implements Serializable {
    // 序列化版本号，保证序列化/反序列化一致性（如网络传输、持久化）
    private static final long serialVersionUID = 1L;

    /**
     * 课程编号（主键）
     * 数据库字段：Cno VARCHAR(8) NOT NULL
     */
    private String cno;

    /**
     * 课程名称
     * 数据库字段：Cname VARCHAR(50) NOT NULL
     */
    private String cname;

    /**
     * 课程学分
     * 数据库字段：Ccredit FLOAT NOT NULL
     * 约束：学分必须大于0
     */
    private Float ccredit;

    /**
     * 授课教师编号（外键）
     * 数据库字段：Tno VARCHAR(8)
     */
    private String tno;

    /**
     * 无参构造方法（JavaBean必需）
     * 用于MyBatis、Spring等框架通过反射实例化对象
     */
    public Course() {
    }

    /**
     * 全参构造方法（去掉学期字段）
     * 用于快速创建完整的课程对象
     * @param cno 课程编号
     * @param cname 课程名称
     * @param ccredit 课程学分
     * @param tno 授课教师编号
     */
    public Course(String cno, String cname, Float ccredit, String tno) {
        this.cno = cno;
        this.cname = cname;
        // 构造方法中复用setCcredit保证学分约束
        setCcredit(ccredit);
        this.tno = tno;
    }

    // Getter & Setter方法（保证属性封装性，适配数据库约束）
    public String getCno() {
        return cno;
    }

    public void setCno(String cno) {
        this.cno = cno;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public Float getCcredit() {
        return ccredit;
    }

    public void setCcredit(Float ccredit) {
        // 核心约束：校验学分>0，与数据库CHECK (Ccredit > 0)一致
        if (ccredit == null) {
            throw new IllegalArgumentException("学分不能为空，且必须大于0");
        } else if (ccredit > 0) {
            this.ccredit = ccredit;
        } else {
            throw new IllegalArgumentException("学分必须大于0（支持小数，如0.5、2.0）");
        }
    }

    public String getTno() {
        return tno;
    }

    public void setTno(String tno) {
        this.tno = tno;
    }

    /**
     * 重写toString方法（去掉学期字段）
     * 方便打印对象信息，便于调试、日志输出和结果展示
     * @return 课程对象的字符串表示
     */
    @Override
    public String toString() {
        return "Course{" +
                "cno='" + cno + '\'' +
                ", cname='" + cname + '\'' +
                ", ccredit=" + ccredit +
                ", tno='" + tno + '\'' +
                '}';
    }

    // 可选：重写equals和hashCode（基于主键cno），用于集合去重/比较
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return cno.equals(course.cno);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(cno);
    }
}