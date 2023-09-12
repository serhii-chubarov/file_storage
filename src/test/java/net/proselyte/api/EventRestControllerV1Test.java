package net.proselyte.api;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.proselyte.context.ApplicationContextInjector;
import net.proselyte.dto.EventDto;
import net.proselyte.service.EventService;
import net.proselyte.util.HibernateUtil;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventRestControllerV1Test {
    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpServletRequest request;

    @Mock
    private EventService eventService;

    @Mock
    private SessionFactory sessionFactory;

    @Test
    void doGetShouldWriteToResponseEventsIfThereIsNoIntPathVariable() throws ServletException, IOException {
        try (MockedStatic<HibernateUtil> hibernateUtil = mockStatic(HibernateUtil.class);
             MockedStatic<ApplicationContextInjector> injectorMockedStatic = mockStatic(ApplicationContextInjector.class)) {
            ApplicationContextInjector injector = mock(ApplicationContextInjector.class);
            when(injector.getEventServiceInstance()).thenReturn(eventService);
            String expectedJson = "[]";
            hibernateUtil.when(() -> HibernateUtil.getSessionFactory(any(), any(), any())).thenReturn(sessionFactory);
            injectorMockedStatic.when(ApplicationContextInjector::getSessionFactory).thenReturn(sessionFactory);
            injectorMockedStatic.when(ApplicationContextInjector::getInstance).thenReturn(injector);
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            when(response.getWriter()).thenReturn(writer);

            EventRestControllerV1 controller = new EventRestControllerV1();
            controller.doGet(request, response);

            verify(eventService).findAll();
            assertThat(stringWriter.toString()).hasToString(expectedJson);
        }
    }

    @Test
    void doGetShouldWriteToResponseEventIfThereIsEventsId() throws ServletException, IOException {
        try (MockedStatic<HibernateUtil> hibernateUtil = mockStatic(HibernateUtil.class);
             MockedStatic<ApplicationContextInjector> injectorMockedStatic = mockStatic(ApplicationContextInjector.class)) {
            int id = 1;
            String expectedJson = "{\"id\":1}";
            ApplicationContextInjector injector = mock(ApplicationContextInjector.class);
            when(injector.getEventServiceInstance()).thenReturn(eventService);
            hibernateUtil.when(() -> HibernateUtil.getSessionFactory(any(), any(), any())).thenReturn(sessionFactory);
            injectorMockedStatic.when(ApplicationContextInjector::getSessionFactory).thenReturn(sessionFactory);
            injectorMockedStatic.when(ApplicationContextInjector::getInstance).thenReturn(injector);
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            when(response.getWriter()).thenReturn(writer);
            when(request.getPathInfo()).thenReturn("/" + id);
            when(eventService.findById(id)).thenReturn(new EventDto(1, null, null));

            EventRestControllerV1 controller = new EventRestControllerV1();
            controller.doGet(request, response);

            verify(eventService).findById(id);
            assertThat(stringWriter.toString()).hasToString(expectedJson);
        }
    }
}
