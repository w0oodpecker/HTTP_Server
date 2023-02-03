import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

public class Settings {

    private int serverPort; //Порт чата
    private int threadBound; //Количество потоков
    private int bufferLimit; //Ограничение буффера входного потока сокета // TODO: 2/3/2023 переделать на чтение из настроек


    private static Settings instance;

    private Settings(String nameOfSettingsFile) {
        Object o;
        try {
            o = new JSONParser().parse(new FileReader(nameOfSettingsFile));
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        } catch (ParseException exc) {
            throw new RuntimeException(exc);
        }
        JSONObject j = (JSONObject) o;
        serverPort = Integer.parseInt((String) j.get("serverPort"));
        threadBound = Integer.parseInt((String) j.get("threadBound"));
        bufferLimit = Integer.parseInt((String) j.get("bufferLimit"));
    }


    public int getServerPort() {
        return serverPort;
    }


    public int getThreadBound() {
        return threadBound;
    }


    public int getBufferLimit() {
        return bufferLimit;
    }

    public static Settings getInstance(String nameOfSettingsFile) {
        if (instance == null) {
            instance = new Settings(nameOfSettingsFile);
        }
        return instance;
    }
}
