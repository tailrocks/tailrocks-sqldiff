#!/usr/bin/env bash
ABSOLUTE_PATH=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
cd "${ABSOLUTE_PATH}" || exit

set -e

printf "> \e[1;37mBuilding jar\e[0m\n"

printf "\n# \e[93mBuilding Gradle project\e[0m\n\n"
../gradlew clean build -x test --stacktrace ${GRADLE_EXTRA_ARGS}

printf "\n# \e[93mBuilt application\e[0m\n\n"
set +e
ls build/libs/* | grep jar

echo ""
