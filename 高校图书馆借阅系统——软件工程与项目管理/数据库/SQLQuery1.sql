/* ========= 0. 创建数据库 ========= */
IF DB_ID(N'UniversityLibrary') IS NULL
BEGIN
    CREATE DATABASE UniversityLibrary;
END
GO

USE UniversityLibrary;
GO

/* ========= 1. 用户表 [user] ========= */
IF OBJECT_ID(N'dbo.[user]', N'U') IS NOT NULL DROP TABLE dbo.[user];
GO

CREATE TABLE dbo.[user] (
    user_id        BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_user PRIMARY KEY,
    login_id       NVARCHAR(50) NOT NULL,
    [name]         NVARCHAR(50) NOT NULL,
    password_hash  NVARCHAR(255) NOT NULL,
    role           NVARCHAR(10) NOT NULL,      -- READER / ADMIN
    [status]       NVARCHAR(10) NOT NULL,      -- ACTIVE / DISABLED
    created_at     DATETIME2(0) NOT NULL CONSTRAINT DF_user_created_at DEFAULT (SYSDATETIME())
);

ALTER TABLE dbo.[user]
ADD CONSTRAINT UQ_user_login_id UNIQUE (login_id);

ALTER TABLE dbo.[user]
ADD CONSTRAINT CK_user_role CHECK (role IN (N'READER', N'ADMIN'));

ALTER TABLE dbo.[user]
ADD CONSTRAINT CK_user_status CHECK ([status] IN (N'ACTIVE', N'DISABLED'));
GO

IF OBJECT_ID(N'dbo.reader_profile', N'U') IS NOT NULL DROP TABLE dbo.reader_profile;
GO

CREATE TABLE dbo.reader_profile (
    user_id           BIGINT NOT NULL CONSTRAINT PK_reader_profile PRIMARY KEY,
    reader_type       NVARCHAR(20) NULL,
    max_borrow_limit  INT NOT NULL CONSTRAINT DF_reader_profile_limit DEFAULT (10),
    CONSTRAINT FK_reader_profile_user
        FOREIGN KEY (user_id) REFERENCES dbo.[user](user_id)
);

ALTER TABLE dbo.reader_profile
ADD CONSTRAINT CK_reader_profile_limit CHECK (max_borrow_limit BETWEEN 0 AND 50);
GO

/* ========= 2. 书目表 book_title ========= */
IF OBJECT_ID(N'dbo.book_title', N'U') IS NOT NULL DROP TABLE dbo.book_title;
GO

CREATE TABLE dbo.book_title (
    book_id     BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_book_title PRIMARY KEY,
    isbn        NVARCHAR(20) NOT NULL,
    title       NVARCHAR(200) NOT NULL,
    author      NVARCHAR(100) NULL,
    publisher   NVARCHAR(100) NULL,
    category    NVARCHAR(50)  NULL
);

ALTER TABLE dbo.book_title
ADD CONSTRAINT UQ_book_title_isbn UNIQUE (isbn);
GO

/* ========= 3. 馆藏副本表 book_copy ========= */
IF OBJECT_ID(N'dbo.book_copy', N'U') IS NOT NULL DROP TABLE dbo.book_copy;
GO

CREATE TABLE dbo.book_copy (
    copy_id     NVARCHAR(50) NOT NULL CONSTRAINT PK_book_copy PRIMARY KEY,  -- 条码/馆藏编号
    book_id     BIGINT NOT NULL,
    [status]    NVARCHAR(20) NOT NULL,  -- AVAILABLE/BORROWED/MAINTENANCE/REMOVED
    location    NVARCHAR(100) NULL
);

ALTER TABLE dbo.book_copy
ADD CONSTRAINT FK_book_copy_book_title
    FOREIGN KEY (book_id) REFERENCES dbo.book_title(book_id);

ALTER TABLE dbo.book_copy
ADD CONSTRAINT CK_book_copy_status CHECK ([status] IN (N'AVAILABLE', N'BORROWED', N'MAINTENANCE', N'REMOVED'));
GO

/* ========= 4. 借阅记录表 loan ========= */
IF OBJECT_ID(N'dbo.loan', N'U') IS NOT NULL DROP TABLE dbo.loan;
GO

CREATE TABLE dbo.loan (
    loan_id      BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_loan PRIMARY KEY,
    reader_id    BIGINT NOT NULL,          -- 逻辑上应为 role=READER（数据库层无法用 FK 直接约束，业务/触发器控制）
    copy_id      NVARCHAR(50) NOT NULL,
    borrowed_at  DATETIME2(0) NOT NULL CONSTRAINT DF_loan_borrowed_at DEFAULT (SYSDATETIME()),
    due_at       DATETIME2(0) NOT NULL,
    returned_at  DATETIME2(0) NULL,
    [status]     NVARCHAR(10) NOT NULL     -- BORROWED/RETURNED/OVERDUE/CLOSED
);

ALTER TABLE dbo.loan
ADD CONSTRAINT FK_loan_reader
    FOREIGN KEY (reader_id) REFERENCES dbo.[user](user_id);

ALTER TABLE dbo.loan
ADD CONSTRAINT FK_loan_copy
    FOREIGN KEY (copy_id) REFERENCES dbo.book_copy(copy_id);

ALTER TABLE dbo.loan
ADD CONSTRAINT CK_loan_status CHECK ([status] IN (N'BORROWED', N'RETURNED', N'OVERDUE', N'CLOSED'));

ALTER TABLE dbo.loan
ADD CONSTRAINT CK_loan_time CHECK (
    due_at >= borrowed_at
    AND (returned_at IS NULL OR returned_at >= borrowed_at)
);

ALTER TABLE dbo.loan
ADD CONSTRAINT CK_loan_return_status_consistency CHECK (
    (returned_at IS NULL AND [status] IN (N'BORROWED', N'OVERDUE'))
    OR
    (returned_at IS NOT NULL AND [status] IN (N'RETURNED', N'CLOSED'))
);
GO

/* 并发关键约束：同一副本同一时刻只能存在一条“未归还”借阅记录（SQL Server 过滤唯一索引） */
CREATE UNIQUE INDEX UX_loan_copy_unreturned
ON dbo.loan(copy_id)
WHERE returned_at IS NULL;
GO

/* 高频索引 */
CREATE INDEX IX_loan_reader_returned ON dbo.loan(reader_id, returned_at) INCLUDE (borrowed_at, due_at, [status]);
CREATE INDEX IX_loan_copy_returned   ON dbo.loan(copy_id, returned_at)   INCLUDE (borrowed_at, due_at, reader_id, [status]);
CREATE INDEX IX_loan_due_returned    ON dbo.loan(due_at, returned_at)    INCLUDE (copy_id, reader_id, [status]);
GO

/* ========= 5. 罚款表 fine ========= */
IF OBJECT_ID(N'dbo.fine', N'U') IS NOT NULL DROP TABLE dbo.fine;
GO

CREATE TABLE dbo.fine (
    fine_id     BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_fine PRIMARY KEY,
    loan_id     BIGINT NOT NULL,
    amount      DECIMAL(10,2) NOT NULL,
    created_at  DATETIME2(0) NOT NULL CONSTRAINT DF_fine_created_at DEFAULT (SYSDATETIME()),
    [status]    NVARCHAR(10) NOT NULL,     -- UNPAID/PAID
    reason      NVARCHAR(20) NOT NULL      -- OVERDUE
);

ALTER TABLE dbo.fine
ADD CONSTRAINT FK_fine_loan
    FOREIGN KEY (loan_id) REFERENCES dbo.loan(loan_id);

ALTER TABLE dbo.fine
ADD CONSTRAINT UQ_fine_loan UNIQUE (loan_id); -- 1 loan <= 1 fine

ALTER TABLE dbo.fine
ADD CONSTRAINT CK_fine_amount CHECK (amount >= 0);

ALTER TABLE dbo.fine
ADD CONSTRAINT CK_fine_status CHECK ([status] IN (N'UNPAID', N'PAID'));

ALTER TABLE dbo.fine
ADD CONSTRAINT CK_fine_reason CHECK (reason IN (N'OVERDUE'));
GO

CREATE INDEX IX_fine_status ON dbo.fine([status]) INCLUDE (loan_id, amount, created_at);
GO

/* ========= 6. 缴费表 payment ========= */
IF OBJECT_ID(N'dbo.payment', N'U') IS NOT NULL DROP TABLE dbo.payment;
GO

CREATE TABLE dbo.payment (
    payment_id  BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_payment PRIMARY KEY,
    fine_id     BIGINT NOT NULL,
    amount      DECIMAL(10,2) NOT NULL,
    paid_at     DATETIME2(0) NOT NULL CONSTRAINT DF_payment_paid_at DEFAULT (SYSDATETIME()),
    method      NVARCHAR(20) NOT NULL       -- 现金/校园卡/线上等（枚举可扩展）
);

ALTER TABLE dbo.payment
ADD CONSTRAINT FK_payment_fine
    FOREIGN KEY (fine_id) REFERENCES dbo.fine(fine_id);

ALTER TABLE dbo.payment
ADD CONSTRAINT CK_payment_amount CHECK (amount > 0);
GO

CREATE INDEX IX_payment_fine_paid_at ON dbo.payment(fine_id, paid_at) INCLUDE (amount, method);
GO

/* ========= （可选加分）管理员重置密码审计表 ========= */
IF OBJECT_ID(N'dbo.password_reset_log', N'U') IS NOT NULL DROP TABLE dbo.password_reset_log;
GO

CREATE TABLE dbo.password_reset_log (
    log_id     BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_password_reset_log PRIMARY KEY,
    admin_id   BIGINT NOT NULL,
    reader_id  BIGINT NOT NULL,
    reset_at   DATETIME2(0) NOT NULL CONSTRAINT DF_password_reset_log_reset_at DEFAULT (SYSDATETIME())
);

ALTER TABLE dbo.password_reset_log
ADD CONSTRAINT FK_password_reset_log_admin
    FOREIGN KEY (admin_id) REFERENCES dbo.[user](user_id);

ALTER TABLE dbo.password_reset_log
ADD CONSTRAINT FK_password_reset_log_reader
    FOREIGN KEY (reader_id) REFERENCES dbo.[user](user_id);

CREATE INDEX IX_password_reset_log_reader_time ON dbo.password_reset_log(reader_id, reset_at) INCLUDE (admin_id);
GO