using System;
using System.Data;
using System.Drawing;
using System.Windows.Forms;

public partial class BookManageForm : Form
{
    private DataGridView dgvBook;
    private TextBox txtBookID;
    private TextBox txtISBN;
    private TextBox txtTitle;
    private TextBox txtAuthor;
    private TextBox txtPublisher;
    private DateTimePicker dtpPublishDate;
    private TextBox txtPrice;
    private ComboBox cmbCategory;
    private ComboBox cmbStatus;
    private Button btnAdd;
    private Button btnEdit;
    private Button btnDelete;
    private Button btnSearch;
    private TextBox txtSearch;
    private ComboBox cmbSearchType;
    private Label lblBookID;
    private Label lblISBN;
    private Label lblTitle;
    private Label lblAuthor;
    private Label lblPublisher;
    private Label lblPublishDate;
    private Label lblPrice;
    private Label lblCategory;
    private Label lblStatus;
    private GroupBox groupBox1;
    private GroupBox groupBox2;
    private Button btnReset;
    private Button btnClearFilter;

    public BookManageForm()
    {
        InitializeComponent();
        LoadBookData();
        InitializeSearchTypes();
    }

    private void InitializeComponent()
    {
            this.dgvBook = new System.Windows.Forms.DataGridView();
            this.txtBookID = new System.Windows.Forms.TextBox();
            this.txtISBN = new System.Windows.Forms.TextBox();
            this.txtTitle = new System.Windows.Forms.TextBox();
            this.txtAuthor = new System.Windows.Forms.TextBox();
            this.txtPublisher = new System.Windows.Forms.TextBox();
            this.dtpPublishDate = new System.Windows.Forms.DateTimePicker();
            this.txtPrice = new System.Windows.Forms.TextBox();
            this.cmbCategory = new System.Windows.Forms.ComboBox();
            this.cmbStatus = new System.Windows.Forms.ComboBox();
            this.btnAdd = new System.Windows.Forms.Button();
            this.btnEdit = new System.Windows.Forms.Button();
            this.btnDelete = new System.Windows.Forms.Button();
            this.btnSearch = new System.Windows.Forms.Button();
            this.txtSearch = new System.Windows.Forms.TextBox();
            this.cmbSearchType = new System.Windows.Forms.ComboBox();
            this.lblBookID = new System.Windows.Forms.Label();
            this.lblISBN = new System.Windows.Forms.Label();
            this.lblTitle = new System.Windows.Forms.Label();
            this.lblAuthor = new System.Windows.Forms.Label();
            this.lblPublisher = new System.Windows.Forms.Label();
            this.lblPublishDate = new System.Windows.Forms.Label();
            this.lblPrice = new System.Windows.Forms.Label();
            this.lblCategory = new System.Windows.Forms.Label();
            this.lblStatus = new System.Windows.Forms.Label();
            this.groupBox1 = new System.Windows.Forms.GroupBox();
            this.btnReset = new System.Windows.Forms.Button();
            this.groupBox2 = new System.Windows.Forms.GroupBox();
            this.btnClearFilter = new System.Windows.Forms.Button();
            ((System.ComponentModel.ISupportInitialize)(this.dgvBook)).BeginInit();
            this.groupBox1.SuspendLayout();
            this.groupBox2.SuspendLayout();
            this.SuspendLayout();
            // 
            // dgvBook
            // 
            this.dgvBook.AllowUserToAddRows = false;
            this.dgvBook.AutoSizeColumnsMode = System.Windows.Forms.DataGridViewAutoSizeColumnsMode.Fill;
            this.dgvBook.BackgroundColor = System.Drawing.Color.LightGray;
            this.dgvBook.ColumnHeadersHeight = 34;
            this.dgvBook.Location = new System.Drawing.Point(12, 300);
            this.dgvBook.Name = "dgvBook";
            this.dgvBook.ReadOnly = true;
            this.dgvBook.RowHeadersWidth = 62;
            this.dgvBook.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
            this.dgvBook.Size = new System.Drawing.Size(1160, 350);
            this.dgvBook.TabIndex = 2;
            this.dgvBook.CellClick += new System.Windows.Forms.DataGridViewCellEventHandler(this.DgvBook_CellClick);
            // 
            // txtBookID
            // 
            this.txtBookID.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.txtBookID.Location = new System.Drawing.Point(126, 27);
            this.txtBookID.Name = "txtBookID";
            this.txtBookID.Size = new System.Drawing.Size(150, 26);
            this.txtBookID.TabIndex = 1;
            // 
            // txtISBN
            // 
            this.txtISBN.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.txtISBN.Location = new System.Drawing.Point(376, 27);
            this.txtISBN.Name = "txtISBN";
            this.txtISBN.Size = new System.Drawing.Size(150, 26);
            this.txtISBN.TabIndex = 3;
            // 
            // txtTitle
            // 
            this.txtTitle.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.txtTitle.Location = new System.Drawing.Point(649, 27);
            this.txtTitle.Name = "txtTitle";
            this.txtTitle.Size = new System.Drawing.Size(200, 26);
            this.txtTitle.TabIndex = 5;
            // 
            // txtAuthor
            // 
            this.txtAuthor.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.txtAuthor.Location = new System.Drawing.Point(100, 77);
            this.txtAuthor.Name = "txtAuthor";
            this.txtAuthor.Size = new System.Drawing.Size(150, 26);
            this.txtAuthor.TabIndex = 7;
            // 
            // txtPublisher
            // 
            this.txtPublisher.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.txtPublisher.Location = new System.Drawing.Point(378, 77);
            this.txtPublisher.Name = "txtPublisher";
            this.txtPublisher.Size = new System.Drawing.Size(150, 26);
            this.txtPublisher.TabIndex = 9;
            // 
            // dtpPublishDate
            // 
            this.dtpPublishDate.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.dtpPublishDate.Location = new System.Drawing.Point(665, 77);
            this.dtpPublishDate.Name = "dtpPublishDate";
            this.dtpPublishDate.Size = new System.Drawing.Size(200, 26);
            this.dtpPublishDate.TabIndex = 11;
            this.dtpPublishDate.ValueChanged += new System.EventHandler(this.dtpPublishDate_ValueChanged);
            // 
            // txtPrice
            // 
            this.txtPrice.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.txtPrice.Location = new System.Drawing.Point(100, 127);
            this.txtPrice.Name = "txtPrice";
            this.txtPrice.Size = new System.Drawing.Size(150, 26);
            this.txtPrice.TabIndex = 13;
            // 
            // cmbCategory
            // 
            this.cmbCategory.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.cmbCategory.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.cmbCategory.Items.AddRange(new object[] {
            "Literature",
            "Technology",
            "History",
            "Art",
            "Education",
            "else"});
            this.cmbCategory.Location = new System.Drawing.Point(376, 127);
            this.cmbCategory.Name = "cmbCategory";
            this.cmbCategory.Size = new System.Drawing.Size(150, 24);
            this.cmbCategory.TabIndex = 15;
            // 
            // cmbStatus
            // 
            this.cmbStatus.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.cmbStatus.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.cmbStatus.Items.AddRange(new object[] {
            "Instock",
            "Lending",
            "Reservation",
            "Repairing"});
            this.cmbStatus.Location = new System.Drawing.Point(647, 127);
            this.cmbStatus.Name = "cmbStatus";
            this.cmbStatus.Size = new System.Drawing.Size(150, 24);
            this.cmbStatus.TabIndex = 17;
            // 
            // btnAdd
            // 
            this.btnAdd.BackColor = System.Drawing.SystemColors.HotTrack;
            this.btnAdd.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.btnAdd.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.btnAdd.Location = new System.Drawing.Point(219, 180);
            this.btnAdd.Name = "btnAdd";
            this.btnAdd.Size = new System.Drawing.Size(120, 30);
            this.btnAdd.TabIndex = 18;
            this.btnAdd.Text = "添加";
            this.btnAdd.UseVisualStyleBackColor = false;
            this.btnAdd.Click += new System.EventHandler(this.BtnAdd_Click);
            // 
            // btnEdit
            // 
            this.btnEdit.BackColor = System.Drawing.SystemColors.HotTrack;
            this.btnEdit.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.btnEdit.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.btnEdit.Location = new System.Drawing.Point(419, 180);
            this.btnEdit.Name = "btnEdit";
            this.btnEdit.Size = new System.Drawing.Size(120, 30);
            this.btnEdit.TabIndex = 19;
            this.btnEdit.Text = "修改";
            this.btnEdit.UseVisualStyleBackColor = false;
            this.btnEdit.Click += new System.EventHandler(this.BtnEdit_Click);
            // 
            // btnDelete
            // 
            this.btnDelete.BackColor = System.Drawing.SystemColors.HotTrack;
            this.btnDelete.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.btnDelete.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.btnDelete.Location = new System.Drawing.Point(619, 180);
            this.btnDelete.Name = "btnDelete";
            this.btnDelete.Size = new System.Drawing.Size(120, 30);
            this.btnDelete.TabIndex = 20;
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
            // lblBookID
            // 
            this.lblBookID.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.lblBookID.Location = new System.Drawing.Point(20, 30);
            this.lblBookID.Name = "lblBookID";
            this.lblBookID.Size = new System.Drawing.Size(100, 23);
            this.lblBookID.TabIndex = 0;
            this.lblBookID.Text = "图书编号：";
            // 
            // lblISBN
            // 
            this.lblISBN.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.lblISBN.Location = new System.Drawing.Point(308, 30);
            this.lblISBN.Name = "lblISBN";
            this.lblISBN.Size = new System.Drawing.Size(62, 23);
            this.lblISBN.TabIndex = 2;
            this.lblISBN.Text = "ISBN：";
            // 
            // lblTitle
            // 
            this.lblTitle.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.lblTitle.Location = new System.Drawing.Point(580, 30);
            this.lblTitle.Name = "lblTitle";
            this.lblTitle.Size = new System.Drawing.Size(63, 23);
            this.lblTitle.TabIndex = 4;
            this.lblTitle.Text = "书名：";
            // 
            // lblAuthor
            // 
            this.lblAuthor.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.lblAuthor.Location = new System.Drawing.Point(20, 80);
            this.lblAuthor.Name = "lblAuthor";
            this.lblAuthor.Size = new System.Drawing.Size(74, 23);
            this.lblAuthor.TabIndex = 6;
            this.lblAuthor.Text = "作者：";
            // 
            // lblPublisher
            // 
            this.lblPublisher.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.lblPublisher.Location = new System.Drawing.Point(290, 80);
            this.lblPublisher.Name = "lblPublisher";
            this.lblPublisher.Size = new System.Drawing.Size(80, 23);
            this.lblPublisher.TabIndex = 8;
            this.lblPublisher.Text = "出版社：";
            // 
            // lblPublishDate
            // 
            this.lblPublishDate.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.lblPublishDate.Location = new System.Drawing.Point(559, 80);
            this.lblPublishDate.Name = "lblPublishDate";
            this.lblPublishDate.Size = new System.Drawing.Size(100, 23);
            this.lblPublishDate.TabIndex = 10;
            this.lblPublishDate.Text = "出版日期：";
            // 
            // lblPrice
            // 
            this.lblPrice.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.lblPrice.Location = new System.Drawing.Point(20, 130);
            this.lblPrice.Name = "lblPrice";
            this.lblPrice.Size = new System.Drawing.Size(74, 23);
            this.lblPrice.TabIndex = 12;
            this.lblPrice.Text = "价格：";
            // 
            // lblCategory
            // 
            this.lblCategory.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.lblCategory.Location = new System.Drawing.Point(291, 130);
            this.lblCategory.Name = "lblCategory";
            this.lblCategory.Size = new System.Drawing.Size(79, 23);
            this.lblCategory.TabIndex = 14;
            this.lblCategory.Text = "分类：";
            // 
            // lblStatus
            // 
            this.lblStatus.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.lblStatus.Location = new System.Drawing.Point(568, 130);
            this.lblStatus.Name = "lblStatus";
            this.lblStatus.Size = new System.Drawing.Size(75, 23);
            this.lblStatus.TabIndex = 16;
            this.lblStatus.Text = "状态：";
            // 
            // groupBox1
            // 
            this.groupBox1.BackColor = System.Drawing.Color.White;
            this.groupBox1.Controls.Add(this.lblBookID);
            this.groupBox1.Controls.Add(this.txtBookID);
            this.groupBox1.Controls.Add(this.lblISBN);
            this.groupBox1.Controls.Add(this.txtISBN);
            this.groupBox1.Controls.Add(this.lblTitle);
            this.groupBox1.Controls.Add(this.txtTitle);
            this.groupBox1.Controls.Add(this.lblAuthor);
            this.groupBox1.Controls.Add(this.txtAuthor);
            this.groupBox1.Controls.Add(this.lblPublisher);
            this.groupBox1.Controls.Add(this.txtPublisher);
            this.groupBox1.Controls.Add(this.lblPublishDate);
            this.groupBox1.Controls.Add(this.dtpPublishDate);
            this.groupBox1.Controls.Add(this.lblPrice);
            this.groupBox1.Controls.Add(this.txtPrice);
            this.groupBox1.Controls.Add(this.lblCategory);
            this.groupBox1.Controls.Add(this.cmbCategory);
            this.groupBox1.Controls.Add(this.lblStatus);
            this.groupBox1.Controls.Add(this.cmbStatus);
            this.groupBox1.Controls.Add(this.btnAdd);
            this.groupBox1.Controls.Add(this.btnEdit);
            this.groupBox1.Controls.Add(this.btnDelete);
            this.groupBox1.Controls.Add(this.btnReset);
            this.groupBox1.Location = new System.Drawing.Point(12, 12);
            this.groupBox1.Name = "groupBox1";
            this.groupBox1.Size = new System.Drawing.Size(1160, 230);
            this.groupBox1.TabIndex = 0;
            this.groupBox1.TabStop = false;
            this.groupBox1.Text = "图书信息";
            // 
            // btnReset
            // 
            this.btnReset.BackColor = System.Drawing.SystemColors.HotTrack;
            this.btnReset.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.btnReset.ForeColor = System.Drawing.SystemColors.ControlLightLight;
            this.btnReset.Location = new System.Drawing.Point(819, 180);
            this.btnReset.Name = "btnReset";
            this.btnReset.Size = new System.Drawing.Size(120, 30);
            this.btnReset.TabIndex = 21;
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
            this.groupBox2.Location = new System.Drawing.Point(12, 250);
            this.groupBox2.Name = "groupBox2";
            this.groupBox2.Size = new System.Drawing.Size(1160, 45);
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
            // BookManageForm
            // 
            this.BackColor = System.Drawing.SystemColors.HotTrack;
            this.ClientSize = new System.Drawing.Size(1178, 659);
            this.Controls.Add(this.groupBox1);
            this.Controls.Add(this.groupBox2);
            this.Controls.Add(this.dgvBook);
            this.Name = "BookManageForm";
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Text = "图书管理";
            ((System.ComponentModel.ISupportInitialize)(this.dgvBook)).EndInit();
            this.groupBox1.ResumeLayout(false);
            this.groupBox1.PerformLayout();
            this.groupBox2.ResumeLayout(false);
            this.groupBox2.PerformLayout();
            this.ResumeLayout(false);

    }

    private void InitializeSearchTypes()
    {
        cmbSearchType.Items.AddRange(new string[] {
            "图书编号",
            "ISBN",
            "书名",
            "作者",
            "出版社",
            "分类",
            "状态"
        });
        cmbSearchType.SelectedIndex = 0;
    }

    private void LoadBookData(string searchCondition = "")
    {
        string sql = "SELECT * FROM Book";
        if (!string.IsNullOrEmpty(searchCondition))
        {
            sql += " WHERE " + searchCondition;
        }
        dgvBook.DataSource = DBHelper.ExecuteQuery(sql);
    }

    private void BtnAdd_Click(object sender, EventArgs e)
    {
        if (ValidateInput())
        {
            string sql = $@"INSERT INTO Book VALUES (
                '{txtBookID.Text}',
                '{txtISBN.Text}',
                '{txtTitle.Text}',
                '{txtAuthor.Text}',
                '{txtPublisher.Text}',
                '{dtpPublishDate.Value.ToString("yyyy-MM-dd")}',
                {txtPrice.Text},
                '{cmbCategory.Text}',
                '{cmbStatus.Text}'
            )";

            try
            {
                DBHelper.ExecuteNonQuery(sql);
                MessageBox.Show("添加成功！");
                LoadBookData();
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
            string sql = $@"UPDATE Book SET 
                ISBN = '{txtISBN.Text}',
                Title = '{txtTitle.Text}',
                Author = '{txtAuthor.Text}',
                Publisher = '{txtPublisher.Text}',
                PublishDate = '{dtpPublishDate.Value.ToString("yyyy-MM-dd")}',
                Price = {txtPrice.Text},
                Category = '{cmbCategory.Text}',
                Status = '{cmbStatus.Text}'
                WHERE BookID = '{txtBookID.Text}'";

            try
            {
                DBHelper.ExecuteNonQuery(sql);
                MessageBox.Show("修改成功！");
                LoadBookData();
            }
            catch (Exception ex)
            {
                MessageBox.Show("修改失败：" + ex.Message);
            }
        }
    }

    private void BtnDelete_Click(object sender, EventArgs e)
    {
        if (string.IsNullOrEmpty(txtBookID.Text))
        {
            MessageBox.Show("请先选择要删除的图书！");
            return;
        }

        if (MessageBox.Show("确定要删除该图书信息吗？", "确认删除", 
            MessageBoxButtons.YesNo) == DialogResult.Yes)
        {
            string sql = $"DELETE FROM Book WHERE BookID = '{txtBookID.Text}'";

            try
            {
                DBHelper.ExecuteNonQuery(sql);
                MessageBox.Show("删除成功！");
                LoadBookData();
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
            case "图书编号":
                condition = $"BookID LIKE '%{txtSearch.Text}%'";
                break;
            case "ISBN":
                condition = $"ISBN LIKE '%{txtSearch.Text}%'";
                break;
            case "书名":
                condition = $"Title LIKE '%{txtSearch.Text}%'";
                break;
            case "作者":
                condition = $"Author LIKE '%{txtSearch.Text}%'";
                break;
            case "出版社":
                condition = $"Publisher LIKE '%{txtSearch.Text}%'";
                break;
            case "分类":
                condition = $"Category LIKE '%{txtSearch.Text}%'";
                break;
            case "状态":
                condition = $"Status LIKE '%{txtSearch.Text}%'";
                break;
        }
        LoadBookData(condition);
    }

    private void DgvBook_CellClick(object sender, DataGridViewCellEventArgs e)
    {
        if (e.RowIndex >= 0)
        {
            DataGridViewRow row = dgvBook.Rows[e.RowIndex];
            txtBookID.Text = row.Cells["BookID"].Value.ToString();
            txtISBN.Text = row.Cells["ISBN"].Value.ToString();
            txtTitle.Text = row.Cells["Title"].Value.ToString();
            txtAuthor.Text = row.Cells["Author"].Value.ToString();
            txtPublisher.Text = row.Cells["Publisher"].Value.ToString();
            dtpPublishDate.Value = Convert.ToDateTime(row.Cells["PublishDate"].Value);
            txtPrice.Text = row.Cells["Price"].Value.ToString();
            cmbCategory.Text = row.Cells["Category"].Value.ToString();
            cmbStatus.Text = row.Cells["Status"].Value.ToString();
        }
    }

    private bool ValidateInput()
    {
        if (string.IsNullOrEmpty(txtBookID.Text))
        {
            MessageBox.Show("图书编号不能为空！");
            return false;
        }
        if (string.IsNullOrEmpty(txtISBN.Text))
        {
            MessageBox.Show("ISBN不能为空！");
            return false;
        }
        if (string.IsNullOrEmpty(txtTitle.Text))
        {
            MessageBox.Show("书名不能为空！");
            return false;
        }
        if (!decimal.TryParse(txtPrice.Text, out _))
        {
            MessageBox.Show("价格必须是有效的数字！");
            return false;
        }
        if (string.IsNullOrEmpty(cmbCategory.Text))
        {
            MessageBox.Show("请选择图书分类！");
            return false;
        }
        if (string.IsNullOrEmpty(cmbStatus.Text))
        {
            MessageBox.Show("请选择图书状态！");
            return false;
        }
        return true;
    }

    private void ClearInputs()
    {
        txtBookID.Clear();
        txtISBN.Clear();
        txtTitle.Clear();
        txtAuthor.Clear();
        txtPublisher.Clear();
        dtpPublishDate.Value = DateTime.Now;
        txtPrice.Clear();
        cmbCategory.SelectedIndex = -1;
        cmbStatus.SelectedIndex = -1;
    }

    private void dtpPublishDate_ValueChanged(object sender, EventArgs e)
    {

    }

    private void BtnReset_Click(object sender, EventArgs e)
    {
        ClearInputs();
    }

    private void BtnClearFilter_Click(object sender, EventArgs e)
    {
        txtSearch.Clear();
        cmbSearchType.SelectedIndex = 0;
        LoadBookData();
    }
}