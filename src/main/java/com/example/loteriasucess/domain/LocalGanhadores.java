package com.example.loteriasucess.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "local_ganhadores")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LocalGanhadores {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int ganhadores;
    private String municipio;
    private String nomeFatansiaUL;
    private String serie;
    private int posicao;
    private String uf;
}
