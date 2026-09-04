package repository

import model.entity.AppError.{CodeCollision, InvalidURL, NotFound}
import model.entity.{AppError, ShortUrl}

import java.util.concurrent.atomic.AtomicLong
import scala.collection.concurrent.TrieMap
import scala.concurrent.{ExecutionContext, Future}

case class UrlRepositoryImpl(map: TrieMap[String, ShortUrl], counter: AtomicLong = new AtomicLong(0L))(using ExecutionContext) extends UrlRepository {

  override def save(shortUrl: ShortUrl): Future[Either[AppError, ShortUrl]] = {
    Future {
      map.put(shortUrl.shortCode, shortUrl) match {
        case Some(value) => Left(CodeCollision)
        case None => Right(shortUrl)
      }
    }
  }

  override def findByCode(code: String): Future[Either[AppError, ShortUrl]] =
    Future{
      map.get(code) match {
        case Some(shortUrl) => Right(shortUrl)
        case None => Left(NotFound)
      }
    }


  override def nextId(): Future[Long] =
    Future(counter.incrementAndGet())
}

