package uz.soccer.domain.custom.exception

import scala.util.control.NoStackTrace

final case class MultipartDecodeError(cause: String) extends NoStackTrace
