package uz.soccer.db.algebras

import uz.soccer.domain.custom.refinements.EmailAddress
import tsec.passwordhashers.PasswordHash
import tsec.passwordhashers.jca.SCrypt

trait IdentityProvider[F[_], U] {
  def findByEmail(email: EmailAddress): F[Option[U]]
  def retrievePass(email: EmailAddress): F[Option[PasswordHash[SCrypt]]]
}
