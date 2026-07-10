package com.alchemist.gamewindow;

import com.alchemist.world.*;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {
    private final int WIDTH = 800;
    private final int HEIGHT = 600;
    Player player = new Player(WIDTH/2, HEIGHT/2);

    Room cruel = new Cruel("Cruel_Room");
    Room deceit = new Deceit("Deceit_Room");
    Room honest = new Honest("Honest_Room");
    Room kind = new Kind("Kind_Room");
    Room[] rooms = { cruel, deceit, honest, kind };

    Ally ally1 = new Ally("Sana", 400, 150);
    Ally ally2 = new Ally("Teo", 400, 450);
    Ally[] allies = { ally1, ally2 };

    MoralityScore score = new MoralityScore();
    PlayerHistory history = new PlayerHistory();

    boolean consequenceTriggered = false;
    boolean rewardTriggered = false;

    String activeMessage = "Walk into a colored room to trigger a scenario.";

    @Override
    public void start(Stage stage) throws Exception {
        setupRooms();

        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        canvas.setFocusTraversable(true);

        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        stage.setTitle("THREAD BOUND");
        stage.setScene(scene);
        stage.show();

        Platform.runLater(canvas::requestFocus);

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

    private void setupRooms() {
        cruel.setPosition(0, 0);
        cruel.setSize(200, 200);
        cruel.addScenario(new Scenario("cruel_1",
                "You threaten a witness to get information faster.", 1, 3, 1));

        deceit.setPosition(600, 0);
        deceit.setSize(200, 200);
        deceit.addScenario(new Scenario("deceit_1",
                "You lie to a grieving companion to spare them pain.", 2, 2, 2));

        honest.setPosition(0, 400);
        honest.setSize(200, 200);
        honest.addScenario(new Scenario("honest_1",
                "You admit your plan failed because of your own mistake.", 2, 2, 3));

        kind.setPosition(600, 400);
        kind.setSize(200, 200);
        kind.addScenario(new Scenario("kind_1",
                "You share your rations with a struggling village.", 3, 2, 2));
    }

    private void render(GraphicsContext gc){
        gc.setFill(Color.BLACK);
        gc.fillRect(0,0,WIDTH,HEIGHT);

        for (Room r : rooms) r.draw(gc);
        for (Ally a : allies) a.draw(gc);
        player.draw(gc);

        gc.setFill(Color.WHITE);
        gc.setFont(new Font(16));
        gc.fillText(activeMessage, 20, 550, 760);
        gc.fillText("Kind:" + score.getScore(MoralityScore.Type.KIND) +
                "  Cruel:" + score.getScore(MoralityScore.Type.CRUEL) +
                "  Deceit:" + score.getScore(MoralityScore.Type.DECEIT) +
                "  Honest:" + score.getScore(MoralityScore.Type.HONEST), 20, 20);
    }

    private void update(){
        player.update();
        checkRoomTriggers();
        checkAllies();
        checkThresholds();
    }

    private void checkRoomTriggers() {
        for (Room r : rooms) {
            if (!r.isTriggered() && r.contains(player.x, player.y)) {
                r.markTriggered();
                Scenario s = r.getScenarios().get(0);
                score.apply(r.getType(), s);
                history.record(r.getType(), s);
                activeMessage = r.getName() + ": " + s.getDescription() +
                        " (severity " + s.getSeverity() + ")";
            }
        }
    }

    private void checkAllies() {
        for (Ally a : allies) {
            boolean inside = a.contains(player.x, player.y);
            if (inside && !a.wasInsideLastFrame()) {
                activeMessage = a.react(score);
            }
            a.setWasInside(inside);
        }
    }

    private void checkThresholds() {
        int bad = score.getScore(MoralityScore.Type.CRUEL) + score.getScore(MoralityScore.Type.DECEIT);
        int good = score.getScore(MoralityScore.Type.KIND) + score.getScore(MoralityScore.Type.HONEST);

        if (!consequenceTriggered && bad >= 5) {
            consequenceTriggered = true;
            player.speed = 1.5;
            activeMessage = "Your past actions catch up with you. The road ahead feels heavier.";
        }

        if (!rewardTriggered && good >= 5) {
            rewardTriggered = true;
            player.speed = 4.5;
            activeMessage = "Word of your kindness has spread. You feel lighter, stronger.";
        }
    }

    public static void main(String[] args){
        launch(args);
    }
}