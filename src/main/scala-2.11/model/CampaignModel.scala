package model

import org.apache.commons.math3.distribution.BetaDistribution

object CampaignModel {

  def apply(
    impressionCounts: CampaignImpressionCounts,
    globalPriors:     GlobalPriors
  ): CampaignModel = new CampaignModel(impressionCounts, globalPriors)

}

class CampaignModel(
  private val impressionCounts: CampaignImpressionCounts,
  globalPriors:     GlobalPriors
) {

  val key: String = impressionCounts.key
  val campaignId: String = impressionCounts.campaignId
  val priors: GlobalPriors = globalPriors

  def getTotalImpressions = impressionCounts.impressions
  def getClicks = impressionCounts.clicks
  def getInstalls = impressionCounts.installs

  def predictCtr: Double = ctrPosterior.sample()
  def predictIr: Double = irPosterior.sample()

  protected val ctrPosterior: BetaDistribution = genPosterior(
    impressionCounts.impressions,
    impressionCounts.clicks,
    priors.ctrPrior.alpha,
    priors.ctrPrior.beta
  )

  protected val irPosterior: BetaDistribution = genPosterior(
    impressionCounts.clicks,
    impressionCounts.installs,
    priors.irPrior.alpha,
    priors.irPrior.beta
  )

  protected def genPosterior(n: Long, x: Long, alpha: Double, beta: Double): BetaDistribution = {
    new BetaDistribution(alpha + x - 1, n - x + beta - 1)
  }

  def update(newImpressionCounts: CampaignImpressionCounts): CampaignModel = {
    require(newImpressionCounts.key == this.key)
    CampaignModel(this.impressionCounts + newImpressionCounts,priors)
  }

}