import org.apache.log4j.LogManager
import session.SessionManager

object QueryExecutor {
    val log = LogManager.getLogger(this.getClass.getName)
    val sparkSession = SessionManager.createSession()

    def getFetchQuery(): String = {
        """
          | SELECT pincode
          | FROM shipping_package_address
          | WHERE uniware_sp_created >= "2023-11-01"
          |""".stripMargin
    }

    def getFilterQuery(): String = {
        """
          | SELECT count(distinct(pincode))
          | FROM raw_dataframe_view
          |""".stripMargin
    }

    def getJdbcOptions(query: String): Map[String, String] = {
        Map(
            "driver" -> "com.mysql.cj.jdbc.Driver",
            "url" -> "jdbc:mysql://db.address.unicommerce.infra:3306",
            "user" -> "developer",
            "password" -> "DevelopeR@4#",
            "dbtable" -> "turbo.shipping_package_address",
            "query" -> query,
            "header" -> "true",
            "inferSchema" -> "true",
            "mode" -> "failfast",
            "fetchSize" -> "50000"
        )
    }

    def main(args: Array[String]): Unit = {
        log.info("=== Spark query executor ===")
        val fetchQuery = getFetchQuery()
        val jdbcOptions = getJdbcOptions(fetchQuery)
        val filterQuery = getFilterQuery()

        val rawDataframe = sparkSession.read
                .format("jdbc")
                .options(jdbcOptions)
                .load()
        rawDataframe.createOrReplaceTempView("raw_dataframe_view")
        val outputDf = sparkSession.sql(filterQuery)
        outputDf.show(false)
    }
}
