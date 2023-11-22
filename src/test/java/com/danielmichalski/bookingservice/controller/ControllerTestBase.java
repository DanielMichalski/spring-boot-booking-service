package com.danielmichalski.bookingservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@AutoConfigureMockMvc
public class ControllerTestBase {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  protected <T> ResultActions post(
      T content,
      HttpStatus status,
      HttpHeaders headers,
      MultiValueMap<String, String> queryParams,
      String url,
      Object... pathArgs)
      throws Exception {
    MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(url, pathArgs)
        .queryParams(queryParams)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsBytes(content));

    if (headers != null) {
      requestBuilder
          .headers(headers);
    }

    return mockMvc.perform(requestBuilder)
        .andExpect(MockMvcResultMatchers.status().is(status.value()));
  }

  protected <T> ResultActions post(T content, HttpStatus status, String url, Object... pathArgs)
      throws Exception {
    return post(content, status, null, url, pathArgs);
  }

  protected <T> ResultActions post(T content, HttpStatus status, HttpHeaders headers, String url, Object... pathArgs)
      throws Exception {
    MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(url, pathArgs)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsBytes(content));

    if (headers != null) {
      requestBuilder
          .headers(headers);
    }

    return mockMvc.perform(requestBuilder)
        .andExpect(MockMvcResultMatchers.status().is(status.value()));
  }

  protected <T> ResultActions put(T content, HttpStatus status, String url, Object... pathArgs)
      throws Exception {
    return put(content, status, null, url, pathArgs);
  }

  protected <T> ResultActions put(T content, HttpStatus status, HttpHeaders headers, String url, Object... pathArgs)
      throws Exception {
    return put(content, status, headers, url, Map.of(), pathArgs);
  }

  protected <T> ResultActions put(T content,
                                  HttpStatus status,
                                  HttpHeaders headers,
                                  String url,
                                  Map<String, List<?>> queryParams,
                                  Object... pathArgs)
      throws Exception {
    MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(url, pathArgs)
        .queryParams(new LinkedMultiValueMap(queryParams))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsBytes(content));

    if (headers != null) {
      requestBuilder
          .headers(headers);
    }

    return mockMvc.perform(requestBuilder)
        .andExpect(MockMvcResultMatchers.status().is(status.value()));
  }

  protected <T> ResultActions patch(T content, HttpStatus status, String url, Object... pathArgs)
      throws Exception {
    return patch(content, status, null, url, pathArgs);
  }

  protected <T> ResultActions patch(T content, HttpStatus status, HttpHeaders headers, String url, Object... pathArgs)
      throws Exception {
    MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.patch(url, pathArgs)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsBytes(content));

    if (headers != null) {
      requestBuilder
          .headers(headers);
    }

    return mockMvc.perform(requestBuilder)
        .andExpect(MockMvcResultMatchers.status().is(status.value()));
  }

  protected <T> ResultActions postEmpty(HttpStatus status, String url, Object... pathArgs)
      throws Exception {
    return mockMvc.perform(MockMvcRequestBuilders.post(url, pathArgs)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().is(status.value()));
  }

  protected <T> T getAndExtract(Class<T> responseType, HttpStatus status, String url, Object... pathArgs)
      throws Exception {
    String content = get(status, url, pathArgs).andReturn().getResponse().getContentAsString();
    return objectMapper.readValue(content, responseType);
  }

  protected <T> ResultActions get(HttpStatus status, String url, Map<String, String> queryParams,
                                  Object... pathArgs) throws Exception {

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    queryParams.forEach(params::add);

    return mockMvc.perform(MockMvcRequestBuilders.get(url, pathArgs)
            .contentType(MediaType.APPLICATION_JSON)
            .queryParams(params))
        .andExpect(MockMvcResultMatchers.status().is(status.value()));
  }

  protected <T> ResultActions get(HttpStatus status, String url, Object... pathArgs) throws Exception {
    return mockMvc.perform(MockMvcRequestBuilders.get(url, pathArgs)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().is(status.value()));
  }

  protected ResultActions delete(HttpStatus status, String url, Object... pathArgs) throws Exception {
    return mockMvc.perform(MockMvcRequestBuilders.delete(url, pathArgs))
        .andExpect(MockMvcResultMatchers.status().is(status.value()));
  }

  protected <T> ResultActions delete(T content, HttpStatus status, String url, Object... pathArgs)
      throws Exception {
    MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete(url, pathArgs)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsBytes(content));

    return mockMvc.perform(requestBuilder)
        .andExpect(MockMvcResultMatchers.status().is(status.value()));
  }

  protected static String formatDateToJsonDate(OffsetDateTime date) {
    return date.atZoneSameInstant(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
  }

}
