package uz.soccer.security

import cats.effect.Sync
import cats.syntax.all._
import pdi.jwt.JwtClaim
import uz.soccer.effects.JwtClock
import uz.soccer.types.TokenExpiration

trait JwtExpire[F[_]] {
  def expiresIn(claim: JwtClaim, exp: TokenExpiration): F[JwtClaim]
}

object JwtExpire {
  def make[F[_]: Sync]: F[JwtExpire[F]] =
    JwtClock[F].utc.map { implicit jClock => (claim: JwtClaim, exp: TokenExpiration) =>
        Sync[F].delay(claim.issuedNow.expiresIn(exp.value.toMillis))
    }
}
