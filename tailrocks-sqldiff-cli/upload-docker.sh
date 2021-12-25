#!/usr/bin/env bash
ABSOLUTE_PATH=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
cd "${ABSOLUTE_PATH}" || exit

# config
DOCKER_REMOTE_REPOSITORY="quay.io/scentbird/krendel"
DOCKER_IMAGE_VERSION="latest"
# @end config

printf "\n> \e[93m\033[1mUploading Docker image\e[0m\n\n"

set -e

printf "# Uploading to %s:%s\n" "${DOCKER_REMOTE_REPOSITORY}" "${DOCKER_IMAGE_VERSION}"

docker push ${DOCKER_REMOTE_REPOSITORY}:"${DOCKER_IMAGE_VERSION}"
