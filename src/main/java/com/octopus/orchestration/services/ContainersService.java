package com.octopus.orchestration.services;

import com.octopus.orchestration.dockerclient.DockerClient;
import com.octopus.orchestration.exceptions.ContainersException;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient.ListContainersParam;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.Container;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ContainersService {

    final DefaultDockerClient dockerClient = DockerClient.init();

    public List<Container> listAll()  {
        try {
            return dockerClient.listContainers(ListContainersParam.allContainers());
        } catch (DockerException | InterruptedException e) {
            throw new ContainersException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
