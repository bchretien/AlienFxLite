#!/bin/sh
# You can pass CMake arguments to the install script

rm -rf build
mkdir -p build && cd build
cmake .. $@
make && make install
cd ..
