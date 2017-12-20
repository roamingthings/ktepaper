#!/usr/bin/env bash

DIR=$(cd "$(dirname "${BASH_SOURCE[0]}" )" && pwd )
PATH=$KONAN_HOME/bin:$PATH

konanc -opt src/main/kotlin \
                     -r $DIR/../kwiringpi \
                     -r $DIR/../wsepd \
                     -r $DIR/../ktgfx \
                     -l kwiringPi \
                     -l ktgfx \
                     -l wsepd \
                     -target raspberrypi \
                     -o epaper
