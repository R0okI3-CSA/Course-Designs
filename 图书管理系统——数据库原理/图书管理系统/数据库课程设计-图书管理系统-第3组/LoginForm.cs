using System;
using System.Data;
using System.Data.SqlClient;
using System.Drawing;
using System.Windows.Forms;

public partial class LoginForm : Form
{
    private Label lblTitle;
    private RadioButton rbtnAdmin;
    private RadioButton rbtnUser;
    private TextBox txtAccount;
    private TextBox txtPassword;
    private Button btnLogin;
    private Label lblAccount;
    private Panel panel1;
    private PictureBox pictureBox1;
    private PictureBox pictureBox2;
    private Label lblPassword;

    public LoginForm()
    {
        InitializeComponent();
    }

    private void InitializeComponent()
    {
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(LoginForm));
            this.lblTitle = new System.Windows.Forms.Label();
            this.rbtnAdmin = new System.Windows.Forms.RadioButton();
            this.rbtnUser = new System.Windows.Forms.RadioButton();
            this.txtAccount = new System.Windows.Forms.TextBox();
            this.txtPassword = new System.Windows.Forms.TextBox();
            this.btnLogin = new System.Windows.Forms.Button();
            this.lblAccount = new System.Windows.Forms.Label();
            this.lblPassword = new System.Windows.Forms.Label();
            this.panel1 = new System.Windows.Forms.Panel();
            this.pictureBox2 = new System.Windows.Forms.PictureBox();
            this.pictureBox1 = new System.Windows.Forms.PictureBox();
            this.panel1.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox2)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox1)).BeginInit();
            this.SuspendLayout();
            // 
            // lblTitle
            // 
            this.lblTitle.AutoSize = true;
            this.lblTitle.BackColor = System.Drawing.Color.White;
            this.lblTitle.Font = new System.Drawing.Font("宋体", 26.25F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.lblTitle.Location = new System.Drawing.Point(694, 90);
            this.lblTitle.Name = "lblTitle";
            this.lblTitle.Size = new System.Drawing.Size(231, 35);
            this.lblTitle.TabIndex = 0;
            this.lblTitle.Text = "图书管理系统";
            this.lblTitle.Click += new System.EventHandler(this.lblTitle_Click);
            // 
            // rbtnAdmin
            // 
            this.rbtnAdmin.AutoSize = true;
            this.rbtnAdmin.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.rbtnAdmin.Location = new System.Drawing.Point(662, 344);
            this.rbtnAdmin.Name = "rbtnAdmin";
            this.rbtnAdmin.Size = new System.Drawing.Size(106, 20);
            this.rbtnAdmin.TabIndex = 1;
            this.rbtnAdmin.Text = "管理员登录";
            // 
            // rbtnUser
            // 
            this.rbtnUser.AutoSize = true;
            this.rbtnUser.Checked = true;
            this.rbtnUser.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.rbtnUser.Location = new System.Drawing.Point(855, 344);
            this.rbtnUser.Name = "rbtnUser";
            this.rbtnUser.Size = new System.Drawing.Size(90, 20);
            this.rbtnUser.TabIndex = 2;
            this.rbtnUser.TabStop = true;
            this.rbtnUser.Text = "用户登录";
            // 
            // txtAccount
            // 
            this.txtAccount.Font = new System.Drawing.Font("宋体", 14F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.txtAccount.Location = new System.Drawing.Point(731, 399);
            this.txtAccount.Name = "txtAccount";
            this.txtAccount.Size = new System.Drawing.Size(250, 29);
            this.txtAccount.TabIndex = 4;
            // 
            // txtPassword
            // 
            this.txtPassword.Font = new System.Drawing.Font("宋体", 14F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.txtPassword.Location = new System.Drawing.Point(731, 468);
            this.txtPassword.Name = "txtPassword";
            this.txtPassword.PasswordChar = '*';
            this.txtPassword.Size = new System.Drawing.Size(250, 29);
            this.txtPassword.TabIndex = 6;
            // 
            // btnLogin
            // 
            this.btnLogin.BackColor = System.Drawing.SystemColors.HotTrack;
            this.btnLogin.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.btnLogin.ForeColor = System.Drawing.SystemColors.HighlightText;
            this.btnLogin.Location = new System.Drawing.Point(731, 546);
            this.btnLogin.Name = "btnLogin";
            this.btnLogin.Size = new System.Drawing.Size(182, 46);
            this.btnLogin.TabIndex = 7;
            this.btnLogin.Text = "登录";
            this.btnLogin.UseVisualStyleBackColor = false;
            this.btnLogin.Click += new System.EventHandler(this.BtnLogin_Click);
            // 
            // lblAccount
            // 
            this.lblAccount.AutoSize = true;
            this.lblAccount.Font = new System.Drawing.Font("宋体", 14F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.lblAccount.Location = new System.Drawing.Point(620, 402);
            this.lblAccount.Name = "lblAccount";
            this.lblAccount.Size = new System.Drawing.Size(66, 19);
            this.lblAccount.TabIndex = 3;
            this.lblAccount.Text = "账户：";
            // 
            // lblPassword
            // 
            this.lblPassword.AutoSize = true;
            this.lblPassword.Font = new System.Drawing.Font("宋体", 14F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.lblPassword.Location = new System.Drawing.Point(620, 471);
            this.lblPassword.Name = "lblPassword";
            this.lblPassword.Size = new System.Drawing.Size(66, 19);
            this.lblPassword.TabIndex = 5;
            this.lblPassword.Text = "密码：";
            // 
            // panel1
            // 
            this.panel1.BackColor = System.Drawing.Color.White;
            this.panel1.Controls.Add(this.lblTitle);
            this.panel1.Controls.Add(this.pictureBox2);
            this.panel1.Controls.Add(this.pictureBox1);
            this.panel1.Controls.Add(this.rbtnUser);
            this.panel1.Controls.Add(this.rbtnAdmin);
            this.panel1.Controls.Add(this.lblPassword);
            this.panel1.Controls.Add(this.lblAccount);
            this.panel1.Controls.Add(this.txtPassword);
            this.panel1.Controls.Add(this.btnLogin);
            this.panel1.Controls.Add(this.txtAccount);
            this.panel1.Location = new System.Drawing.Point(12, 12);
            this.panel1.Name = "panel1";
            this.panel1.Size = new System.Drawing.Size(1033, 668);
            this.panel1.TabIndex = 8;
            // 
            // pictureBox2
            // 
            this.pictureBox2.Image = ((System.Drawing.Image)(resources.GetObject("pictureBox2.Image")));
            this.pictureBox2.Location = new System.Drawing.Point(0, -70);
            this.pictureBox2.Name = "pictureBox2";
            this.pictureBox2.Size = new System.Drawing.Size(574, 805);
            this.pictureBox2.SizeMode = System.Windows.Forms.PictureBoxSizeMode.Zoom;
            this.pictureBox2.TabIndex = 9;
            this.pictureBox2.TabStop = false;
            // 
            // pictureBox1
            // 
            this.pictureBox1.Image = ((System.Drawing.Image)(resources.GetObject("pictureBox1.Image")));
            this.pictureBox1.Location = new System.Drawing.Point(701, 154);
            this.pictureBox1.Name = "pictureBox1";
            this.pictureBox1.Size = new System.Drawing.Size(215, 168);
            this.pictureBox1.SizeMode = System.Windows.Forms.PictureBoxSizeMode.Zoom;
            this.pictureBox1.TabIndex = 8;
            this.pictureBox1.TabStop = false;
            // 
            // LoginForm
            // 
            this.BackColor = System.Drawing.SystemColors.HotTrack;
            this.ClientSize = new System.Drawing.Size(1057, 692);
            this.Controls.Add(this.panel1);
            this.Name = "LoginForm";
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Text = "登录";
            this.panel1.ResumeLayout(false);
            this.panel1.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox2)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox1)).EndInit();
            this.ResumeLayout(false);

    }

    public static string AdminName = "";
    public static string UserName = "";
    private void BtnLogin_Click(object sender, EventArgs e)
    {
        string account = txtAccount.Text.Trim();
        string password = txtPassword.Text.Trim();

        if (rbtnAdmin.Checked)
        {
            // 管理员登录
            string sql = "SELECT COUNT(*) FROM AdminCode WHERE AdminID = @Account AND AdminCode = @Password";
            SqlParameter[] parameters = {
                new SqlParameter("@Account", account),
                new SqlParameter("@Password", password)
            };

            if (Convert.ToInt32(DBHelper.ExecuteScalar(sql, parameters)) > 0)
            {
                AdminName = txtAccount.Text;
                AdminMainForm adminForm = new AdminMainForm();
                adminForm.Show();
                this.Hide();
            }
            else
            {
                MessageBox.Show("账户或密码错误！！！", "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }
        else if (rbtnUser.Checked)
        {
            // 用户登录
            string sql = "SELECT COUNT(*) FROM ReaderCode WHERE ReaderID = @Account AND ReaderCode = @Password";
            SqlParameter[] parameters = {
                new SqlParameter("@Account", account),
                new SqlParameter("@Password", password)
            };

            if (Convert.ToInt32(DBHelper.ExecuteScalar(sql, parameters)) > 0)
            {
                UserName = txtAccount.Text;
                ReaderMainForm readerForm = new ReaderMainForm();
                readerForm.Show();
                this.Hide();
            }
            else
            {
                MessageBox.Show("账户或密码错误！！！", "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }
    }

    private void lblTitle_Click(object sender, EventArgs e)
    {

    }
}