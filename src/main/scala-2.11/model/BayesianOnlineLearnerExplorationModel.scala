package model

import org.apache.commons.math3.distribution.BetaDistribution
import util.Timestamp
import config.Configuration

/**
  * Companion object to BayesianOnlineLearnerExplorationModel
  */
object BayesianOnlineLearnerExplorationModel extends Configuration {

  val Name: String = "BOLExplorer"

  val ImpressionThreshold: Long = conf.getLong("impressionThreshold")

  def apply(
    campaignTargetImpressionCounts: Array[CampaignImpressionCounts]
  ): BayesianOnlineLearnerExplorationModel = {

    val priors: BOLEPriors = BOLEPriorsFactory.globalPriors()

    val models: Array[CampaignBetaBinomialModel] = campaignTargetImpressionCounts.map(CampaignBetaBinomialModel(_, priors))

    // We should really be using the data dump timestamp
    new BayesianOnlineLearnerExplorationModel(models, ImpressionThreshold)
  }

}

/**
  * BayesianOnlineLearnerExplorationModel, scores campaigns by sampling from the posterior of a beta-binomial distribution.
  * @param models The individual campaign models
  * @param ImpressionThreshold Campaigns with more than this number of impressions will graduate out of the exploration model.
  * @param timeStamp The timestamp of the model's last update.  If an external process updates the model,
  *                  this timestamp should correspond to the updating data timestamp.
  */
class BayesianOnlineLearnerExplorationModel(
                                             models:              Array[CampaignBetaBinomialModel],
                                             ImpressionThreshold: Long,
                                             private var invalidCampaigns:    Set[String] = Set.empty[String],
                                             val timeStamp:       Int = Timestamp.getCurrentSeconds
) extends ExplorationModel {

  val Name: String = BayesianOnlineLearnerExplorationModel.Name

  override def toString: String = s"$Name loaded at $timeStamp"

  val modelsMap: Map[String, CampaignBetaBinomialModel] = {
    val (belowThresh, aboveThresh): (Array[CampaignBetaBinomialModel], Array[CampaignBetaBinomialModel]) =
      models.partition { ctModel => ctModel.getTotalImpressions < ImpressionThreshold }

    val aboveThresholdCampaigns: Set[String] = aboveThresh.map { _.key }.toSet

    invalidCampaigns = aboveThresholdCampaigns ++ invalidCampaigns

    belowThresh.filter{ ctModel =>
      !invalidCampaigns.contains(ctModel.key) }
      .map{ ctModel => ctModel.key -> ctModel }
      .toMap
  }

  val priors: BOLEPriors = models.head.priors
  val ctrPrior = new BetaDistribution(priors.ctrPrior.alpha, priors.ctrPrior.beta)
  val irPrior = new BetaDistribution(priors.irPrior.alpha, priors.irPrior.beta)

  def getInvalidCampaigns: Set[String] = invalidCampaigns

  def getImpressionThreshold: Long = ImpressionThreshold

  def getValidExplorationCampaignsTargets: Set[String] = modelsMap.keySet

  def isValidExplorationCampaign(campaignId: String): Boolean = {
    !invalidCampaigns.contains(s"$campaignId")
  }

  def score(campaignId: String): Scores = {
    require(isValidExplorationCampaign(campaignId))
    modelsMap.get(s"$campaignId") match {
      case Some(model) =>
        // If we have information on the campaign, use it's model
        Scores(model.predictCtr, model.predictIr)
      case None =>
        // If the campaign has never been seen, sample from the prior distributions
        Scores(ctrPrior.sample(), irPrior.sample())
    }
  }

  def updateModel(
    currentBOLExplorer: BayesianOnlineLearnerExplorationModel,
    newCounts: Array[CampaignImpressionCounts],
    timestamp: Int = Timestamp.getCurrentSeconds
  ): BayesianOnlineLearnerExplorationModel = {

    val updatedModels: Array[CampaignBetaBinomialModel] = newCounts.map{ counts =>
      modelsMap.get(counts.key) match {
        case Some(ctModel) => ctModel.update(counts)
        case None => CampaignBetaBinomialModel(counts, this.priors)
      }
    }

    new BayesianOnlineLearnerExplorationModel(updatedModels, getImpressionThreshold, invalidCampaigns, timestamp)
  }

}