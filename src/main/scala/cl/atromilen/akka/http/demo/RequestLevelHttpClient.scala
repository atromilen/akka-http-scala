package cl.atromilen.akka.http.demo

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import org.slf4j.LoggerFactory

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

class RequestLevelHttpClient {
  val logger = LoggerFactory.getLogger(getClass)
  implicit val system = ActorSystem(Behaviors.empty, "SingleRequest")
  implicit val executionContext = system.executionContext

  def sendRequest= {
    val responseFuture: Future[String] =
      Http().singleRequest(HttpRequest(uri = "https://api.ipify.org/?format=json"))
        .flatMap(_.entity.toStrict(2.seconds))
        .map(_.data.utf8String)

    responseFuture.
      onComplete {
        case Success(value) => logger.info(value)
        case Failure(exception) => logger.error("something wrong")
      }
  }
}

object RequestLevelHttpClient extends App{
  val request = new RequestLevelHttpClient
  request.sendRequest
}
