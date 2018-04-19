!/usr/bin/env bash

#Rebuilds everything by pulling latest changes from both core and cmd repo and then building them

cd ../Angelia-core
git stash -a
git checkout master
git pull https://github.com/Maxopoly/Angelia-core.git master:master
mvn clean package install
cd ../Angelia-core
git stash -a
git checkout master
git pull https://github.com/Maxopoly/Angelia-cmd.git master:master
mvn clean package