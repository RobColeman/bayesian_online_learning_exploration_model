package model

case class CampaignImpressionCounts(
  campaignId:  String,
  impressions: Long,
  clicks:      Long,
  installs:    Long
) {

  val key: String = s"$campaignId"

  def +(that: CampaignImpressionCounts): CampaignImpressionCounts = {
    require(this.key == that.key)
    this.copy(
      impressions = this.impressions + that.impressions,
      clicks      = this.clicks + that.clicks,
      installs    = this.installs + that.installs
    )
  }

}
