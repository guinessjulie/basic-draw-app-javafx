package se.kth.csc.iprog.draw.javafx.canvas;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Shape;

/**
 * Controller to move shapes, active at all times (not just on toolbar selection) This controller does not observe the
 * model, it simply changes JavaFX Shape locations, and the bindings will change the model
 * 
 * @author cristi
 */
public class SelectionController {
    double dX, dY;

    double startX, startY;

    boolean dragCancelled;

    Shape dragged;

    /**
     * add this controller to a JavaFX Shape
     * 
     * @param s
     */
    void addTo(Shape s) {
        // make the shape empty and draggable
        // this should be in the stylesheet but it doesn't seem to take effect
        s.setStyle("-fx-fill:null;-fx-stroke:black;-fx-stroke-width:3");

        // make sure there will be mouse events on the shape bounds
        s.setPickOnBounds(true);

        s.addEventHandler(MouseEvent.ANY, shapeDragging);
        s.setOnKeyPressed(keyHandler);
    }

    // to avoid creating a new listener for each shape, we define them separately

    EventHandler<MouseEvent> shapeDragging = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent mouseEvent) {
            dragged = (Shape) mouseEvent.getSource();
            if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
                // distances between the drag point and the initial location
                dX = dragged.getLayoutX() - mouseEvent.getSceneX();
                dY = dragged.getLayoutY() - mouseEvent.getSceneY();
                startX = dragged.getLayoutX();
                startY = dragged.getLayoutY();
                dragged.setCursor(Cursor.MOVE);

                // request focus to make sure we detect ESCAPE
                dragged.requestFocus();

                // consume the event so it doesn't go to the Canvas to provoke drawing a new shape
                mouseEvent.consume();
                dragCancelled = false;

            } else if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
                dragged.setCursor(Cursor.HAND);
                mouseEvent.consume();

            } else if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
                if (dragCancelled)
                    return;

                // move the shape, this will change the model!!!
                dragged.setLayoutX(mouseEvent.getSceneX() + dX);
                dragged.setLayoutY(mouseEvent.getSceneY() + dY);
                mouseEvent.consume();

            } else if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_ENTERED)) {
                dragged.setCursor(Cursor.HAND);
                mouseEvent.consume();
            }
        }
    };

    EventHandler<KeyEvent> keyHandler = new EventHandler<KeyEvent>() {

        @Override
        public void handle(KeyEvent key) {
            if (key.getCode().equals(KeyCode.ESCAPE)) {
                if (dragged == null)
                    return;
                key.consume();
                dragCancelled = true;

                // move the shape back to the original location
                dragged.layoutXProperty().set(startX);
                dragged.layoutYProperty().set(startY);
                dragged.setCursor(Cursor.MOVE);
            }
        }
    };

}
