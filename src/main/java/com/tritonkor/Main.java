package com.tritonkor;

import com.tritonkor.persistence.PersistenceConfig;
import com.tritonkor.persistence.context.factory.PersistenceContext;
import com.tritonkor.persistence.entity.User;
import com.tritonkor.persistence.entity.User.Role;
import com.tritonkor.persistence.util.ConnectionManager;
import com.tritonkor.persistence.util.DatabaseInitializer;
import java.time.LocalDate;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

    public static void main(String[] args) {

        var context = new AnnotationConfigApplicationContext(PersistenceConfig.class);
        var connectionManager = context.getBean(ConnectionManager.class);
        var databaseInitializer = context.getBean(DatabaseInitializer.class);

        try {
            databaseInitializer.init();

        } finally {
            connectionManager.closePool();
        }



    }
}
