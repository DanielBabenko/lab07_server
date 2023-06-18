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

    public int addAuthors(Person author) throws SQLException{
        Connection conn = getConnection();
        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO authors(name, eye_color, height, birthday)"
                + " VALUES(?,?,?,?) RETURNING id"
        );
        ps.setString(1,author.getName());
        ps.setString(2,author.getEyeColor().toString());
        ps.setFloat(3,(float) author.getHeight());
        ps.setString(4,author.getBirthday());

        ResultSet rs = ps.executeQuery();
        conn.close();
        rs.next();

        return rs.getInt(1);
    }

    public int addCoordinates(Coordinates coordinates) throws SQLException{
        Connection conn = getConnection();
        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO coordinates(X,Y) VALUES(?,?) RETURNING id"
        );
        ps.setInt(1,coordinates.getX());
        ps.setDouble(2,coordinates.getY());

        ResultSet rs = ps.executeQuery();
        conn.close();
        rs.next();

        return rs.getInt(1);
    }

    public int addLabWork(LabWork labWork) throws SQLException{
        Connection conn = getConnection();
        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO labWorks(name, minimal_point,tuned_in_works,difficulty,coordinates_id,author)"
                        + " VALUES(?,?,?,?,?,?) RETURNING id"
        );
        ps.setString(1,labWork.getName());
        ps.setInt(2,labWork.getMinimalPoint());

        if (labWork.getTunedInWorks() == null) ps.setNull(3, Types.INTEGER);
        else ps.setInt(3, labWork.getTunedInWorks());
        ps.setString(4,labWork.getDifficulty().toString());

        int coordinates_id = addCoordinates(labWork.getCoordinates());
        ps.setInt(5, coordinates_id);

        int person_id = addAuthors(labWork.getAuthor());
        ps.setInt(6, person_id);

        ResultSet rs = ps.executeQuery();
        conn.close();
        rs.next();

        return rs.getInt(1);
    }

    public int upgradeAuthor(int personId, Person newAuthor) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement ps = conn.prepareStatement("UPDATE authors "
                + "SET name = ?, eye_color = ?, height = ? , birthday = ? "
                + "WHERE id = ?"
        );

        ps.setString(1,newAuthor.getName());
        ps.setString(2,newAuthor.getEyeColor().toString());
        ps.setDouble(3,newAuthor.getHeight());
        ps.setString(4, newAuthor.getBirthday());
        ps.setInt(5,personId);

        int res = ps.executeUpdate();
        conn.close();
        return res;
    }

    public int upgradeCoordinates(int coordinatesId, Coordinates newCoordinates) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement ps = conn.prepareStatement("UPDATE coordinates "
                + "SET X = ?, Y = ? "
                + "WHERE id = ?"
        );

        ps.setInt(1,newCoordinates.getX());
        ps.setDouble(2,newCoordinates.getY());
        ps.setInt(3,coordinatesId);

        int res = ps.executeUpdate();
        conn.close();
        return res;
    }

    public int upgradeLabWorks(int labWorkId, LabWork newLabWork) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement ps = conn.prepareStatement("UPDATE LabWorks SET "
                + "name = ?, minimal_point = ?,tuned_in_works = ?,difficulty = ? "
                + "WHERE id = ?"
        );

        ps.setString(1, newLabWork.getName());
        ps.setInt(2, newLabWork.getMinimalPoint());

        if (newLabWork.getTunedInWorks() == null) ps.setNull(3, Types.INTEGER);
        else ps.setInt(3, newLabWork.getTunedInWorks());
        ps.setString(4, newLabWork.getDifficulty().toString());

        ps.setInt(5, labWorkId);

        PreparedStatement ps2 = conn.prepareStatement("SELECT coordinates_id,author FROM labWorks "
                + "WHERE id = ?"
        );
        ps2.setInt(1, labWorkId);
        ResultSet rs = ps2.executeQuery();

        while (rs.next()){
            upgradeCoordinates(rs.getInt(1), newLabWork.getCoordinates());
            upgradeAuthor(rs.getInt(2), newLabWork.getAuthor());
        }

        int res = ps.executeUpdate();
        conn.close();
        return res;
    }

    public int clearLabWorks() throws SQLException {
        Connection connection = getConnection();

        PreparedStatement statement_labWork = connection.prepareStatement(
                "DELETE FROM labWorks"
        );
        statement_labWork.executeUpdate();

        PreparedStatement statement_author = connection.prepareStatement(
                "DELETE FROM authors"
        );
        statement_author.executeUpdate();

        PreparedStatement statement_coordinates = connection.prepareStatement(
                "DELETE FROM coordinates"
        );

        int res = statement_coordinates.executeUpdate();
        connection.close();
        return res;
    }

    public int removeLabWork(LabWork labWork) throws SQLException {
        Connection conn = getConnection();

        PreparedStatement statement_labWork = conn.prepareStatement(
                "DELETE FROM labWorks WHERE id = ?"
        );

        PreparedStatement statement_author = conn.prepareStatement(
                "DELETE FROM authors WHERE id = ?"
        );

        PreparedStatement statement_coordinates = conn.prepareStatement(
                "DELETE FROM coordinates WHERE id = ?"
        );

        PreparedStatement ps2 = conn.prepareStatement("SELECT coordinates_id,author FROM labWorks "
                + "WHERE id = ?"
        );

        ps2.setInt(1, labWork.getId());
        ResultSet rs = ps2.executeQuery();

        statement_labWork.setInt(1, labWork.getId());
        int res = statement_labWork.executeUpdate();

        while (rs.next()){
            statement_coordinates.setInt(1,rs.getInt(1));
            statement_coordinates.executeUpdate();
            statement_author.setInt(1,rs.getInt(2));
            statement_author.executeUpdate();
        }

        conn.close();
        return res;
    }

}
