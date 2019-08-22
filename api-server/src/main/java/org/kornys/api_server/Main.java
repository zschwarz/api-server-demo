package org.kornys.api_server;

public class Main {

    public static void main(String[] args) {
        HttpListener server = new HttpListener();
        server.start();
    }
}
