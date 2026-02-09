package com.example.tareafinalut01.Fragmentos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.tareafinalut01.R;
import com.example.tareafinalut01.Entidades.Tarea;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class SecondFragment extends Fragment {

    private EditText etDescripcion;
    private Button btnVolver;
    private Button btnGuardar;
    private ImageButton btnDocumento, btnImagen, btnAudio, btnVideo;

    private ActivityResultLauncher<Intent> filePickerLauncher;
    private Tarea tarea;
    private String tipoFicheroSolicitado;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null && tarea != null) {
                            String localUri = guardarArchivoEnLocal(uri);
                            if (localUri != null) {
                                switch (tipoFicheroSolicitado) {
                                    case "image/*":
                                        tarea.setUriImagen(localUri);
                                        break;
                                    case "video/*":
                                        tarea.setUriVideo(localUri);
                                        break;
                                    case "audio/*":
                                        tarea.setUriAudio(localUri);
                                        break;
                                    case "*/*":
                                        tarea.setUriDocumento(localUri);
                                        break;
                                }
                                Toast.makeText(getContext(), "Archivo vinculado", Toast.LENGTH_SHORT).show();
                                actualizarEstadoBotones();
                            }
                        }
                    }
                }
        );
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View fragmento2 = inflater.inflate(R.layout.fragment_second, container, false);
        etDescripcion = fragmento2.findViewById(R.id.etm_Descricion);
        btnGuardar = fragmento2.findViewById(R.id.btn_Guardar);
        btnVolver = fragmento2.findViewById(R.id.btn_Volver);

        btnDocumento = fragmento2.findViewById(R.id.btn_documento);
        btnImagen = fragmento2.findViewById(R.id.btn_imagen);
        btnAudio = fragmento2.findViewById(R.id.btn_audio);
        btnVideo = fragmento2.findViewById(R.id.btn_video);

        btnGuardar.setOnClickListener(v -> {
            if (tarea != null) {
                tarea.setDescripcion(etDescripcion.getText().toString());
            }
            comunicador2.guardarDescripcion(etDescripcion.getText().toString());
        });
        btnVolver.setOnClickListener(v -> {
            comunicador2.ir1();
        });

        // Selección de ficheros
        btnDocumento.setOnClickListener(v -> elegirFichero("*/*"));
        btnImagen.setOnClickListener(v -> elegirFichero("image/*"));
        btnAudio.setOnClickListener(v -> elegirFichero("audio/*"));
        btnVideo.setOnClickListener(v -> elegirFichero("video/*"));

        // Eliminación de ficheros (Pulsación larga)
        btnDocumento.setOnLongClickListener(v -> confirmarBorrado("documento"));
        btnImagen.setOnLongClickListener(v -> confirmarBorrado("imagen"));
        btnAudio.setOnLongClickListener(v -> confirmarBorrado("audio"));
        btnVideo.setOnLongClickListener(v -> confirmarBorrado("video"));

        return fragmento2;
    }

    private boolean confirmarBorrado(String tipo) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar adjunto")
                .setMessage("¿Deseas eliminar el archivo de " + tipo + " adjunto?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    switch (tipo) {
                        case "documento": tarea.setUriDocumento(null); break;
                        case "imagen": tarea.setUriImagen(null); break;
                        case "audio": tarea.setUriAudio(null); break;
                        case "video": tarea.setUriVideo(null); break;
                    }
                    Toast.makeText(getContext(), "Archivo de " + tipo + " eliminado", Toast.LENGTH_SHORT).show();
                    actualizarEstadoBotones();
                })
                .setNegativeButton("Cancelar", null)
                .show();
        return true;
    }

    private void actualizarEstadoBotones() {
        if (tarea == null) return;

        actualizarFondoBoton(btnDocumento, tarea.getUriDocumento() != null);
        actualizarFondoBoton(btnImagen, tarea.getUriImagen() != null);
        actualizarFondoBoton(btnAudio, tarea.getUriAudio() != null);
        actualizarFondoBoton(btnVideo, tarea.getUriVideo() != null);
    }
    private void actualizarFondoBoton(ImageButton btn, boolean tieneArchivo) {
        if (tieneArchivo) {
            // fondo leve para saber que se ha añadido un archivo
            btn.setBackgroundColor(Color.parseColor("#4481C784")); 
        } else {
            // Fondo transparente (por defecto)
            btn.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    private void elegirFichero(String mimeType) {
        tipoFicheroSolicitado = mimeType;
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(mimeType);
        filePickerLauncher.launch(intent);
    }

    private String guardarArchivoEnLocal(Uri uri) {
        try {
            String nombreArchivo = getFileName(uri);
            File carpetaDestino;
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
            if (prefs.getBoolean("checkbox_sd", false)) {
                File[] externalDirs = requireContext().getExternalFilesDirs(null);
                carpetaDestino = new File((externalDirs.length > 1 && externalDirs[1] != null) ? externalDirs[1] : externalDirs[0], "adjuntos");
            } else {
                carpetaDestino = new File(requireContext().getFilesDir(), "adjuntos");
            }
            if (!carpetaDestino.exists()) carpetaDestino.mkdirs();
            File archivoDestino = new File(carpetaDestino, System.currentTimeMillis() + "_" + nombreArchivo);
            try (InputStream in = requireContext().getContentResolver().openInputStream(uri);
                 OutputStream out = new FileOutputStream(archivoDestino)) {
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
            }
            return Uri.fromFile(archivoDestino).toString();
        } catch (Exception e) {
            return null;
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = requireContext().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index != -1) result = cursor.getString(index);
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) result = result.substring(cut + 1);
        }
        return result;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey("tarea")) {
            this.tarea = arguments.getParcelable("tarea");
            if (this.tarea != null) {
                etDescripcion.setText(tarea.getDescripcion());
                actualizarEstadoBotones();
            }
        }
    }
}
