package com.octopus.orchestration.controllers;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

	@Autowired
    private ContainersService containersService;

    @GetMapping
    public ResponseEntity<List<Container>> listAll()  {
        LOGGER.info("[GET] - listAll");
        return ResponseEntity.ok(containersService.listAll());
    }

    @GetMapping(params = "status")
    public ResponseEntity<List<Container>> listAll(@RequestParam String status)  {
        LOGGER.info("[GET] - listAll");
        return ResponseEntity.ok(containersService.listAll(status));
    }

    @GetMapping("/inspect/{id}")
    public ResponseEntity<ContainerInfo> inspect(@PathVariable String id) {
        LOGGER.info("[GET] - inspect");
        return ResponseEntity.ok(containersService.inspect(id));
    }

    @GetMapping("/logs/{id}")
    public ResponseEntity<String> getLogs(@PathVariable String id) {
        LOGGER.info("[GET] - getLogs");
        return ResponseEntity.ok(containersService.getLogs(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        LOGGER.info("[DELETE] - delete");
        containersService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
