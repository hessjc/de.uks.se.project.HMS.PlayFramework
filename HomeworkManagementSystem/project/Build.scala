import sbt._
import Keys._
import play.Project._
import com.typesafe.config._
import com.typesafe.sbteclipse.core.EclipsePlugin.EclipseKeys
import de.johoop.jacoco4sbt._
import JacocoPlugin._

object ApplicationBuild extends Build {

  val conf = ConfigFactory.parseFile(new File("conf/application.conf")).resolve()
  val appName = conf.getString("application.name")
  val appVersion = conf.getString("application.version")
  
  lazy val s = Seq(jacoco.settings:_*)

  val appDependencies = Seq(
    javaCore,
    cache,
    javaJdbc,
    javaEbean,
    filters,

    "be.objectify" %% "deadbolt-java" % "2.2-RC2",
    "mysql" % "mysql-connector-java" % "5.1.25",
    "org.apache.directory.api" % "api-all" % "1.0.0-M20",
    "de.uks.se" %% "play-simple-authentication" % "0.2.0-SNAPSHOT",
    "org.mockito" % "mockito-all" % "1.9.5" % "test",
    "de.uks.se" %% "funcy" % "0.4.1" % "test"
    )

  val main = play.Project(appName, appVersion, appDependencies, settings = s).settings(
    resolvers += Resolver.url("Objectify Play Repository", url("http://schaloner.github.com/releases/"))(Resolver.ivyStylePatterns),
    resolvers += Resolver.url("Objectify Play Repository - snapshots", url("http://schaloner.github.com/snapshots/"))(Resolver.ivyStylePatterns),
    resolvers += Resolver.url("typesafe", url("http://repo.typesafe.com/typesafe/repo")),
    resolvers += Resolver.url("play-authenticate (release)", url("http://joscha.github.com/play-authenticate/repo/releases/"))(Resolver.ivyStylePatterns),
    resolvers += Resolver.url("play-authenticate (snapshot)", url("http://joscha.github.com/play-authenticate/repo/snapshots/"))(Resolver.ivyStylePatterns),
    resolvers += Resolver.url("play-easymail (release)", url("http://joscha.github.com/play-easymail/repo/releases/"))(Resolver.ivyStylePatterns),
    resolvers += Resolver.url("play-easymail (snapshot)", url("http://joscha.github.com/play-easymail/repo/snapshots/"))(Resolver.ivyStylePatterns),
    resolvers += Resolver.url("University of Kassel", url("http://se.cs.uni-kassel.de/repository/"))(Resolver.ivyStylePatterns),
    javacOptions in Compile ++= Seq("-source", "1.6", "-target", "1.6"),
    parallelExecution in jacoco.Config := false
//    ,javaOptions in (Test) += "-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=9996"
      )
}
