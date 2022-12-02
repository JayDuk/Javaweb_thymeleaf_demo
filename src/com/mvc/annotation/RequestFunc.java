package com.mvc.annotation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

/**
 * @author javaok
 * 2022/11/29 14:46
 */
public class RequestFunc {
    Object[] paramsName;
    Object[] paramsType;
    Object obj;
    Method method;

    public RequestFunc(Object obj, Method method) {
        this.obj = obj;
        this.method = method;
        paramsName = Arrays.stream(method.getParameters()).map(Parameter::getName).toArray();
        paramsType = Arrays.stream(method.getParameters()).map(Parameter::getType).toArray();
    }

    public String invoke(Object... params) throws InvocationTargetException, IllegalAccessException {
        return (String) method.invoke(obj, params);
    }

    public Object[] getParamsName() {
        return paramsName;
    }

    public Object[] getParamsType() {
        return paramsType;
    }
}
