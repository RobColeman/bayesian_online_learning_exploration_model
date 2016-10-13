package model

import config.Configuration

/**
  * Factory for generating priors
  */
object GlobalPriorsFactory extends Configuration {

  val CtrPriorStrength: Double = conf.getLong("ctrPriorStrength").toDouble
  val IrPriorStrength: Double = conf.getLong("irPriorStrength").toDouble

  val globalCtr: Double = conf.getDouble("network.ctr")
  val globalIr: Double = conf.getDouble("network.ir")

  def globalPriors(): GlobalPriors = {
    GlobalPriors(
      BetaDistributionParams(CtrPriorStrength * globalCtr, CtrPriorStrength * (1 - globalCtr)),
      BetaDistributionParams(IrPriorStrength * globalIr, IrPriorStrength * (1 - globalIr))
    )
  }

}

/**
  * Wrapper for prior model parameters
  * @param ctrPrior
  * @param irPrior
  */
case class GlobalPriors(
  ctrPrior: BetaDistributionParams,
  irPrior:  BetaDistributionParams
)
