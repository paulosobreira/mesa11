package br.hibernate;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.UniqueConstraint;

@Entity
public class CampeonatoMesa11 extends Mesa11Dados {

	@Column(unique = true, nullable = false)
	private String nome;

	private Integer tempoJogo;

	private Integer numeroJogadas;

	private Integer tempoJogada;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "campeonatoMesa11")
	private Collection<TimesCampeonatoMesa11> timesCampeonatoMesa11 = new LinkedList<TimesCampeonatoMesa11>();

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "campeonatoMesa11")
	private Collection<JogadoresCampeonatoMesa11> jogadoresCampeonatoMesa11 = new LinkedList<JogadoresCampeonatoMesa11>();

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "campeonatoMesa11")
	private Collection<RodadaCampeonatoMesa11> rodadaCampeonatoMesa11 = new LinkedList<RodadaCampeonatoMesa11>();

	public Integer getTempoJogo() {
		return tempoJogo;
	}

	public void setTempoJogo(Integer tempoJogo) {
		this.tempoJogo = tempoJogo;
	}

	public Integer getNumeroJogadas() {
		return numeroJogadas;
	}

	public void setNumeroJogadas(Integer numeroJogadas) {
		this.numeroJogadas = numeroJogadas;
	}

	public Integer getTempoJogada() {
		return tempoJogada;
	}

	public void setTempoJogada(Integer tempoJogada) {
		this.tempoJogada = tempoJogada;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Collection<TimesCampeonatoMesa11> getTimesCampeonatoMesa11() {
		return timesCampeonatoMesa11;
	}

	public void setTimesCampeonatoMesa11(
			Collection<TimesCampeonatoMesa11> timesCampeonatoMesa11) {
		this.timesCampeonatoMesa11 = timesCampeonatoMesa11;
	}

	public Collection<JogadoresCampeonatoMesa11> getJogadoresCampeonatoMesa11() {
		return jogadoresCampeonatoMesa11;
	}

	public void setJogadoresCampeonatoMesa11(
			Collection<JogadoresCampeonatoMesa11> jogadoresCampeonatoMesa11) {
		this.jogadoresCampeonatoMesa11 = jogadoresCampeonatoMesa11;
	}

	public Collection<RodadaCampeonatoMesa11> getRodadaCampeonatoMesa11() {
		return rodadaCampeonatoMesa11;
	}

	public void setRodadaCampeonatoMesa11(
			Collection<RodadaCampeonatoMesa11> rodadaCampeonatoMesa11) {
		this.rodadaCampeonatoMesa11 = rodadaCampeonatoMesa11;
	}

}
