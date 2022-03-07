package com.octopus.orchestration.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.octopus.orchestration.dockerclient.DockerClient;
import com.octopus.orchestration.enums.DockerEnums;
import com.octopus.orchestration.exceptions.BaseException;
import com.octopus.orchestration.exceptions.DockerIllegalArgumentException;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient.ListContainersParam;
import com.spotify.docker.client.DockerClient.LogsParam;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.ContainerNotFoundException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.Container;
import com.spotify.docker.client.messages.ContainerInfo;

@Service
public class ContainersService {
    
    private static final String CONTAINER_NOT_FOUND = "Container not found";

    private final DefaultDockerClient dockerClient = DockerClient.init();

    public List<Container> listAll()  {
        try {
            return dockerClient.listContainers(ListContainersParam.allContainers());
        } catch (DockerException | InterruptedException e) {
            throw new BaseException("Failed to list all containers", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<Container> listAll(String status)  {
        try {
            if (status != null && !status.isBlank()) {
                status = status.toUpperCase();
                if (DockerEnums.ACTIVE.name().equals(status)) {
                    return dockerClient.listContainers(ListContainersParam.withStatusRunning());
                }
                if (DockerEnums.INACTIVE.name().equals(status)) {
                    return dockerClient.listContainers(ListContainersParam.withStatusExited());
                }
            }
            throw new DockerIllegalArgumentException("Invalid container status");
        } catch (DockerException | InterruptedException e) {
            throw new BaseException("Failed to list all containers", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ContainerInfo inspect(String id) {
        try {
            return dockerClient.inspectContainer(id);
        } catch(ContainerNotFoundException e) {
            throw new BaseException(CONTAINER_NOT_FOUND, HttpStatus.NOT_FOUND);
        } catch (DockerException | InterruptedException e) {
            throw new BaseException("Failed to inspect container with id = " + id, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String getLogs(String id) {
        try (LogStream stream = dockerClient.logs(id, LogsParam.stdout(), LogsParam.stderr())) {
            return stream.readFully();
        } catch(ContainerNotFoundException e) {
            throw new BaseException(CONTAINER_NOT_FOUND, HttpStatus.NOT_FOUND);
        } catch (DockerException | InterruptedException e) {
            throw new BaseException("Failed to get container logs with id = " + id, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void delete(String id) {
        try {
            dockerClient.removeContainer(id);
        } catch(ContainerNotFoundException e ) {
            throw new BaseException(CONTAINER_NOT_FOUND, HttpStatus.NOT_FOUND);
        } catch (DockerException | InterruptedException e) {
            throw new BaseException("Failed to delete container with id = " + id, HttpStatus.INTERNAL_SERVER_ERROR);
        } 
    }
}
