package org.example.rnc.service;

import org.example.rnc.model.Registro;
import org.example.rnc.repository.RegistroRepository;
import org.springframework.stereotype.Service;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.example.rnc.model.Registro;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RegistroService {

    private final RegistroRepository registroRepository;

    public RegistroService(RegistroRepository registroRepository) {
        this.registroRepository = registroRepository;
    }

    public List<String> getOpcoesUnicas(String campo) {
        List<Registro> registros = registroRepository.carregarRegistrosDoExcel("C:/Users/hecan/novoArquivo.xlsx");

        switch (campo.toLowerCase()) {
            case "ufv":
                return registros.stream()
                        .map(Registro::getUfv)
                        .filter(ufv -> ufv != null && !ufv.isEmpty())
                        .distinct()
                        .sorted()
                        .collect(Collectors.toList());
            case "grupo_ocorrencia":
                return registros.stream()
                        .map(Registro::getGrupoOcorrencia)
                        .filter(grupo -> grupo != null && !grupo.isEmpty())
                        .distinct()
                        .sorted()
                        .collect(Collectors.toList());
            default:
                throw new IllegalArgumentException("Campo inválido: " + campo);
        }


    }



    public List<Registro> getRegistrosPorStatus(String status) {
        List<Registro> registros = registroRepository.carregarRegistrosDoExcel("C:/Users/hecan/novoArquivo.xlsx");
        LocalDate hoje = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        switch (status.toLowerCase()) {
            case "vencidos":
                // Filtro para "vencidos" ordenado por data mais antiga
                return registros.stream()
                        .filter(registro -> "Em tratamento".equalsIgnoreCase(registro.getStatusReg()) &&
                                LocalDate.parse(registro.getDataAcordada(), formatter).isBefore(hoje))
                        .sorted(Comparator.comparing(registro -> LocalDate.parse(registro.getDataAcordada(), formatter)))
                        .collect(Collectors.toList());

            case "recebidos":
                // Filtro para "recebidos"
                return registros.stream()
                        .filter(registro -> "Recebido".equalsIgnoreCase(registro.getStatusReg()))
                        .collect(Collectors.toList());

            case "em_tratamento":
                // Filtro para "em tratamento" com datas futuras ou iguais ao dia atual
                return registros.stream()
                        .filter(registro -> "Em tratamento".equalsIgnoreCase(registro.getStatusReg()) &&
                                !LocalDate.parse(registro.getDataAcordada(), formatter).isBefore(hoje))
                        .collect(Collectors.toList());

            case "finalizado":
                return registros.stream()
                        .filter(registro -> "Finalizado".equalsIgnoreCase(registro.getStatusReg()) &&
                                registro.getDataResolucao() != null && !registro.getDataResolucao().isEmpty())
                        .collect(Collectors.toList());


            case "nao_conformidade":
                // Filtro para "não conformidade" baseado na gravidade
                return registros.stream()
                        .filter(registro -> List.of("Inconformidade Leve", "Inconformidade Média", "Inconformidade Grave")
                                .contains(registro.getGravidade()))
                        .collect(Collectors.toList());

            default:
                throw new IllegalArgumentException("Status inválido: " + status);
        }
    }

    /**
     * Filtra registros por UFV, Grupo de Ocorrência e/ou ID.
     */
    public List<Registro> filtrarRegistros(String ufv, String grupoOcorrencia, String id) {
        List<Registro> registros = registroRepository.carregarRegistrosDoExcel("C:/Users/hecan/novoArquivo.xlsx");

        return registros.stream()
                .filter(registro -> ufv == null || ufv.isEmpty() || registro.getUfv().equalsIgnoreCase(ufv))
                .filter(registro -> grupoOcorrencia == null || grupoOcorrencia.isEmpty() || registro.getGrupoOcorrencia().equalsIgnoreCase(grupoOcorrencia))
                .filter(registro -> id == null || id.isEmpty() || String.valueOf(registro.getId()).contains(id))
                .collect(Collectors.toList());
    }

    public byte[] gerarRelatorioPdf(List<Registro> registros) throws Exception {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Configurar fonte e iniciar texto
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                contentStream.beginText();
                contentStream.setLeading(14.5f);
                contentStream.newLineAtOffset(50, 750);

                // Adicionar título
                contentStream.showText("Relatório de Desvios");
                contentStream.newLine();
                contentStream.newLine();

                // Adicionar cabeçalhos da tabela
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.showText("ID       Data Registro       UFV       Supervisor       Status");
                contentStream.newLine();

                // Adicionar registros
                contentStream.setFont(PDType1Font.HELVETICA, 10);
                for (Registro registro : registros) {
                    contentStream.showText(String.format(
                            "%-10s %-15s %-10s %-15s %-15s",
                            registro.getId(),
                            registro.getDataReg(),
                            registro.getUfv(),
                            registro.getSupervisor(),
                            registro.getStatusReg()
                    ));
                    contentStream.newLine();
                }

                // Finalizar texto
                contentStream.endText();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        }
    }

    public Registro getRegistroById(int id) {
        return registroRepository.carregarRegistrosDoExcel("C:/Users/hecan/novoArquivo.xlsx")
                .stream()
                .filter(r -> r.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public void atualizarRegistro(int id, String status, String dataAcordada) {
        Registro registro = getRegistroById(id);
        if (registro == null) {
            throw new IllegalArgumentException("Registro não encontrado.");
        }

        switch (registro.getStatusReg().toLowerCase()) {
            case "recebido":
                if (status != null && !status.equalsIgnoreCase("em_tratamento")) {
                    throw new IllegalArgumentException("Status inválido para 'Recebido'. Apenas 'Em Tratamento' é permitido.");
                }
                if (status != null) registro.setStatusReg(status);
                if (dataAcordada != null) registro.setDataAcordada(dataAcordada);
                break;

            case "em_tratamento":
                if (dataAcordada != null) registro.setDataAcordada(dataAcordada);
                break;

            case "finalizado":
                throw new IllegalArgumentException("Registros 'Finalizado' não podem ser alterados.");
        }

        // Atualizar o registro no repositório (aqui é simulado, ajuste conforme o repositório)
        registroRepository.salvarRegistro(registro); // Você precisa implementar isso no repositório
    }



}
