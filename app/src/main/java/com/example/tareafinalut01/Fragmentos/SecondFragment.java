package com.example.tareafinalut01.Fragmentos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.example.tareafinalut01.R;
import com.example.tareafinalut01.Entidades.Tarea;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class SecondFragment extends Fragment {

    private EditText etDescripcion;
    private Button btnVolver, btnGuardar;
    private ImageButton btnDocumento, btnImagen, btnAudio, btnVideo;

    private ActivityResultLauncher<Intent> multimediaLauncher;
    private Tarea tarea;
    private String tipoFicheroSolicitado;
    private File archivoCapturaTemporal; // Ahora guardamos el FILE, no la URI

    public interface ComuncacionFragmento2 {
        void ir1(Tarea tareaActualizada);
        void guardarTareaCompleta(Tarea tareaCompleta);
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
        multimediaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        String rutaFinal = null;

                        if (archivoCapturaTemporal != null && archivoCapturaTemporal.exists()) {
                            // Viene de captura directa (Cámara/Audio) -> Ya está guardado en el archivo físico
                            rutaFinal = archivoCapturaTemporal.getAbsolutePath();
                            archivoCapturaTemporal = null;
                        } else if (result.getData() != null) {
                            // Viene de selección de archivo -> Hay que copiarlo a nuestra carpeta local
                            Uri selectedUri = result.getData().getData();
                            if (selectedUri != null) {
                                rutaFinal = copiarArchivoALocal(selectedUri);
                            }
                        }

                        if (rutaFinal != null && tarea != null) {
                            vincularRutaATarea(rutaFinal);
                            actualizarEstadoBotones();
                            Toast.makeText(getContext(), R.string.archivo_vinculado, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        archivoCapturaTemporal = null;
                    }
                }
        );
    }

    private void vincularRutaATarea(String ruta) {
        switch (tipoFicheroSolicitado) {
            case "image/*": tarea.setUriImagen(ruta); break;
            case "video/*": tarea.setUriVideo(ruta); break;
            case "audio/*": tarea.setUriAudio(ruta); break;
            case "*/*": tarea.setUriDocumento(ruta); break;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);
        etDescripcion = view.findViewById(R.id.etm_Descricion);
        btnGuardar = view.findViewById(R.id.btn_Guardar);
        btnVolver = view.findViewById(R.id.btn_Volver);
        btnDocumento = view.findViewById(R.id.btn_documento);
        btnImagen = view.findViewById(R.id.btn_imagen);
        btnAudio = view.findViewById(R.id.btn_audio);
        btnVideo = view.findViewById(R.id.btn_video);

        btnGuardar.setOnClickListener(v -> {
            if (tarea != null) {
                tarea.setDescripcion(etDescripcion.getText().toString());
                comunicador2.guardarTareaCompleta(tarea);
            }
        });
        btnVolver.setOnClickListener(v -> {
            if (tarea != null) {
                tarea.setDescripcion(etDescripcion.getText().toString());
                comunicador2.ir1(tarea);
            }
        });

        btnDocumento.setOnClickListener(v -> abrirSoloSelector("*/*"));
        btnImagen.setOnClickListener(v -> abrirChooserMultimedia("image/*", MediaStore.ACTION_IMAGE_CAPTURE, ".jpg"));
        btnVideo.setOnClickListener(v -> abrirChooserMultimedia("video/*", MediaStore.ACTION_VIDEO_CAPTURE, ".mp4"));
        btnAudio.setOnClickListener(v -> abrirChooserMultimedia("audio/*", MediaStore.Audio.Media.RECORD_SOUND_ACTION, ".3gp"));

        btnDocumento.setOnLongClickListener(v -> confirmarBorrado("documento"));
        btnImagen.setOnLongClickListener(v -> confirmarBorrado("imagen"));
        btnAudio.setOnLongClickListener(v -> confirmarBorrado("audio"));
        btnVideo.setOnLongClickListener(v -> confirmarBorrado("video"));

        return view;
    }

    private void abrirSoloSelector(String mime) {
        tipoFicheroSolicitado = mime;
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(mime);
        multimediaLauncher.launch(intent);
    }

    private void abrirChooserMultimedia(String mime, String action, String extension) {
        tipoFicheroSolicitado = mime;
        archivoCapturaTemporal = null;
        
        Intent intentGaleria = new Intent(Intent.ACTION_GET_CONTENT);
        intentGaleria.setType(mime);
        intentGaleria.addCategory(Intent.CATEGORY_OPENABLE);

        Intent intentCaptura = new Intent(action);
        archivoCapturaTemporal = crearArchivoVacioLocal(extension);
        
        if (archivoCapturaTemporal != null) {
            Uri contentUri = FileProvider.getUriForFile(requireContext(), 
                    "com.example.tareafinalut01.fileprovider", archivoCapturaTemporal);
            intentCaptura.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
            intentCaptura.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }

        Intent chooser = Intent.createChooser(intentGaleria, "Selecciona una opción");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intentCaptura});
        multimediaLauncher.launch(chooser);
    }

    private File crearArchivoVacioLocal(String extension) {
        try {
            File carpeta = obtenerCarpetaAlmacenamiento();
            String nombre = "CAPTURA_" + System.currentTimeMillis() + extension;
            return new File(carpeta, nombre);
        } catch (Exception e) { return null; }
    }

    private String copiarArchivoALocal(Uri uri) {
        try {
            String nombreOriginal = getFileName(uri);
            if (nombreOriginal == null) nombreOriginal = "archivo_adjunto";
            
            File carpetaDestino = obtenerCarpetaAlmacenamiento();
            File archivoFinal = new File(carpetaDestino, System.currentTimeMillis() + "_" + nombreOriginal);
            
            try (InputStream in = requireContext().getContentResolver().openInputStream(uri);
                 OutputStream out = new FileOutputStream(archivoFinal)) {
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
            }
            return archivoFinal.getAbsolutePath();
        } catch (Exception e) { return null; }
    }

    private File obtenerCarpetaAlmacenamiento() {
        File carpeta = null;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        if (prefs.getBoolean("checkbox_sd", false)) {
            File[] dirs = requireContext().getExternalFilesDirs(null);
            if (dirs != null && dirs.length > 1 && dirs[1] != null) {
                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState(dirs[1]))) {
                    carpeta = new File(dirs[1], "adjuntos");
                }
            }
        }
        if (carpeta == null) {
            carpeta = new File(requireContext().getFilesDir(), "adjuntos");
        }
        if (!carpeta.exists()) carpeta.mkdirs();
        return carpeta;
    }

    private void actualizarEstadoBotones() {
        if (tarea == null) return;
        actualizarFondoBoton(btnDocumento, tarea.getUriDocumento() != null);
        actualizarFondoBoton(btnImagen, tarea.getUriImagen() != null);
        actualizarFondoBoton(btnAudio, tarea.getUriAudio() != null);
        actualizarFondoBoton(btnVideo, tarea.getUriVideo() != null);
    }

    private void actualizarFondoBoton(ImageButton btn, boolean tieneArchivo) {
        btn.setBackgroundColor(tieneArchivo ? Color.parseColor("#4481C784") : Color.TRANSPARENT);
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
                    actualizarEstadoBotones();
                })
                .setNegativeButton("Cancelar", null)
                .show();
        return true;
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
        if (getArguments() != null && getArguments().containsKey("tarea")) {
            this.tarea = getArguments().getParcelable("tarea");
            if (this.tarea != null) {
                etDescripcion.setText(tarea.getDescripcion());
                actualizarEstadoBotones();
            }
        }
    }
}
