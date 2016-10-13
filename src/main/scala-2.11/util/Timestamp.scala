package util

object Timestamp {
  /**
    * convert milliseconds to seconds
    * @param ts
    * @return
    */
  def milliToSeconds(ts: Long): Int = (ts / 1000).toInt

  /**
    * get current timestamp in seconds
    * @return
    */
  def getCurrentSeconds: Int = this.milliToSeconds(System.currentTimeMillis())

  /**
    * get current timestamp in milliseconds
    * @return
    */
  def getCurrentMilliSeconds: Long = System.currentTimeMillis()
}
