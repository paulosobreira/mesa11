package br.mesa11.servidor;

import br.recursos.Lang;
import br.tos.ClienteMesa11;
import br.tos.DadosMesa11;
import br.tos.Mesa11TO;
import br.tos.MsgSrv;
import br.tos.SessaoCliente;

public class ControleChatServidor {
	private DadosMesa11 dadosMesa11;

	public ControleChatServidor(DadosMesa11 dadosMesa11) {
		this.dadosMesa11 = dadosMesa11;
	}

	public Object receberTexto(ClienteMesa11 cliente) {
		if (cliente.getSessaoCliente() == null) {
			return (new MsgSrv(Lang.msg("usuarioSemSessao")));
		}
		dadosMesa11.atualizaAtividade(cliente.getSessaoCliente()
				.getNomeJogador());
		dadosMesa11.setLinhaChat(cliente.getSessaoCliente().getNomeJogador()
				+ " : " + cliente.getTexto());
		dadosMesa11.setDataTime(System.currentTimeMillis());
		Mesa11TO mesa11to = new Mesa11TO();
		mesa11to.setData(dadosMesa11);
		return mesa11to;
	}

	public void atualizaSessaoCliente(SessaoCliente sessaoCliente) {
		dadosMesa11.atualizaAtividade(sessaoCliente.getNomeJogador());
	}

}
