/*
 * Copyright © 2011 Juice Technologies, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */

package bootstrap.liftweb

import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.liftweb.http._
import _root_.net.liftweb.http.provider._
import _root_.net.liftweb.sitemap._
import _root_.net.liftweb.sitemap.Loc._
import auth.{AuthRole, userRoles, HttpBasicAuthentication}
import js.jquery.JQuery14Artifacts
import com.plugsmart.rest.{userCredential, EchoRestService}
import com.plugsmart.comet.CometServer
import net.liftweb.actor.LiftActor
import com.plugsmart.snippet.RuntimeStats
import _root_.java.util.Date
import Helpers._


/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {
  def boot {


    // where to search snippet
    LiftRules.addToPackages("com.plugsmart")

    LiftRules.jsArtifacts = JQuery14Artifacts

    LiftRules.statelessDispatchTable.append(EchoRestService)
    LiftRules.httpAuthProtectedResource.append(EchoRestService.protection)

    val roles = AuthRole("admin")

    CometServer

    // Build SiteMap
    def sitemap() = SiteMap(
      Menu("Home") / "index", // Simple menu form
      // Menu with special Link
      Menu(Loc("Static", Link(List("static"), true, "/static/index"), 
	       "Static Content")))

    LiftRules.authentication = HttpBasicAuthentication("echoserver") {
      case (un, pwd, _) => {
        println("auth: " + un + " : " + pwd )
        if (un == "simkey101" && pwd == "ss101") {
          println("authenticated the only user")
          userRoles( List( AuthRole("admin")  ) )
          userCredential(Some(un))
          true
        }
        else false
      }
      case _ => false
    }


    /*
     * Show the spinny image when an Ajax call starts
     */
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)

    /*
     * Make the spinny image go away when it ends
     */
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    LiftRules.early.append(makeUtf8)

    LiftRules.useXhtmlMimeType = false

    // Use HTML5 for rendering
    LiftRules.htmlProperties.default.set((r: Req) =>
      new Html5Properties(r.userAgent))
  }

  /**
   * Force the request to be UTF-8
   */
  private def makeUtf8(req: HTTPRequest) {
    req.setCharacterEncoding("UTF-8")
  }

object SessionInfoDumper extends LiftActor with Loggable {
  private var lastTime = millis

  private def cyclePeriod = 5 seconds

  import com.plugsmart.lib.SessionChecker

  protected def messageHandler =
    {
      case SessionWatcherInfo(sessions) =>
        if ((millis - cyclePeriod) > lastTime) {
          lastTime = millis
          val rt = Runtime.getRuntime
          rt.gc

          RuntimeStats.lastUpdate = timeNow
          RuntimeStats.totalMem = rt.totalMemory
          RuntimeStats.freeMem = rt.freeMemory
          RuntimeStats.sessions = sessions.size

          val percent = (RuntimeStats.freeMem * 100L) / RuntimeStats.totalMem

          // get more aggressive about purging if we're
          // at less than 35% free memory
          if (percent < 35L) {
            SessionChecker.killWhen /= 2L
	    if (SessionChecker.killWhen < 5000L)
	      SessionChecker.killWhen = 5000L
            SessionChecker.killCnt *= 2
          } else {
            SessionChecker.killWhen *= 2L
	    if (SessionChecker.killWhen >
                SessionChecker.defaultKillWhen)
	     SessionChecker.killWhen = SessionChecker.defaultKillWhen
            val newKillCnt = SessionChecker.killCnt / 2
	    if (newKillCnt > 0) SessionChecker.killCnt = newKillCnt
          }

          val dateStr: String = timeNow.toString
          logger.info("[MEMDEBUG] At " + dateStr + " Number of open sessions: " + sessions.size)
          logger.info("[MEMDEBUG] Free Memory: " + pretty(RuntimeStats.freeMem))
          logger.info("[MEMDEBUG] Total Memory: " + pretty(RuntimeStats.totalMem))
          logger.info("[MEMDEBUG] Kill Interval: " + (SessionChecker.killWhen / 1000L))
          logger.info("[MEMDEBUG] Kill Count: " + (SessionChecker.killCnt))
        }
    }


  private def pretty(in: Long): String =
    if (in > 1000L) pretty(in / 1000L) + "," + (in % 1000L)
    else in.toString
}
}
