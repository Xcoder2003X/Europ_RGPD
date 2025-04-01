package com.example.pfa_uplaod.service;
import com.google.privacy.differentialprivacy.BoundedSum;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DifferentialPrivacyService {

    private final double epsilon = 1.0; // Niveau de confidentialité
    private final int maxPartitionsContributed = 1;

    /**
     * Anonymiser une somme (ex: nombre total de documents conformes)
     */
    public double applyDifferentialPrivacySum(List<Integer> values) {
        BoundedSum dpSum = BoundedSum.builder()
                .epsilon(epsilon)
                .maxPartitionsContributed(maxPartitionsContributed)
                .lower(0)
                .upper(100)
                .build();

        values.forEach(dpSum::addEntry);
        return dpSum.computeResult();
    }

    /**
     * Compter anonymement les occurrences d'un mot-clé (ex: "consentement" dans un texte)
     */
    public double applyDifferentialPrivacyCount(int count) {
        BoundedSum dpCount = BoundedSum.builder()
                .epsilon(epsilon)
                .maxPartitionsContributed(maxPartitionsContributed)
                .lower(0)  // Un mot-clé est compté comme 1
                .upper(1)  // Maximum de 1 par occurrence
                .build();

        for (int i = 0; i < count; i++) {
            dpCount.addEntry(1); // Ajouter un bruit sur chaque occurrence
        }
        return dpCount.computeResult();
    }
}
