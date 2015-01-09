package root.base.plugin

import sbt._
import Keys._
import root.base._
import root.Libraries._

object Source extends PluginDefaults {
  implicit val artifactPrefix = Some("org.openmole.plugin.source")

  lazy val file = OsgiProject("file") dependsOn (Core.workflow, Core.serializer, Misc.exception, Tool.csv)
}

