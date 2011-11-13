package com.plugsmart.rest


import net.liftweb._
import common._
import http._
import auth.AuthRole
import json.JsonAST.JString
import rest.RestHelper
import com.plugsmart.comet.{EchoServerMsg, CometServer}

object EchoRestService extends RestHelper {

 serve {
   case Req("api" :: anyuri :: Nil, _, GetRequest ) => {
     CometServer ! EchoServerMsg( anyuri );JsonResponse(JString(anyuri))}
   case req@Req("api" :: anyuri :: Nil, _, PutRequest ) => {
     CometServer ! EchoServerMsg( anyuri );req.body; req.headers(1); req.contentType; Full(AcceptedResponse)}
   case req@Req("api" :: anyuri :: Nil, _, PostRequest ) => {
     CometServer ! EchoServerMsg( anyuri );Full(AcceptedResponse)}
   case req@Req("api" :: anyuri :: Nil, _, DeleteRequest ) => {
     CometServer ! EchoServerMsg( anyuri );Full(AcceptedResponse)}

   case Req("bad" :: anyuri :: Nil, _, GetRequest ) => {
     CometServer ! EchoServerMsg( anyuri ); Full(BadResponse)}
   case req@Req("bad" :: anyuri :: Nil, _, PutRequest ) => {
     CometServer ! EchoServerMsg( anyuri );req.body; req.headers(1); req.contentType; Full(BadResponse)}
   case req@Req("bad" :: anyuri :: Nil, _, PostRequest ) => {
     CometServer ! EchoServerMsg( anyuri );Full(BadResponse)}
   case req@Req("bad" :: anyuri :: Nil, _, DeleteRequest ) => {
     CometServer ! EchoServerMsg( anyuri );Full(BadResponse)}
 }

  def protection : LiftRules.HttpAuthProtectedResourcePF = {
    case Req(List("api", _*), _, _) => Full(AuthRole("Admin"))
  }


}