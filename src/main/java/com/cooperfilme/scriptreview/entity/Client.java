package com.cooperfilme.scriptreview.entity;

import jakarta.persistence.*;
import lombok.*;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {

    //@Embeddable usado pois o Client é apenas um subdocumento do Script.
    private String name;
    private String email;
    private String phone;
}
