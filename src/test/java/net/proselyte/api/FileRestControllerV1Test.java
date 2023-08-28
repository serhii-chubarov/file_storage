package net.proselyte.api;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.proselyte.context.ApplicationContextInjector;
import net.proselyte.dto.FileDto;
import net.proselyte.service.FileService;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileRestControllerV1Test {
    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpServletRequest request;

    @Mock
    private FileService fileService;

    @Mock
    private SessionFactory sessionFactory;

    @Test
    void doGetShouldWriteToResponseFilesIfThereIsNoIntPathVariable() throws ServletException, IOException {
        try (MockedStatic<HibernateUtil> hibernateUtil = mockStatic(HibernateUtil.class);
             MockedStatic<ApplicationContextInjector> injectorMockedStatic = mockStatic(ApplicationContextInjector.class)) {
            ApplicationContextInjector injector = mock(ApplicationContextInjector.class);
            when(injector.getFileServiceInstance()).thenReturn(fileService);
            hibernateUtil.when(() -> HibernateUtil.getSessionFactory(any(), any(), any())).thenReturn(sessionFactory);
            injectorMockedStatic.when(ApplicationContextInjector::getSessionFactory).thenReturn(sessionFactory);
            injectorMockedStatic.when(ApplicationContextInjector::getInstance).thenReturn(injector);
            String expectedJson = "[]";
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            when(response.getWriter()).thenReturn(writer);

            FileRestControllerV1 controller = new FileRestControllerV1();
            controller.doGet(request, response);

            verify(fileService).findAll();
            assertThat(stringWriter.toString()).hasToString(expectedJson);
        }
    }

    @Test
    void doGetShouldWriteToResponseFileIfThereIsEventsId() throws ServletException, IOException {
        try (MockedStatic<HibernateUtil> hibernateUtil = mockStatic(HibernateUtil.class);
             MockedStatic<ApplicationContextInjector> injectorMockedStatic = mockStatic(ApplicationContextInjector.class)) {
            ApplicationContextInjector injector = mock(ApplicationContextInjector.class);
            when(injector.getFileServiceInstance()).thenReturn(fileService);
            hibernateUtil.when(() -> HibernateUtil.getSessionFactory(any(), any(), any())).thenReturn(sessionFactory);
            injectorMockedStatic.when(ApplicationContextInjector::getSessionFactory).thenReturn(sessionFactory);
            injectorMockedStatic.when(ApplicationContextInjector::getInstance).thenReturn(injector);
            int id = 1;
            String expectedJson = "{\"id\":1,\"name\":\"name\",\"filePath\":\"path\",\"data\":\"0xDEADBEEF\"}";
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            when(response.getWriter()).thenReturn(writer);
            when(request.getPathInfo()).thenReturn("/" + id);
            when(fileService.findById(id)).thenReturn(new FileDto(1, "name", "path", "0xDEADBEEF"));

            FileRestControllerV1 controller = new FileRestControllerV1();
            controller.doGet(request, response);

            verify(fileService).findById(id);
            assertThat(stringWriter.toString()).hasToString(expectedJson);
        }
    }

    @Test
    void doPostShouldSaveFile() throws ServletException, IOException {
        try (MockedStatic<HibernateUtil> hibernateUtil = mockStatic(HibernateUtil.class);
             MockedStatic<ApplicationContextInjector> injectorMockedStatic = mockStatic(ApplicationContextInjector.class)) {
            int id = 1;
            ApplicationContextInjector injector = mock(ApplicationContextInjector.class);
            when(injector.getFileServiceInstance()).thenReturn(fileService);
            hibernateUtil.when(() -> HibernateUtil.getSessionFactory(any(), any(), any())).thenReturn(sessionFactory);
            injectorMockedStatic.when(ApplicationContextInjector::getSessionFactory).thenReturn(sessionFactory);
            injectorMockedStatic.when(ApplicationContextInjector::getInstance).thenReturn(injector);
            StringReader stringReader = new StringReader("{\"name\":\"file_1\",\"data\":\"0xDEADBEEF\"}");
            BufferedReader reader = new BufferedReader(stringReader);
            when(request.getReader()).thenReturn(reader);
            when(request.getIntHeader("user-id")).thenReturn(id);
            FileDto expectedFileDto = new FileDto("file_1", "0xDEADBEEF");

            FileRestControllerV1 controller = new FileRestControllerV1();
            controller.doPost(request, response);

            verify(fileService).save(expectedFileDto, id);
            verify(response).setStatus(SC_CREATED);
        }
    }

    @Test
    void doPutShouldUpdateFileIfThereIsFileId() throws ServletException, IOException {
        try (MockedStatic<HibernateUtil> hibernateUtil = mockStatic(HibernateUtil.class);
             MockedStatic<ApplicationContextInjector> injectorMockedStatic = mockStatic(ApplicationContextInjector.class)) {
            ApplicationContextInjector injector = mock(ApplicationContextInjector.class);
            when(injector.getFileServiceInstance()).thenReturn(fileService);
            hibernateUtil.when(() -> HibernateUtil.getSessionFactory(any(), any(), any())).thenReturn(sessionFactory);
            injectorMockedStatic.when(ApplicationContextInjector::getSessionFactory).thenReturn(sessionFactory);
            injectorMockedStatic.when(ApplicationContextInjector::getInstance).thenReturn(injector);
            int id = 1;
            StringReader stringReader = new StringReader("{\"name\":\"file_1\",\"data\":\"0xDEADBEEF\"}");
            BufferedReader reader = new BufferedReader(stringReader);
            when(request.getReader()).thenReturn(reader);
            FileDto expectedFileDto = new FileDto("file_1", "0xDEADBEEF");
            when(request.getPathInfo()).thenReturn("/" + id);

            FileRestControllerV1 controller = new FileRestControllerV1();
            controller.doPut(request, response);

            verify(fileService).update(id, expectedFileDto);
            verify(response).setStatus(SC_CREATED);
        }
    }

    @Test
    void doPutShouldNotUpdateFileIfThereIsNoFileId() throws ServletException, IOException {
        try (MockedStatic<HibernateUtil> hibernateUtil = mockStatic(HibernateUtil.class);
             MockedStatic<ApplicationContextInjector> injectorMockedStatic = mockStatic(ApplicationContextInjector.class)) {
            ApplicationContextInjector injector = mock(ApplicationContextInjector.class);
            when(injector.getFileServiceInstance()).thenReturn(fileService);
            hibernateUtil.when(() -> HibernateUtil.getSessionFactory(any(), any(), any())).thenReturn(sessionFactory);
            injectorMockedStatic.when(ApplicationContextInjector::getSessionFactory).thenReturn(sessionFactory);
            injectorMockedStatic.when(ApplicationContextInjector::getInstance).thenReturn(injector);
            when(request.getPathInfo()).thenReturn("/");

            FileRestControllerV1 controller = new FileRestControllerV1();
            controller.doPut(request, response);

            verify(fileService, never()).update(any(), any());
        }
    }

    @Test
    void doDeleteShouldDeleteFile() throws ServletException, IOException {
        try (MockedStatic<HibernateUtil> hibernateUtil = mockStatic(HibernateUtil.class);
             MockedStatic<ApplicationContextInjector> injectorMockedStatic = mockStatic(ApplicationContextInjector.class)) {
            ApplicationContextInjector injector = mock(ApplicationContextInjector.class);
            when(injector.getFileServiceInstance()).thenReturn(fileService);
            hibernateUtil.when(() -> HibernateUtil.getSessionFactory(any(), any(), any())).thenReturn(sessionFactory);
            injectorMockedStatic.when(ApplicationContextInjector::getSessionFactory).thenReturn(sessionFactory);
            injectorMockedStatic.when(ApplicationContextInjector::getInstance).thenReturn(injector);
            int id = 1;
            when(request.getPathInfo()).thenReturn("/" + id);

            FileRestControllerV1 controller = new FileRestControllerV1();
            controller.doDelete(request, response);

            verify(fileService).remove(id);
        }
    }
}
