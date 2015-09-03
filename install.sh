#!/bin/bash
# You can pass CMake arguments to the install script

DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )

pushd ${DIR}

rm -rf build
mkdir -p build && cd build
cmake .. $@
make && make install

popd
