package net.proselyte.api;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.proselyte.dto.ApiError;
import net.proselyte.util.ControllerUtil;

import java.io.IOException;

import static jakarta.servlet.RequestDispatcher.ERROR_MESSAGE;
import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;

@WebServlet("/ErrorHandler")
@Slf4j
public class ErrorHandler extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setStatus(SC_BAD_REQUEST);
        String errorMessage = (String) request.getAttribute(ERROR_MESSAGE);
        log.debug("Bad request: {}", errorMessage);
        ApiError apiError = new ApiError(errorMessage);
        ControllerUtil.dtoToResponse(apiError, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
