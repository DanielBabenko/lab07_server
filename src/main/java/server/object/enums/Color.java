package server.object.enums;

public enum Color {
    GREEN("GREEN"),
    RED("RED"),
    BLACK("BLACK"),
    ORANGE("ORANGE"),
    WHITE("WHITE");

    String name;

    Color(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}