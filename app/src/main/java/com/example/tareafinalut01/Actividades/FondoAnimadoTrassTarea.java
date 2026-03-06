package com.example.tareafinalut01.Actividades;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class FondoAnimadoTrassTarea extends View {

    private enum FormaTipo { CIRCULO, CUADRADO, TRIANGULO, ESTRELLA }

    private static final int CANTIDAD_FORMAS = 12;
    private static final int TAMANO_MIN = 50;
    private static final int TAMANO_MAX = 100;
    private static final float VELOCIDAD_MIN = 3f;
    private static final float VELOCIDAD_MAX = 7f;
    private static final long FRAME_RATE_MS = 16L; // ~60 FPS
    private static final int OPACIDAD = 120; // Semitransparente

    private static final int[] COLORES_APP = {
            Color.argb(OPACIDAD, 103, 58, 183),  // Deep Purple
            Color.argb(OPACIDAD, 33, 150, 243),  // Blue
            Color.argb(OPACIDAD, 76, 175, 80),   // Green
            Color.argb(OPACIDAD, 255, 193, 7),   // Amber
            Color.argb(OPACIDAD, 233, 30, 99),   // Pink
            Color.argb(OPACIDAD, 0, 188, 212),   // Cyan
            Color.argb(OPACIDAD, 255, 87, 34),   // Deep Orange
            Color.argb(OPACIDAD, 139, 195, 74),  // Light Green
            Color.argb(OPACIDAD, 156, 39, 176),  // Purple
            Color.argb(OPACIDAD, 0, 150, 136),   // Teal
            Color.argb(OPACIDAD, 63, 81, 181),   // Indigo
            Color.argb(OPACIDAD, 205, 220, 57)   // Lime
    };

    private static class FiguraGeometrica {
        FormaTipo tipo;
        float x, y;
        float vx, vy;
        int radio;
        int rotacionOffset;
        Paint pincel;
        final Path pathContorno = new Path();
    }

    private final List<FiguraGeometrica> listaFiguras = new ArrayList<>();
    private ExecutorService poolHilos;
    private final AtomicBoolean animacionActiva = new AtomicBoolean(false);
    private final Random randomGenerator = new Random();

    public FondoAnimadoTrassTarea(Context context) {
        super(context);
    }

    public FondoAnimadoTrassTarea(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            prepararFiguras(w, h);
            iniciarMovimiento();
        }
    }

    private void prepararFiguras(int ancho, int alto) {
        listaFiguras.clear();
        FormaTipo[] tiposDisponibles = FormaTipo.values();
        int colorIndex = 0;

        for (FormaTipo tipo : tiposDisponibles) {
            for (int i = 0; i < 3; i++) { // 3 de cada tipo = 12 en total
                FiguraGeometrica figura = new FiguraGeometrica();
                figura.tipo = tipo;
                figura.radio = TAMANO_MIN + randomGenerator.nextInt(TAMANO_MAX - TAMANO_MIN + 1);

                // Posición aleatoria dentro de los límites
                figura.x = figura.radio + randomGenerator.nextInt(Math.max(1, ancho - 2 * figura.radio));
                figura.y = figura.radio + randomGenerator.nextInt(Math.max(1, alto - 2 * figura.radio));

                // Dirección aleatoria (360 grados)
                double anguloRad = randomGenerator.nextDouble() * 2 * Math.PI;
                float velocidad = VELOCIDAD_MIN + randomGenerator.nextFloat() * (VELOCIDAD_MAX - VELOCIDAD_MIN);
                figura.vx = (float) (velocidad * Math.cos(anguloRad));
                figura.vy = (float) (velocidad * Math.sin(anguloRad));

                figura.rotacionOffset = randomGenerator.nextInt(360);

                figura.pincel = new Paint(Paint.ANTI_ALIAS_FLAG);
                figura.pincel.setStyle(Paint.Style.FILL);
                figura.pincel.setColor(COLORES_APP[colorIndex % COLORES_APP.length]);

                colorIndex++;
                listaFiguras.add(figura);
            }
        }
    }

    private void iniciarMovimiento() {
        detenerMovimiento();
        animacionActiva.set(true);
        // Pool de hilos suficiente para cada forma (12 formas = 12 hilos)
        poolHilos = Executors.newFixedThreadPool(CANTIDAD_FORMAS);

        for (FiguraGeometrica figura : listaFiguras) {
            poolHilos.execute(() -> {
                while (animacionActiva.get()) {
                    int w = getWidth();
                    int h = getHeight();

                    if (w > 0 && h > 0) {
                        synchronized (figura) {
                            figura.x += figura.vx;
                            figura.y += figura.vy;

                            // Rebote en bordes horizontales
                            if (figura.x - figura.radio < 0) {
                                figura.x = figura.radio;
                                figura.vx = Math.abs(figura.vx);
                            } else if (figura.x + figura.radio > w) {
                                figura.x = w - figura.radio;
                                figura.vx = -Math.abs(figura.vx);
                            }

                            // Rebote en bordes verticales
                            if (figura.y - figura.radio < 0) {
                                figura.y = figura.radio;
                                figura.vy = Math.abs(figura.vy);
                            } else if (figura.y + figura.radio > h) {
                                figura.y = h - figura.radio;
                                figura.vy = -Math.abs(figura.vy);
                            }
                        }
                    }

                    postInvalidate();

                    try {
                        Thread.sleep(FRAME_RATE_MS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
        }
    }

    public void detenerMovimiento() {
        animacionActiva.set(false);
        if (poolHilos != null) {
            poolHilos.shutdownNow();
            poolHilos = null;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        detenerMovimiento();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (FiguraGeometrica f : listaFiguras) {
            float cx, cy;
            int r;
            synchronized (f) {
                cx = f.x;
                cy = f.y;
                r = f.radio;
            }

            switch (f.tipo) {
                case CIRCULO:
                    canvas.drawCircle(cx, cy, r, f.pincel);
                    break;
                case CUADRADO:
                    canvas.drawRect(cx - r, cy - r, cx + r, cy + r, f.pincel);
                    break;
                case TRIANGULO:
                    dibujarTriangulo(canvas, cx, cy, r, f.pincel, f.pathContorno, f.rotacionOffset);
                    break;
                case ESTRELLA:
                    dibujarEstrella(canvas, cx, cy, r, f.pincel, f.pathContorno, f.rotacionOffset);
                    break;
            }
        }
    }

    private void dibujarTriangulo(Canvas c, float x, float y, int r, Paint p, Path path, int rot) {
        path.rewind();
        for (int i = 0; i < 3; i++) {
            Point pt = polarToRect(r, rot + i * 120);
            if (i == 0) path.moveTo(x + pt.x, y + pt.y);
            else path.lineTo(x + pt.x, y + pt.y);
        }
        path.close();
        c.drawPath(path, p);
    }

    private void dibujarEstrella(Canvas c, float x, float y, int r, Paint p, Path path, int rot) {
        path.rewind();
        for (int i = 0; i < 10; i++) {
            double dist = (i % 2 == 0) ? r : r / 2.5;
            Point pt = polarToRect(dist, rot + i * 36);
            if (i == 0) path.moveTo(x + pt.x, y + pt.y);
            else path.lineTo(x + pt.x, y + pt.y);
        }
        path.close();
        c.drawPath(path, p);
    }

    private Point polarToRect(double dist, int grados) {
        double rad = Math.toRadians(grados);
        return new Point(
                (int) Math.round(dist * Math.cos(rad)),
                (int) Math.round(dist * Math.sin(rad))
        );
    }
}
