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

    public Login (String url, String login, String password) {
        connectionManager = new ConnectionManager(url, login, password);
    }

    public Login(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public Connection getConnection() throws SQLException {
        return connectionManager.getConnection();
    }

    public boolean saveUser(String username, String password) throws IOException, SQLException, NoSuchAlgorithmException {
        boolean auth = false;
        Map<String,String> users = loadUsers();
        if (users.isEmpty()||!(users.containsKey(username))) {
            password = hashPassword(password);
            users.put(username,password);
            auth(username,password);
            System.out.println("Авторизация нового пользователя прошла успешно!");
            auth = true;
        } else {
            for (Map.Entry<String, String> entry : users.entrySet()){
                String key = entry.getKey();
                String value = entry.getValue();

                if (key.equals(username)){
                    if (value.equals(hashPassword(password))){
                        System.out.println("Аутентификация пользователя прошла успешно");
                        auth = true;
                    } else {
                        System.out.println("Ошибка авторизации! Повторите попытку!");
                        System.out.println(hashPassword(password));
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
        PreparedStatement ps = conn.prepareStatement("INSERT INTO users(username,password) VALUES(?,?)");
        ps.setString(1, username);
        ps.setString(2, password);

        int res = ps.executeUpdate();
        conn.close();
        return res;
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

        byte[] hash = md.digest((pepper+password).getBytes("UTF-8"));
        return pepper+password;
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
