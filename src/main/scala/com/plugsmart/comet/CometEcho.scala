package com.plugsmart.comet

import _root_.net.liftweb._
import http._
import common._
import actor._
import util._
import Helpers._
import _root_.scala.xml._
import S._
import SHtml._
import js._
import JsCmds._
import JE._
import net.liftweb.http.js.jquery.JqJsCmds.{AppendHtml}

class CometEcho extends CometActor with CometListener {
  private var elines: List[EchoLine] = Nil

  /* need these vals to be set eagerly, within the scope
   * of Comet component constructor
   */
  private val ulId = S.attr("ul_id") openOr "some_ul_id"

  private val liId = S.attr("li_id")

  private lazy val li = liId.
  flatMap{ Helpers.findId(defaultXml, _) } openOr NodeSeq.Empty

  private val inputId = Helpers.nextFuncName

  // handle an update to the chat lists
  // by diffing the lists and then sending a partial update
  // to the browser
  override def lowPriority = {
    case EchoServerUpdate(value) => {
      val update = (value -- elines).reverse.
      map(b => AppendHtml(ulId, line(b)))

      partialUpdate(update)
      elines = value
    }
  }

  // display a line
  private def line(c: EchoLine) = {
    ("name=when" #> hourFormat(c.when) &
     "name=body" #> c.msg)(li)
  }

  // display a list of chats
  private def displayList: NodeSeq = elines.reverse.flatMap(line)

  // render the whole list of chats
  override def render = {
    "name=chat_name" #> "Econame" &
    ("#"+ulId+" *") #> displayList
  }

  // register as a listener
  def registerWith = CometServer

}