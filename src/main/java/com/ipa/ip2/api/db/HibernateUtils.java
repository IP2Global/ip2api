package com.ipa.ip2.api.db;

import com.ipa.ip2.api.exception.APIException;
import com.ipa.ip2.api.util.PropertiesReader;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by amit on 14/02/17.
 */
public class HibernateUtils {

    private PropertiesReader propertiesReader = new PropertiesReader("jdbc.properties");
    private Properties databaseProps = null;
    private Map<String, String> databaseNameDriversMap = new HashMap<String, String>();
    private static HibernateUtils hibernateUtils = null;
    private SessionFactory sessionFactory = null;
    private String database;
    private String url;
    private String username;
    private String password;
    private String dialect;
    private String driverClass;

    private HibernateUtils() throws APIException {
        databaseProps = propertiesReader.getProperties();
        //Supported drivers map
        databaseNameDriversMap.put("MYSQL", "com.mysql.jdbc.Driver");
        databaseNameDriversMap.put("ORACLE", "oracle.jdbc.driver.OracleDriver");

        database = databaseProps.getProperty("DATABASE");
        url = databaseProps.getProperty("URL");
        username = databaseProps.getProperty("DB_USERNAME");
        password = databaseProps.getProperty("DB_PASSWORD");

        if (database == null || database.equals("")) {
            throw new APIException("Database not found. Please provide DATABASE in the jdbc.properties");
        }
        if (url == null || url.equals("")) {
            throw new APIException("url not found. Please provide URL in the jdbc.properties");
        }
        if (username == null || username.equals("")) {
            throw new APIException("Username not found. Please provide DB_USERNAME in the jdbc.properties");
        }
        if (password == null || password.equals("")) {
            throw new APIException("Password not found. Please provide DB_PASSWORD in the jdbc.properties");
        }
        driverClass = databaseNameDriversMap.get(database);
        if(driverClass == null || driverClass.equals("")){
            throw new APIException("Not valid and supported database! (IP2API only supports MYSQL and ORACLE)");
        }
        if(database.equals("MYSQL")) {
            dialect = "org.hibernate.dialect.MySQLDialect";
        } else if (database.equals("ORACLE")) {
            dialect = "org.hibernate.dialect.Oracle12cDialect";
        }
    }

    public static HibernateUtils getInstance() throws APIException{
        if (hibernateUtils == null){
            hibernateUtils = new HibernateUtils();
        }
        return hibernateUtils;
    }

    public SessionFactory getSessionFactory() throws APIException{
        try {
            if (sessionFactory == null || sessionFactory.isClosed()) {
                Configuration configuration = new Configuration().configure();
                Properties props = configuration.getProperties();
                props.setProperty("hibernate.dialect", dialect);
                props.setProperty("hibernate.connection.driver_class", driverClass);
                props.setProperty("hibernate.connection.url", url);
                props.setProperty("hibernate.connection.username", username);
                props.setProperty("hibernate.connection.password", password);
                configuration.setProperties(props);
                sessionFactory = configuration
                        .buildSessionFactory();
                System.out.println("Session factory created successfully!");
            }
        } catch (Exception e) {
            System.err.println("Failed to create session factory! Check output console");
            e.printStackTrace();
            throw new APIException("Failed to create session factory.");
        }
        return sessionFactory;
    }

    public void closeSessionFactory() throws APIException{
        try {
            if (sessionFactory != null && !sessionFactory.isClosed()) {
                sessionFactory.close();
            }
            System.out.println("SessionFactory closed successfully!");
        } catch (Exception e) {
            System.err.println("Error while closing session factory ! Check output console");
            e.printStackTrace();
            throw new APIException("Error while closing the session factory.");
        }
    }
}
