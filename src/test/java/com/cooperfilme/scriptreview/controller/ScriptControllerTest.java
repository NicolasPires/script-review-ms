package com.cooperfilme.scriptreview.controller;

import com.cooperfilme.scriptreview.config.TestSecurityConfig;
import com.cooperfilme.scriptreview.dto.request.ScriptRequestDTO;
import com.cooperfilme.scriptreview.dto.request.UpdateStatusRequest;
import com.cooperfilme.scriptreview.dto.request.VoteRequestDTO;
import com.cooperfilme.scriptreview.dto.response.ScriptResponseDTO;
import com.cooperfilme.scriptreview.entity.User;
import com.cooperfilme.scriptreview.enums.ScriptStatus;
import com.cooperfilme.scriptreview.security.JwtAuthFilter;
import com.cooperfilme.scriptreview.service.ScriptService;
import com.cooperfilme.scriptreview.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = ScriptController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthFilter.class)
)
@Import(TestSecurityConfig.class)
class ScriptControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ScriptService scriptService;

    @MockBean
    private UserService userService;

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
        UpdateStatusRequest request = new UpdateStatusRequest();
        request.setNewStatus("IN_ANALYSIS");
        request.setJustification("Justificativa para atualização");

        mockMvc.perform(put("/scripts/1/status")
                        .with(user("user").roles("REVIEWER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(scriptService).updateStatus(1L, ScriptStatus.IN_ANALYSIS);
    }

    @Test
    void shouldVoteSuccessfullyAsApprover() throws Exception {
        VoteRequestDTO dto = new VoteRequestDTO();
        dto.setApproved(true);

        when(userService.getAuthenticatedUser()).thenReturn(new User());

        mockMvc.perform(post("/scripts/1/vote")
                        .with(user("approver").roles("APPROVER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());

        verify(scriptService).vote(eq(1L), any(User.class), eq(true));
    }

    @Test
    void shouldReturnScriptsByClientEmail() throws Exception {
        List<ScriptResponseDTO> responseList = List.of(
                new ScriptResponseDTO(1L, ScriptStatus.AWAITING_ANALYSIS),
                new ScriptResponseDTO(2L, ScriptStatus.APPROVED)
        );

        when(scriptService.getByClientEmail("client@example.com")).thenReturn(responseList);

        mockMvc.perform(get("/scripts/client/client@example.com")
                        .with(user("user").roles("ANALYST")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("AWAITING_ANALYSIS"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].status").value("APPROVED"));

        verify(scriptService).getByClientEmail("client@example.com");
    }
}
