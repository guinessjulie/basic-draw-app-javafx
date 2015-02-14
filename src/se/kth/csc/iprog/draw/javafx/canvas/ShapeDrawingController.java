package se.kth.csc.iprog.draw.javafx.canvas;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import se.kth.csc.iprog.draw.javafx.beanmodel.ShapeBean;
import se.kth.csc.iprog.draw.javafx.beanmodel.ShapeBeanContainer;
import se.kth.csc.iprog.draw.model.ShapeContainer;

/**
 * Shape drawing controller in Java FX. Adds and edits shapes in the model, based on mouse events.
 * 
 * @author cristi
 */
public class ShapeDrawingController {
    // the canvas holding the shapes
    private final Pane canvas;

    // the shape type chooser
    private final ToggleGroup shapeChooser;

    // model
    private final ShapeBeanContainer model;

    // shape currently being created or modified
    ShapeBean created;

    // shape creation cancelled
    boolean cancelled;

    // starting point shape creation
    double dragX, dragY;

    /**
     * subscribe to canvas mouse and key events.
     * 
     * @param m
     * @param canvas
     * @param selector
     */
    public ShapeDrawingController(ShapeBeanContainer m, Pane canvas, ToggleGroup selector) {
        this.model = m;
        this.canvas = canvas;

        this.shapeChooser = selector;
        shapeChooser.selectToggle(shapeChooser.getToggles().get(0));

        canvas.addEventHandler(MouseEvent.ANY, mouseHandler);

        // classic way of subscribing to an event in java fx
        canvas.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent key) {
                if (key.getCode().equals(KeyCode.ESCAPE)) {
                    if (created != null)
                        model.removeShapeBean(created);
                    cancelled = true;
                    created = null;
                }
            }

        });

    }

    /**
     * mouse interaction.
     */
    EventHandler<MouseEvent> mouseHandler = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent mouseEvent) {
            if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
                dragX = mouseEvent.getX();
                dragY = mouseEvent.getY();
                canvas.setCursor(Cursor.MOVE);
                canvas.requestFocus();
                cancelled = false;
            } else if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
                cancelled = true;
                created = null;
            } else if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
                if (cancelled)
                    return;
                if (created == null)
                    created = model.addShapeBean(getShapeType(), dragX, dragY, mouseEvent.getX() - dragX,
                        mouseEvent.getY() - dragY);
                else
                    model.modifyShapeBean(created, dragX, dragY, mouseEvent.getX() - dragX, mouseEvent.getY() - dragY);
            }
        }

    };

    /**
     * find the shape type based on the shape chooser state
     * 
     * @return
     */
    private int getShapeType() {
        switch (shapeChooser.getSelectedToggle().getUserData().toString()) {
            case "segment":
                return ShapeContainer.SEGMENT;
            case "ellipse":
                return ShapeContainer.ELLIPSE;
            case "rectangle":
                return ShapeContainer.RECTANGLE;
        }
        throw new IllegalStateException(shapeChooser.getSelectedToggle().getUserData().toString());
    }

}
