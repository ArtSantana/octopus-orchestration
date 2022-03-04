package com.octopus.orchestration.dockerclient;

import com.spotify.docker.client.DefaultDockerClient;

public class DockerClient {

    private DockerClient() {}

    public static DefaultDockerClient init() {
        return new DefaultDockerClient("unix:///var/run/docker.sock");
    }
}
