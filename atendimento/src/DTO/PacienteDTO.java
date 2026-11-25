/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DTO;
/**
 *
 * @author Alice
 */

import java.time.LocalDate;

public class PacienteDTO {

    private String cpf;
    private String nome;
    private LocalDate dataNascimento;
    private String telefone;

    public PacienteDTO(String cpf, String nome,
                       LocalDate dataNascimento, String telefone) {
        this.cpf = cpf;
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.telefone = telefone;
    }

    public String getCpf() {
        return cpf;
    }

    public String getNome() {
        return nome;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public String getTelefone() {
        return telefone;
    }
}
