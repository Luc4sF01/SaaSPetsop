package com.petshop.agendamento.controller;

import com.petshop.agendamento.service.AgendamentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/telegram")
public class TelegramWebhookController {

    private final AgendamentoService agendamentoService;

    public TelegramWebhookController(AgendamentoService agendamentoService) {
        this.agendamentoService = agendamentoService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> receberUpdate(@RequestBody Map<String, Object> update) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> callbackQuery = (Map<String, Object>) update.get("callback_query");
            if (callbackQuery == null) return ResponseEntity.ok().build();

            String data = (String) callbackQuery.get("data");
            if (data == null || !data.contains(":")) return ResponseEntity.ok().build();

            String[] partes = data.split(":", 2);
            String acao = partes[0];
            Long id = Long.parseLong(partes[1]);

            if ("confirmar".equals(acao)) {
                agendamentoService.confirmarAgendamento(id);
            } else if ("cancelar".equals(acao)) {
                agendamentoService.cancelarAgendamento(id);
            }
        } catch (Exception e) {
            // Ignora updates inesperados ou malformados
        }

        return ResponseEntity.ok().build();
    }
}
