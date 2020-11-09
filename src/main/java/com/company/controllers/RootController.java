package com.company.controllers;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@RestController
public class RootController {

    @RequestMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

    @RequestMapping("/get-url")
    public String getUrl(@RequestParam("url") String url) {
        try {
            var obj = new URL(url);
            var connection = (HttpURLConnection) obj.openConnection();

            connection.setRequestMethod("GET");

            var in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            var response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();
        } catch (IOException e) {
            return e.toString();
        }
    }
}