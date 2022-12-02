package com.mvc.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author javaok
 * 2022/11/28 18:01
 */
public class ConnUtil {
    private final static String URL = "jdbc:mysql://localhost:3306/mydb";
    private final static String USERNAME = "root";
    private final static String PASSWORD = "123456789";

    private final static ThreadLocal<Connection> LOCAL_CONNECTION = new ThreadLocal<>();

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    public static Connection getConnection() throws SQLException {
        if (LOCAL_CONNECTION.get() == null) {


            LOCAL_CONNECTION.set(DriverManager.getConnection(URL, USERNAME, PASSWORD));
        }
        return LOCAL_CONNECTION.get();
    }


    private static void makeSureConnection() throws SQLException {
        if (LOCAL_CONNECTION.get() == null) {
            LOCAL_CONNECTION.set(ConnUtil.getConnection());
        }
    }

    public static void start() throws SQLException {
        makeSureConnection();
        LOCAL_CONNECTION.get().setAutoCommit(false);
    }

    public static void commit() throws SQLException {
        makeSureConnection();
        LOCAL_CONNECTION.get().commit();
        LOCAL_CONNECTION.get().close();
        LOCAL_CONNECTION.remove();
    }

    public static void rollback() throws SQLException {
        makeSureConnection();
        LOCAL_CONNECTION.get().rollback();
        LOCAL_CONNECTION.get().close();
        LOCAL_CONNECTION.remove();
    }
}
