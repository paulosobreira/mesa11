package br.mesa11.servidor;

import java.util.HashMap;
import java.util.Map;

import br.recursos.Lang;
import br.tos.DadosMesa11;
import br.tos.DadosJogoSrvMesa11;
import br.tos.Mesa11TO;
import br.tos.MsgSrv;

public class ControleJogosServidor {
	private int contadorJogos;
	private DadosMesa11 dadosMesa11;
	private Map mapaJogos = new HashMap<String, JogoServidor>();

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

	public Object criarJogo(DadosJogoSrvMesa11 dadosJogoSrvMesa11) {
		dadosJogoSrvMesa11.setNomeJogo("Jogo " + contadorJogos++);
		dadosMesa11.getJogosCriados().add(dadosJogoSrvMesa11.getNomeJogo());
		JogoServidor jogoServidor = new JogoServidor(dadosJogoSrvMesa11);
		mapaJogos.put(dadosJogoSrvMesa11.getNomeJogo(), jogoServidor);
		Mesa11TO mesa11to = new Mesa11TO();
		mesa11to.setData(dadosJogoSrvMesa11);
		return mesa11to;
	}

	public Object entrarJogo(DadosJogoSrvMesa11 dadosJogoSrvMesa11) {
		JogoServidor jogoSrvMesa11 = (JogoServidor) mapaJogos
				.get(dadosJogoSrvMesa11.getNomeJogo());
		if (jogoSrvMesa11 != null) {
			dadosMesa11.getJogosCriados().remove(
					jogoSrvMesa11.getDadosJogoSrvMesa11().getNomeJogo());
			dadosMesa11.getJogosAndamento().add(
					jogoSrvMesa11.getDadosJogoSrvMesa11().getNomeJogo());
			return new MsgSrv("Entrou Jogo");
		}
		return new MsgSrv("Erro ao Entrou Jogo");
	}

	public Object obterDadosJogo(String nomejogo) {
		JogoServidor jogoSrvMesa11 = (JogoServidor) mapaJogos.get(nomejogo);
		if (jogoSrvMesa11 == null) {
			return new MsgSrv(Lang.msg("jogoInexistente"));
		}
		Mesa11TO mesa11to = new Mesa11TO();
		mesa11to.setData(jogoSrvMesa11.getDadosJogoSrvMesa11());
		return mesa11to;
	}
}
