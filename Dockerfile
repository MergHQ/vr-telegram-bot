FROM openjdk:8u181-alpine3.8

ARG TELEGRAM_TOKEN
ENV $TELEGRAM_TOKEN TELEGRAM_TOKEN

WORKDIR /

COPY target/uberjar/vr-telegram-bot-0.1.0-SNAPSHOT-standalone.jar bot.jar

CMD java -jar bot.jar