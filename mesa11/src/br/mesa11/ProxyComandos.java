package br.mesa11;

import br.hibernate.CampeonatoMesa11;
import br.hibernate.Mesa11Dados;
import br.hibernate.Time;
import br.mesa11.servidor.ControleCampeonatoServidor;
import br.mesa11.servidor.ControleChatServidor;
import br.mesa11.servidor.ControleJogosServidor;
import br.mesa11.servidor.ControleLogin;
import br.mesa11.servidor.ControlePersistencia;
import br.mesa11.servidor.MonitorAtividade;
import br.nnpe.HibernateUtil;
import br.nnpe.Logger;
import br.tos.ClienteMesa11;
import br.tos.DadosJogoSrvMesa11;
import br.tos.DadosMesa11;
import br.tos.JogadaMesa11;
import br.tos.Mesa11TO;
import br.tos.SessaoCliente;

public class ProxyComandos {
	private ControleLogin controleLogin;
	private ControlePersistencia controlePersistencia;
	private ControleChatServidor controleChatServidor;
	private ControleJogosServidor controleJogosServidor;
	private ControleCampeonatoServidor controleCampeonatoServidor;
	private DadosMesa11 dadosMesa11;
	private MonitorAtividade monitorAtividade;

	public ProxyComandos(String webDir, String webInfDir) {
		dadosMesa11 = new DadosMesa11();
		controleLogin = new ControleLogin(dadosMesa11);
		controleChatServidor = new ControleChatServidor(dadosMesa11);
		controlePersistencia = new ControlePersistencia(webDir, webInfDir);
		controleJogosServidor = new ControleJogosServidor(dadosMesa11,
				controlePersistencia, this);
		controleCampeonatoServidor = new ControleCampeonatoServidor(
				dadosMesa11, controlePersistencia, this, controleJogosServidor);
		monitorAtividade = new MonitorAtividade(this);
		monitorAtividade.start();
	}

	public Object processarObjeto(Object object) {
		Mesa11TO mesa11TO = (Mesa11TO) object;
		if (ConstantesMesa11.ATUALIZAR_VISAO.equals(mesa11TO.getComando())) {
			return atualizarDadosVisao(mesa11TO);
		} else if (ConstantesMesa11.VERIFICA_POSICAO_DIFF_BOTOES
				.equals(mesa11TO.getComando())) {
			return controleJogosServidor
					.verificaPosicaoDiffBotoes(((String) mesa11TO.getData())
							.split("-"));
		} else if (ConstantesMesa11.OBTER_DADOS_JOGO.equals(mesa11TO
				.getComando())) {
			return controleJogosServidor.obterDadosJogo(mesa11TO);
		} else if (ConstantesMesa11.OBTER_ULTIMA_JOGADA.equals(mesa11TO
				.getComando())) {
			return controleJogosServidor.obterUltimaJogada((String) mesa11TO
					.getData());
		} else if (ConstantesMesa11.OBTER_POSICAO_BOTOES.equals(mesa11TO
				.getComando())) {
			return controleJogosServidor.obterPosicaoBotoes(((String) mesa11TO
					.getData()).split("-"));
		} else if (ConstantesMesa11.JOGADA.equals(mesa11TO.getComando())) {
			return controleJogosServidor.jogada((JogadaMesa11) mesa11TO
					.getData());
		} else if (ConstantesMesa11.SAIR_JOGO.equals(mesa11TO.getComando())) {
			return controleJogosServidor.sairJogo((String) mesa11TO.getData());
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
		} else if (ConstantesMesa11.OBTER_TODOS_JOGADORES.equals(mesa11TO
				.getComando())) {
			return controlePersistencia.obterTodosJogadores();
		} else if (ConstantesMesa11.CRIAR_JOGO.equals(mesa11TO.getComando())) {
			return controleJogosServidor
					.criarJogo((DadosJogoSrvMesa11) mesa11TO.getData());
		} else if (ConstantesMesa11.CRIAR_CAMPEONATO.equals(mesa11TO
				.getComando())) {
			return controleCampeonatoServidor
					.criarCampeonato((CampeonatoMesa11) mesa11TO.getData());
		} else if (ConstantesMesa11.LISTAR_CAMPEONATOS.equals(mesa11TO
				.getComando())) {
			return controleCampeonatoServidor.listarCampeonatos();
		} else if (ConstantesMesa11.CRIAR_JOGO_CPU
				.equals(mesa11TO.getComando())) {
			return controleJogosServidor
					.criarJogoCpu((DadosJogoSrvMesa11) mesa11TO.getData());
		} else if (ConstantesMesa11.ENTRAR_JOGO.equals(mesa11TO.getComando())) {
			return controleJogosServidor
					.entrarJogo((DadosJogoSrvMesa11) mesa11TO.getData());
		} else if (ConstantesMesa11.ENVIAR_IMAGEM.equals(mesa11TO.getComando())) {
			return controleJogosServidor.gravarImagem(mesa11TO);
		} else if (ConstantesMesa11.OBTER_TODAS_IMAGENS.equals(mesa11TO
				.getComando())) {
			return controleJogosServidor.obterTodasImagens();
		} else if (ConstantesMesa11.OBTER_JOGADORES_CAMPEONATO.equals(mesa11TO
				.getComando())) {
			return controleJogosServidor
					.obterJogadoresCampeonato((String) mesa11TO.getData());
		} else if (ConstantesMesa11.VER_RODADA.equals(mesa11TO.getComando())) {
			return controleJogosServidor.verRodada((CampeonatoMesa11) mesa11TO
					.getData());
		} else if (ConstantesMesa11.VER_CLASSIFICACAO.equals(mesa11TO
				.getComando())) {
			try {
				return controleJogosServidor.obterClassificacao();
			} finally {
				HibernateUtil.closeSession();
			}

		} else if (ConstantesMesa11.VER_CAMPEONATO
				.equals(mesa11TO.getComando())) {
			try {
				return controleCampeonatoServidor
						.verCampeonato((String) mesa11TO.getData());
			} finally {
				HibernateUtil.closeSession();
			}

		}

		return null;
	}

	private Object atualizarDadosVisao(Mesa11TO mesa11to) {
		if (mesa11to.getSessaoCliente() != null) {
			controleChatServidor.atualizaSessaoCliente(mesa11to
					.getSessaoCliente());
		}
		mesa11to.setData(dadosMesa11);
		return mesa11to;
	}

	public DadosMesa11 getDadosMesa11() {
		return dadosMesa11;
	}

	public void removerClienteInativo(SessaoCliente sessaoClienteRemover) {
		Logger.logar("removerClienteInativo " + sessaoClienteRemover);
		controleJogosServidor.removerClienteInativo(sessaoClienteRemover);
		dadosMesa11.getClientes().remove(sessaoClienteRemover);
	}

	public ControleJogosServidor getControleJogosServidor() {
		return controleJogosServidor;
	}

	public boolean verificaSemSessao(String nomeCriador) {
		return controleLogin.verificaSemSessao(nomeCriador);
	}

	public void gravarDados(Mesa11Dados... mesa11Dados) throws Exception {
		controlePersistencia.gravarDados(mesa11Dados);
	}

}
