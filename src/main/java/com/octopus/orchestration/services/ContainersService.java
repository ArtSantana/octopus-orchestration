package com.octopus.orchestration.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.octopus.orchestration.dockerclient.DockerClient;
import com.octopus.orchestration.enums.DockerEnums;
import com.octopus.orchestration.exceptions.BaseException;
import com.octopus.orchestration.exceptions.DockerIllegalArgumentException;
import com.spotify.docker.client.DockerClient.ListContainersParam;
import com.spotify.docker.client.DockerClient.LogsParam;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.ContainerNotFoundException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.Container;
import com.spotify.docker.client.messages.ContainerInfo;

@Service
public class ContainersService {
    
    private static final String CONTAINER_NOT_FOUND = "Container not found. Id = ";

    @Autowired
    private DockerClient dockerClient;

    public List<Container> listAll()  {
        try {
            return dockerClient.getClient().listContainers(ListContainersParam.allContainers());
        } catch (DockerException | InterruptedException e) {
            throw new BaseException("Failed to list all containers", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<Container> listAll(String status)  {
        try {
            if (status != null && !status.isBlank()) {
                status = status.toUpperCase();
                if (DockerEnums.ACTIVE.name().equals(status)) {
                    return dockerClient.getClient().listContainers(ListContainersParam.withStatusRunning());
                }
                if (DockerEnums.INACTIVE.name().equals(status)) {
                    return dockerClient.getClient().listContainers(ListContainersParam.withStatusExited());
                }
            }
            throw new DockerIllegalArgumentException("Invalid container status");
        } catch (DockerException | InterruptedException e) {
            throw new BaseException("Failed to list all containers", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ContainerInfo inspect(String id) {
        try {
            return dockerClient.getClient().inspectContainer(id);
        } catch(ContainerNotFoundException e) {
            throw new BaseException(CONTAINER_NOT_FOUND + id, HttpStatus.NOT_FOUND);
        } catch (DockerException | InterruptedException e) {
            throw new BaseException("Failed to inspect container with id = " + id, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String getLogs(String id) {
        try (LogStream stream = dockerClient.getClient().logs(id, LogsParam.stdout(), LogsParam.stderr())) {
            return stream.readFully();
        } catch(ContainerNotFoundException e) {
            throw new BaseException(CONTAINER_NOT_FOUND + id, HttpStatus.NOT_FOUND);
        } catch (DockerException | InterruptedException e) {
            throw new BaseException("Failed to get container logs with id = " + id, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void delete(String id) {
        try {
            dockerClient.getClient().removeContainer(id);
        } catch(ContainerNotFoundException e ) {
            throw new BaseException(CONTAINER_NOT_FOUND + id, HttpStatus.NOT_FOUND);
        } catch (DockerException | InterruptedException e) {
            throw new BaseException("Failed to remove container with id = " + id, HttpStatus.INTERNAL_SERVER_ERROR);
        } 
    }

    public void start(List<String> containersIds) {
        containersIds.forEach(containerId -> {
            try {
                dockerClient.getClient().startContainer(containerId);
            } catch(ContainerNotFoundException e ) {
                throw new BaseException(CONTAINER_NOT_FOUND + containerId, HttpStatus.NOT_FOUND);
            } catch (DockerException | InterruptedException e) {
                throw new BaseException("Failed to start container with id = " + containerId, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        });
    }

    public void stop(List<String> containersIds) {
        containersIds.forEach(containerId -> {
            try {
                dockerClient.getClient().stopContainer(containerId, 5);
            } catch(ContainerNotFoundException e ) {
                throw new BaseException(CONTAINER_NOT_FOUND + containerId, HttpStatus.NOT_FOUND);
            } catch (DockerException | InterruptedException e) {
                throw new BaseException("Failed to stop container with id = " + containerId, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        });
    }

    public void kill(List<String> containersIds) {
        containersIds.forEach(containerId -> {
            try {
                dockerClient.getClient().killContainer(containerId);
            } catch(ContainerNotFoundException e ) {
                throw new BaseException(CONTAINER_NOT_FOUND + containerId, HttpStatus.NOT_FOUND);
            } catch (DockerException | InterruptedException e) {
                throw new BaseException("Failed to kill container with id = " + containerId, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        });
    }

    public void restart(List<String> containersIds) {
        containersIds.forEach(containerId -> {
            try {
                dockerClient.getClient().restartContainer(containerId, 5);
            } catch(ContainerNotFoundException e ) {
                throw new BaseException(CONTAINER_NOT_FOUND + containerId, HttpStatus.NOT_FOUND);
            } catch (DockerException | InterruptedException e) {
                throw new BaseException("Failed to restart container with id = " + containerId, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        });
    }
}
