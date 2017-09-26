package sample;

import core.LogAnalysis;
import io.LogReader;
import io.LogWriter;
import io.impl.OIOLogReader;
import io.impl.OIOLogWriter;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.spi.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

/**
 * @author yccao
 *
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception{

        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        Scene scene = new Scene(root, 1000, 800);
        stage.setTitle("Wcg日志分析工具");
        stage.setScene(scene);
        stage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }

}
