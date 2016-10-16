package br.mesa11.servidor;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.hibernate.Botao;
import br.hibernate.CampeonatoMesa11;
import br.hibernate.Goleiro;
import br.hibernate.JogadoresCampeonatoMesa11;
import br.hibernate.PartidaMesa11;
import br.hibernate.RodadaCampeonatoMesa11;
import br.hibernate.Time;
import br.mesa11.ConstantesMesa11;
import br.mesa11.ProxyComandos;
import br.mesa11.conceito.Animacao;
import br.mesa11.conceito.AtualizadorJogadaCPU;
import br.mesa11.conceito.ControleJogo;
import br.nnpe.HibernateUtil;
import br.nnpe.Logger;
import br.nnpe.Util;
import br.nnpe.tos.ErroServ;
import br.nnpe.tos.MsgSrv;
import br.nnpe.tos.NnpeTO;
import br.nnpe.tos.SessaoCliente;
import br.recursos.Lang;
import br.servlet.ServletMesa11;
import br.tos.BotaoPosSrvMesa11;
import br.tos.ClassificacaoTime;
import br.tos.ClassificacaoUsuario;
import br.tos.DadosJogoSrvMesa11;
import br.tos.DadosMesa11;
import br.tos.JogadaMesa11;
import br.tos.PosicaoBtnsSrvMesa11;

public class ControleJogosServidor {
	private int contadorJogos;
	private ProxyComandos proxyComandos;
	private ControlePersistencia controlePersistencia;
	private DadosMesa11 dadosMesa11;
	private Map<String, JogoServidor> mapaJogos = new HashMap<String, JogoServidor>();

	public ControleJogosServidor(DadosMesa11 dadosMesa11,
			ControlePersistencia controlePersistencia,
			ProxyComandos proxyComandos) {
		super();
		this.dadosMesa11 = dadosMesa11;
		this.controlePersistencia = controlePersistencia;
		this.proxyComandos = proxyComandos;
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
		JogoServidor jogoServidor = new JogoServidor(dadosJogoSrvMesa11,
				proxyComandos);
		mapaJogos.put(dadosJogoSrvMesa11.getNomeJogo(), jogoServidor);
		NnpeTO mesa11to = new NnpeTO();
		mesa11to.setData(dadosJogoSrvMesa11);
		return mesa11to;
	}

	public Object entrarJogo(DadosJogoSrvMesa11 dadosJogoSrvMesa11) {
		JogoServidor jogoSrvMesa11 = (JogoServidor) mapaJogos
				.get(dadosJogoSrvMesa11.getNomeJogo());
		if (jogoSrvMesa11.isJogoCampeonato()) {
			RodadaCampeonatoMesa11 rodadaCampeonatoMesa11 = controlePersistencia
					.pesquisarRodadaPorId(
							jogoSrvMesa11.getRodadaCampeonatoMesa11());

			if (!dadosJogoSrvMesa11.getNomeCriador()
					.equals(jogoSrvMesa11.getJogadorCampeonatoCasa())) {
				return new MsgSrv(Lang.msg("jogadorCasaDeveSer", new String[]{
						jogoSrvMesa11.getJogadorCampeonatoCasa()}));
			}

			if (!dadosJogoSrvMesa11.getNomeVisitante()
					.equals(jogoSrvMesa11.getJogadorCampeonatoVisita())) {
				return new MsgSrv(Lang.msg("jogadorVisitaDeveSer", new String[]{
						jogoSrvMesa11.getJogadorCampeonatoVisita()}));
			}

		} else if (jogoSrvMesa11.getDadosJogoSrvMesa11() != null
				&& !Util.isNullOrEmpty(jogoSrvMesa11.getDadosJogoSrvMesa11()
						.getNomeVisitante())) {
			return new MsgSrv(Lang.msg("jogoInciado"));
		}
		if (jogoSrvMesa11 != null) {
			jogoSrvMesa11.setDadosJogoSrvMesa11(dadosJogoSrvMesa11);
			dadosMesa11.getJogosCriados().remove(
					jogoSrvMesa11.getDadosJogoSrvMesa11().getNomeJogo());
			dadosMesa11.getJogosAndamento()
					.add(jogoSrvMesa11.getDadosJogoSrvMesa11().getNomeJogo());
			NnpeTO mesa11to = new NnpeTO();
			mesa11to.setData(dadosJogoSrvMesa11);
			dadosJogoSrvMesa11.setJogoCampeonatoIniciado(true);
			inciaJogoServidor(jogoSrvMesa11);
			return mesa11to;
		}
		return new MsgSrv(Lang.msg("jogoInexistente"));
	}

	private void inciaJogoServidor(JogoServidor jogoSrvMesa11) {
		ControleJogo controleJogo = new ControleJogo(jogoSrvMesa11);
		DadosJogoSrvMesa11 dadosJogoSrvMesa11 = jogoSrvMesa11
				.getDadosJogoSrvMesa11();
		Time timeCasa = controlePersistencia
				.obterTime(dadosJogoSrvMesa11.getTimeCasa());
		timeCasa.setSegundoUniforme(
				dadosJogoSrvMesa11.isSegundoUniformeTimeCasa());
		Time timeVisita = controlePersistencia
				.obterTime(dadosJogoSrvMesa11.getTimeVisita());
		timeCasa.setSegundoUniforme(
				dadosJogoSrvMesa11.isSegundoUniformeTimeCasa());
		timeVisita.setSegundoUniforme(
				dadosJogoSrvMesa11.isSegundoUniformeTimeVisita());
		jogoSrvMesa11.setTimeCasa(timeCasa);
		jogoSrvMesa11.setTimeVisita(timeVisita);
		jogoSrvMesa11.setControleJogo(controleJogo);
		controleJogo.setDica("inicioJogo");
		controleJogo.iniciaJogoOnline(jogoSrvMesa11.getDadosJogoSrvMesa11(),
				timeCasa, timeVisita);
		if (dadosJogoSrvMesa11.isJogoVsCpu()) {
			timeVisita.setControladoCPU(true);
			timeCasa.setControladoCPU(false);
			controleJogo
					.setNumeroJogadas(dadosJogoSrvMesa11.getNumeroJogadas());
			AtualizadorJogadaCPU atualizadorJogadaCPU = new AtualizadorJogadaCPU(
					controleJogo);
			atualizadorJogadaCPU.start();
		}
		if (Util.isNullOrEmpty(dadosJogoSrvMesa11.getNomeCriador())) {
			timeCasa.setControladoCPU(true);
			timeVisita.setControladoCPU(false);
			controleJogo
					.setNumeroJogadas(dadosJogoSrvMesa11.getNumeroJogadas());
			AtualizadorJogadaCPU atualizadorJogadaCPU = new AtualizadorJogadaCPU(
					controleJogo);
			atualizadorJogadaCPU.start();
		}
		if (Util.isNullOrEmpty(dadosJogoSrvMesa11.getNomeVisitante())) {
			timeVisita.setControladoCPU(true);
			timeCasa.setControladoCPU(false);
			controleJogo
					.setNumeroJogadas(dadosJogoSrvMesa11.getNumeroJogadas());
			AtualizadorJogadaCPU atualizadorJogadaCPU = new AtualizadorJogadaCPU(
					controleJogo);
			atualizadorJogadaCPU.start();
		}

	}

	public Object obterDadosJogo(NnpeTO mesa11to) {
		String nomejogo = (String) mesa11to.getData();

		JogoServidor jogoSrvMesa11 = (JogoServidor) mapaJogos.get(nomejogo);
		if (jogoSrvMesa11 == null) {
			return null;
		}
		DadosJogoSrvMesa11 dadosJogoSrvMesa11 = jogoSrvMesa11
				.getDadosJogoSrvMesa11();
		ControleJogo controleJogo = jogoSrvMesa11.getControleJogo();
		if (controleJogo != null) {
			dadosJogoSrvMesa11
					.setTimeVez(controleJogo.timeJogadaVez().getNome());
			dadosJogoSrvMesa11.setGolsCasa(
					controleJogo.verGolsInt(jogoSrvMesa11.getTimeCasa()));
			dadosJogoSrvMesa11.setGolsVisita(
					controleJogo.verGolsInt(jogoSrvMesa11.getTimeVisita()));
			dadosJogoSrvMesa11
					.setTempoJogoFormatado(controleJogo.tempoJogoFormatado());
			dadosJogoSrvMesa11.setTempoRestanteJogoFormatado(
					controleJogo.tempoRestanteJogoFormatado());
			dadosJogoSrvMesa11.setTempoJogadaRestanteJogoFormatado(
					controleJogo.tempoJogadaRestanteJogoFormatado());
			dadosJogoSrvMesa11.setNumeroJogadasTimeCasa(
					controleJogo.obterNumJogadas(jogoSrvMesa11.getTimeCasa()));
			dadosJogoSrvMesa11.setNumeroJogadasTimeVisita(controleJogo
					.obterNumJogadas(jogoSrvMesa11.getTimeVisita()));
			dadosJogoSrvMesa11.setJogoTerminado(controleJogo.isJogoTerminado());
			dadosJogoSrvMesa11.setJogoIniciado(controleJogo.isJogoIniciado());
			dadosJogoSrvMesa11.setDica(controleJogo.getDica());
			dadosJogoSrvMesa11.setProcessando(controleJogo.isProcessando());
			if (controleJogo.getGolsTempo().size() > mesa11to
					.getTamListaGols()) {
				dadosJogoSrvMesa11.setGolJogador(controleJogo.getGolsTempo()
						.get(mesa11to.getTamListaGols()));
			}
		}
		mesa11to = new NnpeTO();
		mesa11to.setData(jogoSrvMesa11.getDadosJogoSrvMesa11());
		return mesa11to;
	}

	public Object jogada(JogadaMesa11 jogadaMesa11) {
		JogoServidor jogoSrvMesa11 = (JogoServidor) mapaJogos
				.get(jogadaMesa11.getDadosJogoSrvMesa11().getNomeJogo());
		if (jogoSrvMesa11 == null) {
			return null;
		}
		ControleJogo controleJogo = jogoSrvMesa11.getControleJogo();
		if (controleJogo.timeJogadaVez().getNome()
				.equals(jogadaMesa11.getTimeClienteOnline())
				&& !controleJogo.isAnimando()) {
			if (jogoSrvMesa11.getControleJogo().efetuaJogada(
					jogadaMesa11.getPontoClicado(),
					jogadaMesa11.getPontoSolto())) {
				return ConstantesMesa11.OK;
			} else {
				return null;
			}

		}
		return null;
	}

	public Object obterUltimaJogada(String nomejogo) {
		JogoServidor jogoSrvMesa11 = (JogoServidor) mapaJogos.get(nomejogo);
		if (jogoSrvMesa11 == null) {
			return null;
		}
		NnpeTO mesa11to = new NnpeTO();
		Animacao animacao = (Animacao) jogoSrvMesa11.getControleJogo()
				.obterUltimaJogada();
		animacao.setExecutou(false);
		mesa11to.setData(animacao);
		return mesa11to;
	}

	public Object obterPosicaoBotoes(String... dadosJogo) {
		JogoServidor jogoSrvMesa11 = (JogoServidor) mapaJogos.get(dadosJogo[0]);
		if (jogoSrvMesa11 == null) {
			return null;
		}
		if (jogoSrvMesa11.getControleJogo().isAnimando()) {
			return null;
		}
		long tempoUltimaJogada = Long.parseLong(dadosJogo[1]);
		jogoSrvMesa11.getControleJogo()
				.setTempoUltimaJogadaSrvCliente(tempoUltimaJogada);
		Animacao animacaoCliente = jogoSrvMesa11.getControleJogo()
				.getAnimacaoCliente();
		boolean pulaPosicaoBotoes = false;
		if (animacaoCliente != null
				&& tempoUltimaJogada < animacaoCliente.getTimeStamp()) {
			pulaPosicaoBotoes = true;
		}
		NnpeTO mesa11to = new NnpeTO();
		PosicaoBtnsSrvMesa11 posicaoBtnsSrvMesa11 = new PosicaoBtnsSrvMesa11();
		List<BotaoPosSrvMesa11> botaoPosSrvMesa11List = new ArrayList();
		for (Iterator iterator = jogoSrvMesa11.getControleJogo().getBotoes()
				.keySet().iterator(); iterator.hasNext();) {
			Long id = (Long) iterator.next();
			Botao botao = (Botao) jogoSrvMesa11.getControleJogo().getBotoes()
					.get(id);
			if (!(botao instanceof Goleiro) && pulaPosicaoBotoes) {
				continue;
			}
			BotaoPosSrvMesa11 botaoPosSrvMesa11 = new BotaoPosSrvMesa11();
			botaoPosSrvMesa11.setId(id);
			botaoPosSrvMesa11.setPos(botao.getCentro());
			if (botao instanceof Goleiro) {
				Goleiro goleiro = (Goleiro) botao;
				botaoPosSrvMesa11.setRotacao(goleiro.getRotacao());
			}
			botaoPosSrvMesa11List.add(botaoPosSrvMesa11);

		}
		posicaoBtnsSrvMesa11.setBotoes(botaoPosSrvMesa11List);
		posicaoBtnsSrvMesa11.setTimeStamp(System.currentTimeMillis());
		mesa11to.setData(posicaoBtnsSrvMesa11);
		return mesa11to;
	}

	public Map<String, JogoServidor> getMapaJogos() {
		return mapaJogos;
	}

	public void removerClienteInativo(SessaoCliente sessaoClienteRemover) {
		sairJogo(sessaoClienteRemover.getNomeJogador());
	}

	public Object sairJogo(String nomeJogador) {
		if (Util.isNullOrEmpty(nomeJogador)) {
			return null;
		}
		for (Iterator iterator = mapaJogos.keySet().iterator(); iterator
				.hasNext();) {
			String nomeJogo = (String) iterator.next();
			JogoServidor jogoServidor = mapaJogos.get(nomeJogo);
			if (nomeJogador
					.equals(jogoServidor.getDadosJogoSrvMesa11()
							.getNomeCriador())
					|| nomeJogador.equals(jogoServidor.getDadosJogoSrvMesa11()
							.getNomeVisitante())) {
				jogoServidor.jogadorSaiuJogo(nomeJogador);
			}
		}
		return null;
	}

	public Object verificaPosicaoDiffBotoes(String... dadosJogo) {
		JogoServidor jogoSrvMesa11 = (JogoServidor) mapaJogos.get(dadosJogo[0]);
		if (jogoSrvMesa11 == null) {
			return null;
		}
		if (jogoSrvMesa11.getControleJogo().isAnimando()) {
			return null;
		}
		int sumx = 0;
		int sumy = 0;
		int sumAng = 0;
		for (Iterator iterator = jogoSrvMesa11.getControleJogo().getBotoes()
				.keySet().iterator(); iterator.hasNext();) {
			Long id = (Long) iterator.next();
			Botao botao = (Botao) jogoSrvMesa11.getControleJogo().getBotoes()
					.get(id);
			sumx += botao.getCentro().x;
			sumy += botao.getCentro().y;
			sumAng += botao.getAngulo();
		}
		String somas = sumx + "" + sumy + "" + sumAng;
		if (!somas.equals(dadosJogo[1]))
			return ConstantesMesa11.OK;
		return null;
	}

	public Object gravarImagem(NnpeTO mesa11to) {
		File file = new File(ServletMesa11.mediaDir + mesa11to.getData());
		if (file.exists()) {
			return new MsgSrv("imagemExistente");
		}
		try {
			Util.byteArray2file(mesa11to.getDataBytes(),
					ServletMesa11.mediaDir + mesa11to.getData());
		} catch (Exception e) {
			Logger.logarExept(e);
		}
		return ConstantesMesa11.OK;
	}

	public Object obterTodasImagens() {
		File file = new File(ServletMesa11.mediaDir);
		try {
			NnpeTO mesa11to = new NnpeTO();
			mesa11to.setData(file.list());
			return mesa11to;
		} catch (Exception e) {
			Logger.logarExept(e);
		}
		return null;
	}

	public Object criarJogoCpu(DadosJogoSrvMesa11 dadosJogoSrvMesa11) {
		dadosJogoSrvMesa11.setNomeJogo("Jogo " + contadorJogos++);
		dadosMesa11.getJogosCriados().add(dadosJogoSrvMesa11.getNomeJogo());
		JogoServidor jogoServidor = new JogoServidor(dadosJogoSrvMesa11,
				proxyComandos);
		mapaJogos.put(dadosJogoSrvMesa11.getNomeJogo(), jogoServidor);
		JogoServidor jogoSrvMesa11 = (JogoServidor) mapaJogos
				.get(dadosJogoSrvMesa11.getNomeJogo());
		if (jogoSrvMesa11.getDadosJogoSrvMesa11() != null
				&& !Util.isNullOrEmpty(jogoSrvMesa11.getDadosJogoSrvMesa11()
						.getNomeVisitante())) {
			return new MsgSrv(Lang.msg("jogoInciado"));
		}
		if (jogoSrvMesa11 != null) {
			jogoSrvMesa11.setDadosJogoSrvMesa11(dadosJogoSrvMesa11);
			dadosMesa11.getJogosCriados().remove(
					jogoSrvMesa11.getDadosJogoSrvMesa11().getNomeJogo());
			dadosMesa11.getJogosAndamento()
					.add(jogoSrvMesa11.getDadosJogoSrvMesa11().getNomeJogo());
			NnpeTO mesa11to = new NnpeTO();
			mesa11to.setData(dadosJogoSrvMesa11);
			dadosJogoSrvMesa11.setJogoVsCpu(true);
			inciaJogoServidor(jogoSrvMesa11);
			return mesa11to;
		}
		return null;
	}

	public Object obterClassificacao() {
		return obterClassificacao(null);
	}

	public Object obterClassificacao(String campeonato) {
		Collection times = controlePersistencia.obterTimesPartidas(campeonato);
		Map mapaCassificTime = new HashMap();
		for (Iterator iterator = times.iterator(); iterator.hasNext();) {
			String time = (String) iterator.next();
			ClassificacaoTime classificacaoTime = (ClassificacaoTime) mapaCassificTime
					.get(time);
			if (classificacaoTime == null) {
				classificacaoTime = new ClassificacaoTime();
				classificacaoTime.setTime(time);
				mapaCassificTime.put(time, classificacaoTime);
			}
			List partidasCasa = controlePersistencia.obterPartidasTimeCasa(time,
					campeonato);
			for (Iterator iterator2 = partidasCasa.iterator(); iterator2
					.hasNext();) {
				PartidaMesa11 partidaMesa11 = (PartidaMesa11) iterator2.next();
				classificacaoTime.setJogos(classificacaoTime.getJogos() + 1);
				if (partidaMesa11.getGolsTimeCasa() > partidaMesa11
						.getGolsTimeVisita()) {
					classificacaoTime
							.setVitorias(classificacaoTime.getVitorias() + 1);
				} else if (partidaMesa11.getGolsTimeCasa() == partidaMesa11
						.getGolsTimeVisita()) {
					classificacaoTime
							.setEmpates(classificacaoTime.getEmpates() + 1);
				} else {
					classificacaoTime
							.setDerrotas(classificacaoTime.getDerrotas() + 1);
				}
				classificacaoTime.setGolsFavor(classificacaoTime.getGolsFavor()
						+ partidaMesa11.getGolsTimeCasa());
				classificacaoTime
						.setGolsContra(classificacaoTime.getGolsContra()
								+ partidaMesa11.getGolsTimeVisita());
			}

		}

		for (Iterator iterator = times.iterator(); iterator.hasNext();) {
			String time = (String) iterator.next();
			ClassificacaoTime classificacaoTime = (ClassificacaoTime) mapaCassificTime
					.get(time);
			if (classificacaoTime == null) {
				classificacaoTime = new ClassificacaoTime();
				classificacaoTime.setTime(time);
				mapaCassificTime.put(time, classificacaoTime);
			}
			List partidasCasa = controlePersistencia
					.obterPartidasTimeVisita(time, campeonato);
			for (Iterator iterator2 = partidasCasa.iterator(); iterator2
					.hasNext();) {
				PartidaMesa11 partidaMesa11 = (PartidaMesa11) iterator2.next();
				classificacaoTime.setJogos(classificacaoTime.getJogos() + 1);
				if (partidaMesa11.getGolsTimeCasa() < partidaMesa11
						.getGolsTimeVisita()) {
					classificacaoTime
							.setVitorias(classificacaoTime.getVitorias() + 1);
				} else if (partidaMesa11.getGolsTimeCasa() == partidaMesa11
						.getGolsTimeVisita()) {
					classificacaoTime
							.setEmpates(classificacaoTime.getEmpates() + 1);
				} else {
					classificacaoTime
							.setDerrotas(classificacaoTime.getDerrotas() + 1);
				}
				classificacaoTime.setGolsFavor(classificacaoTime.getGolsFavor()
						+ partidaMesa11.getGolsTimeVisita());
				classificacaoTime
						.setGolsContra(classificacaoTime.getGolsContra()
								+ partidaMesa11.getGolsTimeCasa());
			}

		}
		List dadosTimes = new ArrayList();
		for (Iterator iterator = mapaCassificTime.keySet().iterator(); iterator
				.hasNext();) {
			String timeKey = (String) iterator.next();
			dadosTimes.add(mapaCassificTime.get(timeKey));
		}
		Collections.sort(dadosTimes, new Comparator() {
			@Override
			public int compare(Object o1, Object o2) {
				ClassificacaoTime c1 = (ClassificacaoTime) o1;
				ClassificacaoTime c2 = (ClassificacaoTime) o2;
				String val1 = "" + c1.getVitorias() + c1.getEmpates()
						+ c1.getSaldoGols() + c1.getGolsFavor() + c1.getJogos();
				String val2 = "" + c2.getVitorias() + c2.getEmpates()
						+ c2.getSaldoGols() + c2.getGolsFavor() + c2.getJogos();
				return val2.compareTo(val1);
			}
		});
		Map returnMap = new HashMap();
		returnMap.put(ConstantesMesa11.VER_CLASSIFICACAO_TIMES, dadosTimes);

		Collection jogadores = controlePersistencia
				.obterJogadoresPartidas(campeonato);
		Map mapaCassificJogador = new HashMap();
		for (Iterator iterator = jogadores.iterator(); iterator.hasNext();) {
			String usuario = (String) iterator.next();
			ClassificacaoUsuario classificacaoUsuario = (ClassificacaoUsuario) mapaCassificJogador
					.get(usuario);
			if (classificacaoUsuario == null) {
				classificacaoUsuario = new ClassificacaoUsuario();
				classificacaoUsuario.setLogin(usuario);
				mapaCassificJogador.put(usuario, classificacaoUsuario);
			}
			List partidasCasa = controlePersistencia
					.obterPartidasJogadorCasa(usuario, campeonato);
			for (Iterator iterator2 = partidasCasa.iterator(); iterator2
					.hasNext();) {
				PartidaMesa11 partidaMesa11 = (PartidaMesa11) iterator2.next();
				classificacaoUsuario
						.setJogos(classificacaoUsuario.getJogos() + 1);
				if (partidaMesa11.getGolsTimeCasa() > partidaMesa11
						.getGolsTimeVisita()) {
					classificacaoUsuario.setVitorias(
							classificacaoUsuario.getVitorias() + 1);
				} else if (partidaMesa11.getGolsTimeCasa() == partidaMesa11
						.getGolsTimeVisita()) {
					classificacaoUsuario
							.setEmpates(classificacaoUsuario.getEmpates() + 1);
				} else {
					classificacaoUsuario.setDerrotas(
							classificacaoUsuario.getDerrotas() + 1);
				}
				classificacaoUsuario
						.setGolsFavor(classificacaoUsuario.getGolsFavor()
								+ partidaMesa11.getGolsTimeCasa());
				classificacaoUsuario
						.setGolsContra(classificacaoUsuario.getGolsContra()
								+ partidaMesa11.getGolsTimeVisita());
			}

		}

		for (Iterator iterator = jogadores.iterator(); iterator.hasNext();) {
			String usuario = (String) iterator.next();
			ClassificacaoUsuario classificacaoUsuario = (ClassificacaoUsuario) mapaCassificJogador
					.get(usuario);
			if (classificacaoUsuario == null) {
				classificacaoUsuario = new ClassificacaoUsuario();
				classificacaoUsuario.setLogin(usuario);
				mapaCassificJogador.put(usuario, classificacaoUsuario);
			}
			List partidasCasa = controlePersistencia
					.obterPartidasJogadorVisita(usuario, campeonato);
			for (Iterator iterator2 = partidasCasa.iterator(); iterator2
					.hasNext();) {
				PartidaMesa11 partidaMesa11 = (PartidaMesa11) iterator2.next();
				classificacaoUsuario
						.setJogos(classificacaoUsuario.getJogos() + 1);
				if (partidaMesa11.getGolsTimeCasa() < partidaMesa11
						.getGolsTimeVisita()) {
					classificacaoUsuario.setVitorias(
							classificacaoUsuario.getVitorias() + 1);
				} else if (partidaMesa11.getGolsTimeCasa() == partidaMesa11
						.getGolsTimeVisita()) {
					classificacaoUsuario
							.setEmpates(classificacaoUsuario.getEmpates() + 1);
				} else {
					classificacaoUsuario.setDerrotas(
							classificacaoUsuario.getDerrotas() + 1);
				}
				classificacaoUsuario
						.setGolsFavor(classificacaoUsuario.getGolsFavor()
								+ partidaMesa11.getGolsTimeVisita());
				classificacaoUsuario
						.setGolsContra(classificacaoUsuario.getGolsContra()
								+ partidaMesa11.getGolsTimeCasa());
			}

		}
		List dadosJogadores = new ArrayList();
		for (Iterator iterator = mapaCassificJogador.keySet()
				.iterator(); iterator.hasNext();) {
			String loginKey = (String) iterator.next();
			dadosJogadores.add(mapaCassificJogador.get(loginKey));
		}
		Collections.sort(dadosJogadores, new Comparator() {
			@Override
			public int compare(Object o1, Object o2) {
				ClassificacaoUsuario c1 = (ClassificacaoUsuario) o1;
				ClassificacaoUsuario c2 = (ClassificacaoUsuario) o2;
				String val1 = "" + c1.getVitorias() + c1.getEmpates()
						+ c1.getSaldoGols() + c1.getGolsFavor() + c1.getJogos();
				String val2 = "" + c2.getVitorias() + c2.getEmpates()
						+ c2.getSaldoGols() + c2.getGolsFavor() + c2.getJogos();
				return val2.compareTo(val1);
			}
		});
		returnMap.put(ConstantesMesa11.VER_CLASSIFICACAO_JOGADORES,
				dadosJogadores);
		NnpeTO mesa11to = new NnpeTO();
		mesa11to.setData(returnMap);
		return mesa11to;
	}

	public Object verRodada(CampeonatoMesa11 data) {
		NnpeTO mesa11to = new NnpeTO();
		List list = controlePersistencia.verRodada(data);
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			RodadaCampeonatoMesa11 rodadaCampeonatoMesa11 = (RodadaCampeonatoMesa11) iterator
					.next();
			rodadaCampeonatoMesa11.setCampeonatoMesa11(null);
			rodadaCampeonatoMesa11.getTimeCasa().setBotoes(null);
			rodadaCampeonatoMesa11.getTimeVisita().setBotoes(null);
		}
		mesa11to.setData(list);
		return mesa11to;
	}

	public Object obterJogadoresCampeonato(String campeonato) {
		List res = controlePersistencia.obterJogadoresCampeonato(campeonato);
		List jogadores = new ArrayList();
		for (Iterator iterator = res.iterator(); iterator.hasNext();) {
			JogadoresCampeonatoMesa11 jogadoresCampeonatoMesa11 = (JogadoresCampeonatoMesa11) iterator
					.next();
			jogadores.add(jogadoresCampeonatoMesa11.getUsuario().getLogin());
		}
		NnpeTO mesa11to = new NnpeTO();
		mesa11to.setData(jogadores);
		return mesa11to;
	}

	public Object criarJogoCampeonato(NnpeTO mesa11TO) {
		DadosJogoSrvMesa11 dadosJogoSrvMesa11 = (DadosJogoSrvMesa11) mesa11TO
				.getData();

		if (!Util.isNullOrEmpty(dadosJogoSrvMesa11.getNomeCriador())
				&& !Util.isNullOrEmpty(dadosJogoSrvMesa11.getNomeVisitante())
				&& dadosJogoSrvMesa11.getNomeCriador()
						.equals(dadosJogoSrvMesa11.getNomeVisitante())) {
			return new MsgSrv(Lang.msg("jogadoresIguais"));

		}

		Set<String> keySet = mapaJogos.keySet();
		for (Iterator iterator = keySet.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			JogoServidor jogoServidor = mapaJogos.get(key);
			if (jogoServidor.isJogoCampeonato() && dadosJogoSrvMesa11
					.getIdRodadaCampeonato() == jogoServidor
							.getRodadaCampeonatoMesa11()) {
				return new MsgSrv(Lang.msg("jogoRodadaJaEmAdamento",
						new String[]{dadosJogoSrvMesa11.getTimeCasa(),
								dadosJogoSrvMesa11.getTimeVisita()}));
			}

		}

		if (!controlePersistencia.verificaUsuarioCampeonato(
				mesa11TO.getSessaoCliente().getNomeJogador(),
				dadosJogoSrvMesa11.getIdRodadaCampeonato())) {
			return new MsgSrv(
					Lang.msg("usuarioNaoParticipaCampeonato", new String[]{
							mesa11TO.getSessaoCliente().getNomeJogador()}));
		}

		if (controlePersistencia.verificaRodadaFinalizada(
				dadosJogoSrvMesa11.getIdRodadaCampeonato())) {
			return new MsgSrv(Lang.msg("jogoRodadaJaAconteceu",
					new String[]{dadosJogoSrvMesa11.getTimeCasa(),
							dadosJogoSrvMesa11.getTimeVisita()}));
		}

		if (Util.isNullOrEmpty(dadosJogoSrvMesa11.getNomeCriador())
				&& Util.isNullOrEmpty(dadosJogoSrvMesa11.getNomeVisitante())) {
			return jogoCampeonatoCPUvsCPU(dadosJogoSrvMesa11,
					mesa11TO.getSessaoCliente().getNomeJogador());
		}

		if (!Util.isNullOrEmpty(dadosJogoSrvMesa11.getNomeCriador())) {
			if (proxyComandos
					.verificaSemSessao(dadosJogoSrvMesa11.getNomeCriador()))
				return new MsgSrv(Lang.msg("usuarioNaoLogado",
						new String[]{dadosJogoSrvMesa11.getNomeCriador()}));
		}
		if (!Util.isNullOrEmpty(dadosJogoSrvMesa11.getNomeVisitante())) {
			if (proxyComandos
					.verificaSemSessao(dadosJogoSrvMesa11.getNomeVisitante()))
				return new MsgSrv(Lang.msg("usuarioNaoLogado",
						new String[]{dadosJogoSrvMesa11.getNomeVisitante()}));
		}

		if (!Util.isNullOrEmpty(dadosJogoSrvMesa11.getNomeCriador())
				&& !Util.isNullOrEmpty(dadosJogoSrvMesa11.getNomeVisitante())
				&& !mesa11TO.getSessaoCliente().getNomeJogador()
						.equals(dadosJogoSrvMesa11.getNomeCriador())) {
			return new MsgSrv(Lang.msg("jogadorCriadorJogadorCasa"));
		}

		dadosJogoSrvMesa11.setNomeJogo("Jogo " + contadorJogos++);
		dadosMesa11.getJogosCriados().add(dadosJogoSrvMesa11.getNomeJogo());
		JogoServidor jogoServidor = new JogoServidor(dadosJogoSrvMesa11,
				proxyComandos);
		jogoServidor.setRodadaCampeonatoMesa11(
				dadosJogoSrvMesa11.getIdRodadaCampeonato());
		mapaJogos.put(dadosJogoSrvMesa11.getNomeJogo(), jogoServidor);
		JogoServidor jogoSrvMesa11 = (JogoServidor) mapaJogos
				.get(dadosJogoSrvMesa11.getNomeJogo());
		if (jogoSrvMesa11 != null) {
			NnpeTO mesa11to = new NnpeTO();
			mesa11to.setData(dadosJogoSrvMesa11);
			if (Util.isNullOrEmpty(dadosJogoSrvMesa11.getNomeCriador()) || Util
					.isNullOrEmpty(dadosJogoSrvMesa11.getNomeVisitante())) {
				jogoSrvMesa11.setDadosJogoSrvMesa11(dadosJogoSrvMesa11);
				dadosMesa11.getJogosCriados().remove(
						jogoSrvMesa11.getDadosJogoSrvMesa11().getNomeJogo());
				dadosMesa11.getJogosAndamento().add(
						jogoSrvMesa11.getDadosJogoSrvMesa11().getNomeJogo());
				dadosJogoSrvMesa11.setJogoCampeonatoIniciado(true);
				inciaJogoServidor(jogoSrvMesa11);
			} else {
				jogoServidor.setJogadorCampeonatoCasa(
						dadosJogoSrvMesa11.getNomeCriador());
				jogoServidor.setJogadorCampeonatoVisita(
						dadosJogoSrvMesa11.getNomeVisitante());
			}
			return mesa11to;
		}
		return null;
	}

	private Object jogoCampeonatoCPUvsCPU(DadosJogoSrvMesa11 dadosJogoSrvMesa11,
			String criador) {
		Time timeCasa = controlePersistencia
				.obterTime(dadosJogoSrvMesa11.getTimeCasa());
		Time timeVisita = controlePersistencia
				.obterTime(dadosJogoSrvMesa11.getTimeVisita());
		int gols = Util.intervalo(0, 5);
		RodadaCampeonatoMesa11 rodadaCampeonatoMesa11 = null;
		PartidaMesa11 partidaMesa11 = new PartidaMesa11();
		partidaMesa11.setNomeJogadorCasa("CPU");
		partidaMesa11.setNomeJogadorVisita("CPU");
		partidaMesa11.setNomeTimeCasa(timeCasa.getNome());
		partidaMesa11.setNomeTimeVisita(timeVisita.getNome());
		partidaMesa11.setLoginCriador(criador);
		partidaMesa11.setInicio(new Date(System.currentTimeMillis()));
		partidaMesa11.setFim(new Date(System.currentTimeMillis()));
		double fatorCasa = 0;
		double fatorVisita = 0;
		List<Botao> botoesCasaList = timeCasa.getBotoes();
		for (Iterator iterator = botoesCasaList.iterator(); iterator
				.hasNext();) {
			Botao botao = (Botao) iterator.next();
			fatorCasa += botao.getDefesa() + botao.getPrecisao()
					+ botao.getForca();
		}
		List<Botao> botoesVisitaList = timeVisita.getBotoes();
		for (Iterator iterator = botoesVisitaList.iterator(); iterator
				.hasNext();) {
			Botao botao = (Botao) iterator.next();
			fatorVisita += botao.getDefesa() + botao.getPrecisao()
					+ botao.getForca();
		}
		fatorCasa /= Util.intervalo(28000.0, 36000.0);
		fatorVisita /= Util.intervalo(28000.0, 36000.0);
		Logger.logar("fatorCasa " + fatorCasa);
		Logger.logar("fatorVisita " + fatorVisita);
		for (int i = 0; i < gols; i++) {
			if (Math.random() < fatorCasa) {
				partidaMesa11
						.setGolsTimeCasa(partidaMesa11.getGolsTimeCasa() + 1);
			}
			if (Math.random() < fatorVisita) {
				partidaMesa11.setGolsTimeVisita(
						partidaMesa11.getGolsTimeVisita() + 1);
			}
		}
		if (dadosJogoSrvMesa11.getIdRodadaCampeonato() != 0) {
			rodadaCampeonatoMesa11 = proxyComandos.pesquisarRodadaPorId(
					dadosJogoSrvMesa11.getIdRodadaCampeonato());
			partidaMesa11.setCampeonato(
					rodadaCampeonatoMesa11.getCampeonatoMesa11().getNome());
			rodadaCampeonatoMesa11.setGolsCasa(partidaMesa11.getGolsTimeCasa());
			rodadaCampeonatoMesa11
					.setGolsVisita(partidaMesa11.getGolsTimeVisita());

			rodadaCampeonatoMesa11.setRodadaEfetuda(true);
		}

		try {
			controlePersistencia.gravarDados(partidaMesa11,
					rodadaCampeonatoMesa11);
		} catch (Exception e) {
			return new ErroServ(e);
		} finally {
			HibernateUtil.closeSession();
		}
		return new MsgSrv(Lang.msg("resultadoJogoCampeonato",
				new String[]{partidaMesa11.getNomeTimeCasa(),
						"" + partidaMesa11.getGolsTimeCasa(),
						"" + partidaMesa11.getGolsTimeVisita(),
						partidaMesa11.getNomeTimeVisita()}));
	}

	public Object jogadaAssitida(JogadaMesa11 jogadaMesa11) {
		JogoServidor jogoSrvMesa11 = (JogoServidor) mapaJogos
				.get(jogadaMesa11.getDadosJogoSrvMesa11().getNomeJogo());
		if (jogoSrvMesa11 == null) {
			return null;
		}
		ControleJogo controleJogo = jogoSrvMesa11.getControleJogo();
		if (controleJogo.timeJogadaVez().getNome()
				.equals(jogadaMesa11.getTimeClienteOnline())
				&& !controleJogo.isAnimando()) {
			controleJogo.setBotaoSelecionado(controleJogo.getBotoes()
					.get(jogadaMesa11.getIdBtnJogadaAssitida()));
			controleJogo.setarBtnAssistido();
		}
		return null;
	}
}
