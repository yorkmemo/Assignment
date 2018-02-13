package io;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;


public abstract class Window {
    //todo: restrict Input dialogs to Window (if open)
    //todo: add centered text
    //todo: Polygon
    //todo: Button

    //todo: increaseSpeed, decreaseSpeed

    //todo: flip()
    //todo: sound
    //todo: random direction between angles
    //todo: collies
    //todo: more keyhandlers
    //todo: Window.clear()
    //todo: grow() & shrink()


    private static Stage stage;
    private static Scene scene;
    private static BorderPane borderPane;
    private static AnchorPane anchorPane;
    private static EventHandler<KeyEvent> leftKeyHander;
    private static EventHandler<KeyEvent> rightKeyHander;
    private static EventHandler<KeyEvent> downKeyHander;
    private static EventHandler<KeyEvent> upKeyHander;
    private static EventHandler<KeyEvent> spaceKeyHander;
    private static Timeline timeline;

    public static final int FPS = 24;
    private static final int DEFAULT_WIDTH = 600;
    private static final int DEFAULT_HEIGHT = 400;
    private static final double DEFAULT_FONT_SIZE = 18;
    private static final String DEFAULT_FONT_NAME = "Arial";
    private static final Color DEFAULT_TEXT_COLOR = Color.BLACK;
    private static final Color DEFAULT_FILL = Color.BLACK;
    private static final Color DEFAULT_STROKE = Color.BLACK;
    private static final double DEFAULT_STROKE_WIDTH = 0.5;
    private static final String DEFAULT_TITLE = "Application";

    private static String fontName = DEFAULT_FONT_NAME;
    private static double fontSize = DEFAULT_FONT_SIZE;
    private static Color fontColor = DEFAULT_TEXT_COLOR;
    private static double initialWidth;

    private static Map<String, Item> idMap = new HashMap<String, Item>();
    private static ItemGroup circles = new ItemGroup("javafx.scene.shape.Circle");
    private static ItemGroup rectangles = new ItemGroup("javafx.scene.shape.Rectangle");
    private static ItemGroup texts = new ItemGroup("javafx.scene.text.Text");
    private static ItemGroup images = new ItemGroup("javafx.scene.image.ImageView");
    private static ItemGroup all = new ItemGroup();

    private static void registerNode(Node node) {
        registerNode(node, null);
    }

    private static void registerNode(Node node, String id) {
        Item item = new Item(node);

       // System.out.println("Registering .... " + node.getClass().getName());

        if (node instanceof Circle) {
            circles.add(item);
        } else if (node instanceof Rectangle) {
            rectangles.add(item);
        } else if (node instanceof Text) {
            texts.add(item);
        } else if (node instanceof ImageView) {
            images.add(item);
        }

        all.add(item);

        if (id != null) {
            if (idMap.containsKey(id)) {
                return;
            }
            node.setId(id);
            idMap.put(id, item);
        }

        node.setUserData(item);
    }

    public static void open(String title, int width, int height) {
        initialize(title, width, height);

        stage.show();
    }

    public static void open() {
        open(DEFAULT_TITLE);
    }

    public static void open(int width, int height) {
        open(DEFAULT_TITLE, width, height);
    }

    public static void open(String title) {
        Screen screen = Screen.getPrimary();

        Rectangle2D bounds = screen.getVisualBounds();

        if (stage == null) {
            stage = new Stage();
        }

        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());

      //  System.out.println(bounds.getWidth());

        open(title, (int) bounds.getWidth(), (int) bounds.getHeight());
    }

    private static void initialize() {
        if (stage == null) {
            initialize(DEFAULT_TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        }
    }

    private static void initialize(String title, int width, int height) {
        if (stage == null) {
            stage = new Stage();
        }

        initialWidth = width;

        stage.setTitle(title);
        stage.setWidth(width);
        stage.setHeight(height);
        stage.setResizable(false);

        anchorPane = new AnchorPane();

        anchorPane.setClip(new Rectangle(0,0,width, height));
        anchorPane.setUserData(new Rectangle2D(0,0,width,height));
        anchorPane.setMaxWidth(width);
        anchorPane.setMaxHeight(height);

        //anchorPane.setStyle("-fx-background-color: crimson");
        borderPane = new BorderPane(anchorPane);

        scene = new Scene(borderPane);
        scene.setOnKeyPressed(e -> {
            keyPressed(e);
        });

        stage.setScene(scene);

        double duration = Math.round(1000 / FPS);

        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.getKeyFrames().add(new KeyFrame(new Duration(duration), e -> tick()));
        timeline.play();
    }

    public static double width() {
        return stage.getWidth();
       // return ((Rectangle2D)anchorPane.getUserData()).getWidth();
    }

    public static double height() {
        return ((Rectangle2D)anchorPane.getUserData()).getHeight();
    }

    public static void font(String name, double size, Color color) {
        fontName = name;
        fontSize = size;
        fontColor = color;
    }

    public static void font(double size, Color color) {
        fontSize = size;
        fontColor = color;
    }

    public static void font(String name, Color color) {
        fontName = name;
        fontColor = color;
    }


    public static void font(Color color) {
        fontColor = color;
    }

    public static void font(String name, double size) {
        fontName = name;
        fontSize = size;
    }

    public static void font(double size) {
        fontSize = size;
    }

    public static void font(String name) {
        fontName = name;
    }

    private static void tick() {
        for (int i = 0; i < anchorPane.getChildren().size(); i++) {
            Node node = anchorPane.getChildren().get(i);

            if (node.getUserData() != null) {
                Item item = (Item)node.getUserData();
                item.tick();
            }
        }
    }


    private static void keyPressed(KeyEvent e) {

        if (leftKeyHander != null && e.getCode() == KeyCode.LEFT) {
          //  System.out.println(e.getCode());
            leftKeyHander.handle(e);
        } else if (rightKeyHander != null && e.getCode() == KeyCode.RIGHT) {
        //    System.out.println(e.getCode());
            rightKeyHander.handle(e);
        } else if (downKeyHander != null && e.getCode() == KeyCode.DOWN) {
        //    System.out.println(e.getCode());
            downKeyHander.handle(e);
        } else if (upKeyHander != null && e.getCode() == KeyCode.UP) {
            //    System.out.println(e.getCode());
            upKeyHander.handle(e);
        } else if (spaceKeyHander != null && e.getCode() == KeyCode.SPACE) {
            //    System.out.println(e.getCode());
            spaceKeyHander.handle(e);
        }
    }

    public static void addText(String text, double leftX, double topY) {
        addTextWithId(null,text,leftX,topY);
    }

    public static void addText(String text, double topY) {
        addTextWithId(null,text, topY);
    }

    public static void addTextWithId(String id, String text, double topY) {
        addTextWithId(id, text, null, topY);
    }

    public static void addTextWithId(String id, String text, double leftX, double topY) {
        addTextWithId(id, text, Double.valueOf(leftX), topY);
    }

    private static void addTextWithId(String id, String text, Double leftX, double topY) {
        initialize();

        boolean center = leftX == null;

        Text displayText = new Text(text);
        displayText.applyCss();

        System.out.println(displayText.getBoundsInParent().getWidth());

        displayText.setLayoutX(center ? initialWidth / 2.0 - displayText.getBoundsInLocal().getWidth(): leftX);
        displayText.setLayoutY(topY);

//            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

//        displayText.setTextAlignment(TextAlignment.CENTER);
        displayText.setFont(Font.font(fontName, fontSize));
        displayText.setFill(fontColor);

        registerNode(displayText, id);

        anchorPane.getChildren().add(displayText);
    }

    public static void addCircleWithId(String id, double centerX, double centerY, double radius) {
        addCircleWithId(id, centerX, centerY, radius, DEFAULT_FILL);
    }

    public static void addCircleWithId(String id, double centerX, double centerY, double radius, Color fill) {
        addCircleWithId(id, centerX, centerY, radius, fill, DEFAULT_STROKE_WIDTH, DEFAULT_STROKE);
    }

    public static void addCircleWithId(String id, double centerX, double centerY, double radius, Color fill, double strokeWidth) {
        addCircleWithId(id, centerX, centerY, radius, fill, strokeWidth, DEFAULT_STROKE);
    }

    public static void addCircleWithId(String id, double centerX, double centerY, double radius, Color fill, double strokeWidth, Color stroke) {
        initialize();
        Circle circle = new Circle(centerX, centerY, radius, fill);
        circle.setStroke(stroke);
        circle.setStrokeWidth(strokeWidth);

        registerNode(circle, id);

        anchorPane.getChildren().add(circle);
    }

    public static void addCircle(double centerX, double centerY, double radius) {
        addCircleWithId(null, centerX, centerY, radius);
    }

    public static void addCircle(double centerX, double centerY, double radius, Color fill) {
        addCircleWithId(null, centerX, centerY, radius, fill);
    }

    public static void addCircle(double centerX, double centerY, double radius, Color fill, double strokeWidth) {
        addCircleWithId(null, centerX, centerY, radius, fill, strokeWidth);
    }

    public static void addCircle(double centerX, double centerY, double radius, Color fill, double strokeWidth, Color stroke) {
        addCircleWithId(null, centerX, centerY, radius, fill, strokeWidth, stroke);
    }

    public static void addRectangleWithId(String id, double leftX, double topY, double width, double height) {
        addRectangleWithId(id, leftX, topY, width, height, DEFAULT_FILL);
    }

    public static void addRectangleWithId(String id, double leftX, double topY, double width, double height, double angle) {
        addRectangleWithId(id, leftX, topY, width, height, DEFAULT_FILL, DEFAULT_STROKE_WIDTH, DEFAULT_STROKE, angle);
    }

    public static void addRectangleWithId(String id, double leftX, double topY, double width, double height, Color fill) {
        addRectangleWithId(id, leftX, topY, width, height, fill, DEFAULT_STROKE_WIDTH, DEFAULT_STROKE);
    }
    
    public static void addRectangleWithId(String id, double leftX, double topY, double width, double height, Color fill, double strokeWidth) {
        addRectangleWithId(id, leftX, topY, width, height, fill, strokeWidth, DEFAULT_STROKE);
    }

    public static void addRectangleWithId(String id, double leftX, double topY, double width, double height, Color fill, double strokeWidth, double angle) {
        addRectangleWithId(id, leftX, topY, width, height, fill, strokeWidth, DEFAULT_STROKE, angle);
    }

    public static void addRectangleWithId(String id, double leftX, double topY, double width, double height, Color fill, double strokeWidth, Color stroke) {
        addRectangleWithId(id, leftX, topY, width, height, fill, strokeWidth, stroke, 0);
    }

    public static void addRectangleWithId(String id, double leftX, double topY, double width, double height, Color fill, double strokeWidth, Color stroke, double angle) {
        initialize();
        Rectangle rectangle = new Rectangle(leftX, topY, width, height);
        rectangle.setFill(fill);
        rectangle.setStroke(stroke);
        rectangle.setStrokeWidth(strokeWidth);
        rectangle.setRotate(angle);

        registerNode(rectangle, id);

        anchorPane.getChildren().add(rectangle);
    }

    public static void addRectangle(double leftX, double topY, double width, double height) {
        addRectangleWithId(null, leftX, topY, width, height);
    }

    public static void addRectangle(double leftX, double topY, double width, double height, double angle) {
        addRectangleWithId(null, leftX, topY, width, height, angle);
    }

    public static void addRectangle(double leftX, double topY, double width, double height, Color fill) {
        addRectangleWithId(null, leftX, topY, width, height, fill);
    }

    public static void addRectangle(double leftX, double topY, double width, double height, Color fill, double strokeWidth) {
        addRectangleWithId(null, leftX, topY, width, height, fill, strokeWidth);
    }

    public static void addRectangle(double leftX, double topY, double width, double height, Color fill, double strokeWidth, double angle) {
        addRectangleWithId(null, leftX, topY, width, height, fill, strokeWidth, angle);
    }

    public static void addRectangle(double leftX, double topY, double width, double height, Color fill, double strokeWidth, Color stroke) {
        addRectangleWithId(null, leftX, topY, width, height, fill, strokeWidth, stroke);
    }

    public static void addRectangle(double leftX, double topY, double width, double height, Color fill, double strokeWidth, Color stroke, double angle) {
        addRectangleWithId(null, leftX, topY, width, height, fill, strokeWidth, stroke);
    }

    public static void addImage(String filename) {
        addImageWithId(null,filename);
    }

    public static void addImage(String filename, double leftX, double topY) {
        addImageWithId(null,filename, leftX, topY);
    }

    public static void addImage(String filename, double leftX, double topY, double width) {
        addImageWithId(null,filename, leftX, topY, width);
    }

    public static void addImage(String filename, double leftX, double topY, double width, double height) {
        addImageWithId(null,filename,leftX,topY,width,height);
    }

    public static void addImageWithId(String id, String filename) {
        addImageWithId(id,filename,0,0);
    }

    public static void addImageWithId(String id, String filename, double leftX, double topY) {
        initialize();

        ImageView imageView = new ImageView(new Image(filename));
        imageView.setLayoutX(leftX);
        imageView.setLayoutY(topY);

        registerNode(imageView, id);

        anchorPane.getChildren().add(imageView);
    }

    public static void addImageWithId(String id, String filename, double leftX, double topY, double width) {
        initialize();

        ImageView imageView = new ImageView(new Image(filename));

        imageView.setPreserveRatio(true);
        imageView.setFitWidth(width);
        imageView.setLayoutX(leftX);
        imageView.setLayoutY(topY);

        registerNode(imageView, id);

        anchorPane.getChildren().add(imageView);
    }

    public static void addImageWithId(String id, String filename, double leftX, double topY, double width, double height) {
        initialize();

        ImageView imageView = new ImageView(new Image(filename));

        registerNode(imageView, id);

        imageView.setPreserveRatio(false);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setLayoutX(leftX);
        imageView.setLayoutY(topY);

        anchorPane.getChildren().add(imageView);
    }

    public static Itemable id(String id) {
        return idMap.containsKey(id) ? idMap.get(id) : null;
    }

    public static Itemable all() {
        return all;
    }

    public static Itemable circles() {
        return circles;
    }

    public static Itemable rectangles() {
        System.out.println(rectangles.size());
        return rectangles;
    }

    public static Itemable images() {
        return images;
    }

    public static Itemable texts() {
        return texts;
    }

    public static boolean remove(String id) {
        if (idMap.containsKey(id)) {
            anchorPane.getChildren().remove(idMap.get(id).getNode());
            idMap.remove(id);
            return true;
        }

        return false;
    }

    static boolean remove(Item item) {
        if (item.getId() != null) return remove(item.getId());

        return anchorPane.getChildren().remove(item.getNode());
    }

    public static void onLeftKey(EventHandler<KeyEvent> handler) {
        leftKeyHander = handler;
    }

    public static void onRightKey(EventHandler<KeyEvent> handler) {
        rightKeyHander = handler;
    }

    public static void onDownKey(EventHandler<KeyEvent> handler) {
        downKeyHander = handler;
    }

    public static void onUpKey(EventHandler<KeyEvent> handler) {
        upKeyHander = handler;
    }

    public static void onSpaceKey(EventHandler<KeyEvent> handler) {
        spaceKeyHander = handler;
    }

    public static void background(Color color) {
        scene.setFill(color);
    }

    static Bounds edges() {
        return anchorPane.getLayoutBounds();
    }
}