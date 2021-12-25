#!/usr/bin/env bash
ABSOLUTE_PATH=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
cd "${ABSOLUTE_PATH}" || exit

printf "\n> \e[1;37mInstalling\e[0m\n\n"

set -e

rm -rf build

./gradlew clean publishToMavenLocal
