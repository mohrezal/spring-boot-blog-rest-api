#!/bin/bash

CATEGORY=0
USER=0
POST=0
while [[ "$#" -gt 0 ]]; do
    case $1 in
        --user) USER="$2"; shift ;;
        --post) POST="$2"; shift ;;
        --category) CATEGORY="$2"; shift ;;
        *) echo "Unknown parameter passed: $1"; exit 1 ;;
    esac
    shift
done

ARGS=()
[ "$USER" -gt 0 ] && ARGS+=("--user" "$USER")
[ "$CATEGORY" -gt 0 ] && ARGS+=("--category" "$CATEGORY")
[ "$POST" -gt 0 ] && ARGS+=("--post" "$POST")

ARGS+=("--spring.main.web-application-type=none" "--spring.devtools.restart.enabled=false")

./mvnw spring-boot:run \
  -Dspring-boot.run.profiles=seed \
  -Dspring-boot.run.arguments="${ARGS[*]}" \
  -Dcheckstyle.skip=true \
  -Dspotless.check.skip=true \
  -Dmaven.test.skip=true \
  -Dmaven.antrun.skip=true