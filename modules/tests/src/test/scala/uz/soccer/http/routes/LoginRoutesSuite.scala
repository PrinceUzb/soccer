package uz.soccer.http.routes

import cats.effect.IO
import cats.implicits._
import eu.timepit.refined.types.string.NonEmptyString
import org.http4s.Method.POST
import org.http4s.Status
import org.http4s.client.dsl.io._
import org.http4s.implicits.http4sLiteralsSyntax
import uz.soccer.config.jwtConfig
import uz.soccer.domain.auth._
import uz.soccer.http.auth.users.{User, UserWithPassword}
import uz.soccer.security.Crypto
import uz.soccer.services.Users
import uz.soccer.stub_services.{AuthMock, UsersStub}
import uz.soccer.utils.Generators.{booleanGen, userCredentialGen, userGen}
import uz.soccer.utils.HttpSuite

object LoginRoutesSuite extends HttpSuite {
  def users(user: User, pass: Password, crypto: Crypto): Users[F] = new UsersStub[F] {
    override def find(
      username: UserName
    ): F[Option[UserWithPassword]] =
      if (user.name.value.equalsIgnoreCase(username.value))
        Option(UserWithPassword(user.id, user.name, crypto.encrypt(pass))).pure[F]
      else
        none[UserWithPassword].pure[F]
  }

  test("POST login") {
    val gen = for {
      u <- userGen
      c <- userCredentialGen
      b <- booleanGen
    } yield (u, c, b)

    forall(gen) { case (user, c, isCorrect) =>
      for {
        crypto <- Crypto[IO](jwtConfig.passwordSalt.value)
        auth   <- AuthMock[IO](users(user, c.password.toDomain, crypto), crypto)
        (postData, shouldReturn) =
          if (isCorrect)
            (c.copy(username = UserNameParam(NonEmptyString.unsafeFrom(user.name.value))), Status.Ok)
          else
            (c, Status.Forbidden)
        req    = POST(postData, uri"/auth/login")
        routes = LoginRoutes[IO](auth).routes
        res <- expectHttpStatus(routes, req)(shouldReturn)
      } yield res
    }
  }
}
