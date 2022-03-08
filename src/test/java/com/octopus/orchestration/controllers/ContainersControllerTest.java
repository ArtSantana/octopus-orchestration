package com.octopus.orchestration.controllers;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import com.octopus.orchestration.exceptions.BaseException;
import com.octopus.orchestration.services.ContainersService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class ContainersControllerTest {
	@MockBean
	private ContainersService containersService;

	@Autowired
	private MockMvc mockMvc;

	private static URI uri;

	@BeforeAll
	static void setup() throws URISyntaxException {
		uri = new URI("/containers");
	}

	@Test
	void testListAll() throws Exception {
		mockMvc.perform(get(uri))
				.andDo(print())
				.andExpect(status().isOk());

		Mockito.verify(containersService, times(1)).listAll();
	}

	@Test
	void testListAllByStatusActive() throws Exception {
		mockMvc.perform(get(uri + "?status=active"))
				.andDo(print())
				.andExpect(status().isOk());

		Mockito.verify(containersService, times(1)).listAll("active");
	}

	@Test
	void testListAllByStatusInactive() throws Exception {
		mockMvc.perform(get(uri + "?status=inactive"))
				.andDo(print())
				.andExpect(status().isOk());

		Mockito.verify(containersService, times(1)).listAll("inactive");
	}

	@Test
	void testListAllByWrongStatusShouldThrowException() throws Exception {
		when(containersService.listAll(anyString())).thenThrow(new BaseException("some exception message", HttpStatus.BAD_REQUEST));
		mockMvc.perform(get(uri + "?status=wrong-status"))
				.andDo(print())
				.andExpect(status().isBadRequest());

		Mockito.verify(containersService, times(1)).listAll("wrong-status");
	}

	@Test
	void testListAllShouldThrowException() throws Exception {
		when(containersService.listAll()).thenThrow(new BaseException("some exception message", HttpStatus.INTERNAL_SERVER_ERROR));
		mockMvc.perform(get(uri))
				.andDo(print())
				.andExpect(status().isInternalServerError());
	}

	@Test
	void testInspect() throws Exception {
		mockMvc.perform(get(uri + "/inspect/some-container-id"))
				.andDo(print())
				.andExpect(status().isOk());

		Mockito.verify(containersService, times(1)).inspect("some-container-id");
	}

	@Test
	void testInspectShouldThrowException() throws Exception {
		when(containersService.inspect(anyString())).thenThrow(new BaseException("some exception message", HttpStatus.INTERNAL_SERVER_ERROR));
		mockMvc.perform(get(uri + "/inspect/some-container-id"))
				.andDo(print())
				.andExpect(status().isInternalServerError());
	}

	@Test
	void testGetLogs() throws Exception {
		mockMvc.perform(get(uri + "/logs/some-container-id"))
				.andDo(print())
				.andExpect(status().isOk());

		Mockito.verify(containersService, times(1)).getLogs("some-container-id");
	}

	@Test
	void testGetLogsShouldThrowException() throws Exception {
		when(containersService.getLogs(anyString())).thenThrow(new BaseException("some exception message", HttpStatus.INTERNAL_SERVER_ERROR));
		mockMvc.perform(get(uri + "/logs/some-container-id"))
				.andDo(print())
				.andExpect(status().isInternalServerError());
	}

	@Test
	void testDelete() throws Exception {
		doNothing().when(containersService).delete(anyString());

		mockMvc.perform(delete(uri + "/some-container-id"))
				.andDo(print())
				.andExpect(status().isNoContent())
				.andReturn();

		Mockito.verify(containersService, times(1)).delete("some-container-id");
	}

	@Test
	void testDeleteShouldThrowException() throws Exception {
		doThrow(new BaseException("some exception message", HttpStatus.INTERNAL_SERVER_ERROR)).when(containersService).delete(anyString());
		mockMvc.perform(delete(uri + "/some-container-id"))
				.andDo(print())
				.andExpect(status().isInternalServerError());
		Mockito.verify(containersService, times(1)).delete("some-container-id");
	}
}
