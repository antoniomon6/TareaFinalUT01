package com.example.tareafinalut01;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class SecondFragment extends Fragment {

    private EditText etDescripcion;
    private Button btnVolver;
    private Button btnGuardar;

    public interface ComuncacionFragmento2 {
        void ir1();
        void guardarDescripcion(String descripcion);
    }

    private ComuncacionFragmento2 comunicador2;

    public static SecondFragment newInstance(Tarea tarea) {
        SecondFragment fragment = new SecondFragment();
        Bundle args = new Bundle();
        args.putParcelable("tarea", tarea);
        fragment.setArguments(args);
        return fragment;
    }

    public SecondFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ComuncacionFragmento2) {
            comunicador2 = (ComuncacionFragmento2) context;
        } else {
            throw new ClassCastException(context + " debe implementar ComuncacionFragmento2");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View fragmento2 = inflater.inflate(R.layout.fragment_second, container, false);
        etDescripcion = fragmento2.findViewById(R.id.etm_Descricion);
        btnGuardar = fragmento2.findViewById(R.id.btn_Guardar);
        btnVolver = fragmento2.findViewById(R.id.btn_Volver);

        btnGuardar.setOnClickListener(v -> {
            comunicador2.guardarDescripcion(etDescripcion.getText().toString());
        });
        btnVolver.setOnClickListener(v -> {
            comunicador2.ir1();
        });
        return fragmento2;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey("tarea")) {
            Tarea tarea = arguments.getParcelable("tarea");
            if (tarea != null) {
                etDescripcion.setText(tarea.getDescripcion());
            }
        }
    }
}
