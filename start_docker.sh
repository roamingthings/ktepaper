#! /bin/sh
docker run --rm -it -v /Users/alxs/Development/Workspaces/kotlin-native/ktepaper:/home/gradle -v /Users/alxs/.ssh:/home/gradle/.ssh -v /Users/alxs/.konan:/home/gradle/.konan -v /Users/alxs/.gradle:/home/gradle/.gradle -w /home/gradle --name kotlinc roamingthings/kotlin-native /bin/bash
