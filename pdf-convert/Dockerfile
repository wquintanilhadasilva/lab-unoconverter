FROM openjdk:11.0-jre AS builder

RUN apt-get update \
    && apt-get install -y libreoffice --no-install-recommends \
    && apt-get install -y python3-pip

RUN pip install unoserver

FROM builder AS final
ENV APPLICATION_FILE_UPLOADDIR=/home/appuser/uploads

RUN useradd -ms /bin/bash appuser
WORKDIR /home/appuser

ADD entrypoint.sh entrypoint.sh
RUN chmod 755 entrypoint.sh && chown appuser:appuser entrypoint.sh

COPY /target/pdf-convert-0.0.1-SNAPSHOT.war /home/appuser/app.war
RUN chown appuser:appuser app.war

USER appuser

EXPOSE 8081

ENTRYPOINT ["./entrypoint.sh"]

