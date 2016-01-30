CMAKE_MINIMUM_REQUIRED(VERSION 2.8)
ENABLE_LANGUAGE(CXX)
SET(CMAKE_VERBOSE_MAKEFILE OFF)

PROJECT(AlienFxLite)

SET(CMAKE_MODULE_PATH ${CMAKE_MODULE_PATH} "${CMAKE_SOURCE_DIR}/CMakeModules/")

########################################################################
# FIRST PART: GENERATE LED CONTROLLER LIB (C++)
########################################################################

FIND_PACKAGE(PkgConfig REQUIRED)

# Find libusb (1.0 or 0.1)
PKG_CHECK_MODULES(LIBUSB-1.0 libusb-1.0)
IF(LIBUSB-1.0_FOUND)
  SET(LIBUSB_LIB "-lusb-1.0")
  SET(LIBUSB_INCLUDE_DIRS ${LIBUSB-1.0_INCLUDE_DIRS})
ELSE(LIBUSB-1.0_FOUND)
  PKG_CHECK_MODULES(LIBUSB REQUIRED libusb)
  SET(LIBUSB_LIB "-lusb")
  SET(LIBUSB_INCLUDE_DIRS ${LIBUSB_INCLUDE_DIRS})
ENDIF(LIBUSB-1.0_FOUND)

# Java JNI headers required
FIND_PACKAGE(JNI2 REQUIRED)

# Useful variable
SET(ALIENLIB_SRC_PATH  "${CMAKE_CURRENT_SOURCE_DIR}/src/cpp")

# Include paths
INCLUDE_DIRECTORIES(${ALIENLIB_SRC_PATH})
INCLUDE_DIRECTORIES(${JAVA_INCLUDE_PATH};${JAVA_INCLUDE_PATH2})
INCLUDE_DIRECTORIES(${LIBUSB_INCLUDE_DIRS})

# Source and headers of the project
SET(ALIEN_SRC "${ALIENLIB_SRC_PATH}/main.cpp")
SET(ALIEN_HEADERS "${ALIENLIB_SRC_PATH}/LEDController.h")

# Using lsusb to find AlienFX PID for now
FIND_PROGRAM(LSUSB_COMMAND NAMES lsusb)
IF("${LSUSB_COMMAND}" STREQUAL "LSUSB_COMMAND-NOTFOUND")
  MESSAGE(" -- ERROR: lsusb command not found on your system")
ELSE("${LSUSB_COMMAND}" STREQUAL "LSUSB_COMMAND-NOTFOUND")
  MESSAGE("-- Found lsusb")
ENDIF("${LSUSB_COMMAND}" STREQUAL "LSUSB_COMMAND-NOTFOUND")

# 32 or 64bits?
IF(CMAKE_SIZEOF_VOID_P EQUAL 8)
  SET(ARCH "64")
ELSE(CMAKE_SIZEOF_VOID_P EQUAL 8)
  SET(ARCH "32")
ENDIF(CMAKE_SIZEOF_VOID_P EQUAL 8)

# create the lib directory
FILE(MAKE_DIRECTORY lib)

# Native library
ADD_LIBRARY(Alien${ARCH} SHARED ${ALIEN_SRC} ${ALIEN_HEADERS})
# Link with libusb
TARGET_LINK_LIBRARIES(Alien${ARCH} ${LIBUSB_LIB})
# Add library to lib folder
SET_TARGET_PROPERTIES(Alien${ARCH} PROPERTIES LIBRARY_OUTPUT_DIRECTORY "lib")

# Generate library name for later use
SET(LIBRARY_EXTENSION ".so")
SET(NATIVE_LIBRARY_NAME "libAlien${ARCH}${LIBRARY_EXTENSION}")

# Install directory
SET(CMAKE_INSTALL_PREFIX ${CMAKE_CURRENT_SOURCE_DIR})
INSTALL(
  TARGETS Alien${ARCH}
  RUNTIME DESTINATION bin
  LIBRARY DESTINATION lib
  ARCHIVE DESTINATION lib
)

########################################################################
# SECOND PART: GENERATE JAVA GUI
########################################################################

# Java required
FIND_PACKAGE(Java REQUIRED COMPONENTS Development)

# Basic macros for the generation of jar files
INCLUDE(${CMAKE_SOURCE_DIR}/CMakeModules/UseJava.cmake)

# Include variable JAVA_ALIENFX_SRC
INCLUDE(src/java/uk/co/progger/alienFXLite/SourcesLib.cmake)

# GUI jar
SET(CMAKE_JAVA_TARGET_OUTPUT_NAME AlienFX)
SET(CMAKE_JAVA_JAR_ENTRY_POINT uk/co/progger/alienFXLite/Launcher)
SET(CMAKE_JAVA_COMPILE_FLAGS -Xlint:all)
ADD_JAR(AlienFX ${JAVA_ALIENFX_SRC})

# Install in the root folder
INSTALL_JAR(AlienFX ${CMAKE_CURRENT_SOURCE_DIR})

# Add the images to the executable jar
ADD_CUSTOM_TARGET(Images_to_jar ALL DEPENDS AlienFX)
ADD_CUSTOM_COMMAND(
   COMMAND ${Java_JAR_EXECUTABLE}
   ARGS    uf ${CMAKE_BINARY_DIR}/AlienFX.jar -C ${CMAKE_SOURCE_DIR}/src/java uk/co/progger/alienFXLite/gui/images/
   TARGET  Images_to_jar
   POST_BUILD
)

# Add the native library to the executable jar
ADD_CUSTOM_TARGET(Lib_to_jar ALL DEPENDS AlienFX)
ADD_CUSTOM_COMMAND(
   COMMAND ${Java_JAR_EXECUTABLE}
   ARGS    uf ${CMAKE_BINARY_DIR}/AlienFX.jar -C ${CMAKE_BINARY_DIR} lib/${NATIVE_LIBRARY_NAME}
   TARGET  Lib_to_jar
   POST_BUILD
)
