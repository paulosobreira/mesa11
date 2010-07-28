package br.mesa11;

import br.hibernate.Time;
import br.mesa11.cliente.ControleJogosCliente;
import br.mesa11.servidor.ControleChatServidor;
import br.mesa11.servidor.ControleJogosServidor;
import br.mesa11.servidor.ControleLogin;
import br.mesa11.servidor.ControlePersistencia;
import br.tos.ClienteMesa11;
import br.tos.DadosMesa11;
import br.tos.DadosJogoSrvMesa11;
import br.tos.JogadaMesa11;
import br.tos.Mesa11TO;

public class ProxyComandos {
	private ControleLogin controleLogin;
	private ControlePersistencia controlePersistencia;
	private ControleChatServidor controleChatServidor;
	private ControleJogosServidor controleJogosServidor;
	private DadosMesa11 dadosMesa11;

	public ProxyComandos(String webDir, String webInfDir) {
		dadosMesa11 = new DadosMesa11();
		controleLogin = new ControleLogin(dadosMesa11);
		controleChatServidor = new ControleChatServidor(dadosMesa11);
		controlePersistencia = new ControlePersistencia(webDir, webInfDir);
		controleJogosServidor = new ControleJogosServidor(dadosMesa11,
				controlePersistencia);
	}

	public Object processarObjeto(Object object) {
		Mesa11TO mesa11TO = (Mesa11TO) object;
		if (ConstantesMesa11.ATUALIZAR_VISAO.equals(mesa11TO.getComando())) {
			return atualizarDadosVisao();
		} else if (ConstantesMesa11.OBTER_DADOS_JOGO.equals(mesa11TO
				.getComando())) {
			return controleJogosServidor.obterDadosJogo((String) mesa11TO
					.getData());
		} else if (ConstantesMesa11.OBTER_ULTIMA_JOGADA.equals(mesa11TO
				.getComando())) {
			return controleJogosServidor.obterUltimaJogada((String) mesa11TO
					.getData());
		} else if (ConstantesMesa11.OBTER_POSICAO_BOTOES.equals(mesa11TO
				.getComando())) {
			return controleJogosServidor.obterPosicaoBotoes((String) mesa11TO
					.getData());
		} else if (ConstantesMesa11.JOGADA.equals(mesa11TO.getComando())) {
			return controleJogosServidor.jogada((JogadaMesa11) mesa11TO
					.getData());
		} else if (ConstantesMesa11.LOGAR.equals(mesa11TO.getComando())) {
			return controleLogin.logar((ClienteMesa11) mesa11TO.getData());
		} else if (ConstantesMesa11.NOVO_USUARIO.equals(mesa11TO.getComando())) {
			return controleLogin.cadastrarUsuario((ClienteMesa11) mesa11TO
					.getData());
		} else if (ConstantesMesa11.RECUPERA_SENHA
				.equals(mesa11TO.getComando())) {
			return controleLogin.recuperaSenha((ClienteMesa11) mesa11TO
					.getData());
		} else if (ConstantesMesa11.NOVO_CAPCHA.equals(mesa11TO.getComando())) {
			return controleLogin.novoCapcha();
		} else if (ConstantesMesa11.ENVIAR_TEXTO.equals(mesa11TO.getComando())) {
			return controleChatServidor.receberTexto((ClienteMesa11) mesa11TO
					.getData());
		} else if (ConstantesMesa11.SALVAR_TIME.equals(mesa11TO.getComando())) {
			return controlePersistencia.salvarTime((Time) mesa11TO.getData());
		} else if (ConstantesMesa11.OBTER_LISTA_TIMES_JOGADOR.equals(mesa11TO
				.getComando())) {
			return controlePersistencia.obterTimesJogador((String) mesa11TO
					.getData());
		} else if (ConstantesMesa11.OBTER_TIME.equals(mesa11TO.getComando())) {
			Time time = controlePersistencia.obterTime((String) mesa11TO
					.getData());
			Mesa11TO mesa11to = new Mesa11TO();
			mesa11to.setData(time);
			return mesa11to;
		} else if (ConstantesMesa11.OBTER_TODOS_TIMES.equals(mesa11TO
				.getComando())) {
			return controlePersistencia.obterTodosTimes();
		} else if (ConstantesMesa11.CRIAR_JOGO.equals(mesa11TO.getComando())) {
			return controleJogosServidor
					.criarJogo((DadosJogoSrvMesa11) mesa11TO.getData());
		} else if (ConstantesMesa11.ENTRAR_JOGO.equals(mesa11TO.getComando())) {
			return controleJogosServidor
					.entrarJogo((DadosJogoSrvMesa11) mesa11TO.getData());
		}

		return null;
	}

	private Object atualizarDadosVisao() {
		Mesa11TO mesa11to = new Mesa11TO();
		mesa11to.setData(dadosMesa11);
		if (mesa11to.getSessaoCliente() != null) {
			controleChatServidor.atualizaSessaoCliente(mesa11to
					.getSessaoCliente());
		}
		return mesa11to;
	}
}
