package net.proselyte.api;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.Consumer;

import static jakarta.servlet.RequestDispatcher.ERROR_MESSAGE;
import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ErrorHandlerTest {
    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpServletRequest request;

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void doGetShouldWriteToResponseJsonWithErrorMessage() throws ServletException, IOException {
        doGet(errorHandler -> {
            try {
                errorHandler.doGet(request, response);
            } catch (ServletException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void doPostShouldWriteToResponseJsonWithErrorMessage() throws ServletException, IOException {
        doGet(errorHandler -> {
            try {
                errorHandler.doPost(request, response);
            } catch (ServletException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void doPutShouldWriteToResponseJsonWithErrorMessage() throws ServletException, IOException {
        doGet(errorHandler -> {
            try {
                errorHandler.doPut(request, response);
            } catch (ServletException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void doDeleteShouldWriteToResponseJsonWithErrorMessage() throws ServletException, IOException {
        doGet(errorHandler -> {
            try {
                errorHandler.doDelete(request, response);
            } catch (ServletException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    void doGet(Consumer<ErrorHandler> consumer) throws ServletException, IOException {
        String expectedJson = "{\"errorMessage\":\"error message\"}";
        when(request.getAttribute(ERROR_MESSAGE)).thenReturn("error message");
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        consumer.accept(errorHandler);

        verify(response).setStatus(SC_BAD_REQUEST);
        assertThat(stringWriter.toString()).hasToString(expectedJson);
    }
}
