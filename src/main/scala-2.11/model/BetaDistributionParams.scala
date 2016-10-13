package model

case class BetaDistributionParams(alpha: Double, beta: Double) {
  require(alpha > 0 && beta > 0)
}
