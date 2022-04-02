package com.octopus.orchestration.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.octopus.orchestration.exceptions.BaseException;
import com.octopus.orchestration.services.ContainersService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class ContainersControllerTest {
	@MockBean
	private ContainersService containersService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private static URI uri;

	private static List<String> containersIds;

	@BeforeAll
	static void setup() throws URISyntaxException {
		uri = new URI("/containers");
		containersIds = new ArrayList<>(List.of("container-id-1", "container-id-2", "container-id-3", "container-id-4"));
	}

	@Test
	void testListAll() throws Exception {
		mockMvc.perform(get(uri)).andExpect(status().isOk());

		verify(containersService, times(1)).listAllContainers();
	}

	@Test
	void testListAllByStatusActive() throws Exception {
		mockMvc.perform(get(uri + "?status=active")).andExpect(status().isOk());

		verify(containersService, times(1)).listAllContainers("active");
	}

	@Test
	void testListAllByStatusInactive() throws Exception {
		mockMvc.perform(get(uri + "?status=inactive")).andExpect(status().isOk());

		verify(containersService, times(1)).listAllContainers("inactive");
	}

	@Test
	void testListAllByWrongStatusShouldThrowException() throws Exception {
		when(containersService.listAllContainers(anyString()))
				.thenThrow(new BaseException("some exception message", HttpStatus.BAD_REQUEST));

		mockMvc.perform(get(uri + "?status=wrong-status")).andExpect(status().isBadRequest());

		verify(containersService, times(1)).listAllContainers("wrong-status");
	}

	@Test
	void testListAllShouldThrowException() throws Exception {
		when(containersService.listAllContainers())
				.thenThrow(new BaseException("some exception message", HttpStatus.INTERNAL_SERVER_ERROR));

		mockMvc.perform(get(uri)).andExpect(status().isInternalServerError());
	}

	@Test
	void testInspect() throws Exception {
		mockMvc.perform(get(uri + "/inspect/some-container-id")).andExpect(status().isOk());

		verify(containersService, times(1)).inspectContainer("some-container-id");
	}

	@Test
	void testInspectShouldThrowException() throws Exception {
		when(containersService.inspectContainer(anyString()))
				.thenThrow(new BaseException("some exception message", HttpStatus.INTERNAL_SERVER_ERROR));

		mockMvc.perform(get(uri + "/inspect/some-container-id")).andExpect(status().isInternalServerError());
	}

	@Test
	void testGetLogs() throws Exception {
		mockMvc.perform(get(uri + "/logs/some-container-id")).andExpect(status().isOk());

		verify(containersService, times(1)).getContainerLogs("some-container-id");
	}

	@Test
	void testGetLogsShouldThrowException() throws Exception {
		when(containersService.getContainerLogs(anyString()))
				.thenThrow(new BaseException("some exception message", HttpStatus.INTERNAL_SERVER_ERROR));

		mockMvc.perform(get(uri + "/logs/some-container-id")).andExpect(status().isInternalServerError());
	}

	@Test
	void testDelete() throws Exception {
		doNothing().when(containersService).deleteContainer(anyString());

		mockMvc.perform(delete(uri + "/some-container-id")).andExpect(status().isNoContent());

		verify(containersService, times(1)).deleteContainer("some-container-id");
	}

	@Test
	void testDeleteShouldThrowException() throws Exception {
		doThrow(new BaseException("some exception message", HttpStatus.INTERNAL_SERVER_ERROR))
				.when(containersService).deleteContainer(anyString());

		mockMvc.perform(delete(uri + "/some-container-id")).andExpect(status().isInternalServerError());

		verify(containersService, times(1)).deleteContainer("some-container-id");
	}

	@Test
	void testStart() throws Exception {
		doNothing().when(containersService).startContainers(any());

		mockMvc.perform(put(uri + "/start")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(containersIds)))
				.andExpect(status().isNoContent());

		verify(containersService, times(1)).startContainers(containersIds);
	}

	@Test
	void testStartShouldThrowException() throws Exception {
		doThrow(new BaseException("some exception message", HttpStatus.INTERNAL_SERVER_ERROR))
				.when(containersService).startContainers(any());

		mockMvc.perform(put(uri + "/start")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(containersIds)))
				.andExpect(status().isInternalServerError());

		verify(containersService, times(1)).startContainers(containersIds);
	}

	@Test
	void testStop() throws Exception {
		doNothing().when(containersService).stopContainers(any());

		mockMvc.perform(put(uri + "/stop")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(containersIds)))
				.andExpect(status().isNoContent());

		verify(containersService, times(1)).stopContainers(containersIds);
	}

	@Test
	void testStopShouldThrowException() throws Exception {
		doThrow(new BaseException("some exception message", HttpStatus.INTERNAL_SERVER_ERROR))
				.when(containersService).stopContainers(any());

		mockMvc.perform(put(uri + "/stop")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(containersIds)))
				.andExpect(status().isInternalServerError());

		verify(containersService, times(1)).stopContainers(containersIds);
	}

	@Test
	void testKill() throws Exception {
		doNothing().when(containersService).killContainers(any());

		mockMvc.perform(put(uri + "/kill")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(containersIds)))
				.andExpect(status().isNoContent());

		verify(containersService, times(1)).killContainers(containersIds);
	}

	@Test
	void testKillShouldThrowException() throws Exception {
		doThrow(new BaseException("some exception message", HttpStatus.INTERNAL_SERVER_ERROR))
				.when(containersService).killContainers(any());

		mockMvc.perform(put(uri + "/kill")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(containersIds)))
				.andExpect(status().isInternalServerError());

		verify(containersService, times(1)).killContainers(containersIds);
	}

	@Test
	void testRestart() throws Exception {
		doNothing().when(containersService).restartContainers(any());

		mockMvc.perform(put(uri + "/restart")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(containersIds)))
				.andExpect(status().isNoContent());

		verify(containersService, times(1)).restartContainers(containersIds);
	}

	@Test
	void testRestartShouldThrowException() throws Exception {
		doThrow(new BaseException("some exception message", HttpStatus.INTERNAL_SERVER_ERROR))
				.when(containersService).restartContainers(any());

		mockMvc.perform(put(uri + "/restart")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(containersIds)))
				.andExpect(status().isInternalServerError());

		verify(containersService, times(1)).restartContainers(containersIds);
	}
}
