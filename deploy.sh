#!/usr/bin/env bash

DIR=$(cd "$(dirname "${BASH_SOURCE[0]}" )" && pwd )
APP_DIR=epaper
APP_EXE=epaper.kexe
TARGET=$(<.target)

echo "*** Deploying executable to target"
scp $DIR/$APP_DIR/$APP_EXE $TARGET:/home/pi

echo "*** Starting execution on target"
ssh $TARGET "export WIRINGPI_DEBUG=true && ./${APP_EXE}"
