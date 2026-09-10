package service

import model.entity.AppError.InvalidURL
import model.entity.{AppError, ShortUrl}
import org.slf4j.LoggerFactory
import repository.UrlRepository
import utils.{UrlCodec, UrlValidator}

import java.time.LocalDateTime
import scala.concurrent.impl.Promise
import scala.concurrent.{ExecutionContext, Future}

case class UrlShortenerServiceImpl(repository: UrlRepository) extends UrlShortenerService {
  private val logger = LoggerFactory.getLogger(getClass)

  override def shorten(originalUrl: String) (using ExecutionContext): Future[Either[AppError, ShortUrl]] = {

    UrlValidator.validate(originalUrl) match {
      case Left(invalidUrl) =>
        logger.warn(s"Rejected invalid URL: $originalUrl")
        Future.successful(Left(invalidUrl))
      case Right(url) => for {
        id <- repository.nextId()
        code = UrlCodec.toBase62(id)
        shortUrl = ShortUrl(url, code, LocalDateTime.now())
        saveResult <- repository.save(shortUrl)
      } yield {
        saveResult match {
          case Right(_) => logger.info(s"Shortened $url -> $code")
          case Left(err) => logger.warn(s"Failed to save $url: $err")
        }
        saveResult
      }
    }
  }

  override def resolve(code: String)(using ExecutionContext): Future[Either[AppError, String]] =
    repository.findByCode(code).map(result => result.map(shorUrl => shorUrl.originalUrl))
}
