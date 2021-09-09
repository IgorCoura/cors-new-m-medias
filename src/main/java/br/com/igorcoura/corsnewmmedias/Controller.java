package br.com.igorcoura.corsnewmmedias;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/mmedias")
@CrossOrigin(origins = "*", maxAge = 3600)
public class Controller {


    @PostMapping
    public ResponseEntity<String> post(String email, String password) throws Exception{

        Map<Object, Object> data = new HashMap<>();
        data.put("maua_email", email);
        data.put("maua_senha", password);
        data.put("maua_submit", "Enviar");

        var requestPost = HttpRequest.newBuilder()
                .uri(new URI("https://www2.maua.br/mauanet.2.0"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(buildFormDataFromMap(data))
                .build();
        var responsePost = HttpClient.newBuilder()
                .build().send(requestPost, HttpResponse.BodyHandlers.ofString());

        var requestGet = HttpRequest.newBuilder()
                .uri(new URI("https://www2.maua.br/mauanet.2.0/boletim-escolar"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .setHeader("cookie", responsePost.headers().map().get("set-cookie").get(0))
                .GET()
                .build();
        var responseGet = HttpClient.newBuilder()
                .build().send(requestGet, HttpResponse.BodyHandlers.ofString());
        if(responsePost.statusCode() == 302){
            return ResponseEntity.status(HttpStatus.OK).body(responseGet.body());
        }
        else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("UNAUTHORIZED");
        }
    }

    private static HttpRequest.BodyPublisher buildFormDataFromMap(Map<Object, Object> data) {
        var builder = new StringBuilder();
        for (Map.Entry<Object, Object> entry : data.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8));
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
        }
        System.out.println(builder.toString());
        return HttpRequest.BodyPublishers.ofString(builder.toString());
    }
}
