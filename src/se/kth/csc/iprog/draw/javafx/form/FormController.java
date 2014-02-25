package se.kth.csc.iprog.draw.javafx.form;

import java.io.IOException;
import java.util.ArrayList;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.converter.NumberStringConverter;
import se.kth.csc.iprog.draw.javafx.beanmodel.ShapeBean;
import se.kth.csc.iprog.draw.javafx.beanmodel.ShapeBeanContainer;

/**
 * The Java FX form controller. Since the view is generated from an FXML file, it cannot observe the model. Therefore we
 * allow the controller to observe the model. As a JavaFX specific, the link to the model is done via binding. The
 * controller becomes a mediator between model and view, like in e.g. Model-View-ViewModel. Thus the view only addresses
 * the layout concern, and the controller takes both the data view concern, and the interaction concern.
 * 
 * @author cristi
 */
public class FormController {

    /**
     * Controller shows the view. We do it in a static method because no controller instance is available before the
     * FXML file is loaded. An alternative is to call setController() on the FXML loader.
     * 
     * @param model
     */
    static public void showFormView(ShapeBeanContainer model) {
        FXMLLoader loader = new FXMLLoader(FormController.class.getResource("FormView.fxml"));

        // load the FXML
        Parent root = null;
        try {
            root = (Parent) loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // passing the model parameter to the controller
        // see http://stackoverflow.com/quest ions/14187963/passing-parameters-javafx-fxml
        final FormController controller = loader.<FormController> getController();
        controller.setModel(model);

        // make a window (Stage)
        Stage stage = new Stage();
        stage.setTitle("form");
        stage.setScene(new Scene(root));

        // example using a stylesheet
        // scene.getStylesheets().add(FormController.getResource("application.css").toExternalForm());

        // window listener
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                controller.close();
            }
        });
        // show the window
        stage.show();
        stage.setX(Math.random() * 1000);
    }

    private ShapeBean current;

    /**
     * subscribe to the model
     * 
     * @param model
     */
    private void setModel(final ShapeBeanContainer model) {
        errorLabel.setTextFill(Color.RED);
        errorLabel.textProperty().bind(Bindings.concat(model.lastErrorProperty(), numberErrorProperty));

        // this makes sure that changes in any element (not just element addition/removal) are reported to list change
        // listeners such as the ListView
        list.setItems(FXCollections.<ShapeBean> observableList(new ArrayList<ShapeBean>(),
            new Callback<ShapeBean, javafx.beans.Observable[]>() {
                @Override
                public javafx.beans.Observable[] call(ShapeBean shapeBean) {
                    return new javafx.beans.Observable[] { shapeBean.xProperty(), shapeBean.yProperty(),
                            shapeBean.wProperty(), shapeBean.hProperty() };
                }
            }));

        // bind the ListView items to the model (and initialize from it)
        Bindings.bindContentBidirectional(list.getItems(), model.getShapeBeans());

        // initialize null current shape, disable textboxes, etc.
        setCurrentShape(null);

        // listener to detect change of current shape
        list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ShapeBean>() {
            @Override
            public void changed(ObservableValue<? extends ShapeBean> observable, ShapeBean oldValue, ShapeBean newValue) {
                setCurrentShape(newValue);
            }
        });

        // cell factory to make a LabelCell for each list item and to add a transfer controller to it
        list.setCellFactory(new Callback<ListView<ShapeBean>, ListCell<ShapeBean>>() {
            @Override
            public ListCell<ShapeBean> call(ListView<ShapeBean> param) {
                LabelCell<ShapeBean> lc = new LabelCell<ShapeBean>();
                new ShapeTransferCellController(model, lc);
                return lc;
            }
        });

    }

    /**
     * unsubscribe from the model
     * 
     * @param model
     */
    protected void close() {
        setCurrentShape(null);
    }

    @FXML
    ListView<ShapeBean> list;

    @FXML
    TextField xField;

    @FXML
    TextField yField;

    @FXML
    TextField wField;

    @FXML
    TextField hField;

    @FXML
    Label surfaceField;

    @FXML
    Label errorLabel;

    StringProperty numberErrorProperty = new SimpleStringProperty("");

    /**
     * detection of invalid number values, to update the status bar
     */
    NumberStringConverter numberStringConverter = new NumberStringConverter() {
        @Override
        public Number fromString(String s) {
            try {
                Double.parseDouble(s);
                numberErrorProperty.setValue("");
            } catch (Throwable t) {
                System.out.println(t);
                numberErrorProperty.setValue(t.getMessage());
            }
            return super.fromString(s);
        }
    };

    /**
     * Bind the form fields with the current shape bean properties
     * 
     * @param newValue
     */
    void setCurrentShape(ShapeBean newValue) {
        if (current != null) {
            Bindings.unbindBidirectional(xField.textProperty(), current.xProperty());
            Bindings.unbindBidirectional(yField.textProperty(), current.yProperty());
            Bindings.unbindBidirectional(wField.textProperty(), current.wProperty());
            Bindings.unbindBidirectional(hField.textProperty(), current.hProperty());
            surfaceField.textProperty().unbind();
        }

        current = newValue;
        if (current != null) {
            Bindings.bindBidirectional(xField.textProperty(), newValue.xProperty(), numberStringConverter);
            Bindings.bindBidirectional(yField.textProperty(), newValue.yProperty(), numberStringConverter);
            Bindings.bindBidirectional(wField.textProperty(), newValue.wProperty(), numberStringConverter);
            Bindings.bindBidirectional(hField.textProperty(), newValue.hProperty(), numberStringConverter);
            surfaceField.textProperty().bind(Bindings.convert(newValue.surfaceProperty()));

            xField.disableProperty().set(false);
            yField.disableProperty().set(false);
            wField.disableProperty().set(false);
            hField.disableProperty().set(false);

        } else {
            xField.disableProperty().set(true);
            yField.disableProperty().set(true);
            wField.disableProperty().set(true);
            hField.disableProperty().set(true);
            surfaceField.setText("");
        }
    }
}
