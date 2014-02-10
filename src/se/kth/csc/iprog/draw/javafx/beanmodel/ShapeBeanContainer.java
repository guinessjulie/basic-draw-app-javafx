package se.kth.csc.iprog.draw.javafx.beanmodel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import se.kth.csc.iprog.draw.model.Shape;
import se.kth.csc.iprog.draw.model.ShapeContainer;

public class ShapeBeanContainer extends ShapeContainer {

    ObservableList<ShapeBean> shapeBeans = FXCollections.<ShapeBean> observableArrayList();

    StringProperty lastError = new SimpleStringProperty("");

    @Override
    public Shape addShape(int type, double x, double y, double w, double h) throws IllegalArgumentException {
        Shape s = super.addShape(type, x, y, w, h);
        ShapeBean sBean = new ShapeBean(s, this);
        shapeBeans.add(sBean);
        return s;
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

    public ObservableValue<? extends String> lastErrorProperty() {
        return lastError;
    }

    public ObservableList<ShapeBean> getShapeBeans() {
        return shapeBeans;
    }
}
