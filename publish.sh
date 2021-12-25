#!/usr/bin/env bash
ABSOLUTE_PATH=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
cd "${ABSOLUTE_PATH}" || exit

printf "\n> \e[1;37mPublishing\e[0m\n\n"

set -e

rm -rf build

if [ -n "${ARTIFACTORY_CONTEXT_URL}" ]; then
  printf "# Publishing library to Artifactory\n\n"
  ./gradlew clean artifactoryPublish
else
  printf "# ERROR: ARTIFACTORY_CONTEXT_URL is not specified\n"
fi
