package com.petshop.agendamento.controller;

import com.petshop.agendamento.service.AgendamentoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/telegram")
public class TelegramWebhookController {

    private static final String ANSWER_URL = "https://api.telegram.org/bot%s/answerCallbackQuery";

    @Value("${telegram.bot.token}")
    private String token;

    private final AgendamentoService agendamentoService;
    private final RestTemplate restTemplate = new RestTemplate();

    public TelegramWebhookController(AgendamentoService agendamentoService) {
        this.agendamentoService = agendamentoService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> receberUpdate(@RequestBody Map<String, Object> update) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> callbackQuery = (Map<String, Object>) update.get("callback_query");
            if (callbackQuery == null) return ResponseEntity.ok().build();

            String callbackQueryId = (String) callbackQuery.get("id");
            String data = (String) callbackQuery.get("data");
            if (data == null || !data.contains(":")) return ResponseEntity.ok().build();

            String[] partes = data.split(":", 2);
            String acao = partes[0];
            Long id = Long.parseLong(partes[1]);

            String textoResposta = null;
            if ("confirmar".equals(acao)) {
                agendamentoService.confirmarAgendamento(id);
                textoResposta = "✅ Confirmado!";
            } else if ("cancelar".equals(acao)) {
                agendamentoService.cancelarAgendamento(id);
                textoResposta = "❌ Cancelado!";
            }

            if (textoResposta != null) {
                responderCallback(callbackQueryId, textoResposta);
            }
        } catch (Exception e) {
            // Ignora updates inesperados ou malformados
        }

        return ResponseEntity.ok().build();
    }

    private void responderCallback(String callbackQueryId, String texto) {
        Map<String, Object> body = new HashMap<>();
        body.put("callback_query_id", callbackQueryId);
        body.put("text", texto);

        String url = String.format(ANSWER_URL, token);
        restTemplate.postForObject(url, body, Map.class);
    }
}
