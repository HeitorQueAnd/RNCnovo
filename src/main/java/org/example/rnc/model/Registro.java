package org.example.rnc.model;

public class Registro {
    private int id;
    private String dataReg;
    private String ufv;
    private String supervisor;
    private String grupoOcorrencia;
    private String ocorrencia;
    private String gravidade;
    private String dataAcordada;
    private String statusReg;
    private String dataResolucao;

    public Registro(int id, String dataReg, String ufv, String supervisor, String grupoOcorrencia,
                    String ocorrencia, String gravidade, String dataAcordada, String statusReg, String dataResolucao) {
        this.id = id;
        this.dataReg = dataReg;
        this.ufv = ufv;
        this.supervisor = supervisor;
        this.grupoOcorrencia = grupoOcorrencia;
        this.ocorrencia = ocorrencia;
        this.gravidade = gravidade;
        this.dataAcordada = dataAcordada;
        this.statusReg = statusReg;
        this.dataResolucao = dataResolucao;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getDataReg() {
        return dataReg;
    }

    public String getUfv() {
        return ufv;
    }

    public String getSupervisor() {
        return supervisor;
    }

    public String getGrupoOcorrencia() {
        return grupoOcorrencia;
    }

    public String getOcorrencia() {
        return ocorrencia;
    }

    public String getGravidade() {
        return gravidade;
    }

    public String getDataAcordada() {
        return dataAcordada;
    }

    public String getStatusReg() {
        return statusReg;
    }

    public String getDataResolucao() {
        return dataResolucao;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setDataReg(String dataReg) {
        this.dataReg = dataReg;
    }

    public void setUfv(String ufv) {
        this.ufv = ufv;
    }

    public void setSupervisor(String supervisor) {
        this.supervisor = supervisor;
    }

    public void setGrupoOcorrencia(String grupoOcorrencia) {
        this.grupoOcorrencia = grupoOcorrencia;
    }

    public void setOcorrencia(String ocorrencia) {
        this.ocorrencia = ocorrencia;
    }

    public void setGravidade(String gravidade) {
        this.gravidade = gravidade;
    }

    public void setDataAcordada(String dataAcordada) {
        this.dataAcordada = dataAcordada;
    }

    public void setStatusReg(String statusReg) {
        this.statusReg = statusReg;
    }

    public void setDataResolucao(String dataResolucao) {
        this.dataResolucao = dataResolucao;
    }
}
