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
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
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
import com.octopus.orchestration.models.ContainerRequest;
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

	private ContainerRequest containerRequest;

	@BeforeEach
	void setup() throws URISyntaxException {
		uri = new URI("/containers");
		containerRequest = new ContainerRequest();
		containerRequest.setContainersIds(List.of("container-id-1", "container-id-2", "container-id-3", "container-id-4"));
	}

	@Test
	void testListAll() throws Exception {
		mockMvc.perform(get(uri)).andExpect(status().isOk());

		verify(containersService, times(1)).listAll();
	}

	@Test
	void testListAllByStatusActive() throws Exception {
		mockMvc.perform(get(uri + "?status=active")).andExpect(status().isOk());

		verify(containersService, times(1)).listAll("active");
	}

	@Test
	void testListAllByStatusInactive() throws Exception {
		mockMvc.perform(get(uri + "?status=inactive")).andExpect(status().isOk());

		verify(containersService, times(1)).listAll("inactive");
	}

	@Test
	void testListAllByWrongStatusShouldThrowException() throws Exception {
		when(containersService.listAll(anyString()))
				.thenThrow(new BaseException("some exception message", HttpStatus.BAD_REQUEST));

		mockMvc.perform(get(uri + "?status=wrong-status")).andExpect(status().isBadRequest());

		verify(containersService, times(1)).listAll("wrong-status");
	}

	@Test
	void testListAllShouldThrowException() throws Exception {
		when(containersService.listAll())
				.thenThrow(new BaseException("some exception message", HttpStatus.INTERNAL_SERVER_ERROR));

		mockMvc.perform(get(uri)).andExpect(status().isInternalServerError());
	}

	@Test
	void testInspect() throws Exception {
		mockMvc.perform(get(uri + "/inspect/some-container-id")).andExpect(status().isOk());

		verify(containersService, times(1)).inspect("some-container-id");
	}

	@Test
	void testInspectShouldThrowException() throws Exception {
		when(containersService.inspect(anyString()))
				.thenThrow(new BaseException("some exception message", HttpStatus.INTERNAL_SERVER_ERROR));

		mockMvc.perform(get(uri + "/inspect/some-container-id")).andExpect(status().isInternalServerError());
	}

	@Test
	void testGetLogs() throws Exception {
		mockMvc.perform(get(uri + "/logs/some-container-id")).andExpect(status().isOk());

		verify(containersService, times(1)).getLogs("some-container-id");
	}

	@Test
	void testGetLogsShouldThrowException() throws Exception {
		when(containersService.getLogs(anyString()))
				.thenThrow(new BaseException("some exception message", HttpStatus.INTERNAL_SERVER_ERROR));

		mockMvc.perform(get(uri + "/logs/some-container-id")).andExpect(status().isInternalServerError());
	}

	@Test
	void testDelete() throws Exception {
		doNothing().when(containersService).delete(anyString());

		mockMvc.perform(delete(uri + "/some-container-id")).andExpect(status().isNoContent());

		verify(containersService, times(1)).delete("some-container-id");
	}

	@Test
	void testDeleteShouldThrowException() throws Exception {
		doThrow(new BaseException("some exception message", HttpStatus.INTERNAL_SERVER_ERROR))
				.when(containersService).delete(anyString());

		mockMvc.perform(delete(uri + "/some-container-id")).andExpect(status().isInternalServerError());

		verify(containersService, times(1)).delete("some-container-id");
	}

	@Test
	void testStart() throws Exception {
		doNothing().when(containersService).start(any());

		mockMvc.perform(put(uri + "/start")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(containerRequest)))
				.andExpect(status().isNoContent());

		verify(containersService, times(1)).start(containerRequest.getContainersIds());
	}

	@Test
	void testStartShouldThrowException() throws Exception {
		doThrow(new BaseException("some exception message", HttpStatus.INTERNAL_SERVER_ERROR))
				.when(containersService).start(any());

		mockMvc.perform(put(uri + "/start")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(containerRequest)))
				.andExpect(status().isInternalServerError());

		verify(containersService, times(1)).start(containerRequest.getContainersIds());
	}

	@Test
	void testStop() throws Exception {
		doNothing().when(containersService).stop(any());

		mockMvc.perform(put(uri + "/stop")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(containerRequest)))
				.andExpect(status().isNoContent());

		verify(containersService, times(1)).stop(containerRequest.getContainersIds());
	}

	@Test
	void testStopShouldThrowException() throws Exception {
		doThrow(new BaseException("some exception message", HttpStatus.INTERNAL_SERVER_ERROR))
				.when(containersService).stop(any());

		mockMvc.perform(put(uri + "/stop")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(containerRequest)))
				.andExpect(status().isInternalServerError());

		verify(containersService, times(1)).stop(containerRequest.getContainersIds());
	}

	@Test
	void testKill() throws Exception {
		doNothing().when(containersService).kill(any());

		mockMvc.perform(put(uri + "/kill")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(containerRequest)))
				.andExpect(status().isNoContent());

		verify(containersService, times(1)).kill(containerRequest.getContainersIds());
	}

	@Test
	void testKillShouldThrowException() throws Exception {
		doThrow(new BaseException("some exception message", HttpStatus.INTERNAL_SERVER_ERROR))
				.when(containersService).kill(any());

		mockMvc.perform(put(uri + "/kill")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(containerRequest)))
				.andExpect(status().isInternalServerError());

		verify(containersService, times(1)).kill(containerRequest.getContainersIds());
	}

	@Test
	void testRestart() throws Exception {
		doNothing().when(containersService).restart(any());

		mockMvc.perform(put(uri + "/restart")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(containerRequest)))
				.andExpect(status().isNoContent());

		verify(containersService, times(1)).restart(containerRequest.getContainersIds());
	}

	@Test
	void testRestartShouldThrowException() throws Exception {
		doThrow(new BaseException("some exception message", HttpStatus.INTERNAL_SERVER_ERROR))
				.when(containersService).restart(any());

		mockMvc.perform(put(uri + "/restart")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(containerRequest)))
				.andExpect(status().isInternalServerError());

		verify(containersService, times(1)).restart(containerRequest.getContainersIds());
	}
}
