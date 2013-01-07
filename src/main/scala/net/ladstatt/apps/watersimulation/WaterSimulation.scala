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
import javafx.scene.control.Button
import javafx.scene.input.MouseEvent
import javafx.event.EventHandler
import javafx.scene.control.Tooltip
import javafx.scene.shape.Polygon
import javafx.scene.paint.Paint
import javafx.animation.Timeline
import javafx.animation.Animation
import javafx.animation.KeyFrame
import javafx.util.Duration
import javafx.event.ActionEvent
import javafx.scene.paint.Stop
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.CycleMethod
import scala.collection.JavaConversions._
import javafx.scene.effect.DropShadow

/**
 * A simple graphic demo in javafx
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

  val canvasWidth = 640
  val canvasHeight = 400
  val pointCount = 300
  val margin = 20

  val targetHeight = canvasHeight / 2 // height of the wave

  val hor = canvasHeight * 0.5
  val displacement = hor * 0.7

  val dx = (canvasWidth - 2 * margin) / pointCount
  val sinVals =
    (for (i <- 0 to pointCount) yield {
      if (i == 0) 0 else Math.sin(2 * Math.PI / pointCount * i)
    }).toList
    
  var sinMutable = sinVals.toList

  val corners = List(pointCount * dx + margin, canvasHeight.toDouble) ++ List(margin, canvasHeight.toDouble)

  def mkPoints(sins: List[Double]) = sins.zipWithIndex.map { case (sv, idx) => List((idx * dx + margin).toDouble, (sv * displacement + hor)) }.flatten.toList ++ corners

  val polys = {
    val p = new Polygon(mkPoints(sinVals): _*)
    val stops = List(new Stop(0, Color.BLACK), new Stop(1, Color.BLUE))
    val g = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops)

    p.setFill(g)
    p.setEffect(new DropShadow())
    p
  }

  override def start(primaryStage: Stage): Unit = {
    primaryStage.setTitle("Not yet a water simulation");
    val root = new StackPane()
    val timeline = new Timeline
    timeline.setRate(48)
    timeline.setCycleCount(Animation.INDEFINITE)
    timeline.getKeyFrames().add(
      new KeyFrame(Duration.seconds(1),
        new EventHandler[ActionEvent]() {
          def handle(event: ActionEvent) {
            val (hd :: tail) = sinMutable
            sinMutable = tail ::: List(hd)
            polys.getPoints.setAll(mkPoints(sinMutable).map(Double.box(_)))
          }
        }))
    timeline.play
    root.getChildren.add(polys)
    primaryStage.setScene(new Scene(root, canvasWidth, canvasHeight))
    primaryStage.show()
  }

}

