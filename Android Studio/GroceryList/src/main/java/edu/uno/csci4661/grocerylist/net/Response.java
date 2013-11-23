package edu.uno.csci4661.grocerylist.net;

/**
 * Created by danielward on 11/17/13.
 */
public class Response {
    private String data;
    private int statusCode;
    private String response;

    public Response(int statusCode, String response, String data) {
        this.statusCode = statusCode;
        this.response = response;
        this.data = data;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public String getResponse() {
        return this.response;
    }

    public String getData() {
        return this.data;
    }
}
