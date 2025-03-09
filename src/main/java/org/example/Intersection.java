package org.example;

import java.util.ArrayList;
import java.util.List;

public class Intersection {

    /**
     * Знаходить найближчу точку перетину відрізка з полігонами
     *
     * @param start   початкова точка відрізка
     * @param end     кінцева точка відрізка
     * @param objects список об'єктів сцени
     * @return найближча точка перетину або null, якщо перетинів немає
     */
    public Point findIntersectionWithPolygons(Point start, Point end, List<Object> objects) {
        Point closestIntersection = null;
        float closestDistance = Float.MAX_VALUE;

        // Перевіряємо перетин з кожним полігоном
        for (Object object : objects) {
            if (object.getType() == Object.ObjectType.POLYGON) {
                // Перетворюємо вершини полігона в глобальні координати
                List<Point> globalVertices = new ArrayList<>();
                for (Point p : object.getVertices()) {
                    globalVertices.add(new Point(
                            p.getX() + object.getxPos(),
                            p.getY() + object.getyPos()
                    ));
                }

                // Перевіряємо перетин з кожною стороною полігона
                int n = globalVertices.size();
                for (int i = 0; i < n; i++) {
                    Point p1 = globalVertices.get(i);
                    Point p2 = globalVertices.get((i + 1) % n);

                    Point intersection = lineIntersection(start, end, p1, p2);
                    if (intersection != null) {
                        // Обчислюємо відстань від початку променя до точки перетину
                        float dx = intersection.getX() - start.getX();
                        float dy = intersection.getY() - start.getY();
                        float distance = (float) Math.sqrt(dx * dx + dy * dy);

                        // Зберігаємо найближче перетинання
                        if (distance < closestDistance) {
                            closestDistance = distance;
                            closestIntersection = intersection;
                        }
                    }
                }
            }
        }

        return closestIntersection;
    }

    /**
     * Метод для знаходження точки перетину двох відрізків
     *
     * @param a1 початок першого відрізка
     * @param a2 кінець першого відрізка
     * @param b1 початок другого відрізка
     * @param b2 кінець другого відрізка
     * @return точка перетину або null, якщо перетину немає
     */
    private Point lineIntersection(Point a1, Point a2, Point b1, Point b2) {
        float x1 = a1.getX(), y1 = a1.getY();
        float x2 = a2.getX(), y2 = a2.getY();
        float x3 = b1.getX(), y3 = b1.getY();
        float x4 = b2.getX(), y4 = b2.getY();

        // Обчислюємо визначник
        float det = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        if (Math.abs(det) < 1e-6) {
            return null; // Лінії паралельні або збігаються
        }

        // Обчислюємо параметри t і u
        float t = ((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)) / det;
        float u = -((x1 - x2) * (y1 - y3) - (y1 - y2) * (x1 - x3)) / det;

        // Перевіряємо, чи знаходиться точка перетину на обох відрізках
        if (t >= 0 && t <= 1 && u >= 0 && u <= 1) {
            // Обчислюємо координати точки перетину
            float intersectX = x1 + t * (x2 - x1);
            float intersectY = y1 + t * (y2 - y1);
            return new Point(intersectX, intersectY);
        }

        return null; // Перетину на відрізках немає
    }
}