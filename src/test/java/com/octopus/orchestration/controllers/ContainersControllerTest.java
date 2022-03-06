package com.octopus.orchestration.controllers;

import com.octopus.orchestration.services.ContainersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class ContainersControllerTest {
	@InjectMocks
	ContainersController containersController = new ContainersController();

	@Mock
	ContainersService containersService;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	void listAll() throws Exception {
		containersController.listAllContainers();

		Mockito.verify(containersService, Mockito.times(1)).listAll();
	}
}
