package net.proselyte.api;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.proselyte.context.ApplicationContextInjector;
import net.proselyte.dto.UserDto;
import net.proselyte.service.UserService;
import net.proselyte.util.ControllerUtil;

import java.io.IOException;
import java.util.Optional;

import static jakarta.servlet.http.HttpServletResponse.SC_CREATED;

@WebServlet("/api/v1/users/*")
public class UserRestControllerV1 extends HttpServlet {
    private final UserService userService = ApplicationContextInjector.getInstance()
                                                                      .getUserServiceInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Optional<Integer> id = ControllerUtil.getIntPathVariable(request.getPathInfo());
        if (id.isPresent()) {
            ControllerUtil.dtoToResponse(userService.findById(id.get()), response);
        } else {
            ControllerUtil.dtoToResponse(userService.findAll(), response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserDto userDto = ControllerUtil.requestToDto(request, UserDto.class);
        userService.save(userDto);
        response.setStatus(SC_CREATED);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Optional<Integer> id = ControllerUtil.getIntPathVariable(request.getPathInfo());
        if (id.isPresent()) {
            UserDto userDto = ControllerUtil.requestToDto(request, UserDto.class);
            userService.update(id.get(), userDto);
            response.setStatus(SC_CREATED);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Optional<Integer> id = ControllerUtil.getIntPathVariable(request.getPathInfo());
        id.ifPresent(userService::remove);
    }
}
