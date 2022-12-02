package com.mvc.annotation;

import com.mvc.util.ClassUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author javaok
 * 2022/11/29 10:18
 */
public class AnnotationInstaller {
    static Map<Class<?>, Object> iocContainer = new HashMap<>();

    public static void componentInstaller() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<Class<?>> classes = ClassUtil.getClasses("com.javaweb");
        for (Class<?> aClass : classes) {
            Component component = aClass.getAnnotation(Component.class);
            if (component != null) {
                iocContainer.put(aClass, aClass.getConstructor().newInstance());
            }
        }
    }

    public static void componentInstaller(Object obj) {
        iocContainer.put(obj.getClass(), obj);
    }

    public static void autoWiredInstaller() throws IllegalAccessException {
        for (Object obj : iocContainer.values()) {

            for (Field declaredField : obj.getClass().getDeclaredFields()) {

                declaredField.setAccessible(true);
                AutoWired annotation = declaredField.getAnnotation(AutoWired.class);
                if (annotation != null && declaredField.get(obj) == null) {
                    if (annotation.value().equals(void.class)) {
                        declaredField.set(obj, iocContainer.get(declaredField.getType()));
                    } else {
                        declaredField.set(obj, iocContainer.get(annotation.value()));
                    }
                }
            }
        }
    }

    public static List<Object> getControllers() {
        List<Object> rets = new ArrayList<>();

        for (Map.Entry<Class<?>, Object> classObjectEntry : iocContainer.entrySet()) {
            Controller annotation = classObjectEntry.getKey().getAnnotation(Controller.class);
            if (annotation != null) {
                rets.add(classObjectEntry.getValue());
            }
        }

        return rets;
    }


    public static Map<String, RequestFunc> getRequestMapping(List<Object> controllers) {
        Map<String, RequestFunc> requestMap = new HashMap<>(32);
        for (Object controller : controllers) {
            Class<?> aClass = controller.getClass();
            RequestMapping annotation = aClass.getAnnotation(RequestMapping.class);

            String prefix = "";
            if (annotation != null) {
                prefix += annotation.value();
            }

            for (Method declaredMethod : aClass.getDeclaredMethods()) {
                declaredMethod.setAccessible(true);
                RequestMapping annotation1 = declaredMethod.getAnnotation(RequestMapping.class);
                if (annotation1 != null) {
                    RequestFunc requestFunc = new RequestFunc(controller, declaredMethod);
                    requestMap.put(prefix + annotation1.value(), requestFunc);
                }
            }
        }

        return requestMap;
    }
}
