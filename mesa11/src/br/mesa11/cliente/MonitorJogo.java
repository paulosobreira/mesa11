package br.mesa11.cliente;

import br.mesa11.conceito.ControleJogo;
import br.tos.DadosJogoSrvMesa11;

public class MonitorJogo extends Thread {
	private ChatWindow chatWindow;
	private ControleChatCliente controleChatCliente;
	private ControleJogosCliente controleJogosCliente;
	private DadosJogoSrvMesa11 jogoSrvMesa11;
	private ControleJogo controleJogo;

	public MonitorJogo(ChatWindow chatWindow,
			ControleChatCliente controleChatCliente,
			ControleJogosCliente controleJogosCliente,
			DadosJogoSrvMesa11 jogoSrvMesa11) {
		super();
		this.chatWindow = chatWindow;
		this.controleChatCliente = controleChatCliente;
		this.controleJogosCliente = controleJogosCliente;
		this.jogoSrvMesa11 = jogoSrvMesa11;
	}

	@Override
	public void run() {
		while (controleChatCliente.isComunicacaoServer()
				&& controleJogosCliente.verificaJogosNasListas(jogoSrvMesa11
						.getNomeJogo())) {
			System.out.println("Esperando jogo comecar");

		}
	}
}
