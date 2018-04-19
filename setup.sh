!/usr/bin/env bash

#This scripts assumes you have cloned angelia-cmd and now runs this script as the first thing
#to setup everything else


mkdir plugins/
cd ..
git clone git@github.com:Maxopoly/Angelia-core.git
cd Angelia-core/
mvn clean package install
cd ../Angelia-cmd
mvn clean package
cd target/
