#!/usr/bin/env bash
ABSOLUTE_PATH=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
cd "${ABSOLUTE_PATH}" || exit

module_name="$(basename "${ABSOLUTE_PATH}")"

printf "> \e[1;37mTesting ${module_name}\e[0m\n"

set -e

GIT_COMMIT_DESC=$(git log --format=%B -n 1)

if [[ ${GIT_COMMIT_DESC} == *"[skip test]"* ]]; then
  echo "Ignore tests"
  exit 0
fi

if [[ ${SKIP_TEST} == "true" ]]; then
  echo "Ignore tests"
  exit 0
fi

rm -rf build

export SPRING_PROFILES_ACTIVE="test"
../gradlew test --stacktrace ${GRADLE_EXTRA_ARGS}
