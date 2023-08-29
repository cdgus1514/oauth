package dh.example.oauth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class OAuthController {

    @GetMapping("/login")
    public String ouathLoginInfo() {
        return "login";
    }
}
