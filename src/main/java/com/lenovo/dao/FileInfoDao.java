package com.lenovo.dao;

import com.lenovo.domain.FileInfo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by xiaobai on 16-11-14.
 */
public class FileInfoDao {

    private String insert_sql;

    private String connectStr;
    private String username;
    private String password;

    public FileInfoDao() {
        connectStr = "jdbc:mysql://localhost:3306/lenovo";
        insert_sql = "insert into file_info(file_name,company,product_name,file_version,file_size,file_path,description,download_url) values(?,?,?,?,?,?,?,?)";
        username = "root";
        password = "123456";
    }

    public void insertFileInfos(ArrayList<FileInfo> infos) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection(connectStr, username, password);
        conn.setAutoCommit(false); // 设置手动提交
        PreparedStatement psts = conn.prepareStatement(insert_sql);
        for (FileInfo info : infos) {
            psts.setString(1, info.getFileName());
            psts.setString(2, info.getCompany());
            psts.setString(3, info.getProductName());
            psts.setString(4, info.getFileVersion());
            psts.setString(5, info.getFileSize());
            psts.setString(6, info.getFilePath());
            psts.setString(7, info.getDescription());
            psts.setString(8, info.getDownloadUrl());
            psts.addBatch();          // 加入批量处理
        }
        psts.executeBatch(); // 执行批量处理
        conn.commit();  // 提交
        conn.close();
    }

    public void insertFileInfo(FileInfo info) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection(connectStr, username, password);
        PreparedStatement psts = conn.prepareStatement(insert_sql);
        psts.setString(1, info.getFileName());
        psts.setString(2, info.getCompany());
        psts.setString(3, info.getProductName());
        psts.setString(4, info.getFileVersion());
        psts.setString(5, info.getFileSize());
        psts.setString(6, info.getFilePath());
        psts.setString(7, info.getDescription());
        psts.setString(8, info.getDownloadUrl());
        psts.execute();
        System.out.println("插入数据");
        conn.close();
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        ArrayList<FileInfo> list = new ArrayList<FileInfo>();
        FileInfo fileInfo = new FileInfo();
        fileInfo.setDownloadUrl("www.baidu.com");
        fileInfo.setFileName("test");
        list.add(fileInfo);
        new FileInfoDao().insertFileInfos(list);
    }

}
