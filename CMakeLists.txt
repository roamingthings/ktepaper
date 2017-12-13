cmake_minimum_required(VERSION 3.8)

set(CMAKE_MODULE_PATH ${CMAKE_CURRENT_LIST_DIR}/KotlinCMakeModule)
set(WIRINGPI_LIBRARY_DIR ${CMAKE_CURRENT_LIST_DIR}/external/wiringpi/raspberrypi/lib)
set(WIRINGPI_INCLUDE_DIR ${CMAKE_CURRENT_LIST_DIR}/external/wiringpi/raspberrypi/include)

project(epaper Kotlin)

cinterop(
        NAME libwiringPi
        DEF_FILE kwiringpi/src/main/c_interop/libwiringPi.def
        COMPILER_OPTS "-I ${WIRINGPI_INCLUDE_DIR}"
#        TARGET raspberrypi
)

konanc_library(
        NAME kwiringPi
        SOURCES kwiringpi/src/main/kotlin
        LIBRARIES libwiringPi
#        TARGET raspberrypi
)

konanc_library(
        NAME wsepd
        SOURCES wsepd/src/main/kotlin
        LIBRARIES kwiringPi
#        TARGET raspberrypi
)

konanc_executable(
        NAME epaper
        SOURCES epaper/src/main/kotlin
        LIBRARIES kwiringPi; wsepd
        LINKER_OPTS "-L${WIRINGPI_LIBRARY_DIR} -lwiringPi"
#        TARGET raspberrypi
)