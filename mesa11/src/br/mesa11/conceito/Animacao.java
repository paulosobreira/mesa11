package br.mesa11.conceito;

import java.util.List;

import br.hibernate.Botao;

public class Animacao {

	private Botao objetoAnimacao;

	private List pontosAnimacao;

	public Botao getObjetoAnimacao() {
		return objetoAnimacao;
	}

	public void setObjetoAnimacao(Botao objetoAnimacao) {
		if (objetoAnimacao.getCentroInicio() == null)
			objetoAnimacao.setCentroInicio(objetoAnimacao.getCentro());
		this.objetoAnimacao = objetoAnimacao;
	}

	public List getPontosAnimacao() {
		return pontosAnimacao;
	}

	public void setPontosAnimacao(List pontosAnimacao) {
		this.pontosAnimacao = pontosAnimacao;
	}

}
