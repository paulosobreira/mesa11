package br.hibernate;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.chainsaw.Main;

public class Time extends Mesa11Dados {

	private String campo;
	private List botoes = new ArrayList();
	private String nome;
	private Integer qtdePontos;
	private int cor1RGB;
	private int cor2RGB;
	private int cor3RGB;
	private int cor4RGB;
	private int cor5RGB;
	private int cor6RGB;

	public int getCor1RGB() {
		return cor1RGB;
	}

	public void setCor1RGB(int cor1rgb) {
		cor1RGB = cor1rgb;
	}

	public int getCor2RGB() {
		return cor2RGB;
	}

	public void setCor2RGB(int cor2rgb) {
		cor2RGB = cor2rgb;
	}

	public int getCor3RGB() {
		return cor3RGB;
	}

	public void setCor3RGB(int cor3rgb) {
		cor3RGB = cor3rgb;
	}

	public int getCor4RGB() {
		return cor4RGB;
	}

	public void setCor4RGB(int cor4rgb) {
		cor4RGB = cor4rgb;
	}

	public int getCor5RGB() {
		return cor5RGB;
	}

	public void setCor5RGB(int cor5rgb) {
		cor5RGB = cor5rgb;
	}

	public int getCor6RGB() {
		return cor6RGB;
	}

	public void setCor6RGB(int cor6rgb) {
		cor6RGB = cor6rgb;
	}

	public Integer getQtdePontos() {
		return qtdePontos;
	}

	public void setQtdePontos(Integer qtdePontos) {
		this.qtdePontos = qtdePontos;
	}

	public String getCampo() {
		return campo;
	}

	public void setCampo(String campo) {
		this.campo = campo;
	}

	public List getBotoes() {
		return botoes;
	}

	public void setBotoes(List botoes) {
		this.botoes = botoes;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Override
	public String toString() {
		return " Nome: " + nome + " Campo: " + campo;
	}

	public static void main(String[] args) {
		System.out.println(Color.BLUE.toString());
		Color color = new Color(100);
		color.getRGB();

	}
}
