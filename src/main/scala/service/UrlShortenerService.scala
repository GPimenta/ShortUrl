package service

import model.entity.{AppError, ShortUrl}

import scala.concurrent.{ExecutionContext, Future}

trait UrlShortenerService {
  def shorten(originalUrl: String)(using ExecutionContext): Future[Either[AppError, ShortUrl]]
  def resolve(code: String)(using ExecutionContext): Future[Either[AppError, String]]
}
