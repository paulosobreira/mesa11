package br.hibernate;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class RodadaCampeonatoMesa11 extends Mesa11Dados {

	@ManyToOne
	@JoinColumn(nullable = false)
	private Time timeCasa;

	@ManyToOne
	@JoinColumn(nullable = false)
	private Time timeVisita;

	@ManyToOne
	@JoinColumn(nullable = false)
	private CampeonatoMesa11 campeonatoMesa11;

	private int rodada;

	private int golsCasa;

	private int golsVisita;

	private boolean cpuCasa;

	private boolean cpuVisita;

	@ManyToOne
	private Usuario jogadorCasa;
	@ManyToOne
	private Usuario jogadorVisita;

	public Time getTimeCasa() {
		return timeCasa;
	}

	public void setTimeCasa(Time timeCasa) {
		this.timeCasa = timeCasa;
	}

	public Time getTimeVisita() {
		return timeVisita;
	}

	public void setTimeVisita(Time timeVisita) {
		this.timeVisita = timeVisita;
	}

	public int getRodada() {
		return rodada;
	}

	public void setRodada(int rodada) {
		this.rodada = rodada;
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

	public boolean isCpuCasa() {
		return cpuCasa;
	}

	public void setCpuCasa(boolean cpuCasa) {
		this.cpuCasa = cpuCasa;
	}

	public boolean isCpuVisita() {
		return cpuVisita;
	}

	public void setCpuVisita(boolean cpuVisita) {
		this.cpuVisita = cpuVisita;
	}

	public Usuario getJogadorCasa() {
		return jogadorCasa;
	}

	public void setJogadorCasa(Usuario jogadorCasa) {
		this.jogadorCasa = jogadorCasa;
	}

	public Usuario getJogadorVisita() {
		return jogadorVisita;
	}

	public void setJogadorVisita(Usuario jogadorVisita) {
		this.jogadorVisita = jogadorVisita;
	}

	public CampeonatoMesa11 getCampeonatoMesa11() {
		return campeonatoMesa11;
	}

	public void setCampeonatoMesa11(CampeonatoMesa11 campeonatoMesa11) {
		this.campeonatoMesa11 = campeonatoMesa11;
	}

	@Override
	public String toString() {
		return "[ " + timeCasa.getNome() + " X " + timeVisita.getNome()
				+ " , rodada=" + rodada + "]";
	}

}
