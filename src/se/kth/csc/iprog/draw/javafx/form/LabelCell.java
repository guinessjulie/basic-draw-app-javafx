package se.kth.csc.iprog.draw.javafx.form;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;

/**
 * A simple label-based list cell. This is needed to be able to add listeners to the individual cells.
 * 
 * @author cristi
 * @param <T>
 */
class LabelCell<T> extends ListCell<T> {
    Label label = new Label();

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        if (empty) {
            setGraphic(null);
        } else {
            label.setText(item.toString());
            setGraphic(label);
        }
    }
}