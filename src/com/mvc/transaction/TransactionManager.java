package com.mvc.transaction;

import com.mvc.util.ConnUtil;

import java.sql.SQLException;

/**
 * @author javaok
 * 2022/11/28 17:47
 */
public class TransactionManager {
    private static TransactionManager instance;


    public static TransactionManager getInstance() {
        if (instance == null) {
            instance = new TransactionManager();
        }
        return instance;
    }


    public void start() throws SQLException {
        ConnUtil.start();
    }

    public void commit() throws SQLException {
        ConnUtil.commit();
    }

    public void rollback() throws SQLException {
        ConnUtil.rollback();
    }
}
