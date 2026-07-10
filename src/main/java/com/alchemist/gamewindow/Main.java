package com.alchemist.gamewindow;

import com.alchemist.world.*;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {
    private final int WIDTH = 800;
    private final int HEIGHT = 600;
    Player player = new Player(WIDTH/2, HEIGHT/2);
    Room cruel = new Cruel("Cruel_Room");
    Room deceit = new Deceit("Deceit_Room");
    Room honest = new Honest("Honest_Room");
    Room kind = new Kind("Kind_Room");

    @Override
    public void start(Stage stage) throws Exception {
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        stage.setTitle("THREAD BOUND");
        stage.setScene(scene);
        stage.show();

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.W) player.up = true;
            if (e.getCode() == KeyCode.S) player.down = true;
            if (e.getCode() == KeyCode.A) player.left = true;
            if (e.getCode() == KeyCode.D) player.right = true;
        });

        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.W) player.up = false;
            if (e.getCode() == KeyCode.S) player.down = false;
            if (e.getCode() == KeyCode.A) player.left = false;
            if (e.getCode() == KeyCode.D) player.right = false;
        });

        AnimationTimer gameloop = new AnimationTimer() {
            @Override
            public void handle(long l) {
                update();
                render(gc);
            }
        };
        gameloop.start();

    }
    private void render(GraphicsContext gc){
        gc.setFill(Color.BLACK);
        gc.fillRect(0,0,WIDTH,HEIGHT);
        player.draw(gc);
        cruel.draw(gc);
        deceit.draw(gc);
        honest.draw(gc);
        kind.draw(gc);
    }
    private void update(){
        player.update();
    }

    public static void main(String[] args){
        launch(args);
    }
}
