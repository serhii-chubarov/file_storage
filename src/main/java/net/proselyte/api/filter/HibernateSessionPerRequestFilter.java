package net.proselyte.api.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import lombok.extern.slf4j.Slf4j;
import net.proselyte.context.ApplicationContextInjector;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.IOException;

@WebFilter("/*")
@Slf4j
public class HibernateSessionPerRequestFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.debug("Perform HibernateSessionPerRequestFilter");
        try (Session session = ApplicationContextInjector.getSessionFactory().getCurrentSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                chain.doFilter(request, response);
                transaction.commit();
                log.debug("Transaction has committed");
            } catch (Exception exception) {
                try {
                    log.debug("Transaction rollback {}", exception.getMessage());
                    transaction.rollback();
                } catch (Exception suppressedException) {
                    log.debug("Exception during rollback {}", suppressedException.getMessage());
                    exception.addSuppressed(suppressedException);
                }
                throw exception;
            }
        }
    }
}
