package entity;
import java.io.Serializable;

/**
 * 学生实体类（JavaBean）
 * 对应数据库中的Student表，完整映射表字段及约束
 * 遵循JavaBean规范：私有属性、无参构造、全参构造、getter/setter、toString、序列化
 */
public class Student implements Serializable {
    // 序列化版本号，保证序列化/反序列化一致性
    private static final long serialVersionUID = 1L;

    /**
     * 学生编号（主键）
     * 数据库字段：Sno VARCHAR(10) NOT NULL
     */
    private String sno;

    /**
     * 学生姓名
     * 数据库字段：Sname VARCHAR(20) NOT NULL
     */
    private String sname;

    /**
     * 学生性别
     * 数据库字段：Ssex VARCHAR(2) NOT NULL
     * 约束：仅能为"男"或"女"
     */
    private String ssex;

    /**
     * 年级
     * 数据库字段：Sgrade VARCHAR(10)
     */
    private String sgrade;

    /**
     * 专业
     * 数据库字段：Smajor VARCHAR(30)
     */
    private String smajor;

    /**
     * 无参构造方法（JavaBean必需）
     * 用于反射实例化（如MyBatis、Spring等框架）
     */
    public Student() {
    }

    /**
     * 全参构造方法
     * 用于快速创建完整的学生对象
     * @param sno 学生编号
     * @param sname 学生姓名
     * @param ssex 学生性别
     * @param sgrade 年级
     * @param smajor 专业
     */
    public Student(String sno, String sname, String ssex, String sgrade, String smajor) {
        this.sno = sno;
        this.sname = sname;
        this.ssex = ssex;
        this.sgrade = sgrade;
        this.smajor = smajor;
    }

    // Getter & Setter方法（每个属性对应，保证封装性）
    public String getSno() {
        return sno;
    }

    public void setSno(String sno) {
        this.sno = sno;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public String getSsex() {
        return ssex;
    }

    public void setSsex(String ssex) {
        // 可选：在setter中增加性别约束校验（贴合数据库约束）
        if ("男".equals(ssex) || "女".equals(ssex)) {
            this.ssex = ssex;
        } else {
            throw new IllegalArgumentException("性别仅能为'男'或'女'");
        }
    }

    public String getSgrade() {
        return sgrade;
    }

    public void setSgrade(String sgrade) {
        this.sgrade = sgrade;
    }

    public String getSmajor() {
        return smajor;
    }

    public void setSmajor(String smajor) {
        this.smajor = smajor;
    }

    /**
     * 重写toString方法
     * 方便打印对象信息，便于调试和日志输出
     * @return 学生对象的字符串表示
     */
    @Override
    public String toString() {
        return "Student{" +
                "sno='" + sno + '\'' +
                ", sname='" + sname + '\'' +
                ", ssex='" + ssex + '\'' +
                ", sgrade='" + sgrade + '\'' +
                ", smajor='" + smajor + '\'' +
                '}';
    }
}