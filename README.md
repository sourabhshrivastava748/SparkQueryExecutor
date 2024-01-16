# SparkQueryExecutor

## Compile:
    sbt test package

## Run pipeline:
    /spark/bin/spark-submit \
        --name "Spark Query Executor" \
        --properties-file src/main/resources/application.properties \
        --jars $(echo dependencies/*.jar | tr ' ' ',') \
        --deploy-mode cluster \
        target/scala-2.12/sparkqueryexecutor*.jar