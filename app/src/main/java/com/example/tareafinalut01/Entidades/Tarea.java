package com.example.tareafinalut01.Entidades;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Entity(tableName = "tareas")
public class Tarea implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private int id;

    @NonNull
    @ColumnInfo(name = "titulo")
    private String titulo;

    @ColumnInfo(name = "descripcion")
    private String descripcion;

    @ColumnInfo(name = "progreso", defaultValue = "0")
    private int progreso = 0;

    @NonNull
    @ColumnInfo(name = "fecha_creacion", defaultValue = "CURRENT_DATE")
    private LocalDate fechaCreacion = LocalDate.now();

    @NonNull
    @ColumnInfo(name = "fecha_objetivo", defaultValue = "CURRENT_DATE")
    private LocalDate fechaLimite = LocalDate.now();

    @ColumnInfo(name = "prioritaria", defaultValue = "0")
    private boolean prioritaria = false;

    @ColumnInfo(name = "URL_doc")
    private String uriDocumento;

    @ColumnInfo(name = "URL_img")
    private String uriImagen;

    @ColumnInfo(name = "URL_aud")
    private String uriAudio;

    @ColumnInfo(name = "URL_vid")
    private String uriVideo;

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
        id = in.readInt();
        titulo = in.readString();
        descripcion = in.readString();
        progreso = in.readInt();
        fechaCreacion = (LocalDate) in.readSerializable();
        fechaLimite = (LocalDate) in.readSerializable();
        prioritaria = in.readByte() != 0;
        uriDocumento = in.readString();
        uriImagen = in.readString();
        uriAudio = in.readString();
        uriVideo = in.readString();
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

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUriDocumento() { return uriDocumento; }
    public void setUriDocumento(String uriDocumento) { this.uriDocumento = uriDocumento; }

    public String getUriImagen() { return uriImagen; }
    public void setUriImagen(String uriImagen) { this.uriImagen = uriImagen; }

    public String getUriAudio() { return uriAudio; }
    public void setUriAudio(String uriAudio) { this.uriAudio = uriAudio; }

    public String getUriVideo() { return uriVideo; }
    public void setUriVideo(String uriVideo) { this.uriVideo = uriVideo; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getProgreso() { return progreso; }
    public void setProgreso(int progreso) { this.progreso = progreso; }

    public LocalDate getFechaLimite() { return fechaLimite; }
    public void setFechaLimite(LocalDate fechaLimite) { this.fechaLimite = fechaLimite; }

    public int getDiasRestantes() {
        LocalDate fechaActual = LocalDate.now();
        LocalDate fechaLimite = getFechaLimite();
        if (fechaLimite == null) return 0;
        long dias = ChronoUnit.DAYS.between(fechaActual, fechaLimite);
        return (int) dias;
    }

    public boolean isPrioritaria() { return prioritaria; }
    public void setPrioritaria(boolean prioritaria) { this.prioritaria = prioritaria; }

    public LocalDate getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDate fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(titulo);
        dest.writeString(descripcion);
        dest.writeInt(progreso);
        dest.writeSerializable(fechaCreacion);
        dest.writeSerializable(fechaLimite);
        dest.writeByte((byte) (prioritaria ? 1 : 0));
        dest.writeString(uriDocumento);
        dest.writeString(uriImagen);
        dest.writeString(uriAudio);
        dest.writeString(uriVideo);
    }
    @TypeConverter
    public static LocalDate fromString(String value) {
        return value == null ? null : LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    @TypeConverter
    public static String dateToString(LocalDate date) {
        return date == null ? null : date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
}
