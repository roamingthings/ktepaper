# Docker image for kotlin-cross compilation

This docker images allows you to cross-compile kotlin-native apps for a Rasperry Pi on
a macOS (and most likely Windows) system.

## Build the image

Build the image by using the provided `build.sh` script.

## Run the container

If you want to reuse a container you can set it up with the following command:

```
docker run -it -v $(pwd):/home/gradle -v $HOME/.konan:/home/gradle/.konan -v $HOME/.gradle:/home/gradle/.gradle -w /home/gradle --name kotlinc roamingthings/kotlin-native /bin/bash
```

This will map the current directory (your project folder) to the build-directory of the container.

After startup you will have a shell prompt.

To get into a running container use

```
docker exec -it kotlinc /bin/bash
```  

If you want to use the container for automatic building of your project use

```
docker run --rm -v $(pwd):/home/gradle -v $HOME/.konan:/home/gradle/.konan -v $HOME/.gradle:/home/gradle/.gradle -w /home/gradle --name kotlinc roamingthings/kotlin-native ./build.sh
```
