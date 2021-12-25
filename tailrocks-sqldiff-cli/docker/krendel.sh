#!/usr/bin/env bash
ABSOLUTE_PATH=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
cd "${ABSOLUTE_PATH}" || exit

params=""

for var in "$@"; do
  params="${params} \"${var}\""
done

jar_cmd="java -XX:+ShowCodeDetailsInExceptionMessages -jar /opt/krendel/krendel.jar ${params}"

# for debug only
if [ "${DEBUG}" = 'true' ]; then
  echo "${jar_cmd}"
fi

eval "${jar_cmd}"
