package com.octopus.orchestration.dockerclient;

import com.octopus.orchestration.utils.ConfigService;
import com.spotify.docker.client.DefaultDockerClient;
import org.springframework.stereotype.Component;

@Component
public class DockerClient {

    private DockerClient() {}

    public static DefaultDockerClient init() {
        return new DefaultDockerClient(ConfigService.getValue("docker_host", "unix:///var/run/docker.sock"));
    }
}
