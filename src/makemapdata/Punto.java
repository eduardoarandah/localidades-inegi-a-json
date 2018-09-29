/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package makemapdata;

import static java.lang.Double.max;
import static java.lang.Double.min;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author lalo
 */
public class Punto {

    private String label;
    private Double lat;
    private Double lng;
    private Map<String, Punto> hijos;

    public Punto(String label) {
        this.label = label;
        this.hijos = new HashMap<String, Punto>();
    }

    public Punto(String label, Double lat, Double lng) {
        this.label = label;
        this.lat = lat;
        this.lng = lng;
        this.hijos = new HashMap<String, Punto>();
    }

    public Punto agregarHijoSiNoExiste(String label) {
        if (this.getHijos().containsKey(label)) {
            //System.err.println("Ya existe: " + label);
            return this.getHijos().get(label);
        } else {
            //System.err.println("Creando: " + label);
            Punto hijo = new Punto(label);
            this.getHijos().put(label, hijo);
            return hijo;
        }
    }

    public Punto agregarHijoSiNoExiste(String label, Double lat, Double lng) {
        if (this.getHijos().containsKey(label)) {
            //System.err.println("Ya existe: " + label);
            return this.getHijos().get(label);
        } else {
            //System.err.println("Creando: " + label);
            Punto hijo = new Punto(label, lat, lng);
            this.getHijos().put(label, hijo);
            return hijo;
        }
    }

    public int numeroDeHijos() {
        return this.getHijos().size();
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return the lat
     */
    public Double getLat() {
        return lat;
    }

    /**
     * @param lat the lat to set
     */
    public void setLat(Double lat) {
        this.lat = lat;
    }

    /**
     * @return the lng
     */
    public Double getLng() {
        return lng;
    }

    /**
     * @param lng the lng to set
     */
    public void setLng(Double lng) {
        this.lng = lng;
    }

    /**
     * @return the hijos
     */
    public Map<String, Punto> getHijos() {
        return hijos;
    }

    /**
     * @param hijos the hijos to set
     */
    public void setHijos(Map<String, Punto> hijos) {
        this.hijos = hijos;
    }

    public String getFileName() {
        return this.label.toLowerCase()
                .replace("á", "a")
                .replace("é", "e")
                .replace("í", "i")
                .replace("ñ", "n")
                .replace("ó", "o")
                .replace("ú", "u")
                .replaceAll("\\W+", "-");
    }

    public void calcularLatLngDesdeHijos() {
        if (this.hijos.size() > 0) {
            Double latSum = 0.0;
            Double lngSum = 0.0;

            for (Map.Entry<String, Punto> Punto : this.hijos.entrySet()) {
                String key = Punto.getKey();
                Punto value = Punto.getValue();

                latSum += value.lat;
                lngSum += value.lng;
                
            }
            
            this.lat = latSum / this.hijos.size();
            this.lng = lngSum / this.hijos.size();
        }
    }
}
