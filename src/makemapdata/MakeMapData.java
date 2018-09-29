/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package makemapdata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lalo
 */
public class MakeMapData {

    public static void main(String[] args) throws IOException {

        //país es un nodo con hijos, cuyos hijos (estados) tienen hijos (municipios) que tienen hijos (localidades)
        Punto pais = ExtraerNodos("data.csv");

        System.out.println("Árbol de nodos extraído");

        System.out.println("Calculando latitudes y longitudes faltantes país a partir de estado, estado a partir de municipio y municipio a partir de localidad");
        for (Map.Entry<String, Punto> estado : pais.getHijos().entrySet()) {
            Object key = estado.getKey();
            Punto estadoObj = estado.getValue();

            for (Map.Entry<String, Punto> municipio : estadoObj.getHijos().entrySet()) {
                String key1 = municipio.getKey();
                Punto municipioObj = municipio.getValue();

                //calcular desde sus hijos
                municipioObj.calcularLatLngDesdeHijos();

            }

            //calcular desde sus hijos
            estadoObj.calcularLatLngDesdeHijos();

        }
        pais.calcularLatLngDesdeHijos();

        //imprime detalle recursivamente
        ImprimirDetalle(pais, "");

        //Generar archivos json
        GenerarJson(pais, new File("data"));
    }

    public static void ImprimirDetalle(Punto punto, String prefijo) {
        int numeroDeHijos = punto.numeroDeHijos();

        if (numeroDeHijos == 0) {
            return; //SALIDA
        } else {
            System.out.println(prefijo + punto.getLabel() + " hijos: " + numeroDeHijos);

            /*
            Imprimir detalle de cada hijo            
             */
            for (Map.Entry<String, Punto> hijo : punto.getHijos().entrySet()) {
                String key = hijo.getKey();
                Punto value = hijo.getValue();

                //NOTA: DESCOMENTAR ESTA LÍNEA PARA VER EL DETALLE DE CADA HIJO
                //System.out.println(prefijo + key);
                ImprimirDetalle(value, prefijo + " - ");
            }

        }

    }

    public static Punto ExtraerNodos(String pathArchivo) {
        Punto pais = new Punto("Mexico");

        FileReader fileReader = null;
        try {

            //abrir archivo
            fileReader = new FileReader(pathArchivo);

            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String s;

            /*
            
             */
            boolean primerLinea = true;
            while ((s = bufferedReader.readLine()) != null) {

                //evitar la primera 
                if (!primerLinea) {
                    //System.out.println("Procesando: " + s);

                    String[] partes = s.split("\",\"");
                    if (partes.length > 3) {
                        //extraer datos
                        String partes_estado = partes[1].replace("\"", "");
                        String partes_municipio = partes[4].replace("\"", "");
                        String partes_localidad = partes[6].replace("\"", "");
                        Double partes_latitud = Double.parseDouble(partes[8].replace("\"", ""));
                        Double partes_longitud = Double.parseDouble(partes[9].replace("\"", ""));

                        //agregar puntos                    
                        Punto estado = pais.agregarHijoSiNoExiste(partes_estado);
                        Punto municipio = estado.agregarHijoSiNoExiste(partes_municipio);
                        Punto localidad = municipio.agregarHijoSiNoExiste(partes_localidad, partes_latitud, partes_longitud);
                    }

                }
                primerLinea = false;

            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(MakeMapData.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MakeMapData.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fileReader.close();
            } catch (IOException ex) {
                Logger.getLogger(MakeMapData.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return pais;

    }

    private static void GenerarJson(Punto punto, File directorio) {

        //System.out.println("Procesar " + punto.getLabel());
        String fileName = punto.getFileName();
        String jsonPath = directorio.getPath() + File.separator + fileName + ".json";

        //si no existe dir
        if (!directorio.exists()) {
            //System.out.println("Crear " + directorio.getPath());
            directorio.mkdir();
        }

        try {
            //crear el archivo json
            PrintWriter printWriter = new PrintWriter(new File(jsonPath));
            printWriter.print("[");

            boolean hayLineasAntes = false;
            //iteración por puntos
            for (Map.Entry<String, Punto> hijo : punto.getHijos().entrySet()) {
                String key = hijo.getKey();
                Punto value = hijo.getValue();

                String linea = "";
                if (hayLineasAntes) {
                    linea += ",";
                }

                linea += String.format("{\n\"label\": \"%s\", \"lat\": %s, \"lng\": %s",
                        value.getLabel(),
                        value.getLat(),
                        value.getLng()
                );

                //ejecutar recursivo si tiene hijos
                if (value.numeroDeHijos() > 0) {
                    String subdir = directorio.getPath() + File.separator + fileName;
                    File subDirectorio = new File(subdir);
                    GenerarJson(value, subDirectorio);
                    
                    linea+=",\"info\": ";
                    linea+="\"Total: "+value.numeroDeHijos()+" <br><a target='_blank' href=\\\"/"+subdir.replace("\\", "/")+"/"+value.getFileName()+".json\\\">Descargar JSON</a>\"";
                    linea+=",\"children\": \""+subdir.replace("\\", "/")+"/"+value.getFileName()+".json\"";                    
                }

                printWriter.print(linea + "\n}");

                hayLineasAntes = true;

            }
            printWriter.print("]");

            printWriter.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MakeMapData.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
