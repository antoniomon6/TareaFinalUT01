package com.example.tareafinalut01;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TareaAdapter extends RecyclerView.Adapter<TareaAdapter.ViewHolderTarea>{
    private final ArrayList<Tarea> misTareasAdapt;
    public Tarea tareaSeleccionada;
    public TareaAdapter(ArrayList<Tarea> misTareasAdapt) {
        this.misTareasAdapt = misTareasAdapt;
    }

    @NonNull
    @Override
    public ViewHolderTarea onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.tarea_view, parent, false);
        return  new ViewHolderTarea(item);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderTarea holder, int position) {
        holder.bindTarea(misTareasAdapt.get(position));
    }

    @Override
    public int getItemCount() {
        return misTareasAdapt.size();
    }

    /// //////////////// Clase interna ViewHolder ////////////////////////
    protected class ViewHolderTarea extends RecyclerView.ViewHolder {
        private TextView titulo;
        private ProgressBar progreso;
        private TextView fechaLimite;
        private TextView diasRestantes;
        public ViewHolderTarea(@NonNull View itemView) {
            super(itemView);
            /// bindings
            titulo = itemView.findViewById(R.id.txtTitulo);
            progreso = itemView.findViewById(R.id.pgbProgreso);
            fechaLimite = itemView.findViewById(R.id.txtFechaObjetivo);
            diasRestantes = itemView.findViewById(R.id.txtDiasRestantes);
            itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    MenuInflater inflater = new MenuInflater(v.getContext());
                    inflater.inflate(R.menu.menu_tarea,menu);
                    tareaSeleccionada = misTareasAdapt.get(getAdapterPosition());
                }
            });
        }


        public void bindTarea(Tarea tarea) {
            titulo.setText(tarea.getTitulo());
            progreso.setProgress(tarea.getProgreso());
            fechaLimite.setText(tarea.getFechaLimite().toString().replace('-','/'));
            diasRestantes.setText(String.valueOf(tarea.getDiasRestantes()));
            if (tarea.getProgreso() == 100) {
                titulo.setPaintFlags(titulo.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                diasRestantes.setText("0 días");
            } else {
                titulo.setPaintFlags(0);
                if (tarea.getDiasRestantes()<0){
                    diasRestantes.setTextColor(ColorStateList.valueOf(Color.YELLOW));
                }
            }

            if(tarea.isPrioritaria()){
                titulo.setCompoundDrawableTintList(ColorStateList.valueOf(Color.YELLOW));
                titulo.setTypeface(null, Typeface.BOLD);
            }else{
                titulo.setCompoundDrawableTintList(ColorStateList.valueOf(Color.BLACK));
                titulo.setTypeface(null, Typeface.NORMAL);
            }
            itemView.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                builder.setTitle(R.string.descripcion_tarea);
                builder.setMessage(tarea.getDescripcion());
                builder.setPositiveButton(R.string.aceptar, null);
                AlertDialog alert = builder.create();
                alert.show();
            });


        }


    }
}
