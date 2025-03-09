package org.example;

import java.util.List;

public class Object {
    private float xPos, yPos;
    private List<Point> vertices;
    private ObjectType type;

    public enum ObjectType {
        POLYGON,
        CIRCLE

    }

    public Object(float x, float y, List<Point> vertices, ObjectType type) {
        this.xPos = x;
        this.yPos = y;
        this.vertices = vertices;
        this.type = type;
    }

    public float getxPos() {
        return xPos;
    }

    public float getyPos() {
        return yPos;
    }

    public List<Point> getVertices() {
        return vertices;
    }

    public ObjectType getType() {
        return type;
    }

    public void setxPos(float xPos) {
        this.xPos = xPos;
    }

    public void setyPos(float yPos) {
        this.yPos = yPos;
    }
}
