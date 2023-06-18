package server;

/**
 * Ынтерфейс для обеспечения полиморфизма команд с входными данными
 */
public interface ElementCommand {
    /**
     * Метод, который исполняется в командах с входными данными. На вход принимает строку.
     * @param e
     */
    void execute(String e);
}
