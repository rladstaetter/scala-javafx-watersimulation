package net.ladstatt.apps.watersimulation

import javafx.application.Application
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.fxml.JavaFXBuilderFactory
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import java.net.URL
import java.util.ResourceBundle
import javafx.scene.Group

/**
 * A simple application which demonstrates a water simulation in javafx.
 * 
 * Based on the ideas of http://gamedev.tutsplus.com/tutorials/implementation/make-a-splash-with-2d-water-effects/
 * 
 * See also blog post 
 */
object WaterSimulation {

  def main(args: Array[String]): Unit = {
    Application.launch(classOf[WaterSimulation], args: _*)
  }

}


class WaterSimulation extends javafx.application.Application {

  override def start(primaryStage: Stage): Unit = {
    primaryStage.setTitle("Water Simulation");
    val root = new Group()
    primaryStage.setScene(new Scene(root, 600,400))
    primaryStage.show()
  }

}

