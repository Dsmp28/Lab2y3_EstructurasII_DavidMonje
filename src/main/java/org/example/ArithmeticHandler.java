package org.example;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class ArithmeticHandler {
    private Map<Character, Range> probabilities;

    private String text;

    private long underflowBits;


    private static final int default_high = 0xFFFF;

    private static final int default_low = 0;

    private static final int MSD = 0x8000;

    private static final int SSD = 0x4000;

    private int scale;


    public ArithmeticHandler(String text) {
        this.text = text;
        this.probabilities = new HashMap<>();
        calculateProbabilities();
    }

    private void calculateProbabilities() {
        Map<Character, Integer> frequencies = new TreeMap<>();

        for (char symbol : text.toCharArray()) {
            frequencies.put(symbol, frequencies.getOrDefault(symbol, 0) + 1);
        }

        List<Map.Entry<Character, Integer>> sortedFrequencies = frequencies.entrySet()
                .stream()
                .sorted((entry1, entry2) -> {
                    int comparison = Integer.compare(entry1.getValue(), entry2.getValue());
                    if (comparison != 0) {
                        return comparison;
                    }
                    return Character.compare(entry1.getKey(), entry2.getKey());
                })
                .toList();

        scale = text.length();

        int low = 0;

        for (Map.Entry<Character, Integer> entry : sortedFrequencies) {
            int high = low + entry.getValue();
            probabilities.put(entry.getKey(), new Range(low, high));
            low = high;
        }
    }

    private byte[] compress(String input) throws IOException {
        StringBuilder output = new StringBuilder();
        int low = default_low;
        int high = default_high;
        underflowBits = 0;
        long range;

        // Crear el buffer de salida
        BitWriter outputStream = new BitWriter();

        // Iterar por cada símbolo en el texto de entrada
        for (char symbol : input.toCharArray()) {
            range = (long)(high - low) + 1;

            // Actualizar high y low basados en el rango de probabilidad del símbolo
            high = (int) (low + ((range * probabilities.get(symbol).getHigh()) / scale) - 1);
            low = (int) (low + range * probabilities.get(symbol).getLow() / scale);

            // Normalizar el rango
            while (true) {
                if ((high & MSD) == (low & MSD)) {
                    // Escribir el bit más significativo (MSD)
                    outputStream.writeBit((high & MSD) != 0);
                    output.append((high & MSD) != 0 ? "1" : "0");
                    // Escribir los bits de underflow pendientes
                    while (underflowBits > 0) {
                        outputStream.writeBit((high & MSD) == 0);
                        output.append((high & MSD) == 0 ? "1" : "0");
                        underflowBits--;
                    }
                } else if ((low & SSD) != 0 && (high & SSD) == 0) {
                    // Ajustar los bits de underflow
                    underflowBits++;
                    low = (low ^ SSD) & 0xFFFF;
                    high = (high | SSD) & 0xFFFF;
                } else {
                    break;
                }

                // Shift para normalizar el rango
                low = (low<<1) & 0xFFFF;
                high = (high<<1) & 0xFFFF;
                high |= 1;
            }
        }

        // Escribir los bits finales
        outputStream.writeBit((low & SSD) != 0);
        output.append((low & SSD) != 0 ? "1" : "0");
        underflowBits++;
        while (underflowBits-- > 0) {
            outputStream.writeBit((low & SSD) == 0);
            output.append((low & SSD) == 0 ? "1" : "0");
        }

        outputStream.flush();
        return outputStream.getOutput();
    }

    public int getCompressedSize() {
        try {
            return compress(text).length;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }
}


