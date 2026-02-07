package com.example.tareafinalut01;

import android.os.Parcel;
import android.os.Parcelable;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Tarea implements Parcelable {

    private String titulo;
    private String descripcion;
    private int progreso;
    private LocalDate fechaCreacion;
    private LocalDate fechaLimite;
    private boolean prioritaria;

    public Tarea() {
    }

    public Tarea(String titulo, String descripcion, int progreso, LocalDate fechaCreacion, LocalDate fechaLimite, boolean prioritaria) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.progreso = progreso;
        this.fechaCreacion = fechaCreacion;
        this.fechaLimite = fechaLimite;
        this.prioritaria = prioritaria;
    }

    public Tarea(String titulo, int progreso, LocalDate fechaCreacion, LocalDate fechaLimite, boolean prioritaria) {
        this.titulo = titulo;
        this.progreso = progreso;
        this.fechaCreacion = fechaCreacion;
        this.fechaLimite = fechaLimite;
        this.prioritaria = prioritaria;
    }

    public Tarea(String titulo, String descripcion, int progreso, LocalDate fechaLimite, boolean prioritaria) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.progreso = progreso;
        this.fechaCreacion = LocalDate.now();
        this.fechaLimite = fechaLimite;
        this.prioritaria = prioritaria;
    }

    protected Tarea(Parcel in) {
        titulo = in.readString();
        descripcion = in.readString();
        progreso = in.readInt();
        fechaCreacion = (LocalDate) in.readSerializable();
        fechaLimite = (LocalDate) in.readSerializable();
        prioritaria = in.readByte() != 0;
    }

    public static final Creator<Tarea> CREATOR = new Creator<Tarea>() {
        @Override
        public Tarea createFromParcel(Parcel in) {
            return new Tarea(in);
        }

        @Override
        public Tarea[] newArray(int size) {
            return new Tarea[size];
        }
    };

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getProgreso() {
        return progreso;
    }

    public void setProgreso(int progreso) {
        this.progreso = progreso;
    }

    public LocalDate getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(LocalDate fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public int getDiasRestantes() {
        LocalDate fechaActual = LocalDate.now();
        LocalDate fechaLimite = getFechaLimite();
        long dias = ChronoUnit.DAYS.between(fechaActual, fechaLimite);
        return (int) dias;
    }

    public boolean isPrioritaria() {
        return prioritaria;
    }

    public void setPrioritaria(boolean prioritaria) {
        this.prioritaria = prioritaria;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(titulo);
        dest.writeString(descripcion);
        dest.writeInt(progreso);
        dest.writeSerializable(fechaCreacion);
        dest.writeSerializable(fechaLimite);
        dest.writeByte((byte) (prioritaria ? 1 : 0));
    }
}
