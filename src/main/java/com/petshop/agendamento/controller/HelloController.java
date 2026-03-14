package com.petshop.agendamento.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class HelloController {
    @GetMapping("/")
    public String home(){
        return "Sistema de agendamento no ar";
    }

    @GetMapping("/status")
    public String status (){
        return "Servidor funcionando normal";
    }
}
