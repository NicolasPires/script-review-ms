package com.cooperfilme.scriptreview.controller;

import com.cooperfilme.scriptreview.dto.request.ScriptRequestDTO;
import com.cooperfilme.scriptreview.dto.request.VoteRequestDTO;
import com.cooperfilme.scriptreview.dto.response.ScriptResponseDTO;
import com.cooperfilme.scriptreview.enums.ScriptStatus;
import com.cooperfilme.scriptreview.service.ScriptService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ScriptController.class)
class ScriptControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ScriptService scriptService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSubmitScriptSuccessfully() throws Exception {
        ScriptRequestDTO request = new ScriptRequestDTO();
        request.setTitle("A Great Journey");
        request.setContent("Content...");

        ScriptResponseDTO response = new ScriptResponseDTO(1L, ScriptStatus.AWAITING_ANALYSIS);

        when(scriptService.submitScript(any())).thenReturn(response);

        mockMvc.perform(post("/scripts")
                        .with(user("user").roles("ANALYST"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("AWAITING_ANALYSIS"));
    }

    @Test
    void shouldReturnScriptStatus() throws Exception {
        when(scriptService.getScriptStatus(1L)).thenReturn(Optional.of(ScriptStatus.IN_ANALYSIS));

        mockMvc.perform(get("/scripts/1/status")
                        .with(user("user").roles("ANALYST")))
                .andExpect(status().isOk())
                .andExpect(content().json("\"IN_ANALYSIS\""));

    }

    @Test
    void shouldReturn404WhenScriptNotFound() throws Exception {
        when(scriptService.getScriptStatus(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/scripts/1/status")
                        .with(user("user").roles("ANALYST")))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateScriptStatus() throws Exception {
        mockMvc.perform(put("/scripts/1/status")
                        .with(user("user").roles("REVIEWER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("status", "IN_ANALYSIS"))))
                .andExpect(status().isNoContent());

        verify(scriptService).updateStatus(1L, ScriptStatus.IN_ANALYSIS);
    }

    @Test
    void shouldVoteSuccessfullyAsApprover() throws Exception {
        VoteRequestDTO dto = new VoteRequestDTO();
        dto.setApproved(true);

        mockMvc.perform(post("/scripts/1/vote")
                        .with(user("approver").roles("APPROVER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());

        verify(scriptService).vote(eq(1L), any(), eq(true));
    }
}
