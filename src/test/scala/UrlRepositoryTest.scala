import model.entity.AppError.{CodeCollision, NotFound}
import model.entity.ShortUrl
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers
import repository.{UrlRepository, UrlRepositoryImpl}
import utils.UrlCodec

import java.time.LocalDateTime
import scala.collection.concurrent.TrieMap

class UrlRepositoryTest extends AsyncFlatSpec with Matchers {

  def freshRepository(): UrlRepository = {
    UrlRepositoryImpl(TrieMap.empty)
  }

  "save" should "return a ShortUrl for a new shortCode" in {
    val repo = freshRepository()

    val shortCode = UrlCodec.toBase62(1)
    val url = "https://example.com"
    val timeNow = LocalDateTime.now()
    val shortUrl = ShortUrl(url, shortCode, timeNow)

    repo.save(shortUrl).map {
      case Left(error) =>
        fail(s"Expected success but got error: $error")
      case Right(shortUrlSaved) =>
        shortUrlSaved.shortCode shouldBe shortCode
        shortUrlSaved.originalUrl shouldBe url
        shortUrlSaved.createdAt shouldBe timeNow
    }
  }

  it should "return a code collision for already ShortUrl in repo" in {
    val repo = freshRepository()

    val shortCode1 = UrlCodec.toBase62(1)
    val url1 = "https://example.com"
    val timeNow1 = LocalDateTime.now()
    val shortUrl1 = ShortUrl(url1, shortCode1, timeNow1)

    val url2 = "https://example2.com"
    val timeNow2 = LocalDateTime.now()
    val shortUrl2 = ShortUrl(url2, shortCode1, timeNow2)

    repo.save(shortUrl1).flatMap {
      case Left(error) =>
        fail(s"Expected success but got error: $error")
      case Right(shortUrlSaved1) =>
        repo.save(shortUrl2).map {
          case Left(codeCollision) =>
            codeCollision shouldBe CodeCollision
          case Right(saved) =>
            fail(s"Expected collision but got success: $saved")
        }
    }
  }

  "findByCode" should "return ShortUrl for correct code" in {
    val repo = freshRepository()

    val shortCode1 = UrlCodec.toBase62(1)
    val url1 = "https://example.com"
    val timeNow1 = LocalDateTime.now()
    val shortUrl1 = ShortUrl(url1, shortCode1, timeNow1)

    repo.save(shortUrl1).flatMap {
      case Left(error) => fail(s"Expected success but got error: $error")
      case Right(_) =>
        repo.findByCode(shortUrl1.shortCode).map {
          case Left(error) => fail(s"Expected success but got error: $error")
          case Right(found) => found shouldBe shortUrl1
        }
    }
  }

  it should "return NotFound for no code found" in {
    val repo = freshRepository()

    val shortCode1 = UrlCodec.toBase62(1)
    val url1 = "https://example.com"
    val timeNow1 = LocalDateTime.now()
    val shortUrl1 = ShortUrl(url1, shortCode1, timeNow1)

    repo.save(shortUrl1)

    val shortCode2 = UrlCodec.toBase62(2)

    repo.findByCode(shortCode2).map {
      case Left(notFound) =>
        notFound shouldBe NotFound
      case Right(value) =>
        fail(s"Expected fail but got success: $value")
    }

  }

}
