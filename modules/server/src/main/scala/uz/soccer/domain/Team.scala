package uz.soccer.domain
import derevo.cats.show
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import uz.soccer.domain.types.{TeamId, TeamName}

@derive(decoder, encoder, show)
case class Team (UUID: TeamId, name: TeamName)
