package se.kth.csc.iprog.draw.javafx.beanmodel;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import se.kth.csc.iprog.draw.model.Shape;

/** Wrapper that transforms a shape into a JavaFX bean */
public class ShapeBean {

    private final Shape shape;

    final DoubleProperty x = new SimpleDoubleProperty(this, "x");

    final DoubleProperty y = new SimpleDoubleProperty(this, "y");

    final DoubleProperty w = new SimpleDoubleProperty(this, "w");

    final DoubleProperty h = new SimpleDoubleProperty(this, "h");

    final DoubleProperty surface = new SimpleDoubleProperty(this, "surface");

    public DoubleProperty xProperty() {
        return x;
    }

    public DoubleProperty yProperty() {
        return y;
    }

    public DoubleProperty wProperty() {
        return w;
    }

    public DoubleProperty hProperty() {
        return h;
    }

    public DoubleProperty surfaceProperty() {
        return surface;
    }

    ChangeListener<Number> propertyListener = new ChangeListener<Number>() {

        @Override
        public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
            container.changed(ShapeBean.this);
            surface.setValue(shape.getSurface());
        }

    };

    private final ShapeBeanContainer container;

    public ShapeBean(Shape shape, ShapeBeanContainer container) {
        this.shape = shape;
        this.container = container;
        x.setValue(shape.getX());
        y.setValue(shape.getY());
        w.setValue(shape.getW());
        h.setValue(shape.getH());
        surface.setValue(shape.getSurface());

        x.addListener(propertyListener);
        y.addListener(propertyListener);
        w.addListener(propertyListener);
        h.addListener(propertyListener);
    }

    @Override
    public String toString() {
        return shape.toString();
    }

    public Shape getShape() {
        return shape;
    }

}
