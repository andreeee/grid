package lib

import play.api.libs.json._
import play.api.libs.functional.syntax._

import com.gu.mediaservice.model._


object ImageExtras {

  val validityDescription = Map(
    "missing_credit"              -> "Missing credit information",
    "missing_description"         -> "Missing description",
    "is_invalid_png"              -> "PNG images with transparency cannot be used"
  )

  private def optToBool[T](o: Option[T]): Boolean =
    o.map(_ => true).getOrElse(false)

  def hasCredit(meta: ImageMetadata) = optToBool(meta.credit)
  def hasDescription(meta: ImageMetadata) = optToBool(meta.description)
  def isInvalidPng(image: Image): Boolean = {
    image.source.mimeType match {
      case Some("image/jpeg") => false
      case _ => image.fileMetadata.colourModelInformation.get("hasAlpha") match {
        case Some("false") => false
        case _ => true
      }
    }
  }

  def validityMap(image: Image): Map[String, Boolean] = Map(
    "missing_credit"              -> !hasCredit(image.metadata),
    "missing_description"         -> !hasDescription(image.metadata),
    "is_invalid_png"              -> isInvalidPng(image)
  )

  def invalidReasons(validityMap: Map[String, Boolean]) = validityMap
    .filter(_._2 == true)
    .map { case (id, _) => id -> validityDescription.get(id) }
    .map {
      case (id, Some(reason)) => id -> reason
      case (id, None) => id -> s"Validity error: ${id}"
    }.toMap

  def isValid(validityMap: Map[String, Boolean]): Boolean =
    !optToBool(validityMap.find(_._2 == true))
}
