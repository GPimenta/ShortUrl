package repository

import model.entity.{AppError, ShortUrl}

import scala.concurrent.Future

trait UrlRepository {
  def save(shortUrl: ShortUrl): Future[Either[AppError, ShortUrl]]
  def findByCode(code: String): Future[Either[AppError, ShortUrl]]
  def nextId(): Future[Long]

}
