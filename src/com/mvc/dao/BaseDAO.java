package com.mvc.dao;

import com.mvc.util.ConnUtil;
import com.mvc.util.StrUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author javaok
 * 2022/11/28 22:28
 */

public abstract class BaseDAO<T> {
    Class<?> entityClass;


    public BaseDAO() {
        Type type = getClass().getGenericSuperclass();
        Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
        try {
            entityClass = Class.forName(actualTypeArguments[0].getTypeName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static Object generateByClass(Class<?> entityClass, ResultSet fields) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, SQLException {
        Object ret = entityClass.getConstructor().newInstance();
        for (Field declaredField : entityClass.getDeclaredFields()) {
            declaredField.setAccessible(true);
            declaredField.set(ret, fields.getObject(declaredField.getName()));
        }
        return ret;
    }

    public Integer update(String sql, Object... params) throws SQLException {
        Connection connection = ConnUtil.getConnection();

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i + 1, params[i]);
        }

        int ret = preparedStatement.executeUpdate();
        preparedStatement.close();
        return ret;
    }

    public T query(String sql, Object... params) throws SQLException {
        Connection connection = ConnUtil.getConnection();

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i + 1, params[i]);
        }

        ResultSet resultSet = preparedStatement.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();

        int columnCount = metaData.getColumnCount();

        if (resultSet.next()) {
            try {
                Object ret = entityClass.getDeclaredConstructor().newInstance();
                for (int i = 0; i < columnCount; i++) {
                    String fieldName = metaData.getColumnLabel(i + 1);

                    Field declaredField = entityClass.getDeclaredField(fieldName);
                    declaredField.setAccessible(true);
                    declaredField.set(ret, resultSet.getObject(i + 1));

                }

                return (T) ret;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                resultSet.close();
                preparedStatement.close();
            }
        }
        return null;
    }

    public List<T> queries(String sql, Object... params) throws SQLException {
        Connection connection = ConnUtil.getConnection();

        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i + 1, params[i]);
        }

        ResultSet resultSet = preparedStatement.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();

        int columnCount = metaData.getColumnCount();

        ArrayList<T> rets = new ArrayList<>();
        try {
            while (resultSet.next()) {

                Object ret = entityClass.getDeclaredConstructor().newInstance();
                for (int i = 0; i < columnCount; i++) {
                    String fieldName = metaData.getColumnLabel(i + 1);

                    Field declaredField = entityClass.getDeclaredField(StrUtil.transToLowerCamel(fieldName));
                    declaredField.setAccessible(true);
                    declaredField.set(ret, resultSet.getObject(i + 1));

                }
                rets.add((T) ret);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            resultSet.close();
            preparedStatement.close();
        }
        return rets;
    }

    public <E> E getValue(String sql, Object... params) throws SQLException {
        Connection connection = ConnUtil.getConnection();

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i + 1, params[i]);
        }

        ResultSet resultSet = preparedStatement.executeQuery();
        E ret = null;
        if (resultSet.next()) {
            ret = (E) resultSet.getObject(1);
        }

        resultSet.close();
        preparedStatement.close();

        return ret;
    }

}
