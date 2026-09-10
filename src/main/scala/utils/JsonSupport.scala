package utils

import model.dto.request.ShortUrlRequest
import model.dto.response.{AppErrorResponse, ShortUrlResponse}
import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

trait JsonSupport extends SprayJsonSupport{
  implicit val shortUrlRequestFormat: RootJsonFormat[ShortUrlRequest] = jsonFormat1(ShortUrlRequest.apply)
  implicit val shortUrlResponseFormat: RootJsonFormat[ShortUrlResponse] = jsonFormat2(ShortUrlResponse.apply)
  implicit val appErrorResponseFormat: RootJsonFormat[AppErrorResponse] = jsonFormat1(AppErrorResponse.apply)

}
