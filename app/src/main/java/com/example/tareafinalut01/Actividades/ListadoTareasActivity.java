package com.example.tareafinalut01.Actividades;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tareafinalut01.R;
import com.example.tareafinalut01.Entidades.Tarea;
import com.example.tareafinalut01.Adapatadores.TareaAdapter;
import com.example.tareafinalut01.Repositorio.TareaRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;

public class ListadoTareasActivity extends BaseActivity {

    private RecyclerView rvTareas;
    private ArrayList<Tarea> misTareas = new ArrayList<>();
    private TextView txtVacio;
    TareaAdapter miAdaptador;
    private boolean soloPrioritarias;
    private ActivityResultLauncher<Intent> tareaLauncher;
    private TareaRepository repository;
    private boolean primeraCarga = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_listado_tareas);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        repository = new TareaRepository(getApplication());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.titulo_listado_tareas);
        }

        soloPrioritarias = false;
        txtVacio = findViewById(R.id.txtVacio);
        rvTareas = findViewById(R.id.rcvTareas);
        miAdaptador = new TareaAdapter((misTareas));
        rvTareas.setAdapter(miAdaptador);
        rvTareas.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        // Observamos la base de datos
        repository.getAllTareas().observe(this, tareas -> {
            // Solo ejecutamos la lógica de inicialización en la apertura del programa
            if (primeraCarga) {
                primeraCarga = false;
                if (tareas == null || tareas.isEmpty()) {
                    init(); // Si está vacía al arrancar, rellenamos
                    return;
                }
            }
            // Actualizamos la lista local con los datos de la BD
            misTareas.clear();
            if (tareas != null) {
                misTareas.addAll(tareas);
                actualiarLista();
            }
            actualiarLista();
        });


        tareaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    /*
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK && result.getData() != null) {
                        Intent intent = result.getData();
                        if (intent.hasExtra("NUEVA_TAREA")) {
                            Tarea nuevaTarea = result.getData().getParcelableExtra("NUEVA_TAREA");
                            if (nuevaTarea != null) {
                                misTareas.add(nuevaTarea);
                                miAdaptador.notifyItemInserted(misTareas.size() - 1);
                                verificarVacio();
                            }
                        }
                        if (intent.hasExtra("EDITAR_TAREA")) {
                            Tarea editarTarea = result.getData().getParcelableExtra("EDITAR_TAREA");
                            int pos = intent.getIntExtra("pos", 0);
                            misTareas.set(pos, editarTarea);
                            miAdaptador.notifyItemChanged(pos);
                            verificarVacio();
                        }
                    }
                    */
                    // Ya no gestiono misTareas manualmente aquí,
                    // CrearTareaActivity ya guarda en BD y el observador refrescará la lista.
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        actualiarLista();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("misTareas", misTareas);
    }

    private void verificarVacio() {
        if (miAdaptador.getItemCount() == 0) {
            rvTareas.setVisibility(View.GONE);
            txtVacio.setVisibility(View.VISIBLE);
        } else {
            rvTareas.setVisibility(View.VISIBLE);
            txtVacio.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        menu.setGroupVisible(R.id.it_group_gestion_tarea, true);
        menu.setGroupVisible(R.id.it_group_masOpciones, true);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.itmAnadirTarea) {
            Intent intent = new Intent(ListadoTareasActivity.this, CrearTareaActivity.class);
            tareaLauncher.launch(intent);
        } else if (id == R.id.itmHacerPrioritaria) {
            soloPrioritarias = !soloPrioritarias;
            actualiarLista();
        } else if (id == R.id.itmAcercaDe) {
            mostrarAlerta(getString(R.string.acerca_de),
                    "TrassTarea \n IES Trassierra \n Antonio Malagon Garcia \n 2025",
                    getString(R.string.aceptar));

        } else if (id == R.id.itmSalir) {
            finishAffinity();
        } else if (id == R.id.itmPreferencias) {
            Intent intent = new Intent(ListadoTareasActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void actualiarLista() {
        Comparator<Tarea> comparator;
        switch (criterio != null ? criterio : "2") {
            case "1": // Alfabético
                comparator = Comparator.comparing(t -> t.getTitulo().toLowerCase());
                break;
            case "2": // Fecha de creación
                comparator = Comparator.comparing(t -> t.getFechaCreacion());
                break;
            case "3": // Días restantes (Fecha límite)
                comparator = Comparator.comparing(t -> t.getFechaLimite());
                break;
            case "4": // Progreso
                comparator = Comparator.comparingInt(t -> t.getProgreso());
                break;
            default:
                comparator = Comparator.comparing(t -> t.getFechaCreacion());
                break;
        }
        if (!orden) {
            comparator = comparator.reversed();
        }
        misTareas.sort(comparator);
        
        ArrayList<Tarea> listaAMostrar;
        if (soloPrioritarias) {
            listaAMostrar = new ArrayList<>();
            misTareas.forEach(x -> {
                if (x.isPrioritaria()) listaAMostrar.add(x);
            });
        } else {
            listaAMostrar = misTareas;
        }
        
        miAdaptador = new TareaAdapter(listaAMostrar);
        rvTareas.setAdapter(miAdaptador);
        verificarVacio();
    }

    public void mostrarAlerta(String titulo, String mensaje, String boton) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titulo);
        builder.setMessage(mensaje);
        builder.setPositiveButton(boton, null);
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (miAdaptador.tareaSeleccionada == null) return super.onContextItemSelected(item);
        
        Tarea tarea = miAdaptador.tareaSeleccionada;
        int itemId = item.getItemId();
        
        if (itemId == R.id.it_eliminar) {
            AlertDialog.Builder constructor = new AlertDialog.Builder(this);
            constructor.setTitle(R.string.desea_eliminar_este_elemento);
            constructor.setMessage(tarea.getTitulo());
            constructor.setPositiveButton(R.string.borrar, (dialog, which) -> {
                repository.delete(tarea); // Eliminamos de la base de datos
            });
            constructor.setNegativeButton(R.string.cancelar, null);
            constructor.show();
        }
        if (itemId == R.id.it_editar) {
            Intent intent = new Intent(ListadoTareasActivity.this, CrearTareaActivity.class);
            intent.putExtra("tarea", tarea);
            // Ya no es necesario pasar la posición ya que editamos por ID de Room
            intent.putExtra("pos", misTareas.indexOf(tarea)); 
            tareaLauncher.launch(intent);
        }
        return super.onContextItemSelected(item);
    }


    public void init() {
        repository.insert(new Tarea("Revisar API de la cámara", "Investigar CameraX y sus casos de uso.", 50, LocalDate.now().minusDays(10), LocalDate.now().plusDays(5), true));
        repository.insert(new Tarea("Comprar billetes de tren", "Billetes para el viaje de vacaciones de verano.", 0, LocalDate.now().minusDays(2), LocalDate.now().plusMonths(2), false));
        repository.insert(new Tarea("Terminar tutorial de Kotlin", "Completar las lecciones sobre corrutinas.", 100, LocalDate.now().minusDays(5), LocalDate.now().plusDays(1), false));
        repository.insert(new Tarea("Preparar presentación de proyecto", "Crear diapositivas y guion para la demo final.", 75, LocalDate.now().minusDays(3), LocalDate.now().plusWeeks(1), true));
        repository.insert(new Tarea("Organizar archivos del PC", "Clasificar documentos y eliminar duplicados.", 0, LocalDate.now().minusMonths(1), LocalDate.now().plusDays(10), false));
        repository.insert(new Tarea("Entregar borrador del informe", "Primer borrador del informe de análisis de datos.", 25, LocalDate.now().minusDays(1), LocalDate.now().plusDays(3), true));
        repository.insert(new Tarea("Arreglar bug de login", "El botón de 'recordar contraseña' no funciona.", 100, LocalDate.now().minusDays(1), LocalDate.now().plusDays(2), true));
        repository.insert(new Tarea("Leer 'Diseño de APIs REST'", "Leer los primeros 3 capítulos.", 40, LocalDate.now().minusDays(15), LocalDate.now().plusWeeks(3), false));
        repository.insert(new Tarea("Planificar la cena de aniversario", "Reservar en un restaurante y comprar regalo.", 0, LocalDate.now().minusDays(5), LocalDate.now().plusDays(20), false));
        repository.insert(new Tarea("Migrar a Android 14", "Asegurarse de que la app es compatible con la última versión.", 90, LocalDate.now().minusDays(2), LocalDate.now().plusDays(4), true));
        repository.insert(new Tarea("Hacer la colada", "Separar ropa de color y blanca.", 20, LocalDate.now().minusDays(1), LocalDate.now().plusDays(1), false));
        repository.insert(new Tarea("Pasear al perro", "Ruta de 3km por el parque.", 100, LocalDate.now().minusDays(1), LocalDate.now(), false));
        repository.insert(new Tarea("Llamar al dentista", "Pedir cita para revisión anual.", 0, LocalDate.now().minusDays(1), LocalDate.now().plusDays(5), true));
        repository.insert(new Tarea("Actualizar dependencias de Gradle", "Comprobar versiones y actualizar el build.gradle.", 60, LocalDate.now().minusWeeks(2), LocalDate.now().plusWeeks(2), false));
        repository.insert(new Tarea("Terminar serie de TV", "Ver los dos últimos episodios.", 80, LocalDate.now().minusDays(1), LocalDate.now().plusDays(2), false));
    }
}
