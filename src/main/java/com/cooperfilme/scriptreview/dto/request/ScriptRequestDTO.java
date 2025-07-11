package com.cooperfilme.scriptreview.dto.request;


import com.cooperfilme.scriptreview.entity.Client;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScriptRequestDTO {
    private String title;
    private String content;
    private Client client;
}