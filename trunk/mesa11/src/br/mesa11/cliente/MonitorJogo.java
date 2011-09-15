package br.mesa11.cliente;

import br.applet.Mesa11Applet;
import br.hibernate.Time;
import br.mesa11.ConstantesMesa11;
import br.mesa11.conceito.Animacao;
import br.mesa11.conceito.ControleJogo;
import br.mesa11.servidor.JogoServidor;
import br.nnpe.Logger;
import br.nnpe.Util;
import br.tos.DadosJogoSrvMesa11;
import br.tos.Mesa11TO;
import br.tos.PosicaoBtnsSrvMesa11;

public class MonitorJogo extends Thread {
	private ControleChatCliente controleChatCliente;
	private DadosJogoSrvMesa11 dadosJogoSrvMesa11;
	private ControleJogo controleJogo;
	private Mesa11Applet mesa11Applet;
	private String timeClienteOnline;
	private String timeVez;
	private long tempoDormir = 1000;
	private long timeStampAnimacao;
	private boolean jogoTerminado;
	private int erroComunic = 0;
	private Thread threadGol;

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
		while (!jogoTerminado) {
			try {
				dormir(tempoDormir);
				if (timesSelecionados() && controleJogo == null) {
					Logger.logar("Antes iniciaJogo();");
					iniciaJogo();
					Logger.logar("Depois iniciaJogo();");
				}
				if (controleJogo != null) {
					Logger.logar("Antes atualizaDadosJogoSrvMesa11();");
					atualizaDadosJogoSrvMesa11();
					Logger.logar("Depois atualizaDadosJogoSrvMesa11();");
					jogoTerminado = controleJogo.isJogoTerminado();
					Logger
							.logar("jogoTerminado = controleJogo.isJogoTerminado();");
					if (erroComunic > 20) {
						jogoTerminado = true;
					}
					if (jogoTerminado) {
						controleJogo.setDica("fimJogo");
					}
				}
				Logger.logar("controleChatCliente.getLatenciaReal() ");
				if (controleChatCliente.getLatenciaReal() > 500
						&& controleJogo != null) {
					Logger.logar("controleJogo.setProblemasRede(true);");
					controleJogo.setProblemasRede(true);
				} else {
					controleJogo.setProblemasRede(false);
				}
			} catch (Exception e) {
				Logger.logarExept(e);
			}
		}
		controleJogo.setDica("fimJogo");
	}

	private void dormir(long i) {
		try {
			sleep(i);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void atualizaDadosJogoSrvMesa11() {
		Mesa11TO mesa11to = new Mesa11TO();
		mesa11to.setComando(ConstantesMesa11.OBTER_DADOS_JOGO);
		mesa11to.setData(dadosJogoSrvMesa11.getNomeJogo());
		mesa11to.setTamListaGols(controleJogo.getGolsTempo().size());
		Object ret = enviarObjeto(mesa11to);
		if (ret instanceof Mesa11TO) {
			mesa11to = (Mesa11TO) ret;
			dadosJogoSrvMesa11 = (DadosJogoSrvMesa11) mesa11to.getData();
			if (dadosJogoSrvMesa11.getGolJogador() != null
					&& !controleJogo.getGolsTempo().contains(
							dadosJogoSrvMesa11.getGolJogador())) {
				controleJogo.getGolsTempo().add(
						dadosJogoSrvMesa11.getGolJogador());
			}
			controleJogo.setDadosJogoSrvMesa11(dadosJogoSrvMesa11);
			if ("gol".equals(dadosJogoSrvMesa11.getDica())
					|| "intervalo".equals(dadosJogoSrvMesa11.getDica())
					|| "golContra".equals(dadosJogoSrvMesa11.getDica())
					|| "meta".equals(dadosJogoSrvMesa11.getDica())
					|| "escanteio".equals(dadosJogoSrvMesa11.getDica())
					|| "penalti".equals(dadosJogoSrvMesa11.getDica())
					|| "falta".equals(dadosJogoSrvMesa11.getDica())) {
				if ("gol".equals(dadosJogoSrvMesa11.getDica())
						|| "golContra".equals(dadosJogoSrvMesa11.getDica())) {
					Logger
							.logar("controleJogo.getMesaPanel().setDesenhaGol();");
					threadGol = new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								Thread.sleep(1500);
							} catch (InterruptedException e) {
								Logger.logarExept(e);
							}
							controleJogo.getMesaPanel().setDesenhaGol();
						}
					});
					if (threadGol != null && !threadGol.isAlive()) {
						dadosJogoSrvMesa11.setDica(null);
						threadGol.start();
					}

				}
				controleJogo.centralizaBola();
			}
			if (timeVez != null
					&& !timeVez.equals(dadosJogoSrvMesa11.getTimeVez())
					&& controleJogo != null && !controleJogo.isAnimando()
					&& !controleJogo.isEsperandoJogadaOnline()
					&& controleJogo.verificaPosicaoDiffBotoes()) {
				Logger.logar("atualizaBotoesClienteOnline(");
				controleJogo.atualizaBotoesClienteOnline(
						this.timeStampAnimacao, true);
			}

			timeVez = dadosJogoSrvMesa11.getTimeVez();
			if (erroComunic >= 0) {
				erroComunic--;
			}
		} else {
			erroComunic++;
		}
		if (dadosJogoSrvMesa11 == null) {
			return;
		}
		dormir(tempoDormir);
		mesa11to = new Mesa11TO();
		mesa11to.setComando(ConstantesMesa11.OBTER_ULTIMA_JOGADA);
		mesa11to.setData(dadosJogoSrvMesa11.getNomeJogo());
		ret = enviarObjeto(mesa11to);
		if (ret != null && ret instanceof Mesa11TO) {
			mesa11to = (Mesa11TO) ret;
			Animacao animacao = (Animacao) mesa11to.getData();
			if (!controleJogo.isAnimando() && animacao != null
					&& animacao.getTimeStamp() > timeStampAnimacao) {
				timeStampAnimacao = animacao.getTimeStamp();
				controleJogo.executaAnimacao(animacao);
			}
			if (erroComunic >= 0) {
				erroComunic--;
			}
		} else {
			erroComunic++;
		}
	}

	private void iniciaJogo() {
		Mesa11TO mesa11to = new Mesa11TO();
		mesa11to.setData(dadosJogoSrvMesa11.getTimeCasa());
		mesa11to.setComando(ConstantesMesa11.OBTER_TIME);
		Object ret = enviarObjeto(mesa11to);
		mesa11to = (Mesa11TO) ret;
		Time timeCasa = (Time) mesa11to.getData();
		timeCasa.setSegundoUniforme(dadosJogoSrvMesa11
				.isSegundoUniformeTimeCasa());
		mesa11to = new Mesa11TO();
		mesa11to.setData(dadosJogoSrvMesa11.getTimeVisita());
		mesa11to.setComando(ConstantesMesa11.OBTER_TIME);
		ret = enviarObjeto(mesa11to);
		mesa11to = (Mesa11TO) ret;
		Time timeVisita = (Time) mesa11to.getData();
		timeVisita.setSegundoUniforme(dadosJogoSrvMesa11
				.isSegundoUniformeTimeVisita());
		controleJogo = new ControleJogo(mesa11Applet, timeClienteOnline,
				dadosJogoSrvMesa11, controleChatCliente.getSessaoCliente()
						.getNomeJogador());
		controleJogo.iniciaJogoOnline(dadosJogoSrvMesa11, timeCasa, timeVisita);
		controleJogo.inicializaVideo();
		controleJogo.centroCampo();
		controleJogo.setZoom(0.3);
		tempoDormir = 500;

	}

	private boolean timesSelecionados() {
		Mesa11TO mesa11to = new Mesa11TO();
		mesa11to.setComando(ConstantesMesa11.OBTER_DADOS_JOGO);
		Logger.logar("dadosJogoSrvMesa11.getNomeJogo()"
				+ dadosJogoSrvMesa11.getNomeJogo());
		mesa11to.setData(dadosJogoSrvMesa11.getNomeJogo());
		Object ret = enviarObjeto(mesa11to);
		if (ret instanceof Mesa11TO) {
			mesa11to = (Mesa11TO) ret;
			dadosJogoSrvMesa11 = (DadosJogoSrvMesa11) mesa11to.getData();
			Logger
					.logar("dadosJogoSrvMesa11 = (DadosJogoSrvMesa11) mesa11to.getData();"
							+ dadosJogoSrvMesa11);
			if (!Util.isNullOrEmpty(dadosJogoSrvMesa11.getTimeCasa())
					&& !Util.isNullOrEmpty(dadosJogoSrvMesa11.getTimeVisita())) {
				return true;
			}
		}
		return false;
	}

	private Object enviarObjeto(Mesa11TO mesa11to) {
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
