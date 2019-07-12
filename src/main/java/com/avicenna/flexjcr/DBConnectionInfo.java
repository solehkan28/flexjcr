package com.avicenna.flexjcr;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DBConnectionInfo {

    private final String driver;
    private final String url;
    private final String username;
    private final String password;
    private final String serverName;
    private final String dbName;

    private static DBConnectionInfo ourInstance = new DBConnectionInfo();
    public static DBConnectionInfo i() {
        return ourInstance;
    }

    private DBConnectionInfo() {

        String driver = null;
        String url = null;
        String username = null;
        String password = null;
        String serverName = null;
        String dbName = null;

        try (InputStream input = ReportConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            Properties prop = new Properties();
            // load a properties file
            prop.load(input);
            // get the property value and print it out
            driver = prop.getProperty("db.default.driver");
            url = prop.getProperty("db.default.url");
            username = prop.getProperty("db.default.username");
            password = prop.getProperty("db.default.password");
            serverName = prop.getProperty("db.default.servername");
            dbName = prop.getProperty("db.default.dbname");

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        this.driver = driver;
        this.url = url;
        this.username = username;
        this.password = password;
        this.serverName = serverName;
        this.dbName = dbName;
    }

    public String getDriver() {
        return driver;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getServerName() {
        return serverName;
    }

    public String getDbName() {
        return dbName;
    }
}
