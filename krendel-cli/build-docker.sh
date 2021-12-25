#!/usr/bin/env bash
ABSOLUTE_PATH=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
cd "${ABSOLUTE_PATH}" || exit

set -e

printf "> \e[1;37mBuilding docker\e[0m\n"

DOCKER_REPOSITORY="quay.io/scentbird/krendel"
APP_VERSION="latest"

printf "\n> \e[1;37mBuilding Docker image\e[0m\n"
printf "# Image \e[1;37m%s\e[0m\n\n" "${DOCKER_REPOSITORY}"
docker build -t ${DOCKER_REPOSITORY}:"${APP_VERSION}" \
  -t ${DOCKER_REPOSITORY}:latest \
  ./
