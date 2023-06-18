package server.object.enums;

public enum Difficulty {
    VERY_EASY("VERY_EASY",1),
    EASY("EASY",2),
    VERY_HARD("VERY_HARD",3),
    IMPOSSIBLE("IMPOSSIBLE",4),
    HOPELESS("HOPELESS",5);

    String name;
    int level;

    Difficulty(String name, int level) {
        this.name = name;
        this.level = level;
    }

    @Override
    public String toString() {
        return name;
    }

    public int getLevel() {
        return level;
    }
}
