package server;

import server.loggers.Logger;
import server.loggers.StandardLogger;
import server.manager.Controller;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.ParseException;

/**
 * server.App запускающий класс, содержащий метод {@link #main(String[])}
 */

public class App {

    /**
     * Главный  метод, который запускает {@link Controller#start()}
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException, ParseException, ClassNotFoundException, NoSuchAlgorithmException, SQLException {

        String file;
        Logger logger = new StandardLogger();
        logger.write("Логгер запущен");

        try{
            file = args[0];
        } catch (IndexOutOfBoundsException e) {
            file = "notes.json";
        }

        Controller controller = new Controller(file);
        controller.start();
    }
}
