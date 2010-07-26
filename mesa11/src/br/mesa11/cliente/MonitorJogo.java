package br.mesa11.cliente;

import br.applet.Mesa11Applet;
import br.hibernate.Time;
import br.mesa11.ConstantesMesa11;
import br.mesa11.conceito.Animacao;
import br.mesa11.conceito.ControleJogo;
import br.nnpe.Logger;
import br.nnpe.Util;
import br.tos.DadosJogoSrvMesa11;
import br.tos.Mesa11TO;
import br.tos.PosicaoBtnsSrvMesa11;

public class MonitorJogo extends Thread {
	private ControleChatCliente controleChatCliente;
	private ControleJogosCliente controleJogosCliente;
	private DadosJogoSrvMesa11 dadosJogoSrvMesa11;
	private ControleJogo controleJogo;
	private Mesa11Applet mesa11Applet;
	private String timeClienteOnline;
	private int indexAnimacaoAtual = 0;
	private long tempoDormir = 1000;
	private long timeStampPosicoes;

	public MonitorJogo(ControleChatCliente controleChatCliente,
			ControleJogosCliente controleJogosCliente,
			DadosJogoSrvMesa11 dadosJogoSrvMesa11, Mesa11Applet mesa11Applet,
			String timeClienteOnline) {
		super();
		this.controleChatCliente = controleChatCliente;
		this.controleJogosCliente = controleJogosCliente;
		this.dadosJogoSrvMesa11 = dadosJogoSrvMesa11;
		this.mesa11Applet = mesa11Applet;
		this.timeClienteOnline = timeClienteOnline;
	}

	@Override
	public void run() {
		System.out.println("run timeClienteOnline " + timeClienteOnline);
		while (controleChatCliente.isComunicacaoServer()) {
			dormir(tempoDormir);
			if (timesSelecionados() && controleJogo == null) {
				iniciaJogo();
			}
			if (controleJogo != null) {
				atualizaDadosJogoSrvMesa11();
			}

			// && controleJogosCliente
			// .verificaJogosNasListas(dadosJogoSrvMesa11
			// .getNomeJogo()
		}
		System.out.println("end run timeClienteOnline " + timeClienteOnline);
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
		Object ret = enviarObjeto(mesa11to);
		if (ret instanceof Mesa11TO) {
			mesa11to = (Mesa11TO) ret;
			dadosJogoSrvMesa11 = (DadosJogoSrvMesa11) mesa11to.getData();
			controleJogo.setDadosJogoSrvMesa11(dadosJogoSrvMesa11);
			System.out
					.println("mesa11to.setComando(ConstantesMesa11.OBTER_DADOS_JOGO);");
		}
		dormir(tempoDormir);
		mesa11to.setComando(ConstantesMesa11.OBTER_ULTIMA_JOGADA);
		mesa11to.setData(dadosJogoSrvMesa11.getNomeJogo());
		ret = enviarObjeto(mesa11to);
		if (ret instanceof Mesa11TO) {
			mesa11to = (Mesa11TO) ret;
			Animacao animacao = (Animacao) mesa11to.getData();
			if (animacao.getIndex() > indexAnimacaoAtual) {
				indexAnimacaoAtual = animacao.getIndex();
				controleJogo.executaAnimacao(animacao);
				System.out
						.println("mesa11to.setComando(ConstantesMesa11.OBTER_ULTIMA_JOGADA);");
			}

		}
		dormir(tempoDormir);
		mesa11to.setComando(ConstantesMesa11.OBTER_POSICAO_BOTOES);
		mesa11to.setData(dadosJogoSrvMesa11.getNomeJogo());
		ret = enviarObjeto(mesa11to);
		if (ret != null && ret instanceof Mesa11TO) {
			mesa11to = (Mesa11TO) ret;
			PosicaoBtnsSrvMesa11 posicaoBtnsSrvMesa11 = (PosicaoBtnsSrvMesa11) mesa11to
					.getData();
			if (!controleJogo.isAnimando()
					&& posicaoBtnsSrvMesa11.getTimeStamp() > timeStampPosicoes) {
				timeStampPosicoes = posicaoBtnsSrvMesa11.getTimeStamp();
				controleJogo.atualizaBotoesClienteOnline(posicaoBtnsSrvMesa11
						.getBotoes());
				System.out
						.println("mesa11to.setComando(ConstantesMesa11.OBTER_POSICAO_BOTOES);");

			}
		}
	}

	private void iniciaJogo() {
		tempoDormir = 333;
		Mesa11TO mesa11to = new Mesa11TO();
		mesa11to.setData(dadosJogoSrvMesa11.getTimeCasa());
		mesa11to.setComando(ConstantesMesa11.OBTER_TIME);
		Object ret = enviarObjeto(mesa11to);
		mesa11to = (Mesa11TO) ret;
		Time timeCasa = (Time) mesa11to.getData();
		mesa11to = new Mesa11TO();
		mesa11to.setData(dadosJogoSrvMesa11.getTimeVisita());
		mesa11to.setComando(ConstantesMesa11.OBTER_TIME);
		ret = enviarObjeto(mesa11to);
		mesa11to = (Mesa11TO) ret;
		Time timeVisita = (Time) mesa11to.getData();
		System.out.println("iniciaJogo()");
		System.out.println("timeCasa " + timeCasa);
		System.out.println("timeVisita " + timeVisita);
		controleJogo = new ControleJogo(mesa11Applet, timeClienteOnline,
				dadosJogoSrvMesa11);
		controleJogo.iniciaJogoOnline(dadosJogoSrvMesa11, timeCasa, timeVisita);
		controleJogo.inicializaVideo();
		controleJogo.centroCampo();
		controleJogo.setZoom(0.3);
	}

	private boolean timesSelecionados() {
		Mesa11TO mesa11to = new Mesa11TO();
		mesa11to.setComando(ConstantesMesa11.OBTER_DADOS_JOGO);
		mesa11to.setData(dadosJogoSrvMesa11.getNomeJogo());
		Object ret = enviarObjeto(mesa11to);
		if (ret instanceof Mesa11TO) {
			mesa11to = (Mesa11TO) ret;
			dadosJogoSrvMesa11 = (DadosJogoSrvMesa11) mesa11to.getData();
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

}
