package br.mesa11.servidor;

import br.tos.DadosJogoSrvMesa11;

public class JogoServidor {
	private DadosJogoSrvMesa11 dadosJogoSrvMesa11;

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

}
