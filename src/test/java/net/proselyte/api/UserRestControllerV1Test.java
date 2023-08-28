package net.proselyte.api;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.proselyte.context.ApplicationContextInjector;
import net.proselyte.dto.UserDto;
import net.proselyte.service.UserService;
import net.proselyte.util.HibernateUtil;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import static jakarta.servlet.http.HttpServletResponse.SC_CREATED;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRestControllerV1Test {
    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpServletRequest request;

    @Mock
    private UserService userService;

    @Mock
    private SessionFactory sessionFactory;

    @Test
    void doGetShouldWriteToResponseUsersIfThereIsNoIntPathVariable() throws ServletException, IOException {
        try (MockedStatic<HibernateUtil> hibernateUtil = mockStatic(HibernateUtil.class);
             MockedStatic<ApplicationContextInjector> injectorMockedStatic = mockStatic(ApplicationContextInjector.class)) {
            ApplicationContextInjector injector = mock(ApplicationContextInjector.class);
            when(injector.getUserServiceInstance()).thenReturn(userService);
            hibernateUtil.when(() -> HibernateUtil.getSessionFactory(any(), any(), any())).thenReturn(sessionFactory);
            injectorMockedStatic.when(ApplicationContextInjector::getSessionFactory).thenReturn(sessionFactory);
            injectorMockedStatic.when(ApplicationContextInjector::getInstance).thenReturn(injector);
            String expectedJson = "[]";
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            when(response.getWriter()).thenReturn(writer);

            UserRestControllerV1 controller = new UserRestControllerV1();
            controller.doGet(request, response);

            verify(userService).findAll();
            assertThat(stringWriter.toString()).hasToString(expectedJson);
        }
    }

    @Test
    void doGetShouldWriteToResponseFileIfThereIsEventsId() throws ServletException, IOException {
        try (MockedStatic<HibernateUtil> hibernateUtil = mockStatic(HibernateUtil.class);
             MockedStatic<ApplicationContextInjector> injectorMockedStatic = mockStatic(ApplicationContextInjector.class)) {
            ApplicationContextInjector injector = mock(ApplicationContextInjector.class);
            when(injector.getUserServiceInstance()).thenReturn(userService);
            hibernateUtil.when(() -> HibernateUtil.getSessionFactory(any(), any(), any())).thenReturn(sessionFactory);
            injectorMockedStatic.when(ApplicationContextInjector::getSessionFactory).thenReturn(sessionFactory);
            injectorMockedStatic.when(ApplicationContextInjector::getInstance).thenReturn(injector);
            int id = 1;
            String expectedJson = "{\"id\":1,\"name\":\"alice\"}";
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            when(response.getWriter()).thenReturn(writer);
            when(request.getPathInfo()).thenReturn("/" + id);
            when(userService.findById(id)).thenReturn(new UserDto(1, "alice"));

            UserRestControllerV1 controller = new UserRestControllerV1();
            controller.doGet(request, response);

            verify(userService).findById(id);
            assertThat(stringWriter.toString()).hasToString(expectedJson);
        }
    }

    @Test
    void doPostShouldSaveFile() throws ServletException, IOException {
        try (MockedStatic<HibernateUtil> hibernateUtil = mockStatic(HibernateUtil.class);
             MockedStatic<ApplicationContextInjector> injectorMockedStatic = mockStatic(ApplicationContextInjector.class)) {
            ApplicationContextInjector injector = mock(ApplicationContextInjector.class);
            when(injector.getUserServiceInstance()).thenReturn(userService);
            hibernateUtil.when(() -> HibernateUtil.getSessionFactory(any(), any(), any())).thenReturn(sessionFactory);
            injectorMockedStatic.when(ApplicationContextInjector::getSessionFactory).thenReturn(sessionFactory);
            injectorMockedStatic.when(ApplicationContextInjector::getInstance).thenReturn(injector);
            StringReader stringReader = new StringReader("{\"name\":\"alice\"}");
            BufferedReader reader = new BufferedReader(stringReader);
            when(request.getReader()).thenReturn(reader);
            UserDto expectedUserDto = new UserDto("alice");

            UserRestControllerV1 controller = new UserRestControllerV1();
            controller.doPost(request, response);

            verify(userService).save(expectedUserDto);
            verify(response).setStatus(SC_CREATED);
        }
    }

    @Test
    void doPutShouldUpdateFileIfThereIsFileId() throws ServletException, IOException {
        try (MockedStatic<HibernateUtil> hibernateUtil = mockStatic(HibernateUtil.class);
             MockedStatic<ApplicationContextInjector> injectorMockedStatic = mockStatic(ApplicationContextInjector.class)) {
            int id = 1;
            ApplicationContextInjector injector = mock(ApplicationContextInjector.class);
            when(injector.getUserServiceInstance()).thenReturn(userService);
            hibernateUtil.when(() -> HibernateUtil.getSessionFactory(any(), any(), any())).thenReturn(sessionFactory);
            injectorMockedStatic.when(ApplicationContextInjector::getSessionFactory).thenReturn(sessionFactory);
            injectorMockedStatic.when(ApplicationContextInjector::getInstance).thenReturn(injector);
            StringReader stringReader = new StringReader("{\"name\":\"bob\"}");
            BufferedReader reader = new BufferedReader(stringReader);
            when(request.getReader()).thenReturn(reader);
            UserDto expectedUserDto = new UserDto("bob");
            when(request.getPathInfo()).thenReturn("/" + id);

            UserRestControllerV1 controller = new UserRestControllerV1();
            controller.doPut(request, response);

            verify(userService).update(id, expectedUserDto);
            verify(response).setStatus(SC_CREATED);
        }
    }

    @Test
    void doPutShouldNotUpdateFileIfThereIsNoFileId() throws ServletException, IOException {
        try (MockedStatic<HibernateUtil> hibernateUtil = mockStatic(HibernateUtil.class);
             MockedStatic<ApplicationContextInjector> injectorMockedStatic = mockStatic(ApplicationContextInjector.class)) {
            ApplicationContextInjector injector = mock(ApplicationContextInjector.class);
            when(injector.getUserServiceInstance()).thenReturn(userService);
            hibernateUtil.when(() -> HibernateUtil.getSessionFactory(any(), any(), any())).thenReturn(sessionFactory);
            injectorMockedStatic.when(ApplicationContextInjector::getSessionFactory).thenReturn(sessionFactory);
            injectorMockedStatic.when(ApplicationContextInjector::getInstance).thenReturn(injector);
            when(request.getPathInfo()).thenReturn("/");

            UserRestControllerV1 controller = new UserRestControllerV1();
            controller.doPut(request, response);

            verify(userService, never()).update(any(), any());
        }
    }

    @Test
    void doDeleteShouldDeleteFile() throws ServletException, IOException {
        try (MockedStatic<HibernateUtil> hibernateUtil = mockStatic(HibernateUtil.class);
             MockedStatic<ApplicationContextInjector> injectorMockedStatic = mockStatic(ApplicationContextInjector.class)) {
            int id = 1;
            ApplicationContextInjector injector = mock(ApplicationContextInjector.class);
            when(injector.getUserServiceInstance()).thenReturn(userService);
            hibernateUtil.when(() -> HibernateUtil.getSessionFactory(any(), any(), any())).thenReturn(sessionFactory);
            injectorMockedStatic.when(ApplicationContextInjector::getSessionFactory).thenReturn(sessionFactory);
            injectorMockedStatic.when(ApplicationContextInjector::getInstance).thenReturn(injector);
            when(request.getPathInfo()).thenReturn("/" + id);

            UserRestControllerV1 controller = new UserRestControllerV1();
            controller.doDelete(request, response);

            verify(userService).remove(id);
        }
    }
}
