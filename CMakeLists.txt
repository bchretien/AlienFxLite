CMAKE_MINIMUM_REQUIRED(VERSION 2.8)
ENABLE_LANGUAGE(CXX)
SET(CMAKE_VERBOSE_MAKEFILE OFF)

PROJECT(AlienFxLite)

SET(CMAKE_MODULE_PATH ${CMAKE_MODULE_PATH} "${CMAKE_SOURCE_DIR}/CMakeModules/")

#######################################################################
#                     GENERATE C++ LED CONTROLLER                     #
#######################################################################

FIND_PACKAGE(PkgConfig REQUIRED)

# Find libusb (1.0 or 0.1)
PKG_CHECK_MODULES(LIBUSB-1.0 libusb-1.0)
IF(LIBUSB-1.0_FOUND)
  SET(LIBUSB_LIB "-lusb-1.0")
  SET(LIBUSB_INCLUDE_DIRS ${LIBUSB-1.0_INCLUDE_DIRS})
ELSE(LIBUSB-1.0_FOUND)
  PKG_CHECK_MODULES(LIBUSB REQUIRED libusb)
  SET(LIBUSB_LIB "-lusb")
ENDIF(LIBUSB-1.0_FOUND)

# Java JNI headers required
FIND_PACKAGE(JNI2 REQUIRED)

# Useful variable
SET(ALIENLIB_SRC_PATH "${CMAKE_CURRENT_SOURCE_DIR}/src/cpp")

# Include paths
INCLUDE_DIRECTORIES(${ALIENLIB_SRC_PATH})
INCLUDE_DIRECTORIES(${JAVA_INCLUDE_PATH};${JAVA_INCLUDE_PATH2})
INCLUDE_DIRECTORIES(${LIBUSB_INCLUDE_DIRS})

# Source and headers of the project
SET(ALIEN_SRC "${ALIENLIB_SRC_PATH}/main.cpp")
SET(ALIEN_HEADERS "${ALIENLIB_SRC_PATH}/LEDController.h")

# 32 or 64bits?
IF(CMAKE_SIZEOF_VOID_P EQUAL 8)
  SET(ARCH "64")
ELSE(CMAKE_SIZEOF_VOID_P EQUAL 8)
  SET(ARCH "32")
ENDIF(CMAKE_SIZEOF_VOID_P EQUAL 8)

# Native library
ADD_LIBRARY(Alien${ARCH} SHARED ${ALIEN_SRC} ${ALIEN_HEADERS})
# Link with libusb
TARGET_LINK_LIBRARIES(Alien${ARCH} ${LIBUSB_LIB})
# Add library to lib folder
SET_TARGET_PROPERTIES(Alien${ARCH}
  PROPERTIES LIBRARY_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/lib")

# Generate library name for later use
SET(LIBRARY_EXTENSION ".so")
SET(NATIVE_LIBRARY_NAME "libAlien${ARCH}${LIBRARY_EXTENSION}")

#######################################################################
#                          GENERATE JAVA GUI                          #
#######################################################################

# Java required
FIND_PACKAGE(Java REQUIRED COMPONENTS Development)

IF(Java_VERSION_MINOR LESS 7)
  MESSAGE(ERROR "Java >= 7 is required")
ENDIF()

# Basic macros for the generation of jar files
INCLUDE(${CMAKE_SOURCE_DIR}/CMakeModules/UseJava.cmake)

# Include variable JAVA_ALIENFX_SRC
INCLUDE(src/java/uk/co/progger/alienFXLite/SourcesLib.cmake)

# GUI jar
SET(CMAKE_JAVA_TARGET_OUTPUT_NAME AlienFX)
SET(CMAKE_JAVA_JAR_ENTRY_POINT uk/co/progger/alienFXLite/Launcher)
SET(CMAKE_JAVA_COMPILE_FLAGS -Xlint:all)
ADD_JAR(AlienFX ${JAVA_ALIENFX_SRC})
GET_PROPERTY(AlienFX_jar TARGET AlienFX PROPERTY JAR_FILE)

# Add the images to the executable jar
ADD_CUSTOM_TARGET(Images_to_jar ALL DEPENDS AlienFX)
ADD_CUSTOM_COMMAND(
   COMMAND ${Java_JAR_EXECUTABLE}
   ARGS    uf ${AlienFX_jar} -C ${CMAKE_SOURCE_DIR}/src/java uk/co/progger/alienFXLite/gui/images/
   TARGET  Images_to_jar
   POST_BUILD
)

# Add the native library to the executable jar
ADD_CUSTOM_TARGET(Lib_to_jar ALL DEPENDS AlienFX Images_to_jar Alien${ARCH})
ADD_CUSTOM_COMMAND(
   COMMAND ${Java_JAR_EXECUTABLE}
   ARGS    uf ${AlienFX_jar} -C ${CMAKE_BINARY_DIR} lib/${NATIVE_LIBRARY_NAME}
   TARGET  Lib_to_jar
   POST_BUILD
)

#######################################################################
#                            SETUP INSTALL                            #
#######################################################################


# Install jar in the share directory
INSTALL(FILES ${AlienFX_jar} DESTINATION share/alienfx-lite)

# Install executable script to the bin directory
CONFIGURE_FILE(${CMAKE_CURRENT_SOURCE_DIR}/bin/alienfx-lite.in
               ${CMAKE_BINARY_DIR}/bin/alienfx-lite @ONLY)
INSTALL(PROGRAMS ${CMAKE_BINARY_DIR}/bin/alienfx-lite
        DESTINATION ${CMAKE_INSTALL_PREFIX}/bin)

#######################################################################
#                           SETUP UNINSTALL                           #
#######################################################################

CONFIGURE_FILE("${CMAKE_CURRENT_SOURCE_DIR}/CMakeModules/cmake_uninstall.cmake.in"
               "${CMAKE_CURRENT_BINARY_DIR}/cmake_uninstall.cmake"
               IMMEDIATE @ONLY)

ADD_CUSTOM_TARGET(uninstall
  COMMAND ${CMAKE_COMMAND} -P ${CMAKE_CURRENT_BINARY_DIR}/cmake_uninstall.cmake)
