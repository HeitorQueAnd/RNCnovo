package org.example.rnc.controller;

import org.example.rnc.model.Registro;
import org.example.rnc.service.RegistroService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;


import java.util.List;

@Controller
public class RegistroController {

    private final RegistroService registroService;

    public RegistroController(RegistroService registroService) {
        this.registroService = registroService;
    }


    

    /**
     * Endpoint para servir a página inicial (HTML).
     */
    @GetMapping("/")
    public String home() {
        return "index"; // Nome do arquivo HTML em src/main/resources/templates (sem extensão)
    }

    @GetMapping("/api/filtrar")
    @ResponseBody
    public List<Registro> filtrarRegistros(
            @RequestParam(required = false) String ufv,
            @RequestParam(required = false) String grupoOcorrencia,
            @RequestParam(required = false) String id) {
        return registroService.filtrarRegistros(ufv, grupoOcorrencia, id);
    }

    @GetMapping("/api/opcoes")
    @ResponseBody
    public List<String> getOpcoes(@RequestParam String campo) {
        return registroService.getOpcoesUnicas(campo);
    }


    @GetMapping("/api/relatorio")
    public ResponseEntity<byte[]> gerarRelatorioPdf(
            @RequestParam(required = false) String ufv,
            @RequestParam(required = false) String grupoOcorrencia,
            @RequestParam(required = false) String id
    ) {
        try {
            // Obter registros filtrados
            List<Registro> registros = registroService.filtrarRegistros(ufv, grupoOcorrencia, id);

            // Gerar PDF
            byte[] pdf = registroService.gerarRelatorioPdf(registros);

            // Configurar cabeçalhos para download
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=relatorio_desvios.pdf");

            return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/api/registros/{id}")
    @ResponseBody
    public ResponseEntity<String> atualizarRegistro(
            @PathVariable int id,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String dataAcordada) {
        try {
            registroService.atualizarRegistro(id, status, dataAcordada);
            return ResponseEntity.ok("Registro atualizado com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/api/registros/{id}")
    @ResponseBody
    public Registro getRegistroById(@PathVariable int id) {
        Registro registro = registroService.getRegistroById(id);
        if (registro == null) {
            throw new IllegalArgumentException("Registro não encontrado para o ID: " + id);
        }
        return registro;
    }


    /**
     * Endpoint para obter registros por status (API REST).
     */
    @GetMapping("/api/registros")
    @ResponseBody
    public List<Registro> getRegistros(@RequestParam String status) {
        // Adicionar "nao_conformidade" como status válido
        if (!status.equalsIgnoreCase("vencidos") &&
                !status.equalsIgnoreCase("recebidos") &&
                !status.equalsIgnoreCase("em_tratamento") &&
                !status.equalsIgnoreCase("finalizado") &&
                !status.equalsIgnoreCase("nao_conformidade")) { // Adicionado aqui
            throw new IllegalArgumentException("Status inválido: " + status);
        }

        return registroService.getRegistrosPorStatus(status);
    }
}