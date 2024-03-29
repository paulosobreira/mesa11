package br.tos;

import java.io.Serializable;

import br.nnpe.tos.SessaoCliente;

/**
 * @author Paulo Sobreira Criado em 28/07/2007 as 15:51:36
 */
public class ClienteMesa11 implements Serializable {

    private static final long serialVersionUID = 6938384085272885074L;
    private String nomeJogador;
    private String senhaJogador;
    private String emailJogador;
    private SessaoCliente sessaoCliente;
    private String texto;
    private String nomeJogo;

    public ClienteMesa11(SessaoCliente sessaoCliente) {
        super();
        this.sessaoCliente = sessaoCliente;
    }

    public String getTexto() {
        return texto;
    }

    public String getNomeJogo() {
        return nomeJogo;
    }

    public void setNomeJogo(String nomeJogo) {
        this.nomeJogo = nomeJogo;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public SessaoCliente getSessaoCliente() {
        return sessaoCliente;
    }

    public void setSessaoCliente(SessaoCliente sessaoCliente) {
        this.sessaoCliente = sessaoCliente;
    }

    public ClienteMesa11() {

    }

    public String getNomeJogador() {
        return nomeJogador;
    }

    public void setNomeJogador(String apelido) {
        this.nomeJogador = apelido;
    }

    public String getSenhaJogador() {
        return senhaJogador;
    }

    public void setSenhaJogador(String senhaJogador) {
        this.senhaJogador = senhaJogador;
    }

    public String getEmailJogador() {
        return emailJogador;
    }

    public void setEmailJogador(String emailJogador) {
        this.emailJogador = emailJogador;
    }


}
