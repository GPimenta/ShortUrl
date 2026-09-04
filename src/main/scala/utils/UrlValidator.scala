package utils

import model.entity.AppError
import java.net.{URI, URL}
import scala.util.{Failure, Success, Try}

object UrlValidator {

  def validate(url: String): Either[AppError, String] = {
    url match {
      case _ if url.isBlank => Left(AppError.InvalidURL)
      case nonEmptyUrl =>
        Try(new URI(nonEmptyUrl)) match {
          case Failure(exception) => Left(AppError.InvalidURL)
          case Success(uri) if uri.toURL.getProtocol != "https" && uri.toURL.getProtocol != "http" =>
            Left(AppError.InvalidURL)
          case Success(validUrl) => Right(validUrl.toString)
      }
    }
  }
}
