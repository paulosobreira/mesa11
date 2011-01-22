package br.mesa11.conceito;

import java.io.Serializable;
import java.util.List;

import br.hibernate.Botao;
import br.nnpe.Util;

public class Animacao implements Serializable {

	private Long objetoAnimacao;
	private int index;
	private long timeStamp;

	public static void main(String[] args) {
		int variancia = 10 - 687 / 100;
		System.out.println(Util.intervalo(-(variancia), variancia));
	}

	private List pontosAnimacao;

	public Long getObjetoAnimacao() {
		return objetoAnimacao;
	}

	public void setObjetoAnimacao(Long objetoAnimacao) {
		this.objetoAnimacao = objetoAnimacao;
	}

	public List getPontosAnimacao() {
		return pontosAnimacao;
	}

	public void setPontosAnimacao(List pontosAnimacao) {
		this.pontosAnimacao = pontosAnimacao;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

}
