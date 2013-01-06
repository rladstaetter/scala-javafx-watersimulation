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
import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color

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

  val imgWidth = 1024
  val imgHeight = 768

  def randColor = Color.rgb(0, 0, 0)

  def createImage(): ImageView = {
    val imageView = new ImageView()
    val writeableImage = new WritableImage(imgWidth, imgHeight)
    val pw = writeableImage.getPixelWriter()
    for {
      x <- 0 to imgWidth - 1
      y <- 0 to imgHeight - 1
    } pw.setColor(x, y, randColor)

    imageView.setImage(writeableImage)
    imageView
  }

  override def start(primaryStage: Stage): Unit = {
    primaryStage.setTitle("Water Simulation");
    val root = new StackPane()
    root.getChildren.add(createImage)
    primaryStage.setScene(new Scene(root, imgWidth, imgHeight))
    primaryStage.show()
  }

}

