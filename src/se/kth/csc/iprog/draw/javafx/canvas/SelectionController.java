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

    /**
     * drag start point in relation to the shape
     */
    double dX, dY;

    /**
     * original shape position, for moving back in case of escape
     */
    double startX, startY;

    /**
     * the dragged shape, null if none
     */
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

        s.addEventHandler(MouseEvent.ANY, shapeDragging);
        s.setOnKeyPressed(keyHandler);
    }

    // to avoid creating a new listener for each shape, we define them separately

    EventHandler<MouseEvent> shapeDragging = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent mouseEvent) {
            Shape shape = (Shape) mouseEvent.getSource();

            if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
                dragged = shape;
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

            } else if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
                if (dragged == null)
                    return;
                dragged.setCursor(Cursor.HAND);
                mouseEvent.consume();

            } else if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
                if (dragged == null)
                    return;

                // move the shape, this will change the model!!!
                dragged.setLayoutX(mouseEvent.getSceneX() + dX);
                dragged.setLayoutY(mouseEvent.getSceneY() + dY);
                dragged.setCursor(Cursor.MOVE);

                mouseEvent.consume();

            } else if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_ENTERED)) {
                shape.setCursor(Cursor.HAND);
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

                // move the shape back to the original location
                dragged.layoutXProperty().set(startX);
                dragged.layoutYProperty().set(startY);
                dragged.setCursor(Cursor.HAND);
                dragged = null;
            }
        }
    };

}
