package se.kth.csc.iprog.draw.javafx.canvas;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import se.kth.csc.iprog.draw.javafx.beanmodel.ShapeBeanContainer;

public class CanvasController {
    public CanvasController(ShapeBeanContainer model) {
        BorderPane bord = new BorderPane();
        // bord.setLeft(new Label("test"));
        CanvasView canvasView = new CanvasView(model);
        bord.setCenter(canvasView.getCanvas());

        Stage stage = new Stage();
        stage.setTitle("canvas");
        stage.setScene(new Scene(bord));
        stage.show();
    }

}
