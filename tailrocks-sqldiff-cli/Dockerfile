FROM openjdk:19-jdk-slim-buster

ADD docker/krendel.sh /krendel.sh

RUN chmod a+x /krendel.sh

ADD build/libs/krendel-cli-0.1-all.jar /opt/krendel/krendel.jar

COPY docker/krendel.sh /usr/bin/krendel

RUN bash -c 'touch /opt/krendel/krendel.jar'

ENV TERM=xterm-256color

CMD ["/bin/bash"]
