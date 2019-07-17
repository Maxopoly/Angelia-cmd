#!/usr/bin/env bash

#Rebuilds everything

cd ../Angelia-core
mvn clean package install
cd ../Angelia-cmd
mvn clean package
