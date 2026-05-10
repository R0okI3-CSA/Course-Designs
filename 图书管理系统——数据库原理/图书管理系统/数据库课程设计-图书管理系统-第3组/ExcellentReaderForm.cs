using System;
using System.Data;
using System.Drawing;
using System.Windows.Forms;
using System.Data.SqlClient;

public partial class ExcellentReaderForm : Form
{
    private DataGridView dgvExcReader;
    private GroupBox groupBox1;
    private Label lblReaderID;
    private TextBox txtReaderID;
    private Label lblReaderName;
    private TextBox txtReaderName;
    private Label lblDateRange;
    private DateTimePicker dtpStartDate;
    private DateTimePicker dtpEndDate;
    private Button btnSearch;
    private Button btnClearFilter;
    private Button btnAddExcReader;

    public ExcellentReaderForm()
    {
        InitializeComponent();
        LoadExcReaderData();
    }

    private void InitializeComponent()
    {
            this.groupBox1 = new System.Windows.Forms.GroupBox();
            this.lblReaderID = new System.Windows.Forms.Label();
            this.txtReaderID = new System.Windows.Forms.TextBox();
            this.lblReaderName = new System.Windows.Forms.Label();
            this.txtReaderName = new System.Windows.Forms.TextBox();
            this.lblDateRange = new System.Windows.Forms.Label();
            this.dtpStartDate = new System.Windows.Forms.DateTimePicker();
            this.dtpEndDate = new System.Windows.Forms.DateTimePicker();
            this.btnSearch = new System.Windows.Forms.Button();
            this.btnClearFilter = new System.Windows.Forms.Button();
            this.btnAddExcReader = new System.Windows.Forms.Button();
            this.dgvExcReader = new System.Windows.Forms.DataGridView();
            this.groupBox1.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.dgvExcReader)).BeginInit();
            this.SuspendLayout();
            // 
            // groupBox1
            // 
            this.groupBox1.BackColor = System.Drawing.Color.White;
            this.groupBox1.Controls.Add(this.lblReaderID);
            this.groupBox1.Controls.Add(this.txtReaderID);
            this.groupBox1.Controls.Add(this.lblReaderName);
            this.groupBox1.Controls.Add(this.txtReaderName);
            this.groupBox1.Controls.Add(this.lblDateRange);
            this.groupBox1.Controls.Add(this.dtpStartDate);
            this.groupBox1.Controls.Add(this.dtpEndDate);
            this.groupBox1.Controls.Add(this.btnSearch);
            this.groupBox1.Controls.Add(this.btnClearFilter);
            this.groupBox1.Controls.Add(this.btnAddExcReader);
            this.groupBox1.Location = new System.Drawing.Point(12, 12);
            this.groupBox1.Name = "groupBox1";
            this.groupBox1.Size = new System.Drawing.Size(1160, 123);
            this.groupBox1.TabIndex = 0;
            this.groupBox1.TabStop = false;
            this.groupBox1.Text = "查询条件";
            // 
            // lblReaderID
            // 
            this.lblReaderID.AutoSize = true;
            this.lblReaderID.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.lblReaderID.Location = new System.Drawing.Point(20, 35);
            this.lblReaderID.Name = "lblReaderID";
            this.lblReaderID.Size = new System.Drawing.Size(88, 16);
            this.lblReaderID.TabIndex = 0;
            this.lblReaderID.Text = "读者编号：";
            // 
            // txtReaderID
            // 
            this.txtReaderID.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.txtReaderID.Location = new System.Drawing.Point(104, 32);
            this.txtReaderID.Name = "txtReaderID";
            this.txtReaderID.Size = new System.Drawing.Size(150, 26);
            this.txtReaderID.TabIndex = 1;
            // 
            // lblReaderName
            // 
            this.lblReaderName.AutoSize = true;
            this.lblReaderName.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.lblReaderName.Location = new System.Drawing.Point(274, 35);
            this.lblReaderName.Name = "lblReaderName";
            this.lblReaderName.Size = new System.Drawing.Size(88, 16);
            this.lblReaderName.TabIndex = 2;
            this.lblReaderName.Text = "读者姓名：";
            // 
            // txtReaderName
            // 
            this.txtReaderName.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.txtReaderName.Location = new System.Drawing.Point(358, 32);
            this.txtReaderName.Name = "txtReaderName";
            this.txtReaderName.Size = new System.Drawing.Size(150, 26);
            this.txtReaderName.TabIndex = 3;
            // 
            // lblDateRange
            // 
            this.lblDateRange.AutoSize = true;
            this.lblDateRange.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.lblDateRange.Location = new System.Drawing.Point(528, 35);
            this.lblDateRange.Name = "lblDateRange";
            this.lblDateRange.Size = new System.Drawing.Size(88, 16);
            this.lblDateRange.TabIndex = 4;
            this.lblDateRange.Text = "获得时间：";
            // 
            // dtpStartDate
            // 
            this.dtpStartDate.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.dtpStartDate.Format = System.Windows.Forms.DateTimePickerFormat.Short;
            this.dtpStartDate.Location = new System.Drawing.Point(620, 32);
            this.dtpStartDate.Name = "dtpStartDate";
            this.dtpStartDate.Size = new System.Drawing.Size(150, 26);
            this.dtpStartDate.TabIndex = 5;
            // 
            // dtpEndDate
            // 
            this.dtpEndDate.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.dtpEndDate.Format = System.Windows.Forms.DateTimePickerFormat.Short;
            this.dtpEndDate.Location = new System.Drawing.Point(790, 32);
            this.dtpEndDate.Name = "dtpEndDate";
            this.dtpEndDate.Size = new System.Drawing.Size(150, 26);
            this.dtpEndDate.TabIndex = 6;
            // 
            // btnSearch
            // 
            this.btnSearch.BackColor = System.Drawing.SystemColors.HotTrack;
            this.btnSearch.DialogResult = System.Windows.Forms.DialogResult.Cancel;
            this.btnSearch.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.btnSearch.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.btnSearch.Location = new System.Drawing.Point(289, 75);
            this.btnSearch.Name = "btnSearch";
            this.btnSearch.Size = new System.Drawing.Size(100, 30);
            this.btnSearch.TabIndex = 7;
            this.btnSearch.Text = "查询";
            this.btnSearch.UseVisualStyleBackColor = false;
            this.btnSearch.Click += new System.EventHandler(this.BtnSearch_Click);
            // 
            // btnClearFilter
            // 
            this.btnClearFilter.BackColor = System.Drawing.SystemColors.HotTrack;
            this.btnClearFilter.DialogResult = System.Windows.Forms.DialogResult.Cancel;
            this.btnClearFilter.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.btnClearFilter.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.btnClearFilter.Location = new System.Drawing.Point(513, 75);
            this.btnClearFilter.Name = "btnClearFilter";
            this.btnClearFilter.Size = new System.Drawing.Size(100, 30);
            this.btnClearFilter.TabIndex = 8;
            this.btnClearFilter.Text = "取消筛选";
            this.btnClearFilter.UseVisualStyleBackColor = false;
            this.btnClearFilter.Click += new System.EventHandler(this.BtnClearFilter_Click);
            // 
            // btnAddExcReader
            // 
            this.btnAddExcReader.BackColor = System.Drawing.SystemColors.HotTrack;
            this.btnAddExcReader.DialogResult = System.Windows.Forms.DialogResult.Cancel;
            this.btnAddExcReader.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.btnAddExcReader.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.btnAddExcReader.Location = new System.Drawing.Point(740, 75);
            this.btnAddExcReader.Name = "btnAddExcReader";
            this.btnAddExcReader.Size = new System.Drawing.Size(152, 30);
            this.btnAddExcReader.TabIndex = 9;
            this.btnAddExcReader.Text = "添加优秀读者";
            this.btnAddExcReader.UseVisualStyleBackColor = false;
            this.btnAddExcReader.Click += new System.EventHandler(this.BtnAddExcReader_Click);
            // 
            // dgvExcReader
            // 
            this.dgvExcReader.AllowUserToAddRows = false;
            this.dgvExcReader.AutoSizeColumnsMode = System.Windows.Forms.DataGridViewAutoSizeColumnsMode.Fill;
            this.dgvExcReader.BackgroundColor = System.Drawing.Color.LightGray;
            this.dgvExcReader.ColumnHeadersHeight = 34;
            this.dgvExcReader.Location = new System.Drawing.Point(12, 164);
            this.dgvExcReader.Name = "dgvExcReader";
            this.dgvExcReader.ReadOnly = true;
            this.dgvExcReader.RowHeadersWidth = 62;
            this.dgvExcReader.Size = new System.Drawing.Size(1160, 536);
            this.dgvExcReader.TabIndex = 1;
            // 
            // ExcellentReaderForm
            // 
            this.BackColor = System.Drawing.SystemColors.HotTrack;
            this.ClientSize = new System.Drawing.Size(1178, 713);
            this.Controls.Add(this.groupBox1);
            this.Controls.Add(this.dgvExcReader);
            this.Name = "ExcellentReaderForm";
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Text = "优秀读者管理";
            this.groupBox1.ResumeLayout(false);
            this.groupBox1.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.dgvExcReader)).EndInit();
            this.ResumeLayout(false);

    }

    private void LoadExcReaderData(string condition = "")
    {
        string sql = @"
            SELECT 
                er.ReaderID as '读者编号',
                er.ReaderName as '读者姓名',
                er.ObTime as '获得时间'
            FROM ExcReader er
            WHERE 1=1 " + condition;

        DataTable dt = DBHelper.ExecuteQuery(sql);
        dgvExcReader.DataSource = dt;
    }

    private void BtnSearch_Click(object sender, EventArgs e)
    {
        string conditions = "";

        if (!string.IsNullOrEmpty(txtReaderID.Text))
        {
            conditions += $" AND ReaderID LIKE '%{txtReaderID.Text}%'";
        }

        if (!string.IsNullOrEmpty(txtReaderName.Text))
        {
            conditions += $" AND ReaderName LIKE '%{txtReaderName.Text}%'";
        }

        conditions += $" AND ObTime BETWEEN '{dtpStartDate.Value:yyyy-MM-dd}' AND '{dtpEndDate.Value:yyyy-MM-dd}'";

        LoadExcReaderData(conditions);
    }

    private void BtnClearFilter_Click(object sender, EventArgs e)
    {
        txtReaderID.Clear();
        txtReaderName.Clear();
        dtpStartDate.Value = DateTime.Now.AddYears(-1);  // 默认显示最近一年
        dtpEndDate.Value = DateTime.Now;
        LoadExcReaderData();
    }

    private void BtnAddExcReader_Click(object sender, EventArgs e)
    {
        // 创建日期选择对话框
        using (Form dateForm = new Form())
        {
            dateForm.Text = "选择时间范围";
            dateForm.Size = new Size(400, 200);
            dateForm.StartPosition = FormStartPosition.CenterParent;
            dateForm.FormBorderStyle = FormBorderStyle.FixedDialog;
            dateForm.MaximizeBox = false;
            dateForm.MinimizeBox = false;

            DateTimePicker startPicker = new DateTimePicker();
            startPicker.Location = new Point(100, 20);
            startPicker.Format = DateTimePickerFormat.Short;

            DateTimePicker endPicker = new DateTimePicker();
            endPicker.Location = new Point(100, 60);
            endPicker.Format = DateTimePickerFormat.Short;

            Label lblStart = new Label();
            lblStart.Text = "开始日期：";
            lblStart.Location = new Point(20, 25);
            lblStart.AutoSize = true;

            Label lblEnd = new Label();
            lblEnd.Text = "结束日期：";
            lblEnd.Location = new Point(20, 65);
            lblEnd.AutoSize = true;

            Button btnOK = new Button();
            btnOK.Text = "确定";
            btnOK.DialogResult = DialogResult.OK;
            btnOK.Location = new Point(100, 100);

            Button btnCancel = new Button();
            btnCancel.Text = "取消";
            btnCancel.DialogResult = DialogResult.Cancel;
            btnCancel.Location = new Point(200, 100);

            dateForm.Controls.AddRange(new Control[] { 
                startPicker, endPicker, lblStart, lblEnd, btnOK, btnCancel 
            });

            if (dateForm.ShowDialog() == DialogResult.OK)
            {
                FindAndAddExcellentReader(startPicker.Value, endPicker.Value);
            }
        }
    }

    private void FindAndAddExcellentReader(DateTime startDate, DateTime endDate)
    {
        string sql = @"
            WITH ReaderBorrowStats AS (
                /* Get borrowing statistics for each reader */
                SELECT 
                    r.ReaderID,
                    r.ReaderName,
                    COUNT(b.BorrowID) as BorrowCount,
                    SUM(CASE WHEN b.Status IN ('overdue', 'lost') THEN 1 ELSE 0 END) as BadRecordCount
                FROM Reader r
                JOIN BorrowCard bc ON r.ReaderID = bc.ReaderID
                JOIN Borrow b ON bc.CardID = b.CardID
                WHERE b.BorrowDate BETWEEN @StartDate AND @EndDate
                    AND r.ReaderID NOT IN (SELECT ReaderID FROM ExcReader)  /* Exclude existing excellent readers */
                GROUP BY r.ReaderID, r.ReaderName
            )
            SELECT TOP 1
                ReaderID,
                ReaderName,
                BorrowCount
            FROM ReaderBorrowStats
            WHERE BadRecordCount = 0  /* No bad records */
            ORDER BY BorrowCount DESC;  /* Order by borrow count */";

        // 创建参数
        SqlParameter[] parameters = new SqlParameter[]
        {
            new SqlParameter("@StartDate", startDate),
            new SqlParameter("@EndDate", endDate)
        };

        DataTable dt = DBHelper.ExecuteQuery(sql, parameters);

        if (dt != null && dt.Rows.Count > 0)
        {
            // 获取查询结果
            string readerID = dt.Rows[0]["ReaderID"].ToString();
            string readerName = dt.Rows[0]["ReaderName"].ToString();
            int borrowCount = Convert.ToInt32(dt.Rows[0]["BorrowCount"]);

            // 插入新的优秀读者记录
            string insertSql = @"
                INSERT INTO ExcReader (ReaderID, ReaderName, ObTime)
                VALUES (@ReaderID, @ReaderName, @ObTime)";

            SqlParameter[] insertParams = new SqlParameter[]
            {
                new SqlParameter("@ReaderID", readerID),
                new SqlParameter("@ReaderName", readerName),
                new SqlParameter("@ObTime", DateTime.Now)
            };

            try
            {
                DBHelper.ExecuteNonQuery(insertSql, insertParams);
                MessageBox.Show($"已成功添加优秀读者：\n读者编号：{readerID}\n读者姓名：{readerName}\n借阅次数：{borrowCount}",
                    "添加成功", MessageBoxButtons.OK, MessageBoxIcon.Information);

                // 刷新显示
                LoadExcReaderData();
            }
            catch (Exception ex)
            {
                MessageBox.Show($"添加优秀读者失败：{ex.Message}", 
                    "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }
        else
        {
            MessageBox.Show("在指定时间段内未找到符合条件的优秀读者。\n" +
                "符合条件的读者需要：\n1. 无逾期或丢失记录\n2. 有借阅记录\n3. 尚未被评为优秀读者",
                "未找到符合条件的读者", MessageBoxButtons.OK, MessageBoxIcon.Information);
        }
    }
} 