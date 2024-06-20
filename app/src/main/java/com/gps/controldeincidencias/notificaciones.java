package com.gps.controldeincidencias;

//import com.google.firebase.firestore.GeoPoint;

import com.google.firebase.firestore.GeoPoint;

import java.sql.Timestamp;
import java.util.Date;

public class notificaciones {
    private String nombre;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    private Date fecha;

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }


    private GeoPoint localizacion;

    public GeoPoint getLocalizacion() {
        return localizacion;
    }

    public void setLocalizacion(GeoPoint localizacion) {
        this.localizacion = localizacion;
    }

}
