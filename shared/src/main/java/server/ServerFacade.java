package server;

import com.google.gson.Gson;
import dto.*;
import exception.ResponseException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;
    private String authToken = "";

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public RegisterResult register(RegisterRequest req) throws ResponseException {
        var path = "/user";
        var request = buildRequest("POST", path, req);
        var response = sendRequest(request);
        var responseData = handleResponse(response, RegisterResult.class);
        this.authToken = responseData.authToken();
        return responseData;
    }

    public LoginResult login(LoginRequest req) throws ResponseException {
        var path = "/session";
        var request = buildRequest("POST", path, req);
        var response = sendRequest(request);
        var responseData = handleResponse(response, LoginResult.class);
        this.authToken = responseData.authToken();
        return responseData;
    }

    public void logout() throws ResponseException {
        var path = "/session";
        var request = buildRequest("DELETE", path, null);
        var response = sendRequest(request);
        authToken = "";
        handleResponse(response, null);
    }

    public ListGamesResult listGames() throws ResponseException {
        var path = "/game";
        var request = buildRequest("GET", path, null);
        var response = sendRequest(request);
        return handleResponse(response, ListGamesResult.class);
    }

    public CreateGameResult createGame(CreateGameRequest req) throws ResponseException {
        var path = "/game";
        var request = buildRequest("POST", path, req);
        var response = sendRequest(request);
        return handleResponse(response, CreateGameResult.class);
    }

    public void joinGame(JoinGameRequest req) throws ResponseException {
        var path = "/game";
        var request = buildRequest("PUT", path, req);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public String getAuthToken() {
        return this.authToken;
    }

    private HttpRequest buildRequest(String method, String path, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .header("Authorization", authToken)
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        return request.build();
    }

    private HttpRequest.BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw ResponseException.fromJson(ResponseException.Code.ServerError, body);
            }

            throw new ResponseException(ResponseException.fromHttpStatusCode(status), "other failure: " + status);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
