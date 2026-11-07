package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigBanco {
    private static final Properties propriedades = new Properties();

    static {
        try (InputStream input = ConfigBanco.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                System.out.println("Desculpe, o arquivo database.properties não foi encontrado.");
            } else {
                propriedades.load(input);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String getUrl() {
        return propriedades.getProperty("db.url");
    }

    public static String getUser() {
        return propriedades.getProperty("db.user");
    }

    public static String getPassword() {
        return propriedades.getProperty("db.password");
    }
}