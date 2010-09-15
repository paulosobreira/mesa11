package br.tos;

import java.io.Serializable;

import br.mesa11.conceito.Animacao;

public class DadosJogoSrvMesa11 implements Serializable {

	private String nomeJogo;
	private String nomeCriador;
	private String nomeVisitante;
	private String senhaJogo;
	private String timeCasa;
	private boolean SegundoUniformeTimeCasa;
	private String timeVisita;
	private boolean SegundoUniformeTimeVisita;
	private boolean jogoTerminado;
	private String bolaCampoCasa;
	private String bolaCampoVisita;
	private int tempoJogo;
	private int tempoJogoJogada;
	private int golsCasa;
	private int golsVisita;
	private int numeroJogadas;
	private int numeroJogadasTimeCasa;
	private int numeroJogadasTimeVisita;
	private String timeVez;
	private String tempoJogoFormatado;
	private String tempoRestanteJogoFormatado;
	private String tempoJogadaRestanteJogoFormatado;

	public String getTempoJogadaRestanteJogoFormatado() {
		return tempoJogadaRestanteJogoFormatado;
	}

	public void setTempoJogadaRestanteJogoFormatado(
			String tempoJogadaRestanteJogoFormatado) {
		this.tempoJogadaRestanteJogoFormatado = tempoJogadaRestanteJogoFormatado;
	}

	public String getTempoRestanteJogoFormatado() {
		return tempoRestanteJogoFormatado;
	}

	public void setTempoRestanteJogoFormatado(String tempoRestanteJogoFormatado) {
		this.tempoRestanteJogoFormatado = tempoRestanteJogoFormatado;
	}

	public String getTempoJogoFormatado() {
		return tempoJogoFormatado;
	}

	public void setTempoJogoFormatado(String tempoJogoFormatado) {
		this.tempoJogoFormatado = tempoJogoFormatado;
	}

	public String getTimeVez() {
		return timeVez;
	}

	public void setTimeVez(String timeVez) {
		this.timeVez = timeVez;
	}

	public int getNumeroJogadas() {
		return numeroJogadas;
	}

	public void setNumeroJogadas(int numeroJogadas) {
		this.numeroJogadas = numeroJogadas;
	}

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

	public int getNumeroJogadasTimeCasa() {
		return numeroJogadasTimeCasa;
	}

	public void setNumeroJogadasTimeCasa(int numeroJogadasTimeCasa) {
		this.numeroJogadasTimeCasa = numeroJogadasTimeCasa;
	}

	public int getNumeroJogadasTimeVisita() {
		return numeroJogadasTimeVisita;
	}

	public void setNumeroJogadasTimeVisita(int numeroJogadasTimeVisita) {
		this.numeroJogadasTimeVisita = numeroJogadasTimeVisita;
	}

	public boolean isJogoTerminado() {
		return jogoTerminado;
	}

	public void setJogoTerminado(boolean jogoTerminado) {
		this.jogoTerminado = jogoTerminado;
	}

}
