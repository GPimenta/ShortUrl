package service

import model.entity.AppError.InvalidURL
import model.entity.{AppError, ShortUrl}
import repository.UrlRepository
import utils.{UrlCodec, UrlValidator}

import java.time.LocalDateTime
import scala.concurrent.impl.Promise
import scala.concurrent.{ExecutionContext, Future}

case class UrlShortenerServiceImpl(repository: UrlRepository) extends UrlShortenerService {

  override def shorten(originalUrl: String) (using ExecutionContext): Future[Either[AppError, ShortUrl]] =
    UrlValidator.validate(originalUrl) match {
      case Left(invalidUrl) => Future.successful(Left(invalidUrl))
      case Right(url) => for {
        id <- repository.nextId()
        code = UrlCodec.toBase62(id)
        shortUrl = ShortUrl(url, code, LocalDateTime.now())
        saveResult <- repository.save(shortUrl)
      } yield saveResult
    }

  override def resolve(code: String)(using ExecutionContext): Future[Either[AppError, String]] =
    repository.findByCode(code).map(result => result.map(shorUrl => shorUrl.originalUrl))
}
