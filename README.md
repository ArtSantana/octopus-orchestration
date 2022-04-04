# Octopus Orchestration [![Java CI with Maven](https://github.com/ArtSantana/octopus-orchestration/actions/workflows/maven.yml/badge.svg)](https://github.com/ArtSantana/octopus-orchestration/actions/workflows/maven.yml)

API developed in order to manage local docker containers through a GUI.

## API Requests:
### Find All 
**GET** /containers

### Find All By Status 
**GET** /containers?status={active/inactive}

### Inspect Container
**GET** /containers/inspect/{containerId}

### Get Container Logs
**GET** /containers/logs/{containerId}

### Delete Container By ID
**DELETE** /containers/{containerId}

### Start All Containers
**PUT** /containers/start

### Stop All Containers
**PUT** /containers/stop

### Kill All Container
**PUT** /containers/kill

### Restart All Containers
**PUT** /containers/restart

