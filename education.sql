-- 创建教务管理数据库，指定字符集为utf8mb4（兼容中文），排序规则为utf8mb4_general_ci
CREATE DATABASE IF NOT EXISTS education_manage_system
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_general_ci;

-- 使用该数据库
USE education_manage_system;

-- 1. 系统用户表（核心）
CREATE TABLE IF NOT EXISTS SysUser (
   UserID BIGINT AUTO_INCREMENT NOT NULL COMMENT '系统用户ID（主键，如U001001）',
   Account VARCHAR(20) NOT NULL COMMENT '登录账户（唯一）',
   Password VARCHAR(64) NOT NULL COMMENT '登录密码（SHA256加密）',
   Role VARCHAR(10) NOT NULL COMMENT '角色（student/teacher/admin）',
   RelID VARCHAR(10) COMMENT '关联ID（学生编号/教师编号/管理员ID）',
   CreateTime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   Status TINYINT DEFAULT 1 COMMENT '状态（1-启用，0-禁用）',
-- 主键约束
   PRIMARY KEY (UserID),
-- 唯一约束：账户不能重复
   UNIQUE KEY uk_sysuser_account (Account),
-- 自定义约束
   CONSTRAINT chk_sysuser_role CHECK (Role IN ('student', 'teacher', 'admin')),
   CONSTRAINT chk_sysuser_status CHECK (Status IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表（登录验证核心）';

-- 2. 学生表
CREATE TABLE IF NOT EXISTS Student (
   Sno VARCHAR(10) NOT NULL COMMENT '学生编号（主键）',
   Sname VARCHAR(20) NOT NULL COMMENT '学生姓名',
   Ssex VARCHAR(2) NOT NULL COMMENT '学生性别',
   Sgrade VARCHAR(10) COMMENT '年级',
   Smajor VARCHAR(30) COMMENT '专业',
   PRIMARY KEY (Sno),
   CONSTRAINT chk_student_sex CHECK (Ssex IN ('男', '女'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生信息表';

-- 3. 教师表
CREATE TABLE IF NOT EXISTS Teacher (
   Tno VARCHAR(8) NOT NULL COMMENT '教师编号（主键）',
   Tname VARCHAR(20) NOT NULL COMMENT '教师姓名',
   Tsex VARCHAR(2) COMMENT '教师性别',
   Ttitle VARCHAR(10) COMMENT '教师职称',
   Tdept VARCHAR(30) COMMENT '所属部门',
   PRIMARY KEY (Tno),
   CONSTRAINT chk_teacher_title CHECK (Ttitle IN ('助教', '讲师', '副教授', '教授'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师信息表';

-- 4. 课程表
CREATE TABLE IF NOT EXISTS Course (
    Cno VARCHAR(8) NOT NULL COMMENT '课程编号（主键）',
    Cname VARCHAR(50) NOT NULL COMMENT '课程名称',
    Ccredit FLOAT NOT NULL COMMENT '课程学分',
    Tno VARCHAR(8) COMMENT '授课教师编号（外键）',
    PRIMARY KEY (Cno),
    FOREIGN KEY (Tno) REFERENCES Teacher(Tno)
      ON UPDATE CASCADE
      ON DELETE SET NULL,
    CONSTRAINT chk_course_credit CHECK (Ccredit > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程信息表';

-- 5. 成绩表
CREATE TABLE IF NOT EXISTS Score (
     Sno VARCHAR(10) NOT NULL COMMENT '学生编号（外键/主键）',
     Cno VARCHAR(8) NOT NULL COMMENT '课程编号（外键/主键）',
     Score FLOAT COMMENT '成绩',
     InputTime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '成绩录入时间',
     InputTno VARCHAR(8) COMMENT '录入教师编号（外键）',
     PRIMARY KEY (Sno, Cno),
     FOREIGN KEY (Sno) REFERENCES Student(Sno)
         ON UPDATE CASCADE
         ON DELETE CASCADE,
     FOREIGN KEY (Cno) REFERENCES Course(Cno)
         ON UPDATE CASCADE
         ON DELETE CASCADE,
     FOREIGN KEY (InputTno) REFERENCES Teacher(Tno)
         ON UPDATE CASCADE
         ON DELETE SET NULL,
     CONSTRAINT chk_score_value CHECK (Score BETWEEN 0 AND 100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成绩信息表';

-- 索引
CREATE INDEX idx_student_name ON Student(Sname);
CREATE INDEX idx_course_tno ON Course(Tno);
CREATE INDEX idx_score_sno ON Score(Sno);
CREATE INDEX idx_score_cno ON Score(Cno);

-- 系统用户表索引
CREATE INDEX idx_sysuser_account ON SysUser(Account); -- 登录时按账户查询，高频
CREATE INDEX idx_sysuser_role_relid ON SysUser(Role, RelID); -- 按角色+关联ID查询（如绑定学生）
CREATE INDEX idx_sysuser_status ON SysUser(Status); -- 筛选启用/禁用用户

-- 视图1 - 学生-分数关联视图
CREATE VIEW v_student_score AS
SELECT
    s.Sno AS 学生编号,
    s.Sname AS 学生姓名,
    c.Cno AS 课程编号,
    c.Cname AS 课程名称,
    sc.Score AS 成绩,
    sc.InputTime AS 录入时间,
    t.Tname AS 录入教师
FROM
    Student s
        JOIN
    Score sc ON s.Sno = sc.Sno
        JOIN
    Course c ON sc.Cno = c.Cno
        LEFT JOIN
    Teacher t ON sc.InputTno = t.Tno;

-- 视图2 - 教师-课程-分数关联视图
CREATE VIEW v_teacher_course_score AS
SELECT
    t.Tno AS 教师编号,
    t.Tname AS 教师姓名,
    c.Cno AS 课程编号,
    c.Cname AS 课程名称,
    s.Sno AS 学生编号,
    s.Sname AS 学生姓名,
    sc.Score AS 成绩
FROM
    Teacher t
        JOIN
    Course c ON t.Tno = c.Tno
        LEFT JOIN
    Score sc ON c.Cno = sc.Cno
        LEFT JOIN
    Student s ON sc.Sno = s.Sno;

-- 视图3 - 系统用户-学生关联视图（管理员查看）
CREATE VIEW v_sysuser_student AS
SELECT
    su.UserID AS 用户ID,
    su.Account AS 登录账户,
    su.Role AS 角色,
    su.Status AS 状态,
    s.Sno AS 学生编号,
    s.Sname AS 学生姓名,
    s.Smajor AS 专业
FROM
    SysUser su
        LEFT JOIN
    Student s ON su.RelID = s.Sno
WHERE
    su.Role = 'student';

-- 视图4 - 系统用户-教师关联视图（管理员查看）
CREATE VIEW v_sysuser_teacher AS
SELECT
    su.UserID AS 用户ID,
    su.Account AS 登录账户,
    su.Role AS 角色,
    su.Status AS 状态,
    t.Tno AS 教师编号,
    t.Tname AS 教师姓名,
    t.Tdept AS 所属部门
FROM
    SysUser su
        LEFT JOIN
    Teacher t ON su.RelID = t.Tno
WHERE
    su.Role = 'teacher';

