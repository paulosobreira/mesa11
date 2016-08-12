package br.hibernate;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "m11_partida")
public class PartidaMesa11 extends Mesa11Dados {
	private String nomeJogadorCasa;
	private String nomeTimeCasa;
	private String nomeJogadorVisita;
	private String nomeTimeVisita;
	private String campeonato;
	private Date inicio;
	private Date fim;
	private int golsTimeCasa;
	private int golsTimeVisita;
	private boolean vsCpu;

	public String getCampeonato() {
		return campeonato;
	}

	public void setCampeonato(String campeonato) {
		this.campeonato = campeonato;
	}

	public String getNomeJogadorCasa() {
		return nomeJogadorCasa;
	}

	public void setNomeJogadorCasa(String nomeJogadorCasa) {
		this.nomeJogadorCasa = nomeJogadorCasa;
	}

	public String getNomeTimeCasa() {
		return nomeTimeCasa;
	}

	public void setNomeTimeCasa(String nomeTimeCasa) {
		this.nomeTimeCasa = nomeTimeCasa;
	}

	public String getNomeJogadorVisita() {
		return nomeJogadorVisita;
	}

	public void setNomeJogadorVisita(String nomeJogadorVisita) {
		this.nomeJogadorVisita = nomeJogadorVisita;
	}

	public String getNomeTimeVisita() {
		return nomeTimeVisita;
	}

	public void setNomeTimeVisita(String nomeTimeVisita) {
		this.nomeTimeVisita = nomeTimeVisita;
	}

	public Date getInicio() {
		return inicio;
	}

	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	public Date getFim() {
		return fim;
	}

	public void setFim(Date fim) {
		this.fim = fim;
	}

	public int getGolsTimeCasa() {
		return golsTimeCasa;
	}

	public void setGolsTimeCasa(int golsTimeCasa) {
		this.golsTimeCasa = golsTimeCasa;
	}

	public int getGolsTimeVisita() {
		return golsTimeVisita;
	}

	public void setGolsTimeVisita(int golsTimeVisita) {
		this.golsTimeVisita = golsTimeVisita;
	}

	public boolean isVsCpu() {
		return vsCpu;
	}

	public void setVsCpu(boolean vsCpu) {
		this.vsCpu = vsCpu;
	}

}
