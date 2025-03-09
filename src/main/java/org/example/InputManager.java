package org.example;

import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class InputManager {
    private long window;
    private List<Object> objects;
    private Object character;

    // Швидкість руху персонажа
    private final float MOVEMENT_SPEED = 0.01f;

    // Флаги для відстеження натиснутих клавіш
    private boolean keyW = false;
    private boolean keyA = false;
    private boolean keyS = false;
    private boolean keyD = false;

    public InputManager(long window, List<Object> objects) {
        this.window = window;
        this.objects = objects;

        initCharacter(objects);
        setupKeyCallbacks();
    }

    private void initCharacter(List<Object> objects) {
        // Знаходимо об'єкт, що відповідає персонажу (коло)
        for (Object object : objects) {
            if (object.getType() == Object.ObjectType.CIRCLE) {
                character = object;
            }
        }
    }

    private void setupKeyCallbacks() {
        // Встановлюємо колбек для відстеження натискання та відпускання клавіш
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            // Оновлюємо стан флагів при натисканні або відпусканні
            if (key == GLFW_KEY_W) {
                keyW = (action == GLFW_PRESS || action == GLFW_REPEAT);
            }
            if (key == GLFW_KEY_A) {
                keyA = (action == GLFW_PRESS || action == GLFW_REPEAT);
            }
            if (key == GLFW_KEY_S) {
                keyS = (action == GLFW_PRESS || action == GLFW_REPEAT);
            }
            if (key == GLFW_KEY_D) {
                keyD = (action == GLFW_PRESS || action == GLFW_REPEAT);
            }

            // Клавіша ESC для виходу
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true);
            }
        });
    }

    public void callBack() {
        // Обробляємо рух на основі стану флагів
        updateMovement();
    }

    private void updateMovement() {
        // Застосовуємо рух у всіх активних напрямках
        if (keyW) {
            character.setyPos(character.getyPos() + MOVEMENT_SPEED);
        }
        if (keyS) {
            character.setyPos(character.getyPos() - MOVEMENT_SPEED);
        }
        if (keyA) {
            character.setxPos(character.getxPos() - MOVEMENT_SPEED);
        }
        if (keyD) {
            character.setxPos(character.getxPos() + MOVEMENT_SPEED);
        }
    }
}