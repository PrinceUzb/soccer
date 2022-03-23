package uz.soccer.services

import cats._
import cats.implicits._
import dev.profunktor.auth.jwt.JwtToken
import dev.profunktor.redis4cats.RedisCommands
import pdi.jwt.JwtClaim
import uz.soccer.http.auth.users._
import uz.soccer.implicits.CirceDecoderOps

import scala.tools.nsc.tasty.SafeEq

trait UsersAuth[F[_], A] {
  def findUser(token: JwtToken)(claim: JwtClaim): F[Option[A]]
}

object UsersAuth {
  def admin[F[_]: Applicative](
    adminToken: JwtToken,
    adminUser: AdminUser
  ): UsersAuth[F, AdminUser] =
    new UsersAuth[F, AdminUser] {
      def findUser(token: JwtToken)(claim: JwtClaim): F[Option[AdminUser]] =
        (token === adminToken)
          .guard[Option]
          .as(adminUser)
          .pure[F]
    }

  def common[F[_]: Functor](
    redis: RedisCommands[F, String, String]
  ): UsersAuth[F, CommonUser] =
    new UsersAuth[F, CommonUser] {
      def findUser(token: JwtToken)(claim: JwtClaim): F[Option[CommonUser]] =
        redis
          .get(token.value)
          .map {
            _.map { u =>
              CommonUser(u.as[User])
            }
          }
    }

}
