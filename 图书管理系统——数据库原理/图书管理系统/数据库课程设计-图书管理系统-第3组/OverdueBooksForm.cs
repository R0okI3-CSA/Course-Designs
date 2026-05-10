using System;
using System.Data;
using System.Drawing;
using System.Windows.Forms;
using System.Drawing.Printing;

public partial class OverdueBooksForm : Form
{
    private DataGridView dgvOverdueBooks;
    private Button btnSearch;
    private Button btnClearFilter;
    private Button btnPrintNotice;
    private TextBox txtSearch;
    private ComboBox cmbSearchType;
    private GroupBox groupBox1;
    private PrintDocument printDocument;
    private string selectedReaderName;
    private int overdueDays;

    public OverdueBooksForm()
    {
        InitializeComponent();
        LoadOverdueBooks();
        InitializeSearchTypes();
        InitializePrintDocument();
    }

    private void InitializeComponent()
    {
            this.dgvOverdueBooks = new System.Windows.Forms.DataGridView();
            this.btnSearch = new System.Windows.Forms.Button();
            this.btnClearFilter = new System.Windows.Forms.Button();
            this.btnPrintNotice = new System.Windows.Forms.Button();
            this.txtSearch = new System.Windows.Forms.TextBox();
            this.cmbSearchType = new System.Windows.Forms.ComboBox();
            this.groupBox1 = new System.Windows.Forms.GroupBox();
            this.printDocument = new System.Drawing.Printing.PrintDocument();
            ((System.ComponentModel.ISupportInitialize)(this.dgvOverdueBooks)).BeginInit();
            this.groupBox1.SuspendLayout();
            this.SuspendLayout();
            // 
            // dgvOverdueBooks
            // 
            this.dgvOverdueBooks.AllowUserToAddRows = false;
            this.dgvOverdueBooks.AutoSizeColumnsMode = System.Windows.Forms.DataGridViewAutoSizeColumnsMode.Fill;
            this.dgvOverdueBooks.BackgroundColor = System.Drawing.Color.LightGray;
            this.dgvOverdueBooks.Location = new System.Drawing.Point(12, 80);
            this.dgvOverdueBooks.Name = "dgvOverdueBooks";
            this.dgvOverdueBooks.ReadOnly = true;
            this.dgvOverdueBooks.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
            this.dgvOverdueBooks.Size = new System.Drawing.Size(1160, 620);
            this.dgvOverdueBooks.TabIndex = 1;
            this.dgvOverdueBooks.SelectionChanged += new System.EventHandler(this.DgvOverdueBooks_SelectionChanged);
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
            // btnPrintNotice
            // 
            this.btnPrintNotice.BackColor = System.Drawing.SystemColors.HotTrack;
            this.btnPrintNotice.Enabled = false;
            this.btnPrintNotice.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.btnPrintNotice.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.btnPrintNotice.Location = new System.Drawing.Point(560, 23);
            this.btnPrintNotice.Name = "btnPrintNotice";
            this.btnPrintNotice.Size = new System.Drawing.Size(120, 30);
            this.btnPrintNotice.TabIndex = 4;
            this.btnPrintNotice.Text = "打印催还通知单";
            this.btnPrintNotice.UseVisualStyleBackColor = false;
            this.btnPrintNotice.Click += new System.EventHandler(this.BtnPrintNotice_Click);
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
            this.groupBox1.Controls.Add(this.btnPrintNotice);
            this.groupBox1.Location = new System.Drawing.Point(12, 12);
            this.groupBox1.Name = "groupBox1";
            this.groupBox1.Size = new System.Drawing.Size(1160, 60);
            this.groupBox1.TabIndex = 0;
            this.groupBox1.TabStop = false;
            this.groupBox1.Text = "搜索";
            // 
            // OverdueBooksForm
            // 
            this.BackColor = System.Drawing.SystemColors.HotTrack;
            this.ClientSize = new System.Drawing.Size(1184, 711);
            this.Controls.Add(this.groupBox1);
            this.Controls.Add(this.dgvOverdueBooks);
            this.Name = "OverdueBooksForm";
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Text = "超期图书";
            ((System.ComponentModel.ISupportInitialize)(this.dgvOverdueBooks)).EndInit();
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
            "读者类型",
            "班级"
        });
        cmbSearchType.SelectedIndex = 0;
    }

    private void InitializePrintDocument()
    {
        printDocument.PrintPage += new PrintPageEventHandler(PrintDocument_PrintPage);
    }

    private void LoadOverdueBooks(string searchCondition = "")
    {
        string sql = @"SELECT b.BorrowID, b.CardID, b.BookID, bk.Title, 
                             b.BorrowDate, b.DueDate, b.Status, 
                             b.ClassName, b.ReaderType,
                             DATEDIFF(day, b.DueDate, GETDATE()) as OverdueDays
                      FROM Borrow b
                      JOIN Book bk ON b.BookID = bk.BookID
                      WHERE b.Status = 'overdue'";

        if (!string.IsNullOrEmpty(searchCondition))
        {
            sql += " AND " + searchCondition;
        }

        sql += " ORDER BY b.DueDate ASC";

        dgvOverdueBooks.DataSource = DBHelper.ExecuteQuery(sql);
    }

    private void BtnSearch_Click(object sender, EventArgs e)
    {
        string condition = "";
        switch (cmbSearchType.Text)
        {
            case "借阅编号":
                condition = $"b.BorrowID LIKE '%{txtSearch.Text}%'";
                break;
            case "借阅卡号":
                condition = $"b.CardID LIKE '%{txtSearch.Text}%'";
                break;
            case "图书编号":
                condition = $"b.BookID LIKE '%{txtSearch.Text}%'";
                break;
            case "读者类型":
                condition = $"b.ReaderType LIKE '%{txtSearch.Text}%'";
                break;
            case "班级":
                condition = $"b.ClassName LIKE '%{txtSearch.Text}%'";
                break;
        }
        LoadOverdueBooks(condition);
    }

    private void BtnClearFilter_Click(object sender, EventArgs e)
    {
        txtSearch.Clear();
        cmbSearchType.SelectedIndex = 0;
        LoadOverdueBooks();
    }

    private void DgvOverdueBooks_SelectionChanged(object sender, EventArgs e)
    {
        if (dgvOverdueBooks.SelectedRows.Count > 0)
        {
            string cardID = dgvOverdueBooks.SelectedRows[0].Cells["CardID"].Value.ToString();
            string sql = @"SELECT r.ReaderName 
                          FROM Reader r 
                          JOIN BorrowCard bc ON r.ReaderID = bc.ReaderID 
                          WHERE bc.CardID = @CardID";
            
            using (var cmd = new System.Data.SqlClient.SqlCommand(sql, DBHelper.GetConnection()))
            {
                cmd.Parameters.AddWithValue("@CardID", cardID);
                selectedReaderName = cmd.ExecuteScalar()?.ToString();
            }

            overdueDays = Convert.ToInt32(dgvOverdueBooks.SelectedRows[0].Cells["OverdueDays"].Value);
            btnPrintNotice.Enabled = true;
        }
        else
        {
            btnPrintNotice.Enabled = false;
        }
    }

    private void BtnPrintNotice_Click(object sender, EventArgs e)
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

        string title = "图书催还通知单";
        string content = $"尊敬的读者 {selectedReaderName}：\n\n" +
                        $"    您借阅的图书已超期 {overdueDays} 天，" +
                        $"请尽快归还。如有疑问，请与图书馆联系。\n\n" +
                        $"图书馆\n" +
                        $"{DateTime.Now.ToString("yyyy年MM月dd日")}";

        // 绘制标题
        StringFormat centerFormat = new StringFormat();
        centerFormat.Alignment = StringAlignment.Center;
        e.Graphics.DrawString(title, titleFont, Brushes.Black, 
            new RectangleF(0, 100, e.PageBounds.Width, 30), centerFormat);

        // 绘制内容
        e.Graphics.DrawString(content, contentFont, Brushes.Black, 100, 200);
    }
} 