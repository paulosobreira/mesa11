package br.tos;

import java.io.Serializable;

public class ClassificacaoTime implements Serializable {
	private String time;
	private int vitorias;
	private int empates;
	private int derrotas;
	private int golsFavor;
	private int golsContra;

	@Override
	public String toString() {

		return time + " V " + getVitorias() + " E " + getEmpates() + " SG "
				+ getSaldoGols() + " G " + getGolsFavor();
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getSaldoGols() {
		return golsFavor - golsContra;
	}

	public int getVitorias() {
		return vitorias;
	}

	public void setVitorias(int vitorias) {
		this.vitorias = vitorias;
	}

	public int getEmpates() {
		return empates;
	}

	public void setEmpates(int empates) {
		this.empates = empates;
	}

	public int getDerrotas() {
		return derrotas;
	}

	public void setDerrotas(int derrotas) {
		this.derrotas = derrotas;
	}

	public int getGolsFavor() {
		return golsFavor;
	}

	public void setGolsFavor(int golsFavor) {
		this.golsFavor = golsFavor;
	}

	public int getGolsContra() {
		return golsContra;
	}

	public void setGolsContra(int golsContra) {
		this.golsContra = golsContra;
	}

}
