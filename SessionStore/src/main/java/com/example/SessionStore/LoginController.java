package com.example.SessionStore;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.HashMap;

@RestController
public class LoginController {

    // DB 대신하는 역할
    HashMap<String, String> sessionMap = new HashMap<>();

    // 로그인
    @GetMapping("/login")
    public String login(HttpSession session, @RequestParam String name){
        sessionMap.put(session.getId(),name);
        return "save";
    }

    // 세션 저장 되어 있는 것을 확인
    @GetMapping("/myName")
    public String myName(HttpSession session){
        String myName = sessionMap.get(session.getId());
        return myName;
    }
}
