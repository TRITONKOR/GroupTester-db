package com.tritonkor;

import com.tritonkor.persistence.AppConfig;
import com.tritonkor.persistence.util.ConnectionManager;
import com.tritonkor.persistence.util.DatabaseInitializer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

    public static AnnotationConfigApplicationContext persistenceContext;

    public static void main(String[] args) {
        persistenceContext = new AnnotationConfigApplicationContext(AppConfig.class);
        var connectionManager = persistenceContext.getBean(ConnectionManager.class);
        var databaseInitializer = persistenceContext.getBean(DatabaseInitializer.class);

        try {
            databaseInitializer.init();

        } finally {
            connectionManager.closePool();
        }
    }
}
