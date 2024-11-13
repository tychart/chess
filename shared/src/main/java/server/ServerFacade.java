package server;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import exception.ResponseException;
import model.*;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public LoginResponse registerUser(UserData newUser) throws ResponseException {
        var path = "/user";
        return this.makeRequest("POST", path, newUser, LoginResponse.class);
    }

    public LoginResponse loginUser(UserData loginUser) throws ResponseException {
        var path = "/session";
        return this.makeRequest("POST", path, loginUser, LoginResponse.class);
    }

    public void logoutUser(String authToken) throws ResponseException {
        var path = "/session";
        this.makeRequest("DELETE", path, null, null, authToken);
    }

    public void createGame(String authToken, GameRequest gameRequest) throws ResponseException {
        var path = "/game";
        this.makeRequest("POST", path, gameRequest, GameData.class, authToken);
    }


    public void deleteDatabase(String authToken) throws ResponseException {
        var path = "/db";
        this.makeRequest("DELETE", path, null, null, authToken);
    }

    // Version of makeRequest that does not require an authToken
    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        return makeRequest(method, path, request, responseClass, null);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            // Add the authToken to the request headers if it's provided
            if (authToken != null) {
                http.setRequestProperty("authorization", authToken);
            }

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ResponseException(status, "failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
