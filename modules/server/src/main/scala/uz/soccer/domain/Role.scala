package uz.soccer.domain

import cats.Show
import io.circe.{Decoder, Encoder}

sealed abstract class Role(val value: String)

object Role {
  case object ADMIN extends Role("male")
  case object USER  extends Role("female")

  val roles = List(ADMIN, USER)

  def find(value: String): Option[Role] =
    roles.find(_.value == value.toLowerCase)

  def unsafeFrom(value: String): Role =
    find(value).getOrElse(throw new IllegalArgumentException(s"value doesn't match [ $value ]"))

  implicit val encStatus: Encoder[Role] = Encoder.encodeString.contramap[Role](_.value)
  implicit val decStatus: Decoder[Role] = Decoder.decodeString.map(unsafeFrom)
  implicit val show: Show[Role] = Show.show(_.value)

}