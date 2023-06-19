package server.databaseManager;

import server.exceptions.InvalidFieldY;
import server.exceptions.NullX;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Login {
    private final ConnectionManager connectionManager;
    public int userID;

    public Login (String url, String login, String password) {
        connectionManager = new ConnectionManager(url, login, password);
    }

    public Login(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public Connection getConnection() throws SQLException {
        return connectionManager.getConnection();
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public boolean saveUser(String username, String password) throws IOException, SQLException, NoSuchAlgorithmException {
        boolean auth = false;
        Map<String,String> users = loadUsers();
        if (users.isEmpty()||!(users.containsKey(username))) {
            String hash = hashPassword(password);
            users.put(username,hash);
            setUserID(auth(username,hash));
            System.out.println("Авторизация нового пользователя прошла успешно!");
            auth = true;
        } else {
            for (Map.Entry<String, String> entry : users.entrySet()){
                String key = entry.getKey();
                String value = entry.getValue();

                if (key.equals(username)){
                    if (value.equals(hashPassword(password))){
                        System.out.println("Аутентификация пользователя прошла успешно");
                        setUserID(findUserID(username));
                        auth = true;
                    } else {
                        System.out.println("Ошибка авторизации! Повторите попытку!");
                        auth = false;
                    }
                    break;
                }
            }
        }
        return auth;
    }

    public int auth(String username, String password) throws IOException, SQLException {
        Connection conn = getConnection();
        PreparedStatement ps = conn.prepareStatement("INSERT INTO users(username,password) VALUES(?,?) returning id");
        ps.setString(1, username);
        ps.setString(2, password);

        ResultSet rs = ps.executeQuery();
        conn.close();
        rs.next();

        return rs.getInt(1);
    }

    private int findUserID(String username) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT id FROM users WHERE username = ?");
        ps.setString(1,username);

        ResultSet rs = ps.executeQuery();
        conn.close();
        rs.next();

        return rs.getInt(1);
    }

    private static String getRandomString() {
        int l = 6;
        String AlphaNumericStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvxyz0123456789";
        StringBuilder s = new StringBuilder(l);
        int i;
        for (i = 0; i < l; i++) {
            int ch = (int) (AlphaNumericStr.length() * Math.random());
            s.append(AlphaNumericStr.charAt(ch));
        }
        return s.toString();
    }

    private String hashPassword(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("MD2");
        //String salt = getRandomString();
        String pepper = "#63Rq*9Oxx!";

        //byte[] hash = md.digest((pepper+password).getBytes("UTF-8"));
        String hash = pepper+password.hashCode();
        return hash;
    }

    public Map<String, String> loadUsers() throws SQLException, NullX, InvalidFieldY {
        Connection conn = getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM users");
        ResultSet rs = ps.executeQuery();

        Map<String,String> UsersMap = new HashMap<>();

        while (rs.next()){
            String username = rs.getString("username");
            String password = rs.getString("password");

            UsersMap.put(username,password);
        }

        conn.close();
        return UsersMap;
    }
}
