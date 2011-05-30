package br.mesa11.conceito;

import java.io.Serializable;

public class GolJogador implements Serializable {

	private Long idJogador;

	private String tempoGol;

	private boolean contra;

	public boolean isContra() {
		return contra;
	}

	public void setContra(boolean contra) {
		this.contra = contra;
	}

	public Long getIdJogador() {
		return idJogador;
	}

	public void setIdJogador(Long idJogador) {
		this.idJogador = idJogador;
	}

	public String getTempoGol() {
		return tempoGol;
	}

	public void setTempoGol(String tempoGol) {
		this.tempoGol = tempoGol;
	}

}
