using System;
using System.Windows.Forms;

public partial class AdminMainForm : Form
{
    public AdminMainForm()
    {
        InitializeComponent();
    }

    private void InitializeComponent()
    {
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(AdminMainForm));
            this.menuStrip1 = new System.Windows.Forms.MenuStrip();
            this.readerManageToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.bookManageToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.studentManageToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.teacherManageToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.classManageToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.borrowCardManageToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.borrowQueryToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.btnOverdueBooks = new System.Windows.Forms.Button();
            this.btnFineManage = new System.Windows.Forms.Button();
            this.btnCirculationStats = new System.Windows.Forms.Button();
            this.btnCollectionStats = new System.Windows.Forms.Button();
            this.btnExcellentReader = new System.Windows.Forms.Button();
            this.panel1 = new System.Windows.Forms.Panel();
            this.panel2 = new System.Windows.Forms.Panel();
            this.pictureBox1 = new System.Windows.Forms.PictureBox();
            this.pictureBox2 = new System.Windows.Forms.PictureBox();
            this.pictureBox3 = new System.Windows.Forms.PictureBox();
            this.pictureBox4 = new System.Windows.Forms.PictureBox();
            this.pictureBox5 = new System.Windows.Forms.PictureBox();
            this.button1 = new System.Windows.Forms.Button();
            this.pictureBox6 = new System.Windows.Forms.PictureBox();
            this.pictureBox7 = new System.Windows.Forms.PictureBox();
            this.lblAccount = new System.Windows.Forms.Label();
            this.label1 = new System.Windows.Forms.Label();
            this.label2 = new System.Windows.Forms.Label();
            this.label3 = new System.Windows.Forms.Label();
            this.label4 = new System.Windows.Forms.Label();
            this.label5 = new System.Windows.Forms.Label();
            this.label6 = new System.Windows.Forms.Label();
            this.ADMIN = new System.Windows.Forms.Label();
            this.menuStrip1.SuspendLayout();
            this.panel1.SuspendLayout();
            this.panel2.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox1)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox2)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox3)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox4)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox5)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox6)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox7)).BeginInit();
            this.SuspendLayout();
            // 
            // menuStrip1
            // 
            this.menuStrip1.AccessibleRole = System.Windows.Forms.AccessibleRole.None;
            this.menuStrip1.Anchor = System.Windows.Forms.AnchorStyles.None;
            this.menuStrip1.BackColor = System.Drawing.SystemColors.HotTrack;
            this.menuStrip1.BackgroundImageLayout = System.Windows.Forms.ImageLayout.None;
            this.menuStrip1.Dock = System.Windows.Forms.DockStyle.None;
            this.menuStrip1.ImageScalingSize = new System.Drawing.Size(24, 24);
            this.menuStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.readerManageToolStripMenuItem,
            this.bookManageToolStripMenuItem,
            this.studentManageToolStripMenuItem,
            this.teacherManageToolStripMenuItem,
            this.classManageToolStripMenuItem,
            this.borrowCardManageToolStripMenuItem,
            this.borrowQueryToolStripMenuItem});
            this.menuStrip1.Location = new System.Drawing.Point(0, 17);
            this.menuStrip1.Name = "menuStrip1";
            this.menuStrip1.Size = new System.Drawing.Size(496, 25);
            this.menuStrip1.TabIndex = 0;
            // 
            // readerManageToolStripMenuItem
            // 
            this.readerManageToolStripMenuItem.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.readerManageToolStripMenuItem.Name = "readerManageToolStripMenuItem";
            this.readerManageToolStripMenuItem.Size = new System.Drawing.Size(68, 21);
            this.readerManageToolStripMenuItem.Text = "读者管理";
            this.readerManageToolStripMenuItem.Click += new System.EventHandler(this.ReaderManage_Click);
            // 
            // bookManageToolStripMenuItem
            // 
            this.bookManageToolStripMenuItem.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.bookManageToolStripMenuItem.Name = "bookManageToolStripMenuItem";
            this.bookManageToolStripMenuItem.Size = new System.Drawing.Size(68, 21);
            this.bookManageToolStripMenuItem.Text = "图书管理";
            this.bookManageToolStripMenuItem.Click += new System.EventHandler(this.BookManage_Click);
            // 
            // studentManageToolStripMenuItem
            // 
            this.studentManageToolStripMenuItem.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.studentManageToolStripMenuItem.Name = "studentManageToolStripMenuItem";
            this.studentManageToolStripMenuItem.Size = new System.Drawing.Size(68, 21);
            this.studentManageToolStripMenuItem.Text = "学生管理";
            this.studentManageToolStripMenuItem.Click += new System.EventHandler(this.StudentManage_Click);
            // 
            // teacherManageToolStripMenuItem
            // 
            this.teacherManageToolStripMenuItem.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.teacherManageToolStripMenuItem.Name = "teacherManageToolStripMenuItem";
            this.teacherManageToolStripMenuItem.Size = new System.Drawing.Size(68, 21);
            this.teacherManageToolStripMenuItem.Text = "教师管理";
            this.teacherManageToolStripMenuItem.Click += new System.EventHandler(this.TeacherManage_Click);
            // 
            // classManageToolStripMenuItem
            // 
            this.classManageToolStripMenuItem.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.classManageToolStripMenuItem.Name = "classManageToolStripMenuItem";
            this.classManageToolStripMenuItem.Size = new System.Drawing.Size(68, 21);
            this.classManageToolStripMenuItem.Text = "班级管理";
            this.classManageToolStripMenuItem.Click += new System.EventHandler(this.ClassManage_Click);
            // 
            // borrowCardManageToolStripMenuItem
            // 
            this.borrowCardManageToolStripMenuItem.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.borrowCardManageToolStripMenuItem.Name = "borrowCardManageToolStripMenuItem";
            this.borrowCardManageToolStripMenuItem.Size = new System.Drawing.Size(80, 21);
            this.borrowCardManageToolStripMenuItem.Text = "借阅卡管理";
            this.borrowCardManageToolStripMenuItem.Click += new System.EventHandler(this.BorrowCardManage_Click);
            // 
            // borrowQueryToolStripMenuItem
            // 
            this.borrowQueryToolStripMenuItem.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.borrowQueryToolStripMenuItem.Name = "borrowQueryToolStripMenuItem";
            this.borrowQueryToolStripMenuItem.Size = new System.Drawing.Size(68, 21);
            this.borrowQueryToolStripMenuItem.Text = "借阅查询";
            this.borrowQueryToolStripMenuItem.Click += new System.EventHandler(this.BorrowQuery_Click);
            // 
            // btnOverdueBooks
            // 
            this.btnOverdueBooks.BackColor = System.Drawing.SystemColors.HotTrack;
            this.btnOverdueBooks.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.btnOverdueBooks.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.btnOverdueBooks.Location = new System.Drawing.Point(361, 130);
            this.btnOverdueBooks.Name = "btnOverdueBooks";
            this.btnOverdueBooks.Size = new System.Drawing.Size(140, 50);
            this.btnOverdueBooks.TabIndex = 1;
            this.btnOverdueBooks.Text = "超期图书";
            this.btnOverdueBooks.UseVisualStyleBackColor = false;
            this.btnOverdueBooks.Click += new System.EventHandler(this.BtnOverdueBooks_Click);
            // 
            // btnFineManage
            // 
            this.btnFineManage.BackColor = System.Drawing.SystemColors.HotTrack;
            this.btnFineManage.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.btnFineManage.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.btnFineManage.Location = new System.Drawing.Point(621, 130);
            this.btnFineManage.Name = "btnFineManage";
            this.btnFineManage.Size = new System.Drawing.Size(140, 50);
            this.btnFineManage.TabIndex = 2;
            this.btnFineManage.Text = "罚款管理";
            this.btnFineManage.UseVisualStyleBackColor = false;
            this.btnFineManage.Click += new System.EventHandler(this.BtnFineManage_Click);
            // 
            // btnCirculationStats
            // 
            this.btnCirculationStats.BackColor = System.Drawing.SystemColors.HotTrack;
            this.btnCirculationStats.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.btnCirculationStats.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.btnCirculationStats.Location = new System.Drawing.Point(361, 245);
            this.btnCirculationStats.Name = "btnCirculationStats";
            this.btnCirculationStats.Size = new System.Drawing.Size(140, 50);
            this.btnCirculationStats.TabIndex = 3;
            this.btnCirculationStats.Text = "流通统计";
            this.btnCirculationStats.UseVisualStyleBackColor = false;
            this.btnCirculationStats.Click += new System.EventHandler(this.BtnCirculationStats_Click);
            // 
            // btnCollectionStats
            // 
            this.btnCollectionStats.BackColor = System.Drawing.SystemColors.HotTrack;
            this.btnCollectionStats.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.btnCollectionStats.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.btnCollectionStats.Location = new System.Drawing.Point(621, 245);
            this.btnCollectionStats.Name = "btnCollectionStats";
            this.btnCollectionStats.Size = new System.Drawing.Size(140, 50);
            this.btnCollectionStats.TabIndex = 4;
            this.btnCollectionStats.Text = "馆藏统计";
            this.btnCollectionStats.UseVisualStyleBackColor = false;
            this.btnCollectionStats.Click += new System.EventHandler(this.BtnCollectionStats_Click);
            // 
            // btnExcellentReader
            // 
            this.btnExcellentReader.BackColor = System.Drawing.SystemColors.HotTrack;
            this.btnExcellentReader.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.btnExcellentReader.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.btnExcellentReader.Location = new System.Drawing.Point(361, 366);
            this.btnExcellentReader.Name = "btnExcellentReader";
            this.btnExcellentReader.Size = new System.Drawing.Size(140, 50);
            this.btnExcellentReader.TabIndex = 5;
            this.btnExcellentReader.Text = "优秀读者";
            this.btnExcellentReader.UseVisualStyleBackColor = false;
            this.btnExcellentReader.Click += new System.EventHandler(this.BtnExcellentReader_Click);
            // 
            // panel1
            // 
            this.panel1.BackColor = System.Drawing.Color.White;
            this.panel1.Controls.Add(this.ADMIN);
            this.panel1.Controls.Add(this.label6);
            this.panel1.Controls.Add(this.label5);
            this.panel1.Controls.Add(this.label4);
            this.panel1.Controls.Add(this.label3);
            this.panel1.Controls.Add(this.label2);
            this.panel1.Controls.Add(this.label1);
            this.panel1.Controls.Add(this.lblAccount);
            this.panel1.Controls.Add(this.pictureBox7);
            this.panel1.Controls.Add(this.pictureBox6);
            this.panel1.Controls.Add(this.button1);
            this.panel1.Controls.Add(this.pictureBox5);
            this.panel1.Controls.Add(this.pictureBox4);
            this.panel1.Controls.Add(this.pictureBox3);
            this.panel1.Controls.Add(this.pictureBox2);
            this.panel1.Controls.Add(this.pictureBox1);
            this.panel1.Controls.Add(this.panel2);
            this.panel1.Controls.Add(this.btnOverdueBooks);
            this.panel1.Controls.Add(this.btnFineManage);
            this.panel1.Controls.Add(this.btnCollectionStats);
            this.panel1.Controls.Add(this.btnExcellentReader);
            this.panel1.Controls.Add(this.btnCirculationStats);
            this.panel1.Location = new System.Drawing.Point(12, 12);
            this.panel1.Name = "panel1";
            this.panel1.Size = new System.Drawing.Size(796, 545);
            this.panel1.TabIndex = 6;
            // 
            // panel2
            // 
            this.panel2.BackColor = System.Drawing.SystemColors.HotTrack;
            this.panel2.Controls.Add(this.menuStrip1);
            this.panel2.Location = new System.Drawing.Point(0, -13);
            this.panel2.Name = "panel2";
            this.panel2.Size = new System.Drawing.Size(796, 48);
            this.panel2.TabIndex = 6;
            // 
            // pictureBox1
            // 
            this.pictureBox1.Image = ((System.Drawing.Image)(resources.GetObject("pictureBox1.Image")));
            this.pictureBox1.Location = new System.Drawing.Point(290, 114);
            this.pictureBox1.Name = "pictureBox1";
            this.pictureBox1.Size = new System.Drawing.Size(65, 72);
            this.pictureBox1.SizeMode = System.Windows.Forms.PictureBoxSizeMode.Zoom;
            this.pictureBox1.TabIndex = 9;
            this.pictureBox1.TabStop = false;
            // 
            // pictureBox2
            // 
            this.pictureBox2.Image = ((System.Drawing.Image)(resources.GetObject("pictureBox2.Image")));
            this.pictureBox2.Location = new System.Drawing.Point(550, 114);
            this.pictureBox2.Name = "pictureBox2";
            this.pictureBox2.Size = new System.Drawing.Size(65, 72);
            this.pictureBox2.SizeMode = System.Windows.Forms.PictureBoxSizeMode.Zoom;
            this.pictureBox2.TabIndex = 10;
            this.pictureBox2.TabStop = false;
            // 
            // pictureBox3
            // 
            this.pictureBox3.Image = ((System.Drawing.Image)(resources.GetObject("pictureBox3.Image")));
            this.pictureBox3.Location = new System.Drawing.Point(290, 234);
            this.pictureBox3.Name = "pictureBox3";
            this.pictureBox3.Size = new System.Drawing.Size(65, 72);
            this.pictureBox3.SizeMode = System.Windows.Forms.PictureBoxSizeMode.Zoom;
            this.pictureBox3.TabIndex = 11;
            this.pictureBox3.TabStop = false;
            // 
            // pictureBox4
            // 
            this.pictureBox4.Image = ((System.Drawing.Image)(resources.GetObject("pictureBox4.Image")));
            this.pictureBox4.Location = new System.Drawing.Point(550, 234);
            this.pictureBox4.Name = "pictureBox4";
            this.pictureBox4.Size = new System.Drawing.Size(65, 72);
            this.pictureBox4.SizeMode = System.Windows.Forms.PictureBoxSizeMode.Zoom;
            this.pictureBox4.TabIndex = 12;
            this.pictureBox4.TabStop = false;
            // 
            // pictureBox5
            // 
            this.pictureBox5.Image = ((System.Drawing.Image)(resources.GetObject("pictureBox5.Image")));
            this.pictureBox5.Location = new System.Drawing.Point(290, 355);
            this.pictureBox5.Name = "pictureBox5";
            this.pictureBox5.Size = new System.Drawing.Size(65, 72);
            this.pictureBox5.SizeMode = System.Windows.Forms.PictureBoxSizeMode.Zoom;
            this.pictureBox5.TabIndex = 13;
            this.pictureBox5.TabStop = false;
            // 
            // button1
            // 
            this.button1.BackColor = System.Drawing.SystemColors.HotTrack;
            this.button1.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.button1.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.button1.Location = new System.Drawing.Point(621, 366);
            this.button1.Name = "button1";
            this.button1.Size = new System.Drawing.Size(140, 50);
            this.button1.TabIndex = 14;
            this.button1.Text = "退出登录";
            this.button1.UseVisualStyleBackColor = false;
            this.button1.Click += new System.EventHandler(this.button1_Click);
            // 
            // pictureBox6
            // 
            this.pictureBox6.Image = ((System.Drawing.Image)(resources.GetObject("pictureBox6.Image")));
            this.pictureBox6.Location = new System.Drawing.Point(550, 355);
            this.pictureBox6.Name = "pictureBox6";
            this.pictureBox6.Size = new System.Drawing.Size(65, 72);
            this.pictureBox6.SizeMode = System.Windows.Forms.PictureBoxSizeMode.Zoom;
            this.pictureBox6.TabIndex = 15;
            this.pictureBox6.TabStop = false;
            this.pictureBox6.Click += new System.EventHandler(this.pictureBox6_Click);
            // 
            // pictureBox7
            // 
            this.pictureBox7.Image = ((System.Drawing.Image)(resources.GetObject("pictureBox7.Image")));
            this.pictureBox7.Location = new System.Drawing.Point(19, 123);
            this.pictureBox7.Name = "pictureBox7";
            this.pictureBox7.Size = new System.Drawing.Size(70, 57);
            this.pictureBox7.SizeMode = System.Windows.Forms.PictureBoxSizeMode.Zoom;
            this.pictureBox7.TabIndex = 16;
            this.pictureBox7.TabStop = false;
            // 
            // lblAccount
            // 
            this.lblAccount.AutoSize = true;
            this.lblAccount.Font = new System.Drawing.Font("宋体", 14F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.lblAccount.Location = new System.Drawing.Point(12, 200);
            this.lblAccount.Name = "lblAccount";
            this.lblAccount.Size = new System.Drawing.Size(180, 19);
            this.lblAccount.TabIndex = 17;
            this.lblAccount.Text = "图书馆管理员准则：";
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Font = new System.Drawing.Font("宋体", 10.5F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.label1.Location = new System.Drawing.Point(25, 227);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(154, 14);
            this.label1.TabIndex = 18;
            this.label1.Text = "1. 维护良好阅读环境。";
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Font = new System.Drawing.Font("宋体", 10.5F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.label2.Location = new System.Drawing.Point(25, 251);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(224, 14);
            this.label2.TabIndex = 19;
            this.label2.Text = "2. 准确办理手续，确保信息无误。";
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Font = new System.Drawing.Font("宋体", 10.5F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.label3.Location = new System.Drawing.Point(25, 275);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(224, 14);
            this.label3.TabIndex = 20;
            this.label3.Text = "3. 定期检查图书，及时修补损坏。";
            // 
            // label4
            // 
            this.label4.AutoSize = true;
            this.label4.Font = new System.Drawing.Font("宋体", 10.5F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.label4.Location = new System.Drawing.Point(25, 299);
            this.label4.Name = "label4";
            this.label4.Size = new System.Drawing.Size(154, 14);
            this.label4.TabIndex = 21;
            this.label4.Text = "4. 积极解答读者咨询。";
            // 
            // label5
            // 
            this.label5.AutoSize = true;
            this.label5.Font = new System.Drawing.Font("宋体", 10.5F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.label5.Location = new System.Drawing.Point(25, 323);
            this.label5.Name = "label5";
            this.label5.Size = new System.Drawing.Size(154, 14);
            this.label5.TabIndex = 22;
            this.label5.Text = "5. 严格遵守开放时间。";
            // 
            // label6
            // 
            this.label6.AutoSize = true;
            this.label6.Font = new System.Drawing.Font("宋体", 10.5F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.label6.Location = new System.Drawing.Point(25, 347);
            this.label6.Name = "label6";
            this.label6.Size = new System.Drawing.Size(238, 14);
            this.label6.TabIndex = 23;
            this.label6.Text = "6. 保护读者隐私，不泄露个人信息。";
            // 
            // ADMIN
            // 
            this.ADMIN.AutoSize = true;
            this.ADMIN.Font = new System.Drawing.Font("宋体", 21.75F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.ADMIN.Location = new System.Drawing.Point(95, 136);
            this.ADMIN.Name = "ADMIN";
            this.ADMIN.Size = new System.Drawing.Size(100, 29);
            this.ADMIN.TabIndex = 24;
            this.ADMIN.Text = "管理员";
            // 
            // AdminMainForm
            // 
            this.BackColor = System.Drawing.SystemColors.HotTrack;
            this.ClientSize = new System.Drawing.Size(820, 569);
            this.Controls.Add(this.panel1);
            this.MainMenuStrip = this.menuStrip1;
            this.Name = "AdminMainForm";
            this.Text = "图书馆管理系统 - 管理员";
            this.WindowState = System.Windows.Forms.FormWindowState.Maximized;
            this.Load += new System.EventHandler(this.AdminMainForm_Load);
            this.menuStrip1.ResumeLayout(false);
            this.menuStrip1.PerformLayout();
            this.panel1.ResumeLayout(false);
            this.panel1.PerformLayout();
            this.panel2.ResumeLayout(false);
            this.panel2.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox1)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox2)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox3)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox4)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox5)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox6)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox7)).EndInit();
            this.ResumeLayout(false);

    }

    private MenuStrip menuStrip1;
    private ToolStripMenuItem readerManageToolStripMenuItem;
    private ToolStripMenuItem bookManageToolStripMenuItem;
    private ToolStripMenuItem studentManageToolStripMenuItem;
    private ToolStripMenuItem teacherManageToolStripMenuItem;
    private ToolStripMenuItem classManageToolStripMenuItem;
    private ToolStripMenuItem borrowCardManageToolStripMenuItem;
    private ToolStripMenuItem borrowQueryToolStripMenuItem;
    private Button btnOverdueBooks;
    private Button btnFineManage;
    private Button btnCirculationStats;
    private Button btnCollectionStats;
    private Panel panel1;
    private Panel panel2;
    private PictureBox pictureBox5;
    private PictureBox pictureBox4;
    private PictureBox pictureBox3;
    private PictureBox pictureBox2;
    private PictureBox pictureBox1;
    private PictureBox pictureBox7;
    private PictureBox pictureBox6;
    private Button button1;
    private Label label6;
    private Label label5;
    private Label label4;
    private Label label3;
    private Label label2;
    private Label label1;
    private Label lblAccount;
    private Label ADMIN;
    private Button btnExcellentReader;

    private void ReaderManage_Click(object sender, EventArgs e)
    {
        ReaderManageForm form = new ReaderManageForm();
        form.ShowDialog();
    }

    private void BookManage_Click(object sender, EventArgs e)
    {
        BookManageForm form = new BookManageForm();
        form.ShowDialog();
    }

    private void StudentManage_Click(object sender, EventArgs e)
    {
        StudentManageForm form = new StudentManageForm();
        form.ShowDialog();
    }

    private void TeacherManage_Click(object sender, EventArgs e)
    {
        TeacherManageForm form = new TeacherManageForm();
        form.ShowDialog();
    }

    private void ClassManage_Click(object sender, EventArgs e)
    {
        ClassManageForm form = new ClassManageForm();
        form.ShowDialog();
    }

    private void BorrowCardManage_Click(object sender, EventArgs e)
    {
        BorrowCardManageForm form = new BorrowCardManageForm();
        form.ShowDialog();
    }

    private void BorrowQuery_Click(object sender, EventArgs e)
    {
        BorrowQueryForm form = new BorrowQueryForm();
        form.ShowDialog();
    }

    private void BtnOverdueBooks_Click(object sender, EventArgs e)
    {
        OverdueBooksForm form = new OverdueBooksForm();
        form.ShowDialog();
    }

    private void BtnFineManage_Click(object sender, EventArgs e)
    {
        FineManageForm form = new FineManageForm();
        form.ShowDialog();
    }

    private void BtnCirculationStats_Click(object sender, EventArgs e)
    {
        CirculationStatsForm form = new CirculationStatsForm();
        form.ShowDialog();
    }

    private void BtnCollectionStats_Click(object sender, EventArgs e)
    {
        CollectionStatsForm form = new CollectionStatsForm();
        form.ShowDialog();
    }

    private void BtnExcellentReader_Click(object sender, EventArgs e)
    {
        ExcellentReaderForm form = new ExcellentReaderForm();
        form.ShowDialog();
    }

    private void button1_Click(object sender, EventArgs e)
    {
        LoginForm loginForm = new LoginForm();
        loginForm.Show();
        this.Hide();
    }

    private void AdminMainForm_Load(object sender, EventArgs e)
    {
        ADMIN.Text = LoginForm.AdminName;
    }

    private void pictureBox6_Click(object sender, EventArgs e)
    {

    }
}