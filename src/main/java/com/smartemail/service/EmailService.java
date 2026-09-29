package com.smartemail.service;

import com.smartemail.dto.EmailRequest;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * Generates context-aware email replies via Gemini (Spring AI).
 * Optimised for fast responses: compact prompt, 600-token cap, body truncation.
 */
@Service
public class EmailService {

    private final ChatClient chatClient;

    /** Max chars of email body sent to AI — keeps the prompt short → faster */
    private static final int MAX_BODY_CHARS = 1500;

    /**
     * Compact prompt — fewer tokens = faster Gemini response.
     * All context is present; wall-of-text instructions removed.
     */
    private static final String PROMPT = """
            You are a professional email assistant. Write a reply to this email.

            From: {sender}
            Subject: {subject}
            Email:
            {emailContent}

            Reply approach: {responseApproach}
            (ACCEPT=agree; DECLINE=politely refuse; NEUTRAL=balanced)
            Tone: {tone}
            {extra}

            Rules: greeting → concise body → sign-off. No subject line. Reply text only.
            """;

    public EmailService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    /**
     * Generates an AI email reply.
     * Defaults: tone=professional, approach=NEUTRAL, sender/subject=Unknown if blank.
     *
     * @param req the enriched request payload
     * @return generated reply as plain text
     * @throws IllegalArgumentException if emailContent is missing
     */
    public String generateEmailReply(EmailRequest req) {
        if (!StringUtils.hasText(req.getEmailContent())) {
            throw new IllegalArgumentException("emailContent must not be blank.");
        }

        // Truncate long bodies so the prompt stays compact and fast
        String body = req.getEmailContent().trim();
        if (body.length() > MAX_BODY_CHARS) {
            body = body.substring(0, MAX_BODY_CHARS) + "\n[... truncated for brevity]";
        }

        String extra = StringUtils.hasText(req.getCustomInstruction())
                ? "Extra instruction: " + req.getCustomInstruction().trim()
                : "";

        Prompt prompt = new PromptTemplate(PROMPT).create(Map.of(
                "sender",           blank(req.getSender(),           "Unknown"),
                "subject",          blank(req.getSubject(),          "No Subject"),
                "emailContent",     body,
                "responseApproach", blank(req.getResponseApproach(), "NEUTRAL").toUpperCase(),
                "tone",             blank(req.getTone(),             "professional"),
                "extra",            extra
        ));

        return chatClient.prompt(prompt).call().content();
    }

    private String blank(String v, String def) {
        return StringUtils.hasText(v) ? v.trim() : def;
    }
}
