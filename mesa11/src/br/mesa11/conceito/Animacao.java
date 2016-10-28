package br.mesa11.conceito;

import java.io.Serializable;
import java.util.List;

import br.hibernate.Botao;
import br.nnpe.Util;
import br.tos.PosicaoBtnsSrvMesa11;

public class Animacao implements Serializable {

	private Long objetoAnimacao;
	private Long sequencia;
	private boolean executou;
	private String dica;
	private PosicaoBtnsSrvMesa11 posicaoBtnsSrvMesa11;

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

	public boolean isExecutou() {
		return executou;
	}

	public void setExecutou(boolean executou) {
		this.executou = executou;
	}

	public PosicaoBtnsSrvMesa11 getPosicaoBtnsSrvMesa11() {
		return posicaoBtnsSrvMesa11;
	}

	public void setPosicaoBtnsSrvMesa11(
			PosicaoBtnsSrvMesa11 posicaoBtnsSrvMesa11) {
		this.posicaoBtnsSrvMesa11 = posicaoBtnsSrvMesa11;
	}

	public String getDica() {
		return dica;
	}

	public void setDica(String dica) {
		this.dica = dica;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((sequencia == null) ? 0 : sequencia.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Animacao other = (Animacao) obj;
		if (sequencia == null) {
			if (other.sequencia != null)
				return false;
		} else if (!sequencia.equals(other.sequencia))
			return false;
		return true;
	}

	public Long getSequencia() {
		return sequencia;
	}

	public void setSequencia(Long sequencia) {
		this.sequencia = sequencia;
	}

}
