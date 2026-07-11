package com.alchemist.gamewindow;

import com.alchemist.world.*;
import com.alchemist.world.Direction;
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
    private Direction currentDirection = Direction.NONE;


    Room cruelRoom = new Cruel("Room_1");
    Room deceitRoom = new Deceit("Room_2");
    Room honestRoom = new Honest("Room_3");
    Room kindRoom = new Kind("Room_4");
    Room[] rooms = { cruelRoom, deceitRoom, honestRoom, kindRoom };

    Ally ally1 = new Ally("Sana", 400, 150);
    Ally ally2 = new Ally("Teo", 400, 450);
    Ally[] allies = { ally1, ally2 };

    MoralityScore score = new MoralityScore();
    PlayerHistory history = new PlayerHistory();

    boolean consequenceTriggered = false;
    boolean rewardTriggered = false;
    boolean gameOver = false;

    Room activeRoom = null;
    int roomStep = 0;
    String activeMessage = "Walk into a room to face a choice.";
    String endingText = "";




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
            KeyCode k = e.getCode();
            if (k == KeyCode.W || k == KeyCode.UP) player.up = true;
            if (k == KeyCode.S || k == KeyCode.DOWN) player.down = true;
            if (k == KeyCode.A || k == KeyCode.LEFT) player.left = true;
            if (k == KeyCode.D || k == KeyCode.RIGHT) player.right = true;
            if (k == KeyCode.Y) chooseOption(true);
            if (k == KeyCode.N) chooseOption(false);
        });

        scene.setOnKeyReleased(e -> {
            KeyCode k = e.getCode();
            if (k == KeyCode.W || k == KeyCode.UP) player.up = false;
            if (k == KeyCode.S || k == KeyCode.DOWN) player.down = false;
            if (k == KeyCode.A || k == KeyCode.LEFT) player.left = false;
            if (k == KeyCode.D || k == KeyCode.RIGHT) player.right = false;
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
        cruelRoom.setPosition(0, 0);
        cruelRoom.setSize(200, 200);
        cruelRoom.addScenario(new Scenario("cruel_1",
                "A witness is stalling. You need answers now.",
                "Y = Threaten them until they talk",
                "N = Wait patiently for them to speak",
                1, 3, 1));
        cruelRoom.addScenario(new Scenario("cruel_2",
                "A rival is cornered and defenseless.",
                "Y = Strike while you have the chance",
                "N = Let them go",
                2, 3, 1));

        deceitRoom.setPosition(600, 0);
        deceitRoom.setSize(200, 200);
        deceitRoom.addScenario(new Scenario("deceit_1",
                "Your friend asks how their sibling really died.",
                "Y = Tell a comforting lie",
                "N = Tell them the painful truth",
                2, 2, 2));
        deceitRoom.addScenario(new Scenario("deceit_2",
                "A guard asks for your papers at the checkpoint.",
                "Y = Hand over forged papers",
                "N = Hand over your real, incomplete papers",
                1, 2, 1));

        honestRoom.setPosition(0, 400);
        honestRoom.setSize(200, 200);
        honestRoom.addScenario(new Scenario("honest_1",
                "Your plan failed. The team is asking what happened.",
                "Y = Admit it was your mistake",
                "N = Let them believe it was bad luck",
                2, 2, 3));
        honestRoom.addScenario(new Scenario("honest_2",
                "A stranger asks if the road ahead is safe.",
                "Y = Warn them about the danger",
                "N = Say nothing and let them find out",
                1, 1, 1));

        kindRoom.setPosition(600, 400);
        kindRoom.setSize(200, 200);
        kindRoom.addScenario(new Scenario("kind_1",
                "A beggar asks for food outside the village.",
                "Y = Share your rations with them",
                "N = Walk past and keep your food",
                3, 2, 2));
        kindRoom.addScenario(new Scenario("kind_2",
                "An old enemy is injured and begging for help.",
                "Y = Stop and help them",
                "N = Leave them behind",
                2, 2, 3));
    }

    private void render(GraphicsContext gc){
        gc.setFill(Color.BLACK);
        gc.fillRect(0,0,WIDTH,HEIGHT);

        if (gameOver) {
            gc.setFill(Color.WHITE);
            gc.setFont(new Font(20));
            gc.fillText(endingText, 40, 250, 720);
            return;
        }

        for (Room r : rooms) r.draw(gc);
        for (Ally a : allies) a.draw(gc);
        player.draw(gc);

        double compassX = 400;
        double compassY = 60;
        double radius = 22;

        gc.setLineWidth(2);
        gc.setStroke(Color.DARKGRAY);
        gc.strokeOval(compassX - radius, compassY - radius, radius * 2, radius * 2);

        gc.setFont(new Font("Arial", 12));
        gc.setFill(Color.GRAY);
        gc.fillText("N", compassX - 4, compassY - radius - 4);
        gc.fillText("S", compassX - 4, compassY + radius + 12);
        gc.fillText("W", compassX - radius - 14, compassY + 4);
        gc.fillText("E", compassX + radius + 4, compassY + 4);

        double targetX = compassX;
        double targetY = compassY;

        if (currentDirection == Direction.NORTH) targetY -= radius - 5;
        else if (currentDirection == Direction.SOUTH) targetY += radius - 5;
        else if (currentDirection == Direction.WEST)  targetX -= radius - 5;
        else if (currentDirection == Direction.EAST)  targetX += radius - 5;

        // Draw compass indicator (Green vector pointing to moving direction)
        if (currentDirection != Direction.NONE) {
            gc.setStroke(Color.LIGHTGREEN);
            gc.setLineWidth(3);
            gc.strokeLine(compassX, compassY, targetX, targetY);

            // Draw a small arrow indicator tip at target point
            gc.setFill(Color.LIGHTGREEN);
            gc.fillOval(targetX - 3, targetY - 3, 6, 6);
        } else {
            // Draw dead-center placeholder dot when idle
            gc.setFill(Color.DARKGRAY);
            gc.fillOval(compassX - 3, compassY - 3, 6, 6);
        }

        // Output matching text tracker right below UI element
        gc.setFill(Color.LIGHTGREEN);
        gc.setFont(new Font("Arial", 14));
        gc.fillText("Heading: " + currentDirection, 20, 100);


        gc.setFill(Color.WHITE);
        gc.setFont(new Font(16));

        if (activeRoom != null) {
            Scenario s = activeRoom.getScenarios().get(roomStep);
            gc.fillText(s.getDescription(), 20, 500, 760);
            gc.fillText(s.getYesChoiceText(), 20, 525, 760);
            gc.fillText(s.getNoChoiceText(), 20, 550, 760);
        } else {
            gc.fillText(activeMessage, 20, 550, 760);
        }
    }



    private void update(){
        if (gameOver) return;
        player.update();
        calculateDirection();
        checkRoomTriggers();
        checkAllies();
        checkThresholds();
        checkEnding();
    }


    private void calculateDirection() {
        if (player.up) {
            currentDirection = Direction.NORTH;
        } else if (player.down) {
            currentDirection = Direction.SOUTH;
        } else if (player.left) {
            currentDirection = Direction.WEST;
        } else if (player.right) {
            currentDirection = Direction.EAST;
        } else {
            currentDirection = Direction.NONE;
        }
    }



    private void checkRoomTriggers() {
        if (activeRoom != null) return;
        for (Room r : rooms) {
            if (!r.isTriggered() && r.contains(player.x, player.y)) {
                activeRoom = r;
                roomStep = 0;
            }
        }
    }

    private void chooseOption(boolean pickedYes) {
        if (activeRoom == null) return;

        Scenario s = activeRoom.getScenarios().get(roomStep);
        MoralityScore.Type resultType = pickedYes ? activeRoom.getType() : oppositeOf(activeRoom.getType());

        score.apply(resultType, s);
        history.record(resultType, s);
        activeMessage = "You made your choice.";

        roomStep++;
        if (roomStep >= activeRoom.getScenarios().size()) {
            activeRoom.markTriggered();
            activeRoom = null;
        }
    }

    private MoralityScore.Type oppositeOf(MoralityScore.Type t) {
        switch (t) {
            case CRUEL: return MoralityScore.Type.HONEST;
            case DECEIT: return MoralityScore.Type.HONEST;
            case HONEST: return MoralityScore.Type.CRUEL;
            case KIND: return MoralityScore.Type.CRUEL;
            default: return t;
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

        if (!consequenceTriggered && bad >= 6) {
            consequenceTriggered = true;
            player.speed = 1.5;
            activeMessage = "Something in you feels heavier than before.";
        }

        if (!rewardTriggered && good >= 6) {
            rewardTriggered = true;
            player.speed = 4.5;
            activeMessage = "Something in you feels lighter than before.";
        }
    }

    private void checkEnding() {
        boolean allDone = true;
        for (Room r : rooms) {
            if (!r.isTriggered()) allDone = false;
        }
        if (allDone && !gameOver) {
            gameOver = true;
            MoralityScore.Type dominant = score.getDominantType();
            endingText = "Your journey is over.\n\nLooking back at everything you did,\nthe thread that binds you most is:\n\n" + dominant + "\n\nKind: " + score.getScore(MoralityScore.Type.KIND) +
                    "   Cruel: " + score.getScore(MoralityScore.Type.CRUEL) +
                    "   Deceit: " + score.getScore(MoralityScore.Type.DECEIT) +
                    "   Honest: " + score.getScore(MoralityScore.Type.HONEST);
        }
    }

    public static void main(String[] args){
        launch(args);
    }
}