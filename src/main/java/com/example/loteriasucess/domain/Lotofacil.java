package com.example.loteriasucess.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "lotofacil")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Lotofacil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String loteria;
    private int concurso;
    private String data;
    private String local;

    @ElementCollection
    @CollectionTable(name = "lotofacil_dezenas", joinColumns = @JoinColumn(name = "lotofacil_id"))
    @Column(name = "dezena")
    private List<String> dezenas;

    @ElementCollection
    @CollectionTable(name = "lotofacil_dezenas_ordem", joinColumns = @JoinColumn(name = "lotofacil_id"))
    @Column(name = "dezena_ordem")
    private List<String> dezenasOrdemSorteio;

    @ElementCollection
    @CollectionTable(name = "lotofacil_trevos", joinColumns = @JoinColumn(name = "lotofacil_id"))
    @Column(name = "trevo")
    private List<String> trevos;

    private String timeCoracao;
    private String mesSorte;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "lotofacil_id")
    private List<Premiacao> premiacoes;

    @ElementCollection
    @CollectionTable(name = "lotofacil_estados_premiados", joinColumns = @JoinColumn(name = "lotofacil_id"))
    private List<String> estadosPremiados;

    private Boolean acumulou;
    private int proximoConcurso;
    private String dataProximoConcurso;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "lotofacil_id")
    private List<LocalGanhadores> localGanhadores;

    private BigDecimal valorArrecadado;
    private BigDecimal valorAcumuladoConcurso_0_5;
    private BigDecimal valorAcumuladoConcursoEspecial;
    private BigDecimal valorAcumuladoProximoConcurso;
    private BigDecimal valorEstimadoProximoConcurso;
}
