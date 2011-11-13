package com.plugsmart.snippet

import _root_.net.liftweb._
import http._
import common._
import util._
import Helpers._

import _root_.java.text.NumberFormat
import _root_.scala.xml.{NodeSeq, Text}

object RuntimeStats extends DispatchSnippet {
  @volatile
  var totalMem: Long = Runtime.getRuntime.totalMemory
  @volatile
  var freeMem: Long = Runtime.getRuntime.freeMemory

  @volatile
  var sessions = 1

  @volatile
  var lastUpdate = timeNow

  val startedAt = timeNow

  private def nf(in: Long): String = NumberFormat.getInstance.format(in)

  def dispatch = {
    case "total_mem" => i => Text(nf(totalMem))
    case "free_mem" => i => Text(nf(freeMem))
    case "sessions" => i => Text(sessions.toString)
    case "updated_at" => i => Text(lastUpdate.toString)
    case "started_at" => i => Text(startedAt.toString)
  }

}