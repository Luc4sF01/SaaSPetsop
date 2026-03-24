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

    private static final String ANSWER_URL  = "https://api.telegram.org/bot%s/answerCallbackQuery";
    private static final String MESSAGE_URL = "https://api.telegram.org/bot%s/sendMessage";

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

            @SuppressWarnings("unchecked")
            Map<String, Object> message = (Map<String, Object>) callbackQuery.get("message");
            @SuppressWarnings("unchecked")
            Map<String, Object> chat = (Map<String, Object>) message.get("chat");
            Object chatId = chat.get("id");

            String[] partes = data.split(":", 2);
            String acao = partes[0];
            Long id = Long.parseLong(partes[1]);

            String textoMensagem = null;
            if ("confirmar".equals(acao)) {
                agendamentoService.confirmarAgendamento(id);
                textoMensagem = "✅ *Agendamento #" + id + " confirmado!*\nO cliente será atendido no horário marcado.";
            } else if ("cancelar".equals(acao)) {
                agendamentoService.cancelarAgendamento(id);
                textoMensagem = "❌ *Agendamento #" + id + " cancelado.*\nO horário foi liberado.";
            }

            if (textoMensagem != null) {
                responderCallback(callbackQueryId);
                enviarMensagem(chatId, textoMensagem);
            }
        } catch (Exception e) {
            // Ignora updates inesperados ou malformados
        }

        return ResponseEntity.ok().build();
    }

    private void responderCallback(String callbackQueryId) {
        Map<String, Object> body = new HashMap<>();
        body.put("callback_query_id", callbackQueryId);
        restTemplate.postForObject(String.format(ANSWER_URL, token), body, Map.class);
    }

    private void enviarMensagem(Object chatId, String texto) {
        Map<String, Object> body = new HashMap<>();
        body.put("chat_id", chatId);
        body.put("text", texto);
        body.put("parse_mode", "Markdown");
        restTemplate.postForObject(String.format(MESSAGE_URL, token), body, Map.class);
    }
}
