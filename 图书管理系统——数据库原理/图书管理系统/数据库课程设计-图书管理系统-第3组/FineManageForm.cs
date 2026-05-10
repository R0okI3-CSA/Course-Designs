using System;
using System.Data;
using System.Drawing;
using System.Windows.Forms;
using System.Drawing.Printing;

public partial class FineManageForm : Form
{
    private DataGridView dgvFineRecord;
    private Button btnSearch;
    private Button btnClearFilter;
    private Button btnPrintBill;
    private TextBox txtSearch;
    private ComboBox cmbSearchType;
    private GroupBox groupBox1;
    private PrintDocument printDocument;
    private string selectedReaderName;
    private decimal fineAmount;
    private string fineReason;

    public FineManageForm()
    {
        InitializeComponent();
        LoadFineRecordData();
        InitializeSearchTypes();
        InitializePrintDocument();
    }

    private void InitializeComponent()
    {
            this.dgvFineRecord = new System.Windows.Forms.DataGridView();
            this.btnSearch = new System.Windows.Forms.Button();
            this.btnClearFilter = new System.Windows.Forms.Button();
            this.btnPrintBill = new System.Windows.Forms.Button();
            this.txtSearch = new System.Windows.Forms.TextBox();
            this.cmbSearchType = new System.Windows.Forms.ComboBox();
            this.groupBox1 = new System.Windows.Forms.GroupBox();
            this.printDocument = new System.Drawing.Printing.PrintDocument();
            ((System.ComponentModel.ISupportInitialize)(this.dgvFineRecord)).BeginInit();
            this.groupBox1.SuspendLayout();
            this.SuspendLayout();
            // 
            // dgvFineRecord
            // 
            this.dgvFineRecord.AllowUserToAddRows = false;
            this.dgvFineRecord.AutoSizeColumnsMode = System.Windows.Forms.DataGridViewAutoSizeColumnsMode.Fill;
            this.dgvFineRecord.BackgroundColor = System.Drawing.Color.LightGray;
            this.dgvFineRecord.ColumnHeadersHeight = 34;
            this.dgvFineRecord.Location = new System.Drawing.Point(12, 80);
            this.dgvFineRecord.Name = "dgvFineRecord";
            this.dgvFineRecord.ReadOnly = true;
            this.dgvFineRecord.RowHeadersWidth = 62;
            this.dgvFineRecord.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
            this.dgvFineRecord.Size = new System.Drawing.Size(1160, 620);
            this.dgvFineRecord.TabIndex = 1;
            this.dgvFineRecord.SelectionChanged += new System.EventHandler(this.DgvFineRecord_SelectionChanged);
            // 
            // btnSearch
            // 
            this.btnSearch.BackColor = System.Drawing.SystemColors.HotTrack;
            this.btnSearch.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.btnSearch.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.btnSearch.Location = new System.Drawing.Point(360, 23);
            this.btnSearch.Name = "btnSearch";
            this.btnSearch.Size = new System.Drawing.Size(80, 30);
            this.btnSearch.TabIndex = 2;
            this.btnSearch.Text = "搜索";
            this.btnSearch.UseVisualStyleBackColor = false;
            this.btnSearch.Click += new System.EventHandler(this.BtnSearch_Click);
            // 
            // btnClearFilter
            // 
            this.btnClearFilter.BackColor = System.Drawing.SystemColors.HotTrack;
            this.btnClearFilter.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.btnClearFilter.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.btnClearFilter.Location = new System.Drawing.Point(450, 23);
            this.btnClearFilter.Name = "btnClearFilter";
            this.btnClearFilter.Size = new System.Drawing.Size(100, 30);
            this.btnClearFilter.TabIndex = 3;
            this.btnClearFilter.Text = "取消筛选";
            this.btnClearFilter.UseVisualStyleBackColor = false;
            this.btnClearFilter.Click += new System.EventHandler(this.BtnClearFilter_Click);
            // 
            // btnPrintBill
            // 
            this.btnPrintBill.BackColor = System.Drawing.SystemColors.HotTrack;
            this.btnPrintBill.Enabled = false;
            this.btnPrintBill.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.btnPrintBill.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.btnPrintBill.Location = new System.Drawing.Point(560, 23);
            this.btnPrintBill.Name = "btnPrintBill";
            this.btnPrintBill.Size = new System.Drawing.Size(165, 30);
            this.btnPrintBill.TabIndex = 4;
            this.btnPrintBill.Text = "打印罚款结算单";
            this.btnPrintBill.UseVisualStyleBackColor = false;
            this.btnPrintBill.Click += new System.EventHandler(this.BtnPrintBill_Click);
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
            this.groupBox1.Controls.Add(this.btnSearch);
            this.groupBox1.Controls.Add(this.btnClearFilter);
            this.groupBox1.Controls.Add(this.btnPrintBill);
            this.groupBox1.Location = new System.Drawing.Point(12, 12);
            this.groupBox1.Name = "groupBox1";
            this.groupBox1.Size = new System.Drawing.Size(1160, 60);
            this.groupBox1.TabIndex = 0;
            this.groupBox1.TabStop = false;
            this.groupBox1.Text = "搜索";
            // 
            // FineManageForm
            // 
            this.BackColor = System.Drawing.SystemColors.HotTrack;
            this.ClientSize = new System.Drawing.Size(1178, 708);
            this.Controls.Add(this.groupBox1);
            this.Controls.Add(this.dgvFineRecord);
            this.Name = "FineManageForm";
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Text = "罚款管理";
            ((System.ComponentModel.ISupportInitialize)(this.dgvFineRecord)).EndInit();
            this.groupBox1.ResumeLayout(false);
            this.groupBox1.PerformLayout();
            this.ResumeLayout(false);

    }

    private void InitializeSearchTypes()
    {
        cmbSearchType.Items.AddRange(new string[] {
            "罚款编号",
            "借阅卡号",
            "罚款原因",
            "状态"
        });
        cmbSearchType.SelectedIndex = 0;
    }

    private void InitializePrintDocument()
    {
        printDocument.PrintPage += new PrintPageEventHandler(PrintDocument_PrintPage);
    }

    private void LoadFineRecordData(string searchCondition = "")
    {
        string sql = "SELECT * FROM FineRecord";
        if (!string.IsNullOrEmpty(searchCondition))
        {
            sql += " WHERE " + searchCondition;
        }
        dgvFineRecord.DataSource = DBHelper.ExecuteQuery(sql);
    }

    private void BtnSearch_Click(object sender, EventArgs e)
    {
        string condition = "";
        switch (cmbSearchType.Text)
        {
            case "罚款编号":
                condition = $"FineID LIKE '%{txtSearch.Text}%'";
                break;
            case "借阅卡号":
                condition = $"CardID LIKE '%{txtSearch.Text}%'";
                break;
            case "罚款原因":
                condition = $"Reason LIKE '%{txtSearch.Text}%'";
                break;
            case "状态":
                condition = $"Status LIKE '%{txtSearch.Text}%'";
                break;
        }
        LoadFineRecordData(condition);
    }

    private void BtnClearFilter_Click(object sender, EventArgs e)
    {
        txtSearch.Clear();
        cmbSearchType.SelectedIndex = 0;
        LoadFineRecordData();
    }

    private void DgvFineRecord_SelectionChanged(object sender, EventArgs e)
    {
        if (dgvFineRecord.SelectedRows.Count > 0)
        {
            string cardID = dgvFineRecord.SelectedRows[0].Cells["CardID"].Value.ToString();
            string sql = @"SELECT r.ReaderName 
                          FROM Reader r 
                          JOIN BorrowCard bc ON r.ReaderID = bc.ReaderID 
                          WHERE bc.CardID = @CardID";
            
            using (var cmd = new System.Data.SqlClient.SqlCommand(sql, DBHelper.GetConnection()))
            {
                cmd.Parameters.AddWithValue("@CardID", cardID);
                selectedReaderName = cmd.ExecuteScalar()?.ToString();
            }

            fineAmount = Convert.ToDecimal(dgvFineRecord.SelectedRows[0].Cells["Amount"].Value);
            fineReason = dgvFineRecord.SelectedRows[0].Cells["Reason"].Value.ToString();
            btnPrintBill.Enabled = true;
        }
        else
        {
            btnPrintBill.Enabled = false;
        }
    }

    private void BtnPrintBill_Click(object sender, EventArgs e)
    {
        PrintDialog printDialog = new PrintDialog();
        printDialog.Document = printDocument;
        
        if (printDialog.ShowDialog() == DialogResult.OK)
        {
            printDocument.Print();
        }
    }

    private void PrintDocument_PrintPage(object sender, PrintPageEventArgs e)
    {
        Font titleFont = new Font("宋体", 16, FontStyle.Bold);
        Font contentFont = new Font("宋体", 12);

        string title = "图书馆罚款结算单";
        string content = $"读者姓名：{selectedReaderName}\n\n" +
                        $"罚款金额：{fineAmount:C2}\n\n" +
                        $"罚款原因：{fineReason}\n\n" +
                        $"缴纳说明：\n" +
                        $"1. 请携带本结算单和有效证件到图书馆服务台\n" +
                        $"2. 可选择现金或校园卡支付罚款\n" +
                        $"3. 缴纳完成后请保管好收据\n" +
                        $"4. 如有疑问，请咨询图书馆工作人员\n\n" +
                        $"打印日期：{DateTime.Now.ToString("yyyy年MM月dd日")}";

        // 绘制标题
        StringFormat centerFormat = new StringFormat();
        centerFormat.Alignment = StringAlignment.Center;
        e.Graphics.DrawString(title, titleFont, Brushes.Black, 
            new RectangleF(0, 100, e.PageBounds.Width, 30), centerFormat);

        // 绘制内容
        e.Graphics.DrawString(content, contentFont, Brushes.Black, 100, 200);
    }
} 