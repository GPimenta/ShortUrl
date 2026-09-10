import controller.Routes
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.http.scaladsl.Http
import repository.UrlRepositoryImpl
import service.UrlShortenerServiceImpl

import scala.collection.concurrent.TrieMap
import scala.io.StdIn

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
@main def main(): Unit = {
  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "url-shortener-system")
  implicit val ec = system.executionContext

  val repository = UrlRepositoryImpl(TrieMap.empty)
  val service = UrlShortenerServiceImpl(repository)
  val routes = Routes(service).routes

  val bindingFuture = Http().newServerAt("localhost", 8080).bind(routes)

  println("Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine()
  bindingFuture.flatMap(_.unbind()).onComplete(_ => system.terminate())
}

