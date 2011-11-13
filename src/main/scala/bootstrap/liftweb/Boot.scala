package bootstrap.liftweb

import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.liftweb.http._
import _root_.net.liftweb.http.provider._
import _root_.net.liftweb.sitemap._
import _root_.net.liftweb.sitemap.Loc._
import auth.{AuthRole, userRoles, HttpBasicAuthentication}
import com.plugsmart.rest.EchoRestService


/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {
  def boot {

    // where to search snippet
    LiftRules.addToPackages("com.plugsmart")

    LiftRules.statelessDispatchTable.append(EchoRestService)
    LiftRules.httpAuthProtectedResource.append(EchoRestService.protection)

    val roles = AuthRole("admin")

    // Build SiteMap
    def sitemap() = SiteMap(
      Menu("Home") / "index", // Simple menu form
      // Menu with special Link
      Menu(Loc("Static", Link(List("static"), true, "/static/index"), 
	       "Static Content")))

    LiftRules.authentication = HttpBasicAuthentication("echoserver") {
      case (un, pwd, req) => if (un == "simkey101" && pwd == "ss101") {
        userRoles( List( AuthRole("admin")  ) );
        true
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
  }

  /**
   * Force the request to be UTF-8
   */
  private def makeUtf8(req: HTTPRequest) {
    req.setCharacterEncoding("UTF-8")
  }
}
