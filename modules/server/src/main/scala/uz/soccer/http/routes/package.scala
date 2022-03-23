package uz.soccer.http

import cats.MonadThrow
import cats.effect.Async
import cats.syntax.all._
import io.circe.{Decoder, Encoder}
import org.http4s.circe.{JsonDecoder, jsonEncoderOf, jsonOf, toMessageSyntax}
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.`Content-Type`
import org.http4s.{EntityDecoder, EntityEncoder, MediaType, Request, Response}

package object routes {
  implicit def deriveEntityEncoder[F[_]: Async, A: Encoder]: EntityEncoder[F, A] = jsonEncoderOf[F, A]

  implicit def deriveEntityDecoder[F[_]: Async, A: Decoder]: EntityDecoder[F, A] = jsonOf[F, A]

  def nameToContentType(filename: String): Option[`Content-Type`] =
    filename.lastIndexOf('.') match {
      case -1 => None
      case i  => MediaType.forExtension(filename.substring(i + 1)).map(`Content-Type`(_))
    }

  def getFileType(filename: String): String =
    filename.drop(filename.lastIndexOf(".") match {
      case -1                  => filename.length
      case extensionStartIndex => extensionStartIndex
    })

  implicit class RefinedRequestDecoder[F[_]: JsonDecoder: MonadThrow](req: Request[F]) extends Http4sDsl[F] {

    def decodeR[A: Decoder](f: A => F[Response[F]]): F[Response[F]] =
      req.asJsonDecode[A].attempt.flatMap {
        case Left(e) =>
          Option(e.getCause) match {
            case Some(c) if c.getMessage.startsWith("Predicate") => BadRequest(c.getMessage)
            case _                                               => UnprocessableEntity()
          }
        case Right(a) => f(a)
      }

  }
}
