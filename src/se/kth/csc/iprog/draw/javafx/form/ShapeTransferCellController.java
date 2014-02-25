package se.kth.csc.iprog.draw.javafx.form;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;

import javafx.event.EventHandler;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import se.kth.csc.iprog.draw.javafx.beanmodel.ShapeBean;
import se.kth.csc.iprog.draw.javafx.beanmodel.ShapeBeanContainer;
import se.kth.csc.iprog.draw.model.Shape;

/**
 * Drag and drop listener on the list cells, rather than on the list itself. This is needed to determine the drop
 * location precisely.
 * 
 * @author cristi
 */
public class ShapeTransferCellController {

    ShapeBean dragged;

    public ShapeTransferCellController(final ShapeBeanContainer model, final LabelCell<ShapeBean> cell) {

        // drag start
        cell.setOnDragDetected(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                // start drag and drop look-and-feel
                Dragboard db = cell.startDragAndDrop(TransferMode.MOVE);

                // save the dragged item for later removal
                dragged = cell.getItem();

                // put the shape in the clipboard as a File list
                ClipboardContent content = new ClipboardContent();

                Shape shape = dragged.getShape();
                try {
                    File shapeFile = File.createTempFile(shape.getType(), ".properties");
                    FileWriter out = new FileWriter(shapeFile);
                    shape.writeTo(out);
                    out.close();
                    content.putFiles(Arrays.asList(shapeFile));

                    StringWriter sout = new StringWriter();
                    shape.writeTo(sout);
                    content.putString(sout.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                db.setContent(content);

                event.consume();
            }
        });

        // drag has finished, on some other component
        cell.setOnDragDone(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {

                // remove the dragged shape as it was moved somewhere else
                if (event.getTransferMode() == null || event.getTransferMode() == TransferMode.MOVE) {
                    model.removeShapeBean(dragged);
                }
                event.consume();
            }
        });

        // potential drop target
        cell.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                if (db.hasFiles()) {
                    // signal the possible drop actions
                    event.acceptTransferModes(TransferMode.COPY, TransferMode.MOVE);
                } else {
                    event.consume();
                }
            }
        });

        // drop target
        cell.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {

                // calculate drop location: the index of the cell, maximum the current size
                int index = cell.getIndex();

                // below the last item, there may be empty cells
                if (index > model.getAllShapes().size())
                    index = model.getAllShapes().size();

                Dragboard db = event.getDragboard();

                boolean success = false;
                if (db.hasFiles()) {

                    // for each file, add the shape to the model
                    for (File f : db.getFiles()) {
                        try {
                            FileReader in = new FileReader(f);
                            model.addShapeBean(Shape.readFrom(in), index++);
                            in.close();
                            success = true;
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                else if (db.hasString()) {
                    try {
                        model.addShapeBean(Shape.readFrom(new StringReader(db.getString())), index);
                        success = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                // drop completed
                event.setDropCompleted(success);
                event.consume();
            }
        });
    }
}
