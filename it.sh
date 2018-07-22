#!/usr/bin/env bash

# build client
./gradlew :it:build


# run the client
./it/build/libs/it.jar
