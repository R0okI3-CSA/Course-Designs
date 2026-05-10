using System;
using System.Data;
using System.Drawing;
using System.Windows.Forms;

public partial class StudentManageForm : Form
{
    private DataGridView dgvStudent;
    private TextBox txtStudentID;
    private TextBox txtReaderID;
    private TextBox txtClassID;
    private TextBox txtMajor;
    private TextBox txtGrade;
    private Button btnAdd;
    private Button btnEdit;
    private Button btnDelete;
    private Button btnReset;
    private Button btnSearch;
    private Button btnClearFilter;
    private TextBox txtSearch;
    private ComboBox cmbSearchType;
    private Label lblStudentID;
    private Label lblReaderID;
    private Label lblClassID;
    private Label lblMajor;
    private Label lblGrade;
    private GroupBox groupBox1;
    private GroupBox groupBox2;

    public StudentManageForm()
    {
        InitializeComponent();
        LoadStudentData();
        InitializeSearchTypes();
    }

    private void InitializeComponent()
    {
            this.dgvStudent = new System.Windows.Forms.DataGridView();
            this.txtStudentID = new System.Windows.Forms.TextBox();
            this.txtReaderID = new System.Windows.Forms.TextBox();
            this.txtClassID = new System.Windows.Forms.TextBox();
            this.txtMajor = new System.Windows.Forms.TextBox();
            this.txtGrade = new System.Windows.Forms.TextBox();
            this.btnAdd = new System.Windows.Forms.Button();
            this.btnEdit = new System.Windows.Forms.Button();
            this.btnDelete = new System.Windows.Forms.Button();
            this.btnReset = new System.Windows.Forms.Button();
            this.btnSearch = new System.Windows.Forms.Button();
            this.btnClearFilter = new System.Windows.Forms.Button();
            this.txtSearch = new System.Windows.Forms.TextBox();
            this.cmbSearchType = new System.Windows.Forms.ComboBox();
            this.lblStudentID = new System.Windows.Forms.Label();
            this.lblReaderID = new System.Windows.Forms.Label();
            this.lblClassID = new System.Windows.Forms.Label();
            this.lblMajor = new System.Windows.Forms.Label();
            this.lblGrade = new System.Windows.Forms.Label();
            this.groupBox1 = new System.Windows.Forms.GroupBox();
            this.groupBox2 = new System.Windows.Forms.GroupBox();
            ((System.ComponentModel.ISupportInitialize)(this.dgvStudent)).BeginInit();
            this.groupBox1.SuspendLayout();
            this.groupBox2.SuspendLayout();
            this.SuspendLayout();
            // 
            // dgvStudent
            // 
            this.dgvStudent.AllowUserToAddRows = false;
            this.dgvStudent.AutoSizeColumnsMode = System.Windows.Forms.DataGridViewAutoSizeColumnsMode.Fill;
            this.dgvStudent.BackgroundColor = System.Drawing.Color.LightGray;
            this.dgvStudent.ColumnHeadersHeight = 34;
            this.dgvStudent.Location = new System.Drawing.Point(12, 250);
            this.dgvStudent.Name = "dgvStudent";
            this.dgvStudent.ReadOnly = true;
            this.dgvStudent.RowHeadersWidth = 62;
            this.dgvStudent.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
            this.dgvStudent.Size = new System.Drawing.Size(960, 300);
            this.dgvStudent.TabIndex = 2;
            this.dgvStudent.CellClick += new System.Windows.Forms.DataGridViewCellEventHandler(this.DgvStudent_CellClick);
            // 
            // txtStudentID
            // 
            this.txtStudentID.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.txtStudentID.Location = new System.Drawing.Point(100, 27);
            this.txtStudentID.Name = "txtStudentID";
            this.txtStudentID.Size = new System.Drawing.Size(150, 26);
            this.txtStudentID.TabIndex = 1;
            // 
            // txtReaderID
            // 
            this.txtReaderID.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.txtReaderID.Location = new System.Drawing.Point(374, 27);
            this.txtReaderID.Name = "txtReaderID";
            this.txtReaderID.Size = new System.Drawing.Size(150, 26);
            this.txtReaderID.TabIndex = 3;
            // 
            // txtClassID
            // 
            this.txtClassID.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.txtClassID.Location = new System.Drawing.Point(646, 27);
            this.txtClassID.Name = "txtClassID";
            this.txtClassID.Size = new System.Drawing.Size(150, 26);
            this.txtClassID.TabIndex = 5;
            // 
            // txtMajor
            // 
            this.txtMajor.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.txtMajor.Location = new System.Drawing.Point(100, 77);
            this.txtMajor.Name = "txtMajor";
            this.txtMajor.Size = new System.Drawing.Size(150, 26);
            this.txtMajor.TabIndex = 7;
            // 
            // txtGrade
            // 
            this.txtGrade.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.txtGrade.Location = new System.Drawing.Point(374, 77);
            this.txtGrade.Name = "txtGrade";
            this.txtGrade.Size = new System.Drawing.Size(150, 26);
            this.txtGrade.TabIndex = 9;
            // 
            // btnAdd
            // 
            this.btnAdd.BackColor = System.Drawing.SystemColors.HotTrack;
            this.btnAdd.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.btnAdd.Location = new System.Drawing.Point(119, 130);
            this.btnAdd.Name = "btnAdd";
            this.btnAdd.Size = new System.Drawing.Size(120, 30);
            this.btnAdd.TabIndex = 10;
            this.btnAdd.Text = "添加";
            this.btnAdd.UseVisualStyleBackColor = false;
            this.btnAdd.Click += new System.EventHandler(this.BtnAdd_Click);
            // 
            // btnEdit
            // 
            this.btnEdit.BackColor = System.Drawing.SystemColors.HotTrack;
            this.btnEdit.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.btnEdit.Location = new System.Drawing.Point(319, 130);
            this.btnEdit.Name = "btnEdit";
            this.btnEdit.Size = new System.Drawing.Size(120, 30);
            this.btnEdit.TabIndex = 11;
            this.btnEdit.Text = "修改";
            this.btnEdit.UseVisualStyleBackColor = false;
            this.btnEdit.Click += new System.EventHandler(this.BtnEdit_Click);
            // 
            // btnDelete
            // 
            this.btnDelete.BackColor = System.Drawing.SystemColors.HotTrack;
            this.btnDelete.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.btnDelete.Location = new System.Drawing.Point(519, 130);
            this.btnDelete.Name = "btnDelete";
            this.btnDelete.Size = new System.Drawing.Size(120, 30);
            this.btnDelete.TabIndex = 12;
            this.btnDelete.Text = "删除";
            this.btnDelete.UseVisualStyleBackColor = false;
            this.btnDelete.Click += new System.EventHandler(this.BtnDelete_Click);
            // 
            // btnReset
            // 
            this.btnReset.BackColor = System.Drawing.SystemColors.HotTrack;
            this.btnReset.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.btnReset.Location = new System.Drawing.Point(719, 130);
            this.btnReset.Name = "btnReset";
            this.btnReset.Size = new System.Drawing.Size(120, 30);
            this.btnReset.TabIndex = 13;
            this.btnReset.Text = "重置";
            this.btnReset.UseVisualStyleBackColor = false;
            this.btnReset.Click += new System.EventHandler(this.BtnReset_Click);
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
            // lblStudentID
            // 
            this.lblStudentID.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.lblStudentID.Location = new System.Drawing.Point(20, 30);
            this.lblStudentID.Name = "lblStudentID";
            this.lblStudentID.Size = new System.Drawing.Size(74, 23);
            this.lblStudentID.TabIndex = 0;
            this.lblStudentID.Text = "学号：";
            // 
            // lblReaderID
            // 
            this.lblReaderID.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.lblReaderID.Location = new System.Drawing.Point(271, 30);
            this.lblReaderID.Name = "lblReaderID";
            this.lblReaderID.Size = new System.Drawing.Size(100, 23);
            this.lblReaderID.TabIndex = 2;
            this.lblReaderID.Text = "读者编号：";
            // 
            // lblClassID
            // 
            this.lblClassID.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.lblClassID.Location = new System.Drawing.Point(544, 30);
            this.lblClassID.Name = "lblClassID";
            this.lblClassID.Size = new System.Drawing.Size(100, 23);
            this.lblClassID.TabIndex = 4;
            this.lblClassID.Text = "班级编号：";
            // 
            // lblMajor
            // 
            this.lblMajor.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.lblMajor.Location = new System.Drawing.Point(20, 80);
            this.lblMajor.Name = "lblMajor";
            this.lblMajor.Size = new System.Drawing.Size(74, 23);
            this.lblMajor.TabIndex = 6;
            this.lblMajor.Text = "专业：";
            // 
            // lblGrade
            // 
            this.lblGrade.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.lblGrade.Location = new System.Drawing.Point(271, 80);
            this.lblGrade.Name = "lblGrade";
            this.lblGrade.Size = new System.Drawing.Size(100, 23);
            this.lblGrade.TabIndex = 8;
            this.lblGrade.Text = "年级：";
            // 
            // groupBox1
            // 
            this.groupBox1.BackColor = System.Drawing.Color.White;
            this.groupBox1.Controls.Add(this.lblStudentID);
            this.groupBox1.Controls.Add(this.txtStudentID);
            this.groupBox1.Controls.Add(this.lblReaderID);
            this.groupBox1.Controls.Add(this.txtReaderID);
            this.groupBox1.Controls.Add(this.lblClassID);
            this.groupBox1.Controls.Add(this.txtClassID);
            this.groupBox1.Controls.Add(this.lblMajor);
            this.groupBox1.Controls.Add(this.txtMajor);
            this.groupBox1.Controls.Add(this.lblGrade);
            this.groupBox1.Controls.Add(this.txtGrade);
            this.groupBox1.Controls.Add(this.btnAdd);
            this.groupBox1.Controls.Add(this.btnEdit);
            this.groupBox1.Controls.Add(this.btnDelete);
            this.groupBox1.Controls.Add(this.btnReset);
            this.groupBox1.Location = new System.Drawing.Point(12, 12);
            this.groupBox1.Name = "groupBox1";
            this.groupBox1.Size = new System.Drawing.Size(960, 180);
            this.groupBox1.TabIndex = 0;
            this.groupBox1.TabStop = false;
            this.groupBox1.Text = "学生信息";
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
            this.groupBox2.Size = new System.Drawing.Size(960, 45);
            this.groupBox2.TabIndex = 1;
            this.groupBox2.TabStop = false;
            this.groupBox2.Text = "搜索";
            // 
            // StudentManageForm
            // 
            this.BackColor = System.Drawing.SystemColors.HotTrack;
            this.ClientSize = new System.Drawing.Size(978, 558);
            this.Controls.Add(this.groupBox1);
            this.Controls.Add(this.groupBox2);
            this.Controls.Add(this.dgvStudent);
            this.Name = "StudentManageForm";
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Text = "学生管理";
            ((System.ComponentModel.ISupportInitialize)(this.dgvStudent)).EndInit();
            this.groupBox1.ResumeLayout(false);
            this.groupBox1.PerformLayout();
            this.groupBox2.ResumeLayout(false);
            this.groupBox2.PerformLayout();
            this.ResumeLayout(false);

    }

    private void InitializeSearchTypes()
    {
        cmbSearchType.Items.AddRange(new string[] {
            "学号",
            "读者编号",
            "班级编号",
            "专业",
            "年级"
        });
        cmbSearchType.SelectedIndex = 0;
    }

    private void LoadStudentData(string searchCondition = "")
    {
        string sql = @"SELECT s.*, r.ReaderName, c.ClassName 
                      FROM Student s 
                      LEFT JOIN Reader r ON s.ReaderID = r.ReaderID
                      LEFT JOIN Class c ON s.ClassID = c.ClassID";
        if (!string.IsNullOrEmpty(searchCondition))
        {
            sql += " WHERE " + searchCondition;
        }
        dgvStudent.DataSource = DBHelper.ExecuteQuery(sql);
    }

    private void BtnAdd_Click(object sender, EventArgs e)
    {
        if (ValidateInput())
        {
            string sql = $@"INSERT INTO Student VALUES (
                '{txtStudentID.Text}',
                '{txtReaderID.Text}',
                '{txtClassID.Text}',
                '{txtMajor.Text}',
                '{txtGrade.Text}'
            )";

            try
            {
                DBHelper.ExecuteNonQuery(sql);
                MessageBox.Show("添加成功！");
                LoadStudentData();
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
            string sql = $@"UPDATE Student SET 
                ReaderID = '{txtReaderID.Text}',
                ClassID = '{txtClassID.Text}',
                Major = '{txtMajor.Text}',
                Grade = '{txtGrade.Text}'
                WHERE StudentID = '{txtStudentID.Text}'";

            try
            {
                DBHelper.ExecuteNonQuery(sql);
                MessageBox.Show("修改成功！");
                LoadStudentData();
            }
            catch (Exception ex)
            {
                MessageBox.Show("修改失败：" + ex.Message);
            }
        }
    }

    private void BtnDelete_Click(object sender, EventArgs e)
    {
        if (string.IsNullOrEmpty(txtStudentID.Text))
        {
            MessageBox.Show("请先选择要删除的学生！");
            return;
        }

        if (MessageBox.Show("确定要删除该学生信息吗？", "确认删除", 
            MessageBoxButtons.YesNo) == DialogResult.Yes)
        {
            string sql = $"DELETE FROM Student WHERE StudentID = '{txtStudentID.Text}'";

            try
            {
                DBHelper.ExecuteNonQuery(sql);
                MessageBox.Show("删除成功！");
                LoadStudentData();
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
            case "学号":
                condition = $"s.StudentID LIKE '%{txtSearch.Text}%'";
                break;
            case "读者编号":
                condition = $"s.ReaderID LIKE '%{txtSearch.Text}%'";
                break;
            case "班级编号":
                condition = $"s.ClassID LIKE '%{txtSearch.Text}%'";
                break;
            case "专业":
                condition = $"s.Major LIKE '%{txtSearch.Text}%'";
                break;
            case "年级":
                condition = $"s.Grade LIKE '%{txtSearch.Text}%'";
                break;
        }
        LoadStudentData(condition);
    }

    private void DgvStudent_CellClick(object sender, DataGridViewCellEventArgs e)
    {
        if (e.RowIndex >= 0)
        {
            DataGridViewRow row = dgvStudent.Rows[e.RowIndex];
            txtStudentID.Text = row.Cells["StudentID"].Value.ToString();
            txtReaderID.Text = row.Cells["ReaderID"].Value.ToString();
            txtClassID.Text = row.Cells["ClassID"].Value.ToString();
            txtMajor.Text = row.Cells["Major"].Value.ToString();
            txtGrade.Text = row.Cells["Grade"].Value.ToString();
        }
    }

    private bool ValidateInput()
    {
        if (string.IsNullOrEmpty(txtStudentID.Text))
        {
            MessageBox.Show("学号不能为空！");
            return false;
        }
        if (string.IsNullOrEmpty(txtReaderID.Text))
        {
            MessageBox.Show("读者编号不能为空！");
            return false;
        }
        if (string.IsNullOrEmpty(txtClassID.Text))
        {
            MessageBox.Show("班级编号不能为空！");
            return false;
        }
        if (string.IsNullOrEmpty(txtMajor.Text))
        {
            MessageBox.Show("专业不能为空！");
            return false;
        }
        if (string.IsNullOrEmpty(txtGrade.Text))
        {
            MessageBox.Show("年级不能为空！");
            return false;
        }
        return true;
    }

    private void ClearInputs()
    {
        txtStudentID.Clear();
        txtReaderID.Clear();
        txtClassID.Clear();
        txtMajor.Clear();
        txtGrade.Clear();
    }

    private void BtnReset_Click(object sender, EventArgs e)
    {
        ClearInputs();
    }

    private void BtnClearFilter_Click(object sender, EventArgs e)
    {
        txtSearch.Clear();
        cmbSearchType.SelectedIndex = 0;
        LoadStudentData();
    }
} 