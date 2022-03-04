package com.octopus.orchestration.services;

import com.octopus.orchestration.dockerclient.DockerClient;
import com.octopus.orchestration.exceptions.ContainersException;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient.ListContainersParam;
import com.spotify.docker.client.DockerClient.LogsParam;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.Container;
import com.spotify.docker.client.messages.ContainerInfo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContainersService {

    private final DefaultDockerClient dockerClient = DockerClient.init();

    public List<Container> listAll()  {
        try {
            return dockerClient.listContainers(ListContainersParam.allContainers());
        } catch (DockerException | InterruptedException e) {
            throw new ContainersException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public ContainerInfo inspect(String id) {
        try {
            return dockerClient.inspectContainer(id);
        } catch (DockerException | InterruptedException e) {
            throw new ContainersException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public String getLogs(String id) {
        try (LogStream stream = dockerClient.logs(id, LogsParam.stdout(), LogsParam.stderr())) {
            return stream.readFully();
        } catch (DockerException | InterruptedException e) {
            throw new ContainersException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public String delete(String id) {
        try {
            dockerClient.removeContainer(id);
            return id;
        } catch (DockerException | InterruptedException e) {
            throw new ContainersException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
