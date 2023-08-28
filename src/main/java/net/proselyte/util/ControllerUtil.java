package net.proselyte.util;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;
import java.util.stream.Collectors;

@UtilityClass
public class ControllerUtil {
    private final Gson gson = new Gson();

    public Optional<Integer> getIntPathVariable(String path) {
        if (path == null) {
            return Optional.empty();
        }
        String[] pathParts = path.split("/");
        if (pathParts.length > 1) {
            try {
                return Optional.of(Integer.valueOf(pathParts[1]));
            } catch (NumberFormatException exception) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    public <T> void dtoToResponse(T object, HttpServletResponse response) throws IOException {
        try (PrintWriter writer = response.getWriter()) {
            String json = gson.toJson(object);
            writer.write(json);
        }
    }

    public <T> T requestToDto(HttpServletRequest request, Class<T> clazz) throws IOException {
        String data = request.getReader()
                             .lines()
                             .collect(Collectors.joining());
        return gson.fromJson(data, clazz);
    }
}
