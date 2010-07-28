package br.mesa11.servidor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.hibernate.Bola;
import br.hibernate.Botao;
import br.hibernate.Goleiro;
import br.hibernate.Time;
import br.mesa11.conceito.ControleJogo;
import br.recursos.Lang;
import br.tos.BotaoPosSrvMesa11;
import br.tos.DadosMesa11;
import br.tos.DadosJogoSrvMesa11;
import br.tos.JogadaMesa11;
import br.tos.Mesa11TO;
import br.tos.MsgSrv;
import br.tos.PosicaoBtnsSrvMesa11;

public class ControleJogosServidor {
	private int contadorJogos;
	private ControlePersistencia controlePersistencia;
	private DadosMesa11 dadosMesa11;
	private Map mapaJogos = new HashMap<String, JogoServidor>();

	public ControleJogosServidor(DadosMesa11 dadosMesa11,
			ControlePersistencia controlePersistencia) {
		super();
		this.dadosMesa11 = dadosMesa11;
		this.controlePersistencia = controlePersistencia;
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
			jogoSrvMesa11.setDadosJogoSrvMesa11(dadosJogoSrvMesa11);
			dadosMesa11.getJogosCriados().remove(
					jogoSrvMesa11.getDadosJogoSrvMesa11().getNomeJogo());
			dadosMesa11.getJogosAndamento().add(
					jogoSrvMesa11.getDadosJogoSrvMesa11().getNomeJogo());
			Mesa11TO mesa11to = new Mesa11TO();
			mesa11to.setData(dadosJogoSrvMesa11);
			inciaJogoServidor(jogoSrvMesa11);
			return mesa11to;
		}
		return new MsgSrv(Lang.msg("jogoInexistente"));
	}

	private void inciaJogoServidor(JogoServidor jogoSrvMesa11) {
		ControleJogo controleJogo = new ControleJogo(jogoSrvMesa11);
		DadosJogoSrvMesa11 dadosJogoSrvMesa11 = jogoSrvMesa11
				.getDadosJogoSrvMesa11();
		Time timeCasa = controlePersistencia.obterTime(dadosJogoSrvMesa11
				.getTimeCasa());
		Time timeVisita = controlePersistencia.obterTime(dadosJogoSrvMesa11
				.getTimeVisita());
		jogoSrvMesa11.setTimeCasa(timeCasa);
		jogoSrvMesa11.setTimeVisita(timeVisita);
		jogoSrvMesa11.setControleJogo(controleJogo);
		controleJogo.iniciaJogoOnline(jogoSrvMesa11.getDadosJogoSrvMesa11(),
				timeCasa, timeVisita);

	}

	public Object obterDadosJogo(String nomejogo) {
		JogoServidor jogoSrvMesa11 = (JogoServidor) mapaJogos.get(nomejogo);
		if (jogoSrvMesa11 == null) {
			return new MsgSrv(Lang.msg("jogoInexistente"));
		}
		DadosJogoSrvMesa11 dadosJogoSrvMesa11 = jogoSrvMesa11
				.getDadosJogoSrvMesa11();
		ControleJogo controleJogo = jogoSrvMesa11.getControleJogo();
		if (controleJogo != null) {
			dadosJogoSrvMesa11.setTimeVez(controleJogo.timeJogadaVez()
					.getNome());
			dadosJogoSrvMesa11.setGolsCasa(controleJogo
					.verGolsInt(jogoSrvMesa11.getTimeCasa()));
			dadosJogoSrvMesa11.setGolsVisita(controleJogo
					.verGolsInt(jogoSrvMesa11.getTimeVisita()));
			dadosJogoSrvMesa11.setTempoJogoFormatado(controleJogo
					.tempoJogoFormatado());
			dadosJogoSrvMesa11.setTempoRestanteJogoFormatado(controleJogo
					.tempoRestanteJogoFormatado());
			dadosJogoSrvMesa11.setTempoJogadaRestanteJogoFormatado(controleJogo
					.tempoJogadaRestanteJogoFormatado());
			dadosJogoSrvMesa11.setNumeroJogadasTimeCasa(controleJogo
					.obterNumJogadas(jogoSrvMesa11.getTimeCasa()));

		}
		Mesa11TO mesa11to = new Mesa11TO();
		mesa11to.setData(jogoSrvMesa11.getDadosJogoSrvMesa11());
		return mesa11to;
	}

	public Object jogada(JogadaMesa11 jogadaMesa11) {
		JogoServidor jogoSrvMesa11 = (JogoServidor) mapaJogos.get(jogadaMesa11
				.getDadosJogoSrvMesa11().getNomeJogo());
		if (jogoSrvMesa11 == null) {
			return new MsgSrv(Lang.msg("jogoInexistente"));
		}
		ControleJogo controleJogo = jogoSrvMesa11.getControleJogo();
		if (controleJogo.timeJogadaVez().getNome().equals(
				jogadaMesa11.getTimeClienteOnline())
				&& !controleJogo.isAnimando()) {
			jogoSrvMesa11.getControleJogo().efetuaJogada(
					jogadaMesa11.getPontoClicado(),
					jogadaMesa11.getPontoSolto());
		}
		return null;
	}

	public Object obterUltimaJogada(String nomejogo) {
		JogoServidor jogoSrvMesa11 = (JogoServidor) mapaJogos.get(nomejogo);
		if (jogoSrvMesa11 == null) {
			return new MsgSrv(Lang.msg("jogoInexistente"));
		}
		Mesa11TO mesa11to = new Mesa11TO();
		mesa11to.setData(jogoSrvMesa11.getControleJogo().obterUltimaJogada());
		return mesa11to;
	}

	public Object obterPosicaoBotoes(String nomejogo) {
		JogoServidor jogoSrvMesa11 = (JogoServidor) mapaJogos.get(nomejogo);
		if (jogoSrvMesa11 == null) {
			return new MsgSrv(Lang.msg("jogoInexistente"));
		}
		if (jogoSrvMesa11.getControleJogo().isAnimando()) {
			return null;
		}
		Mesa11TO mesa11to = new Mesa11TO();
		PosicaoBtnsSrvMesa11 posicaoBtnsSrvMesa11 = new PosicaoBtnsSrvMesa11();
		List<BotaoPosSrvMesa11> botaoPosSrvMesa11List = new ArrayList();
		for (Iterator iterator = jogoSrvMesa11.getControleJogo().getBotoes()
				.keySet().iterator(); iterator.hasNext();) {
			Long id = (Long) iterator.next();
			Botao botao = (Botao) jogoSrvMesa11.getControleJogo().getBotoes()
					.get(id);
			BotaoPosSrvMesa11 botaoPosSrvMesa11 = new BotaoPosSrvMesa11();
			botaoPosSrvMesa11.setId(id);
			botaoPosSrvMesa11.setPos(botao.getCentro());
			if (botao instanceof Goleiro) {
				Goleiro goleiro = (Goleiro) botao;
				botaoPosSrvMesa11.setRotacao(goleiro.getRotacao());
			}
			if (botao instanceof Bola) {
				System.out.println("envia bola pos");
			}
			botaoPosSrvMesa11List.add(botaoPosSrvMesa11);

		}
		posicaoBtnsSrvMesa11.setBotoes(botaoPosSrvMesa11List);
		posicaoBtnsSrvMesa11.setTimeStamp(System.currentTimeMillis());
		mesa11to.setData(posicaoBtnsSrvMesa11);
		return mesa11to;
	}
}
