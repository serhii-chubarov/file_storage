package net.proselyte.api.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.proselyte.context.ApplicationContextInjector;
import net.proselyte.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HibernateSessionPerRequestFilterTest {
    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpServletRequest request;

    @Mock
    private FilterChain chain;

    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @Mock
    private Transaction transaction;

    private final HibernateSessionPerRequestFilter filter = new HibernateSessionPerRequestFilter();

    @Test
    void doFilterShouldWrapRequestInTransaction() throws ServletException, IOException {
        try (MockedStatic<HibernateUtil> hibernateUtil = mockStatic(HibernateUtil.class);
             MockedStatic<ApplicationContextInjector> appInjector = mockStatic(ApplicationContextInjector.class)) {
            hibernateUtil.when(() -> HibernateUtil.getSessionFactory(any(), any(), any())).thenReturn(sessionFactory);
            appInjector.when(ApplicationContextInjector::getSessionFactory).thenReturn(sessionFactory);
            hibernateUtil.when(sessionFactory::getCurrentSession).thenReturn(session);
            hibernateUtil.when(session::beginTransaction).thenReturn(transaction);

            filter.doFilter(request, response, chain);

            appInjector.verify(ApplicationContextInjector::getSessionFactory);
            verify(chain).doFilter(any(), any());
            verify(transaction).commit();
            verify(transaction, never()).rollback();
        }
    }

    @Test
    void doFilterShouldThrowExceptionIfThereIsErrorDuringProcessingRequest() throws ServletException, IOException {
        try (MockedStatic<HibernateUtil> hibernateUtil = mockStatic(HibernateUtil.class);
             MockedStatic<ApplicationContextInjector> appInjector = mockStatic(ApplicationContextInjector.class)) {
            hibernateUtil.when(() -> HibernateUtil.getSessionFactory(any(), any(), any())).thenReturn(sessionFactory);
            appInjector.when(ApplicationContextInjector::getSessionFactory).thenReturn(sessionFactory);
            hibernateUtil.when(sessionFactory::getCurrentSession).thenReturn(session);
            hibernateUtil.when(session::beginTransaction).thenReturn(transaction);
            hibernateUtil.when(() -> chain.doFilter(any(), any())).thenThrow(new IOException());

            assertThatThrownBy(() -> filter.doFilter(request, response, chain)).isInstanceOf(IOException.class);

            appInjector.verify(ApplicationContextInjector::getSessionFactory);
            verify(chain).doFilter(any(), any());
            verify(transaction, never()).commit();
            verify(transaction).rollback();
        }
    }

    @Test
    void doFilterShouldThrowExceptionIfThereIsErrorDuringRollbackTransaction() throws ServletException, IOException {
        try (MockedStatic<HibernateUtil> hibernateUtil = mockStatic(HibernateUtil.class);
             MockedStatic<ApplicationContextInjector> appInjector = mockStatic(ApplicationContextInjector.class)) {
            hibernateUtil.when(() -> HibernateUtil.getSessionFactory(any(), any(), any())).thenReturn(sessionFactory);
            appInjector.when(ApplicationContextInjector::getSessionFactory).thenReturn(sessionFactory);
            hibernateUtil.when(sessionFactory::getCurrentSession).thenReturn(session);
            hibernateUtil.when(session::beginTransaction).thenReturn(transaction);
            hibernateUtil.when(() -> chain.doFilter(any(), any())).thenThrow(new IOException());
            hibernateUtil.when(transaction::rollback).thenThrow(new IllegalStateException());

            assertThatThrownBy(() -> filter.doFilter(request, response, chain)).isInstanceOf(IOException.class);

            appInjector.verify(ApplicationContextInjector::getSessionFactory);
            verify(chain).doFilter(any(), any());
            verify(transaction, never()).commit();
            verify(transaction).rollback();
        }
    }
}
