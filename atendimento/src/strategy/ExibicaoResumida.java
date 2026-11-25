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
public class ExibicaoResumida implements EstrategiaExibicaoPaciente {

    @Override
    public void exibir(Paciente paciente) {
        System.out.println("Nome: " + paciente.getNome());
        System.out.println("CPF: " + paciente.getCpf());
    }
}
