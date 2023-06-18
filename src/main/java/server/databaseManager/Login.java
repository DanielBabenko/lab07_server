package server.databaseManager;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner; // I use scanner because it's command line.

public class Login {
    private final ConnectionManager connectionManager;

    public Login (String url, String login, String password) {
        connectionManager = new ConnectionManager(url, login, password);
    }

    public Login(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public Connection getConnection() throws SQLException {
        return connectionManager.getConnection();
    }

    public int saveUser(String username, String salt, byte[] hash) throws IOException, SQLException {
        Connection conn = getConnection();
        PreparedStatement ps = conn.prepareStatement("INSERT INTO users(username,salt,hash) VALUES(?,?,?)");
        ps.setString(1,username);
        ps.setString(2,salt);
        ps.setString(3, hash.toString());

        int res = ps.executeUpdate();
        conn.close();
        return res;
    }

    public void checkOnAuth(String username, String salt, byte[] hash) throws IOException, SQLException {
        Connection conn = getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND salt = ? AND hash = ?");
        PreparedStatement ps2 = conn.prepareStatement("SELECT * FROM users WHERE username = ?");
        ps.setString(1,username);
        ps.setString(2,salt);
        ps.setString(3, hash.toString());

        ps2.setString(1,username);

        ResultSet rs = ps.executeQuery();
        ResultSet rs2 = ps2.executeQuery();

        if (rs2.wasNull()||rs.wasNull()){
            saveUser(username,salt,hash);
            System.out.println("Авторизация прошла успешно!");
        }
        else if ((!(rs2.wasNull())) & rs.wasNull()){
            System.out.println("Ошибка авторизации! Попробуйте ещё раз!");
            checkOnAuth(username,salt,hash);
        }
        else {
            System.out.println("Авторизация прошла успешно!");
        }

        rs.next();
        rs2.next();

        conn.close();
    }

}
