package br.hibernate;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Entity
public class CampeonatoMesa11 extends Mesa11Dados {

	private Integer tempoJogo;

	private Integer numeroJogadas;

	private Integer tempoJogada;

	
	
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

	private String nome;
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "campeonatoMesa11", targetEntity = TimesCampeonatoMesa11.class)
	private List<TimesCampeonatoMesa11> timesCampeonatoMesa11 = new ArrayList<TimesCampeonatoMesa11>();

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "campeonatoMesa11", targetEntity = JogadoresCampeonatoMesa11.class)
	private List<JogadoresCampeonatoMesa11> jogadoresCampeonatoMesa11 = new ArrayList<JogadoresCampeonatoMesa11>();

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public List<TimesCampeonatoMesa11> getTimesCampeonatoMesa11() {
		return timesCampeonatoMesa11;
	}

	public void setTimesCampeonatoMesa11(
			List<TimesCampeonatoMesa11> timesCampeonatoMesa11) {
		this.timesCampeonatoMesa11 = timesCampeonatoMesa11;
	}

	public List<JogadoresCampeonatoMesa11> getJogadoresCampeonatoMesa11() {
		return jogadoresCampeonatoMesa11;
	}

	public void setJogadoresCampeonatoMesa11(
			List<JogadoresCampeonatoMesa11> jogadoresCampeonatoMesa11) {
		this.jogadoresCampeonatoMesa11 = jogadoresCampeonatoMesa11;
	}

}
