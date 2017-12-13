#!/usr/bin/env bash

if [ ! -d $KONAN_HOME ]; then
  echo "Set KONAN_HOME to Kotlin/Native redist"
  exit 1
fi

for l in kwiringpi wsepd ; do
  (cd $l && ./make_klib.sh)
done

for e in epaper ; do
  (cd $e && ./build.sh)
done
