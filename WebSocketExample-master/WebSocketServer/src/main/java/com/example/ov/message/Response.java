package com.example.ov.message;

/**
  * The server sends such a message to the client
  */
public class Response {
    private String response;

    public Response(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }
}
