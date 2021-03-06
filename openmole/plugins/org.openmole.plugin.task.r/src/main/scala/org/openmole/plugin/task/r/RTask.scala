package org.openmole.plugin.task.r

import org.openmole.plugin.task.udocker._
import org.openmole.core.fileservice._
import org.openmole.core.preference._
import org.openmole.core.workspace._
import org.openmole.plugin.task.external._
import org.openmole.core.expansion._
import org.openmole.core.threadprovider.ThreadProvider
import org.openmole.tool.hash._
import org.openmole.core.dsl._

object RTask {

  sealed trait InstallCommand
  object InstallCommands {
    case class RLibrary(name: String) extends InstallCommand

    def toCommand(installCommands: InstallCommand) = {
      installCommands match {
        case RLibrary(name) ⇒
          //Vector(s"""R -e 'install.packages(c(${names.map(lib ⇒ '"' + s"$lib" + '"').mkString(",")}), dependencies = T)'""")
          s"""R -e 'install.packages(c("$name")}), dependencies = T)'"""
      }
    }

    implicit def stringToRLibrary(name: String) = RLibrary(name)

    def installCommands(libraries: Vector[InstallCommand]): Vector[String] = libraries.map(InstallCommands.toCommand)
  }

  def rImage(version: OptionalArgument[String]) = DockerImage("r-base", version.getOrElse("latest"))

  def apply(
    script:  FromContext[String],
    install: Seq[InstallCommand]      = Seq.empty,
    version: OptionalArgument[String] = None,
    cache:   Boolean                  = true
  )(implicit name: sourcecode.Name, newFile: NewFile, workspace: Workspace, preference: Preference, fileService: FileService, threadProvider: ThreadProvider) = {
    val scriptVariable = Val[File]("script", org.openmole.core.context.Namespace("RTask"))

    val scriptContent = FromContext[File] { p ⇒
      val scriptFile = p.newFile.newFile("script", ".R")
      scriptFile.content = script.from(p.context)(p.random, p.newFile, p.fileService)
      p.fileService.deleteWhenGarbageCollected(scriptFile)
      scriptFile
    }

    val installCommands = InstallCommands.installCommands(install.toVector)
    val cacheKey: Option[String] = if (cache) Some((Seq(rImage(version).image, rImage(version).tag) ++ installCommands).mkString("\n").hash().toString) else None

    UDockerTask(
      rImage(version),
      s"R --slave -f script.R",
      installCommands = installCommands,
      cachedKey = OptionalArgument(cacheKey)
    ) set (
        inputFiles += (scriptVariable, "script.R", true),
        scriptVariable := scriptContent,
        reuseContainer := true
      )
  }

}
