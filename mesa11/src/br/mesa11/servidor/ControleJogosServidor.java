package br.mesa11.servidor;

import java.util.HashMap;
import java.util.Map;

import br.recursos.Lang;
import br.tos.DadosMesa11;
import br.tos.DadosJogoSrvMesa11;
import br.tos.MsgSrv;

public class ControleJogosServidor {
	private int contadorJogos;
	private DadosMesa11 dadosMesa11;
	private Map mapaJogos = new HashMap();

	public ControleJogosServidor(DadosMesa11 dadosMesa11) {
		super();
		this.dadosMesa11 = dadosMesa11;
	}

	public DadosMesa11 getDadosMesa11() {
		return dadosMesa11;
	}

	public void setDadosMesa11(DadosMesa11 dadosMesa11) {
		this.dadosMesa11 = dadosMesa11;
	}

	public Object criarJogo(DadosJogoSrvMesa11 jogoSrvMesa11) {
		jogoSrvMesa11.setNomeJogo("Jogo " + contadorJogos++);
		dadosMesa11.getJogosCriados().add(jogoSrvMesa11.getNomeJogo());
		JogoServidor jogoServidor = new JogoServidor(jogoSrvMesa11);
		mapaJogos.put(jogoSrvMesa11.getNomeJogo(), jogoServidor);
		System.out.println("criarJogo jogoCriado");
		return new MsgSrv(Lang.msg("jogoCriado"));
	}
}
