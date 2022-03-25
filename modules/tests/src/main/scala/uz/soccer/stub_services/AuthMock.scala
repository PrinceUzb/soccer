package uz.soccer.stub_services

import cats.effect.Sync
import cats.implicits._
import uz.soccer.config.jwtConfig
import uz.soccer.security.{Crypto, JwtExpire, Tokens}
import uz.soccer.services.{Auth, Users}

object AuthMock {

  def apply[F[_]: Sync](users: Users[F], crypto: Crypto): F[Auth[F]] =
    for {
      tokens <- JwtExpire[F].map(Tokens.make[F](_, jwtConfig.tokenConfig.value, jwtConfig.tokenExpiration))
      auth = Auth[F](jwtConfig.tokenExpiration, tokens, users, RedisClientMock[F], crypto)
    } yield auth
}
