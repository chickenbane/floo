#!/usr/bin/env bash

./gradlew :client:build
java -jar client/build/libs/client.jar