package com.example.tareafinalut01.Fragmentos;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.tareafinalut01.R;

import java.io.File;
import java.io.FileInputStream;
import java.util.Timer;
import java.util.TimerTask;

public class ReproductorFragment extends Fragment {

    private String tipo, uriStr;
    private ImageView ivVisor;
    private VideoView vvVisor;
    private LinearLayout llAudio;
    private ProgressBar pbAudio;
    private ImageButton btnPlay, btnPause, btnStop;
    private Button btnCerrar;
    private MediaPlayer mediaPlayer;
    private Timer timer;

    public static ReproductorFragment newInstance(String tipo, String uri) {
        ReproductorFragment fragment = new ReproductorFragment();
        Bundle args = new Bundle();
        args.putString("tipo", tipo);
        args.putString("uri", uri);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reproductor, container, false);
        
        ivVisor = v.findViewById(R.id.iv_visor_fragment);
        vvVisor = v.findViewById(R.id.vv_visor_fragment);
        llAudio = v.findViewById(R.id.ll_audio_controls);
        pbAudio = v.findViewById(R.id.pb_audio);
        btnPlay = v.findViewById(R.id.btn_play);
        btnPause = v.findViewById(R.id.btn_pause);
        btnStop = v.findViewById(R.id.btn_stop);
        btnCerrar = v.findViewById(R.id.btn_cerrar_visor);

        if (getArguments() != null) {
            tipo = getArguments().getString("tipo");
            uriStr = getArguments().getString("uri");
            configurarVisor();
        }

        btnCerrar.setOnClickListener(view -> cerrar());

        return v;
    }

    private void configurarVisor() {
        Uri uri = Uri.parse(uriStr);
        ocultarTodo();

        switch (tipo) {
            case "Imagen":
                ivVisor.setVisibility(View.VISIBLE);
                try {
                    File file = new File(uri.getPath());
                    if (file.exists()) {
                        ivVisor.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
                    } else {
                        ivVisor.setImageURI(uri);
                    }
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error al cargar imagen", Toast.LENGTH_SHORT).show();
                }
                break;
            case "Video":
                vvVisor.setVisibility(View.VISIBLE);
                vvVisor.setVideoURI(uri);
                MediaController mc = new MediaController(requireContext());
                mc.setAnchorView(vvVisor);
                vvVisor.setMediaController(mc);
                vvVisor.start();
                break;
            case "Audio":
                llAudio.setVisibility(View.VISIBLE);
                try {
                    mediaPlayer = new MediaPlayer();
                    File file = new File(uri.getPath());
                    try (FileInputStream fis = new FileInputStream(file)) {
                        mediaPlayer.setDataSource(fis.getFD());
                    }
                    mediaPlayer.prepare();
                    pbAudio.setMax(mediaPlayer.getDuration());
                    mediaPlayer.start();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error al reproducir audio", Toast.LENGTH_SHORT).show();
                }
                
                btnPlay.setOnClickListener(v -> { if(mediaPlayer != null) mediaPlayer.start(); });
                btnPause.setOnClickListener(v -> { if(mediaPlayer != null) mediaPlayer.pause(); });
                btnStop.setOnClickListener(v -> {
                    if(mediaPlayer != null) {
                        mediaPlayer.pause();
                        mediaPlayer.seekTo(0);
                        pbAudio.setProgress(0);
                    }
                });
                break;
            case "Documento":
                abrirDocumentoExterno(uri);
                break;
        }
    }

    private void abrirDocumentoExterno(Uri uri) {
        try {
            File file = new File(uri.getPath());
            Uri contentUri = FileProvider.getUriForFile(requireContext(), "com.example.tareafinalut01.fileprovider", file);
            
            String extension = MimeTypeMap.getFileExtensionFromUrl(uriStr);
            String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            if (type == null) type = "*/*";

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(contentUri, type);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, "Abrir documento con..."));
            
            // Cerramos el fragmento ya que el visor es externo
            cerrar();
        } catch (Exception e) {
            Toast.makeText(getContext(), "No se pudo abrir el documento", Toast.LENGTH_SHORT).show();
            cerrar();
        }
    }

    private void cerrar() {
        getParentFragmentManager().beginTransaction().remove(this).commit();
    }



    private void ocultarTodo() {
        ivVisor.setVisibility(View.GONE);
        vvVisor.setVisibility(View.GONE);
        llAudio.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (timer != null) {
            timer.cancel();
        }
    }
}
