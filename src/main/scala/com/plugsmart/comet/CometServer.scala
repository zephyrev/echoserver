package com.plugsmart.comet

import _root_.net.liftweb._
import http._
import common._
import actor._
import util._
import Helpers._
import _root_.scala.xml.{NodeSeq, Text}
import _root_.java.util.Date

object CometServer extends LiftActor with ListenerManager {
  private var elines: List[EchoLine] = List(EchoLine(Text("Rest Echo Server"), now))

  override def lowPriority = {
    case EchoServerMsg( msg ) =>
      elines ::= EchoLine( toHtml(msg), timeNow)
      elines = elines.take(50)
      updateListeners()

    case _ =>
  }

  def createUpdate = EchoServerUpdate(elines.take(15))

  /**
   * Convert an incoming string into XHTML using Textile Markup
   *
   * @param msg the incoming string
   *
   * @return textile markup for the incoming string
   */
  def toHtml(msg: String): NodeSeq = <div>msg</div>

}

case class EchoLine(msg: NodeSeq, when: Date)
case class EchoServerMsg(msg: String)
case class EchoServerUpdate(msgs: List[EchoLine])
