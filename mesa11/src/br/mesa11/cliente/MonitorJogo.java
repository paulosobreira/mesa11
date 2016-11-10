package br.mesa11.cliente;

import java.util.Vector;

import br.applet.Mesa11Applet;
import br.hibernate.Time;
import br.mesa11.ConstantesMesa11;
import br.mesa11.conceito.Animacao;
import br.mesa11.conceito.ControleJogo;
import br.nnpe.Logger;
import br.nnpe.Util;
import br.nnpe.tos.NnpeTO;
import br.tos.DadosJogoSrvMesa11;

public class MonitorJogo extends Thread {
	private ControleChatCliente controleChatCliente;
	private DadosJogoSrvMesa11 dadosJogoSrvMesa11;
	private ControleJogo controleJogo;
	private Mesa11Applet mesa11Applet;
	private String timeClienteOnline;
	private Long indexProxJogada = 0l;
	private boolean jogoTerminado;
	private Vector<Animacao> bufferAnimacao = new Vector<Animacao>();
	private Vector<Animacao> animacoesExecutadas = new Vector<Animacao>();

	public MonitorJogo(ControleChatCliente controleChatCliente,
			ControleJogosCliente controleJogosCliente,
			DadosJogoSrvMesa11 dadosJogoSrvMesa11, Mesa11Applet mesa11Applet,
			String timeClienteOnline) {
		super();
		this.controleChatCliente = controleChatCliente;
		this.dadosJogoSrvMesa11 = dadosJogoSrvMesa11;
		this.mesa11Applet = mesa11Applet;
		this.timeClienteOnline = timeClienteOnline;
	}

	@Override
	public void run() {
		boolean interrrupt = false;
		while (!jogoTerminado && !interrrupt) {
			try {
				if (controleJogo == null && timesSelecionados()) {
					iniciaJogo();
				} else {
					dormir(1000);
				}
				if (controleJogo != null) {
					obterDadosJogo();
					if (dadosJogoSrvMesa11.isJogoTerminado()) {
						Logger.logar("Fim de jogo");
						controleJogo.setDica("fimJogo");
						controleJogo.setEsperandoJogadaOnline(false);
						jogoTerminado = true;
						controleJogo.setJogoTerminado(true);
					} else {
						consumirJogada();
					}
					dormir(150);
				}
			} catch (InterruptedException e) {
				interrrupt = true;
				Logger.logarExept(e);
			}
		}

	}

	private void dormir(long i) throws InterruptedException {
		sleep(i);
	}

	private void obterDadosJogo() throws InterruptedException {
		NnpeTO mesa11to = new NnpeTO();
		mesa11to.setComando(ConstantesMesa11.OBTER_DADOS_JOGO);
		mesa11to.setData(
				dadosJogoSrvMesa11.getNomeJogo());
		mesa11to.setIndexProxJogada(indexProxJogada);
		mesa11to.setTamListaGols(controleJogo.getGolsTempo().size());
		Object ret = enviarObjeto(mesa11to);
		if (ret == null) {
			return;
		}
		if (ret instanceof NnpeTO) {
			mesa11to = (NnpeTO) ret;
			dadosJogoSrvMesa11 = (DadosJogoSrvMesa11) mesa11to.getData();
			if (dadosJogoSrvMesa11 == null) {
				return;
			}
			controleJogo.setDadosJogoSrvMesa11(dadosJogoSrvMesa11);
			if (dadosJogoSrvMesa11.getGolJogador() != null
					&& !controleJogo.getGolsTempo()
							.contains(dadosJogoSrvMesa11.getGolJogador())) {
				controleJogo.getGolsTempo()
						.add(dadosJogoSrvMesa11.getGolJogador());
			}
			controleJogo.setDadosJogoSrvMesa11(dadosJogoSrvMesa11);

			Animacao animacao = dadosJogoSrvMesa11.getAnimacao();
			if (animacao == null) {
				return;
			}
			if (bufferAnimacao.contains(animacao)
					|| animacoesExecutadas.contains(animacao)) {
				return;
			}
			bufferAnimacao.addElement(animacao);
			Logger.logar("Obteve jogada " + indexProxJogada);
			indexProxJogada++;
		}
	}

	private void consumirJogada() {
		if (controleJogo.isAnimando()) {
			return;
		}
		if (bufferAnimacao.isEmpty()) {
			controleJogo.setEsperandoJogadaOnline(false);
			return;
		}
		Animacao animacaoVez = bufferAnimacao.remove(0);
		animacoesExecutadas.addElement(animacaoVez);
		if (animacaoVez.getPosicaoBtnsSrvMesa11() != null) {
			controleJogo.atualizaPosicoesBotoes(
					animacaoVez.getPosicaoBtnsSrvMesa11());
			if (controleJogo.verificaBolaCentro()) {
				controleJogo.centralizaBola();
			} else {
				for (int i = 0; i < 30; i++) {
					controleJogo.centralizaBotao(controleJogo.getBola());
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			Logger.logar("Centralizou animacao " + animacaoVez.getSequencia());
		}
		controleJogo.setDica(animacaoVez.getDica());
		controleJogo.executaAnimacao(animacaoVez);
		controleJogo.setPontoClicado(null);
		controleJogo.zeraBtnAssistido();
		controleJogo.setEsperandoJogadaOnline(true);
		Logger.logar("Executado animacao " + animacaoVez.getSequencia());
	}

	private void iniciaJogo() {
		NnpeTO mesa11to = new NnpeTO();
		mesa11to.setData(dadosJogoSrvMesa11.getTimeCasa());
		mesa11to.setComando(ConstantesMesa11.OBTER_TIME);
		Object ret = enviarObjeto(mesa11to);
		mesa11to = (NnpeTO) ret;
		Time timeCasa = (Time) mesa11to.getData();
		timeCasa.setSegundoUniforme(
				dadosJogoSrvMesa11.isSegundoUniformeTimeCasa());
		mesa11to = new NnpeTO();
		mesa11to.setData(dadosJogoSrvMesa11.getTimeVisita());
		mesa11to.setComando(ConstantesMesa11.OBTER_TIME);
		ret = enviarObjeto(mesa11to);
		mesa11to = (NnpeTO) ret;
		Time timeVisita = (Time) mesa11to.getData();
		timeVisita.setSegundoUniforme(
				dadosJogoSrvMesa11.isSegundoUniformeTimeVisita());
		controleJogo = new ControleJogo(mesa11Applet, timeClienteOnline,
				dadosJogoSrvMesa11,
				controleChatCliente.getSessaoCliente().getNomeJogador());
		controleJogo.iniciaJogoOnline(dadosJogoSrvMesa11, timeCasa, timeVisita);
		controleJogo.inicializaVideo();
		controleJogo.centroCampo();
		controleJogo.setZoom(0.4);
	}

	private boolean timesSelecionados() {
		NnpeTO mesa11to = new NnpeTO();
		mesa11to.setComando(ConstantesMesa11.OBTER_DADOS_JOGO);
		mesa11to.setData(dadosJogoSrvMesa11.getNomeJogo());
		Object ret = enviarObjeto(mesa11to);
		if (ret instanceof NnpeTO) {
			mesa11to = (NnpeTO) ret;
			dadosJogoSrvMesa11 = (DadosJogoSrvMesa11) mesa11to.getData();
			if (dadosJogoSrvMesa11.getIdRodadaCampeonato() != 0) {
				return dadosJogoSrvMesa11.isJogoCampeonatoIniciado();
			}
			if (!Util.isNullOrEmpty(dadosJogoSrvMesa11.getTimeCasa()) && !Util
					.isNullOrEmpty(dadosJogoSrvMesa11.getTimeVisita())) {
				return true;
			}
		}
		return false;
	}

	private Object enviarObjeto(NnpeTO mesa11to) {
		if (mesa11Applet == null) {
			Logger.logar("enviarObjeto mesa11Applet null");
			return null;
		}
		return mesa11Applet.enviarObjeto(mesa11to);
	}

	public void setJogoTerminado(boolean jogoTerminado) {
		this.jogoTerminado = jogoTerminado;
	}

	@Override
	public void interrupt() {
		controleJogo.setDica("fimJogo");
		super.interrupt();
	}
}
