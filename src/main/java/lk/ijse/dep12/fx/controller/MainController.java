package lk.ijse.dep12.fx.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;


public class MainController {
    public TreeView<String> trComponent;
    public ScrollPane scrolPaneWrapper;
    public TilePane tilePaneContainer;
    public StackPane stackPane;

    public void initialize() {
        stackPane.setVisible(true);
        scrolPaneWrapper.setVisible(false);
        tilePaneContainer.getChildren().clear();

        TreeItem<String> rootNode = new TreeItem<>();
        rootNode.setValue("This PC");
        rootNode.setGraphic(getIcon("pc"));
        trComponent.setRoot(rootNode);
        rootNode.setExpanded(true);

        for (Path disk : FileSystems.getDefault().getRootDirectories()) {
            TreeItem<String> diskNode = new TreeItem<>(disk.toString());
            diskNode.setGraphic(getIcon("disk"));
            rootNode.getChildren().add(diskNode);

            setFolder(disk, diskNode);
            rootNode.getChildren().getFirst().setExpanded(true);

        }
    }

    private ImageView getIcon(String icon) {
        ImageView imageView = new ImageView(switch (icon) {
            case "pc" -> "/icon/computer.png";
            case "disk" -> "/icon/external-hard-drive.png";
            case "folder" -> "/icon/folder.png";
            case "folder-open" -> "/icon/open-folder.png";
            case "image" -> "/icon/picture.png";
            case null, default -> throw new RuntimeException("Invalid icon");
        });
        imageView.setFitWidth(24);
        imageView.setPreserveRatio(true);
        return imageView;
    }


    private void setFolder(Path disk, TreeItem<String> diskNode) {

        if (Files.isDirectory(disk)) {
         //   System.out.println("disk directory " + disk);
            diskNode.expandedProperty().addListener((observable, previous, current) -> {
                if (!current || !diskNode.getChildren().isEmpty()) return;

                if(scrolPaneWrapper.isVisible()){
                    tilePaneContainer.getChildren().clear();
                    scrolPaneWrapper.setVisible(false);
                    stackPane.setVisible(true);
                }

                try {
                    try (DirectoryStream<Path> paths = Files.newDirectoryStream(disk)) {
                        for (Path folder : paths) {
                            TreeItem<String> folderNode = new TreeItem<>(folder.getFileName().toString());

                            if (Files.isRegularFile(folder)) {
                                if (folder.getFileName().toString().endsWith("jpeg") || folder.getFileName().toString().endsWith("jpg") ||
                                        folder.getFileName().toString().endsWith("bmp") || folder.getFileName().toString().endsWith("png") ||
                                        folder.getFileName().toString().endsWith("gif")) {
//
                                    TreeItem<String> childImage = new TreeItem<>(folder.getFileName().toString());
                                    childImage.setGraphic(getIcon("image"));
                                    diskNode.getChildren().add(childImage);

                                    ImageView imageView = new ImageView(folder.toUri().toString());
                                    imageView.setFitWidth(200);
                                    imageView.setPreserveRatio(true);
                                    tilePaneContainer.getChildren().add(imageView);
                                    if (!scrolPaneWrapper.isVisible()) {
                                        scrolPaneWrapper.setVisible(true);
                                        stackPane.setVisible(false);

                                    }

                                }


                            } else {
                                folderNode.setGraphic(getIcon("folder"));
                                diskNode.getChildren().add(folderNode);

                                folderNode.expandedProperty().addListener((o, p, c) -> {
                                    folderNode.setGraphic(getIcon(c ? "folder-open" : "folder"));

                                });

                                setFolder(folder, folderNode);
                            }

                        }
                    }
                } catch (IOException e) {
                    new Alert(Alert.AlertType.ERROR, "Con not open the folder").show();
                    e.printStackTrace();
                }
            });
        }

    }
}

