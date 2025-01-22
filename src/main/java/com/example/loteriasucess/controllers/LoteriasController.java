package com.example.loteriasucess.controllers;

import com.example.loteriasucess.domain.Lotofacil;
import com.example.loteriasucess.services.LoteriasService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/loterias")
public class LoteriasController {
    private final LoteriasService loteriasService;

    public LoteriasController(LoteriasService loteriasService) {
        this.loteriasService = loteriasService;
    }

    @GetMapping("/jogos")
    public List<String> getJogos() {
        return loteriasService.getJogos();
    }

    @GetMapping("/lotofacil")
        public List<Lotofacil> getLotofacil(){
            return loteriasService.getLotofacil();
        }


    @GetMapping("/lotofacil/predictions")
    public ResponseEntity<List<List<String>>> gerarPrevisoes() {
        List<List<String>> jogosSugeridos = loteriasService.gerarJogos();
        return ResponseEntity.ok(jogosSugeridos);
    }





}
