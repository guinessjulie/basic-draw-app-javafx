package se.kth.csc.iprog.draw.javafx.canvas;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import se.kth.csc.iprog.draw.javafx.beanmodel.ShapeBean;
import se.kth.csc.iprog.draw.javafx.beanmodel.ShapeBeanContainer;
import se.kth.csc.iprog.draw.model.Ellipse;
import se.kth.csc.iprog.draw.model.Rectangle;
import se.kth.csc.iprog.draw.model.Segment;

public class CanvasView {
    Pane canvas;

    static final int strokeWidth = 3;

    public CanvasView(final ShapeBeanContainer model) {
        model.getShapeBeans().addListener(new ListChangeListener<ShapeBean>() {
            @Override
            public void onChanged(Change<? extends ShapeBean> chg) {
                if (chg.wasAdded()) {
                    canvas.getChildren().add(makeNode(model.getShapeBeans().get(chg.getFrom())));
                }
            }

        });
        canvas = new Pane();

        canvas.setPrefSize(200, 200);
        canvas.setPickOnBounds(true);

        for (ShapeBean sb : model.getShapeBeans()) {
            canvas.getChildren().add(makeNode(sb));
        }

        /*
                EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent e) {
                        System.out.println(e);
                    }

                };
                */
    }

    protected Node makeNode(ShapeBean shapeBean) {

        if (shapeBean.getShape() instanceof Segment) {
            Line line = new Line();
            line.startXProperty().bindBidirectional(shapeBean.xProperty());
            line.startYProperty().bindBidirectional(shapeBean.yProperty());

            line.endXProperty().bind(Bindings.add(line.startXProperty(), shapeBean.wProperty()));
            line.endYProperty().bind(Bindings.add(line.startYProperty(), shapeBean.hProperty()));

            // shapeBean.wProperty().bind(Bindings.subtract(line.endXProperty(), shapeBean.xProperty()));
            // shapeBean.hProperty().bind(Bindings.subtract(line.endYProperty(), shapeBean.yProperty()));

            style(line);
            return line;
        }
        if (shapeBean.getShape() instanceof Rectangle) {
            javafx.scene.shape.Rectangle rect = new javafx.scene.shape.Rectangle();
            rect.xProperty().bindBidirectional(shapeBean.xProperty());
            rect.yProperty().bindBidirectional(shapeBean.yProperty());
            rect.widthProperty().bindBidirectional(shapeBean.wProperty());
            rect.heightProperty().bindBidirectional(shapeBean.hProperty());
            style(rect);
            return rect;
        } else if (shapeBean.getShape() instanceof Ellipse) {
            javafx.scene.shape.Ellipse ellipse = new javafx.scene.shape.Ellipse();

            NumberBinding halfShapeW = Bindings.divide(shapeBean.wProperty(), 2);
            // NumberBinding doubleXRadius = Bindings.multiply(ellipse.radiusXProperty(), 2);
            NumberBinding halfShapeH = Bindings.divide(shapeBean.hProperty(), 2);
            // NumberBinding doubleYRadius = Bindings.multiply(ellipse.radiusYProperty(), 2);

            ellipse.centerXProperty().bind(Bindings.add(shapeBean.xProperty(), halfShapeW));
            // shapeBean.xProperty().bind(Bindings.subtract(ellipse.centerXProperty(), doubleXRadius));

            ellipse.centerYProperty().bind(Bindings.add(shapeBean.yProperty(), halfShapeH));
            // shapeBean.yProperty().bind(Bindings.subtract(ellipse.centerYProperty(), doubleYRadius));

            ellipse.radiusXProperty().bind(halfShapeW);
            // shapeBean.wProperty().bind(doubleXRadius);

            ellipse.radiusYProperty().bind(halfShapeH);
            // shapeBean.hProperty().bind(doubleYRadius);

            style(ellipse);
            return ellipse;
        }

        return null;
    }

    private void style(javafx.scene.shape.Shape shape) {
        shape.setFill(null);
        shape.setStroke(Color.BLACK);
        shape.setStrokeWidth(strokeWidth);
    }

    public Pane getCanvas() {
        return canvas;
    }
}
