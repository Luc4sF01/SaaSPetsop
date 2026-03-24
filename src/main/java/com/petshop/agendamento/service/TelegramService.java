package com.petshop.agendamento.service;

import com.petshop.agendamento.model.Agendamento;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TelegramService {

    private static final String API_URL = "https://api.telegram.org/bot%s/sendMessage";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");

    @Value("${telegram.bot.token}")
    private String token;

    @Value("${telegram.bot.chatId}")
    private String chatId;

    private final RestTemplate restTemplate = new RestTemplate();

    public void notificarNovoAgendamento(Agendamento a) {
        String texto = String.format(
            "*Novo Agendamento* 🐾\n\n" +
            "*Cliente:* %s\n" +
            "*Telefone:* %s\n" +
            "*Serviço:* %s\n" +
            "*Porte:* %s\n" +
            "*Horário:* %s",
            escapar(a.getNomeCliente()),
            escapar(a.getTelefone()),
            escapar(a.getServico().toString()),
            escapar(a.getPorte().toString()),
            a.getHorario().format(FORMATTER)
        );

        Map<String, Object> confirmar = new HashMap<>();
        confirmar.put("text", "✅ Confirmar");
        confirmar.put("callback_data", "confirmar:" + a.getId());

        Map<String, Object> cancelar = new HashMap<>();
        cancelar.put("text", "❌ Cancelar");
        cancelar.put("callback_data", "cancelar:" + a.getId());

        Map<String, Object> replyMarkup = new HashMap<>();
        replyMarkup.put("inline_keyboard", List.of(List.of(confirmar, cancelar)));

        Map<String, Object> body = new HashMap<>();
        body.put("chat_id", chatId);
        body.put("text", texto);
        body.put("parse_mode", "Markdown");
        body.put("reply_markup", replyMarkup);

        String url = String.format(API_URL, token);
        restTemplate.postForObject(url, body, Map.class);
    }

    private String escapar(String texto) {
        if (texto == null) return "";
        return texto.replace("_", "\\_").replace("*", "\\*").replace("[", "\\[").replace("`", "\\`");
    }
}
