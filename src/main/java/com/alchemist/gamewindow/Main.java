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
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Main extends Application {
    private final int WIDTH = 800;
    private final int HEIGHT = 600;

    // game states
    private enum GameState {
        INTRO, NAME_ENTRY, NAME_CONFIRM, AVATAR_SELECT, HOW_TO_PLAY,
        WORLD, IN_ROOM, CHOOSING, ALLY_SCENE, GAME_OVER
    }
    private GameState state = GameState.INTRO;

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

    // room entry — player position inside expanded room
    double roomPlayerX = 80;
    double roomPlayerY = HEIGHT / 2.0;
    boolean crossedRiver = false;

    // --- intro flow state ---
    String playerName = "";
    String[] avatarNames = {"Cyan Drifter", "Ember Wanderer", "Verdant Roamer", "Violet Seeker"};
    Color[] avatarColors = {Color.CYAN, Color.web("#ff7043"), Color.web("#66bb6a"), Color.web("#ab47bc")};
    int avatarIndex = 0;
    long nameConfirmStartTick = 0;

    // --- ally scene / bonding state ---
    int roomsCompletedCount = 0;
    boolean allySceneTriggered = false;
    boolean allyOpenedUp = false;
    boolean teoFollowing = false;
    String allySceneText = "";

    // --- world ambience ---
    LinkedList<double[]> playerTrail = new LinkedList<>();
    static final int TRAIL_MAX = 600;
    static final int TEO_FOLLOW_DELAY = 30;
    double[][] worldStars = generateStars();

    long tickCounter = 0;

    private double[][] generateStars() {
        Random rnd = new Random(1337);
        double[][] stars = new double[70][3];
        for (int i = 0; i < stars.length; i++) {
            stars[i][0] = rnd.nextDouble() * WIDTH;
            stars[i][1] = rnd.nextDouble() * HEIGHT;
            stars[i][2] = rnd.nextDouble() * Math.PI * 2;
        }
        return stars;
    }

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

            switch (state) {
                case INTRO:
                    if (k == KeyCode.ENTER || k == KeyCode.SPACE) {
                        state = GameState.NAME_ENTRY;
                    }
                    break;

                case NAME_ENTRY:
                    if (k == KeyCode.BACK_SPACE && playerName.length() > 0) {
                        playerName = playerName.substring(0, playerName.length() - 1);
                    } else if (k == KeyCode.ENTER && !playerName.trim().isEmpty()) {
                        nameConfirmStartTick = tickCounter;
                        state = GameState.NAME_CONFIRM;
                    }
                    break;

                case NAME_CONFIRM:
                    if (k == KeyCode.ENTER || k == KeyCode.SPACE) {
                        state = GameState.AVATAR_SELECT;
                    }
                    break;

                case AVATAR_SELECT:
                    if (k == KeyCode.LEFT || k == KeyCode.A) {
                        avatarIndex = (avatarIndex - 1 + avatarColors.length) % avatarColors.length;
                    }
                    if (k == KeyCode.RIGHT || k == KeyCode.D) {
                        avatarIndex = (avatarIndex + 1) % avatarColors.length;
                    }
                    if (k == KeyCode.ENTER) {
                        player.color = avatarColors[avatarIndex];
                        state = GameState.HOW_TO_PLAY;
                    }
                    break;

                case HOW_TO_PLAY:
                    if (k == KeyCode.ENTER || k == KeyCode.SPACE) {
                        state = GameState.WORLD;
                    }
                    break;

                case WORLD:
                case IN_ROOM:
                    if (k == KeyCode.W || k == KeyCode.UP) player.up = true;
                    if (k == KeyCode.S || k == KeyCode.DOWN) player.down = true;
                    if (k == KeyCode.A || k == KeyCode.LEFT) player.left = true;
                    if (k == KeyCode.D || k == KeyCode.RIGHT) player.right = true;
                    if (k == KeyCode.SPACE && state == GameState.IN_ROOM) player.jump();
                    break;

                case CHOOSING:
                    if (k == KeyCode.Y) chooseOption(true);
                    if (k == KeyCode.N) chooseOption(false);
                    break;

                case ALLY_SCENE:
                    if (k == KeyCode.Y) chooseAllyOption(true);
                    if (k == KeyCode.N) chooseAllyOption(false);
                    break;

                case GAME_OVER:
                    break;
            }
        });

        scene.setOnKeyTyped(e -> {
            if (state != GameState.NAME_ENTRY) return;
            String ch = e.getCharacter();
            if (ch == null || ch.isEmpty()) return;
            char c = ch.charAt(0);
            if ((Character.isLetterOrDigit(c) || c == ' ' || c == '-' || c == '\'')
                    && playerName.length() < 14) {
                playerName += c;
            }
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
        tickCounter++;

        switch (state) {
            case GAME_OVER:
                return;

            case NAME_CONFIRM:
                if (tickCounter - nameConfirmStartTick > 90) {
                    state = GameState.AVATAR_SELECT;
                }
                break;

            case WORLD:
                player.update();
                updateWorldAmbience();
                checkRoomEntry();
                checkAllies();
                checkThresholds();
                checkEnding();
                break;

            case IN_ROOM:
                if (activeRoom != null) activeRoom.updateObstacles(tickCounter);
                updateRoomMovement();
                break;

            default:
                break;
        }
    }

    private void updateWorldAmbience() {
        playerTrail.addFirst(new double[]{player.x, player.y});
        if (playerTrail.size() > TRAIL_MAX) playerTrail.removeLast();

        if (teoFollowing && playerTrail.size() > TEO_FOLLOW_DELAY) {
            double[] target = playerTrail.get(TEO_FOLLOW_DELAY);
            ally2.setPosition(target[0], target[1]);
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

        // patrolling guard sends the player back to the entrance
        if (activeRoom != null && activeRoom.playerHitsGuard(roomPlayerX, roomPlayerY)) {
            roomPlayerX = 60;
            roomPlayerY = HEIGHT / 2.0;
            crossedRiver = false;
            activeMessage = "The guard catches your trail. Back to the start.";
        }

        // mark crossed once past the river
        if (activeRoom != null && roomPlayerX > activeRoom.getRiverX() + activeRoom.getRiverW() + 20) {
            crossedRiver = true;
        }

        // near NPC and crossed river — show choice
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
                activeMessage = "Cross the river with SPACE, then dodge the patrol to reach the figure.";
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
            roomsCompletedCount++;

            if (roomsCompletedCount == 2 && !allySceneTriggered) {
                allySceneTriggered = true;
                allySceneText =
                        "Teo finds you at the edge of the world.\n" +
                                "\"You don't have to carry all of this alone,\" he says.\n" +
                                "Something about the way he says it reminds you of someone -\n" +
                                "someone who said the same thing, right before they left.\n" +
                                "Your first instinct is to pull away.";
                state = GameState.ALLY_SCENE;
            } else {
                int remaining = 0;
                for (Room r : rooms) if (!r.isTriggered()) remaining++;
                state = GameState.WORLD;
                if (remaining > 0) {
                    activeMessage = "Well done, " + playerName + ". "
                            + remaining + " more room" + (remaining == 1 ? "" : "s") + " await.";
                } else {
                    activeMessage = "You made your choice.";
                }
            }
        } else {
            activeMessage = "Another moment unfolds...";
        }
    }

    private void chooseAllyOption(boolean openUp) {
        allyOpenedUp = openUp;

        if (openUp) {
            Scenario bonding = new Scenario("ally_bond", "You let Teo in.", "", "", 1, 1, 1);
            score.apply(MoralityScore.Type.KIND, bonding);
            history.record(MoralityScore.Type.KIND, bonding);
            teoFollowing = true;
            playerTrail.clear();
            activeMessage = "Teo falls into step beside you. It doesn't fix everything. But it helps.";
        } else {
            activeMessage = "Teo nods, like he understands. You shouldn't have to shut people out "
                    + "because of what someone else did - but some habits take longer to unlearn.";
        }

        state = GameState.WORLD;
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

    private Color colorForType(MoralityScore.Type t) {
        switch (t) {
            case CRUEL: return Color.web("#e53935");
            case DECEIT: return Color.web("#ff9800");
            case HONEST: return Color.web("#1565c0");
            case KIND: return Color.web("#4caf50");
            default: return Color.WHITE;
        }
    }

    private void render(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        switch (state) {
            case INTRO: renderIntro(gc); break;
            case NAME_ENTRY: renderNameEntry(gc); break;
            case NAME_CONFIRM: renderNameConfirm(gc); break;
            case AVATAR_SELECT: renderAvatarSelect(gc); break;
            case HOW_TO_PLAY: renderHowToPlay(gc); break;
            case GAME_OVER: renderGameOver(gc); break;
            case WORLD: renderWorld(gc); break;
            case IN_ROOM:
            case CHOOSING: renderRoom(gc); break;
            case ALLY_SCENE: renderAllyScene(gc); break;
        }
    }

    // ---------- disco / drama helpers ----------

    private void drawDiscoBackground(GraphicsContext gc, long tick) {
        double t = tick / 40.0;
        Color[] palette = {
                Color.web("#ff4d4d"), Color.web("#ffd93d"), Color.web("#4dff88"),
                Color.web("#4dc3ff"), Color.web("#c94dff"), Color.web("#ff4dd2")
        };
        for (int i = 0; i < palette.length; i++) {
            double angle = t + i * (Math.PI * 2 / palette.length);
            double cx = WIDTH / 2.0 + Math.cos(angle) * 260;
            double cy = HEIGHT / 2.0 + Math.sin(angle * 1.3) * 160;
            double radius = 140 + 30 * Math.sin(t * 2 + i);

            RadialGradient grad = new RadialGradient(
                    0, 0, cx, cy, radius, false, CycleMethod.NO_CYCLE,
                    new Stop(0, palette[i].deriveColor(0, 1, 1, 0.35)),
                    new Stop(1, Color.TRANSPARENT)
            );
            gc.setFill(grad);
            gc.fillOval(cx - radius, cy - radius, radius * 2, radius * 2);
        }
    }

    private void renderIntro(GraphicsContext gc) {
        drawDiscoBackground(gc, tickCounter);

        double hue = (tickCounter * 1.5) % 360;
        gc.setFill(Color.hsb(hue, 0.25, 1.0));
        gc.setFont(Font.font("Georgia", FontWeight.BOLD, 40));
        gc.fillText("THREAD BOUND", WIDTH / 2.0 - 165, 210);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Georgia", FontWeight.BOLD, 18));
        gc.fillText("Ready to get ThreadBound?", WIDTH / 2.0 - 145, 260);

        gc.setFont(Font.font("Georgia", 15));
        gc.fillText("Every choice is a thread. Some bind. Some break.", WIDTH / 2.0 - 230, 300);

        gc.setFont(Font.font("Arial", 14));
        boolean blink = (tickCounter / 30) % 2 == 0;
        if (blink) {
            gc.fillText("Press ENTER to begin", WIDTH / 2.0 - 80, 360);
        }
    }

    private void renderNameEntry(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Georgia", FontWeight.BOLD, 22));
        gc.fillText("What should we call you?", WIDTH / 2.0 - 160, 240);

        gc.setFont(Font.font("Arial", 20));
        boolean showCursor = (System.currentTimeMillis() / 500) % 2 == 0;
        String display = playerName + (showCursor ? "_" : "");
        gc.fillText(display, WIDTH / 2.0 - 100, 300);

        gc.setFont(Font.font("Arial", 13));
        gc.fillText("Type your name, then press ENTER", WIDTH / 2.0 - 140, 360);
    }

    private void renderNameConfirm(GraphicsContext gc) {
        drawDiscoBackground(gc, tickCounter);

        double progress = Math.min(1.0, (tickCounter - nameConfirmStartTick) / 60.0);
        double eased = 1 - Math.pow(1 - progress, 3);

        double flashAlpha = Math.max(0, 0.8 - eased * 0.8);
        gc.setFill(Color.color(1, 1, 1, flashAlpha));
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        double fontSize = 20 + eased * 42;
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Georgia", FontWeight.BOLD, fontSize));
        gc.fillText(playerName, WIDTH / 2.0 - (playerName.length() * fontSize * 0.27), HEIGHT / 2.0);

        gc.setFont(Font.font("Arial", 14));
        gc.setFill(Color.color(1, 1, 1, eased));
        gc.fillText("the threads are listening...", WIDTH / 2.0 - 110, HEIGHT / 2.0 + 50);
    }

    private void renderAvatarSelect(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Georgia", FontWeight.BOLD, 22));
        gc.fillText("Choose your thread color, " + playerName, WIDTH / 2.0 - 210, 180);

        Color c = avatarColors[avatarIndex];
        gc.setFill(c);
        gc.fillPolygon(
                new double[]{WIDTH / 2.0, WIDTH / 2.0 - 20, WIDTH / 2.0 + 20},
                new double[]{260, 310, 310},
                3
        );

        gc.setFont(Font.font("Arial", 16));
        gc.fillText(avatarNames[avatarIndex], WIDTH / 2.0 - 70, 350);

        gc.setFont(Font.font("Arial", 13));
        gc.fillText("<- / -> to browse   |   ENTER to confirm", WIDTH / 2.0 - 150, 400);
    }

    private void renderHowToPlay(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Georgia", FontWeight.BOLD, 22));
        gc.fillText("HOW TO PLAY", WIDTH / 2.0 - 90, 70);

        gc.setFont(Font.font("Arial", 14));
        String[] lines = {
                "WASD or Arrow Keys - move and turn",
                "SPACE - jump (used to cross rivers inside rooms)",
                "Walk into a glowing room to step inside it",
                "",
                "Inside a room, cross the moving river and dodge the patrolling guard",
                "Reach the figure waiting on the other side to face a choice",
                "",
                "Y - choose the first option    N - choose the second option",
                "",
                "Every choice shapes who you become: Kind, Cruel, Honest, or Deceitful",
                "Watch the threads connecting you to each room - they remember what you did",
                "",
                "Press ENTER to step into the world"
        };
        double startY = 120;
        for (String line : lines) {
            gc.fillText(line, 60, startY, 680);
            startY += 26;
        }
    }

    private void renderAllyScene(GraphicsContext gc) {
        drawStarfield(gc);
        drawThreadWeb(gc);

        for (Room r : rooms) r.draw(gc);
        for (Ally a : allies) a.draw(gc);
        player.draw(gc);

        gc.setFill(Color.color(0, 0, 0, 0.75));
        gc.fillRect(0, 400, WIDTH, 200);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Georgia", FontWeight.BOLD, 15));
        gc.fillText(allySceneText, 30, 430, 740);

        gc.setFont(Font.font("Arial", 14));
        gc.fillText("Y = Let him in", 30, 540, 740);
        gc.fillText("N = Push him away", 30, 565, 740);
    }

    private void renderGameOver(GraphicsContext gc) {
        drawTapestry(gc);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Georgia", FontWeight.BOLD, 18));
        gc.fillText(endingText, 40, 40, 720);
    }

    private void renderWorld(GraphicsContext gc) {
        drawStarfield(gc);
        drawThreadWeb(gc);

        for (Room r : rooms) r.draw(gc);
        for (Ally a : allies) a.draw(gc);
        player.draw(gc);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 12));
        gc.fillText("Thread: " + playerName, 20, 30);

        gc.setFont(Font.font("Arial", 16));
        gc.fillText(activeMessage, 20, 550, 760);
    }

    private void drawStarfield(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        for (double[] star : worldStars) {
            double alpha = 0.35 + 0.35 * Math.sin(tickCounter / 25.0 + star[2]);
            gc.setGlobalAlpha(Math.max(0.15, alpha));
            gc.fillOval(star[0], star[1], 2, 2);
        }
        gc.setGlobalAlpha(1.0);
    }

    private void drawThreadWeb(GraphicsContext gc) {
        long dashShift = tickCounter % 40;
        gc.setLineDashes(6, 6);
        gc.setLineDashOffset(-dashShift);

        for (Room r : rooms) {
            double cx = r.getCenterX();
            double cy = r.getCenterY();
            if (r.isTriggered()) {
                Color c = colorForType(r.getType());
                gc.setStroke(Color.color(c.getRed(), c.getGreen(), c.getBlue(), 0.55));
                gc.setLineWidth(2.2);
            } else {
                gc.setStroke(Color.color(1, 1, 1, 0.10));
                gc.setLineWidth(1.2);
            }
            gc.strokeLine(player.x, player.y, cx, cy);
        }

        for (Ally a : allies) {
            boolean bonded = (a == ally2 && teoFollowing);
            double alpha = bonded ? 0.6 : 0.12;
            gc.setStroke(Color.color(1, 0.85, 0.2, alpha));
            gc.setLineWidth(bonded ? 2.0 : 1.2);
            gc.strokeLine(player.x, player.y, a.getX(), a.getY());
        }

        gc.setLineDashes(null);
    }

    private void drawTapestry(GraphicsContext gc) {
        List<HistoryEntry> entries = history.getEntries();
        if (entries.isEmpty()) return;

        double cx = WIDTH / 2.0;
        double cy = 420;
        double radius = 130;

        gc.setFill(Color.WHITE);
        gc.fillOval(cx - 6, cy - 6, 12, 12);

        int n = entries.size();
        for (int i = 0; i < n; i++) {
            double angle = (Math.PI * 2 * i) / n - Math.PI / 2;
            double nx = cx + Math.cos(angle) * radius;
            double ny = cy + Math.sin(angle) * radius;

            Color c = colorForType(entries.get(i).getType());
            gc.setStroke(Color.color(c.getRed(), c.getGreen(), c.getBlue(), 0.6));
            gc.setLineWidth(1.8);
            gc.strokeLine(cx, cy, nx, ny);

            gc.setFill(c);
            gc.fillOval(nx - 5, ny - 5, 10, 10);
        }
    }

    private void renderRoom(GraphicsContext gc) {
        activeRoom.drawExpanded(gc, WIDTH, HEIGHT);

        // draw player inside room
        gc.setFill(player.color);
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
            endingText = "Your journey is over, " + playerName + ".\n\nLooking back at everything you did,\nthe thread that binds you most is:\n\n"
                    + dominant
                    + "\n\nKind: " + score.getScore(MoralityScore.Type.KIND)
                    + "   Cruel: " + score.getScore(MoralityScore.Type.CRUEL)
                    + "   Deceit: " + score.getScore(MoralityScore.Type.DECEIT)
                    + "   Honest: " + score.getScore(MoralityScore.Type.HONEST);

            if (allyOpenedUp) {
                endingText += "\n\nA cord of three strands is not quickly broken.\nYou let someone in, and the thread held.";
            } else {
                endingText += "\n\nYou carried this alone - not because you had to,\nbut because it felt safer that way.\nSomeday, maybe, you'll let someone hold the other end of the thread.";
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}