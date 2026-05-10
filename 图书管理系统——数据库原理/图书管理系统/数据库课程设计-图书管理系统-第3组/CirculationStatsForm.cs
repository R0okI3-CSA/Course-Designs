using System;
using System.Data;
using System.Drawing;
using System.Windows.Forms;
using System.Linq;
using ScottPlot;
using ScottPlot.WinForms;
using System.Drawing.Printing;

public partial class CirculationStatsForm : Form
{
    private GroupBox groupBox1;
    private System.Windows.Forms.Label lblBookID;
    private TextBox txtBookID;
    private System.Windows.Forms.Label lblCategory;
    private ComboBox cmbCategory;
    private System.Windows.Forms.Label lblDateRange;
    private DateTimePicker dtpStartDate;
    private DateTimePicker dtpEndDate;
    private Button btnSearch;
    private TabControl tabControl1;
    private TabPage tabChart;
    private TabPage tabGrid;
    private FormsPlot formsPlot;
    private DataGridView dgvCirculation;
    private System.Windows.Forms.Label lblBookTitle;
    private TextBox txtBookTitle;
    private Button btnPrint;
    private PrintDocument printDocument;
    private PrintPreviewDialog printPreviewDialog;

    public CirculationStatsForm()
    {
        InitializeComponent();
        LoadCategories();
    }

    private void InitializeComponent()
    {
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(CirculationStatsForm));
            this.groupBox1 = new System.Windows.Forms.GroupBox();
            this.lblBookID = new System.Windows.Forms.Label();
            this.txtBookID = new System.Windows.Forms.TextBox();
            this.lblCategory = new System.Windows.Forms.Label();
            this.cmbCategory = new System.Windows.Forms.ComboBox();
            this.lblDateRange = new System.Windows.Forms.Label();
            this.dtpStartDate = new System.Windows.Forms.DateTimePicker();
            this.dtpEndDate = new System.Windows.Forms.DateTimePicker();
            this.btnSearch = new System.Windows.Forms.Button();
            this.lblBookTitle = new System.Windows.Forms.Label();
            this.txtBookTitle = new System.Windows.Forms.TextBox();
            this.btnPrint = new System.Windows.Forms.Button();
            this.tabControl1 = new System.Windows.Forms.TabControl();
            this.tabChart = new System.Windows.Forms.TabPage();
            this.formsPlot = new ScottPlot.WinForms.FormsPlot();
            this.tabGrid = new System.Windows.Forms.TabPage();
            this.dgvCirculation = new System.Windows.Forms.DataGridView();
            this.printDocument = new System.Drawing.Printing.PrintDocument();
            this.printPreviewDialog = new System.Windows.Forms.PrintPreviewDialog();
            this.groupBox1.SuspendLayout();
            this.tabControl1.SuspendLayout();
            this.tabChart.SuspendLayout();
            this.tabGrid.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.dgvCirculation)).BeginInit();
            this.SuspendLayout();
            // 
            // groupBox1
            // 
            this.groupBox1.BackColor = System.Drawing.Color.White;
            this.groupBox1.Controls.Add(this.lblBookID);
            this.groupBox1.Controls.Add(this.txtBookID);
            this.groupBox1.Controls.Add(this.lblCategory);
            this.groupBox1.Controls.Add(this.cmbCategory);
            this.groupBox1.Controls.Add(this.lblDateRange);
            this.groupBox1.Controls.Add(this.dtpStartDate);
            this.groupBox1.Controls.Add(this.dtpEndDate);
            this.groupBox1.Controls.Add(this.btnSearch);
            this.groupBox1.Controls.Add(this.lblBookTitle);
            this.groupBox1.Controls.Add(this.txtBookTitle);
            this.groupBox1.Controls.Add(this.btnPrint);
            this.groupBox1.Location = new System.Drawing.Point(12, 12);
            this.groupBox1.Name = "groupBox1";
            this.groupBox1.Size = new System.Drawing.Size(1275, 66);
            this.groupBox1.TabIndex = 0;
            this.groupBox1.TabStop = false;
            this.groupBox1.Text = "查询条件";
            // 
            // lblBookID
            // 
            this.lblBookID.AutoSize = true;
            this.lblBookID.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.lblBookID.Location = new System.Drawing.Point(20, 35);
            this.lblBookID.Name = "lblBookID";
            this.lblBookID.Size = new System.Drawing.Size(88, 16);
            this.lblBookID.TabIndex = 0;
            this.lblBookID.Text = "图书编号：";
            // 
            // txtBookID
            // 
            this.txtBookID.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.txtBookID.Location = new System.Drawing.Point(135, 32);
            this.txtBookID.Name = "txtBookID";
            this.txtBookID.Size = new System.Drawing.Size(150, 26);
            this.txtBookID.TabIndex = 1;
            // 
            // lblCategory
            // 
            this.lblCategory.AutoSize = true;
            this.lblCategory.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.lblCategory.Location = new System.Drawing.Point(305, 35);
            this.lblCategory.Name = "lblCategory";
            this.lblCategory.Size = new System.Drawing.Size(88, 16);
            this.lblCategory.TabIndex = 2;
            this.lblCategory.Text = "图书类型：";
            // 
            // cmbCategory
            // 
            this.cmbCategory.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.cmbCategory.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.cmbCategory.Location = new System.Drawing.Point(413, 32);
            this.cmbCategory.Name = "cmbCategory";
            this.cmbCategory.Size = new System.Drawing.Size(150, 24);
            this.cmbCategory.TabIndex = 3;
            // 
            // lblDateRange
            // 
            this.lblDateRange.AutoSize = true;
            this.lblDateRange.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.lblDateRange.Location = new System.Drawing.Point(583, 35);
            this.lblDateRange.Name = "lblDateRange";
            this.lblDateRange.Size = new System.Drawing.Size(88, 16);
            this.lblDateRange.TabIndex = 4;
            this.lblDateRange.Text = "时间范围：";
            // 
            // dtpStartDate
            // 
            this.dtpStartDate.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.dtpStartDate.Format = System.Windows.Forms.DateTimePickerFormat.Short;
            this.dtpStartDate.Location = new System.Drawing.Point(690, 32);
            this.dtpStartDate.Name = "dtpStartDate";
            this.dtpStartDate.Size = new System.Drawing.Size(150, 26);
            this.dtpStartDate.TabIndex = 5;
            // 
            // dtpEndDate
            // 
            this.dtpEndDate.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.dtpEndDate.Format = System.Windows.Forms.DateTimePickerFormat.Short;
            this.dtpEndDate.Location = new System.Drawing.Point(860, 32);
            this.dtpEndDate.Name = "dtpEndDate";
            this.dtpEndDate.Size = new System.Drawing.Size(150, 26);
            this.dtpEndDate.TabIndex = 6;
            // 
            // btnSearch
            // 
            this.btnSearch.BackColor = System.Drawing.SystemColors.HotTrack;
            this.btnSearch.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.btnSearch.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.btnSearch.Location = new System.Drawing.Point(1030, 30);
            this.btnSearch.Name = "btnSearch";
            this.btnSearch.Size = new System.Drawing.Size(100, 30);
            this.btnSearch.TabIndex = 7;
            this.btnSearch.Text = "查询";
            this.btnSearch.UseVisualStyleBackColor = false;
            this.btnSearch.Click += new System.EventHandler(this.BtnSearch_Click);
            // 
            // lblBookTitle
            // 
            this.lblBookTitle.AutoSize = true;
            this.lblBookTitle.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.lblBookTitle.Location = new System.Drawing.Point(20, 35);
            this.lblBookTitle.Name = "lblBookTitle";
            this.lblBookTitle.Size = new System.Drawing.Size(72, 16);
            this.lblBookTitle.TabIndex = 8;
            this.lblBookTitle.Text = "图书名：";
            // 
            // txtBookTitle
            // 
            this.txtBookTitle.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.txtBookTitle.Location = new System.Drawing.Point(135, 32);
            this.txtBookTitle.Name = "txtBookTitle";
            this.txtBookTitle.Size = new System.Drawing.Size(150, 26);
            this.txtBookTitle.TabIndex = 9;
            // 
            // btnPrint
            // 
            this.btnPrint.BackColor = System.Drawing.SystemColors.HotTrack;
            this.btnPrint.Enabled = false;
            this.btnPrint.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.btnPrint.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.btnPrint.Location = new System.Drawing.Point(1158, 30);
            this.btnPrint.Name = "btnPrint";
            this.btnPrint.Size = new System.Drawing.Size(100, 30);
            this.btnPrint.TabIndex = 8;
            this.btnPrint.Text = "打印报表";
            this.btnPrint.UseVisualStyleBackColor = false;
            this.btnPrint.Click += new System.EventHandler(this.BtnPrint_Click);
            // 
            // tabControl1
            // 
            this.tabControl1.Controls.Add(this.tabChart);
            this.tabControl1.Controls.Add(this.tabGrid);
            this.tabControl1.Location = new System.Drawing.Point(12, 100);
            this.tabControl1.Name = "tabControl1";
            this.tabControl1.SelectedIndex = 0;
            this.tabControl1.Size = new System.Drawing.Size(1160, 600);
            this.tabControl1.TabIndex = 1;
            // 
            // tabChart
            // 
            this.tabChart.Controls.Add(this.formsPlot);
            this.tabChart.Location = new System.Drawing.Point(4, 22);
            this.tabChart.Name = "tabChart";
            this.tabChart.Size = new System.Drawing.Size(1152, 574);
            this.tabChart.TabIndex = 0;
            this.tabChart.Text = "图表显示";
            // 
            // formsPlot
            // 
            this.formsPlot.DisplayScale = 0F;
            this.formsPlot.Dock = System.Windows.Forms.DockStyle.Fill;
            this.formsPlot.Location = new System.Drawing.Point(0, 0);
            this.formsPlot.Name = "formsPlot";
            this.formsPlot.Size = new System.Drawing.Size(1152, 574);
            this.formsPlot.TabIndex = 0;
            // 
            // tabGrid
            // 
            this.tabGrid.Controls.Add(this.dgvCirculation);
            this.tabGrid.Location = new System.Drawing.Point(4, 22);
            this.tabGrid.Name = "tabGrid";
            this.tabGrid.Size = new System.Drawing.Size(1152, 574);
            this.tabGrid.TabIndex = 1;
            this.tabGrid.Text = "表格显示";
            // 
            // dgvCirculation
            // 
            this.dgvCirculation.AllowUserToAddRows = false;
            this.dgvCirculation.AutoSizeColumnsMode = System.Windows.Forms.DataGridViewAutoSizeColumnsMode.Fill;
            this.dgvCirculation.ColumnHeadersHeight = 34;
            this.dgvCirculation.Dock = System.Windows.Forms.DockStyle.Fill;
            this.dgvCirculation.Location = new System.Drawing.Point(0, 0);
            this.dgvCirculation.Name = "dgvCirculation";
            this.dgvCirculation.ReadOnly = true;
            this.dgvCirculation.RowHeadersWidth = 62;
            this.dgvCirculation.Size = new System.Drawing.Size(1152, 574);
            this.dgvCirculation.TabIndex = 0;
            // 
            // printDocument
            // 
            this.printDocument.PrintPage += new System.Drawing.Printing.PrintPageEventHandler(this.PrintDocument_PrintPage);
            // 
            // printPreviewDialog
            // 
            this.printPreviewDialog.AutoScrollMargin = new System.Drawing.Size(0, 0);
            this.printPreviewDialog.AutoScrollMinSize = new System.Drawing.Size(0, 0);
            this.printPreviewDialog.ClientSize = new System.Drawing.Size(400, 300);
            this.printPreviewDialog.Document = this.printDocument;
            this.printPreviewDialog.Enabled = true;
            this.printPreviewDialog.Icon = ((System.Drawing.Icon)(resources.GetObject("printPreviewDialog.Icon")));
            this.printPreviewDialog.Name = "printPreviewDialog";
            this.printPreviewDialog.Visible = false;
            this.printPreviewDialog.WindowState = System.Windows.Forms.FormWindowState.Maximized;
            // 
            // CirculationStatsForm
            // 
            this.BackColor = System.Drawing.SystemColors.HotTrack;
            this.ClientSize = new System.Drawing.Size(1299, 708);
            this.Controls.Add(this.groupBox1);
            this.Controls.Add(this.tabControl1);
            this.Name = "CirculationStatsForm";
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Text = "流通统计";
            this.groupBox1.ResumeLayout(false);
            this.groupBox1.PerformLayout();
            this.tabControl1.ResumeLayout(false);
            this.tabChart.ResumeLayout(false);
            this.tabGrid.ResumeLayout(false);
            ((System.ComponentModel.ISupportInitialize)(this.dgvCirculation)).EndInit();
            this.ResumeLayout(false);

    }

    private void LoadCategories()
    {
        string sql = "SELECT DISTINCT Category FROM Book WHERE Category IS NOT NULL";
        DataTable dt = DBHelper.ExecuteQuery(sql);
        cmbCategory.Items.Clear();
        cmbCategory.Items.Add("全部");
        foreach (DataRow row in dt.Rows)
        {
            cmbCategory.Items.Add(row["Category"].ToString());
        }
        cmbCategory.SelectedIndex = 0;
    }

    private void BtnSearch_Click(object sender, EventArgs e)
    {
        string titleCondition = !string.IsNullOrEmpty(txtBookTitle.Text) ? 
            $"AND b.Title LIKE '%{txtBookTitle.Text}%'" : "";
        
        string categoryCondition = cmbCategory.Text != "全部" ? 
            $"AND b.Category = '{cmbCategory.Text}'" : "";

        string sql = $@"
            WITH BookStats AS (
                -- 首先找出所有符合条件的图书
                SELECT DISTINCT b.BookID
                FROM Book b
                WHERE 1=1 
                    {titleCondition}
                    {categoryCondition}
            ),
            MonthlyStats AS (
                SELECT 
                    FORMAT(br.BorrowDate, 'yyyy-MM') as Month,
                    COUNT(DISTINCT bs.BookID) as TotalBooks,  -- 不同图书种类数
                    COUNT(*) as BorrowCount  -- 借阅总次数
                FROM BookStats bs
                JOIN Borrow br ON bs.BookID = br.BookID
                WHERE br.BorrowDate BETWEEN '{dtpStartDate.Value:yyyy-MM-dd}' 
                    AND '{dtpEndDate.Value:yyyy-MM-dd}'
                GROUP BY FORMAT(br.BorrowDate, 'yyyy-MM')
            )
            SELECT 
                s1.Month,
                s1.TotalBooks as BookCount,    -- 图书种类数
                s1.BorrowCount,                -- 借阅次数
                CASE 
                    WHEN LAG(s1.BorrowCount) OVER (ORDER BY s1.Month) IS NULL THEN 0
                    ELSE ROUND((s1.BorrowCount - LAG(s1.BorrowCount) OVER (ORDER BY s1.Month)) * 100.0 / 
                         NULLIF(LAG(s1.BorrowCount) OVER (ORDER BY s1.Month), 0), 2)
                END as MoM,
                CASE 
                    WHEN s2.BorrowCount IS NULL OR s2.BorrowCount = 0 THEN 0
                    ELSE ROUND((s1.BorrowCount - s2.BorrowCount) * 100.0 / s2.BorrowCount, 2)
                END as YoY
            FROM MonthlyStats s1
            LEFT JOIN MonthlyStats s2 ON s2.Month = FORMAT(DATEADD(YEAR, -1, CAST(s1.Month + '-01' AS DATE)), 'yyyy-MM')
            ORDER BY s1.Month";

        DataTable dt = DBHelper.ExecuteQuery(sql);
        
        if (dt == null || dt.Rows.Count == 0)
        {
            MessageBox.Show("未查询到数据，请检查：\n1. 借阅表是否有数据\n2. 所选时间范围是否正确\n3. 图书编号和类型是否存在");
            return;
        }

        UpdateChart(dt);
        UpdateGrid(dt);
        btnPrint.Enabled = true;
    }

    private void UpdateChart(DataTable dt)
    {
        // 清除现有图表
        formsPlot.Plot.Clear();

        // 检查是否有数据
        if (dt == null || dt.Rows.Count == 0)
        {
            formsPlot.Plot.Title("暂无数据");
            formsPlot.Refresh();
            return;
        }

        // 准备数据
        double[] positions = new double[dt.Rows.Count];
        double[] borrowCounts = new double[dt.Rows.Count];
        double[] yoyChanges = new double[dt.Rows.Count];
        double[] momChanges = new double[dt.Rows.Count];
        string[] labels = new string[dt.Rows.Count];

        for (int i = 0; i < dt.Rows.Count; i++)
        {
            positions[i] = i;
            borrowCounts[i] = Convert.ToDouble(dt.Rows[i]["BorrowCount"]);
            yoyChanges[i] = Convert.ToDouble(dt.Rows[i]["YoY"]);
            momChanges[i] = Convert.ToDouble(dt.Rows[i]["MoM"]);
            labels[i] = dt.Rows[i]["Month"].ToString();
        }

        // 添加借阅量折线图
        var linePlot = formsPlot.Plot.Add.Scatter(
            positions, 
            borrowCounts);
        linePlot.Color = new ScottPlot.Color(0, 0, 255);
        linePlot.Label = "借阅量";
        linePlot.MarkerSize = 5;
        linePlot.LineWidth = 2;  // 使用 LineWidth 替代 ShowLine

        // 添加同比变化柱状图
        double[] yoyPositions = positions.Select(x => x - 0.2).ToArray();
        for (int i = 0; i < yoyChanges.Length; i++)
        {
            var yoyBar = formsPlot.Plot.Add.Bar(
                value: yoyChanges[i],
                position: yoyPositions[i]);
            yoyBar.Color = new ScottPlot.Color(255, 0, 0);
            if (i == 0) yoyBar.Label = "同比变化(%)";  // 只给第一个柱子添加标签
        }

        // 添加环比变化柱状图
        double[] momPositions = positions.Select(x => x + 0.2).ToArray();
        for (int i = 0; i < momChanges.Length; i++)
        {
            var momBar = formsPlot.Plot.Add.Bar(
                value: momChanges[i],
                position: momPositions[i]);
            momBar.Color = new ScottPlot.Color(0, 255, 0);
            if (i == 0) momBar.Label = "环比变化(%)";  // 只给第一个柱子添加标签
        }

        // 设置X轴标签和范围
        for (int i = 0; i < positions.Length; i++)
        {
            formsPlot.Plot.Add.Text(labels[i], positions[i], 0);
        }
        formsPlot.Plot.Axes.Bottom.Min = -0.5;
        formsPlot.Plot.Axes.Bottom.Max = Math.Max(positions.Length - 0.5, 0.5);  // 至少显示一个单位宽度
        double yMin = borrowCounts.Length > 0 ? 
            Math.Min(borrowCounts.Min(), Math.Min(yoyChanges.Min(), momChanges.Min())) : 0;
        double yMax = borrowCounts.Length > 0 ? 
            Math.Max(borrowCounts.Max(), Math.Max(yoyChanges.Max(), momChanges.Max())) : 100;
        formsPlot.Plot.Axes.Left.Min = yMin * 1.1;
        formsPlot.Plot.Axes.Left.Max = yMax * 1.1;

        // 设置标签旋转
        formsPlot.Plot.Axes.Bottom.Label.Rotation = 45;

        // 添加图例
        formsPlot.Plot.ShowLegend();

        // 设置标题和轴标签
        formsPlot.Plot.Title("图书流通统计");
        formsPlot.Plot.XLabel("月份");
        formsPlot.Plot.YLabel("数量/百分比");

        // 刷新图表
        formsPlot.Refresh();
    }

    private void UpdateGrid(DataTable dt)
    {
        dgvCirculation.DataSource = dt;
        if (dgvCirculation.Columns.Contains("BookCount"))
            dgvCirculation.Columns["BookCount"].HeaderText = "图书种类数";
        if (dgvCirculation.Columns.Contains("BorrowCount"))
            dgvCirculation.Columns["BorrowCount"].HeaderText = "借阅次数";
        if (dgvCirculation.Columns.Contains("Month"))
            dgvCirculation.Columns["Month"].HeaderText = "月份";
        if (dgvCirculation.Columns.Contains("MoM"))
            dgvCirculation.Columns["MoM"].HeaderText = "环比变化(%)";
        if (dgvCirculation.Columns.Contains("YoY"))
            dgvCirculation.Columns["YoY"].HeaderText = "同比变化(%)";
    }

    private void BtnPrint_Click(object sender, EventArgs e)
    {
        printPreviewDialog.ShowDialog();
    }

    private void PrintDocument_PrintPage(object sender, System.Drawing.Printing.PrintPageEventArgs e)
    {
        // 设置字体，使用完全限定名称
        Font titleFont = new Font("宋体", 16, System.Drawing.FontStyle.Bold);
        Font subtitleFont = new Font("宋体", 12, System.Drawing.FontStyle.Bold);
        Font contentFont = new Font("宋体", 10);

        // 页面边距
        int margin = 50;
        int currentY = margin;

        // 打印标题
        string title = "图书流通统计报表";
        SizeF titleSize = e.Graphics.MeasureString(title, titleFont);
        e.Graphics.DrawString(title, titleFont, Brushes.Black, 
            (e.PageBounds.Width - titleSize.Width) / 2, currentY);
        currentY += (int)titleSize.Height + 20;

        // 打印查询条件
        string conditions = $"统计时间：{dtpStartDate.Value:yyyy-MM-dd} 至 {dtpEndDate.Value:yyyy-MM-dd}\n";
        if (!string.IsNullOrEmpty(txtBookTitle.Text))
            conditions += $"图书名称：{txtBookTitle.Text}\n";
        if (cmbCategory.Text != "全部")
            conditions += $"图书类型：{cmbCategory.Text}\n";
        e.Graphics.DrawString(conditions, contentFont, Brushes.Black, margin, currentY);
        currentY += (int)e.Graphics.MeasureString(conditions, contentFont).Height + 20;

        // 打印图表说明
        string chartDescription = "图表说明：\n" +
            "- 蓝色折线：表示每月借阅总次数\n" +
            "- 红色柱形：表示同比变化率\n" +
            "- 绿色柱形：表示环比变化率\n";
        e.Graphics.DrawString(chartDescription, contentFont, Brushes.Black, margin, currentY);
        currentY += (int)e.Graphics.MeasureString(chartDescription, contentFont).Height + 20;

        // 打印图表
        if (formsPlot.Plot.GetPlottables().Any())
        {
            // 保存当前图表为位图
            using (var bmp = new Bitmap(formsPlot.Width, formsPlot.Height))
            {
                formsPlot.DrawToBitmap(bmp, new Rectangle(0, 0, formsPlot.Width, formsPlot.Height));
                
                // 调整图表大小以适应页面宽度
                float scale = (e.PageBounds.Width - 2 * margin) / (float)bmp.Width;
                int scaledWidth = (int)(bmp.Width * scale);
                int scaledHeight = (int)(bmp.Height * scale);
                
                e.Graphics.DrawImage(bmp, margin, currentY, scaledWidth, scaledHeight);
                currentY += scaledHeight + 40;
            }
        }

        // 打印表格标题
        e.Graphics.DrawString("详细数据", subtitleFont, Brushes.Black, margin, currentY);
        currentY += 30;

        // 打印表格
        if (dgvCirculation.Rows.Count > 0)
        {
            // 计算列宽
            int[] columnWidths = new int[dgvCirculation.Columns.Count];
            int totalWidth = e.PageBounds.Width - 2 * margin;
            int columnWidth = totalWidth / dgvCirculation.Columns.Count;

            // 打印表头
            int currentX = margin;
            for (int i = 0; i < dgvCirculation.Columns.Count; i++)
            {
                e.Graphics.DrawString(dgvCirculation.Columns[i].HeaderText,
                    contentFont, Brushes.Black, currentX, currentY);
                currentX += columnWidth;
            }
            currentY += 25;

            // 打印数据行
            foreach (DataGridViewRow row in dgvCirculation.Rows)
            {
                currentX = margin;
                for (int i = 0; i < dgvCirculation.Columns.Count; i++)
                {
                    if (row.Cells[i].Value != null)
                    {
                        e.Graphics.DrawString(row.Cells[i].Value.ToString(),
                            contentFont, Brushes.Black, currentX, currentY);
                    }
                    currentX += columnWidth;
                }
                currentY += 20;
            }
        }

        // 打印页脚
        string footer = $"打印时间：{DateTime.Now:yyyy-MM-dd HH:mm:ss}";
        SizeF footerSize = e.Graphics.MeasureString(footer, contentFont);
        e.Graphics.DrawString(footer, contentFont, Brushes.Black,
            e.PageBounds.Width - margin - footerSize.Width,
            e.PageBounds.Height - margin - footerSize.Height);
    }
} 