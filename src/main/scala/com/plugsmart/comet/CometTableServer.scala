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
import actor._
import util._
import _root_.scala.xml.{NodeSeq, Text}

object CometTableServer extends LiftActor with ListenerManager {
  private var erows: List[EchoRow] = List( EchoRow( <td>PUT</td>, <td>00:00:99</td>, <td>example.com</td>, <td>"payload:init"</td>, <td>"Content-Type"</td> ) )

  override def lowPriority = {
    case EchoTableServerMsg( arow ) =>  {
      println("EchoServer received msg # " + erows.size )
      erows =  arow :: erows
      erows = erows.take(50)
      updateListeners()
    }
    case _ => {}
  }

  def createUpdate = EchoTableServerUpdate(erows.take(15))

  def initdata = 
    <tr id="main_tr_id">
					<td name="verb">PUT</td>
					<td name="timest">00:00:10</td>
					<td name="url">echo.example.com</td>
          <td name="contentType">application/json</td>
					<td name="content">"raw:input"</td>
	</tr>
}

case class EchoRow(verb: NodeSeq, timest: NodeSeq, url: NodeSeq, payload: NodeSeq, contentType: NodeSeq)
case class EchoTableServerMsg(msg: EchoRow)
case class EchoTableServerUpdate(msgs: List[EchoRow])

