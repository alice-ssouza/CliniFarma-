/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package strategy;
import model.Paciente;

/**
 *
 * @author Alice
 */
public class ExibicaoCompleta implements EstrategiaExibicaoPaciente {

    @Override
    public void exibir(Paciente paciente) {
        System.out.println("Nome: " + paciente.getNome());
        System.out.println("CPF: " + paciente.getCpf());
        System.out.println("Nascimento: " + paciente.getDataNascimento());
        System.out.println("Endereco: " + paciente.getEndereco());
        System.out.println("Telefone: " + paciente.getTelefone());
        System.out.println("Alergia: " + paciente.getAlergia());
        Paciente.ContatoEmergencia contato =
        paciente.getContatoEmergencia();
        System.out.println("\n--- Contato de Emergencia ---");

        if (contato != null) {
            System.out.println("Nome: " + contato.getNome());
            System.out.println("Telefone: " + contato.getTelefone());
        } else {
            System.out.println("Nenhum contato de emergencia cadastrado.");
        }
    }
}