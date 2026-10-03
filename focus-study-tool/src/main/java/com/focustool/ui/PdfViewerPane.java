package com.focustool.ui;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Simple PDF reading pane: open a file, scroll through rendered pages.
 * This is the only "content" surface in the app besides the whitelisted browser.
 */
public class PdfViewerPane extends BorderPane {

    private PDDocument document;
    private PDFRenderer renderer;
    private int currentPage = 0;
    private int pageCount = 0;

    private final ImageView imageView = new ImageView();
    private final Label pageLabel = new Label("No PDF open");
    private final Label fileLabel = new Label("");

    public PdfViewerPane() {
        setPadding(new Insets(8));
        setTop(buildToolbar());

        imageView.setPreserveRatio(true);
        imageView.fitWidthProperty().bind(widthProperty().subtract(40));

        ScrollPane scrollPane = new ScrollPane(imageView);
        scrollPane.setFitToWidth(true);
        setCenter(scrollPane);
    }

    private ToolBar buildToolbar() {
        Button openBtn = new Button("Open PDF");
        openBtn.setOnAction(e -> openFile());

        Button prevBtn = new Button("◀ Prev");
        prevBtn.setOnAction(e -> showPage(currentPage - 1));

        Button nextBtn = new Button("Next ▶");
        nextBtn.setOnAction(e -> showPage(currentPage + 1));

        fileLabel.setPadding(new Insets(0, 0, 0, 12));

        ToolBar bar = new ToolBar(openBtn, prevBtn, pageLabel, nextBtn, fileLabel);
        return bar;
    }

    private void openFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open a PDF to study");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF files", "*.pdf"));
        File file = chooser.showOpenDialog(getScene().getWindow());
        if (file == null) return;

        try {
            closeCurrentDocument();
            document = PDDocument.load(file);
            renderer = new PDFRenderer(document);
            pageCount = document.getNumberOfPages();
            fileLabel.setText(file.getName());
            showPage(0);
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Couldn't open this PDF: " + ex.getMessage());
            alert.showAndWait();
        }
    }

    private void showPage(int pageIndex) {
        if (renderer == null || pageIndex < 0 || pageIndex >= pageCount) return;
        try {
            BufferedImage bImage = renderer.renderImageWithDPI(pageIndex, 120);
            Image fxImage = SwingFXUtils.toFXImage(bImage, null);
            imageView.setImage(fxImage);
            currentPage = pageIndex;
            pageLabel.setText("Page " + (currentPage + 1) + " / " + pageCount);
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Couldn't render this page: " + ex.getMessage());
            alert.showAndWait();
        }
    }

    private void closeCurrentDocument() throws Exception {
        if (document != null) {
            document.close();
            document = null;
        }
    }
}
