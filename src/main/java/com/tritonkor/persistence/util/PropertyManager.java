package com.tritonkor.persistence.util;

import com.tritonkor.persistence.context.GenericUnitOfWork;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PropertyManager {
    private static final Properties PROPERTIES = new Properties();
    final Logger LOGGER = LoggerFactory.getLogger(GenericUnitOfWork.class);

    public PropertyManager() {
        loadProperties();
    }
    public PropertyManager(InputStream applicationProperties) {
        try (applicationProperties) {
            PROPERTIES.load(applicationProperties);
        } catch (IOException e) {
            LOGGER.error("failed to read properties. %s".formatted(e));
            throw new RuntimeException(e);
        }
    }

    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }

    private void loadProperties() {
        try (InputStream applicationProperties =
                PropertyManager.class
                        .getClassLoader()
                        .getResourceAsStream("application.properties")) {
            PROPERTIES.load(applicationProperties);
        } catch (IOException e) {
            LOGGER.error("failed to read properties. %s".formatted(e));
            throw new RuntimeException(e);
        }
    }
}
