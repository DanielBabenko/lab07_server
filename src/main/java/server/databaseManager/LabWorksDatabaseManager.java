package server.databaseManager;

import server.exceptions.InvalidFieldY;
import server.exceptions.NullX;
import server.object.Coordinates;
import server.object.LabWork;
import server.object.Person;
import server.object.enums.Color;
import server.object.enums.Difficulty;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class LabWorksDatabaseManager {
    private final ConnectionManager connectionManager;

    public LabWorksDatabaseManager (String url, String login, String password) {
        connectionManager = new ConnectionManager(url, login, password);
    }

    public LabWorksDatabaseManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public Connection getConnection() throws SQLException {
        return connectionManager.getConnection();
    }

    public Map<Integer, Person> loadAuthors() throws SQLException{
        Connection conn = getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM authors");
        ResultSet rs = ps.executeQuery();

        Map<Integer,Person> authors = new HashMap<>();

        while (rs.next()){
            Integer id = rs.getInt("id");
            Person author = new Person(
                    rs.getString("name"), Color.valueOf(rs.getString("eye_color")),
                    rs.getFloat("height"), rs.getDate("birthday").toString()
            );

            authors.put(id,author);
        }

        conn.close();
        return authors;
    }

    public Map<Integer,Coordinates> loadCoordinates() throws SQLException, NullX, InvalidFieldY {
        Connection conn = getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM coordinates");
        ResultSet rs = ps.executeQuery();

        Map<Integer,Coordinates> coordinatesMap = new HashMap<>();

        while (rs.next()){
            Integer id = rs.getInt("id");
            Coordinates cords = new Coordinates(
                    rs.getInt("X"),
                    rs.getDouble("Y")
            );

            coordinatesMap.put(id,cords);
        }

        conn.close();
        return coordinatesMap;
    }

    public List<LabWork> loadLabWorks() throws SQLException, NullX, InvalidFieldY {
        Map<Integer,Person> authors = loadAuthors();
        Map<Integer,Coordinates> coordinates = loadCoordinates();
        List<LabWork> labWorkList = new LinkedList<>();

        Connection conn = getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM labWorks");
        ResultSet rs = ps.executeQuery();

        while (rs.next()){
            int authorId = rs.getInt("author");
            Person author = authors.get(authorId);

            int coordinatesId = rs.getInt("coordinates_id");
            Coordinates cords = coordinates.get(coordinatesId);

            LabWork lab = new LabWork(
                    rs.getInt("id"),rs.getString("name"),
                    rs.getInt("minimal_point"), rs.getLong("tuned_in_works"),
                    Difficulty.valueOf(rs.getString("difficulty")),
                    cords, author
            );
            labWorkList.add(lab);
        }

        conn.close();
        return labWorkList;
    }

    public int addAuthors(Person author,int user_id) throws SQLException{
        Connection conn = getConnection();
        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO authors(name, eye_color, height, birthday,user_id)"
                + " VALUES(?,?,?,?,?) RETURNING id"
        );
        ps.setString(1,author.getName());
        ps.setString(2,author.getEyeColor().toString());
        ps.setFloat(3,(float) author.getHeight());
        ps.setString(4,author.getBirthday());
        ps.setInt(5,user_id);

        ResultSet rs = ps.executeQuery();
        conn.close();
        rs.next();

        return rs.getInt(1);
    }

    public int addCoordinates(Coordinates coordinates,int user_id) throws SQLException{
        Connection conn = getConnection();
        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO coordinates(X,Y,user_id) VALUES(?,?,?) RETURNING id"
        );
        ps.setInt(1,coordinates.getX());
        ps.setDouble(2,coordinates.getY());
        ps.setInt(3,user_id);

        ResultSet rs = ps.executeQuery();
        conn.close();
        rs.next();

        return rs.getInt(1);
    }

    public int addLabWork(LabWork labWork, int user_id) throws SQLException{
        Connection conn = getConnection();
        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO labWorks(name, minimal_point,tuned_in_works,difficulty,coordinates_id,author,user_id)"
                        + " VALUES(?,?,?,?,?,?,?) RETURNING id"
        );
        ps.setString(1,labWork.getName());
        ps.setInt(2,labWork.getMinimalPoint());

        if (labWork.getTunedInWorks() == null) ps.setNull(3, Types.INTEGER);
        else ps.setInt(3, labWork.getTunedInWorks());
        ps.setString(4,labWork.getDifficulty().toString());

        int coordinates_id = addCoordinates(labWork.getCoordinates(),user_id);
        ps.setInt(5, coordinates_id);

        int person_id = addAuthors(labWork.getAuthor(),user_id);
        ps.setInt(6, person_id);

        ps.setInt(7,user_id);

        ResultSet rs = ps.executeQuery();
        conn.close();
        rs.next();

        return rs.getInt(1);
    }

    public int upgradeAuthor(int personId,int userId, Person newAuthor) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement ps = conn.prepareStatement("UPDATE authors "
                + "SET name = ?, eye_color = ?, height = ? , birthday = ? "
                + "WHERE id = ? AND user_id = ?"
        );

        ps.setString(1,newAuthor.getName());
        ps.setString(2,newAuthor.getEyeColor().toString());
        ps.setDouble(3,newAuthor.getHeight());
        ps.setString(4, newAuthor.getBirthday());
        ps.setInt(5,personId);
        ps.setInt(6,userId);

        int res = ps.executeUpdate();
        conn.close();
        return res;
    }

    public int upgradeCoordinates(int coordinatesId,int userId, Coordinates newCoordinates) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement ps = conn.prepareStatement("UPDATE coordinates "
                + "SET X = ?, Y = ? "
                + "WHERE id = ? AND user_id = ?"
        );

        ps.setInt(1,newCoordinates.getX());
        ps.setDouble(2,newCoordinates.getY());
        ps.setInt(3,coordinatesId);
        ps.setInt(4,userId);

        int res = ps.executeUpdate();
        conn.close();
        return res;
    }

    public int upgradeLabWorks(int labWorkId, int userId, LabWork newLabWork) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement ps = conn.prepareStatement("UPDATE LabWorks SET "
                + "name = ?, minimal_point = ?,tuned_in_works = ?,difficulty = ? "
                + "WHERE id = ? AND user_id = ?"
        );

        ps.setString(1, newLabWork.getName());
        ps.setInt(2, newLabWork.getMinimalPoint());

        if (newLabWork.getTunedInWorks() == null) ps.setNull(3, Types.INTEGER);
        else ps.setInt(3, newLabWork.getTunedInWorks());
        ps.setString(4, newLabWork.getDifficulty().toString());

        ps.setInt(5, labWorkId);
        ps.setInt(6,userId);

        PreparedStatement ps2 = conn.prepareStatement("SELECT coordinates_id,author FROM labWorks "
                + "WHERE id = ? AND user_id = ?"
        );
        ps2.setInt(1, labWorkId);
        ps2.setInt(2,userId);
        ResultSet rs = ps2.executeQuery();

        while (rs.next()){
            upgradeCoordinates(rs.getInt(1),userId, newLabWork.getCoordinates());
            upgradeAuthor(rs.getInt(2),userId, newLabWork.getAuthor());
        }

        int res = ps.executeUpdate();
        conn.close();
        return res;
    }

    public LinkedList<Integer> elementToBeCleared(int userID) throws SQLException {
        Connection connection = getConnection();
        LinkedList<Integer> els = new LinkedList<>();

        PreparedStatement del_labWork = connection.prepareStatement(
                "SELECT id from labWorks where user_id = ?"
        );
        del_labWork.setInt(1,userID);

        ResultSet rs = del_labWork.executeQuery();
        while (rs.next()){
            Integer id = rs.getInt("id");
            els.add(id);
        }

        connection.close();
        return els;
    }

    public int clearLabWorks(int userID) throws SQLException {
        Connection connection = getConnection();

        PreparedStatement statement_labWork = connection.prepareStatement(
                "DELETE FROM labWorks WHERE user_id = ?"
        );
        statement_labWork.setInt(1,userID);
        statement_labWork.executeUpdate();

        PreparedStatement statement_author = connection.prepareStatement(
                "DELETE FROM authors WHERE user_id = ?"
        );
        statement_author.setInt(1,userID);
        statement_author.executeUpdate();

        PreparedStatement statement_coordinates = connection.prepareStatement(
                "DELETE FROM coordinates WHERE user_id = ?"
        );
        statement_coordinates.setInt(1,userID);
        int res = statement_coordinates.executeUpdate();
        connection.close();
        return res;
    }

    public int removeLabWork(LabWork labWork, int userID) throws SQLException {
        Connection conn = getConnection();

        PreparedStatement statement_labWork = conn.prepareStatement(
                "DELETE FROM labWorks WHERE id = ? AND user_id = ?"
        );

        PreparedStatement statement_author = conn.prepareStatement(
                "DELETE FROM authors WHERE id = ? AND user_id = ?"
        );

        PreparedStatement statement_coordinates = conn.prepareStatement(
                "DELETE FROM coordinates WHERE id = ? AND user_id = ?"
        );

        PreparedStatement ps2 = conn.prepareStatement("SELECT coordinates_id,author FROM labWorks "
                + "WHERE id = ? AND user_id = ?"
        );

        ps2.setInt(1, labWork.getId());
        ps2.setInt(2,userID);
        ResultSet rs = ps2.executeQuery();

        statement_labWork.setInt(1, labWork.getId());
        statement_labWork.setInt(2,userID);
        int res = statement_labWork.executeUpdate();

        while (rs.next()){
            statement_coordinates.setInt(1,rs.getInt(1));
            statement_coordinates.setInt(2,userID);
            statement_coordinates.executeUpdate();
            statement_author.setInt(1,rs.getInt(2));
            statement_author.setInt(2,userID);
            statement_author.executeUpdate();
        }

        conn.close();
        return res;
    }

}
