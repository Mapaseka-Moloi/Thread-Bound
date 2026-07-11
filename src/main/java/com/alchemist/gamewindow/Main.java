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
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class Main extends Application {
    private final int WIDTH = 800;
    private final int HEIGHT = 600;

    // game states
    private enum GameState { WORLD, IN_ROOM, CHOOSING, GAME_OVER }
    private GameState state = GameState.WORLD;

    Player player = new Player(WIDTH / 2, HEIGHT / 2);

    Room cruelRoom = new Cruel("Room_1");
    Room deceitRoom = new Deceit("Room_2");
    Room honestRoom = new Honest("Room_3");
    Room kindRoom = new Kind("Room_4");
    Room[] rooms = {cruelRoom, deceitRoom, honestRoom, kindRoom};

    Ally ally1 = new Ally("Sana", 400, 150);
    Ally ally2 = new Ally("Teo", 400, 450);
    Ally[] allies = {ally1, ally2};

    MoralityScore score = new MoralityScore();
    PlayerHistory history = new PlayerHistory();

    boolean consequenceTriggered = false;
    boolean rewardTriggered = false;

    Room activeRoom = null;
    int roomStep = 0;
    String activeMessage = "Walk into a room to face a choice.";
    String endingText = "";

    // room entry â€” player position inside expanded room
    double roomPlayerX = 80;
    double roomPlayerY = HEIGHT / 2.0;
    boolean crossedRiver = false;

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
            if (k == KeyCode.SPACE) {
                if (state == GameState.IN_ROOM) player.jump();
            }
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
                "N = Hand over your real incomplete papers",
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

    private void update() {
        if (state == GameState.GAME_OVER) return;

        if (state == GameState.WORLD) {
            player.update();
            checkRoomEntry();
            checkAllies();
            checkThresholds();
            checkEnding();
        } else if (state == GameState.IN_ROOM) {
            updateRoomMovement();
        }
    }

    private void updateRoomMovement() {
        if (player.left) roomPlayerX -= player.speed;
        if (player.right) roomPlayerX += player.speed;
        if (player.up) roomPlayerY -= player.speed;
        if (player.down) roomPlayerY += player.speed;

        roomPlayerX = Math.clamp(roomPlayerX, 20, WIDTH - 20);
        roomPlayerY = Math.clamp(roomPlayerY, 20, HEIGHT - 20);

        player.update();

        // block river unless jumping
        if (activeRoom != null && activeRoom.playerOnRiver(roomPlayerX, roomPlayerY)) {
            if (!player.isJumping) {
                roomPlayerX -= player.speed * 2;
            }
        }

        // mark crossed once past the river
        if (activeRoom != null && roomPlayerX > activeRoom.getRiverX() + activeRoom.getRiverW() + 20) {
            crossedRiver = true;
        }

        // near NPC and crossed river â€” show choice
        if (crossedRiver && activeRoom != null && activeRoom.playerNearNPC(roomPlayerX, roomPlayerY)) {
            if (!activeRoom.isTriggered()) {
                state = GameState.CHOOSING;
                roomStep = 0;
            }
        }
    }

    private void checkRoomEntry() {
        for (Room r : rooms) {
            if (!r.isTriggered() && r.contains(player.x, player.y)) {
                activeRoom = r;
                crossedRiver = false;
                roomPlayerX = 60;
                roomPlayerY = HEIGHT / 2.0;
                player.up = false;
                player.down = false;
                player.left = false;
                player.right = false;
                state = GameState.IN_ROOM;
                activeMessage = "Cross the river with SPACE, then reach the figure.";
                return;
            }
        }
    }

    private void chooseOption(boolean pickedYes) {
        if (state != GameState.CHOOSING) return;
        if (activeRoom == null) return;

        Scenario s = activeRoom.getScenarios().get(roomStep);
        MoralityScore.Type resultType = pickedYes
                ? activeRoom.getType()
                : oppositeOf(activeRoom.getType());

        score.apply(resultType, s);
        history.record(resultType, s);

        roomStep++;
        if (roomStep >= activeRoom.getScenarios().size()) {
            activeRoom.markTriggered();
            activeRoom = null;
            state = GameState.WORLD;
            activeMessage = "You made your choice.";
        } else {
            // another scenario in this room â€” stay in choosing state
            activeMessage = "Another moment unfolds...";
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

    private void render(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        if (state == GameState.GAME_OVER) {
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Georgia", FontWeight.BOLD, 20));
            gc.fillText(endingText, 40, 220, 720);
            return;
        }

        if (state == GameState.WORLD) {
            renderWorld(gc);
        } else if (state == GameState.IN_ROOM || state == GameState.CHOOSING) {
            renderRoom(gc);
        }
    }

    private void renderWorld(GraphicsContext gc) {
        for (Room r : rooms) r.draw(gc);
        for (Ally a : allies) a.draw(gc);
        player.draw(gc);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 16));
        gc.fillText(activeMessage, 20, 550, 760);
    }

    private void renderRoom(GraphicsContext gc) {
        activeRoom.drawExpanded(gc, WIDTH, HEIGHT);

        // draw player inside room
        gc.setFill(Color.CYAN);
        gc.fillPolygon(
                new double[]{roomPlayerX, roomPlayerX - 12, roomPlayerX + 12},
                new double[]{roomPlayerY - 16 + player.getZOffset(),
                        roomPlayerY + 8 + player.getZOffset(),
                        roomPlayerY + 8 + player.getZOffset()},
                3
        );

        // shadow when jumping
        if (player.isJumping) {
            gc.setFill(Color.color(0, 0, 0, 0.3));
            gc.fillOval(roomPlayerX - 10, roomPlayerY + 5, 20, 8);
        }

        // river label
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 13));
        gc.fillText("SPACE to jump", activeRoom.getRiverX() - 10, activeRoom.getRiverY() - 10);

        if (state == GameState.CHOOSING) {
            Scenario s = activeRoom.getScenarios().get(roomStep);
            // dark overlay for readability
            gc.setFill(Color.color(0, 0, 0, 0.65));
            gc.fillRect(0, 450, WIDTH, 150);

            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Georgia", FontWeight.BOLD, 16));
            gc.fillText(s.getDescription(), 30, 480, 740);
            gc.setFont(Font.font("Arial", 15));
            gc.fillText(s.getYesChoiceText(), 30, 510, 740);
            gc.fillText(s.getNoChoiceText(), 30, 535, 740);
        } else {
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", 14));
            gc.fillText(activeMessage, 20, 580, 760);
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
            if (!r.isTriggered()) { allDone = false; break; }
        }
        if (allDone && state != GameState.GAME_OVER) {
            state = GameState.GAME_OVER;
            MoralityScore.Type dominant = score.getDominantType();
            endingText = "Your journey is over.\n\nLooking back at everything you did,\nthe thread that binds you most is:\n\n"
                    + dominant
                    + "\n\nKind: " + score.getScore(MoralityScore.Type.KIND)
                    + "   Cruel: " + score.getScore(MoralityScore.Type.CRUEL)
                    + "   Deceit: " + score.getScore(MoralityScore.Type.DECEIT)
                    + "   Honest: " + score.getScore(MoralityScore.Type.HONEST);
        }
    }

    private void checkRoomTriggers() {}

    public static void main(String[] args) {
        launch(args);
    }
}