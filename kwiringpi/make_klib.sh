#!/usr/bin/env bash

#compilerOpts = -I external/wiringpi/raspberrypi/include


PATH=$KONAN_HOME/bin:$PATH
COMPILER_OPTS="-I ../external/wiringpi/raspberrypi/include"

cinterop -def src/main/c_interop/libwiringPi.def -target raspberrypi -o wiringPi.klib -compilerOpts "$COMPILER_OPTS"
konanc -p library src/main/kotlin -l wiringPi -o kwiringPi -target raspberrypi
