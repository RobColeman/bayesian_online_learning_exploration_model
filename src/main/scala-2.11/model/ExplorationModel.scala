package model

import config.Configuration

/**
  * Trait describing the behavior of an exploration model
  */
trait ExplorationModel extends Configuration {
  def isValidExplorationCampaign(campaignId: String): Boolean
  def score(campaignId: String): Scores
  val name: String
  val timeStamp: Int
}
