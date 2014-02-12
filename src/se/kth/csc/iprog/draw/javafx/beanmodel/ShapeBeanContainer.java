package se.kth.csc.iprog.draw.javafx.beanmodel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import se.kth.csc.iprog.draw.model.Shape;
import se.kth.csc.iprog.draw.model.ShapeContainer;

/**
 * Adapter that transforms the shape model into a model containing shape beans as per the JavaFX bean definition.
 * Enforces the rules of the original model.
 * 
 * @author cristi
 */
public class ShapeBeanContainer extends ShapeContainer {

    ObservableList<ShapeBean> shapeBeans = FXCollections.<ShapeBean> observableArrayList();

    StringProperty lastError = new SimpleStringProperty("");

    public ShapeBean addShapeBean(int type, double x, double y, double w, double h) throws IllegalArgumentException {
        Shape s = super.addShape(type, x, y, w, h);
        ShapeBean sBean = new ShapeBean(s, this);
        shapeBeans.add(sBean);
        return sBean;
    }

    public void removeShapeBean(ShapeBean sBean) {
        super.removeShape(sBean.getShape());
        shapeBeans.remove(sBean);
    }

    @Override
    public Shape addShape(int type, double x, double y, double w, double h) throws IllegalArgumentException {
        return addShapeBean(type, x, y, w, h).getShape();
    }

    /**
     * We intercept all shape bean changes to apply model business rules
     */
    void changed(ShapeBean shapeBean) {
        try {
            super.modifyShape(shapeBean.getShape(), shapeBean.xProperty().getValue(), shapeBean.yProperty().getValue(),
                shapeBean.wProperty().getValue(), shapeBean.hProperty().getValue());
            lastError.setValue("");
        } catch (Throwable t) {
            lastError.setValue(t.getMessage());
        }
    }

    public void modifyShapeBean(ShapeBean sBean, double x, double y, double w, double h) {
        sBean.xProperty().set(x);
        sBean.yProperty().set(y);
        sBean.wProperty().set(w);
        sBean.hProperty().set(h);

    }

    public ObservableValue<? extends String> lastErrorProperty() {
        return lastError;
    }

    public ObservableList<ShapeBean> getShapeBeans() {
        return shapeBeans;
    }

}
