package com.mvc.filter;

import com.mvc.transaction.TransactionManager;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
import java.sql.SQLException;

/**
 * @author javaok
 * 2022/11/28 14:11
 */
@WebFilter("/*")
public class FilterImp implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            TransactionManager.getInstance().start();

            filterChain.doFilter(servletRequest, servletResponse);

            TransactionManager.getInstance().commit();
        } catch (SQLException e) {
            try {
                TransactionManager.getInstance().rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }
}
