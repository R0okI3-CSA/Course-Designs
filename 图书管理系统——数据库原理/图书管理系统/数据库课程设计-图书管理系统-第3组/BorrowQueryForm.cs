using System;
using System.Data;
using System.Drawing;
using System.Windows.Forms;

public partial class BorrowQueryForm : Form
{
    private DataGridView dgvBorrow;
    private Button btnSearch;
    private Button btnClearFilter;
    private TextBox txtSearch;
    private ComboBox cmbSearchType;
    private GroupBox groupBox1;
    private DateTimePicker dtpStartDate;
    private DateTimePicker dtpEndDate;
    private Label lblDateRange;
    private CheckBox chkDateFilter;
    private ComboBox cmbStatus;
    private Label lblStatus;
    private CheckBox chkStatusFilter;

    public BorrowQueryForm()
    {
        InitializeComponent();
        LoadBorrowData();
        InitializeSearchTypes();
    }

    private void InitializeComponent()
    {
            this.dgvBorrow = new System.Windows.Forms.DataGridView();
            this.btnSearch = new System.Windows.Forms.Button();
            this.btnClearFilter = new System.Windows.Forms.Button();
            this.txtSearch = new System.Windows.Forms.TextBox();
            this.cmbSearchType = new System.Windows.Forms.ComboBox();
            this.groupBox1 = new System.Windows.Forms.GroupBox();
            this.chkStatusFilter = new System.Windows.Forms.CheckBox();
            this.lblStatus = new System.Windows.Forms.Label();
            this.cmbStatus = new System.Windows.Forms.ComboBox();
            this.chkDateFilter = new System.Windows.Forms.CheckBox();
            this.lblDateRange = new System.Windows.Forms.Label();
            this.dtpStartDate = new System.Windows.Forms.DateTimePicker();
            this.dtpEndDate = new System.Windows.Forms.DateTimePicker();
            ((System.ComponentModel.ISupportInitialize)(this.dgvBorrow)).BeginInit();
            this.groupBox1.SuspendLayout();
            this.SuspendLayout();
            // 
            // dgvBorrow
            // 
            this.dgvBorrow.AllowUserToAddRows = false;
            this.dgvBorrow.AutoSizeColumnsMode = System.Windows.Forms.DataGridViewAutoSizeColumnsMode.Fill;
            this.dgvBorrow.BackgroundColor = System.Drawing.Color.LightGray;
            this.dgvBorrow.ColumnHeadersHeight = 34;
            this.dgvBorrow.Location = new System.Drawing.Point(12, 120);
            this.dgvBorrow.Name = "dgvBorrow";
            this.dgvBorrow.ReadOnly = true;
            this.dgvBorrow.RowHeadersWidth = 62;
            this.dgvBorrow.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
            this.dgvBorrow.Size = new System.Drawing.Size(1160, 520);
            this.dgvBorrow.TabIndex = 1;
            // 
            // btnSearch
            // 
            this.btnSearch.BackColor = System.Drawing.SystemColors.HotTrack;
            this.btnSearch.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.btnSearch.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.btnSearch.Location = new System.Drawing.Point(546, 59);
            this.btnSearch.Name = "btnSearch";
            this.btnSearch.Size = new System.Drawing.Size(100, 30);
            this.btnSearch.TabIndex = 9;
            this.btnSearch.Text = "搜索";
            this.btnSearch.UseVisualStyleBackColor = false;
            this.btnSearch.Click += new System.EventHandler(this.BtnSearch_Click);
            // 
            // btnClearFilter
            // 
            this.btnClearFilter.BackColor = System.Drawing.SystemColors.HotTrack;
            this.btnClearFilter.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.btnClearFilter.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.btnClearFilter.Location = new System.Drawing.Point(663, 59);
            this.btnClearFilter.Name = "btnClearFilter";
            this.btnClearFilter.Size = new System.Drawing.Size(100, 30);
            this.btnClearFilter.TabIndex = 10;
            this.btnClearFilter.Text = "取消筛选";
            this.btnClearFilter.UseVisualStyleBackColor = false;
            this.btnClearFilter.Click += new System.EventHandler(this.BtnClearFilter_Click);
            // 
            // txtSearch
            // 
            this.txtSearch.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.txtSearch.Location = new System.Drawing.Point(150, 24);
            this.txtSearch.Name = "txtSearch";
            this.txtSearch.Size = new System.Drawing.Size(200, 26);
            this.txtSearch.TabIndex = 1;
            // 
            // cmbSearchType
            // 
            this.cmbSearchType.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.cmbSearchType.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.cmbSearchType.Location = new System.Drawing.Point(20, 24);
            this.cmbSearchType.Name = "cmbSearchType";
            this.cmbSearchType.Size = new System.Drawing.Size(120, 24);
            this.cmbSearchType.TabIndex = 0;
            // 
            // groupBox1
            // 
            this.groupBox1.BackColor = System.Drawing.Color.White;
            this.groupBox1.Controls.Add(this.cmbSearchType);
            this.groupBox1.Controls.Add(this.txtSearch);
            this.groupBox1.Controls.Add(this.chkStatusFilter);
            this.groupBox1.Controls.Add(this.lblStatus);
            this.groupBox1.Controls.Add(this.cmbStatus);
            this.groupBox1.Controls.Add(this.chkDateFilter);
            this.groupBox1.Controls.Add(this.lblDateRange);
            this.groupBox1.Controls.Add(this.dtpStartDate);
            this.groupBox1.Controls.Add(this.dtpEndDate);
            this.groupBox1.Controls.Add(this.btnSearch);
            this.groupBox1.Controls.Add(this.btnClearFilter);
            this.groupBox1.Location = new System.Drawing.Point(12, 12);
            this.groupBox1.Name = "groupBox1";
            this.groupBox1.Size = new System.Drawing.Size(1160, 100);
            this.groupBox1.TabIndex = 0;
            this.groupBox1.TabStop = false;
            this.groupBox1.Text = "查询条件";
            // 
            // chkStatusFilter
            // 
            this.chkStatusFilter.AutoSize = true;
            this.chkStatusFilter.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.chkStatusFilter.Location = new System.Drawing.Point(406, 24);
            this.chkStatusFilter.Name = "chkStatusFilter";
            this.chkStatusFilter.Size = new System.Drawing.Size(107, 20);
            this.chkStatusFilter.TabIndex = 2;
            this.chkStatusFilter.Text = "按状态筛选";
            // 
            // lblStatus
            // 
            this.lblStatus.AutoSize = true;
            this.lblStatus.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.lblStatus.Location = new System.Drawing.Point(519, 25);
            this.lblStatus.Name = "lblStatus";
            this.lblStatus.Size = new System.Drawing.Size(56, 16);
            this.lblStatus.TabIndex = 3;
            this.lblStatus.Text = "状态：";
            // 
            // cmbStatus
            // 
            this.cmbStatus.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.cmbStatus.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.cmbStatus.Items.AddRange(new object[] {
            "lending",
            "return",
            "overdue",
            "lost"});
            this.cmbStatus.Location = new System.Drawing.Point(587, 24);
            this.cmbStatus.Name = "cmbStatus";
            this.cmbStatus.Size = new System.Drawing.Size(120, 24);
            this.cmbStatus.TabIndex = 4;
            // 
            // chkDateFilter
            // 
            this.chkDateFilter.AutoSize = true;
            this.chkDateFilter.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.chkDateFilter.Location = new System.Drawing.Point(20, 65);
            this.chkDateFilter.Name = "chkDateFilter";
            this.chkDateFilter.Size = new System.Drawing.Size(139, 20);
            this.chkDateFilter.TabIndex = 5;
            this.chkDateFilter.Text = "按借阅日期筛选";
            // 
            // lblDateRange
            // 
            this.lblDateRange.AutoSize = true;
            this.lblDateRange.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.lblDateRange.Location = new System.Drawing.Point(336, 61);
            this.lblDateRange.Name = "lblDateRange";
            this.lblDateRange.Size = new System.Drawing.Size(24, 16);
            this.lblDateRange.TabIndex = 6;
            this.lblDateRange.Text = "至";
            // 
            // dtpStartDate
            // 
            this.dtpStartDate.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.dtpStartDate.Format = System.Windows.Forms.DateTimePickerFormat.Short;
            this.dtpStartDate.Location = new System.Drawing.Point(176, 59);
            this.dtpStartDate.Name = "dtpStartDate";
            this.dtpStartDate.Size = new System.Drawing.Size(150, 26);
            this.dtpStartDate.TabIndex = 7;
            // 
            // dtpEndDate
            // 
            this.dtpEndDate.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.dtpEndDate.Format = System.Windows.Forms.DateTimePickerFormat.Short;
            this.dtpEndDate.Location = new System.Drawing.Point(366, 59);
            this.dtpEndDate.Name = "dtpEndDate";
            this.dtpEndDate.Size = new System.Drawing.Size(150, 26);
            this.dtpEndDate.TabIndex = 8;
            // 
            // BorrowQueryForm
            // 
            this.BackColor = System.Drawing.SystemColors.HotTrack;
            this.ClientSize = new System.Drawing.Size(1178, 644);
            this.Controls.Add(this.groupBox1);
            this.Controls.Add(this.dgvBorrow);
            this.Name = "BorrowQueryForm";
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Text = "借阅查询";
            ((System.ComponentModel.ISupportInitialize)(this.dgvBorrow)).EndInit();
            this.groupBox1.ResumeLayout(false);
            this.groupBox1.PerformLayout();
            this.ResumeLayout(false);

    }

    private void InitializeSearchTypes()
    {
        cmbSearchType.Items.AddRange(new string[] {
            "借阅编号",
            "借阅卡号",
            "图书编号",
            "班级",
            "读者类型"
        });
        cmbSearchType.SelectedIndex = 0;
    }

    private void LoadBorrowData(string searchCondition = "")
    {
        string sql = @"SELECT BorrowID, CardID, BookID, BorrowDate, 
                             DueDate, ReturnDate, Status, ClassName, ReaderType 
                      FROM Borrow";
        
        if (!string.IsNullOrEmpty(searchCondition))
        {
            sql += " WHERE " + searchCondition;
        }
        dgvBorrow.DataSource = DBHelper.ExecuteQuery(sql);
    }

    private void BtnSearch_Click(object sender, EventArgs e)
    {
        string condition = "";
        
        // 基本搜索条件
        if (!string.IsNullOrEmpty(txtSearch.Text))
        {
            switch (cmbSearchType.Text)
            {
                case "借阅编号":
                    condition = $"BorrowID LIKE '%{txtSearch.Text}%'";
                    break;
                case "借阅卡号":
                    condition = $"CardID LIKE '%{txtSearch.Text}%'";
                    break;
                case "图书编号":
                    condition = $"BookID LIKE '%{txtSearch.Text}%'";
                    break;
                case "班级":
                    condition = $"ClassName LIKE '%{txtSearch.Text}%'";
                    break;
                case "读者类型":
                    condition = $"ReaderType LIKE '%{txtSearch.Text}%'";
                    break;
            }
        }

        // 状态筛选条件
        if (chkStatusFilter.Checked && !string.IsNullOrEmpty(cmbStatus.Text))
        {
            if (!string.IsNullOrEmpty(condition))
                condition += " AND ";
            condition += $"Status = '{cmbStatus.Text}'";
        }

        // 日期范围筛选条件
        if (chkDateFilter.Checked)
        {
            if (!string.IsNullOrEmpty(condition))
                condition += " AND ";
            condition += $"BorrowDate BETWEEN '{dtpStartDate.Value.ToString("yyyy-MM-dd")}' AND '{dtpEndDate.Value.ToString("yyyy-MM-dd")}'";
        }

        LoadBorrowData(condition);
    }

    private void BtnClearFilter_Click(object sender, EventArgs e)
    {
        txtSearch.Clear();
        cmbSearchType.SelectedIndex = 0;
        chkStatusFilter.Checked = false;
        cmbStatus.SelectedIndex = -1;
        chkDateFilter.Checked = false;
        dtpStartDate.Value = DateTime.Now;
        dtpEndDate.Value = DateTime.Now;
        LoadBorrowData();
    }
} 