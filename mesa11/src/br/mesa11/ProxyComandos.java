package br.mesa11;

import br.hibernate.Time;
import br.mesa11.servidor.ControleChatServidor;
import br.mesa11.servidor.ControleLogin;
import br.mesa11.servidor.ControlePersistencia;
import br.tos.ClienteMesa11;
import br.tos.DadosMesa11;
import br.tos.Mesa11TO;

public class ProxyComandos {
	private ControleLogin controleLogin;
	private ControlePersistencia controlePersistencia;
	private ControleChatServidor controleChatServidor;
	private DadosMesa11 dadosMesa11;

	public ProxyComandos(String webDir, String webInfDir) {
		dadosMesa11 = new DadosMesa11();
		controleLogin = new ControleLogin(dadosMesa11);
		controleChatServidor = new ControleChatServidor(dadosMesa11);
		controlePersistencia = new ControlePersistencia(webDir, webInfDir);
	}

	public Object processarObjeto(Object object) {
		Mesa11TO mesa11TO = (Mesa11TO) object;
		if (ConstantesMesa11.ATUALIZAR_VISAO.equals(mesa11TO.getComando())) {
			return atualizarDadosVisao();
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
			return controlePersistencia.obterTime((String) mesa11TO.getData());
		} else if (ConstantesMesa11.OBTER_TODOS_TIMES.equals(mesa11TO
				.getComando())) {
			return controlePersistencia.obterTodosTimes();
		}

		return null;
	}

	private Object atualizarDadosVisao() {
		Mesa11TO mesa11to = new Mesa11TO();
		mesa11to.setData(dadosMesa11);
		return mesa11to;
	}
}
