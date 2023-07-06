package com.simplebooks.api.api;

public class BaseApi {

    protected static final String baseUri = "https://simple-books-api.glitch.me/";
    public String statusEndpoint = baseUri + "status/";
    public String booksEndpoint = baseUri + "books/";
    public String ordersEndpoint = baseUri + "orders/";

    public String bearerToken = "4c837d3453f6cd0196d906be22b75da81a75d5f6bf705434cb401c83c314deb3";

}
