package model.entity

import java.time.LocalDateTime

case class ShortUrl(originalUrl: String, shortCode: String, createdAt: LocalDateTime)