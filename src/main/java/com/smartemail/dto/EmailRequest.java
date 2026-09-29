package com.smartemail.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Request DTO for generating an AI-powered email reply.
 *
 * <p>Example JSON body:
 * <pre>
 * {
 *   "sender":           "hr@company.com",
 *   "subject":          "Interview Invitation",
 *   "emailContent":     "Hi, we'd like to invite you for an interview...",
 *   "responseApproach": "ACCEPT",
 *   "tone":             "professional",
 *   "customInstruction": "Mention I'm available Monday or Tuesday only."
 * }
 * </pre>
 *
 * <p>Supported responseApproach values: ACCEPT, DECLINE, NEUTRAL
 * <p>Supported tone values: professional, friendly, formal, casual, apologetic, assertive
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequest {

    /**
     * Sender's name or email address extracted from the email header.
     * Optional — improves personalization when provided.
     */
    private String sender;

    /**
     * Subject line of the original email.
     * Optional — provides topic context to the AI.
     */
    private String subject;

    /**
     * The full body text of the original email that needs an AI-generated reply.
     * Required.
     */
    private String emailContent;

    /**
     * Desired response approach: ACCEPT, DECLINE, or NEUTRAL.
     * Defaults to NEUTRAL if null/blank.
     */
    private String responseApproach;

    /**
     * The desired tone of the reply.
     * Examples: professional, friendly, formal, casual, apologetic, assertive.
     * Defaults to "professional" if null/blank.
     */
    private String tone;

    /**
     * Optional free-text instruction for the AI to follow when composing the reply.
     * Example: "Mention I'm available Monday or Tuesday only."
     */
    private String customInstruction;
}
