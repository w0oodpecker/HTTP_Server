public class Main {

    public static final String nameOfSettingsFile = "settings.json";
    public static final String ERRORMSG = "Что-то пошло не так :(";

    public static void main(String[] args) {
        Settings settings = Settings.getInstance(nameOfSettingsFile);
        Server server = new Server(settings);
        server.start();
        while (true) {}
    }
}


