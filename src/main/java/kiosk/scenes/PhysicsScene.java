package kiosk.scenes;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import kiosk.EventListener;
import kiosk.InputEvent;
import kiosk.Kiosk;
import kiosk.SceneGraph;
import processing.core.PConstants;
import processing.event.MouseEvent;

public class PhysicsScene implements Scene {

    private MouseEvent firstDragEvent = null;
    private MouseEvent lastDragEvent = null;
    private long lastDragMs = 0;
    Body bodyDragging = null;
    ArrayList<Body> bodies = new ArrayList<>();

    @Override
    public void init(Kiosk sketch) {
        Map<InputEvent, EventListener> listeners = new HashMap<>();
        listeners.put(InputEvent.MouseReleased, this::onMouseReleased);
        listeners.put(InputEvent.MousePressed, this::onMousePressed);
        listeners.put(InputEvent.MouseDragged, this::onMouseDragged);
//        sketch.hookControl(listeners);

        Body rootBody = new Body();
        rootBody.width = 25;
        rootBody.height = 25;
        this.bodies.add(rootBody);
    }

    private void onMousePressed(Object mouseEventObject) {
        MouseEvent mouseEvent = (MouseEvent)mouseEventObject;

        for (Body body : this.bodies) {
            Rectangle rect = new Rectangle((int) body.x, (int) body.y, body.width, body.height);
            if (rect.contains(mouseEvent.getX(), mouseEvent.getY())) {
                this.bodyDragging = body;
                break;
            }
        }

        this.lastDragEvent = mouseEvent;
        this.lastDragMs = System.currentTimeMillis();
    }

    private void onMouseReleased(Object mouseEventObject) {
        MouseEvent mouseEvent = (MouseEvent)mouseEventObject;

        this.lastDragEvent = null;
        this.bodyDragging = null;
    }

    private void onMouseDragged(Object mouseEventObject) {
        MouseEvent mouseEvent = (MouseEvent)mouseEventObject;

        if (this.bodyDragging != null && this.lastDragEvent != null) {
            int dx = mouseEvent.getX() - this.lastDragEvent.getX();
            int dy = mouseEvent.getY() - this.lastDragEvent.getY();
            double dt = (System.currentTimeMillis() - this.lastDragMs) / 1000.0;

            this.bodyDragging.x += dx;
            this.bodyDragging.y += dy;
            if (dt > 0) {
                this.bodyDragging.vx = dx / dt;
                this.bodyDragging.vy = dy / dt;
            } else {
                this.bodyDragging.vx = 0;
                this.bodyDragging.vy = 0;
            }
            System.out.println((double)dx / dt);
        }

        this.lastDragEvent = mouseEvent;
        this.lastDragMs = System.currentTimeMillis();
    }

    @Override
    public void update(float floatDt, SceneGraph sceneGraph) {
        double dt = (double) floatDt;

        for (Body body : this.bodies) {
            if (body != this.bodyDragging)
                body.tick(dt);
        }
    }

    @Override
    public void draw(Kiosk sketch) {
        sketch.background(100);

        sketch.rectMode(PConstants.CORNER);

        sketch.fill(255, 0, 0);
        for (Body body : this.bodies) {
            sketch.rect((int) body.x, (int) body.y, body.width, body.height);
        }
    }



    private class Body {

        private static final double FRICION = 0;

        private double x = 0;
        private double y = 0;
        private double vx = 0;
        private double vy = 0;
        private int width = 0;
        private int height = 0;
        private boolean anchored = false;

        private void tick(double dt) {
            double friction = FRICION * dt;

            if (vx != 0) {
                if (Math.abs(vx) < friction) {
                    vx = 0;
                } else if (vx > 0) {
                    vx -= friction;
                } else {
                    vx += friction;
                }
                x += vx * dt;
            }

            if (vy != 0) {
                if (Math.abs(vy) < friction) {
                    vy = 0;
                } else if (vy > 0) {
                    vy -= friction;
                } else {
                    vy += friction;
                }
                y += vy * dt;
            }
        }
    }

    private class ElasticBand {
        private Body head;
        private Body tail;

        public ElasticBand(Body head, Body tail) {
            this.head = head;
            this.tail = tail;
        }
    }
}
