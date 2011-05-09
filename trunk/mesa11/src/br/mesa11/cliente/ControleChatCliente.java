package br.mesa11.cliente;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jnlp.FileContents;
import javax.jnlp.PersistenceService;
import javax.jnlp.ServiceManager;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

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
import br.tos.ClassificacaoTime;
import br.tos.ClassificacaoUsuario;
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
		try {
			PersistenceService persistenceService = (PersistenceService) ServiceManager
					.lookup("javax.jnlp.PersistenceService");
			FileContents fileContents = persistenceService.get(mesa11Applet
					.getCodeBase());
			if (fileContents == null) {
				Logger.logar(" fileContents == null  ");
			}
			ObjectInputStream ois = new ObjectInputStream(
					fileContents.getInputStream());
			Map map = (Map) ois.readObject();
			String login = (String) map.get("login");
			String pass = (String) map.get("pass");
			if (!Util.isNullOrEmpty(pass) && !Util.isNullOrEmpty(login)) {
				formLogin.getNome().setText(login);
				formLogin.getSenha().setText(pass);
				formLogin.getLembrar().setSelected(true);
			}
		} catch (Exception e) {
			Logger.logarExept(e);
		}
		int result = JOptionPane.showConfirmDialog(chatWindow.getMainPanel(),
				formLogin, Lang.msg("formularioLogin"),
				JOptionPane.OK_CANCEL_OPTION);

		if (JOptionPane.OK_OPTION == result) {
			registrarUsuario();
			atualizaVisao();
			if (formLogin.getLembrar().isSelected()) {
				try {
					PersistenceService persistenceService = (PersistenceService) ServiceManager
							.lookup("javax.jnlp.PersistenceService");
					FileContents fileContents = null;
					try {
						fileContents = persistenceService.get(mesa11Applet
								.getCodeBase());
					} catch (Exception e) {
						persistenceService.create(mesa11Applet.getCodeBase(),
								1024);
						fileContents = persistenceService.get(mesa11Applet
								.getCodeBase());
					}

					if (fileContents == null) {
						Logger.logar(" fileContents == null  ");

					}

					Map map = new HashMap();
					map.put("login", formLogin.getNome().getText());
					map.put("pass", String.valueOf((formLogin.getSenha()
							.getPassword())));
					ObjectOutputStream stream = new ObjectOutputStream(
							fileContents.getOutputStream(true));
					stream.writeObject(map);
					stream.flush();

				} catch (Exception e) {
					Logger.logarExept(e);
				}
			}
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
			JOptionPane.showMessageDialog(chatWindow.getMainPanel(),
					e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
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
			JOptionPane.showMessageDialog(chatWindow.getMainPanel(),
					Lang.msg("problemasRede"), "Erro",
					JOptionPane.ERROR_MESSAGE);
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
				Lang.msg("entrarJogo") + jogoSelecionado,
				Lang.msg("entrarJogo"), JOptionPane.YES_NO_OPTION);
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
				Lang.msg("verDetalhesJogo") + " " + jogoSelecionado,
				Lang.msg("verDetalhesJogo"), JOptionPane.YES_NO_OPTION);
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
		if (ret == JOptionPane.YES_OPTION) {
			controleJogo.salvarTime(time);
		}

	}

	public void verClassificacao() {
		if (sessaoCliente == null) {
			logar();
			return;
		}
		Mesa11TO mesa11to = new Mesa11TO();
		mesa11to.setComando(ConstantesMesa11.VER_CLASSIFICACAO);
		Object ret = enviarObjeto(mesa11to);
		if (!(ret instanceof Mesa11TO)) {
			return;
		}
		mesa11to = (Mesa11TO) ret;
		Map data = (Map) mesa11to.getData();
		List dadosTimes = (List) data
				.get(ConstantesMesa11.VER_CLASSIFICACAO_TIMES);
		List dadosJogadores = (List) data
				.get(ConstantesMesa11.VER_CLASSIFICACAO_JOGADORES);
		JPanel classificacaoPanel = gerarPanelClassificacao(dadosTimes,
				dadosJogadores);
		JOptionPane.showMessageDialog(this.mesa11Applet, classificacaoPanel,
				Lang.msg("classificacao"), JOptionPane.INFORMATION_MESSAGE);
	}

	private JPanel gerarPanelClassificacao(final List dadosTimes,
			final List dadosJogadores) {
		final JTable timesTable = new JTable();

		final TableModel timesTableModel = new AbstractTableModel() {

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				ClassificacaoTime value = (ClassificacaoTime) dadosTimes
						.get(rowIndex);

				switch (columnIndex) {
				case 0:
					return value.getTime();
				case 1:
					return value.getJogos();
				case 2:
					return value.getVitorias();
				case 3:
					return value.getEmpates();
				case 4:
					return value.getDerrotas();
				case 5:
					return value.getSaldoGols();
				case 6:
					return value.getGolsFavor();
				case 7:
					return value.getGolsContra();
				default:
					return "";
				}
			}

			@Override
			public int getRowCount() {
				if (dadosTimes == null) {
					return 0;
				}
				return dadosTimes.size();
			}

			@Override
			public int getColumnCount() {
				return 8;
			}

			@Override
			public String getColumnName(int columnIndex) {

				switch (columnIndex) {
				case 0:
					return Lang.msg("time");
				case 1:
					return Lang.msg("jogos");
				case 2:
					return Lang.msg("vitorias");
				case 3:
					return Lang.msg("empates");
				case 4:
					return Lang.msg("derrotas");
				case 5:
					return Lang.msg("saldogols");
				case 6:
					return Lang.msg("golsfavor");
				case 7:
					return Lang.msg("golscontra");
				default:
					return "";
				}

			}
		};

		timesTable.setModel(timesTableModel);
		JScrollPane timesJs = new JScrollPane(timesTable) {
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(620, 150);
			}
		};
		final JTable jogadoresTable = new JTable();

		final TableModel jogadoresTableModel = new AbstractTableModel() {

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				ClassificacaoUsuario value = (ClassificacaoUsuario) dadosJogadores
						.get(rowIndex);
				switch (columnIndex) {
				case 0:
					return value.getLogin();
				case 1:
					return value.getJogos();
				case 2:
					return value.getVitorias();
				case 3:
					return value.getEmpates();
				case 4:
					return value.getDerrotas();
				case 5:
					return value.getSaldoGols();
				case 6:
					return value.getGolsFavor();
				case 7:
					return value.getGolsContra();
				default:
					return "";
				}
			}

			@Override
			public int getRowCount() {
				if (dadosJogadores == null) {
					return 0;
				}
				return dadosJogadores.size();
			}

			@Override
			public int getColumnCount() {
				return 8;
			}

			@Override
			public String getColumnName(int columnIndex) {

				switch (columnIndex) {
				case 0:
					return Lang.msg("jogador");
				case 1:
					return Lang.msg("jogos");
				case 2:
					return Lang.msg("vitorias");
				case 3:
					return Lang.msg("empates");
				case 4:
					return Lang.msg("derrotas");
				case 5:
					return Lang.msg("saldogols");
				case 6:
					return Lang.msg("golsfavor");
				case 7:
					return Lang.msg("golscontra");
				default:
					return "";
				}

			}
		};

		jogadoresTable.setModel(jogadoresTableModel);
		JScrollPane jogadoresJs = new JScrollPane(jogadoresTable) {
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(620, 150);
			}
		};

		JPanel jPanelTimes = new JPanel();
		jPanelTimes.setBorder(new TitledBorder("rankingTimes") {
			@Override
			public String getTitle() {
				return Lang.msg("rankingTimes");
			}
		});
		jPanelTimes.add(timesJs);

		JPanel jPanelJogs = new JPanel();
		jPanelJogs.setBorder(new TitledBorder("rankingJogadores") {
			@Override
			public String getTitle() {
				return Lang.msg("rankingJogadores");
			}
		});
		jPanelJogs.add(jogadoresJs);

		JPanel jPanel = new JPanel(new GridLayout(2, 1));
		jPanel.add(jPanelTimes);
		jPanel.add(jPanelJogs);
		return jPanel;
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
		JComboBox jComboBoxTimes = new JComboBox(
				new String[] { Lang.msg("semTimes") });
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
				int retOpt = JOptionPane.showConfirmDialog(
						chatWindow.getMainPanel(), editorTime,
						Lang.msg("editarTime"), JOptionPane.YES_NO_OPTION);
				if (retOpt == JOptionPane.YES_OPTION) {
					controleJogo.salvarTime(time);
				}

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

	public void criarJogoVsCPU() {
		if (sessaoCliente == null) {
			logar();
			return;
		}
		controleJogosCliente.criarJogoVsCPU();

	}

	public String getVersao() {
		return mesa11Applet.getVersao();
	}
}
