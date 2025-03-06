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

//    public List<List<String>> gerarJogos() {
//        List<Lotofacil> ultimosJogos = lotofacilRepository
//                .findTop10ByOrderByConcursoDesc();
//
//        Map<String, Integer> frequencias = new HashMap<>();
//        for (Lotofacil jogo : ultimosJogos) {
//            for (String dezena : jogo.getDezenas()) {
//                frequencias.put(dezena, frequencias.getOrDefault(dezena, 0) + 1);
//            }
//        }
//
//        List<String> dezenasOrdenadas = frequencias.entrySet().stream()
//                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
//                .map(Map.Entry::getKey)
//                .collect(Collectors.toList());
//
//        List<String> fixos = dezenasOrdenadas.subList(0, 5);
//
//        List<List<String>> novosJogos = new ArrayList<>();
//        Random random = new Random();
//        for (int i = 0; i < 5; i++) {
//            List<String> jogo = new ArrayList<>(fixos);
//
//
//            List<String> restantes = new ArrayList<>(frequencias.keySet());
//            restantes.removeAll(fixos);
//            Collections.shuffle(restantes, random);
//            jogo.addAll(restantes.subList(0, 10));
//
//            Collections.sort(jogo);
//            novosJogos.add(jogo);
//        }
//
//        return novosJogos;
//    }

    public List<List<String>> gerarJogos() {
        List<Lotofacil> ultimosJogos = lotofacilRepository.findTop50ByOrderByConcursoDesc();
        Map<String, Integer> frequencias = new HashMap<>();
        for (Lotofacil jogo : ultimosJogos) {
            for (String dezena : jogo.getDezenas()) {
                frequencias.put(dezena, frequencias.getOrDefault(dezena, 0) + 1);
            }
        }

        List<List<String>> novosJogos = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < 5; i++) {
            Set<String> jogo = new HashSet<>(); // Usar Set para garantir unicidade
            List<String> todasDezenas = new ArrayList<>();

            // Preencher lista ponderada
            for (String dezena : frequencias.keySet()) {
                for (int j = 0; j < frequencias.get(dezena); j++) {
                    todasDezenas.add(dezena);
                }
            }
            Collections.shuffle(todasDezenas);

            // Adicionar números até atingir 15 únicos
            int index = 0;
            while (jogo.size() < 15 && index < todasDezenas.size()) {
                jogo.add(todasDezenas.get(index));
                index++;
            }

            // Se não atingir 15 (caso raro), completar com números não usados
            while (jogo.size() < 15) {
                String num;
                do {
                    num = String.format("%02d", random.nextInt(25) + 1);
                } while (jogo.contains(num));
                jogo.add(num);
            }

            // Converter para lista e ajustar paridade
            List<String> jogoLista = new ArrayList<>(jogo);
            int impares = (int) jogoLista.stream().mapToInt(Integer::parseInt).filter(n -> n % 2 != 0).count();
            while (impares < 7 || impares > 9) {
                if (impares < 7) {
                    String par = jogoLista.stream().filter(n -> Integer.parseInt(n) % 2 == 0).findFirst().get();
                    jogoLista.remove(par);
                    jogoLista.add(getRandomImparNaoUsado(jogoLista, random));
                } else {
                    String impar = jogoLista.stream().filter(n -> Integer.parseInt(n) % 2 != 0).findFirst().get();
                    jogoLista.remove(impar);
                    jogoLista.add(getRandomParNaoUsado(jogoLista, random));
                }
                impares = (int) jogoLista.stream().mapToInt(Integer::parseInt).filter(n -> n % 2 != 0).count();
            }

            Collections.sort(jogoLista);
            novosJogos.add(jogoLista);
        }

        return novosJogos;
    }

    // Métodos auxiliares permanecem iguais
    private String getRandomImparNaoUsado(List<String> jogo, Random random) {
        List<String> impares = new ArrayList<>();
        for (int i = 1; i <= 25; i += 2) {
            String num = String.format("%02d", i);
            if (!jogo.contains(num)) {
                impares.add(num);
            }
        }
        return impares.get(random.nextInt(impares.size()));
    }

    private String getRandomParNaoUsado(List<String> jogo, Random random) {
        List<String> pares = new ArrayList<>();
        for (int i = 2; i <= 25; i += 2) {
            String num = String.format("%02d", i);
            if (!jogo.contains(num)) {
                pares.add(num);
            }
        }
        return pares.get(random.nextInt(pares.size()));
    }

}
