package net.ladstatt.apps.watersimulation

import scala.collection.JavaConversions.seqAsJavaList

import javafx.animation.Animation
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.application.Application
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.effect.DropShadow
import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.Stop
import javafx.scene.shape.Polygon
import javafx.stage.Stage
import javafx.util.Duration

/**
 * A simple graphic demo in javafx
 *
 * Based on the ideas of http://gamedev.tutsplus.com/tutorials/implementation/make-a-splash-with-2d-water-effects/
 *
 * See also blog post at http://ladstatt.blogspot.co.at/2013/01/2d-water-effects-with-javafx-and-scala.html
 */
object WaterSimulation {

  def main(args: Array[String]): Unit = {
    Application.launch(classOf[WaterSimulation], args: _*)
  }

}

class WaterSimulation extends javafx.application.Application {

  val canvasWidth = 820
  val canvasHeight = 600
  val pointCount = 250
  val margin = 20
  val splash = 0.3

  val targetHeight = canvasHeight / 2

  case class Spring(position: Double, velocity: Double)

  val hor = canvasHeight * 0.5

  val splashPointCount = pointCount * splash

  val displacement = hor * splash

  val tension = 0.025
  val dampening = 0.05
  val spread = 0.25

  val dx = (canvasWidth - 2 * margin) / pointCount

  // see http://en.wikipedia.org/wiki/Panthalassa
  val panthalassa = {
    val seaLevel = (for (i <- 0 to ((pointCount - splashPointCount) / 2).toInt) yield Spring(hor, 0.toDouble)).toList
    val splashPoints = (for (i <- 0 to splashPointCount.toInt) yield Spring(hor + displacement, 0.toDouble)).toList
    seaLevel ++ splashPoints ++ seaLevel
  }

  var ocean = panthalassa

  val corners = List(ocean.size * dx + margin, canvasHeight.toDouble) ++ List(margin, canvasHeight.toDouble)

  def mkPoints(springs: List[Spring]) =
    springs.zipWithIndex.map { case (Spring(pos, velocity), idx) => List((idx * dx + margin).toDouble, pos) }.flatten.toList ++ corners

  val polys = {
    val p = new Polygon(mkPoints(ocean): _*)
    val stops = List(new Stop(0, Color.BLACK), new Stop(1, Color.BLUE))
    val g = new LinearGradient(0, 1, 0, 0, true, CycleMethod.NO_CYCLE, stops)
    p.setFill(g)
    p.setEffect(new DropShadow())
    p
  }

  def updateOcean(spread: Double, tension: Double, dampening: Double, ocean: List[Spring]): List[Spring] = {
    val dampedOcean = dampAndTense(ocean, dampening, tension)

    val (lefts, rights) = deltas(hor, spread, dampedOcean)
    val fasterOcean = changeVelocity(dampedOcean, lefts, rights)

    changePos(fasterOcean, lefts, rights)
  }

  def dampAndTense(ocean: List[Spring], dampening: Double, tension: Double): List[Spring] = {
    ocean.map {
      case Spring(pos, speed) => {
        val x = pos - hor
        val newSpeed = -tension * x + speed - speed * dampening
        val newPos = pos + newSpeed
        Spring(newPos, newSpeed)
      }
    }
  }

  def deltas(offset: Double, spread: Double, springs: List[Spring]): (List[Double], List[Double]) = {
    val normedSprings = springs.map(_.position - offset)
    ((for (List(a, b) <- normedSprings.sliding(2)) yield spread * (b - a)).toList ::: List(0.toDouble),
      0.toDouble :: (for (List(d, c) <- normedSprings.reverse.sliding(2)) yield spread * (c - d)).toList.reverse)
  }

  def changeVelocity(springs: List[Spring], left: List[Double], right: List[Double]): List[Spring] =
    {
      val velocities = left zip right map { case (a, b) => a + b }
      springs zip velocities map { case (s, l) => s.copy(velocity = s.velocity + l) }
    }

  def changePos(springs: List[Spring], left: List[Double], right: List[Double]): List[Spring] =
    {
      val leftSprings = springs zip left map { case (s, l) => s.copy(position = s.position + l) }
      leftSprings zip right map { case (s, l) => s.copy(position = s.position + l) }
    }

  override def start(primaryStage: Stage): Unit = {
    primaryStage.setTitle("Almost a water simulation");
    val root = new StackPane()
    val b = new Button("splash!")

    val timeline = new Timeline
    timeline.setRate(48)
    timeline.setCycleCount(Animation.INDEFINITE)
    timeline.getKeyFrames().add(
      new KeyFrame(Duration.seconds(1),
        new EventHandler[ActionEvent]() {
          def handle(event: ActionEvent) {
            val newOcean = updateOcean(spread, tension, dampening, ocean)
            polys.getPoints().setAll(mkPoints(newOcean).map(Double.box(_)))
            ocean = newOcean
          }
        }))

    b.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler[MouseEvent] {
      def handle(event: MouseEvent) {
        b.setVisible(false)
        timeline.play
      }
    })

    root.getChildren.addAll(polys, b)
    primaryStage.setScene(new Scene(root, canvasWidth, canvasHeight))
    primaryStage.show()
  }

}

