package com.example.pfa_uplaod.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * @Configuration
Cette annotation indique à Spring que cette classe contient des définitions de beans. 
Spring la lira au démarrage pour instancier et configurer les objets qu’elle fournit.
 */

@Configuration
public class AppConfig {

    /**La méthode annotée @Bean renvoie une instance de RestTemplate que Spring 
     * enregistre dans son contexte. Partout où vous aurez besoin d’un RestTemplate, vous 
     * pourrez l’injecter via @Autowired ou en le déclarant comme paramètre d’un 
     * constructeur.*/

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        /**Spring Boot met à disposition un RestTemplateBuilder : un « builder » 
         * (construteur fluide) qui facilite la personnalisation de la configuration (timeouts, interceptors, message converters, etc.). */
        return builder
                .connectTimeout(Duration.ofSeconds(5))  // Nouvelle méthode
                .readTimeout(Duration.ofSeconds(30))    // Nouvelle méthode
                .build();
    }
}
