import model.entity.AppError.{InvalidURL, NotFound}
import model.entity.ShortUrl
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers
import repository.UrlRepositoryImpl
import service.{UrlShortenerService, UrlShortenerServiceImpl}

import scala.collection.concurrent.TrieMap

class UrlShortenerServiceTest extends AsyncFlatSpec with Matchers {

  def freshService(): UrlShortenerService = {
    val repo = UrlRepositoryImpl(TrieMap.empty)
    UrlShortenerServiceImpl(repo)
  }

  "shorten" should "return a ShortUrl for a valid URL" in {
    val service = freshService()
    service.shorten("https://example.com").map {
      case Left(error) =>
        fail(s"Expected success but got error: $error")
      case Right(shortUrl) => {
        shortUrl.originalUrl shouldBe "https://example.com"
        shortUrl.shortCode should not be empty
      }
    }
  }

  it should "return InvalidURL for a malformed URL" in {
    val service = freshService()
    service.shorten("notarealurl").map {
      case Left(error) => error shouldBe InvalidURL
      case Right(shortUrl) => fail(s"Expected fail but got success: $shortUrl")
    }
  }


  "resolve" should "return NotFound for a code that doesn't exist" in {
    val service = freshService()
    service.resolve("doesnotexist").map {
      case Left(error) => error shouldBe NotFound
      case Right(url) => fail(s"Expected fail but got success: $url")
    }
  }
}
