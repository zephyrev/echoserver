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

package com.plugsmart.rest


import net.liftweb._
import common._
import http._
import auth.AuthRole
import json.JsonAST.JString
import rest.RestHelper
import util.Helpers
import Helpers._
import com.plugsmart.comet.{EchoServerMsg, CometServer}
import com.plugsmart.comet.{EchoTableServerMsg, CometTableServer, EchoRow}
import java.text.SimpleDateFormat

object userCredential extends RequestVar[Option[String]](None)

object EchoRestService extends RestHelper {

val formatter = new SimpleDateFormat("HH:mm:ss:SSS");


 serve {
   case req @Req("api" :: anyuri :: Nil, _, GetRequest ) => sendGetMessage(anyuri, req)
   case req @Req("api" :: anyuri :: Nil, _, PutRequest ) => sendPutMessage(anyuri, req)
   case req @Req("api" :: anyuri :: Nil, _, PostRequest ) => sendPostMessage(anyuri, req)
   case req @Req("api" :: anyuri :: Nil, _, DeleteRequest ) => sendDeleteMessage(anyuri, req)
 }

  def protection : LiftRules.HttpAuthProtectedResourcePF = {
    case Req(List("api", _*), _, _) => Full(AuthRole("admin"))
    case Req(List("bad", _*), _, _) => Full(AuthRole("admin"))
  }

  private def sendGetMessage(uri: String, req: Req) : LiftResponse = {
    val timestamp = formatter.format(now)

    val contentType = req.contentType.getOrElse("none")

    val out = <span class="rest.get">GET    @ {timestamp} :: {uri}</span>
    CometServer ! EchoServerMsg( out )
    CometTableServer ! EchoTableServerMsg(EchoRow(<td>GET</td>, <td>{timestamp}</td>, <td>{uri}</td>, <td></td>, <td>{contentType}</td>)  )
    JsonResponse(JString(uri))
  }

  private def sendPutMessage(uri: String, req: Req) : LiftResponse = {
    val timestamp = formatter.format(now)

    val content = limitsz ( req.body match {
      case Full(rawbytes)  =>  new String(rawbytes, "UTF-8")
      case _ => ""
    } )
    val contentType = req.contentType.getOrElse("none")

    val out = <span class="rest.put">PUT    @ {timestamp} :: {uri}</span>
    CometServer ! EchoServerMsg( out )
    CometTableServer ! EchoTableServerMsg(EchoRow(<td>PUT</td>, <td>{timestamp}</td>, <td>{uri}</td>, <td>{content}</td>, <td>{contentType}</td>)  )
     AcceptedResponse()
  }

  private def sendPostMessage(uri: String, req: Req) : LiftResponse = {
    val timestamp = formatter.format(now)
    val content = limitsz ( req.body match {
      case Full(rawbytes)  =>  new String(rawbytes, "UTF-8")
      case _ => ""
    } )
    val contentType = req.contentType.getOrElse("none")

    val out = <span class="rest.post">POST  @ {timestamp} :: {uri}</span>
    CometServer ! EchoServerMsg( out )
    CometTableServer ! EchoTableServerMsg(EchoRow(<td>POST</td>, <td>{timestamp}</td>, <td>{uri}</td>, <td>{content}</td>, <td>{contentType}</td>) )
     AcceptedResponse()
  }

  private def sendDeleteMessage(uri: String, req: Req) : LiftResponse = {
    val timestamp = formattedTimeNow

    val content = limitsz ( req.body match {
      case Full(rawbytes)  =>  new String(rawbytes, "UTF-8")
      case _ => ""
    } )
    val contentType = req.contentType.getOrElse("none")

    val out = <span class="rest.del">DELETE @ {timestamp} :: {uri}</span>
    CometServer ! EchoServerMsg( out )
    CometTableServer ! EchoTableServerMsg(EchoRow(<td>DELETE</td>, <td>{timestamp}</td>, <td>{uri}</td>, <td>{content}</td>, <td>{contentType}</td>) )
     AcceptedResponse()
  }

  private def limitsz( str: String ): String = {
    if( str.length > 82) str.substring(0, 82)
    else str
  }


}