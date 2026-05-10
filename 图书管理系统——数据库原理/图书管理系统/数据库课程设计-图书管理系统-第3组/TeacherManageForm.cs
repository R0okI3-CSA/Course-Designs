using System;
using System.Data;
using System.Drawing;
using System.Windows.Forms;

public partial class TeacherManageForm : Form
{
    private DataGridView dgvTeacher;
    private TextBox txtTeacherID;
    private TextBox txtReaderID;
    private TextBox txtDepartment;
    private TextBox txtTitle;
    private Button btnAdd;
    private Button btnEdit;
    private Button btnDelete;
    private Button btnReset;
    private Button btnSearch;
    private Button btnClearFilter;
    private TextBox txtSearch;
    private ComboBox cmbSearchType;
    private Label lblTeacherID;
    private Label lblReaderID;
    private Label lblDepartment;
    private Label lblTitle;
    private GroupBox groupBox1;
    private GroupBox groupBox2;

    public TeacherManageForm()
    {
        InitializeComponent();
        LoadTeacherData();
        InitializeSearchTypes();
    }

    private void InitializeComponent()
    {
            this.dgvTeacher = new System.Windows.Forms.DataGridView();
            this.txtTeacherID = new System.Windows.Forms.TextBox();
            this.txtReaderID = new System.Windows.Forms.TextBox();
            this.txtDepartment = new System.Windows.Forms.TextBox();
            this.txtTitle = new System.Windows.Forms.TextBox();
            this.btnAdd = new System.Windows.Forms.Button();
            this.btnEdit = new System.Windows.Forms.Button();
            this.btnDelete = new System.Windows.Forms.Button();
            this.btnReset = new System.Windows.Forms.Button();
            this.btnSearch = new System.Windows.Forms.Button();
            this.btnClearFilter = new System.Windows.Forms.Button();
            this.txtSearch = new System.Windows.Forms.TextBox();
            this.cmbSearchType = new System.Windows.Forms.ComboBox();
            this.lblTeacherID = new System.Windows.Forms.Label();
            this.lblReaderID = new System.Windows.Forms.Label();
            this.lblDepartment = new System.Windows.Forms.Label();
            this.lblTitle = new System.Windows.Forms.Label();
            this.groupBox1 = new System.Windows.Forms.GroupBox();
            this.groupBox2 = new System.Windows.Forms.GroupBox();
            ((System.ComponentModel.ISupportInitialize)(this.dgvTeacher)).BeginInit();
            this.groupBox1.SuspendLayout();
            this.groupBox2.SuspendLayout();
            this.SuspendLayout();
            // 
            // dgvTeacher
            // 
            this.dgvTeacher.AllowUserToAddRows = false;
            this.dgvTeacher.AutoSizeColumnsMode = System.Windows.Forms.DataGridViewAutoSizeColumnsMode.Fill;
            this.dgvTeacher.BackgroundColor = System.Drawing.Color.LightGray;
            this.dgvTeacher.ColumnHeadersHeight = 34;
            this.dgvTeacher.Location = new System.Drawing.Point(12, 250);
            this.dgvTeacher.Name = "dgvTeacher";
            this.dgvTeacher.ReadOnly = true;
            this.dgvTeacher.RowHeadersWidth = 62;
            this.dgvTeacher.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
            this.dgvTeacher.Size = new System.Drawing.Size(960, 300);
            this.dgvTeacher.TabIndex = 2;
            this.dgvTeacher.CellClick += new System.Windows.Forms.DataGridViewCellEventHandler(this.DgvTeacher_CellClick);
            // 
            // txtTeacherID
            // 
            this.txtTeacherID.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.txtTeacherID.Location = new System.Drawing.Point(127, 27);
            this.txtTeacherID.Name = "txtTeacherID";
            this.txtTeacherID.Size = new System.Drawing.Size(150, 26);
            this.txtTeacherID.TabIndex = 1;
            // 
            // txtReaderID
            // 
            this.txtReaderID.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.txtReaderID.Location = new System.Drawing.Point(405, 27);
            this.txtReaderID.Name = "txtReaderID";
            this.txtReaderID.Size = new System.Drawing.Size(150, 26);
            this.txtReaderID.TabIndex = 3;
            // 
            // txtDepartment
            // 
            this.txtDepartment.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.txtDepartment.Location = new System.Drawing.Point(127, 77);
            this.txtDepartment.Name = "txtDepartment";
            this.txtDepartment.Size = new System.Drawing.Size(150, 26);
            this.txtDepartment.TabIndex = 5;
            // 
            // txtTitle
            // 
            this.txtTitle.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.txtTitle.Location = new System.Drawing.Point(405, 77);
            this.txtTitle.Name = "txtTitle";
            this.txtTitle.Size = new System.Drawing.Size(150, 26);
            this.txtTitle.TabIndex = 7;
            // 
            // btnAdd
            // 
            this.btnAdd.BackColor = System.Drawing.SystemColors.HotTrack;
            this.btnAdd.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.btnAdd.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.btnAdd.Location = new System.Drawing.Point(116, 130);
            this.btnAdd.Name = "btnAdd";
            this.btnAdd.Size = new System.Drawing.Size(120, 30);
            this.btnAdd.TabIndex = 8;
            this.btnAdd.Text = "添加";
            this.btnAdd.UseVisualStyleBackColor = false;
            this.btnAdd.Click += new System.EventHandler(this.BtnAdd_Click);
            // 
            // btnEdit
            // 
            this.btnEdit.BackColor = System.Drawing.SystemColors.HotTrack;
            this.btnEdit.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.btnEdit.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.btnEdit.Location = new System.Drawing.Point(316, 130);
            this.btnEdit.Name = "btnEdit";
            this.btnEdit.Size = new System.Drawing.Size(120, 30);
            this.btnEdit.TabIndex = 9;
            this.btnEdit.Text = "修改";
            this.btnEdit.UseVisualStyleBackColor = false;
            this.btnEdit.Click += new System.EventHandler(this.BtnEdit_Click);
            // 
            // btnDelete
            // 
            this.btnDelete.BackColor = System.Drawing.SystemColors.HotTrack;
            this.btnDelete.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.btnDelete.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.btnDelete.Location = new System.Drawing.Point(516, 130);
            this.btnDelete.Name = "btnDelete";
            this.btnDelete.Size = new System.Drawing.Size(120, 30);
            this.btnDelete.TabIndex = 10;
            this.btnDelete.Text = "删除";
            this.btnDelete.UseVisualStyleBackColor = false;
            this.btnDelete.Click += new System.EventHandler(this.BtnDelete_Click);
            // 
            // btnReset
            // 
            this.btnReset.BackColor = System.Drawing.SystemColors.HotTrack;
            this.btnReset.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.btnReset.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.btnReset.Location = new System.Drawing.Point(716, 130);
            this.btnReset.Name = "btnReset";
            this.btnReset.Size = new System.Drawing.Size(120, 30);
            this.btnReset.TabIndex = 11;
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
            // lblTeacherID
            // 
            this.lblTeacherID.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.lblTeacherID.Location = new System.Drawing.Point(20, 30);
            this.lblTeacherID.Name = "lblTeacherID";
            this.lblTeacherID.Size = new System.Drawing.Size(100, 23);
            this.lblTeacherID.TabIndex = 0;
            this.lblTeacherID.Text = "教师编号：";
            // 
            // lblReaderID
            // 
            this.lblReaderID.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.lblReaderID.Location = new System.Drawing.Point(297, 30);
            this.lblReaderID.Name = "lblReaderID";
            this.lblReaderID.Size = new System.Drawing.Size(100, 23);
            this.lblReaderID.TabIndex = 2;
            this.lblReaderID.Text = "读者编号：";
            // 
            // lblDepartment
            // 
            this.lblDepartment.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.lblDepartment.Location = new System.Drawing.Point(20, 80);
            this.lblDepartment.Name = "lblDepartment";
            this.lblDepartment.Size = new System.Drawing.Size(100, 23);
            this.lblDepartment.TabIndex = 4;
            this.lblDepartment.Text = "院系：";
            // 
            // lblTitle
            // 
            this.lblTitle.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.lblTitle.Location = new System.Drawing.Point(297, 80);
            this.lblTitle.Name = "lblTitle";
            this.lblTitle.Size = new System.Drawing.Size(100, 23);
            this.lblTitle.TabIndex = 6;
            this.lblTitle.Text = "职称：";
            // 
            // groupBox1
            // 
            this.groupBox1.BackColor = System.Drawing.Color.White;
            this.groupBox1.Controls.Add(this.lblTeacherID);
            this.groupBox1.Controls.Add(this.txtTeacherID);
            this.groupBox1.Controls.Add(this.lblReaderID);
            this.groupBox1.Controls.Add(this.txtReaderID);
            this.groupBox1.Controls.Add(this.lblDepartment);
            this.groupBox1.Controls.Add(this.txtDepartment);
            this.groupBox1.Controls.Add(this.lblTitle);
            this.groupBox1.Controls.Add(this.txtTitle);
            this.groupBox1.Controls.Add(this.btnAdd);
            this.groupBox1.Controls.Add(this.btnEdit);
            this.groupBox1.Controls.Add(this.btnDelete);
            this.groupBox1.Controls.Add(this.btnReset);
            this.groupBox1.Location = new System.Drawing.Point(12, 12);
            this.groupBox1.Name = "groupBox1";
            this.groupBox1.Size = new System.Drawing.Size(960, 180);
            this.groupBox1.TabIndex = 0;
            this.groupBox1.TabStop = false;
            this.groupBox1.Text = "教师信息";
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
            // TeacherManageForm
            // 
            this.BackColor = System.Drawing.SystemColors.HotTrack;
            this.ClientSize = new System.Drawing.Size(978, 558);
            this.Controls.Add(this.groupBox1);
            this.Controls.Add(this.groupBox2);
            this.Controls.Add(this.dgvTeacher);
            this.ForeColor = System.Drawing.SystemColors.ControlText;
            this.Name = "TeacherManageForm";
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Text = "教师管理";
            ((System.ComponentModel.ISupportInitialize)(this.dgvTeacher)).EndInit();
            this.groupBox1.ResumeLayout(false);
            this.groupBox1.PerformLayout();
            this.groupBox2.ResumeLayout(false);
            this.groupBox2.PerformLayout();
            this.ResumeLayout(false);

    }

    private void InitializeSearchTypes()
    {
        cmbSearchType.Items.AddRange(new string[] {
            "教师编号",
            "读者编号",
            "院系",
            "职称"
        });
        cmbSearchType.SelectedIndex = 0;
    }

    private void LoadTeacherData(string searchCondition = "")
    {
        string sql = @"SELECT t.*, r.ReaderName 
                      FROM Teacher t 
                      LEFT JOIN Reader r ON t.ReaderID = r.ReaderID";
        if (!string.IsNullOrEmpty(searchCondition))
        {
            sql += " WHERE " + searchCondition;
        }
        dgvTeacher.DataSource = DBHelper.ExecuteQuery(sql);
    }

    private void BtnAdd_Click(object sender, EventArgs e)
    {
        if (ValidateInput())
        {
            string sql = $@"INSERT INTO Teacher VALUES (
                '{txtTeacherID.Text}',
                '{txtReaderID.Text}',
                '{txtDepartment.Text}',
                '{txtTitle.Text}'
            )";

            try
            {
                DBHelper.ExecuteNonQuery(sql);
                MessageBox.Show("添加成功！");
                LoadTeacherData();
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
            string sql = $@"UPDATE Teacher SET 
                ReaderID = '{txtReaderID.Text}',
                Department = '{txtDepartment.Text}',
                Title = '{txtTitle.Text}'
                WHERE TeacherID = '{txtTeacherID.Text}'";

            try
            {
                DBHelper.ExecuteNonQuery(sql);
                MessageBox.Show("修改成功！");
                LoadTeacherData();
            }
            catch (Exception ex)
            {
                MessageBox.Show("修改失败：" + ex.Message);
            }
        }
    }

    private void BtnDelete_Click(object sender, EventArgs e)
    {
        if (string.IsNullOrEmpty(txtTeacherID.Text))
        {
            MessageBox.Show("请先选择要删除的教师！");
            return;
        }

        if (MessageBox.Show("确定要删除该教师信息吗？", "确认删除", 
            MessageBoxButtons.YesNo) == DialogResult.Yes)
        {
            string sql = $"DELETE FROM Teacher WHERE TeacherID = '{txtTeacherID.Text}'";

            try
            {
                DBHelper.ExecuteNonQuery(sql);
                MessageBox.Show("删除成功！");
                LoadTeacherData();
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
            case "教师编号":
                condition = $"t.TeacherID LIKE '%{txtSearch.Text}%'";
                break;
            case "读者编号":
                condition = $"t.ReaderID LIKE '%{txtSearch.Text}%'";
                break;
            case "院系":
                condition = $"t.Department LIKE '%{txtSearch.Text}%'";
                break;
            case "职称":
                condition = $"t.Title LIKE '%{txtSearch.Text}%'";
                break;
        }
        LoadTeacherData(condition);
    }

    private void DgvTeacher_CellClick(object sender, DataGridViewCellEventArgs e)
    {
        if (e.RowIndex >= 0)
        {
            DataGridViewRow row = dgvTeacher.Rows[e.RowIndex];
            txtTeacherID.Text = row.Cells["TeacherID"].Value.ToString();
            txtReaderID.Text = row.Cells["ReaderID"].Value.ToString();
            txtDepartment.Text = row.Cells["Department"].Value.ToString();
            txtTitle.Text = row.Cells["Title"].Value.ToString();
        }
    }

    private bool ValidateInput()
    {
        if (string.IsNullOrEmpty(txtTeacherID.Text))
        {
            MessageBox.Show("教师编号不能为空！");
            return false;
        }
        if (string.IsNullOrEmpty(txtReaderID.Text))
        {
            MessageBox.Show("读者编号不能为空！");
            return false;
        }
        if (string.IsNullOrEmpty(txtDepartment.Text))
        {
            MessageBox.Show("院系不能为空！");
            return false;
        }
        if (string.IsNullOrEmpty(txtTitle.Text))
        {
            MessageBox.Show("职称不能为空！");
            return false;
        }
        return true;
    }

    private void ClearInputs()
    {
        txtTeacherID.Clear();
        txtReaderID.Clear();
        txtDepartment.Clear();
        txtTitle.Clear();
    }

    private void BtnReset_Click(object sender, EventArgs e)
    {
        ClearInputs();
    }

    private void BtnClearFilter_Click(object sender, EventArgs e)
    {
        txtSearch.Clear();
        cmbSearchType.SelectedIndex = 0;
        LoadTeacherData();
    }
} 