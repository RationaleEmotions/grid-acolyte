package rationale.emotions;

import org.openqa.selenium.server.SeleniumServer;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 *
 */
public class CustomServerLauncher {
    public static void main(String[] args) throws Exception {
        String file = System.getProperty("selenium.config", "selenium.properties");
        Properties properties = new Properties();
        properties.load(new FileReader(file));
        Map<String, Object> config = new HashMap<>();
        for (String key : properties.stringPropertyNames()) {
            config.put(key, properties.getProperty(key));
        }
        SeleniumServer server = new SeleniumServer(config);
        server.boot();
    }

}
