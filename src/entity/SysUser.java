package entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统用户实体类（JavaBean）
 * 对应数据库中的SysUser表，完整映射表字段及约束
 * 遵循JavaBean规范：私有属性、无参构造、全参构造、getter/setter、toString、序列化
 */
public class SysUser implements Serializable {
    // 序列化版本号，保证序列化/反序列化一致性（如网络传输、持久化）
    private static final long serialVersionUID = 1L;

    /**
     * 系统用户ID（主键）
     * 数据库字段：UserID VARCHAR(12) NOT NULL（如U001001）
     */
    private String userId;

    /**
     * 登录账户（唯一）
     * 数据库字段：Account VARCHAR(20) NOT NULL
     */
    private String account;

    /**
     * 登录密码（SHA256加密存储）
     * 数据库字段：Password VARCHAR(64) NOT NULL
     */
    private String password;

    /**
     * 角色
     * 数据库字段：Role VARCHAR(10) NOT NULL
     * 约束：仅能为"student"、"teacher"、"admin"
     */
    private String role;

    /**
     * 关联ID（学生编号/教师编号/管理员ID）
     * 数据库字段：RelID VARCHAR(10)
     */
    private String relId;

    /**
     * 创建时间
     * 数据库字段：CreateTime DATETIME DEFAULT CURRENT_TIMESTAMP
     * 注：JDK8+可替换为java.time.LocalDateTime，此处用Date保证兼容性
     */
    private Date createTime;

    /**
     * 状态（1-启用，0-禁用）
     * 数据库字段：Status TINYINT DEFAULT 1
     * 约束：仅能为0或1
     */
    private Integer status;

    /**
     * 无参构造方法（JavaBean必需）
     * 用于MyBatis、Spring等框架通过反射实例化对象
     */
    public SysUser() {
    }

    /**
     * 全参构造方法
     * 用于快速创建完整的系统用户对象
     * @param userId 系统用户ID
     * @param account 登录账户
     * @param password 加密密码
     * @param role 角色
     * @param relId 关联ID
     * @param createTime 创建时间
     * @param status 状态（0/1）
     */
    public SysUser(String userId, String account, String password, String role,
                   String relId, Date createTime, Integer status) {
        this.userId = userId;
        setAccount(account);
        setPassword(password);
        setRole(role);
        this.relId = relId;
        this.createTime = createTime;
        setStatus(status);
    }

    // Getter & Setter方法（保证属性封装性，适配数据库约束）
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        // 约束：账户非空
        if (account == null || account.trim().isEmpty()) {
            throw new IllegalArgumentException("登录账户不能为空");
        }
        this.account = account.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        // 约束：密码非空（加密后长度固定，此处仅校验非空）
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("登录密码不能为空");
        }
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        // 核心约束：校验角色合法性，与数据库CHECK约束一致
        if (role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("角色不能为空");
        }
        String roleTrim = role.trim();
        if ("student".equals(roleTrim) || "teacher".equals(roleTrim) || "admin".equals(roleTrim)) {
            this.role = roleTrim;
        } else {
            throw new IllegalArgumentException("角色仅能为'student'、'teacher'、'admin'");
        }
    }

    public String getRelId() {
        return relId;
    }

    public void setRelId(String relId) {
        this.relId = relId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        // 核心约束：校验状态合法性，与数据库CHECK约束一致
        if (status == null) {
            this.status = 1; // 默认启用
        } else if (status == 0 || status == 1) {
            this.status = status;
        } else {
            throw new IllegalArgumentException("状态仅能为0（禁用）或1（启用）");
        }
    }

    /**
     * 重写toString方法
     * 方便打印对象信息（隐藏密码敏感字段），便于调试、日志输出
     * @return 系统用户对象的字符串表示
     */
    @Override
    public String toString() {
        return "SysUser{" +
                "userId='" + userId + '\'' +
                ", account='" + account + '\'' +
                ", role='" + role + '\'' +
                ", relId='" + relId + '\'' +
                ", createTime=" + createTime +
                ", status=" + (status == 1 ? "启用" : "禁用") +
                '}';
    }

    /**
     * 重写equals和hashCode（基于主键userId）
     * 用于集合去重/比较，保证主键唯一性
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SysUser sysUser = (SysUser) o;
        return userId.equals(sysUser.userId);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(userId);
    }
}