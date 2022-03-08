package com.octopus.orchestration.services;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

import com.octopus.orchestration.dockerclient.DockerClient;
import com.octopus.orchestration.exceptions.BaseException;
import com.octopus.orchestration.exceptions.DockerIllegalArgumentException;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient.ListContainersParam;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.Container;
import com.spotify.docker.client.messages.ContainerInfo;

@SpringBootTest(classes = ContainersService.class)
class ContainersServiceTest {

    @Autowired
    private ContainersService containersService;

    @MockBean
    private DockerClient dockerClient;

    @MockBean
    private DefaultDockerClient defaultDockerClient;

    @MockBean
    Container container;

    @BeforeEach
    public void init() {
        when(dockerClient.getClient()).thenReturn(defaultDockerClient);
    }

    @Test
    void testListAll() throws DockerException, InterruptedException {
        when(defaultDockerClient.listContainers(any())).thenReturn(List.of(container, container));
        List<Container> response = containersService.listAll();
        assertEquals(2, response.size());
    }

    @Test
    void testListAllShouldThrowException() throws DockerException, InterruptedException {
        when(defaultDockerClient.listContainers(any())).thenThrow(DockerException.class);
        BaseException thrown =
                assertThrows(BaseException.class,
                        () -> containersService.listAll(),
                        "It was expected that listAll() thrown an exception, " +
                                "due to an error in DefaultDockerClient");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getHttpStatus());
    }

    @Test
    void testListAllByStatusActive() throws DockerException, InterruptedException {
        when(defaultDockerClient.listContainers(ListContainersParam.withStatusRunning())).thenReturn(List.of(container, container));
        List<Container> response = containersService.listAll("active");
        assertEquals(2, response.size());
    }

    @Test
    void testListAllByStatusActiveShouldThrowException() throws DockerException, InterruptedException {
        when(defaultDockerClient.listContainers(ListContainersParam.withStatusRunning())).thenThrow(DockerException.class);
        BaseException thrown =
                assertThrows(BaseException.class,
                        () -> containersService.listAll("active"),
                        "It was expected that listAll() thrown an exception, " +
                                "due to an error in DefaultDockerClient");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getHttpStatus());
    }

    @Test
    void testListAllByStatusInactive() throws DockerException, InterruptedException {
        when(defaultDockerClient.listContainers(ListContainersParam.withStatusExited())).thenReturn(List.of(container));
        List<Container> response = containersService.listAll("inactive");
        assertEquals(1, response.size());
    }

    @Test
    void testListAllByStatusInactiveShouldThrowException() throws DockerException, InterruptedException {
        when(defaultDockerClient.listContainers(ListContainersParam.withStatusExited())).thenThrow(DockerException.class);
        BaseException thrown =
                assertThrows(BaseException.class,
                        () -> containersService.listAll("inactive"),
                        "It was expected that listAll() thrown an exception, " +
                                "due to an error in DefaultDockerClient");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getHttpStatus());
    }

    @Test
    void testListAllByWrongStatus() {
        BaseException thrown =
                assertThrows(DockerIllegalArgumentException.class,
                        () -> containersService.listAll("wrong-status"),
                        "It was expected that listAll() thrown an exception, " +
                                "due to an error in DefaultDockerClient");
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getHttpStatus());
    }

    @Test
    void testInspect() throws DockerException, InterruptedException {
        ContainerInfo containerInfo = mock(ContainerInfo.class);
        when(defaultDockerClient.inspectContainer(anyString())).thenReturn(containerInfo);
        ContainerInfo response = containersService.inspect("some-container-id");
        assertNotNull(response);
    }

    @Test
    void testInspectShouldThrowException() throws DockerException, InterruptedException {
        when(defaultDockerClient.inspectContainer(anyString())).thenThrow(DockerException.class);
        BaseException thrown =
                assertThrows(BaseException.class,
                        () -> containersService.inspect("some-container-id"),
                        "It was expected that listAll() thrown an exception, " +
                                "due to an error in DefaultDockerClient");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getHttpStatus());
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
    void testGetLogsShouldThrowException() throws DockerException, InterruptedException {
        when(defaultDockerClient.logs(anyString(), any(), any())).thenThrow(DockerException.class);
        BaseException thrown =
                assertThrows(BaseException.class,
                        () -> containersService.getLogs("some-container-id"),
                        "It was expected that listAll() thrown an exception, " +
                                "due to an error in DefaultDockerClient");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getHttpStatus());
    }

    @Test
    void testDelete() throws DockerException, InterruptedException {
        doNothing().when(defaultDockerClient).removeContainer(anyString());
        assertThatCode(() -> containersService.delete("some-container-id")).doesNotThrowAnyException();
    }

    @Test
    void testDeleteShouldThrowException() throws DockerException, InterruptedException {
        doThrow(DockerException.class).when(defaultDockerClient).removeContainer(anyString());
        BaseException thrown =
                assertThrows(BaseException.class,
                        () -> containersService.delete("some-container-id"),
                        "It was expected that listAll() thrown an exception, " +
                                "due to an error in DefaultDockerClient");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getHttpStatus());
    }
}
