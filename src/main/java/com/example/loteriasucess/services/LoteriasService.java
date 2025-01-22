package com.example.loteriasucess.services;

import com.example.loteriasucess.domain.LocalGanhadores;
import com.example.loteriasucess.domain.Lotofacil;
import com.example.loteriasucess.domain.Premiacao;
import com.example.loteriasucess.repositories.LotofacilRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LoteriasService {
    private final RestTemplate restTemplate;

    private final LotofacilRepository lotofacilRepository;

    public LoteriasService(RestTemplate restTemplate, LotofacilRepository lotofacilRepository) {
        this.restTemplate = restTemplate;
        this.lotofacilRepository = lotofacilRepository;
    }

    public List<String> getJogos() {
        String url = "https://loteriascaixa-api.herokuapp.com/api";
        try {
            return restTemplate.getForObject(url, List.class);
        } catch (HttpClientErrorException e) {
            System.err.println("Erro na requisição: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            throw e;
        }
    }

    public List<Lotofacil> getLotofacil() {
        String url = "https://loteriascaixa-api.herokuapp.com/api/lotofacil";
        try {
            List<LinkedHashMap<String, Object>> apiResult = restTemplate.getForObject(url, List.class);
            List<Lotofacil> lotofacilList = apiResult.stream().map(this::mapToLotofacil).toList();
            lotofacilRepository.saveAll(lotofacilList);

            return lotofacilList;
        } catch (HttpClientErrorException e) {
            System.err.println("Erro na requisição: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            throw e;
        }
    }

    private Lotofacil mapToLotofacil(LinkedHashMap<String, Object> map) {
        Lotofacil lotofacil = new Lotofacil();

        lotofacil.setLoteria((String) map.get("loteria"));
        lotofacil.setConcurso((Integer) map.get("concurso"));
        lotofacil.setData((String) map.get("data"));
        lotofacil.setLocal((String) map.get("local"));
        lotofacil.setDezenas((List<String>) map.get("dezenas"));
        lotofacil.setDezenasOrdemSorteio((List<String>) map.get("dezenasOrdemSorteio"));
        lotofacil.setTrevos((List<String>) map.get("trevos"));
        lotofacil.setTimeCoracao((String) map.get("timeCoracao"));
        lotofacil.setMesSorte((String) map.get("mesSorte"));

        // Map premiacoes
        List<LinkedHashMap<String, Object>> premiacoes = (List<LinkedHashMap<String, Object>>) map.get("premiacoes");
        if (premiacoes != null) {
            lotofacil.setPremiacoes(premiacoes.stream().map(p -> {
                Premiacao premiacao = new Premiacao();
                premiacao.setDescricao((String) p.get("descricao"));
                premiacao.setFaixa((Integer) p.get("faixa"));
                premiacao.setGanhadores((Integer) p.get("ganhadores"));
                premiacao.setValorPremio(new BigDecimal(p.get("valorPremio").toString()));
                return premiacao;
            }).toList());
        }

        // Map localGanhadores
        List<LinkedHashMap<String, Object>> locais = (List<LinkedHashMap<String, Object>>) map.get("localGanhadores");
        if (locais != null) {
            lotofacil.setLocalGanhadores(locais.stream().map(l -> {
                LocalGanhadores local = new LocalGanhadores();
                local.setGanhadores((Integer) l.get("ganhadores"));
                local.setMunicipio((String) l.get("municipio"));
                local.setNomeFatansiaUL((String) l.get("nomeFatansiaUL"));
                local.setSerie((String) l.get("serie"));
                local.setPosicao((Integer) l.get("posicao"));
                local.setUf((String) l.get("uf"));
                return local;
            }).toList());
        }

        lotofacil.setAcumulou((Boolean) map.get("acumulou"));
        lotofacil.setProximoConcurso((Integer) map.get("proximoConcurso"));
        lotofacil.setDataProximoConcurso((String) map.get("dataProximoConcurso"));

        lotofacil.setValorArrecadado(new BigDecimal(map.get("valorArrecadado").toString()));
        lotofacil.setValorAcumuladoConcurso_0_5(new BigDecimal(map.get("valorAcumuladoConcurso_0_5").toString()));
        lotofacil.setValorAcumuladoConcursoEspecial(new BigDecimal(map.get("valorAcumuladoConcursoEspecial").toString()));
        lotofacil.setValorAcumuladoProximoConcurso(new BigDecimal(map.get("valorAcumuladoProximoConcurso").toString()));
        lotofacil.setValorEstimadoProximoConcurso(new BigDecimal(map.get("valorEstimadoProximoConcurso").toString()));

        return lotofacil;
    }

    public List<List<String>> gerarJogos() {
        List<Lotofacil> ultimosJogos = lotofacilRepository
                .findTop10ByOrderByConcursoDesc();

        Map<String, Integer> frequencias = new HashMap<>();
        for (Lotofacil jogo : ultimosJogos) {
            for (String dezena : jogo.getDezenas()) {
                frequencias.put(dezena, frequencias.getOrDefault(dezena, 0) + 1);
            }
        }

        List<String> dezenasOrdenadas = frequencias.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        List<String> fixos = dezenasOrdenadas.subList(0, 5);

        List<List<String>> novosJogos = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            List<String> jogo = new ArrayList<>(fixos);


            List<String> restantes = new ArrayList<>(frequencias.keySet());
            restantes.removeAll(fixos);
            Collections.shuffle(restantes, random);
            jogo.addAll(restantes.subList(0, 10));

            Collections.sort(jogo);
            novosJogos.add(jogo);
        }

        return novosJogos;
    }


}
