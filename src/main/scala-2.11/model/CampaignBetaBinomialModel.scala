package model

import org.apache.commons.math3.distribution.BetaDistribution

object CampaignBetaBinomialModel {

  def apply(
    impressionCounts: CampaignImpressionCounts,
    globalPriors:     BOLEPriors
  ): CampaignBetaBinomialModel = new CampaignBetaBinomialModel(impressionCounts, globalPriors)

}

class CampaignBetaBinomialModel(
  private val impressionCounts: CampaignImpressionCounts,
  globalPriors:     BOLEPriors
) {

  val key: String = impressionCounts.key
  val campaignId: String = impressionCounts.campaignId
  val priors: BOLEPriors = globalPriors

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

  def update(newImpressionCounts: CampaignImpressionCounts): CampaignBetaBinomialModel = {
    require(newImpressionCounts.key == this.key)
    CampaignBetaBinomialModel(this.impressionCounts + newImpressionCounts,priors)
  }

}