package se.kth.csc.iprog.draw.javafx.canvas;

import java.io.IOException;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import se.kth.csc.iprog.draw.javafx.beanmodel.ShapeBean;
import se.kth.csc.iprog.draw.javafx.beanmodel.ShapeBeanContainer;
import se.kth.csc.iprog.draw.model.Ellipse;
import se.kth.csc.iprog.draw.model.Rectangle;
import se.kth.csc.iprog.draw.model.Segment;

/**
 * CanvasController, mediator between model and view (like FormController, it does not observe the model, but uses
 * binding. Delegates to interaction controllers (selection, drawing)
 * 
 * @author cristi
 */
public class CanvasController {

    public static void showCanvasView(ShapeBeanContainer model) {
        FXMLLoader loader = new FXMLLoader(CanvasController.class.getResource("CanvasView.fxml"));

        // load the FXML
        Parent root = null;
        try {
            root = (Parent) loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // passing the model parameter to the controller
        // see http://stackoverflow.com/quest ions/14187963/passing-parameters-javafx-fxml
        final CanvasController controller = loader.<CanvasController> getController();
        controller.setModel(model);

        Stage stage = new Stage();
        stage.setTitle("canvas");
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    Pane canvas;

    @FXML
    ToggleGroup shapeChooser;

    /** Controller for moving shapes around. It subscribes to each shape */
    SelectionController selectionController = new SelectionController();

    /** Controller to create new shapes. Subscribes to the canvas */
    ShapeDrawingController shapeDrawingController;

    /** Read the model and bind it to the canvas children (shape) list */
    void setModel(final ShapeBeanContainer model) {
        shapeDrawingController = new ShapeDrawingController(model, canvas, shapeChooser);

        // read the current shapes and add them to the view
        for (ShapeBean sb : model.getShapeBeans()) {
            canvas.getChildren().add(makeNode(sb));
        }

        // keep the shape list in synch between model and view
        model.getShapeBeans().addListener(new ListChangeListener<ShapeBean>() {
            @Override
            public void onChanged(Change<? extends ShapeBean> chg) {
                chg.next();
                if (chg.wasAdded()) {
                    canvas.getChildren().add(makeNode(model.getShapeBeans().get(chg.getFrom())));
                }
                if (chg.wasRemoved()) {
                    canvas.getChildren().remove(chg.getFrom());
                }
            }
        });

    }

    ModelViewBinding mvBinder = new ModelViewBinding();

    /**
     * create a UI shape out of a model shape. The two objects (beans) will be bound to each other
     * 
     * @param shapeBean
     * @return
     */
    protected Node makeNode(final ShapeBean shapeBean) {
        Shape ret = null;
        if (shapeBean.getShape() instanceof Segment) {
            Line line = new Line();
            line.layoutXProperty().bindBidirectional(shapeBean.xProperty());
            line.layoutYProperty().bindBidirectional(shapeBean.yProperty());
            line.endXProperty().bindBidirectional(shapeBean.wProperty());
            line.endYProperty().bindBidirectional(shapeBean.hProperty());

            ret = line;
        }
        if (shapeBean.getShape() instanceof Rectangle) {
            javafx.scene.shape.Rectangle rect = new javafx.scene.shape.Rectangle();
            rect.layoutXProperty().bindBidirectional(shapeBean.xProperty());
            rect.layoutYProperty().bindBidirectional(shapeBean.yProperty());
            rect.widthProperty().bindBidirectional(shapeBean.wProperty());
            rect.heightProperty().bindBidirectional(shapeBean.hProperty());

            ret = rect;
        } else if (shapeBean.getShape() instanceof Ellipse) {
            javafx.scene.shape.Ellipse ellipse = new javafx.scene.shape.Ellipse();

            // set the math relations between ellipse model and view using a ModelViewBinding
            // centerX=x+w/2
            mvBinder.bind(ellipse.layoutXProperty(), shapeBean.xProperty().add(shapeBean.wProperty().divide(2)));
            mvBinder.bind(ellipse.layoutYProperty(), shapeBean.yProperty().add(shapeBean.hProperty().divide(2)));

            // x=centerX-radiusX
            mvBinder.bind(shapeBean.xProperty(), ellipse.layoutXProperty().subtract(ellipse.radiusXProperty()));
            mvBinder.bind(shapeBean.yProperty(), ellipse.layoutYProperty().subtract(ellipse.radiusYProperty()));

            // radiusX= w/2
            mvBinder.bind(ellipse.radiusXProperty(), shapeBean.wProperty().divide(2));
            mvBinder.bind(ellipse.radiusYProperty(), shapeBean.hProperty().divide(2));

            // w= radius*2
            mvBinder.bind(shapeBean.wProperty(), ellipse.radiusXProperty().multiply(2));
            mvBinder.bind(shapeBean.hProperty(), ellipse.radiusYProperty().multiply(2));

            ret = ellipse;
        }

        selectionController.addTo(ret);

        return ret;
    }

}
