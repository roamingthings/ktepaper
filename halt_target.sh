#!/usr/bin/env bash

TARGET=$(<.target)

echo "*** Halting target"
ssh $TARGET "sudo halt"
