package br.mesa11.cliente;

import br.applet.Mesa11Applet;
import br.hibernate.Time;
import br.mesa11.ConstantesMesa11;
import br.mesa11.conceito.ControleJogo;
import br.nnpe.Logger;
import br.nnpe.Util;
import br.tos.DadosJogoSrvMesa11;
import br.tos.Mesa11TO;

public class MonitorJogo extends Thread {
	private ChatWindow chatWindow;
	private ControleChatCliente controleChatCliente;
	private ControleJogosCliente controleJogosCliente;
	private DadosJogoSrvMesa11 dadosJogoSrvMesa11;
	private ControleJogo controleJogo;
	private Mesa11Applet mesa11Applet;

	public MonitorJogo(ChatWindow chatWindow,
			ControleChatCliente controleChatCliente,
			ControleJogosCliente controleJogosCliente,
			DadosJogoSrvMesa11 dadosJogoSrvMesa11, Mesa11Applet mesa11Applet) {
		super();
		this.chatWindow = chatWindow;
		this.controleChatCliente = controleChatCliente;
		this.controleJogosCliente = controleJogosCliente;
		this.dadosJogoSrvMesa11 = dadosJogoSrvMesa11;
		this.mesa11Applet = mesa11Applet;
	}

	@Override
	public void run() {
		while (controleChatCliente.isComunicacaoServer()
				&& controleJogosCliente
						.verificaJogosNasListas(dadosJogoSrvMesa11
								.getNomeJogo())) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (timesSelecionados() && controleJogo == null) {
				iniciaJogo();
			}
			System.out.println("Esperando jogo comecar");

		}
	}

	private void iniciaJogo() {
		controleJogo = new ControleJogo(mesa11Applet);
		controleJogo.inicializaVideo();
		controleJogo.centroCampo();
		controleJogo.setZoom(0.3);
		controleJogo.setJogoCliente(true);
		Mesa11TO mesa11to = new Mesa11TO();
		mesa11to.setData(dadosJogoSrvMesa11.getTimeCasa());
		mesa11to.setComando(ConstantesMesa11.OBTER_TIME);
		Object ret = enviarObjeto(mesa11to);
		mesa11to = (Mesa11TO) ret;
		Time timeCasa = (Time) mesa11to.getData();
		mesa11to = new Mesa11TO();
		mesa11to.setData(dadosJogoSrvMesa11.getTimeCasa());
		mesa11to.setComando(ConstantesMesa11.OBTER_TIME);
		ret = enviarObjeto(mesa11to);
		mesa11to = (Mesa11TO) ret;
		Time timeVisita = (Time) mesa11to.getData();
		controleJogo
				.iniciaJogoCliente(dadosJogoSrvMesa11, timeCasa, timeVisita);
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
