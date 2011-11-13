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
import net.liftweb.http.js.jquery.JqJsCmds.{PrependHtml}

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
      val update = (value -- elines).
      map(b => PrependHtml(ulId, line(b)))
      elines = value
      partialUpdate(update)
    }
  }

  // display a line
  private def line(c: EchoLine) = {
    ("name=restline" #> c.msg)(li)
  }

  // display a list of chats
  private def displayList: NodeSeq = elines.flatMap(line)

  // render the whole list of chats
  override def render = {
    println("render called with " + displayList)
    "name=chat_name" #> "Econame" &
    ("#"+ulId+" *") #> displayList
  }

  // register as a listener
  def registerWith = CometServer

}