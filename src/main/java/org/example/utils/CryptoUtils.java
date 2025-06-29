package org.example.utils;

public class CryptoUtils {
    public static String criptare(String text, int cheie) {
        StringBuilder rezultat = new StringBuilder();

        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                char baza = Character.isUpperCase(c) ? 'A' : 'a';
                rezultat.append((char) ((c - baza + cheie) % 26 + baza));
            } else {
                rezultat.append(c);
            }
        }

        return rezultat.toString();
    }

    public static String decriptare(String text, int cheie) {
        return criptare(text, 26 - (cheie % 26));
    }
}

