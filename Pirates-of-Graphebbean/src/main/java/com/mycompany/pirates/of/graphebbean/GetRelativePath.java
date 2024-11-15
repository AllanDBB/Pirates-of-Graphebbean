/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.pirates.of.graphebbean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

/**
 * Clase para manejar rutas relativas y listar imágenes en carpetas específicas.
 * 
 * @author Usuario 2024
 */
public class GetRelativePath {
    private final File archivo = new File("src/main/java/com/mycommpany/pirates/of/graphebbean");
    private final File carpetaRecursos = new File("src/main/java/com/mycompany/pirates/of/graphebbean");

    /**
     * Obtiene la ruta absoluta de la carpeta principal del proyecto.
     *
     * @return Ruta absoluta como String.
     */
    public String getJavaPath() {
        return new File("src/main/java/com/mycompany/pirates/of/graphebbean").getAbsolutePath();
    }

    /**
     * Obtiene la ruta absoluta de la carpeta "Recursos".
     *
     * @return Ruta absoluta como String.
     */
    public String getResourcePath() {
        System.out.println(archivo.getAbsolutePath());
        return archivo.getAbsolutePath();
    }

    /**
     * Lista las imágenes (.png, .jpg, .jpeg) dentro de la carpeta "ImagenesBuscarPareja".
     *
     * @return Lista de archivos de imagen encontrados.
     */
    public List<File> getImagenesEnCarpeta() {
        List<File> imagenes = new ArrayList<>();

        if (carpetaRecursos.isDirectory()) {
            File[] archivos = carpetaRecursos.listFiles((dir, nombre) ->
                    nombre.toLowerCase().endsWith(".png") ||
                    nombre.toLowerCase().endsWith(".jpg") ||
                    nombre.toLowerCase().endsWith(".jpeg"));
            if (archivos != null) {
                for (File archivo : archivos) {
                    imagenes.add(archivo);
                }
            }
        }
        return imagenes;
    }

    /**
     * Lista las imágenes (.png, .jpg, .jpeg) dentro de una carpeta específica.
     *
     * @param nombreCarpeta Nombre de la carpeta relativa a "src/main/java".
     * @return Lista de archivos de imagen encontrados.
     */
    public List<File> getImagenesEnCarpeta(String nombreCarpeta) {
        List<File> imagenes = new ArrayList<>();
        File carpeta = new File("src/main/java/" + nombreCarpeta);

        if (carpeta.isDirectory()) {
            File[] archivos = carpeta.listFiles((dir, nombre) ->
                    nombre.toLowerCase().endsWith(".png") ||
                    nombre.toLowerCase().endsWith(".jpg") ||
                    nombre.toLowerCase().endsWith(".jpeg"));
            if (archivos != null) {
                for (File archivo : archivos) {
                    imagenes.add(archivo);
                }
            }
        }
        return imagenes;
    }
    public ImageIcon buscarImagenComoIcono(String nombreImagen) {
        File carpeta = new File("src/main/java/com/mycompany/pirates/of/graphebbean");
        
        if (carpeta.isDirectory()) {
            File[] archivos = carpeta.listFiles((dir, nombre) -> nombre.equalsIgnoreCase(nombreImagen));
            if (archivos != null && archivos.length > 0) {
                File imagen = archivos[0]; // Toma la primera coincidencia
                System.out.println("encontrada imagen");
                return new ImageIcon(imagen.getAbsolutePath());
            }
        }
        return null; // Si no encuentra la imagen
    }
}
