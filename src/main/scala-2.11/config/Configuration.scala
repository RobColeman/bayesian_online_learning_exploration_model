package config

import com.typesafe.config.{Config, ConfigFactory}

/**
  * mixin for application configuration
  */
trait Configuration {
  lazy val conf: Config = ConfigFactory.load("reference.conf").getConfig("default")
}
