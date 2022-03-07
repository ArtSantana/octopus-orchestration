package com.octopus.orchestration.services;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.octopus.orchestration.dockerclient.DockerClient;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient.ListContainersParam;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.Container;
import com.spotify.docker.client.messages.ContainerInfo;

class ContainersServiceTest {
    @InjectMocks
    private ContainersService containersService;

    @MockBean
    private DefaultDockerClient defaultDockerClient;

    @MockBean
    DockerClient dockerClient;

    @MockBean
    private static Container container;

    @Test
    void testListAll() throws DockerException, InterruptedException {
        when(defaultDockerClient.listContainers(ListContainersParam.allContainers())).thenReturn(List.of(container, container));
        List<Container> response = containersService.listAll();
        assertEquals(2, response.size());
    }

    @Test
    void testInspect() throws DockerException, InterruptedException {
        ContainerInfo containerInfo = mock(ContainerInfo.class);
            when(defaultDockerClient.inspectContainer(anyString())).thenReturn(containerInfo);
            ContainerInfo response = containersService.inspect("some-container-id");
            assertNotNull(response);
    }

    @Test
    void testGetLogs() throws DockerException, InterruptedException {
        LogStream logStream = mock(LogStream.class);
        String logs = "some-container-log";
            when(defaultDockerClient.logs(anyString(), any(), any())).thenReturn(logStream);
            when(logStream.readFully()).thenReturn(logs);
            String response = containersService.getLogs("some-container-id");
            assertEquals(logs, response);
    }

    @Test
    void testDelete() throws DockerException, InterruptedException {
            doNothing().when(defaultDockerClient).removeContainer(anyString());
            assertThatCode(() -> containersService.delete("some-container-id")).doesNotThrowAnyException();
    }
}
