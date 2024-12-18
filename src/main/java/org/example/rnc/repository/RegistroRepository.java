package org.example.rnc.repository;

import org.example.rnc.model.Registro;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Repository;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RegistroRepository {

    public List<Registro> carregarRegistrosDoExcel(String filePath) {
        List<Registro> registros = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                int id = (int) getCellValueAsNumeric(row.getCell(0));
                String dataReg = getCellValueAsString(row.getCell(1));
                String ufv = getCellValueAsString(row.getCell(2));
                String supervisor = getCellValueAsString(row.getCell(3));
                String grupoOcorrencia = getCellValueAsString(row.getCell(4));
                String ocorrencia = getCellValueAsString(row.getCell(5));
                String gravidade = getCellValueAsString(row.getCell(6));
                String dataAcordada = getCellValueAsString(row.getCell(7));
                String statusReg = getCellValueAsString(row.getCell(8));
                String dataResolucao = getCellValueAsString(row.getCell(9));

                registros.add(new Registro(id, dataReg, ufv, supervisor, grupoOcorrencia, ocorrencia, gravidade, dataAcordada, statusReg, dataResolucao));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return registros;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue().toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate().toString();
            }
            return String.valueOf((int) cell.getNumericCellValue());
        }
        return "";
    }

    private double getCellValueAsNumeric(Cell cell) {
        if (cell == null || cell.getCellType() != CellType.NUMERIC) return 0;
        return cell.getNumericCellValue();
    }
    public void salvarRegistro(Registro registro) {
        String filePath = "C:/Users/hecan/novoArquivo.xlsx"; // Certifique-se de usar o caminho correto do arquivo Excel

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            // Localizar o registro pelo ID e atualizar os dados
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                int id = (int) getCellValueAsNumeric(row.getCell(0));
                if (id == registro.getId()) {
                    // Atualizar as colunas correspondentes
                    row.getCell(8).setCellValue(registro.getStatusReg()); // Status
                    row.getCell(7).setCellValue(registro.getDataAcordada()); // Data Acordada

                    break; // Encerrar o loop após encontrar o registro
                }
            }

            // Salvar alterações no arquivo Excel
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao salvar registro no arquivo Excel.");
        }
    }


}

