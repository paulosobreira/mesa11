package br.mesa11.conceito;

import java.io.Serializable;
import java.util.List;

import br.hibernate.Botao;

public class Animacao implements Serializable {

	private Botao objetoAnimacao;
	public boolean valida = true;
	private int index;

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

	public boolean isValida() {
		return valida;
	}

	public void setValida(boolean valida) {
		this.valida = valida;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

}
