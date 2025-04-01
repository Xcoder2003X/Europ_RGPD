package com.example.pfa_uplaod.service;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ConsentVerificationService {

    // Liste des mots-clés liés au consentement
    private static final List<String> CONSENT_KEYWORDS = List.of(
            "consentement explicite", "autorisation écrite", "accord signé", "RGPD", "protection des données"
    );

    // Méthode pour vérifier si le texte contient un consentement
    public boolean hasConsent(String text) {
        return CONSENT_KEYWORDS.stream().anyMatch(text.toLowerCase()::contains);
    }
}

