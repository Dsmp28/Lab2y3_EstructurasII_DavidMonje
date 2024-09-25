package org.example;

import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.util.Scanner;

public class Menu {
    private boolean continuar; // Variable para controlar la ejecución del menú
    private final ManejadorLibros manejador; // Instancia de la clase ManejadorLibros

    // Constructor de la clase Menu
    public Menu() {
        continuar = true;
        manejador = new ManejadorLibros();
    }

    // Método para mostrar el menú de opciones
    public void showMenu() throws CsvValidationException, IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Bienvenido al sistema de compresión de libros\n");
        while (continuar) {
            try{
                System.out.println("Menú de opciones:");
                System.out.println("1. importar archivo");
                System.out.println("2. salir");
                int opcion = scanner.nextInt();
                switch (opcion) {
                    case 1:
                        showPedirArchivo();
                        break;
                    case 2:
                        continuar = false;
                        break;
                    default:
                        System.out.println("Opción inválida, intente de nuevo");
                }
            } catch (Exception e) {
                System.out.println("Seleccione una opcion valida. Por ejemplo: '1'\n");
                scanner.next();
            }
        }
        System.out.println("Gracias por usar el sistema de compresión de libros");
    }

    // Método para solicitar la ruta del archivo a importar
    private void showPedirArchivo() throws IOException, CsvValidationException {
        boolean valido = false;
        Scanner scanner = new Scanner(System.in);
        String ruta = "";
        while (!valido) {
            try {
                System.out.println("Ingrese la ruta absoluta del archivo: ");
                ruta = scanner.nextLine();
                valido = true;
            } catch (Exception e) {
                System.out.println("Ingrese una ruta válida");
            }
        }
        manejador.processFile(ruta);
    }
}
