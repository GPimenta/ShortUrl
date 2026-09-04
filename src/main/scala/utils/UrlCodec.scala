package utils

import scala.annotation.tailrec

object UrlCodec {
  private val alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"

  def toBase62(id: Long): String =
    if id == 0 then "0"
    else
      @tailrec
        def helper(n: Long, accum: String): String =
          if n == 0 then accum
          else helper(n / 62, alphabet((n % 62).toInt).toString + accum)

        helper(id, "")
  
  def fromBase62(code: String): Long =
    @tailrec
    def helper(s: String, accum: Long): Long =
      if s.isEmpty then accum
      else helper(s.tail, accum * 62 + alphabet.indexOf(s.head))

    helper(code, 0)

}
