using System;
using System.Data;
using System.Data.SqlClient;

public class DBHelper
{
    private static string connectionString = @"Data Source=(LocalDB)\MSSQLLocalDB;AttachDbFilename=C:\Users\xupan\Documents\Library.mdf;Integrated Security=True;Connect Timeout=30";
    
    public static SqlConnection GetConnection()
    {
        SqlConnection connection = new SqlConnection(connectionString);
        try
        {
            connection.Open();
            return connection;
        }
        catch (Exception ex)
        {
            throw new Exception("数据库连接失败: " + ex.Message);
        }
    }

    public static DataTable ExecuteQuery(string sql, SqlParameter[] parameters = null)
    {
        using (SqlConnection conn = GetConnection())
        {
            SqlCommand cmd = new SqlCommand(sql, conn);
            if (parameters != null)
            {
                cmd.Parameters.AddRange(parameters);
            }
            SqlDataAdapter adapter = new SqlDataAdapter(cmd);
            DataTable dt = new DataTable();
            adapter.Fill(dt);
            return dt;
        }
    }

    public static int ExecuteNonQuery(string sql, SqlParameter[] parameters = null)
    {
        using (SqlConnection conn = GetConnection())
        {
            SqlCommand cmd = new SqlCommand(sql, conn);
            if (parameters != null)
            {
                cmd.Parameters.AddRange(parameters);
            }
            return cmd.ExecuteNonQuery();
        }
    }

    public static object ExecuteScalar(string sql, SqlParameter[] parameters = null)
    {
        using (SqlConnection conn = GetConnection())
        {
            SqlCommand cmd = new SqlCommand(sql, conn);
            if (parameters != null)
            {
                cmd.Parameters.AddRange(parameters);
            }
            return cmd.ExecuteScalar();
        }
    }
} 