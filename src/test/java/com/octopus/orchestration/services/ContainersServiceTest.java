package com.octopus.orchestration.services;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
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
import com.spotify.docker.client.exceptions.ContainerNotFoundException;
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
    private Container container;

    @BeforeEach
    public void init() {
        when(dockerClient.getClient()).thenReturn(defaultDockerClient);
    }

    @Test
    void testListAll() throws DockerException, InterruptedException {
        when(defaultDockerClient.listContainers(any())).thenReturn(List.of(container, container));
        List<Container> response = containersService.listAllContainers();
        assertEquals(2, response.size());
    }

    @Test
    void testListAllShouldThrowException() throws DockerException, InterruptedException {
        when(defaultDockerClient.listContainers(any())).thenThrow(DockerException.class);
        BaseException thrown =
                assertThrows(BaseException.class,
                        () -> containersService.listAllContainers(),
                        "It was expected that listAllContainers() thrown an exception, " +
                                "due to an error in DefaultDockerClient");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getHttpStatus());
    }

    @Test
    void testListAllByStatusActive() throws DockerException, InterruptedException {
        when(defaultDockerClient.listContainers(ListContainersParam.withStatusRunning())).thenReturn(List.of(container, container));
        List<Container> response = containersService.listAllContainers("active");
        assertEquals(2, response.size());
    }

    @Test
    void testListAllByStatusActiveShouldThrowException() throws DockerException, InterruptedException {
        when(defaultDockerClient.listContainers(ListContainersParam.withStatusRunning())).thenThrow(DockerException.class);
        BaseException thrown =
                assertThrows(BaseException.class,
                        () -> containersService.listAllContainers("active"),
                        "It was expected that listAllContainers() thrown an exception, " +
                                "due to an error in DefaultDockerClient");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getHttpStatus());
    }

    @Test
    void testListAllByStatusInactive() throws DockerException, InterruptedException {
        when(defaultDockerClient.listContainers(ListContainersParam.withStatusExited())).thenReturn(List.of(container));
        List<Container> response = containersService.listAllContainers("inactive");
        assertEquals(1, response.size());
    }

    @Test
    void testListAllByStatusInactiveShouldThrowException() throws DockerException, InterruptedException {
        when(defaultDockerClient.listContainers(ListContainersParam.withStatusExited())).thenThrow(DockerException.class);
        BaseException thrown =
                assertThrows(BaseException.class,
                        () -> containersService.listAllContainers("inactive"),
                        "It was expected that listAllContainers() thrown an exception, " +
                                "due to an error in DefaultDockerClient");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getHttpStatus());
    }

    @Test
    void testListAllByWrongStatus() {
        DockerIllegalArgumentException thrown =
                assertThrows(DockerIllegalArgumentException.class,
                        () -> containersService.listAllContainers("wrong-status"),
                        "It was expected that listAllContainers() thrown an exception, " +
                                "due to an error in DefaultDockerClient");
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getHttpStatus());
    }

    @Test
    void testInspect() throws DockerException, InterruptedException {
        ContainerInfo containerInfo = mock(ContainerInfo.class);
        when(defaultDockerClient.inspectContainer(anyString())).thenReturn(containerInfo);
        ContainerInfo response = containersService.inspectContainer("some-container-id");
        assertNotNull(response);
    }

    @Test
    void testInspectShouldThrowDockerException() throws DockerException, InterruptedException {
        when(defaultDockerClient.inspectContainer(anyString())).thenThrow(DockerException.class);
        BaseException thrown =
                assertThrows(BaseException.class,
                        () -> containersService.inspectContainer("some-container-id"),
                        "It was expected that listAllContainers() thrown an exception, " +
                                "due to an error in DefaultDockerClient");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getHttpStatus());
    }

    @Test
    void testInspectShouldThrowContainerNotFoundException() throws DockerException, InterruptedException {
        when(defaultDockerClient.inspectContainer(anyString())).thenThrow(ContainerNotFoundException.class);
        BaseException thrown =
                assertThrows(BaseException.class,
                        () -> containersService.inspectContainer("some-container-id"),
                        "It was expected that listAllContainers() thrown an exception, " +
                                "due to a not found container");
        assertEquals(HttpStatus.NOT_FOUND, thrown.getHttpStatus());
    }

    @Test
    void testGetLogs() throws DockerException, InterruptedException {
        LogStream logStream = mock(LogStream.class);
        String logs = "some-container-log";
        when(defaultDockerClient.logs(anyString(), any(), any())).thenReturn(logStream);
        when(logStream.readFully()).thenReturn(logs);
        String response = containersService.getContainerLogs("some-container-id");
        assertEquals(logs, response);
    }

    @Test
    void testGetLogsShouldThrowDockerException() throws DockerException, InterruptedException {
        when(defaultDockerClient.logs(anyString(), any(), any())).thenThrow(DockerException.class);
        BaseException thrown =
                assertThrows(BaseException.class,
                        () -> containersService.getContainerLogs("some-container-id"),
                        "It was expected that listAllContainers() thrown an exception, " +
                                "due to an error in DefaultDockerClient");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getHttpStatus());
    }

    @Test
    void testGetLogsShouldThrowContainerNotFoundException() throws DockerException, InterruptedException {
        when(defaultDockerClient.logs(anyString(), any(), any())).thenThrow(ContainerNotFoundException.class);
        BaseException thrown =
                assertThrows(BaseException.class,
                        () -> containersService.getContainerLogs("some-container-id"),
                        "It was expected that listAllContainers() thrown an exception, " +
                                "due to a not found container");
        assertEquals(HttpStatus.NOT_FOUND, thrown.getHttpStatus());
    }

    @Test
    void testDelete() throws DockerException, InterruptedException {
        doNothing().when(defaultDockerClient).removeContainer(anyString());
        assertThatCode(() -> containersService.deleteContainer("some-container-id")).doesNotThrowAnyException();
    }

    @Test
    void testDeleteShouldThrowDockerException() throws DockerException, InterruptedException {
        doThrow(DockerException.class).when(defaultDockerClient).removeContainer(anyString());
        BaseException thrown =
                assertThrows(BaseException.class,
                        () -> containersService.deleteContainer("some-container-id"),
                        "It was expected that listAllContainers() thrown an exception, " +
                                "due to an error in DefaultDockerClient");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getHttpStatus());
    }

    @Test
    void testDeleteShouldThrowContainerNotFoundException() throws DockerException, InterruptedException {
        doThrow(ContainerNotFoundException.class).when(defaultDockerClient).removeContainer(anyString());
        BaseException thrown =
                assertThrows(BaseException.class,
                        () -> containersService.deleteContainer("some-container-id"),
                        "It was expected that listAllContainers() thrown an exception, " +
                                "due to a not found container");
        assertEquals(HttpStatus.NOT_FOUND, thrown.getHttpStatus());
    }

    @Test
    void testStart() throws DockerException, InterruptedException {
        doNothing().when(defaultDockerClient).startContainer(anyString());
        assertThatCode(() -> containersService.startContainers(buildContainerRequest())).doesNotThrowAnyException();
    }

    @Test
    void testStartShouldThrowDockerException() throws DockerException, InterruptedException {
        doThrow(DockerException.class).when(defaultDockerClient).startContainer(anyString());
        List<String> containersIds = buildContainerRequest();
        BaseException thrown =
                assertThrows(BaseException.class,
                        () -> containersService.startContainers(containersIds),
                        "It was expected that startContainers() thrown an exception, " +
                                "due to an error in DefaultDockerClient");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getHttpStatus());
    }

    @Test
    void testStartShouldThrowNotFoundException() throws DockerException, InterruptedException {
        doThrow(ContainerNotFoundException.class).when(defaultDockerClient).startContainer(anyString());
        List<String> containersIds = buildContainerRequest();
        BaseException thrown =
                assertThrows(BaseException.class,
                        () -> containersService.startContainers(containersIds),
                        "It was expected that startContainers() thrown an exception, " +
                                "due to a not found container");
        assertEquals(HttpStatus.NOT_FOUND, thrown.getHttpStatus());
    }

    @Test
    void testStop() throws DockerException, InterruptedException {
        doNothing().when(defaultDockerClient).stopContainer(anyString(), anyInt());
        assertThatCode(() -> containersService.stopContainers(buildContainerRequest())).doesNotThrowAnyException();
    }

    @Test
    void testStopShouldThrowDockerException() throws DockerException, InterruptedException {
        doThrow(DockerException.class).when(defaultDockerClient).stopContainer(anyString(), anyInt());
        List<String> containersIds = buildContainerRequest();
        BaseException thrown =
                assertThrows(BaseException.class,
                        () -> containersService.stopContainers(containersIds),
                        "It was expected that stopContainers() thrown an exception, " +
                                "due to an error in DefaultDockerClient");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getHttpStatus());
    }

    @Test
    void testStopShouldThrowContainerNotFoundException() throws DockerException, InterruptedException {
        doThrow(ContainerNotFoundException.class).when(defaultDockerClient).stopContainer(anyString(), anyInt());
        List<String> containersIds = buildContainerRequest();
        BaseException thrown =
                assertThrows(BaseException.class,
                        () -> containersService.stopContainers(containersIds),
                        "It was expected that stopContainers() thrown an exception, " +
                                "due to a not found container");
        assertEquals(HttpStatus.NOT_FOUND, thrown.getHttpStatus());
    }

    @Test
    void testKill() throws DockerException, InterruptedException {
        doNothing().when(defaultDockerClient).killContainer(anyString());
        assertThatCode(() -> containersService.killContainers(buildContainerRequest())).doesNotThrowAnyException();
    }

    @Test
    void testKillShouldThrowDockerException() throws DockerException, InterruptedException {
        doThrow(DockerException.class).when(defaultDockerClient).killContainer(anyString());
        List<String> containersIds = buildContainerRequest();
        BaseException thrown =
                assertThrows(BaseException.class,
                        () -> containersService.killContainers(containersIds),
                        "It was expected that killContainers() thrown an exception, " +
                                "due to an error in DefaultDockerClient");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getHttpStatus());
    }

    @Test
    void testKillShouldThrowContainerNotFoundException() throws DockerException, InterruptedException {
        doThrow(ContainerNotFoundException.class).when(defaultDockerClient).killContainer(anyString());
        List<String> containersIds = buildContainerRequest();
        BaseException thrown =
                assertThrows(BaseException.class,
                        () -> containersService.killContainers(containersIds),
                        "It was expected that killContainers() thrown an exception, " +
                                "due to a not found container");
        assertEquals(HttpStatus.NOT_FOUND, thrown.getHttpStatus());
    }

    @Test
    void testRestart() throws DockerException, InterruptedException {
        doNothing().when(defaultDockerClient).restartContainer(anyString(), anyInt());
        assertThatCode(() -> containersService.restartContainers(buildContainerRequest())).doesNotThrowAnyException();
    }

    @Test
    void testRestartShouldThrowDockerException() throws DockerException, InterruptedException {
        doThrow(DockerException.class).when(defaultDockerClient).restartContainer(anyString(), anyInt());
        List<String> containersIds = buildContainerRequest();
        BaseException thrown =
                assertThrows(BaseException.class,
                        () -> containersService.restartContainers(containersIds),
                        "It was expected that restartContainers() thrown an exception, " +
                                "due to an error in DefaultDockerClient");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getHttpStatus());
    }

    @Test
    void testRestartShouldThrowContainerNotFoundException() throws DockerException, InterruptedException {
        doThrow(ContainerNotFoundException.class).when(defaultDockerClient).restartContainer(anyString(), anyInt());
        List<String> containersIds = buildContainerRequest();
        BaseException thrown =
                assertThrows(BaseException.class,
                        () -> containersService.restartContainers(containersIds),
                        "It was expected that restartContainers() thrown an exception, " +
                                "due to a not found container");
        assertEquals(HttpStatus.NOT_FOUND, thrown.getHttpStatus());
    }

    private List<String> buildContainerRequest() {
        return new ArrayList<>(List.of("container-id-1", "container-id-2", "container-id-3", "container-id-4"));
    }
}
