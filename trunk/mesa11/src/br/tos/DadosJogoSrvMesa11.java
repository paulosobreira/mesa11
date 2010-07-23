package br.tos;

import java.io.Serializable;

public class DadosJogoSrvMesa11 implements Serializable {

	private String nomeJogo;
	private String nomeCriador;
	private String nomeVisitante;
	private String senhaJogo;
	private String timeCasa;
	private boolean SegundoUniformeTimeCasa;
	private String timeVisita;
	private boolean SegundoUniformeTimeVisita;
	private String bolaCampoCasa;
	private String bolaCampoVisita;
	private int tempoJogo;
	private int tempoJogoJogada;
	private int golsCasa;
	private int golsVisita;

	public int getGolsCasa() {
		return golsCasa;
	}

	public void setGolsCasa(int golsCasa) {
		this.golsCasa = golsCasa;
	}

	public int getGolsVisita() {
		return golsVisita;
	}

	public void setGolsVisita(int golsVisita) {
		this.golsVisita = golsVisita;
	}

	public String getNomeJogo() {
		return nomeJogo;
	}

	public void setNomeJogo(String nomeJogo) {
		this.nomeJogo = nomeJogo;
	}

	public String getSenhaJogo() {
		return senhaJogo;
	}

	public void setSenhaJogo(String senhaJogo) {
		this.senhaJogo = senhaJogo;
	}

	public String getNomeVisitante() {
		return nomeVisitante;
	}

	public void setNomeVisitante(String nomeVisitante) {
		this.nomeVisitante = nomeVisitante;
	}

	public String getNomeCriador() {
		return nomeCriador;
	}

	public void setNomeCriador(String nomeCriador) {
		this.nomeCriador = nomeCriador;
	}

	public String getTimeCasa() {
		return timeCasa;
	}

	public void setTimeCasa(String timeCasa) {
		this.timeCasa = timeCasa;
	}

	public boolean isSegundoUniformeTimeCasa() {
		return SegundoUniformeTimeCasa;
	}

	public void setSegundoUniformeTimeCasa(boolean segundoUniformeTimeCasa) {
		SegundoUniformeTimeCasa = segundoUniformeTimeCasa;
	}

	public String getTimeVisita() {
		return timeVisita;
	}

	public void setTimeVisita(String timeVisita) {
		this.timeVisita = timeVisita;
	}

	public boolean isSegundoUniformeTimeVisita() {
		return SegundoUniformeTimeVisita;
	}

	public void setSegundoUniformeTimeVisita(boolean segundoUniformeTimeVisita) {
		SegundoUniformeTimeVisita = segundoUniformeTimeVisita;
	}

	public int getTempoJogo() {
		return tempoJogo;
	}

	public void setTempoJogo(int tempoJogo) {
		this.tempoJogo = tempoJogo;
	}

	public int getTempoJogoJogada() {
		return tempoJogoJogada;
	}

	public void setTempoJogoJogada(int tempoJogoJogada) {
		this.tempoJogoJogada = tempoJogoJogada;
	}

	@Override
	public boolean equals(Object obj) {
		if (nomeJogo != null) {
			DadosJogoSrvMesa11 jogoSrvMesa11 = (DadosJogoSrvMesa11) obj;
			return nomeJogo.equals(jogoSrvMesa11.getNomeJogo());
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		if (nomeJogo != null) {
			return nomeJogo.hashCode();
		}
		return super.hashCode();
	}

	public String getBolaCampoCasa() {
		return bolaCampoCasa;
	}

	public void setBolaCampoCasa(String bolaCampoCasa) {
		this.bolaCampoCasa = bolaCampoCasa;
	}

	public String getBolaCampoVisita() {
		return bolaCampoVisita;
	}

	public void setBolaCampoVisita(String bolaCampoVisita) {
		this.bolaCampoVisita = bolaCampoVisita;
	}

}
