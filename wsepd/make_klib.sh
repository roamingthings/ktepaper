#!/usr/bin/env bash


DIR=$(cd "$(dirname "${BASH_SOURCE[0]}" )" && pwd )
PATH=$KONAN_HOME/bin:$PATH

konanc -opt src/main/kotlin \
       -p library \
       -r $DIR/../kwiringpi \
       -r $DIR/../ktgfx \
       -l kwiringPi \
       -l ktgfx \
       -target raspberrypi \
       -o wsepd
