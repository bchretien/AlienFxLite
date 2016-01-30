AlienFX Lite
============

[![Build Status](https://travis-ci.org/bchretien/AlienFxLite.svg?branch=master)](https://travis-ci.org/bchretien/AlienFxLite)

A keyboard color management tool for Alienware laptops.

This software was initially developed for M15x and M17x laptops. The latest
modifications were tested on the M14x R2 and R3 laptops.

## Dependencies

* libusb (version 0.1 and 1.0 supported)
* lsusb (Linux)
* CMake (>= 2.8)
* Java Development Kit (JDK)
* C++ compiler (gcc or clang)

## Compilation

In order to compile the Linux library (`libAlienXX.so`) and the Java GUI (`AlienFX.jar`):

```sh
$ mkdir build && cd build
$ cmake ..
$ make && make install
```

`AlienFX.jar` will be installed in the root folder. Then, you can launch the program:

  * if your user has USB rights:

```sh
$ java -jar AlienFX.jar
```

  * else:

```sh
$ sudo java -jar AlienFX.jar
```

## Thanks

* Thanks to [Ingrater][1] for providing the protocol for the AlienFX device and
  some windows native code.
* Thanks to Wattos for developing the [first versions][2] of AlienFX Lite.


[1]: http://3d.benjamin-thaut.de/
[2]: http://forum.notebookreview.com/alienware/458528-alienfx-lite-linux-windows-alienfx-tool.html
