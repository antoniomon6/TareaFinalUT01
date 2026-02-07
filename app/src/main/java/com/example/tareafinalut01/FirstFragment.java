package com.example.tareafinalut01;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Button;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

public class FirstFragment extends Fragment {

    private EditText etTitulo;
    private EditText etFechaCreacion;
    private EditText etFechaLimite;
    private Spinner spnProgreso;
    private CheckBox cbPrioritaria;
    private Button btnSiguiente;
    private Tarea tarea;

    private int progreso;

    public interface ComuncacionFragmento1 {
        void guardarTareaSinDescripcion(Tarea tareaSinDescripcion);
    }

    private ComuncacionFragmento1 comunicador1;

    public static FirstFragment newInstance(Tarea tarea) {
        FirstFragment fragment = new FirstFragment();
        Bundle args = new Bundle();
        args.putParcelable("tarea", tarea);
        fragment.setArguments(args);
        return fragment;
    }

    public FirstFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ComuncacionFragmento1) {
            comunicador1 = (ComuncacionFragmento1) context;
        } else {
            throw new ClassCastException(context + " debe implementar ComunicacionFragmento1");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey("tarea")) {
            tarea = arguments.getParcelable("tarea");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View fragmento1 = inflater.inflate(R.layout.fragment_first, container, false);

        etTitulo = fragmento1.findViewById(R.id.etx_titulo);
        etFechaCreacion = fragmento1.findViewById(R.id.etx_fecha_creacion);
        etFechaLimite = fragmento1.findViewById(R.id.etx_fecha_limite);
        spnProgreso = fragmento1.findViewById(R.id.spn_progreso);
        cbPrioritaria = fragmento1.findViewById(R.id.cmb_prioritaria);
        btnSiguiente = fragmento1.findViewById(R.id.btn_siguiente);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.opciones_progreso,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnProgreso.setAdapter(adapter);

        spnProgreso.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                progreso = position * 25;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                progreso = 0;
            }
        });

        etFechaCreacion.setOnClickListener(v -> showDatePickerDialog(etFechaCreacion));
        etFechaLimite.setOnClickListener(v -> showDatePickerDialog(etFechaLimite));

        btnSiguiente.setOnClickListener(v -> {
            if (verificarCampos()) {
                // Actualizamos el objeto Tarea existente en lugar de crear uno nuevo
                if (tarea != null) {
                    tarea.setTitulo(etTitulo.getText().toString());
                    tarea.setProgreso(progreso);
                    tarea.setFechaCreacion(LocalDate.parse(etFechaCreacion.getText().toString(), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    tarea.setFechaLimite(LocalDate.parse(etFechaLimite.getText().toString(), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    tarea.setPrioritaria(cbPrioritaria.isChecked());
                }
                comunicador1.guardarTareaSinDescripcion(tarea);
            }
        });

        return fragmento1;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (tarea != null) {
            etTitulo.setText(tarea.getTitulo());
            if (tarea.getFechaCreacion() != null) {
                etFechaCreacion.setText(tarea.getFechaCreacion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            } else {
                etFechaCreacion.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }
            if (tarea.getFechaLimite() != null) {
                etFechaLimite.setText(tarea.getFechaLimite().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }
            spnProgreso.setSelection(tarea.getProgreso() / 25);
            cbPrioritaria.setChecked(tarea.isPrioritaria());
        }
    }

    public boolean verificarCampos() {
        boolean flag = true;
        if (etTitulo.getText().toString().isEmpty()) {
            etTitulo.setError(getString(R.string.error_campo_vacio));
            flag = false;
        }
        if (etFechaCreacion.getText().toString().isEmpty()) {
            etFechaCreacion.setError(getString(R.string.error_campo_vacio));
            flag = false;
        }
        if (etFechaLimite.getText().toString().isBlank()) {
            etFechaLimite.setError(getString(R.string.error_campo_vacio));
            flag = false;
        }
        return flag;
    }

    private void showDatePickerDialog(EditText etFechaObjetivo) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (v, year1, monthOfYear, dayOfMonth) -> {
                    String fechaSeleccionada = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year1);
                    etFechaObjetivo.setText(fechaSeleccionada);
                }, year, month, day);
        datePickerDialog.show();
    }
}
