package net.proselyte.api;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.proselyte.context.ApplicationContextInjector;
import net.proselyte.dto.FileDto;
import net.proselyte.service.FileService;
import net.proselyte.util.ControllerUtil;

import java.io.IOException;
import java.util.Optional;

import static jakarta.servlet.http.HttpServletResponse.SC_CREATED;

@WebServlet("/api/v1/files/*")
public class FileRestControllerV1 extends HttpServlet {
    private final FileService fileService = ApplicationContextInjector.getInstance()
                                                                      .getFileServiceInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Optional<Integer> id = ControllerUtil.getIntPathVariable(request.getPathInfo());
        if (id.isPresent()) {
            ControllerUtil.dtoToResponse(fileService.findById(id.get()), response);
        } else {
            ControllerUtil.dtoToResponse(fileService.findAll(), response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        FileDto fileDto = ControllerUtil.requestToDto(request, FileDto.class);
        Integer userId = request.getIntHeader("user-id");
        fileService.save(fileDto, userId);
        response.setStatus(SC_CREATED);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Optional<Integer> id = ControllerUtil.getIntPathVariable(request.getPathInfo());
        if (id.isPresent()) {
            FileDto fileDto = ControllerUtil.requestToDto(request, FileDto.class);
            fileService.update(id.get(), fileDto);
            response.setStatus(SC_CREATED);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Optional<Integer> id = ControllerUtil.getIntPathVariable(request.getPathInfo());
        id.ifPresent(fileService::remove);
    }
}
