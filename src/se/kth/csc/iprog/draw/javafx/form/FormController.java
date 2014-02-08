package se.kth.csc.iprog.draw.javafx.form;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import se.kth.csc.iprog.draw.model.Shape;
import se.kth.csc.iprog.draw.model.ShapeContainer;

/**
 * The Java FX form controller. Since the view is generated from an FXML file, it cannot implement Observer. Therefore
 * we allow the controller to observe the model, and manipulate the view elements. The controller becomes a mediator
 * between model and view, like in e.g. Model-View-ViewModel. Thus the view only addresses the layout concern, and the
 * controller takes both the data view concern, and the interaction concern.
 * 
 * @author cristi
 */
public class FormController implements Observer {

    /**
     * Controller shows the view. We do it in a static method because no controller instance is available before the
     * FXML file is loaded. An alternative is to call setController() on the FXML loader.
     * 
     * @param model
     */
    static public void showFormView(ShapeContainer model) {
        FXMLLoader loader = new FXMLLoader(
                FormController.class.getResource("/se/kth/csc/iprog/draw/javafx/form/FormView.fxml"));

        // load the FXML
        Parent root = null;
        try {
            root = (Parent) loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // passing the model parameter to the controller
        // see http://stackoverflow.com/questions/14187963/passing-parameters-javafx-fxml
        final FormController controller = loader.<FormController> getController();
        controller.setModel(model);

        // make a window (Stage)
        Stage stage = new Stage();
        stage.setTitle("form");
        stage.setScene(new Scene(root, 400, 275));

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
    }

    private ShapeContainer model;

    private Shape current;

    /**
     * subscribe to the model
     * 
     * @param model
     */
    private void setModel(ShapeContainer model) {
        this.model = model;
        model.addObserver(this);
        update(model, null);
    }

    /**
     * unsubscribe from the model
     * 
     * @param model
     */
    protected void close() {
        model.deleteObserver(this);
    }

    @FXML
    ListView<Shape> list;

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

    /**
     * We can't add a selection listener by FXML so we add it in the FXML-annotated initialize() method
     */
    @FXML
    protected void initialize() {
        list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Shape>() {
            @Override
            public void changed(ObservableValue<? extends Shape> observable, Shape oldValue, Shape newValue) {
                setCurrentShape(newValue);
            }
        });
    }

    /**
     * handler method delcared in FXML. Instead, we could have added a listener in initialize()
     * 
     * @param e
     */
    public void handleCoordinateChange(ActionEvent e) {
        try {
            model.modifyShape(current, Double.parseDouble(xField.getText()), Double.parseDouble(yField.getText()),
                Double.parseDouble(wField.getText()), Double.parseDouble(hField.getText()));
            errorLabel.setText("");
        } catch (Exception ex) {
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText(ex.getMessage());

        }
    }

    /** update,called by the model */
    @Override
    public void update(Observable o, Object arg) {
        Shape savedCurrent = current;
        // set the items to empty list
        list.setItems(FXCollections.<Shape> observableArrayList());
        // set the items to the new list
        list.setItems(FXCollections.observableArrayList(model.getAllShapes()));
        setCurrentShape(savedCurrent);
    }

    /**
     * Fill the form values, based on the current shape
     * 
     * @param newValue
     */
    void setCurrentShape(Shape newValue) {
        current = newValue;
        if (current != null) {
            xField.disableProperty().set(false);
            yField.disableProperty().set(false);
            wField.disableProperty().set(false);
            hField.disableProperty().set(false);

            xField.setText("" + current.getX());
            yField.setText("" + current.getY());
            wField.setText("" + current.getW());
            hField.setText("" + current.getH());
            surfaceField.setText("" + current.getSurface());
        } else {
            xField.disableProperty().set(true);
            yField.disableProperty().set(true);
            wField.disableProperty().set(true);
            hField.disableProperty().set(true);
            surfaceField.setText("");
        }
    }
}
