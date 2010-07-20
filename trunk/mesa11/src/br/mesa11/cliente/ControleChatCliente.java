package br.mesa11.cliente;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.XMLDecoder;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import br.applet.Mesa11Applet;
import br.hibernate.Botao;
import br.hibernate.Goleiro;
import br.hibernate.Time;
import br.mesa11.BotaoUtils;
import br.mesa11.ConstantesMesa11;
import br.mesa11.conceito.ControleJogo;
import br.mesa11.visao.EditorTime;
import br.nnpe.Logger;
import br.nnpe.Util;
import br.recursos.CarregadorRecursos;
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
	private ControleJogo controleJogo;
	private boolean comunicacaoServer = true;
	private boolean segundoUniforme;
	private JComboBox jComboBoxTimes = new JComboBox(new String[] { Lang
			.msg("semTimes") });

	public ControleChatCliente(Mesa11Applet mesa11Applet) {
		this.mesa11Applet = mesa11Applet;
		threadAtualizadora = new Thread(new Runnable() {

			public void run() {
				try {
					while (comunicacaoServer) {
						Thread.sleep((5000 + ((int) Math.random() * 1000)));
						atualizaVisao();
					}
				} catch (Exception e) {
					Logger.logarExept(e);
				}

			}

		});
		threadAtualizadora.setPriority(Thread.MIN_PRIORITY);
		chatWindow = new ChatWindow(this);
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
		Object ret = enviarObjeto(mesa11to);
		if (ret == null) {
			return;
		}
		mesa11to = (Mesa11TO) ret;
		chatWindow.atualizar((DadosMesa11) mesa11to.getData());

	}

	private Object enviarObjeto(Mesa11TO mesa11to) {
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
		// TODO Auto-generated method stub

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
		Mesa11TO mesa11to = new Mesa11TO();
		mesa11to.setComando(ConstantesMesa11.OBTER_TODOS_TIMES);
		Object ret = enviarObjeto(mesa11to);
		boolean semTimes = true;
		if (ret instanceof Mesa11TO) {
			mesa11to = (Mesa11TO) ret;
			String[] times = (String[]) mesa11to.getData();
			jComboBoxTimes = new JComboBox(times);
			semTimes = false;
		}
		JPanel panelComboTimes = new JPanel();

		final JLabel uniforme = new JLabel() {
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(ConstantesMesa11.DIAMENTRO_BOTAO + 10,
						ConstantesMesa11.DIAMENTRO_BOTAO + 10);
			}
		};
		jComboBoxTimes.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if (arg0.getStateChange() == ItemEvent.SELECTED) {
					String nomeTime = (String) jComboBoxTimes.getSelectedItem();
					Mesa11TO mesa11to = new Mesa11TO();
					mesa11to.setData(nomeTime);
					mesa11to.setComando(ConstantesMesa11.OBTER_TIME);
					Object ret = enviarObjeto(mesa11to);
					mesa11to = (Mesa11TO) ret;
					Time time = (Time) mesa11to.getData();
					uniforme.setIcon(new ImageIcon(BotaoUtils.desenhaUniforme(
							time, 1)));
					segundoUniforme = false;
				}
			}
		});

		uniforme.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				segundoUniforme = !segundoUniforme;
				String nomeTime = (String) jComboBoxTimes.getSelectedItem();
				Mesa11TO mesa11to = new Mesa11TO();
				mesa11to.setData(nomeTime);
				mesa11to.setComando(ConstantesMesa11.OBTER_TIME);
				Object ret = enviarObjeto(mesa11to);
				mesa11to = (Mesa11TO) ret;
				Time time = (Time) mesa11to.getData();
				uniforme.setIcon(new ImageIcon(BotaoUtils.desenhaUniforme(time,
						segundoUniforme ? 2 : 1)));
			}
		});
		JPanel uniformesPanel = new JPanel();
		uniformesPanel.setBorder(new TitledBorder("") {
			@Override
			public String getTitle() {
				return Lang.msg("CliqueSegundoUniforme");
			}
		});
		uniformesPanel.add(uniforme);

		panelComboTimes.setBorder(new TitledBorder("") {
			@Override
			public String getTitle() {
				return Lang.msg("escolhaTime");
			}
		});
		panelComboTimes.add(jComboBoxTimes);

		JPanel escolhaTimesPanel = new JPanel(new BorderLayout());
		escolhaTimesPanel.add(panelComboTimes, BorderLayout.NORTH);
		escolhaTimesPanel.add(uniformesPanel, BorderLayout.CENTER);

		JPanel tempoJogoPanel = new JPanel();
		tempoJogoPanel.add(new JLabel() {
			@Override
			public String getText() {
				return Lang.msg("tempoJogoMinutos");
			}
		});
		JComboBox tempoJogoCombo = new JComboBox();
		tempoJogoCombo.addItem(new Integer(10));
		tempoJogoCombo.addItem(new Integer(20));
		tempoJogoCombo.addItem(new Integer(30));
		tempoJogoPanel.add(tempoJogoCombo);
		tempoJogoPanel.add(new JLabel() {
			@Override
			public String getText() {
				return Lang.msg("tempoJogadaSegundos");
			}
		});
		JComboBox tempoJogadaCombo = new JComboBox();
		tempoJogadaCombo.addItem(new Integer(20));
		tempoJogadaCombo.addItem(new Integer(30));
		tempoJogadaCombo.addItem(new Integer(40));
		tempoJogadaCombo.addItem(new Integer(50));
		tempoJogadaCombo.addItem(new Integer(60));
		tempoJogoPanel.add(tempoJogadaCombo);

		JPanel iniciarJogoPanel = new JPanel(new BorderLayout());
		iniciarJogoPanel.add(escolhaTimesPanel, BorderLayout.CENTER);
		iniciarJogoPanel.add(tempoJogoPanel, BorderLayout.NORTH);

		JOptionPane.showMessageDialog(chatWindow.getMainPanel(),
				iniciarJogoPanel);

	}

	public void entarJogo(Object object) {
		if (sessaoCliente == null) {
			logar();
			return;
		}
		// TODO Auto-generated method stub

	}

	public void verDetalhesJogo(Object object) {
		if (sessaoCliente == null) {
			logar();
			return;
		}
		// TODO Auto-generated method stub

	}

	public void verDetalhesJogador(Object object) {
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
		time.setQtdePontos(Util.intervalo(100, 1000));
		Goleiro goleiro = new Goleiro();
		goleiro.setForca(Util.intervalo(500, 1000));
		goleiro.setPrecisao(Util.intervalo(500, 1000));
		goleiro.setDefesa(Util.intervalo(500, 1000));
		goleiro.setGoleiro(true);
		goleiro.setTitular(true);
		goleiro.setTime(time);
		goleiro.setLoginCriador(sessaoCliente.getNomeJogador());
		time.getBotoes().add(goleiro);
		for (int i = 0; i < 10; i++) {
			Botao botao = new Botao();
			botao.setForca(Util.intervalo(500, 1000));
			botao.setPrecisao(Util.intervalo(500, 1000));
			botao.setDefesa(Util.intervalo(500, 1000));
			botao.setGoleiro(false);
			botao.setTitular(false);
			botao.setTime(time);
			botao.setLoginCriador(sessaoCliente.getNomeJogador());
			time.getBotoes().add(botao);
		}

		if (controleJogo == null) {
			controleJogo = new ControleJogo(mesa11Applet);
		}
		EditorTime editorTime = new EditorTime(time, controleJogo);
		JOptionPane.showMessageDialog(chatWindow.getMainPanel(), editorTime);
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
				if (controleJogo == null) {
					controleJogo = new ControleJogo(mesa11Applet);
				}
				EditorTime editorTime = new EditorTime((Time) mesa11to
						.getData(), controleJogo);
				JOptionPane.showMessageDialog(chatWindow.getMainPanel(),
						editorTime);
			}
		}

	}
}