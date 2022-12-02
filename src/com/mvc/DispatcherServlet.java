package com.mvc;

import com.mvc.annotation.AnnotationInstaller;
import com.mvc.annotation.Component;
import com.mvc.annotation.RequestFunc;
import com.mvc.thymeleaf.ViewBaseServlet;
import com.mvc.util.ParamBuilder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import static com.mvc.util.StrUtil.FORWARD;
import static com.mvc.util.StrUtil.REDIRECT;

/**
 * @author javaok
 * 2022/11/28 13:49
 */
@WebServlet("/javaweb/*")
@Component
public class DispatcherServlet extends ViewBaseServlet {
    Map<String, RequestFunc> requestMap;

    @Override
    public void init() {
        super.init();

        try {
            AnnotationInstaller.componentInstaller();
            AnnotationInstaller.componentInstaller(this);
            AnnotationInstaller.autoWiredInstaller();

            List<Object> controllers = AnnotationInstaller.getControllers();
            requestMap = AnnotationInstaller.getRequestMapping(controllers);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String pathInfo = req.getPathInfo();

        System.out.println("isRequesting = \"" + req.getPathInfo() + "\"");
        System.out.println("req.getServletPath() = " + req.getServletPath());
        RequestFunc requestFunc = requestMap.get(pathInfo);

        Object[] paramsName = requestFunc.getParamsName();
        Object[] paramsType = requestFunc.getParamsType();

        Object[] params = new Object[paramsName.length];
        for (int i = 0; i < params.length; i++) {
            if (paramsType[i] == HttpSession.class) {
                params[i] = req.getSession();
            } else {
                String value = req.getParameter(paramsName[i].toString());
                Object param = ParamBuilder.build(value, paramsType[i]);
                params[i] = param;
            }
        }

        try {
            String s = requestFunc.invoke(params);

            if (s.startsWith(REDIRECT)) {
                resp.sendRedirect(s.substring(REDIRECT.length()));
            } else if (s.startsWith(FORWARD)) {
                req.getRequestDispatcher(s.substring(FORWARD.length())).forward(req, resp);
            } else {
                super.processTemplate(s, req, resp);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
