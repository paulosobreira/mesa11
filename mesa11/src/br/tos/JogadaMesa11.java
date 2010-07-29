package br.tos;

import java.awt.Point;
import java.io.Serializable;

public class JogadaMesa11 implements Serializable {
	private String timeClienteOnline;
	private DadosJogoSrvMesa11 dadosJogoSrvMesa11;
	private Point pontoClicado;
	private Point pontoSolto;
	private int idGoleiro;
	private double rotacaoGoleiro;

	public JogadaMesa11(String timeClienteOnline,
			DadosJogoSrvMesa11 dadosJogoSrvMesa11) {
		super();
		this.timeClienteOnline = timeClienteOnline;
		this.dadosJogoSrvMesa11 = dadosJogoSrvMesa11;
	}

	public String getTimeClienteOnline() {
		return timeClienteOnline;
	}

	public void setTimeClienteOnline(String timeClienteOnline) {
		this.timeClienteOnline = timeClienteOnline;
	}

	public DadosJogoSrvMesa11 getDadosJogoSrvMesa11() {
		return dadosJogoSrvMesa11;
	}

	public void setDadosJogoSrvMesa11(DadosJogoSrvMesa11 dadosJogoSrvMesa11) {
		this.dadosJogoSrvMesa11 = dadosJogoSrvMesa11;
	}

	public Point getPontoClicado() {
		return pontoClicado;
	}

	public void setPontoClicado(Point pontoClicado) {
		this.pontoClicado = pontoClicado;
	}

	public Point getPontoSolto() {
		return pontoSolto;
	}

	public void setPontoSolto(Point pontoSolto) {
		this.pontoSolto = pontoSolto;
	}

	public int getIdGoleiro() {
		return idGoleiro;
	}

	public void setIdGoleiro(int idGoleiro) {
		this.idGoleiro = idGoleiro;
	}

	public double getRotacaoGoleiro() {
		return rotacaoGoleiro;
	}

	public void setRotacaoGoleiro(double rotacaoGoleiro) {
		this.rotacaoGoleiro = rotacaoGoleiro;
	}

}
