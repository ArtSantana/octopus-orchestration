#!/bin/bash

mvn clean package $@

docker build -t octopus-orchestration .