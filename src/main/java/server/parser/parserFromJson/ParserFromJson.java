package server.parser.parserFromJson;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import server.object.Coordinates;
import server.object.LabWork;
import server.object.Person;
import server.object.enums.Color;
import server.object.enums.Difficulty;
import server.parser.Root;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

/**
 * Метод парсит данные из json файла в коллекцию {@link Root#getLabWorkSet()}
 * Ключевой метод для работы с коллекцией.
 */

public class ParserFromJson {

    /**
     * Метод обращается к файлу, использует его в качестве базы данных объектов.
     * @return
     * @throws IOException
     */
    public Root parse(String fileName) throws IOException {
        Root root = new Root();
        JSONParser parser = new JSONParser();
        File file = new File(fileName);
        if (file.exists())
            try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file))) {
                JSONArray labsJsonArray = (JSONArray) parser.parse(reader);

                HashSet<LabWork> labWorks = new HashSet<>();
                for (Object lab : labsJsonArray) {
                    JSONObject labJsonObject = (JSONObject) lab;

                    // at the first parsing primitive type of json file
                    long id = (Long) labJsonObject.get("id");
                    long minimalPoint = (Long) labJsonObject.get("minimalPoint");
                    Long tunedInWorks = (Long) labJsonObject.get("tunedInWorks");
                    String name = (String) labJsonObject.get("name");
                    String creationDate = (String) labJsonObject.get("creationDateString");
                    String difficulty = (String) labJsonObject.get("difficulty");

                    // at the second parsing object type of json file
                    //Coordinates type
                    JSONObject coordinatesJsonObject = (JSONObject) labJsonObject.get("coordinates");
                    long x = (Long) coordinatesJsonObject.get("x");
                    double y = (Double) coordinatesJsonObject.get("y");

                    // Person type
                    JSONObject personJsonObject = (JSONObject) labJsonObject.get("author");
                    String nameAuthor = (String) personJsonObject.get("name");
                    String color = (String) personJsonObject.get("eyeColor");
                    double height = (Double) personJsonObject.get("height");
                    String dataBirthday = (String) personJsonObject.get("birthday");

                    LabWork labWork = new LabWork((int) id, name, (int) minimalPoint,  tunedInWorks, Difficulty.valueOf(difficulty), new Coordinates((int) x, y), new Person(nameAuthor, Color.valueOf(color), height, dataBirthday), creationDate);

                    labWorks.add(labWork);
                }

                validation(labWorks);

                root.setLabWorkSet(labWorks);

                root.setValid(true);

                return root;
            } catch (ParseException | NullPointerException e) {
                System.out.println("Невалидный файл json!");
                System.out.println(e.getMessage());
                root.setValid(false);
            } catch(IllegalArgumentException | ClassCastException e) {
                System.out.println("Невалидные данные -->");
                System.out.println(e.getMessage());
                root.setValid(false);
            }
        return root;
    }


    /**
     * Метод проверяет есть ли в файле обьекты коллекции.
     * @return boolean
     */
    public boolean checkOnEmpty(String fileName) {
        try {

            File file = new File(fileName);
            BufferedReader br = new BufferedReader(new FileReader(file));

            try {
                if (br.readLine() == null)
                    return false;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Метод проверяет наличие одинаковых id {@link LabWork#getId()}
     * Если программа находит таковые, завершает свою работу.
     * @param labWorks
     */
    private void checkOnIdentify(HashSet<LabWork> labWorks) {
        ArrayList<Integer> idLabs = new ArrayList<>();

        for (LabWork lab: labWorks) {
            idLabs.add((int) lab.getId());
        }

        Collections.sort(idLabs);


        for (Integer id: idLabs) {
            for (int i = id+1; i < idLabs.size(); i++) {
               // System.out.println("id="+id);
               // System.out.println(ParserFromJson.class.getSimpleName()+" id object=" + idLabs.get(i)+ " id=" + id);
                try {
                    if (id.equals(idLabs.get(i)))
                        throw new IOException("Файл не прошел валидацию. Мы нашли объекты с одинаковым id.");
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                        System.exit(0);
                    }
            }
        }
    }


    /**
     * Проверяет все поля объекта на валидные данные.
     * Прекращает работу программы в случае невалидных данных.
     * @param labWorks
     */
    private void validation(HashSet<LabWork> labWorks) {
        checkOnIdentify(labWorks);
        boolean flag = false;
        for (LabWork labWork: labWorks) {
            if (labWork.getTunedInWorks() != null)
                if (labWork.getTunedInWorks() < 0 || labWork.getTunedInWorks() > 1001)
                    flag = true;
            if (labWork.getCoordinates().getY() < -184)
                flag = true;
            if (labWork.getAuthor().getHeight() < 0 || labWork.getAuthor().getHeight() > 273)
                flag = true;
            if (labWork.getMinimalPoint() < 0 || labWork.getMinimalPoint() > 1000)
                flag = true;
            if (labWork.getName().trim().isEmpty())
                flag = true;
        }

        if (flag){
            System.out.println("Файл не прошел валидацию.");
            System.exit(0);
        }
    }
}
