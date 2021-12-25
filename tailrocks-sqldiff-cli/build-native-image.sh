#!/usr/bin/env bash
ABSOLUTE_PATH=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
cd "${ABSOLUTE_PATH}" || exit

set -e

printf "> \e[1;37mBuilding native-image\e[0m\n"

native-image --no-server -cp build/libs/krendel-cli-0.1-all.jar
