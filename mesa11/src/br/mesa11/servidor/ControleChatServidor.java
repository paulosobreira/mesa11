package br.mesa11.servidor;

import br.nnpe.tos.NnpeTO;
import br.nnpe.tos.MsgSrv;
import br.nnpe.tos.SessaoCliente;
import br.recursos.Lang;
import br.tos.ClienteMesa11;
import br.tos.DadosMesa11;

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
		NnpeTO mesa11to = new NnpeTO();
		mesa11to.setData(dadosMesa11);
		return mesa11to;
	}

	public void atualizaSessaoCliente(SessaoCliente sessaoCliente) {
		dadosMesa11.atualizaAtividade(sessaoCliente.getNomeJogador());
	}

}
