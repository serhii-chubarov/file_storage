package net.proselyte.dao;

import lombok.experimental.UtilityClass;

import java.util.ResourceBundle;

@UtilityClass
public class DatabaseSettings {
    private static final ResourceBundle DATABASE_CONFIG = ResourceBundle.getBundle("application");

    public String getUrl() {
        return DATABASE_CONFIG.getString("db.url");
    }

    public String getUser() {
        return DATABASE_CONFIG.getString("db.user");
    }

    public String getPassword() {
        return DATABASE_CONFIG.getString("db.password");
    }
}
