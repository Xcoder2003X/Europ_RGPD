package com.example.pfa_uplaod.service;
import org.springframework.stereotype.Service;

@Service
public class AnonymizationService {

    // Regex pour détecter les emails, numéros de téléphone et noms simples
    private static final String EMAIL_REGEX = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
    private static final String PHONE_REGEX = "\\b\\d{10}\\b";
    //private static final String NAME_REGEX = "\\b([A-Z][a-z]+)\\s([A-Z][a-z]+)\\b"; // Ex: Jean Dupont

    // Méthode pour anonymiser le texte
    public String anonymizeText(String text) {
        text = text.replaceAll(EMAIL_REGEX, "[ANONYMIZED_EMAIL]");
        text = text.replaceAll(PHONE_REGEX, "[ANONYMIZED_PHONE]");
        //text = text.replaceAll(NAME_REGEX, "[ANONYMIZED_NAME]");
        return text;
    }
}

