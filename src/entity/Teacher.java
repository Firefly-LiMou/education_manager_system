package entity;

import java.io.Serializable;

/**
 * 教师实体类（JavaBean）
 * 对应数据库中的Teacher表，完整映射表字段及约束
 * 遵循JavaBean规范：私有属性、无参构造、全参构造、getter/setter、toString、序列化
 */
public class Teacher implements Serializable {
    // 序列化版本号，保证序列化/反序列化一致性（如网络传输、持久化）
    private static final long serialVersionUID = 1L;

    /**
     * 教师编号（主键）
     * 数据库字段：Tno VARCHAR(8) NOT NULL
     */
    private String tno;

    /**
     * 教师姓名
     * 数据库字段：Tname VARCHAR(20) NOT NULL
     */
    private String tname;

    /**
     * 教师性别
     * 数据库字段：Tsex VARCHAR(2)
     */
    private String tsex;

    /**
     * 教师职称
     * 数据库字段：Ttitle VARCHAR(10)
     * 约束：仅能为"助教"、"讲师"、"副教授"、"教授"
     */
    private String ttitle;

    /**
     * 所属部门
     * 数据库字段：Tdept VARCHAR(30)
     */
    private String tdept;

    /**
     * 无参构造方法（JavaBean必需）
     * 用于MyBatis、Spring等框架通过反射实例化对象
     */
    public Teacher() {
    }

    /**
     * 全参构造方法
     * 用于快速创建完整的教师对象
     * @param tno 教师编号
     * @param tname 教师姓名
     * @param tsex 教师性别
     * @param ttitle 教师职称
     * @param tdept 所属部门
     */
    public Teacher(String tno, String tname, String tsex, String ttitle, String tdept) {
        this.tno = tno;
        this.tname = tname;
        this.tsex = tsex;
        // 构造方法中也校验职称合法性，与setter逻辑一致
        setTtitle(ttitle);
        this.tdept = tdept;
    }

    // Getter & Setter方法（保证属性封装性，适配数据库约束）
    public String getTno() {
        return tno;
    }

    public void setTno(String tno) {
        this.tno = tno;
    }

    public String getTname() {
        return tname;
    }

    public void setTname(String tname) {
        this.tname = tname;
    }

    public String getTsex() {
        return tsex;
    }

    public void setTsex(String tsex) {
        // 可选：性别补充约束（数据库未强制，此处可选校验）
        if (tsex == null || "男".equals(tsex) || "女".equals(tsex)) {
            this.tsex = tsex;
        } else {
            throw new IllegalArgumentException("性别仅能为'男'或'女'（或为空）");
        }
    }

    public String getTtitle() {
        return ttitle;
    }

    public void setTtitle(String ttitle) {
        // 核心约束：校验职称合法性，与数据库CHECK约束一致
        if (ttitle == null
                || "助教".equals(ttitle)
                || "讲师".equals(ttitle)
                || "副教授".equals(ttitle)
                || "教授".equals(ttitle)) {
            this.ttitle = ttitle;
        } else {
            throw new IllegalArgumentException("职称仅能为'助教'、'讲师'、'副教授'、'教授'（或为空）");
        }
    }

    public String getTdept() {
        return tdept;
    }

    public void setTdept(String tdept) {
        this.tdept = tdept;
    }

    /**
     * 重写toString方法
     * 方便打印对象信息，便于调试、日志输出和结果展示
     * @return 教师对象的字符串表示
     */
    @Override
    public String toString() {
        return "Teacher{" +
                "tno='" + tno + '\'' +
                ", tname='" + tname + '\'' +
                ", tsex='" + tsex + '\'' +
                ", ttitle='" + ttitle + '\'' +
                ", tdept='" + tdept + '\'' +
                '}';
    }
}