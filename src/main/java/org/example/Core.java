package org.example;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Core {
    private long window;
    private int WIDTH = 800;
    private int HEIGHT = 600;
    private List<Object> objects;
    private Renderer renderer;
    private InputManager inputManager;

    private void run(){
        init();
        loop();

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Налаштування обробника помилок
        GLFWErrorCallback.createPrint(System.err).set();

        // Ініціалізація GLFW
        if (!glfwInit())
            throw new IllegalStateException("Помилка ініціалізації GLFW");

        // Налаштування параметрів GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        // Створення вікна
        window = glfwCreateWindow(WIDTH, HEIGHT, "Аналізатор з ефектом освітлення", NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Помилка створення вікна GLFW");

        // Налаштування колбека зміни розміру вікна
        glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            WIDTH = width;
            HEIGHT = height;
            glViewport(0, 0, width, height);
        });

        // Отримуємо стек для керування пам'яттю
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            // Отримуємо розмір вікна
            glfwGetWindowSize(window, pWidth, pHeight);

            // Отримуємо роздільну здатність основного монітора
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Центруємо вікно
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        }

        // Робимо контекст OpenGL поточним
        glfwMakeContextCurrent(window);
        // Увімкнення вертикальної синхронізації
        glfwSwapInterval(1);
        // Показуємо вікно
        glfwShowWindow(window);

        // Створюємо можливості OpenGL
        GL.createCapabilities();

        // Увімкнення підтримки прозорості (для ефекту світіння)
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // Ініціалізація об'єктів
        initObjects();

        renderer = new Renderer(objects);
        inputManager = new InputManager(window, objects);
    }

    private void initObjects() {
        // Створюємо кілька полігонів
        objects = new ArrayList<>(Arrays.asList(
                // Основний прямокутник
                new Object(
                        0.8f,
                        0.0f,
                        Arrays.asList(
                                new Point(-0.3f, -0.3f),
                                new Point(0.3f, -0.3f),
                                new Point(0.3f, 0.3f),
                                new Point(-0.3f, 0.3f)
                        ),
                        Object.ObjectType.POLYGON
                ),
                new Object(
                        -0.6f,
                        0.4f,
                        Arrays.asList(
                                new Point(-0.2f, -0.2f),
                                new Point(0.2f, -0.2f),
                                new Point(0.2f, 0.2f),
                                new Point(-0.2f, 0.2f)
                        ),
                        Object.ObjectType.POLYGON
                ),
                // Світло
                new Object(
                        0.0f,
                        0.0f,
                        Arrays.asList(
                                new Point(0.0f, 0.0f),  // Центр
                                new Point(0.05f, 0.0f)   // Радіус
                        ),
                        Object.ObjectType.CIRCLE
                )
        ));
    }

    private void loop() {
        // Основний цикл рендерингу
        while (!glfwWindowShouldClose(window)) {
            // Обробка вводу
            inputManager.callBack();

            // Оновлення viewport та матриці проєкції
            updateViewport();

            // Малюємо об'єкти з ефектом освітлення
            renderer.drawObjects(objects);

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void updateViewport() {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();

        float aspectRatio = (float) WIDTH / HEIGHT;
        if (WIDTH > HEIGHT) {
            // Широке вікно
            glOrtho(-aspectRatio, aspectRatio, -1.0f, 1.0f, -1.0f, 1.0f);
        } else {
            // Високе вікно
            glOrtho(-1.0f, 1.0f, -1.0f / aspectRatio, 1.0f / aspectRatio, -1.0f, 1.0f);
        }

        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
    }

    public static void main(String[] args) {
        new Core().run();
    }
}
