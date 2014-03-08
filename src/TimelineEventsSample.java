/**
 * Copyright (c) 2008, 2012 Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 */

import javafx.animation.AnimationTimer;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.effect.Lighting;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * A sample that demonstrates events triggered during timeline play. The circle changes its radius in a linear fashion
 * during each key frame and randomly jumps to a new location along the x coordinate at the end of the key frame.
 * 
 * @see javafx.animation.KeyFrame
 * @see javafx.animation.KeyValue
 * @see javafx.animation.Timeline
 * @see javafx.util.Duration
 * @see javafx.event.ActionEvent
 * @see javafx.event.EventHandler
 */
public class TimelineEventsSample extends Application {
    // main timeline
    private Timeline timeline;

    private AnimationTimer timer;

    // variable for storing actual frame
    private Integer i = 0;

    private void init(Stage primaryStage) {
        Group root = new Group();
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 700, 700));
        // create a circle with effect
        final Circle circle = new Circle(20, Color.rgb(156, 216, 255));
        circle.setEffect(new Lighting());
        // create a text inside a circle
        final Text text = new Text(i.toString());
        text.setStroke(Color.BLACK);
        // create a layout for circle with text inside
        final StackPane stack = new StackPane();
        stack.getChildren().addAll(circle, text);
        stack.setLayoutX(300);
        stack.setLayoutY(30);

        // create a timeline for moving the circle

        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setAutoReverse(true);

        // one can add a specific action when each frame is started. There are one or more frames during
        // executing one KeyFrame depending on set Interpolator.
        timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                text.setText(i.toString());
                i++;
            }

        };

        // create a keyFrame, the keyValue is reached at time 2s
        Duration duration = Duration.seconds(4);
        // one can add a specific action when the keyframe is reached
        EventHandler<ActionEvent> onFinished = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                System.out.println("finished" + i);
                stack.setTranslateX(java.lang.Math.random() * 200 - 100);
                // reset counter
                i = 0;
            }
        };

        KeyFrame keyFrame = new KeyFrame(duration, onFinished,
        //
                new KeyValue(stack.scaleXProperty(), 4, Interpolator.EASE_IN)
                //
                , new KeyValue(stack.translateYProperty(), 200, Interpolator.EASE_IN)
        //
        // , new KeyValue(circle.fillProperty(), Color.RED)
        //
        // , new KeyValue(text.strokeProperty(), Color.WHITE)
        //
        );

        KeyFrame keyFrame1 = new KeyFrame(Duration.seconds(2)
        //
                , new KeyValue(stack.scaleYProperty(), 4, Interpolator.EASE_IN));

        // add the keyframe to the timeline
        timeline.getKeyFrames().add(keyFrame);
        timeline.getKeyFrames().add(keyFrame1);
        /*
                Rectangle rect = new Rectangle(10, 10, 20, 30);
                timeline.getKeyFrames().add(
                    new KeyFrame(Duration.seconds(4), new KeyValue(rect.scaleXProperty(), 2), new KeyValue(
                            rect.scaleYProperty(), 4)));
                timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(2), new KeyValue(rect.fillProperty(), Color.RED)));
        */
        root.getChildren().add(stack);
        // root.getChildren().add(rect);
    }

    public void play() {
        timeline.play();
        timer.start();
    }

    @Override
    public void stop() {
        timeline.stop();
        timer.stop();
    }

    public double getSampleWidth() {
        return 700;
    }

    public double getSampleHeight() {
        return 700;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        init(primaryStage);
        primaryStage.show();
        play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}