package se.kth.csc.iprog.draw.javafx.canvas;

import java.util.HashSet;
import java.util.Set;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
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

    Set<DoubleBinding> bindings = new HashSet<DoubleBinding>();

    DoubleBinding propagateFrom = null;

    boolean propagateDirection = false;

    /**
     * Bind a property to an expression which may contain properties that depend on it. For example if the model shape x
     * changes, the ellipse.centerX depends on the shape x. If centerX changes, the model shape x depends on centerX. To
     * avoid circular binding, we make sure that properties are propagated in a single direction (from model to view or
     * from view to model)
     */
    void weakBind(final DoubleProperty prop, final DoubleBinding expr) {

        // data propagates from model to view if the property to be set is from a JavaFX Shape
        final boolean modelToView = prop.getBean() != null && prop.getBean() instanceof javafx.scene.shape.Shape;

        // expressions must be hanged in a set, otherwise they will get garbage collected and their listeners will take
        // no effect!
        bindings.add(expr);

        // initially the model is populated while the UI nodes are not, so we copy the value
        if (modelToView)
            prop.set(expr.doubleValue());

        // then we listen to expression changes and update the property
        expr.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable arg0) {
                // if propagation takes place in the opposite direction, we ignore
                if (propagateFrom != null && propagateDirection != modelToView)
                    return;

                if (propagateFrom == null) {
                    // propagation starts
                    propagateFrom = expr;
                    propagateDirection = modelToView;
                }

                // propagate the change: set the property. This will generate more calls to this listener, all of which
                // should be ignored as we are only propagating in a single direction
                prop.set(expr.doubleValue());

                if (propagateFrom == expr)
                    // we are back to the initial expression, therefore propagation finished
                    propagateFrom = null;
            }
        });
    }

    /**
     * create a UI shape out of a model shape. The two objects (beans) will be bound to each other
     * 
     * @param shapeBean
     * @return
     */
    protected Node makeNode(final ShapeBean shapeBean) {

        if (shapeBean.getShape() instanceof Segment) {
            final Line line = new Line();
            line.startXProperty().bindBidirectional(shapeBean.xProperty());
            line.startYProperty().bindBidirectional(shapeBean.yProperty());

            weakBind(line.endXProperty(), shapeBean.xProperty().add(shapeBean.wProperty()));
            weakBind(line.endYProperty(), shapeBean.yProperty().add(shapeBean.hProperty()));

            weakBind(shapeBean.wProperty(), line.endXProperty().subtract(line.startXProperty()));
            weakBind(shapeBean.hProperty(), line.endYProperty().subtract(line.startYProperty()));

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
            final javafx.scene.shape.Ellipse ellipse = new javafx.scene.shape.Ellipse();

            weakBind(ellipse.centerXProperty(), shapeBean.xProperty().add(shapeBean.wProperty().divide(2)));
            weakBind(ellipse.centerYProperty(), shapeBean.yProperty().add(shapeBean.hProperty().divide(2)));

            weakBind(shapeBean.xProperty(), ellipse.centerXProperty().subtract(ellipse.radiusXProperty()));
            weakBind(shapeBean.yProperty(), ellipse.centerYProperty().subtract(ellipse.radiusYProperty()));

            weakBind(ellipse.radiusXProperty(), shapeBean.wProperty().divide(2));
            weakBind(ellipse.radiusYProperty(), shapeBean.hProperty().divide(2));

            weakBind(shapeBean.wProperty(), ellipse.radiusXProperty().multiply(2));
            weakBind(shapeBean.hProperty(), ellipse.radiusYProperty().multiply(2));

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
