using System;
using System.Data;
using System.Drawing;
using System.Windows.Forms;

public partial class ReaderManageForm : Form
{
    private DataGridView dgvReader;
    private TextBox txtReaderID;
    private TextBox txtReaderName;
    private ComboBox cmbGender;
    private TextBox txtPhone;
    private TextBox txtEmail;
    private ComboBox cmbReaderType;
    private Button btnAdd;
    private Button btnEdit;
    private Button btnDelete;
    private Button btnSearch;
    private TextBox txtSearch;
    private ComboBox cmbSearchType;
    private Label lblReaderID;
    private Label lblReaderName;
    private Label lblGender;
    private Label lblPhone;
    private Label lblEmail;
    private Label lblReaderType;
    private GroupBox groupBox1;
    private GroupBox groupBox2;
    private Button btnReset;
    private Button btnClearFilter;

    public ReaderManageForm()
    {
        InitializeComponent();
        LoadReaderData();
        InitializeSearchTypes();
    }

    private void InitializeComponent()
    {
            this.dgvReader = new System.Windows.Forms.DataGridView();
            this.txtReaderID = new System.Windows.Forms.TextBox();
            this.txtReaderName = new System.Windows.Forms.TextBox();
            this.cmbGender = new System.Windows.Forms.ComboBox();
            this.txtPhone = new System.Windows.Forms.TextBox();
            this.txtEmail = new System.Windows.Forms.TextBox();
            this.cmbReaderType = new System.Windows.Forms.ComboBox();
            this.btnAdd = new System.Windows.Forms.Button();
            this.btnEdit = new System.Windows.Forms.Button();
            this.btnDelete = new System.Windows.Forms.Button();
            this.btnSearch = new System.Windows.Forms.Button();
            this.txtSearch = new System.Windows.Forms.TextBox();
            this.cmbSearchType = new System.Windows.Forms.ComboBox();
            this.lblReaderID = new System.Windows.Forms.Label();
            this.lblReaderName = new System.Windows.Forms.Label();
            this.lblGender = new System.Windows.Forms.Label();
            this.lblPhone = new System.Windows.Forms.Label();
            this.lblEmail = new System.Windows.Forms.Label();
            this.lblReaderType = new System.Windows.Forms.Label();
            this.groupBox1 = new System.Windows.Forms.GroupBox();
            this.btnReset = new System.Windows.Forms.Button();
            this.groupBox2 = new System.Windows.Forms.GroupBox();
            this.btnClearFilter = new System.Windows.Forms.Button();
            ((System.ComponentModel.ISupportInitialize)(this.dgvReader)).BeginInit();
            this.groupBox1.SuspendLayout();
            this.groupBox2.SuspendLayout();
            this.SuspendLayout();
            // 
            // dgvReader
            // 
            this.dgvReader.AllowUserToAddRows = false;
            this.dgvReader.AutoSizeColumnsMode = System.Windows.Forms.DataGridViewAutoSizeColumnsMode.Fill;
            this.dgvReader.BackgroundColor = System.Drawing.Color.LightGray;
            this.dgvReader.ColumnHeadersHeight = 34;
            this.dgvReader.Location = new System.Drawing.Point(12, 250);
            this.dgvReader.Name = "dgvReader";
            this.dgvReader.ReadOnly = true;
            this.dgvReader.RowHeadersWidth = 62;
            this.dgvReader.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
            this.dgvReader.Size = new System.Drawing.Size(954, 306);
            this.dgvReader.TabIndex = 2;
            this.dgvReader.CellClick += new System.Windows.Forms.DataGridViewCellEventHandler(this.DgvReader_CellClick);
            // 
            // txtReaderID
            // 
            this.txtReaderID.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.txtReaderID.Location = new System.Drawing.Point(126, 27);
            this.txtReaderID.Name = "txtReaderID";
            this.txtReaderID.Size = new System.Drawing.Size(124, 26);
            this.txtReaderID.TabIndex = 1;
            // 
            // txtReaderName
            // 
            this.txtReaderName.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.txtReaderName.Location = new System.Drawing.Point(376, 27);
            this.txtReaderName.Name = "txtReaderName";
            this.txtReaderName.Size = new System.Drawing.Size(124, 26);
            this.txtReaderName.TabIndex = 3;
            // 
            // cmbGender
            // 
            this.cmbGender.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.cmbGender.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.cmbGender.Items.AddRange(new object[] {
            "male",
            "female"});
            this.cmbGender.Location = new System.Drawing.Point(601, 27);
            this.cmbGender.Name = "cmbGender";
            this.cmbGender.Size = new System.Drawing.Size(54, 24);
            this.cmbGender.TabIndex = 5;
            // 
            // txtPhone
            // 
            this.txtPhone.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.txtPhone.Location = new System.Drawing.Point(126, 77);
            this.txtPhone.Name = "txtPhone";
            this.txtPhone.Size = new System.Drawing.Size(124, 26);
            this.txtPhone.TabIndex = 7;
            // 
            // txtEmail
            // 
            this.txtEmail.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.txtEmail.Location = new System.Drawing.Point(376, 77);
            this.txtEmail.Name = "txtEmail";
            this.txtEmail.Size = new System.Drawing.Size(124, 26);
            this.txtEmail.TabIndex = 9;
            // 
            // cmbReaderType
            // 
            this.cmbReaderType.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.cmbReaderType.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.cmbReaderType.Items.AddRange(new object[] {
            "student",
            "teacher",
            "else"});
            this.cmbReaderType.Location = new System.Drawing.Point(644, 77);
            this.cmbReaderType.Name = "cmbReaderType";
            this.cmbReaderType.Size = new System.Drawing.Size(90, 24);
            this.cmbReaderType.TabIndex = 11;
            // 
            // btnAdd
            // 
            this.btnAdd.BackColor = System.Drawing.SystemColors.HotTrack;
            this.btnAdd.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.btnAdd.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.btnAdd.Location = new System.Drawing.Point(118, 130);
            this.btnAdd.Name = "btnAdd";
            this.btnAdd.Size = new System.Drawing.Size(120, 30);
            this.btnAdd.TabIndex = 12;
            this.btnAdd.Text = "添加";
            this.btnAdd.UseVisualStyleBackColor = false;
            this.btnAdd.Click += new System.EventHandler(this.BtnAdd_Click);
            // 
            // btnEdit
            // 
            this.btnEdit.BackColor = System.Drawing.SystemColors.HotTrack;
            this.btnEdit.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.btnEdit.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.btnEdit.Location = new System.Drawing.Point(318, 130);
            this.btnEdit.Name = "btnEdit";
            this.btnEdit.Size = new System.Drawing.Size(120, 30);
            this.btnEdit.TabIndex = 13;
            this.btnEdit.Text = "修改";
            this.btnEdit.UseVisualStyleBackColor = false;
            this.btnEdit.Click += new System.EventHandler(this.BtnEdit_Click);
            // 
            // btnDelete
            // 
            this.btnDelete.BackColor = System.Drawing.SystemColors.HotTrack;
            this.btnDelete.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.btnDelete.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.btnDelete.Location = new System.Drawing.Point(518, 130);
            this.btnDelete.Name = "btnDelete";
            this.btnDelete.Size = new System.Drawing.Size(120, 30);
            this.btnDelete.TabIndex = 14;
            this.btnDelete.Text = "删除";
            this.btnDelete.UseVisualStyleBackColor = false;
            this.btnDelete.Click += new System.EventHandler(this.BtnDelete_Click);
            // 
            // btnSearch
            // 
            this.btnSearch.BackColor = System.Drawing.SystemColors.HotTrack;
            this.btnSearch.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.btnSearch.Location = new System.Drawing.Point(360, 16);
            this.btnSearch.Name = "btnSearch";
            this.btnSearch.Size = new System.Drawing.Size(80, 25);
            this.btnSearch.TabIndex = 2;
            this.btnSearch.Text = "搜索";
            this.btnSearch.UseVisualStyleBackColor = false;
            this.btnSearch.Click += new System.EventHandler(this.BtnSearch_Click);
            // 
            // txtSearch
            // 
            this.txtSearch.Location = new System.Drawing.Point(150, 17);
            this.txtSearch.Name = "txtSearch";
            this.txtSearch.Size = new System.Drawing.Size(200, 21);
            this.txtSearch.TabIndex = 1;
            // 
            // cmbSearchType
            // 
            this.cmbSearchType.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.cmbSearchType.Location = new System.Drawing.Point(20, 17);
            this.cmbSearchType.Name = "cmbSearchType";
            this.cmbSearchType.Size = new System.Drawing.Size(120, 20);
            this.cmbSearchType.TabIndex = 0;
            // 
            // lblReaderID
            // 
            this.lblReaderID.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.lblReaderID.Location = new System.Drawing.Point(20, 30);
            this.lblReaderID.Name = "lblReaderID";
            this.lblReaderID.Size = new System.Drawing.Size(100, 23);
            this.lblReaderID.TabIndex = 0;
            this.lblReaderID.Text = "读者编号：";
            // 
            // lblReaderName
            // 
            this.lblReaderName.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.lblReaderName.Location = new System.Drawing.Point(270, 30);
            this.lblReaderName.Name = "lblReaderName";
            this.lblReaderName.Size = new System.Drawing.Size(100, 23);
            this.lblReaderName.TabIndex = 2;
            this.lblReaderName.Text = "读者姓名：";
            // 
            // lblGender
            // 
            this.lblGender.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.lblGender.Location = new System.Drawing.Point(529, 30);
            this.lblGender.Name = "lblGender";
            this.lblGender.Size = new System.Drawing.Size(66, 23);
            this.lblGender.TabIndex = 4;
            this.lblGender.Text = "性别：";
            // 
            // lblPhone
            // 
            this.lblPhone.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.lblPhone.Location = new System.Drawing.Point(20, 77);
            this.lblPhone.Name = "lblPhone";
            this.lblPhone.Size = new System.Drawing.Size(89, 23);
            this.lblPhone.TabIndex = 6;
            this.lblPhone.Text = "联系电话：";
            // 
            // lblEmail
            // 
            this.lblEmail.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.lblEmail.Location = new System.Drawing.Point(270, 80);
            this.lblEmail.Name = "lblEmail";
            this.lblEmail.Size = new System.Drawing.Size(100, 23);
            this.lblEmail.TabIndex = 8;
            this.lblEmail.Text = "邮箱：";
            // 
            // lblReaderType
            // 
            this.lblReaderType.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.lblReaderType.Location = new System.Drawing.Point(529, 80);
            this.lblReaderType.Name = "lblReaderType";
            this.lblReaderType.Size = new System.Drawing.Size(109, 23);
            this.lblReaderType.TabIndex = 10;
            this.lblReaderType.Text = "读者类型：";
            // 
            // groupBox1
            // 
            this.groupBox1.BackColor = System.Drawing.Color.White;
            this.groupBox1.Controls.Add(this.lblReaderID);
            this.groupBox1.Controls.Add(this.txtReaderID);
            this.groupBox1.Controls.Add(this.lblReaderName);
            this.groupBox1.Controls.Add(this.txtReaderName);
            this.groupBox1.Controls.Add(this.lblGender);
            this.groupBox1.Controls.Add(this.cmbGender);
            this.groupBox1.Controls.Add(this.lblPhone);
            this.groupBox1.Controls.Add(this.txtPhone);
            this.groupBox1.Controls.Add(this.lblEmail);
            this.groupBox1.Controls.Add(this.txtEmail);
            this.groupBox1.Controls.Add(this.lblReaderType);
            this.groupBox1.Controls.Add(this.cmbReaderType);
            this.groupBox1.Controls.Add(this.btnAdd);
            this.groupBox1.Controls.Add(this.btnEdit);
            this.groupBox1.Controls.Add(this.btnDelete);
            this.groupBox1.Controls.Add(this.btnReset);
            this.groupBox1.Location = new System.Drawing.Point(12, 12);
            this.groupBox1.Name = "groupBox1";
            this.groupBox1.Size = new System.Drawing.Size(954, 180);
            this.groupBox1.TabIndex = 0;
            this.groupBox1.TabStop = false;
            this.groupBox1.Text = "读者信息";
            // 
            // btnReset
            // 
            this.btnReset.BackColor = System.Drawing.SystemColors.HotTrack;
            this.btnReset.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.btnReset.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.btnReset.Location = new System.Drawing.Point(718, 130);
            this.btnReset.Name = "btnReset";
            this.btnReset.Size = new System.Drawing.Size(120, 30);
            this.btnReset.TabIndex = 15;
            this.btnReset.Text = "重置";
            this.btnReset.UseVisualStyleBackColor = false;
            this.btnReset.Click += new System.EventHandler(this.BtnReset_Click);
            // 
            // groupBox2
            // 
            this.groupBox2.BackColor = System.Drawing.Color.White;
            this.groupBox2.Controls.Add(this.cmbSearchType);
            this.groupBox2.Controls.Add(this.txtSearch);
            this.groupBox2.Controls.Add(this.btnSearch);
            this.groupBox2.Controls.Add(this.btnClearFilter);
            this.groupBox2.Location = new System.Drawing.Point(12, 200);
            this.groupBox2.Name = "groupBox2";
            this.groupBox2.Size = new System.Drawing.Size(954, 45);
            this.groupBox2.TabIndex = 1;
            this.groupBox2.TabStop = false;
            this.groupBox2.Text = "搜索";
            // 
            // btnClearFilter
            // 
            this.btnClearFilter.BackColor = System.Drawing.SystemColors.HotTrack;
            this.btnClearFilter.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.btnClearFilter.Location = new System.Drawing.Point(450, 16);
            this.btnClearFilter.Name = "btnClearFilter";
            this.btnClearFilter.Size = new System.Drawing.Size(100, 25);
            this.btnClearFilter.TabIndex = 3;
            this.btnClearFilter.Text = "取消筛选";
            this.btnClearFilter.UseVisualStyleBackColor = false;
            this.btnClearFilter.Click += new System.EventHandler(this.BtnClearFilter_Click);
            // 
            // ReaderManageForm
            // 
            this.BackColor = System.Drawing.SystemColors.HotTrack;
            this.ClientSize = new System.Drawing.Size(978, 557);
            this.Controls.Add(this.groupBox1);
            this.Controls.Add(this.groupBox2);
            this.Controls.Add(this.dgvReader);
            this.Name = "ReaderManageForm";
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Text = "读者管理";
            ((System.ComponentModel.ISupportInitialize)(this.dgvReader)).EndInit();
            this.groupBox1.ResumeLayout(false);
            this.groupBox1.PerformLayout();
            this.groupBox2.ResumeLayout(false);
            this.groupBox2.PerformLayout();
            this.ResumeLayout(false);

    }

    private void InitializeSearchTypes()
    {
        cmbSearchType.Items.AddRange(new string[] {
            "读者编号",
            "读者姓名",
            "性别",
            "联系电话",
            "读者类型"
        });
        cmbSearchType.SelectedIndex = 0;
    }

    private void LoadReaderData(string searchCondition = "")
    {
        string sql = "SELECT * FROM Reader";
        if (!string.IsNullOrEmpty(searchCondition))
        {
            sql += " WHERE " + searchCondition;
        }
        dgvReader.DataSource = DBHelper.ExecuteQuery(sql);
    }

    private void BtnAdd_Click(object sender, EventArgs e)
    {
        if (ValidateInput())
        {
            string sql = $@"INSERT INTO Reader VALUES (
                '{txtReaderID.Text}',
                '{txtReaderName.Text}',
                '{cmbGender.Text}',
                '{txtPhone.Text}',
                '{txtEmail.Text}',
                '{cmbReaderType.Text}'
            )";

            try
            {
                DBHelper.ExecuteNonQuery(sql);
                MessageBox.Show("添加成功！");
                LoadReaderData();
                ClearInputs();
            }
            catch (Exception ex)
            {
                MessageBox.Show("添加失败：" + ex.Message);
            }
        }
    }

    private void BtnEdit_Click(object sender, EventArgs e)
    {
        if (ValidateInput())
        {
            string sql = $@"UPDATE Reader SET 
                ReaderName = '{txtReaderName.Text}',
                Gender = '{cmbGender.Text}',
                ContactPhone = '{txtPhone.Text}',
                Email = '{txtEmail.Text}',
                ReaderType = '{cmbReaderType.Text}'
                WHERE ReaderID = '{txtReaderID.Text}'";

            try
            {
                DBHelper.ExecuteNonQuery(sql);
                MessageBox.Show("修改成功！");
                LoadReaderData();
            }
            catch (Exception ex)
            {
                MessageBox.Show("修改失败：" + ex.Message);
            }
        }
    }

    private void BtnDelete_Click(object sender, EventArgs e)
    {
        if (string.IsNullOrEmpty(txtReaderID.Text))
        {
            MessageBox.Show("请先选择要删除的读者！");
            return;
        }

        if (MessageBox.Show("确定要删除该读者信息吗？", "确认删除", 
            MessageBoxButtons.YesNo) == DialogResult.Yes)
        {
            string sql = $"DELETE FROM Reader WHERE ReaderID = '{txtReaderID.Text}'";

            try
            {
                DBHelper.ExecuteNonQuery(sql);
                MessageBox.Show("删除成功！");
                LoadReaderData();
                ClearInputs();
            }
            catch (Exception ex)
            {
                MessageBox.Show("删除失败：" + ex.Message);
            }
        }
    }

    private void BtnSearch_Click(object sender, EventArgs e)
    {
        string condition = "";
        switch (cmbSearchType.Text)
        {
            case "读者编号":
                condition = $"ReaderID LIKE '%{txtSearch.Text}%'";
                break;
            case "读者姓名":
                condition = $"ReaderName LIKE '%{txtSearch.Text}%'";
                break;
            case "性别":
                condition = $"Gender = '{txtSearch.Text}'";
                break;
            case "联系电话":
                condition = $"ContactPhone LIKE '%{txtSearch.Text}%'";
                break;
            case "读者类型":
                condition = $"ReaderType LIKE '%{txtSearch.Text}%'";
                break;
        }
        LoadReaderData(condition);
    }

    private void DgvReader_CellClick(object sender, DataGridViewCellEventArgs e)
    {
        if (e.RowIndex >= 0)
        {
            DataGridViewRow row = dgvReader.Rows[e.RowIndex];
            txtReaderID.Text = row.Cells["ReaderID"].Value.ToString();
            txtReaderName.Text = row.Cells["ReaderName"].Value.ToString();
            cmbGender.Text = row.Cells["Gender"].Value.ToString();
            txtPhone.Text = row.Cells["ContactPhone"].Value.ToString();
            txtEmail.Text = row.Cells["Email"].Value.ToString();
            cmbReaderType.Text = row.Cells["ReaderType"].Value.ToString();
        }
    }

    private bool ValidateInput()
    {
        if (string.IsNullOrEmpty(txtReaderID.Text))
        {
            MessageBox.Show("读者编号不能为空！");
            return false;
        }
        if (string.IsNullOrEmpty(txtReaderName.Text))
        {
            MessageBox.Show("读者姓名不能为空！");
            return false;
        }
        if (string.IsNullOrEmpty(cmbGender.Text))
        {
            MessageBox.Show("请选择性别！");
            return false;
        }
        if (string.IsNullOrEmpty(cmbReaderType.Text))
        {
            MessageBox.Show("请选择读者类型！");
            return false;
        }
        return true;
    }

    private void ClearInputs()
    {
        txtReaderID.Clear();
        txtReaderName.Clear();
        cmbGender.SelectedIndex = -1;
        txtPhone.Clear();
        txtEmail.Clear();
        cmbReaderType.SelectedIndex = -1;
    }

    private void BtnReset_Click(object sender, EventArgs e)
    {
        ClearInputs();
    }

    private void BtnClearFilter_Click(object sender, EventArgs e)
    {
        txtSearch.Clear();
        cmbSearchType.SelectedIndex = 0;
        LoadReaderData();
    }
} 