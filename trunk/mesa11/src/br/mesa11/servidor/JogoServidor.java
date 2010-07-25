package br.mesa11.servidor;

import br.hibernate.Time;
import br.mesa11.conceito.ControleJogo;
import br.tos.DadosJogoSrvMesa11;

public class JogoServidor {
	private DadosJogoSrvMesa11 dadosJogoSrvMesa11;
	private Time timeCasa;
	private Time timeVisita;
	private ControleJogo controleJogo;

	public JogoServidor(DadosJogoSrvMesa11 dadosJogoSrvMesa11) {
		super();
		this.dadosJogoSrvMesa11 = dadosJogoSrvMesa11;
	}

	public DadosJogoSrvMesa11 getDadosJogoSrvMesa11() {
		return dadosJogoSrvMesa11;
	}

	public void setDadosJogoSrvMesa11(DadosJogoSrvMesa11 dadosJogoSrvMesa11) {
		this.dadosJogoSrvMesa11 = dadosJogoSrvMesa11;
	}

	public ControleJogo getControleJogo() {
		return controleJogo;
	}

	public void setControleJogo(ControleJogo controleJogo) {
		this.controleJogo = controleJogo;
	}

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

}
