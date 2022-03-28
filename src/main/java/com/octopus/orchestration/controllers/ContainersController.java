package com.octopus.orchestration.controllers;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.octopus.orchestration.services.ContainersService;
import com.spotify.docker.client.messages.Container;
import com.spotify.docker.client.messages.ContainerInfo;

@RestController
@RequestMapping("/containers")
public class ContainersController {
	private static final Logger LOGGER = Logger.getLogger(ContainersController.class);

    private final ContainersService containersService;

    @Autowired
    public ContainersController(ContainersService containersService) {
        this.containersService = containersService;
    }

    @GetMapping
    public ResponseEntity<List<Container>> listAllContainers()  {
        LOGGER.info("[GET] - listAllContainers");
        return ResponseEntity.ok(containersService.listAllContainers());
    }

    @GetMapping(params = "status")
    public ResponseEntity<List<Container>> listAllContainers(@RequestParam String status)  {
        LOGGER.info("[GET] - listAllContainers");
        return ResponseEntity.ok(containersService.listAllContainers(status));
    }

    @GetMapping("/inspect/{id}")
    public ResponseEntity<ContainerInfo> inspectContainer(@PathVariable String id) {
        LOGGER.info("[GET] - inspectContainer");
        return ResponseEntity.ok(containersService.inspectContainer(id));
    }

    @GetMapping("/logs/{id}")
    public ResponseEntity<String> getContainerLogs(@PathVariable String id) {
        LOGGER.info("[GET] - getContainerLogs");
        return ResponseEntity.ok(containersService.getContainerLogs(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContainer(@PathVariable String id) {
        LOGGER.info("[DELETE] - deleteContainer");
        containersService.deleteContainer(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/start")
    public ResponseEntity<Void> startContainers(@RequestBody List<String> containersIds) {
        LOGGER.info("[PUT] - startContainers");
        containersService.startContainers(containersIds);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/stop")
    public ResponseEntity<Void> stopContainers(@RequestBody List<String> containersIds) {
        LOGGER.info("[PUT] - stopContainers");
        containersService.stopContainers(containersIds);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/kill")
    public ResponseEntity<Void> killContainers(@RequestBody List<String> containersIds) {
        LOGGER.info("[PUT] - killContainers");
        containersService.killContainers(containersIds);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/restart")
    public ResponseEntity<Void> restartContainers(@RequestBody List<String> containersIds) {
        LOGGER.info("[PUT] - restartContainers");
        containersService.restartContainers(containersIds);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
