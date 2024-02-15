package br.mesa11.conceito;

import java.io.Serializable;

public class GolJogador implements Serializable {

	private Long idJogador;

	private String tempoGol;

	private boolean contra;

	@Override
	public boolean equals(Object obj) {
		GolJogador golJogador = (GolJogador) obj;
		if (idJogador == null && golJogador.getIdJogador() != null)
			return false;
		if (tempoGol == null && golJogador.getTempoGol() != null)
			return false;
		return idJogador.equals(golJogador.getIdJogador())
				&& tempoGol.equals(golJogador.getTempoGol())
				&& contra == golJogador.isContra();
	}

	@Override
	public int hashCode() {
		String toHash = " " + idJogador + tempoGol + contra;
		return toHash.hashCode();
	}

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
