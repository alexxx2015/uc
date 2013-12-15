#!/bin/bash

command -v scons >/dev/null 2>&1 || { echo >&2 "scons is not installed. using preinstalled libraries."; mkdir -p ../../../target/classes/; cp -r ../../../nativeLibs ../../../target/classes/; exit 0; }
scons $@
