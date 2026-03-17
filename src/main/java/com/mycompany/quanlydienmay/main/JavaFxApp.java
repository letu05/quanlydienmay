package com.mycompany.quanlydienmay.main;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JavaFxApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/quanlydienmay/login.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 520, 500);
        scene.getStylesheets().add(getClass().getResource("/com/mycompany/quanlydienmay/app.css").toExternalForm());

        stage.setTitle("Đăng nhập - Quản Lý Điện Máy");
        stage.setScene(scene);
        stage.setMinWidth(400);
        stage.setMinHeight(420);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}