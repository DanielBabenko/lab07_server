package server;

import server.object.LabWork;

public interface UpdateCommand {

    /**
     * Метод для работы с командами с параметром {@link LabWork#id}
     * @param id
     */
    void execute(int id);
}
