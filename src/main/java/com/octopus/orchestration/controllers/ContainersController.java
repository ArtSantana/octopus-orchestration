package com.octopus.orchestration.controllers;

import com.octopus.orchestration.services.ContainersService;
import com.spotify.docker.client.messages.Container;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController public class ContainersController {
	private static final Logger LOGGER = Logger.getLogger(ContainersController.class);

	@Autowired private ContainersService containersService;

	@GetMapping public List<Container> listAllContainers() {
		LOGGER.info("[GET] - listAllContainers");
		return containersService.listAll();
	}
}
