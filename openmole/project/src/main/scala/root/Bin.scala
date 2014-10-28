package root

import root.gui.Bootstrap
import root.base.Misc
import root.libraries.Apache
import sbt._
import Keys._

import org.openmole.buildsystem.OMKeys._
import org.openmole.buildsystem._, Assembly._
import Libraries._
import com.typesafe.sbt.osgi.OsgiKeys._
import sbt.inc.Analysis
import sbtunidoc.Plugin._
import UnidocKeys._
import sbtassembly.Plugin._
import AssemblyKeys._

import scala.util.matching.Regex

object Bin extends Defaults(Base, Gui, Libraries, ThirdParties, Web) {
  val dir = file("bin")

  def filter(m: ModuleID) = {
    m.organization == "org.eclipse.core" ||
      m.organization == "fr.iscpif.gridscale.bundle" ||
      m.organization == "org.bouncycastle" ||
      m.organization.contains("org.openmole")
  }

  lazy val equinox = Seq(
    equinoxApp intransitive (),
    equinoxContenttype intransitive (),
    equinoxJobs intransitive (),
    equinoxRuntime intransitive (),
    equinoxCommon intransitive (),
    equinoxLauncher intransitive (),
    equinoxRegistry intransitive (),
    equinoxPreferences intransitive (),
    equinoxOsgi intransitive ()
  )

  lazy val renameEquinox =
    Map[Regex, String ⇒ String](
      """org\.eclipse\.equinox\.launcher.*\.jar""".r -> { s ⇒ "org.eclipse.equinox.launcher.jar" },
      """org\.eclipse\.(core|equinox|osgi)""".r -> { s ⇒ s.replaceFirst("-", "_") }
    )

  lazy val openmoleui = OsgiProject("org.openmole.ui", singleton = true, buddyPolicy = Some("global")) settings (
    organization := "org.openmole.ui"
  ) settings (
      libraryDependencies ++= Seq(jodaTime, scalaLang, jasypt, Apache.config, Apache.ant, jline, Apache.log4j, scopt, robustIt, equinoxApp)
    ) dependsOn (
        base.Misc.workspace, base.Misc.replication, base.Misc.exception, base.Misc.tools, base.Misc.eventDispatcher,
        base.Misc.pluginManager, base.Core.implementation, base.Core.batch, gui.Server.core, gui.Client.core, gui.Bootstrap.core, base.Misc.sftpserver, base.Misc.logging,
        Web.core, base.Misc.console, base.Core.convenience)

  lazy val openmole = AssemblyProject("openmole") settings (
    //FIXME
    /*resourceSets <+= (baseDirectory, zip in openmoleRuntime, downloadUrls in openmoleRuntime) map { (bd, zipFile, downloadUrls) ⇒
      Set(bd / "resources" -> "", zipFile -> "runtime") ++ downloadUrls.map(_ -> "runtime")
    },*/
    //resourceSets <+= (baseDirectory) map { _ / "db-resources" -> "dbserver/bin" },
    //FIXME
    //resourceSets <++= (copyDependencies in openmolePlugins) map { _ -> "openmole-plugins" },
    //resourcesAssemble <++= Seq(openmoleui.project) sendTo "plugins",
    resourceOutDir := "",
    setExecutable ++= Seq("openmole", "openmole.bat"),
    resourcesAssemble <++= (assemble in openmolePlugins) map { f ⇒ Seq(f -> "plugins") },
    resourcesAssemble <++= (assemble in dbServer) map { f ⇒ Seq(f -> "dbserver") },
    resourcesAssemble <++= (assemble in consolePlugins) map { f ⇒ Seq(f -> "openmole-plugins") },
    resourcesAssemble <++= (assemble in guiPlugins) map { f ⇒ Seq(f -> "openmole-plugins-gui") },
    tarGZName := Some("openmole"),
    innerZipFolder := Some("openmole"),
    dependencyFilter := filter //DependencyFilter.fnToModuleFilter { m ⇒ m.organization == "org.eclipse.core" || m.organization == "fr.iscpif.gridscale.bundle" || m.organization == "org.bouncycastle" || m.organization == "org.openmole" }
  )

  lazy val openmolePlugins = AssemblyProject("openmoleplugins") settings (
    resourcesAssemble <++= subProjects.keyFilter(bundleType, (a: Set[String]) ⇒ a contains "core") sendTo "",
    resourcesAssemble <++= Seq(openmoleui.project) sendTo "",
    libraryDependencies ++= Seq(
      Libraries.bouncyCastle,
      Libraries.gridscale,
      Libraries.logback,
      Libraries.scopt,
      Libraries.guava,
      Libraries.bonecp,
      Libraries.arm,
      Libraries.xstream,
      Libraries.slick,
      Libraries.jline,
      Apache.ant,
      Apache.codec,
      Apache.config,
      Apache.exec,
      Apache.math,
      Apache.pool,
      Apache.log4j,
      Apache.sshd,
      Libraries.groovy,
      Libraries.h2,
      Libraries.jasypt,
      Libraries.jodaTime,
      Libraries.scalajHttp,
      Libraries.scalaLang,
      Libraries.scalatra,
      Libraries.scalaz,
      Libraries.slf4j,
      Libraries.robustIt,
      Libraries.jacksonJson,
      Libraries.jetty,
      Libraries.scalajsLibrary,
      Libraries.scalajsTools,
      Libraries.scalajsDom,
      Libraries.scalaTagsJS,
      Libraries.autowireJS,
      Libraries.upickleJS,
      Libraries.scalaRxJS
    ) ++ equinox,
      dependencyFilter := filter,
      dependencyNameMap := renameEquinox
  )

  lazy val consolePlugins = AssemblyProject("consoleplugins") settings (
    resourcesAssemble <++= subProjects.keyFilter(bundleType, (a: Set[String]) ⇒ a contains "plugin", true) sendTo "",
    libraryDependencies ++=
    Seq(
      Libraries.opencsv,
      Libraries.netlogo4,
      Libraries.netlogo5,
      Libraries.mgo,
      Libraries.scalabc,
      Libraries.monocle,
      Libraries.gridscaleHTTP intransitive (),
      Libraries.gridscalePBS intransitive (),
      Libraries.gridscaleSLURM intransitive (),
      Libraries.gridscaleDirac intransitive (),
      Libraries.gridscaleGlite intransitive (),
      Libraries.gridscaleSGE intransitive (),
      Libraries.gridscaleCondor intransitive (),
      Libraries.gridscalePBS intransitive (),
      Libraries.gridscaleOAR intransitive (),
      Libraries.gridscaleSSH intransitive ()
    ),
      dependencyFilter := filter
  )

  lazy val guiPlugins = AssemblyProject("guiplugins") settings (
    resourcesAssemble <++= subProjects.keyFilter(bundleType, (a: Set[String]) ⇒ a.contains("guiPlugin"), true) sendTo "",
    dependencyFilter := filter
  )

  lazy val dbServer = AssemblyProject("dbserver", "lib") settings (
    resourceOutDir := "bin",
    resourcesAssemble <++= Seq(Misc.replication.project, base.Runtime.dbserver.project) sendTo "lib",
    libraryDependencies ++= Seq(
      Libraries.xstream,
      Libraries.slick,
      Libraries.h2,
      Libraries.slf4j,
      Libraries.scalaLang
    ),
      dependencyFilter := filter
  )

  /*lazy val dbserverProjects = resourceSets <++= subProjects.keyFilter(bundleType, (a: Set[String]) ⇒ a contains "dbserver") sendTo "dbserver/lib"
>>>>>>> Stashed changes

  lazy val runtimeProjects = resourceSets <++= subProjects.keyFilter(bundleType, (a: Set[String]) ⇒ a contains "runtime") sendTo "plugins"

  lazy val java368URL = new URL("http://maven.iscpif.fr/thirdparty/com/oracle/java-jre-linux-386/20-b17/java-jre-linux-386-20-b17.tgz")
  lazy val javax64URL = new URL("http://maven.iscpif.fr/thirdparty/com/oracle/java-jre-linux-x64/20-b17/java-jre-linux-x64-20-b17.tgz")

  lazy val openmoleRuntime = AssemblyProject("runtime", "plugins",
    depNameMap = Map("""org\.eclipse\.equinox\.launcher.*\.jar""".r -> { s ⇒ "org.eclipse.equinox.launcher.jar" },
      """org\.eclipse\.(core|equinox|osgi)""".r -> { s ⇒ s.replaceFirst("-", "_") }), settings = resAssemblyProject ++ zipProject ++ urlDownloadProject ++ runtimeProjects) settings
    (equinoxDependencies, resourceDirectory <<= baseDirectory / "resources",
      urls <++= target { t ⇒ Seq(java368URL -> t / "jvm-386.tar.gz", javax64URL -> t / "jvm-x64.tar.gz") },
      libraryDependencies += Libraries.gridscale intransitive (),
      tarGZName := Some("runtime"),
      setExecutable += "run.sh",
      resourceSets <+= baseDirectory map { _ / "resources" -> "." },
      dependencyFilter := DependencyFilter.fnToModuleFilter { m ⇒ (m.organization == "org.eclipse.core" || m.organization == "fr.iscpif.gridscale.bundle" || m.organization == "org.openmole") })

  lazy val daemonProjects =
    resourceSets <++= subProjects.keyFilter(bundleType, (a: Set[String]) ⇒ (a contains "core") || (a contains "daemon")) sendTo "plugins"

  lazy val openmoleDaemon = AssemblyProject("daemon", "plugins", settings = resAssemblyProject ++ daemonProjects, depNameMap =
    Map("""org\.eclipse\.equinox\.launcher.*\.jar""".r -> { s ⇒ "org.eclipse.equinox.launcher.jar" }, """org\.eclipse\.(core|equinox|osgi)""".r -> { s ⇒ s.replaceFirst("-", "_") })) settings
    (resourceSets <+= baseDirectory map { _ / "resources" -> "." },
      equinoxDependencies,
      libraryDependencies += gridscale,
      libraryDependencies ++= gridscaleSSH,
      libraryDependencies += bouncyCastle,
      setExecutable += "openmole-daemon",
      dependencyFilter := DependencyFilter.fnToModuleFilter { m ⇒ m.extraAttributes get ("project-name") map (_ == projectName) getOrElse (m.organization == "org.eclipse.core" || m.organization == "fr.iscpif.gridscale.bundle" || m.organization == "org.bouncycastle" || m.organization == "org.openmole") })

  lazy val docProj = Project("documentation", dir / "documentation") aggregate ((Base.subProjects ++ Gui.subProjects ++ Web.subProjects): _*) settings (
    unidocSettings: _*
  ) settings (compile := Analysis.Empty, scalacOptions in (ScalaUnidoc, unidoc) += "-Ymacro-no-expand",
      unidocProjectFilter in (ScalaUnidoc, unidoc) := inAnyProject -- inProjects(Libraries.subProjects: _*) -- inProjects(ThirdParties.subProjects: _*)
    )*/
}
