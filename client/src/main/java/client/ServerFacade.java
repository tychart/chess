package client;

import com.google.gson.Gson;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import exception.ResponseException;
import model.*;
import server.ErrorResponse;

public class ServerFacade {

    private final String serverUrl;
    Gson gson = new Gson();

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
        this.makeRequest("POST", path, gameRequest, null, authToken);
    }

    public GameListResponse listGames(String authToken) throws ResponseException {
        var path = "/game";
        return this.makeRequest("GET", path, null, GameListResponse.class, authToken);
    }

    public void joinGame(String authToken, JoinGameRequest joinGameRequest) throws ResponseException {
        var path = "/game";
        this.makeRequest("PUT", path, joinGameRequest, null, authToken);
    }


    public void deleteDatabase() throws ResponseException {
        var path = "/db";
        this.makeRequest("DELETE", path, null, null);
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
            // Attempt to read the response body to get a more descriptive error message
            String responseBody = readErrorResponse(http);
            throw new ResponseException(status, responseBody != null ? responseBody : "Request failed with status: " + status);
        }
    }

    private String readErrorResponse(HttpURLConnection http) {
        try {
            InputStream errorStream = http.getErrorStream();
            if (errorStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream));
                StringBuilder errorMessage = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    errorMessage.append(line);
                }
                return gson.fromJson(errorMessage.toString(), ErrorResponse.class).getMessage();
            }
        } catch (IOException ex) {
            // Log this if you want more details on failed error reading
        }
        return null; // Return null if no detailed error message was available
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
