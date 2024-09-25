package org.example;

import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws CsvValidationException, IOException {
        Menu menu = new Menu();
        menu.showMenu();
    }
}