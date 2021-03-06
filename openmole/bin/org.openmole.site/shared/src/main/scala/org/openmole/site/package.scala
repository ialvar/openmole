package org.openmole.site

/*
 * Copyright (C) 11/05/17 // mathieu.leclaire@openmole.org
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

import scalatags.Text.all._

package object shared {
  lazy val searchDiv = "search-div"
  lazy val searchImg = "search-img"
  lazy val blogposts = "blog-posts"
  lazy val newsPosts = "news-posts"
  lazy val shortTraining = "short-training"
  lazy val longTraining = "long-training"

  object profile {
    val button = "profileTrigger"
    val animation = "startProfileAnim"
  }

  object pse {
    val button = "pseTrigger"
    val animation = "startPseAnim"
  }

  object sensitivity {
    val button = "sensitivityTrigger"
    val animation = "startSensitivityAnim"
  }

  object guiGuide {
    lazy val overview = "Overview"
    lazy val startProject = "Starting a project"
    lazy val fileManagment = "File Management"
    lazy val authentication = "Authentications"
    lazy val playAndMonitor = "Play and monitor executions"
    lazy val plugin = "Plugins"
  }

  object clusterMenu {
    lazy val pbsTorque = "PBS and Torque"
    lazy val sge = "SGE"
    lazy val oar = "OAR"
    lazy val slurm = "Slurm"
    lazy val condor = "Condor"
  }

  object nativeModel {
    lazy val rExample = "An example with R"
    lazy val pythonExample = "Another example with Python"
    lazy val advancedOptions = "Advanced options"
  }
  object nativePackagingMenu {
    lazy val introCARE = "Packaging with CARE"
    lazy val advancedOptions = "Advanced Options"
    lazy val localResources = "Using local Resources"
    lazy val localExecutable = "Using local executable"
    lazy val troubleshooting = "CARE Troubleshooting"
    lazy val nativeAPI = "Native API"
  }

  object netlogoMenu {
    lazy val simulation = "The simulation"
    lazy val doe = "The Design of Experiment"
    lazy val task = "The NetLogo task"
    lazy val storing = "Storing the results"
    lazy val together = "Bringing all the pieces together"
    lazy val further = "Going further"
  }

  object otherDoEMenu {

    lazy val LHSSobol = "Latin Hypercube, Sobol Sequence"
    lazy val severalInputs = "Exploration of several inputs "
    lazy val sensitivityAnalysis = "Sensitivity Analysis"
    lazy val sensitivityFireModel = "Real world Example"
    lazy val csvSampling = "CSV Sampling"
  }

  object directSamplingMenu {
    lazy val gridSampling = "Grid Sampling"
    lazy val uniformSampling = "Uniform Distribution Sampling"
  }

  object dataProcessingMenu {
    lazy val setOfFiles = "Exploring a set of files"
    lazy val pathsVsFiles = "Files vs Paths"
    lazy val example = "More examples"
    lazy val further = "Going further"
  }

  object advancedSamplingMenu {
    lazy val sampling = "Sampling"
    lazy val combineSampling = "Combine samplings"
    lazy val zipSampling = "Zip samplings"
    lazy val filterSampling = "Take, filter, sample samplings"
    lazy val randomSampling = "Random samplings"
    lazy val higherLevelSampling = "Higher level samplings"
    lazy val isKeyword = "The is keyword"
  }

  object hookMenu {
    lazy val plugHook = "Plug a Hook"
    lazy val appendToFileHook = "Append to file"
    lazy val copyFileHook = "Copy file"
    lazy val toStringHook = "Display variables"
    lazy val displayHook = "Display results in the stdout"
    lazy val csvHook = "CSV Hook"
  }

  object sourceMenu {
    lazy val plugSource = "Plug a source"
    lazy val listFiles = "List files in a directory"
    lazy val listDirectories = "List directories in a directory"
    lazy val example = "A complete example"
  }

  object capsuleMenu {
    lazy val definition = "Definition"
    lazy val strainer = "Strainer capsule"
    lazy val master = "Master capsule"
  }

  object consoleMenu {
    lazy val authentication = "Authentications"
    lazy val run = "Run scripts"
  }

  object howToContributeMenu {
    lazy val prerequisites = "Prerequisites"
    lazy val firstTimeSetup = "First time setup"
    lazy val buildAppFromSources = "Build from Sources"
    lazy val standaloneArchive = "Standalone archive"
    lazy val compileDocker = "Compile within Docker"
    lazy val buildWebsite = "Build the website"
    lazy val repositories = "Repositories"
    lazy val webpagesSources = "Edit Documentation pages"
    lazy val devVersion = "Dev versions "
    lazy val projectOrganization = "Project organization"
    lazy val bugReport = "Bug Report"
    lazy val contributionProcedure = "Contribution procedure"
    lazy val branchingModel = "Branching model"
  }

  object gaWithNetlogo {
    lazy val antModel = "The ant model"
    lazy val defineProblem = "An optimisation problem"
    lazy val runOpeMOLE = "Run it in OpenMOLE"
    lazy val optimizationAlgo = "The optimisation algorithm"
    lazy val scaleUp = "Scale up"
  }

  object link {
    lazy val demo = "http://demo.openmole.org"
    lazy val twitter = "https://twitter.com/OpenMOLE"
    lazy val blog = "https://blog.openmole.org"
    lazy val chat = "https://chat.iscpif.fr/channel/openmole"
    lazy val simpluDemo = "https://simplu.openmole.org"
    lazy val mailingList = "http://ask.openmole.org"
    lazy val shortTraining = "https://iscpif.fr/events/formationsjedi/"
    lazy val longTraining = "http://cnrsformation.cnrs.fr"
    lazy val egi = "http://www.egi.eu/"
    lazy val batchProcessing = "https://en.wikipedia.org/wiki/Batch_processing"
    lazy val batchSystem = "http://en.wikipedia.org/wiki/Portable_Batch_System"
    lazy val grieEngine = "https://en.wikipedia.org/wiki/Oracle_Grid_Engine"
    lazy val slurm = "https://en.wikipedia.org/wiki/Simple_Linux_Utility_for_Resource_Management"
    lazy val condor = "https://en.wikipedia.org/wiki/HTCondor"
    lazy val oar = "http://oar.imag.fr/dokuwiki/doku.php"
    lazy val ssh = "https://en.wikipedia.org/wiki/Secure_Shell"
    lazy val geodivercity = "http://geodivercity.parisgeo.cnrs.fr/blog/"
    lazy val ercSpringer = "http://www.springer.com/fr/book/9783319464954"
    lazy val git = "https://git-scm.com/"
    lazy val gitlfs = "https://git-lfs.github.com/"
    lazy val sbt = "http://www.scala-sbt.org/"
    lazy val scala = "http://www.scala-lang.org/"
    lazy val scalaBook = "http://www.scala-lang.org/node/959"
    lazy val scalaDoc = "http://www.scala-lang.org/api/current/index.html"
    lazy val intelliJ = "https://www.jetbrains.com/idea/"
    lazy val scalatex = "http://www.lihaoyi.com/Scalatex/"
    lazy val netlogoAnts = "http://ccl.northwestern.edu/netlogo/models/Ants"
    lazy val branchingModel = "http://nvie.com/posts/a-successful-git-branching-model/"
    lazy val issue = "https://github.com/openmole/openmole/issues"
    lazy val pullRequests = "https://github.com/openmole/openmole/pulls"
    lazy val next = "https://next.openmole.org/"
    lazy val CAREsite = "https://proot-me.github.io/"
    lazy val CAREmailing = "https://groups.google.com/forum/?fromgroups#!forum/reproducible"
    lazy val ggplot2 = "http://ggplot2.tidyverse.org/reference/"
    lazy val urbanDynamics = "https://hal.archives-ouvertes.fr/view/index/docid/1583528"
    lazy val urbanDynamicsBib = "https://hal.archives-ouvertes.fr/hal-01583528v1/bibtex"
    lazy val sobol = "https://en.wikipedia.org/wiki/Sobol_sequence"
    lazy val lhs = "https://en.wikipedia.org/wiki/Latin_hypercube_sampling"

    object partner {
      lazy val iscpif = "http://iscpif.fr"
      lazy val parisgeo = "http://www.parisgeo.cnrs.fr/"
      lazy val biomedia = "https://biomedia.doc.ic.ac.uk/"
      lazy val idf = "https://www.iledefrance.fr/"
      lazy val paris = "https://www.paris.fr/"
      lazy val ign = "http://www.ign.fr/"
    }

    object repo {
      lazy val openmole = "https://github.com/openmole/openmole"
      lazy val market = "https://github.com/openmole/openmole-market"
      lazy val gridscale = "https://github.com/openmole/gridscale"
      lazy val scaladget = "https://github.com/openmole/scaladaget"
      lazy val scalawui = "https://github.com/openmole/scalaWUI"
      lazy val mgo = "https://github.com/openmole/mgo"
      lazy val simplu = "https://github.com/IGNF/simplu3D"
      lazy val myOpenmolePlugin = "https://github.com/openmole/myopenmoleplugin"
      lazy val gamaPlugin = "https://github.com/openmole/gama-plugin"
    }

  }

  def rawFrag(content: String) = {
    val builder = new scalatags.text.Builder()
    raw(content).applyTo(builder)
    div(textAlign := "center")(builder.children.head)
  }
}
