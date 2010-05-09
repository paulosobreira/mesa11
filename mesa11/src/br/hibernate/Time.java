package br.hibernate;

import java.util.ArrayList;
import java.util.List;

public class Time {

	private String campo;
	private List botoes = new ArrayList();
	private String nome;

	public String getCampo() {
		return campo;
	}

	public void setCampo(String campo) {
		this.campo = campo;
	}

	public List getBotoes() {
		return botoes;
	}

	public void setBotoes(List botoes) {
		this.botoes = botoes;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Override
	public String toString() {
		return " Nome: " + nome + " Campo: " + campo;
	}
}
