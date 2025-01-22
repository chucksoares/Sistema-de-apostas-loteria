package com.example.loteriasucess.domain.dto;

import java.util.List;

public class LoteriasResponse {
    private List<String> jogos;

    private List<String> lotofacil;
    // Getters e Setters
    public List<String> getJogos() {
        return jogos;
    }

    public void setJogos(List<String> jogos) {
        this.jogos = jogos;
    }
    public List<String> getLotofacil() {
        return lotofacil;

    }
    public void setLotofacil(List<String> lotofacil) {

        this.lotofacil = lotofacil;

    }

}



