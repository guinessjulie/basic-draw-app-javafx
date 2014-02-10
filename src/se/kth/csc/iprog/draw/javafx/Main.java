package se.kth.csc.iprog.draw.javafx;

import javafx.application.Application;
import javafx.stage.Stage;
import se.kth.csc.iprog.draw.javafx.beanmodel.ShapeBeanContainer;
import se.kth.csc.iprog.draw.javafx.canvas.CanvasController;
import se.kth.csc.iprog.draw.javafx.form.FormController;
import se.kth.csc.iprog.draw.model.ShapeContainer;

/**
 * Java FX version of the drawing app.
 * 
 * @author cristi
 */
public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {

        ShapeBeanContainer model = new ShapeBeanContainer();

        // some test shapes
        model.addShape(ShapeContainer.SEGMENT, 10, 20, 30, 40);
        model.addShape(ShapeContainer.RECTANGLE, 40, 30, 30, 40);
        model.addShape(ShapeContainer.ELLIPSE, 70, 70, 30, 40);

        // show a form view, this will create a stage. primaryStage is not used
        FormController.showFormView(model);

        // show a canvas view
        new CanvasController(model);

    }

    public static void main(String[] args) {
        launch(args);
    }
}
