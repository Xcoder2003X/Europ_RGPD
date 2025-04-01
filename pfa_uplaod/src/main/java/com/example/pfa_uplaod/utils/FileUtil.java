package com.example.pfa_uplaod.utils;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public class FileUtil {

    private static final Tika tika = new Tika();

    public static String extractText(MultipartFile file) throws IOException {
        try {
            return tika.parseToString(file.getInputStream());
        } catch (TikaException e) {
            throw new IOException("Erreur d'extraction du texte", e);
        }
    }
}

