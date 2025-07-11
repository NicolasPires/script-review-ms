package com.cooperfilme.scriptreview.dto.response;

import com.cooperfilme.scriptreview.enums.ScriptStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScriptResponseDTO {
    private Long id;
    private ScriptStatus status;
}