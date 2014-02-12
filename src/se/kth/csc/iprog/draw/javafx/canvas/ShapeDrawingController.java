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

public class ShapeDrawingController {
    private final Pane canvas;

    private final ToggleGroup shapeChooser;

    private final ShapeBeanContainer model;

    public ShapeDrawingController(ShapeBeanContainer m, Pane canvas, ToggleGroup selector) {
        this.model = m;
        this.canvas = canvas;

        this.shapeChooser = selector;
        shapeChooser.selectToggle(shapeChooser.getToggles().get(0));

        canvas.addEventHandler(MouseEvent.ANY, mouseHandler);

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

    boolean cancelled;

    ShapeBean created;

    double dragX, dragY;

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
