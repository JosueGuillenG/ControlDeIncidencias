package com.gps.controldeincidencias;

import com.google.firebase.firestore.GeoPoint;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class reporte {

    private String nombre;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    private String Descripcion;

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    private Date fecha;

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    private List<String> notificaciones;

    private Map<String,Boolean> Tipos;


    public List<String> getNotificaciones() {
        return notificaciones;
    }

    public void setNotificaciones(List<String> notificaciones) {
        this.notificaciones = notificaciones;
    }

    private GeoPoint localizacion;

    public GeoPoint getLocalizacion() {
        return localizacion;
    }

    public void setLocalizacion(GeoPoint localizacion) {
        this.localizacion = localizacion;
    }

    public Map getTipos() {
        return Tipos;
    }

    public void setTipos(Map tipos) {
        Tipos = tipos;
    }
}
