package com.baarsch_bytes.studentRegDemo.config;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// The web UI is a single-page app: React decides what to show for each URL in the browser.
// Opening /students directly (or refreshing it) asks the server for that path, so the
// server must answer with the app's index.html and let React take over.
@Hidden
@Controller
public class WebUiController {

    @GetMapping("/students")
    public String students() {
        return "forward:/index.html";
    }
}
