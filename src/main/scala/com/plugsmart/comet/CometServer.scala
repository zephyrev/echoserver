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

object CometServer extends LiftActor with ListenerManager {
  private var elines: List[EchoLine] = List( EchoLine(<span><b>Rest Echo Server</b></span>))

  override def lowPriority = {
    case EchoServerMsg( msg ) =>  {
      println("EchoServer received msg # " + elines.size )
      elines =  EchoLine( msg ) :: elines
      elines = elines.take(20)
      updateListeners()
    }
    case _ => {}
  }

  def createUpdate = EchoServerUpdate(elines.take(15))

}

case class EchoLine(msg: NodeSeq)
case class EchoServerMsg(msg: NodeSeq)
case class EchoServerUpdate(msgs: List[EchoLine])
