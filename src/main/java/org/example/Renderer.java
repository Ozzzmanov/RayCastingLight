package org.example;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {
    private List<Object> objects;
    private Intersection intersection;
    private static final int LIGHT_SEGMENTS = 1080; // Кількість сегментів для ефекту світла

    public Renderer(List<Object> objects) {
        this.objects = objects;
        this.intersection = new Intersection();
    }

    // Очищення екрану
    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void drawObjects(List<Object> objects) {
        // Встановлення чорного фону
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Спочатку малюємо освітлення для кола
        for (Object object : objects) {
            if (object.getType() == Object.ObjectType.CIRCLE) {
                drawLightEffect(object);
            }
        }

        // Потім малюємо всі об'єкти
        for (Object object : objects) {
            glPushMatrix();
            glTranslatef(object.getxPos(), object.getyPos(), 0.0f);

            switch (object.getType()) {
                case POLYGON:
                    glColor3f(0.0f, 0.0f, 0.5f); // Темно-синій колір для полігонів
                    drawPolygon(object);
                    break;
                case CIRCLE:
                    glColor3f(0.8f, 0.8f, 0.0f); // Яскравий колір для кола
                    drawCircle(object, true); // Не малюємо окремо промені
                    break;
            }

            glPopMatrix();
        }
    }

    // Малювання полігону
    private void drawPolygon(Object object) {
        glBegin(GL_POLYGON);
        for (Point point : object.getVertices()) {
            glVertex2f(point.getX(), point.getY());
        }
        glEnd();
    }

    // Малювання кола
    public void drawCircle(Object object, boolean drawRays) {
        if (object.getVertices().size() >= 2) {
            Point center = object.getVertices().get(0);
            Point radiusPoint = object.getVertices().get(1);

            // Обчислення радіуса
            float radius = (float) Math.sqrt(
                    Math.pow(radiusPoint.getX() - center.getX(), 2) +
                            Math.pow(radiusPoint.getY() - center.getY(), 2)
            );

            // Малювання кола
            glBegin(GL_TRIANGLE_FAN);
            glVertex2f(center.getX(), center.getY());

            int segments = 32; // Кількість сегментів для кола
            for (int i = 0; i <= segments; i++) {
                float angle = (float) (2.0f * Math.PI * i / segments);
                float x = center.getX() + (float) Math.cos(angle) * radius;
                float y = center.getY() + (float) Math.sin(angle) * radius;
                glVertex2f(x, y);
            }
            glEnd();

            if (drawRays) {
                drawDebugRays(center, radius, object.getxPos(), object.getyPos());
            }
        }
    }

    // Малювання ефекту освітлення
    private void drawLightEffect(Object object) {
        if (object.getVertices().size() >= 2) {
            Point localCenter = object.getVertices().get(0);
            Point radiusPoint = object.getVertices().get(1);

            // Обчислення радіуса
            float radius = (float) Math.sqrt(
                    Math.pow(radiusPoint.getX() - localCenter.getX(), 2) +
                            Math.pow(radiusPoint.getY() - localCenter.getY(), 2)
            );

            float globalCenterX = localCenter.getX() + object.getxPos();
            float globalCenterY = localCenter.getY() + object.getyPos();
            float lightLength = 4.0f; // Довжина світлових променів

            List<Point> lightVertices = new ArrayList<>();
            lightVertices.add(new Point(globalCenterX, globalCenterY));

            for (int i = 0; i <= LIGHT_SEGMENTS; i++) {
                float angle = (float) (2.0f * Math.PI * i / LIGHT_SEGMENTS);

                float circleX = globalCenterX + (float) Math.cos(angle) * radius;
                float circleY = globalCenterY + (float) Math.sin(angle) * radius;
                float dirX = (float) Math.cos(angle);
                float dirY = (float) Math.sin(angle);
                float endX = circleX + dirX * lightLength;
                float endY = circleY + dirY * lightLength;

                Point start = new Point(circleX, circleY);
                Point end = new Point(endX, endY);
                Point intersections = intersection.findIntersectionWithPolygons(start, end, objects);

                if (intersections != null) {
                    lightVertices.add(intersections);
                } else {
                    lightVertices.add(end);
                }
            }

            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glBegin(GL_TRIANGLE_FAN);
            glColor4f(0.9f, 0.9f, 0.5f, 0.5f);

            for (Point p : lightVertices) {
                glVertex2f(p.getX(), p.getY());
            }
            glEnd();
            glDisable(GL_BLEND);
        }
    }

    // Відображення променів для налагодження
    private void drawDebugRays(Point localCenter, float radius, float objectX, float objectY) {
        float globalCenterX = localCenter.getX();
        float globalCenterY = localCenter.getY();
        float lightLength = 2.0f;

        glColor3f(1.0f, 0.0f, 0.0f);
        glBegin(GL_LINES);

        for (int i = 0; i < LIGHT_SEGMENTS; i++) {
            float angle = (float) (2.0f * Math.PI * i / LIGHT_SEGMENTS);

            float circleX = globalCenterX + (float) Math.cos(angle) * radius;
            float circleY = globalCenterY + (float) Math.sin(angle) * radius;
            float dirX = (float) Math.cos(angle);
            float dirY = (float) Math.sin(angle);
            float endX = circleX + dirX * lightLength;
            float endY = circleY + dirY * lightLength;

            Point adjustedStart = new Point(circleX + objectX, circleY + objectY);
            Point adjustedEnd = new Point(endX + objectX, endY + objectY);
            Point intersections = intersection.findIntersectionWithPolygons(adjustedStart, adjustedEnd, objects);

            glVertex2f(circleX, circleY);
            if (intersections != null) {

                glVertex2f(intersections.getX() - objectX, intersections.getY() - objectY);
            } else {
                glVertex2f(endX, endY);
            }
        }
        glEnd();
    }
}
