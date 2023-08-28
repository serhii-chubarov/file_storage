package net.proselyte.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.proselyte.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ControllerUtilTest {
    @Mock
    HttpServletResponse response;

    @Mock
    HttpServletRequest request;

    @Test
    void getIntPathVariableShouldReturnIntIfThereIsIntPathVariableInPath() {
        assertThat(ControllerUtil.getIntPathVariable("/1")).hasValue(1);
    }

    @Test
    void getIntPathVariableShouldReturnEmptyIfThereIsNoIntPathVariableInPath() {
        assertThat(ControllerUtil.getIntPathVariable("/")).isEmpty();
    }

    @Test
    void getIntPathVariableShouldReturnEmptyIfPathIsNull() {
        assertThat(ControllerUtil.getIntPathVariable(null)).isEmpty();
    }

    @Test
    void getIntPathVariableShouldReturnEmptyIfPathIsNotANumber() {
        assertThat(ControllerUtil.getIntPathVariable("/foo")).isEmpty();
    }

    @Test
    void dtoToResponseShouldWriteDtoToResponse() throws IOException {
        String expectedJson = "{\"id\":1,\"name\":\"foo\"}";
        UserDto userDto = new UserDto();
        userDto.setId(1);
        userDto.setName("foo");
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        ControllerUtil.dtoToResponse(userDto, response);

        assertThat(stringWriter.toString()).hasToString(expectedJson);
        verify(response).getWriter();
    }

    @Test
    void requestToDtoShouldReturnDtoFromRequest() throws IOException {
        UserDto expectedUserDto = new UserDto();
        expectedUserDto.setId(1);
        expectedUserDto.setName("foo");
        StringReader stringReader = new StringReader("{\"id\":1,\"name\":\"foo\"}");
        BufferedReader reader = new BufferedReader(stringReader);
        when(request.getReader()).thenReturn(reader);

        UserDto actualUserDto = ControllerUtil.requestToDto(request, UserDto.class);
        assertThat(actualUserDto).isEqualTo(expectedUserDto);
    }
}
