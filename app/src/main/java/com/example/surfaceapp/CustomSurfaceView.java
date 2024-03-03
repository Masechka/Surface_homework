package com.example.surfaceapp;

import static android.view.MotionEvent.ACTION_DOWN;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.health.connect.datatypes.WeightRecord;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;

public class CustomSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private DrawThread thread = null;
    private final List<Circle> circles = new ArrayList<>();


    public final int[] colours = {Color.RED, Color.WHITE, Color.BLUE, Color.YELLOW, Color.GREEN, Color.GRAY, Color.MAGENTA, Color.CYAN};

    Random randomizer = new Random();

    public CustomSurfaceView(Context context) {
        super(context);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        DrawThread drawThread = new DrawThread(holder);
        drawThread.start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        // do nothing
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        stop();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case ACTION_DOWN:
                int random = colours[randomizer.nextInt(colours.length)];
                circles.add(new Circle(random, (int) event.getX(), (int) event.getY()));
                if (circles.size() == 7) {
                    circles.remove(0);
                }
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }


    private void stop() {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }

    private class DrawThread extends Thread {

        private SurfaceHolder holder;
        private Paint paint;

        public DrawThread(SurfaceHolder holder) {
            this.holder = holder;
            this.paint = new Paint();
        }

        @Override
        public void run() {
            while (!isInterrupted()) {
                draw();
            }
        }


        private void draw() {
            if (holder.getSurface().isValid()) {
                Canvas canvas = holder.lockCanvas();
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                for (Circle circle : circles) {
                    circle.move(canvas);
                    paint.setColor(circle.color);
                    canvas.drawCircle(circle.x, circle.y, circle.RADIUS, paint);
                }
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }
    private class Circle {
        final int RADIUS = 100;
        int color;
        int step_x = 15, step_y = 15;
        int x, y;

        public Circle(int color, int start_x, int start_y) {
            this.x = start_x;
            this.y = start_y;
            this.color = color;
        }

        public void move(Canvas canvas) {
            this.x = this.x + step_x;
            this.y = this.y + step_y;
            if (this.x >= canvas.getWidth() - 100) {
                this.step_x = -15;
            } else if (this.x <= 100) {
                this.step_x = 15;
            }
            if (this.y >= canvas.getHeight() - 100) {
                step_y = -15;
            } else if (this.y <= 100) {
                step_y = 15;
            }
        }
    }
}
