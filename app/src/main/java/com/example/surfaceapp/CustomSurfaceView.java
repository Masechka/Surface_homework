package com.example.surfaceapp;

import static android.view.MotionEvent.ACTION_DOWN;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

public class CustomSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private DrawThread thread = null;

    public CustomSurfaceView(Context context) {
        super(context);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        start();
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
                thread.setPoint(new Point((int) event.getX(), (int) event.getY()));
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    private void start() {
        // new DrawThread(getHolder(), Color.BLUE, 250, 400).start();
        thread = new DrawThread(getHolder(), Color.WHITE, 10, 10);
        thread.start();
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
        private Point point;

        public DrawThread(SurfaceHolder holder, int color, int startX, int startY) {
            this.holder = holder;
            this.point = new Point(startX, startY);
            this.paint = new Paint();
            paint.setColor(color);
        }

        public void setPoint(Point point) {
            this.point = point;
        }

        @Override
        public void run() {
            while (!isInterrupted()) {
                update();
                draw();
                control();
            }
        }

        private void update() {
            int x = point.x + 10;
            int y = point.y + 10;
            point.set(x, y);
        }

        private void draw() {
            if (holder.getSurface().isValid()) {
                Canvas canvas = holder.lockCanvas();
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                canvas.drawCircle(point.x, point.y, 100, paint);
                holder.unlockCanvasAndPost(canvas);
            }
        }

        private void control() {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
