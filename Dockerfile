FROM openjdk:latest
MAINTAINER Karol Wójcik <karol.wojcik@tuta.io>

ADD .holy-lambda/build/output.jar output.jar

CMD java -jar output.jar "cmcc.mailup.core.ExampleLambda"