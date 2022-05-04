package uz.soccer.http.routes

import cats.effect.{IO, Sync}
import cats.implicits._
import org.http4s.Method.{DELETE, GET, POST, PUT}
import org.http4s.client.dsl.io._
import org.http4s.headers.Authorization
import org.http4s.implicits.http4sLiteralsSyntax
import org.http4s.{AuthScheme, Credentials, Status, Uri}
import uz.soccer.domain.custom.exception.TeamNameInUse
import uz.soccer.domain.types.{TeamId, UserId}
import uz.soccer.domain.{ID, Team, User, types}
import uz.soccer.effects.GenUUID
import uz.soccer.stub_services.{AuthMock, TeamsStub}
import uz.soccer.utils.Generators._
import uz.soccer.utils.HttpSuite

import scala.concurrent.duration.DurationInt

object TeamRoutesSuite extends HttpSuite {
  def teams[F[_]: Sync: GenUUID](inUse: Boolean = false): TeamsStub[F] = new TeamsStub[F] {
    override def create(teamName: types.TeamName): F[Team] =
      if (inUse)
        Sync[F].raiseError[Team](TeamNameInUse(teamName))
      else
        ID.make[F, TeamId].map { teamId =>
          Team(teamId, teamName)
        }

    override def getAll: F[List[Team]]                      = List.empty[Team].pure[F]
    override def getByUserId(userId: UserId): F[List[Team]] = List.empty[Team].pure[F]
    override def update(team: Team): F[Unit]                = Sync[F].unit
    override def delete(teamId: TeamId): F[Unit]            = Sync[F].unit
  }

  test("POST Create team") {
    val gen = for {
      u <- userGen
      t <- createTeamGen
      b <- booleanGen
    } yield (u, t, b)

    forall(gen) { case (user, createTeam, inUse) =>
      for {
        token <- authToken(user)
        req          = POST(createTeam, uri"/team").putHeaders(token)
        routes       = TeamRoutes[IO](teams(inUse)).routes(usersMiddleware)
        shouldReturn = if (inUse) Status.Conflict else Status.Created
        res <- expectHttpStatus(routes, req)(shouldReturn)
      } yield res
    }
  }

  test("GET all teams") {
    forall(userGen) { user =>
      for {
        token <- authToken(user)
        req    = GET(uri"/team").putHeaders(token)
        routes = TeamRoutes[IO](teams()).routes(usersMiddleware)
        res <- expectHttpStatus(routes, req)(Status.Ok)
      } yield res
    }
  }

  test("PUT update team") {
    val gen = for {
      u <- userGen
      t <- teamGen
    } yield (u, t)
    forall(gen) { case (user, team) =>
      for {
        token <- authToken(user)
        req    = PUT(team, uri"/team").putHeaders(token)
        routes = TeamRoutes[IO](teams()).routes(usersMiddleware)
        res <- expectHttpStatus(routes, req)(Status.NoContent)
      } yield res
    }
  }

  test("DELETE team") {
    val gen = for {
      u <- userGen
      t <- teamIdGen
    } yield (u, t)
    forall(gen) { case (user, teamId) =>
      for {
        token <- authToken(user)
        req    = DELETE(Uri.unsafeFromString(s"/team/$teamId")).putHeaders(token)
        routes = TeamRoutes[IO](teams()).routes(usersMiddleware)
        res <- expectHttpStatus(routes, req)(Status.NoContent)
      } yield res
    }
  }

  test("GET user teams") {
    val gen = for {
      u <- userGen
      i <- userIdGen
    } yield (u, i)
    forall(gen) { case (user, userId) =>
      for {
        token <- authToken(user)
        req    = GET(Uri.unsafeFromString(s"/team/$userId")).putHeaders(token)
        routes = TeamRoutes[IO](teams()).routes(usersMiddleware)
        res <- expectHttpStatus(routes, req)(Status.Ok)
      } yield res
    }
  }
}
