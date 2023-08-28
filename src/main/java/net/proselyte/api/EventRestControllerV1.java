package net.proselyte.api;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.proselyte.context.ApplicationContextInjector;
import net.proselyte.service.EventService;
import net.proselyte.util.ControllerUtil;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/api/v1/events/*")
public class EventRestControllerV1 extends HttpServlet {
    private final EventService eventService = ApplicationContextInjector.getInstance()
                                                                        .getEventServiceInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Optional<Integer> id = ControllerUtil.getIntPathVariable(request.getPathInfo());
        if (id.isPresent()) {
            ControllerUtil.dtoToResponse(eventService.findById(id.get()), response);
        } else {
            ControllerUtil.dtoToResponse(eventService.findAll(), response);
        }
    }
}
