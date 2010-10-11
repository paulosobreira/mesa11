package br.mesa11.cliente;

import java.awt.BorderLayout;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import br.applet.Mesa11Applet;
import br.hibernate.Botao;
import br.hibernate.Goleiro;
import br.hibernate.Time;
import br.mesa11.ConstantesMesa11;
import br.mesa11.conceito.ControleJogo;
import br.mesa11.visao.EditorTime;
import br.nnpe.Logger;
import br.nnpe.Util;
import br.recursos.Lang;
import br.tos.ClienteMesa11;
import br.tos.DadosMesa11;
import br.tos.ErroServ;
import br.tos.Mesa11TO;
import br.tos.MsgSrv;
import br.tos.SessaoCliente;

public class ControleChatCliente {
	private FormLogin formLogin;
	private ChatWindow chatWindow;
	private Mesa11Applet mesa11Applet;
	private SessaoCliente sessaoCliente;
	private Thread threadAtualizadora;
	private boolean comunicacaoServer = true;
	private ControleJogosCliente controleJogosCliente;

	public boolean isComunicacaoServer() {
		return comunicacaoServer;
	}

	public void setComunicacaoServer(boolean comunicacaoServer) {
		this.comunicacaoServer = comunicacaoServer;
	}

	public SessaoCliente getSessaoCliente() {
		return sessaoCliente;
	}

	public ControleChatCliente(Mesa11Applet mesa11Applet) {
		this.mesa11Applet = mesa11Applet;
		threadAtualizadora = new Thread(new Runnable() {

			public void run() {
				try {
					while (comunicacaoServer) {
						Thread.sleep(1000);
						atualizaVisao();
					}
				} catch (Exception e) {
					Logger.logarExept(e);
				}

			}

		});
		threadAtualizadora.setPriority(Thread.MIN_PRIORITY);
		chatWindow = new ChatWindow(this);
		controleJogosCliente = new ControleJogosCliente(chatWindow, this,
				mesa11Applet);
		atualizaVisao();
		mesa11Applet.setLayout(new BorderLayout());
		mesa11Applet.add(chatWindow.getMainPanel(), BorderLayout.CENTER);
		threadAtualizadora.start();

	}

	public void logar() {
		formLogin = new FormLogin(mesa11Applet);
		formLogin.setToolTipText(Lang.msg("formularioLogin"));
		int result = JOptionPane.showConfirmDialog(chatWindow.getMainPanel(),
				formLogin, Lang.msg("formularioLogin"),
				JOptionPane.OK_CANCEL_OPTION);

		if (JOptionPane.OK_OPTION == result) {
			registrarUsuario();
			atualizaVisao();
		}

	}

	private void atualizaVisao() {
		if (chatWindow == null) {
			return;
		}
		Mesa11TO mesa11to = new Mesa11TO();
		mesa11to.setComando(ConstantesMesa11.ATUALIZAR_VISAO);
		mesa11to.setSessaoCliente(sessaoCliente);
		Object ret = enviarObjeto(mesa11to);
		if (ret == null) {
			return;
		}
		mesa11to = (Mesa11TO) ret;
		DadosMesa11 dadosMesa11 = (DadosMesa11) mesa11to.getData();
		chatWindow.atualizar(dadosMesa11);
		controleJogosCliente.setDadosMesa11(dadosMesa11);
	}

	public Object enviarObjeto(Mesa11TO mesa11to) {
		if (mesa11Applet == null) {
			Logger.logar("enviarObjeto mesa11Applet null");
			return null;
		}
		return mesa11Applet.enviarObjeto(mesa11to);
	}

	private boolean registrarUsuario() {
		Mesa11TO mesa11to = new Mesa11TO();
		ClienteMesa11 clienteMesa11 = new ClienteMesa11();
		mesa11to.setData(clienteMesa11);
		clienteMesa11.setNomeJogador(formLogin.getNome().getText());

		try {
			if (!Util.isNullOrEmpty(new String(formLogin.getSenha()
					.getPassword()))) {
				clienteMesa11.setSenhaJogador(Util.md5(new String(formLogin
						.getSenha().getPassword())));
			}
		} catch (Exception e) {
			Logger.logarExept(e);
			JOptionPane.showMessageDialog(chatWindow.getMainPanel(), e
					.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
		}
		clienteMesa11.setEmailJogador(formLogin.getEmail().getText());
		clienteMesa11.setRecuperar(formLogin.getRecuperar().isSelected());
		clienteMesa11.setChaveCapcha(formLogin.getCapchaChave());
		clienteMesa11.setTexto(formLogin.getCapchaTexto());

		if (!Util.isNullOrEmpty(clienteMesa11.getNomeJogador())
				&& !Util.isNullOrEmpty(clienteMesa11.getSenhaJogador())) {
			mesa11to.setComando(ConstantesMesa11.LOGAR);
		} else if (!Util.isNullOrEmpty(clienteMesa11.getNomeJogador())
				&& !Util.isNullOrEmpty(clienteMesa11.getEmailJogador())
				&& !clienteMesa11.isRecuperar()) {
			mesa11to.setComando(ConstantesMesa11.NOVO_USUARIO);
		} else if (clienteMesa11.isRecuperar()) {
			mesa11to.setComando(ConstantesMesa11.RECUPERA_SENHA);
		}
		Logger.logar("registrarUsuario mesa11to.getComando() "
				+ mesa11to.getComando());
		Object ret = mesa11Applet.enviarObjeto(mesa11to);
		if (ret == null) {
			return false;
		}
		if (ret instanceof Mesa11TO) {
			mesa11to = (Mesa11TO) ret;
			SessaoCliente cliente = (SessaoCliente) mesa11to.getData();
			this.sessaoCliente = cliente;
		}
		return true;
	}

	public void setChatWindow(ChatWindow chatWindow) {
		this.chatWindow = chatWindow;

	}

	private boolean retornoNaoValido(Object ret) {
		if (ret instanceof ErroServ || ret instanceof MsgSrv) {
			return true;
		}
		return false;
	}

	public void enviarTexto(String text) {
		if (sessaoCliente == null) {
			logar();
			return;
		}
		ClienteMesa11 clienteMesa11 = new ClienteMesa11(sessaoCliente);
		clienteMesa11.setTexto(text);
		Mesa11TO mesa11to = new Mesa11TO();
		mesa11to.setData(clienteMesa11);
		mesa11to.setComando(ConstantesMesa11.ENVIAR_TEXTO);
		Object ret = enviarObjeto(mesa11to);
		if (retornoNaoValido(ret)) {
			return;
		}
		if (ret == null) {
			JOptionPane.showMessageDialog(chatWindow.getMainPanel(), Lang
					.msg("problemasRede"), "Erro", JOptionPane.ERROR_MESSAGE);
			return;
		}
		mesa11to = (Mesa11TO) ret;
		chatWindow.atualizar((DadosMesa11) mesa11to.getData());
	}

	public void criarJogo() {
		if (sessaoCliente == null) {
			logar();
			return;
		}
		controleJogosCliente.criarJogo();

	}

	public void entarJogo() {
		if (sessaoCliente == null) {
			logar();
			return;
		}
		String jogoSelecionado = chatWindow.obterJogoSelecionado();
		if (jogoSelecionado == null) {
			return;
		}
		int result = JOptionPane.showConfirmDialog(chatWindow.getMainPanel(),
				Lang.msg("entrarJogo") + jogoSelecionado, Lang
						.msg("entrarJogo"), JOptionPane.YES_NO_OPTION);
		if (result == JOptionPane.YES_OPTION) {
			controleJogosCliente.entrarJogo(jogoSelecionado);
		}

	}

	public void verDetalhesJogo() {
		if (sessaoCliente == null) {
			logar();
			return;
		}
		String jogoSelecionado = chatWindow.obterJogoSelecionado();
		if (jogoSelecionado == null) {
			return;
		}
		int result = JOptionPane.showConfirmDialog(chatWindow.getMainPanel(),
				Lang.msg("verDetalhesJogo") + " " + jogoSelecionado, Lang
						.msg("verDetalhesJogo"), JOptionPane.YES_NO_OPTION);
		if (result == JOptionPane.YES_OPTION) {
			controleJogosCliente.verDetalhesJogo(jogoSelecionado);
		}

	}

	public void verDetalhesJogador() {
		if (sessaoCliente == null) {
			logar();
			return;
		}
		// TODO Auto-generated method stub

	}

	public void criarTime() {
		if (sessaoCliente == null) {
			logar();
			return;
		}
		Time time = new Time();
		time.setLoginCriador(sessaoCliente.getNomeJogador());
		time.setNomeJogador(sessaoCliente.getNomeJogador());
		time.setQtdePontos(0);
		Goleiro goleiro = new Goleiro();
		goleiro.setForca(500);
		goleiro.setPrecisao(500);
		goleiro.setDefesa(500);
		goleiro.setGoleiro(true);
		goleiro.setTitular(true);
		goleiro.setTime(time);
		goleiro.setNumero(1);
		goleiro.setLoginCriador(sessaoCliente.getNomeJogador());
		time.getBotoes().add(goleiro);
		for (int i = 0; i < 10; i++) {
			Botao botao = new Botao();
			botao.setForca(500);
			botao.setPrecisao(500);
			botao.setDefesa(500);
			botao.setGoleiro(false);
			botao.setTitular(false);
			botao.setTime(time);
			botao.setNumero(i + 2);
			botao.setLoginCriador(sessaoCliente.getNomeJogador());
			time.getBotoes().add(botao);
		}
		ControleJogo controleJogo = new ControleJogo(mesa11Applet, null, null,
				null);
		EditorTime editorTime = new EditorTime(time, controleJogo);
		int ret = JOptionPane.showConfirmDialog(chatWindow.getMainPanel(),
				editorTime, Lang.msg("criarTime"), JOptionPane.YES_NO_OPTION);
		if (ret == JOptionPane.NO_OPTION) {
			return;
		}
		controleJogo.salvarTime(time);
	}

	public void verClassificacao() {
		if (sessaoCliente == null) {
			logar();
			return;
		}
		// TODO Auto-generated method stub

	}

	public int getLatenciaMinima() {
		return mesa11Applet.getLatenciaMinima();
	}

	public int getLatenciaReal() {
		return mesa11Applet.getLatenciaReal();
	}

	public void editarTime() {
		Mesa11TO mesa11to = new Mesa11TO();
		mesa11to.setComando(ConstantesMesa11.OBTER_LISTA_TIMES_JOGADOR);
		mesa11to.setData(sessaoCliente.getNomeJogador());
		Object ret = enviarObjeto(mesa11to);
		JComboBox jComboBoxTimes = new JComboBox(new String[] { Lang
				.msg("semTimes") });
		boolean semTimes = true;
		if (ret instanceof Mesa11TO) {
			mesa11to = (Mesa11TO) ret;
			String[] times = (String[]) mesa11to.getData();
			jComboBoxTimes = new JComboBox(times);
			semTimes = false;
		}
		JPanel panelTimes = new JPanel();
		panelTimes.setBorder(new TitledBorder("") {
			@Override
			public String getTitle() {
				return Lang.msg("escolhaTime");
			}
		});
		panelTimes.add(jComboBoxTimes);
		JOptionPane.showMessageDialog(chatWindow.getMainPanel(), panelTimes);
		if (!semTimes) {
			String timeSelecionado = (String) jComboBoxTimes.getSelectedItem();
			Logger.logar("timeSelecionado " + timeSelecionado);
			mesa11to = new Mesa11TO();
			mesa11to.setComando(ConstantesMesa11.OBTER_TIME);
			mesa11to.setData(timeSelecionado);
			ret = enviarObjeto(mesa11to);
			if (ret instanceof Mesa11TO) {
				mesa11to = (Mesa11TO) ret;
				Time time = (Time) mesa11to.getData();
				ControleJogo controleJogo = new ControleJogo(mesa11Applet,
						null, null, null);
				EditorTime editorTime = new EditorTime(time, controleJogo);
				int retOpt = JOptionPane.showConfirmDialog(chatWindow
						.getMainPanel(), editorTime, Lang.msg("editarTime"),
						JOptionPane.YES_NO_OPTION);
				if (retOpt == JOptionPane.NO_OPTION) {
					return;
				}
				controleJogo.salvarTime(time);
			}
		}

	}

	public void sairJogo() {
		Mesa11TO mesa11to = new Mesa11TO();
		mesa11to.setComando(ConstantesMesa11.SAIR_JOGO);
		mesa11to.setData(sessaoCliente.getNomeJogador());
		Object ret = enviarObjeto(mesa11to);
		if (controleJogosCliente != null) {
			controleJogosCliente.sairJogo();
		}
	}

	public void atualizaInfo() {
		if (chatWindow != null)
			chatWindow.atualizaInfo();

	}
}
