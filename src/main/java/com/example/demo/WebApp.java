package com.example.demo;

//import ch.qos.logback.core.joran.conditional.ThenAction;
//import io.prometheus.metrics.model.snapshots.Unit;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.JsonNode;
import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import io.prometheus.client.exporter.HTTPServer;

import java.io.IOException;
import java.time.Duration;

@RestController
public class WebApp {

    private static final Counter counter = Counter.build()
            .name("total_requests")
            .help("Total number of requests to /hello endpoint")
            .register();

    private static final Histogram requestDuration = Histogram.build()
            .name("http_request_duration_ms")
            .help("HTTP requests service time in ms")
            .buckets(20,40,60,80,100,120,140,160,180,200) // create time buckets at an interval of 20ms.
//            .unit(Unit.SECONDS+"")
            .labelNames("method", "path", "status_code")
            .register();

//    private static final Histogram requestLatencyForPost = Histogram.build()
//            .name("post_request_latency_milliseconds")
//            .help("Request latency for POST requests in milliseconds")
//            .labelNames("status_code")
//            .register();

    private static final Histogram requestLatencyForHello = Histogram.build()
            .name("hello_request_latency_milliseconds")
            .help("Request latency for Hello requests in milliseconds")
            .register();

    static {
        try {
            // Start Prometheus HTTPServer on port 9400
            HTTPServer server = new HTTPServer(9400);
            System.out.println("HTTPServer listening on port http://localhost:" + server.getPort() + "/metrics");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/Hello")
    public String Hello() {

        counter.inc();
        Histogram.Timer requestTimer = requestLatencyForHello.startTimer();
        try{
            Thread.sleep(10);
            return "Trying to build a Server";
        }catch(Exception x) {
            requestTimer.observeDuration(); //observe request duration
        }
        return "hello";
    }

    @PostMapping("/PostEndpoint")
    public ResponseEntity<String> tryDelay(@RequestBody JsonNode requestBody) {
        String keyValue = requestBody.has("key") ? requestBody.get("key").asText() : "Key not found";
        System.out.println("POST request received with body: " + keyValue);


        long start = System.nanoTime();
        try {

            int delayValue = Integer.parseInt(keyValue);
            Thread.sleep(delayValue);
            long durationInMillis = (System.nanoTime() - start) / 1_000_000; // Convert nanoseconds to milliseconds
            System.out.println("POST request - delay of " + durationInMillis + " ms");

            requestDuration.labels("POST", "/PostEndpoint", "200")
                    .observe(durationInMillis); // Observe in milliseconds
            System.out.println("POST request - delay of " + durationInMillis + " ms");
            return ResponseEntity.ok("Delay processed: " + durationInMillis + " ms");

        } catch (NumberFormatException ex) {
            requestDuration.labels("POST", "/PostEndpoint", "400")
                    .observe((System.nanoTime() - start) / 1_000_000.0);
            return ResponseEntity.badRequest().body("Invalid number format for key: " + keyValue);
        } catch (InterruptedException ex) {
            requestDuration.labels("POST", "/PostEndpoint", "500")
                    .observe((System.nanoTime() - start) / 1_000_000.0);
            Thread.currentThread().interrupt();
            return ResponseEntity.internalServerError().body("Error during thread sleep: " + ex.getMessage());
        }
    }
}


