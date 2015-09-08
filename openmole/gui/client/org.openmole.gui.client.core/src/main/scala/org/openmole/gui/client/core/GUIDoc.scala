package org.openmole.gui.client.core

import org.openmole.gui.client.core.EnvironmentErrorPanel.SelectableLevel
import org.openmole.gui.client.core.files.TreeNodePanel
import org.openmole.gui.ext.data.{ DebugLevel, ErrorLevel }
import org.scalajs.dom.raw.{ HTMLInputElement, HTMLDivElement }

import scalatags.JsDom.TypedTag
import scalatags.JsDom.{ tags ⇒ tags }
import org.openmole.gui.misc.js.JsRxTags._
import org.openmole.gui.misc.js.{ BootstrapTags ⇒ bs, Select, ClassKeyAggregator }
import scalatags.JsDom.all._
import bs._
import rx._

/*
 * Copyright (C) 27/08/15 // mathieu.leclaire@openmole.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

object GUIDoc {

  case class GUIDocEntry(glyph: ClassKeyAggregator, title: String, content: TypedTag[HTMLDivElement])

  val selectedEntry: Var[Option[GUIDocEntry]] = Var(None)

  val omLangageLink = a(href := "http://www.openmole.org/current/Documentation_Language.html", target := "_blank")("OpenMOLE language")
  val omPluginLink = a(href := "http://www.openmole.org/current/Documentation_Development_Plugins.html", target := "_blank")("OpenMOLE plugin")
  val omMarketLink = a(href := "http://www.openmole.org/current/Documentation_Market%20Place.html", target := "_blank")("Market Place")
  val omEnvironmentLink = a(href := "http://www.openmole.org/current/Documentation_Language_Environments.html", target := "_blank")("Environment page")
  val githubMarketLink = a(href := "https://github.com/openmole/openmole-market/", target := "_blank")("Github page")
  val toStringHookLink = a(href := "http://www.openmole.org/current/Documentation_Language_Hooks.html", target := "_blank")("ToString Hook")

  val rLink = a(href := "https://www.r-project.org/", target := "_blank")("R")
  val netlogoLink = a(href := "https://ccl.northwestern.edu/netlogo/", target := "_blank")("Netlogo")
  val pythonLink = a(href := "https://www.python.org/", target := "_blank")("Python")
  val javaLink = a(href := "http://openjdk.java.net/", target := "_blank")("Java")
  val scalaLink = a(href := "http://www.scala-lang.org/", target := "_blank")("Scala")
  val csvLink = a(href := "https://en.wikipedia.org/wiki/Comma-separated_values", target := "_blank")("CSV")

  val resourcesContent = tags.div(
    "The resources are files, that can be used in OpenMOLE simulations:",
    ul(
      li(b(".oms for Open Mole Script"), "is a file describing an OpenMOLE workflow according the its ", omLangageLink),
      li(b("external code"), " run used in OpenMOLE scripts: editable in the application (", javaLink, ", ", scalaLink, ", ", netlogoLink, ", ", rLink, ", ", pythonLink, ", ...) or not (C, C++, or any binary code)"),
      li(b("Any external resources"), " used as input for a model editable in the application (", csvLink, " files, text files, ...) or not (pictures, or any binary files)")
    ),
    "These files are managed in a file system located in left hand side and that can be showed or hidden thanks to the ", glyph(bs.glyph_file + " glyphText"), " icon",
    bs.div("centerImg")(img(src := "img/fileManagement.png", alt := "fileManagement")),
    "The ", bs.span("greenBold")("current directory"), " path is stacked in the top. The current directory is on the right of the stack. Clicking on one folder of the stack set this folder as current. On the image above, the current directory is for example ", tags.em("SimpopLocal"),
    ".",
    bs.div("spacer20")("The content of the current directory is listed in the bottom. Each row gives the name and the size of each file or folder. A folder is preceded by a ", tags.div(`class` := "dir bottom-5"), ". A ", glyph(bs.glyph_plus), " inside means that the folder is not empty.",
      "In between, a file managment tool enable to: ",
      ul(
        li("create a new file or folder in the current directory. To do so, select ", tags.em("file"), " or ", tags.em("folder"), " thanks to the ", bs.span("greenBold")("file or folder selector"), ". Then, type the required name in the ",
          bs.span("greenBold")("file or folder name input"), " and press enter. The freshly created file or folder appears in the list."),
        li(bs.span("greenBold")("upload a file"), " in the current directory"),
        li(bs.span("greenBold")("Refresh"), " the content of the current directory.")
      )),
    bs.div("spacer20")("When a file or a folder row is hovered, new options appear:",
      ul(
        li(glyph(bs.glyph_download + " right2"), " for downloading the current file or directory (as an archive for the latter)."),
        li(glyph(bs.glyph_edit + " right2"), " for renaming the current file or directory. An input field appears; just input the new name and press " + tags.em("enter") + " to validate."),
        li(glyph(bs.glyph_trash + " right2"), " for deleting the current file or direcotry."),
        li(glyph(bs.glyph_archive + " right2"), " for uncompressing the current file (appears only in case of archive files (", tags.em(".tgz"), " or ", tags.em("tar.gz"), ").")
      )),
    bs.div("spacer20")("The editable files can be modified in the central editor. To do so, simply click on the file to be edited.")
  )

  val executionContent = {
    val logLevels = Seq(ErrorLevel(), DebugLevel())

    tags.div(
      "An .oms script file can be run and monitor thanks to the execution section ", glyph(bs.glyph_settings + " glyphText"),
      bs.div("spacer20")(tags.b("Monitor an execution:"),
        tags.div("When a .oms file is edited, a ", bs.button("Play", btn_primary + " labelInline"), " in top right hand corner permits to start a workflow execution." +
          " Once the workflow has been started, the execution panel appears giving the following information for each execution and from left to right:",
          tags.ul(
            tags.li("The script name (Ex: explore.oms)"),
            tags.li("The start time of the execution (Ex: 1/9/2015, 15:07:20 )"),
            tags.li(glyph(bs.glyph_flash + " right2"), " the number of running jobs (Ex: ", glyph(bs.glyph_fire + " right2"), " 227)"),
            tags.li(glyph(bs.glyph_flag + " right2"), " the jobs progression with (#finished jobs / # jobs) (Ex: ", glyph(bs.glyph_flag + " right2"), " 17/2345)"),
            tags.li("The execution duration (Ex: 1:17:44)"),
            tags.li("The execution state with:",
              tags.ul(
                tags.li(tags.span(style := "color: yellow; font-weight: bold;", "running"), " : the jobs are running"),
                tags.li(tags.span(style := "color: #a6bf26; font-weight: bold;", "success"), " : the execution has successfully finished",
                  tags.li(tags.span(style := "color: #CC3A36; font-weight: bold;", "failed"), " : the execution has failed: click on this state to see the errors"),
                  tags.li(tags.span(style := "color: orange; font-weight: bold;", "canceled"), ": the execution has been canceled (by means of the ", glyph(bs.glyph_remove + " right2"), ")")
                ))
            ),
            tags.li(glyph(bs.glyph_stats), "Env give informations about the execution monitoring on the environments defined in the workflow (See below)"),
            tags.li(glyph(bs.glyph_list + " right2"), " display the standard output stream. You will see here the results of your ", toStringHookLink, " if you defined one in your script"),
            tags.li(glyph(bs.glyph_remove + " right2"), " cancel the execution"),
            tags.li(glyph(bs.glyph_trash + " right2"), " remove the execution from the list.")
          )
        ),
        bs.div("spacer20")(
          "The output history ", bs.input("500", "right2 labelInline")(style := "color:#333; width : 60px;"), " permits to set the number of entries in the standard outputs of the executions ( ",
          glyph(bs.glyph_list + " right2"), " ). It is set by default to 500."
        )
      ),
      bs.div("spacer20")(tags.b("Monitor the environments of an execution:"),
        tags.div("When clicking on ", glyph(bs.glyph_stats), "Env, and at least one environment has been defined in the running script, a new line about environment statuses appear with the following informations:",
          tags.ul(
            tags.li("The name of the environment. If it has not been named explicitely in the script, it will appear like: LocalEnvironment@1371838186 or GridEnvironment@5718318899"),
            tags.li(glyph(bs.glyph_upload + " right2"), "The number of files and the amount of uploaded data on the remote environment (Ex: 27(14Mo))"),
            tags.li(glyph(bs.glyph_download + " right2"), "The number of files and the amount of downloaded data from the remote environment (Ex: 144(221Ko))"),
            tags.li(glyph(bs.glyph_road + " right2"), "The number of submitted jobs on the remote environment (Ex: ", glyph(bs.glyph_road + " right2"), " 1225)"),
            tags.li(glyph(bs.glyph_flash + " right2"), " the number of running jobs on the remote environment (Ex: ", glyph(bs.glyph_fire + " right2"), " 447)"),
            tags.li(glyph(bs.glyph_flag + " right2"), " the number of finished jobs on the remote environment (Ex: ", glyph(bs.glyph_flag + " right2"), " 127)"),
            tags.li(glyph(bs.glyph_fire + " right2"), " the number of failed jobs on the remote environment (Ex: ", glyph(bs.glyph_fire + " right2"), " 4)"),
            tags.li(tags.a("details"), " is a link to monitor the environment logs. It is useful to diagnose a problem on the environment.")
          )
        ),
        bs.div("spacer20")(
          Select[SelectableLevel]("errorLevelDoc", logLevels.map { level ⇒
            (SelectableLevel(level, level.name), emptyCK)
          }, Some(logLevels.head), btn_primary
          ).selector, " switches from fine logging (DEBUG) to minimal logging (ERROR) for all the environments."
        )
      )
    )
  }

  val authenticationContent = {
    val factories = ClientService.authenticationFactories

    tags.div(
      "In OpenMOLE, the computation load can be delegated to remote environments (SSH machine, Cluster, Grid) as explained on the ",
      omEnvironmentLink, ". It is previously necessary to save the connection settings on these different environments (like login/password or ssh key). When clicking on the ",
      glyph(bs.glyph_lock + " glyphText"), " a panel appears with the list of all the defined authentications (initially, no one is defined).",
      bs.div("spacer20")(
        "The supported authentications are:",
        tags.ul(
          tags.li("SSH authentication with login and password (any environment accessed by means of SSH)"),
          tags.li("SSH authentiaction with SSH private key (any environment accessed by means of SSH)"),
          tags.li("Grid certificate (.p12) for Grid computing")
        )
      ),
      bs.div("spacer20")(
        "To add one authentication, click on the ", glyph(bs.glyph_plus + " right2"),
        " icon. A new panel is displayed. First select the authentication category you need: ",
        Select("authentications",
          factories.map { f ⇒ (f, emptyCK) },
          factories.headOption,
          btn_primary
        ).selector
      ), "Depending on your selection, the settings change. Let see them in details:",
      bs.div("spacer20")(tags.b("SSH Password:"),
        tags.div("Set your host and your login on it (for example john on blueberry.org), as well as your password. Once you saved it, the authentication will be added to your list (by example: john@blueberry.org)")
      ),
      bs.div("spacer20")(tags.b("SSH Key:"),
        tags.div("The same three settings as the ones for the SSH Password are required as well as your private SSH key. To add it, click on ",
          tags.label(`class` := "inputFileStyle labelInline")("No certificate"),
          " and navigate to your private SSH key. A random name will be associated to your key. ",
          "Once you saved it, the authentication will be added to your list (by example: john@blueberry.org)")
      ),
      bs.div("spacer20")(tags.b("EGI P12 Certificate:"),
        tags.div("It only requires your EGI certificate file and the associated password. To add it, click on ",
          tags.label(`class` := "inputFileStyle labelInline")("No certificate"),
          " and navigate to your certificate file. It will be renamed by egi.p12. Note that only one EGI certificate is required (you will not need any other one !)"
        ),
        bs.div("spacer20")("An authentication can be remove by clicking on the ", glyph(bs.glyph_trash + " right2"), " (visible when hovering an row of the authentication list), or edited by clicking on the name of an authentication from the authentications list."
        )
      )
    )
  }

  val marketContent = tags.div(
    "It gathers working examples of OpenMOLE workflow. They are excellent starting points for building your own project. All the examples in the market place provide:",
    tags.ul(
      tags.li("At least a .oms file containing an executable workflow script,"),
      tags.li("The model executable (except for a small Scala code, which can be contained in an Scala Task),"),
      tags.li("A README.md giving informations about the model or the method used in the example.")
    ),
    bs.div("spacer20")(
      "All the examples of the market can be found in the ", omMarketLink, " of the website and in the application by clicking the ", glyph(bs.glyph_market + " glyphText"),
      " icon. The list of all the entries of the Market will appear. You can read the README.md by clicking on the name of the example and then downloading it in the current directory by pressing the ",
      tags.em("Download"), " button. Once the download is over, the dialog is closed and the file manager refreshed. You just need to open its .oms file and press play to start its execution.",
      bs.div("spacer20")("The sources and proposal for new entries can be found on a ", githubMarketLink, ".")
    )

  )

  val pluginContent = tags.div(
    "The OpenMOLE platform is pluggable, meaning that you can build your own extension for any concept. It is however an advanced way of using the platform, so that you probably do not need it.",
    bs.div("spacer20")("All the documentation about plugins can be found on the ",
      omPluginLink, " section on the website. Nethertheless, the ", glyph(bs.glyph_plug + " glyphText"), " section enable to provide your plugins as ", tags.em(" jar"), " file, so that they can be found at execution time if it is used in an OpenMOLE script."
    ), bs.div("spacer20")("To do so, simply click on the ", bs.glyph(bs.glyph_upload + " right2"), " in the plugin panel and navigate to your jar file. " +
      "Once uploaded, the file appears in the list above. Hovering a row in this list makes appear the ", glyph(bs.glyph_trash + " right2"), " icon to remove this plugin from your selection."
    )
  )

  val entries = Seq(
    GUIDocEntry(bs.glyph_file, "Manage the resources", resourcesContent),
    GUIDocEntry(bs.glyph_settings, "Execute scripts", executionContent),
    GUIDocEntry(bs.glyph_lock, "Manage authentications", authenticationContent),
    GUIDocEntry(bs.glyph_market, "The Market place", marketContent),
    GUIDocEntry(bs.glyph_plug, "Plugins", pluginContent)
  )

  val doc: TypedTag[HTMLDivElement] = {
    tags.div(`class` := "docText",
      "This help provides informations to use the OpenMOLE web application.  It looks very much to a web application even if, for now, both server and client run locally.",
      tags.div(
        "This application enables to manage OpenMOLE scripts, to edit them and to run them." +
          " It does not explain how to build a workflow by means of the ",
        omLangageLink,
        ", which is explained in detail on the OpenMOLE website."),
      for (entry ← entries) yield {
        Rx {
          val isSelected = selectedEntry() == Some(entry)
          tags.div(
            `class` := "docEntry" + {
              if (isSelected) " docEntrySelected" else ""
            },
            tags.span(
              `class` := "docTitleEntry",
              onclick := { () ⇒
                {
                  selectedEntry() = {
                    if (isSelected) None
                    else Some(entry)
                  }
                }
              },
              glyph(entry.glyph + "glyphText"),
              entry.title
            ),
            if (isSelected) tags.div(`class` := "docContent", entry.content)
            else tags.div

          )
        }
      }
    )

  }

}