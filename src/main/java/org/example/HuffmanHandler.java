package org.example;

import java.util.PriorityQueue;
import java.util.HashMap;
import java.util.Map;

// Clase Nodo del árbol de Huffman
class Nodo implements Comparable<Nodo> {
    char simbolo;
    double probabilidad;
    Nodo izquierda;
    Nodo derecha;

    // Constructor para símbolo hoja
    public Nodo(char simbolo, double probabilidad) {
        this.simbolo = simbolo;
        this.probabilidad = probabilidad;
        this.izquierda = null;
        this.derecha = null;
    }

    // Constructor para nodo interno
    public Nodo(double probabilidad, Nodo izquierda, Nodo derecha) {
        this.simbolo = '\0'; // Nodo interno no tiene símbolo
        this.probabilidad = probabilidad;
        this.izquierda = izquierda;
        this.derecha = derecha;
    }

    // Implementar comparación para ordenar en la cola de prioridad
    @Override
    public int compareTo(Nodo o) {
        // Primero se compara por probabilidad
        if (this.probabilidad != o.probabilidad) {
            return Double.compare(this.probabilidad, o.probabilidad);
        }
        // Si la probabilidad es la misma, se compara por el valor del símbolo (orden natural)
        return Character.compare(this.simbolo, o.simbolo);
    }
}

public class HuffmanHandler {

    // Método para calcular frecuencias de cada carácter en el texto
    private Map<Character, Double> calcularFrecuencias(String texto) {
        Map<Character, Integer> conteo = new HashMap<>();
        int longitudTexto = texto.length();

        // Contar la cantidad de ocurrencias de cada carácter
        for (char c : texto.toCharArray()) {
            conteo.put(c, conteo.getOrDefault(c, 0) + 1);
        }

        // Convertir las frecuencias absolutas en probabilidades
        Map<Character, Double> frecuencias = new HashMap<>();
        for (Map.Entry<Character, Integer> entrada : conteo.entrySet()) {
            frecuencias.put(entrada.getKey(), entrada.getValue() / (double) longitudTexto);
        }

        return frecuencias;
    }

    // Método para generar el árbol de Huffman
    private Nodo construirArbol(Map<Character, Double> frecuencias) {
        PriorityQueue<Nodo> cola = new PriorityQueue<>();

        // Insertar los nodos hojas en la cola de prioridad
        for (Map.Entry<Character, Double> entrada : frecuencias.entrySet()) {
            cola.add(new Nodo(entrada.getKey(), entrada.getValue()));
        }

        // Construcción del árbol de Huffman
        while (cola.size() > 1) {
            Nodo izquierda = cola.poll();
            Nodo derecha = cola.poll();

            // Crear un nuevo nodo interno con la suma de las probabilidades
            Nodo nuevoNodo = new Nodo(izquierda.probabilidad + derecha.probabilidad, izquierda, derecha);

            cola.add(nuevoNodo);
        }

        // El último nodo en la cola es la raíz del árbol
        return cola.poll();
    }

    // Método para generar los códigos de Huffman
    private void generarCodigos(Nodo nodo, String codigo, Map<Character, String> codigos) {
        if (nodo == null) {
            return;
        }

        if (nodo.izquierda == null && nodo.derecha == null) {
            codigos.put(nodo.simbolo, codigo);
        }

        generarCodigos(nodo.izquierda, codigo + "0", codigos);
        generarCodigos(nodo.derecha, codigo + "1", codigos);
    }

    // Método para comprimir una cadena de texto
    private String comprimir(String texto) {
        // Calcular las frecuencias a partir del texto
        Map<Character, Double> frecuencias = calcularFrecuencias(texto);

        // Construir el árbol de Huffman
        Nodo raiz = construirArbol(frecuencias);

        // Generar los códigos de Huffman para cada símbolo
        Map<Character, String> codigos = new HashMap<>();
        generarCodigos(raiz, "", codigos);

        // Comprimir el texto utilizando los códigos generados
        StringBuilder comprimido = new StringBuilder();
        for (char c : texto.toCharArray()) {
            comprimido.append(codigos.get(c));
        }

        return comprimido.toString();
    }

    public int getCompressedSize(String texto) {
        return comprimir(texto).length();
    }
}


