package cog.com.sic.frontend.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@ControllerAdvice
public class GlobalController {

    @ModelAttribute("DataHoraAtual")
    public String getCurrentDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE · MMMM d, yyyy · h:mm a", Locale.forLanguageTag("pt-BR"));
        String formattedDate = LocalDateTime.now().format(formatter);
        return capitalizeFirstLetters(formattedDate);
    }

    private String capitalizeFirstLetters(String str) {
        String[] words = str.split(" ");
        StringBuilder capitalizedWords = new StringBuilder();
        for (String word : words) {
            if (word.length() > 1) {
                capitalizedWords.append(Character.toUpperCase(word.charAt(0)))
                                .append(word.substring(1).toLowerCase());
            } else {
                capitalizedWords.append(word.toUpperCase());
            }
            capitalizedWords.append(" ");
        }
        return capitalizedWords.toString().trim();
    }
}
