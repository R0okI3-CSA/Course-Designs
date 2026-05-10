using System;
using System.Data;
using System.Drawing;
using System.Windows.Forms;

public partial class BorrowCardManageForm : Form
{
    private DataGridView dgvBorrowCard;
    private TextBox txtCardID;
    private TextBox txtReaderID;
    private DateTimePicker dtpValidityDate;
    private ComboBox cmbStatus;
    private Button btnAdd;
    private Button btnEdit;
    private Button btnDelete;
    private Button btnReset;
    private Button btnSearch;
    private Button btnClearFilter;
    private TextBox txtSearch;
    private ComboBox cmbSearchType;
    private Label lblCardID;
    private Label lblReaderID;
    private Label lblValidityDate;
    private Label lblStatus;
    private GroupBox groupBox1;
    private GroupBox groupBox2;

    public BorrowCardManageForm()
    {
        InitializeComponent();
        LoadBorrowCardData();
        InitializeSearchTypes();
    }

    private void InitializeComponent()
    {
            this.dgvBorrowCard = new System.Windows.Forms.DataGridView();
            this.txtCardID = new System.Windows.Forms.TextBox();
            this.txtReaderID = new System.Windows.Forms.TextBox();
            this.dtpValidityDate = new System.Windows.Forms.DateTimePicker();
            this.cmbStatus = new System.Windows.Forms.ComboBox();
            this.btnAdd = new System.Windows.Forms.Button();
            this.btnEdit = new System.Windows.Forms.Button();
            this.btnDelete = new System.Windows.Forms.Button();
            this.btnReset = new System.Windows.Forms.Button();
            this.btnSearch = new System.Windows.Forms.Button();
            this.btnClearFilter = new System.Windows.Forms.Button();
            this.txtSearch = new System.Windows.Forms.TextBox();
            this.cmbSearchType = new System.Windows.Forms.ComboBox();
            this.lblCardID = new System.Windows.Forms.Label();
            this.lblReaderID = new System.Windows.Forms.Label();
            this.lblValidityDate = new System.Windows.Forms.Label();
            this.lblStatus = new System.Windows.Forms.Label();
            this.groupBox1 = new System.Windows.Forms.GroupBox();
            this.groupBox2 = new System.Windows.Forms.GroupBox();
            ((System.ComponentModel.ISupportInitialize)(this.dgvBorrowCard)).BeginInit();
            this.groupBox1.SuspendLayout();
            this.groupBox2.SuspendLayout();
            this.SuspendLayout();
            // 
            // dgvBorrowCard
            // 
            this.dgvBorrowCard.AllowUserToAddRows = false;
            this.dgvBorrowCard.AutoSizeColumnsMode = System.Windows.Forms.DataGridViewAutoSizeColumnsMode.Fill;
            this.dgvBorrowCard.ColumnHeadersHeight = 34;
            this.dgvBorrowCard.Location = new System.Drawing.Point(12, 250);
            this.dgvBorrowCard.Name = "dgvBorrowCard";
            this.dgvBorrowCard.ReadOnly = true;
            this.dgvBorrowCard.RowHeadersWidth = 62;
            this.dgvBorrowCard.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
            this.dgvBorrowCard.Size = new System.Drawing.Size(960, 300);
            this.dgvBorrowCard.TabIndex = 2;
            this.dgvBorrowCard.CellClick += new System.Windows.Forms.DataGridViewCellEventHandler(this.DgvBorrowCard_CellClick);
            // 
            // txtCardID
            // 
            this.txtCardID.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.txtCardID.Location = new System.Drawing.Point(129, 27);
            this.txtCardID.Name = "txtCardID";
            this.txtCardID.Size = new System.Drawing.Size(150, 26);
            this.txtCardID.TabIndex = 1;
            // 
            // txtReaderID
            // 
            this.txtReaderID.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.txtReaderID.Location = new System.Drawing.Point(410, 27);
            this.txtReaderID.Name = "txtReaderID";
            this.txtReaderID.Size = new System.Drawing.Size(150, 26);
            this.txtReaderID.TabIndex = 3;
            // 
            // dtpValidityDate
            // 
            this.dtpValidityDate.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.dtpValidityDate.Location = new System.Drawing.Point(129, 77);
            this.dtpValidityDate.Name = "dtpValidityDate";
            this.dtpValidityDate.Size = new System.Drawing.Size(150, 26);
            this.dtpValidityDate.TabIndex = 5;
            // 
            // cmbStatus
            // 
            this.cmbStatus.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.cmbStatus.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.cmbStatus.Items.AddRange(new object[] {
            "normal",
            "lost",
            "logoff"});
            this.cmbStatus.Location = new System.Drawing.Point(410, 77);
            this.cmbStatus.Name = "cmbStatus";
            this.cmbStatus.Size = new System.Drawing.Size(150, 24);
            this.cmbStatus.TabIndex = 7;
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
            // lblCardID
            // 
            this.lblCardID.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.lblCardID.Location = new System.Drawing.Point(20, 30);
            this.lblCardID.Name = "lblCardID";
            this.lblCardID.Size = new System.Drawing.Size(100, 23);
            this.lblCardID.TabIndex = 0;
            this.lblCardID.Text = "借阅卡号：";
            // 
            // lblReaderID
            // 
            this.lblReaderID.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.lblReaderID.Location = new System.Drawing.Point(299, 30);
            this.lblReaderID.Name = "lblReaderID";
            this.lblReaderID.Size = new System.Drawing.Size(100, 23);
            this.lblReaderID.TabIndex = 2;
            this.lblReaderID.Text = "读者编号：";
            // 
            // lblValidityDate
            // 
            this.lblValidityDate.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.lblValidityDate.Location = new System.Drawing.Point(20, 80);
            this.lblValidityDate.Name = "lblValidityDate";
            this.lblValidityDate.Size = new System.Drawing.Size(100, 23);
            this.lblValidityDate.TabIndex = 4;
            this.lblValidityDate.Text = "有效期至：";
            // 
            // lblStatus
            // 
            this.lblStatus.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.lblStatus.Location = new System.Drawing.Point(299, 80);
            this.lblStatus.Name = "lblStatus";
            this.lblStatus.Size = new System.Drawing.Size(100, 23);
            this.lblStatus.TabIndex = 6;
            this.lblStatus.Text = "状态：";
            // 
            // groupBox1
            // 
            this.groupBox1.BackColor = System.Drawing.Color.White;
            this.groupBox1.Controls.Add(this.lblCardID);
            this.groupBox1.Controls.Add(this.txtCardID);
            this.groupBox1.Controls.Add(this.lblReaderID);
            this.groupBox1.Controls.Add(this.txtReaderID);
            this.groupBox1.Controls.Add(this.lblValidityDate);
            this.groupBox1.Controls.Add(this.dtpValidityDate);
            this.groupBox1.Controls.Add(this.lblStatus);
            this.groupBox1.Controls.Add(this.cmbStatus);
            this.groupBox1.Controls.Add(this.btnAdd);
            this.groupBox1.Controls.Add(this.btnEdit);
            this.groupBox1.Controls.Add(this.btnDelete);
            this.groupBox1.Controls.Add(this.btnReset);
            this.groupBox1.Location = new System.Drawing.Point(12, 12);
            this.groupBox1.Name = "groupBox1";
            this.groupBox1.Size = new System.Drawing.Size(960, 180);
            this.groupBox1.TabIndex = 0;
            this.groupBox1.TabStop = false;
            this.groupBox1.Text = "借阅卡信息";
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
            // BorrowCardManageForm
            // 
            this.BackColor = System.Drawing.SystemColors.HotTrack;
            this.ClientSize = new System.Drawing.Size(978, 574);
            this.Controls.Add(this.groupBox1);
            this.Controls.Add(this.groupBox2);
            this.Controls.Add(this.dgvBorrowCard);
            this.Cursor = System.Windows.Forms.Cursors.Cross;
            this.Name = "BorrowCardManageForm";
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Text = "借阅卡管理";
            ((System.ComponentModel.ISupportInitialize)(this.dgvBorrowCard)).EndInit();
            this.groupBox1.ResumeLayout(false);
            this.groupBox1.PerformLayout();
            this.groupBox2.ResumeLayout(false);
            this.groupBox2.PerformLayout();
            this.ResumeLayout(false);

    }

    private void InitializeSearchTypes()
    {
        cmbSearchType.Items.AddRange(new string[] {
            "借阅卡号",
            "读者编号",
            "状态"
        });
        cmbSearchType.SelectedIndex = 0;
    }

    private void LoadBorrowCardData(string searchCondition = "")
    {
        string sql = @"SELECT bc.*, r.ReaderName 
                      FROM BorrowCard bc 
                      LEFT JOIN Reader r ON bc.ReaderID = r.ReaderID";
        if (!string.IsNullOrEmpty(searchCondition))
        {
            sql += " WHERE " + searchCondition;
        }
        dgvBorrowCard.DataSource = DBHelper.ExecuteQuery(sql);
    }

    private void BtnAdd_Click(object sender, EventArgs e)
    {
        if (ValidateInput())
        {
            string sql = $@"INSERT INTO BorrowCard VALUES (
                '{txtCardID.Text}',
                '{txtReaderID.Text}',
                '{DateTime.Now.ToString("yyyy-MM-dd")}',
                '{dtpValidityDate.Value.ToString("yyyy-MM-dd")}',
                '{cmbStatus.Text}'
            )";

            try
            {
                DBHelper.ExecuteNonQuery(sql);
                MessageBox.Show("添加成功！");
                LoadBorrowCardData();
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
            string sql = $@"UPDATE BorrowCard SET 
                ReaderID = '{txtReaderID.Text}',
                ValidityDate = '{dtpValidityDate.Value.ToString("yyyy-MM-dd")}',
                Status = '{cmbStatus.Text}'
                WHERE CardID = '{txtCardID.Text}'";

            try
            {
                DBHelper.ExecuteNonQuery(sql);
                MessageBox.Show("修改成功！");
                LoadBorrowCardData();
            }
            catch (Exception ex)
            {
                MessageBox.Show("修改失败：" + ex.Message);
            }
        }
    }

    private void BtnDelete_Click(object sender, EventArgs e)
    {
        if (string.IsNullOrEmpty(txtCardID.Text))
        {
            MessageBox.Show("请先选择要删除的借阅卡！");
            return;
        }

        if (MessageBox.Show("确定要删除该借阅卡信息吗？", "确认删除", 
            MessageBoxButtons.YesNo) == DialogResult.Yes)
        {
            string sql = $"DELETE FROM BorrowCard WHERE CardID = '{txtCardID.Text}'";

            try
            {
                DBHelper.ExecuteNonQuery(sql);
                MessageBox.Show("删除成功！");
                LoadBorrowCardData();
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
            case "借阅卡号":
                condition = $"bc.CardID LIKE '%{txtSearch.Text}%'";
                break;
            case "读者编号":
                condition = $"bc.ReaderID LIKE '%{txtSearch.Text}%'";
                break;
            case "状态":
                condition = $"bc.Status LIKE '%{txtSearch.Text}%'";
                break;
        }
        LoadBorrowCardData(condition);
    }

    private void DgvBorrowCard_CellClick(object sender, DataGridViewCellEventArgs e)
    {
        if (e.RowIndex >= 0)
        {
            DataGridViewRow row = dgvBorrowCard.Rows[e.RowIndex];
            txtCardID.Text = row.Cells["CardID"].Value.ToString();
            txtReaderID.Text = row.Cells["ReaderID"].Value.ToString();
            dtpValidityDate.Value = Convert.ToDateTime(row.Cells["ValidityDate"].Value);
            cmbStatus.Text = row.Cells["Status"].Value.ToString();
        }
    }

    private bool ValidateInput()
    {
        if (string.IsNullOrEmpty(txtCardID.Text))
        {
            MessageBox.Show("借阅卡号不能为空！");
            return false;
        }
        if (string.IsNullOrEmpty(txtReaderID.Text))
        {
            MessageBox.Show("读者编号不能为空！");
            return false;
        }
        if (string.IsNullOrEmpty(cmbStatus.Text))
        {
            MessageBox.Show("请选择借阅卡状态！");
            return false;
        }
        return true;
    }

    private void ClearInputs()
    {
        txtCardID.Clear();
        txtReaderID.Clear();
        dtpValidityDate.Value = DateTime.Now;
        cmbStatus.SelectedIndex = -1;
    }

    private void BtnReset_Click(object sender, EventArgs e)
    {
        ClearInputs();
    }

    private void BtnClearFilter_Click(object sender, EventArgs e)
    {
        txtSearch.Clear();
        cmbSearchType.SelectedIndex = 0;
        LoadBorrowCardData();
    }
} 