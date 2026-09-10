package controller

import model.dto.request.ShortUrlRequest
import model.dto.response.{AppErrorResponse, ShortUrlResponse}
import org.apache.pekko.http.scaladsl.server.Directives.*
import org.apache.pekko.http.scaladsl.model.{StatusCodes, Uri}
import model.entity.AppError
import org.apache.pekko.http.javadsl.model.ws.Message
import service.UrlShortenerService
import utils.JsonSupport

import javax.xml.stream.events.EntityReference
import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext

case class Routes(urlShortenerService: UrlShortenerService)(implicit ec: ExecutionContext) extends JsonSupport {
  
  private def toHttpError(error: AppError): (StatusCodes.ClientError, AppErrorResponse) =
    error match {
      case AppError.InvalidURL => (StatusCodes.BadRequest, AppErrorResponse("Invalid Url"))
      case AppError.CodeCollision => (StatusCodes.Conflict, AppErrorResponse("Code code collision"))
      case AppError.NotFound => (StatusCodes.NotFound, AppErrorResponse("Not found"))
    }


  val routes = {
    path("shorten") {
      post {
        entity(as[ShortUrlRequest]) { req =>
          onSuccess(urlShortenerService.shorten(req.url)) {
            case Left(error) =>
              val (status, body) = toHttpError(error)
              complete(status -> body)
            case Right(shortUrl) =>
              complete(StatusCodes.Created -> ShortUrlResponse(shortUrl.shortCode,s"http://localhost:8080/${shortUrl.shortCode}"))
          }
        }
      }
    } ~
    path(Segment) { code =>
      get {
        onSuccess(urlShortenerService.resolve(code)) {
          case Left(error) =>
            val (status, body) = toHttpError(error)
            complete(status -> body)
          case Right(originalUrl) =>
            redirect(Uri(originalUrl), StatusCodes.Found)
        }
      }
    }
  }
}
