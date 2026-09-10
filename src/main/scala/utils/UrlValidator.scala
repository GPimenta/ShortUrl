package utils

import model.entity.AppError
import java.net.{URI, URL}
import scala.util.{Failure, Success, Try}

object UrlValidator {

  def validate(url: String): Either[AppError, String] = {
    val allowedSchemes = Set("http", "https")

    url match {
      case _ if url.isBlank => Left(AppError.InvalidURL)
      case nonEmptyUrl =>
        Try(new URI(nonEmptyUrl)) match {
          case Failure(exception) => Left(AppError.InvalidURL)
          case Success(uri) if allowedSchemes.contains(uri.getScheme) =>
            Right(uri.toString)
          case Success(_) => Left(AppError.InvalidURL)
      }
    }
  }
}
