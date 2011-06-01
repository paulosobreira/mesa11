package br.mesa11.cliente;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import br.applet.Mesa11Applet;
import br.hibernate.Time;
import br.mesa11.BotaoUtils;
import br.mesa11.ConstantesMesa11;
import br.mesa11.visao.Java2sAutoComboBox;
import br.nnpe.ImageUtil;
import br.nnpe.Logger;
import br.nnpe.Util;
import br.recursos.Lang;
import br.tos.DadosJogoSrvMesa11;
import br.tos.DadosMesa11;
import br.tos.Mesa11TO;

public class ControleJogosCliente {
	private ChatWindow chatWindow;
	private boolean segundoUniforme;
	private boolean segundoUniformeCpu;
	private ControleChatCliente controleChatCliente;
	private JComboBox jComboBoxTimes = new JComboBox(
			new String[] { Lang.msg("semTimes") });
	private JComboBox jComboBoxTimesCpu = new JComboBox(
			new String[] { Lang.msg("semTimes") });
	private DadosMesa11 dadosMesa11;
	private MonitorJogo monitorJogo;
	private Mesa11Applet mesa11Applet;

	public DadosMesa11 getDadosMesa11() {
		return dadosMesa11;
	}

	public void setDadosMesa11(DadosMesa11 dadosMesa11) {
		this.dadosMesa11 = dadosMesa11;
	}

	public ControleJogosCliente(ChatWindow chatWindow,
			ControleChatCliente controleChatCliente, Mesa11Applet mesa11Applet) {
		super();
		this.chatWindow = chatWindow;
		this.controleChatCliente = controleChatCliente;
		this.mesa11Applet = mesa11Applet;
	}

	private Object enviarObjeto(Mesa11TO mesa11to) {
		if (mesa11Applet == null) {
			Logger.logar("enviarObjeto mesa11Applet null");
			return null;
		}
		return mesa11Applet.enviarObjeto(mesa11to);
	}

	public void criarJogo() {
		if (monitorJogo != null && monitorJogo.isAlive()) {
			JOptionPane.showMessageDialog(chatWindow.getMainPanel(),
					Lang.msg("jaEstaEmUmJogo"));
			return;
		}
		Mesa11TO mesa11to = new Mesa11TO();
		mesa11to.setComando(ConstantesMesa11.OBTER_TODOS_TIMES);
		Object ret = enviarObjeto(mesa11to);
		final JLabel uniforme = new JLabel() {
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(ConstantesMesa11.DIAMENTRO_BOTAO + 10,
						ConstantesMesa11.DIAMENTRO_BOTAO + 10);
			}
		};
		if (ret instanceof Mesa11TO) {
			mesa11to = (Mesa11TO) ret;
			String[] times = (String[]) mesa11to.getData();
			jComboBoxTimes = new JComboBox(times);
			String nomeTime = times[0];
			mesa11to = new Mesa11TO();
			mesa11to.setData(nomeTime);
			mesa11to.setComando(ConstantesMesa11.OBTER_TIME);
			ret = enviarObjeto(mesa11to);
			mesa11to = (Mesa11TO) ret;
			Time time = (Time) mesa11to.getData();
			uniforme.setIcon(new ImageIcon(BotaoUtils.desenhaUniforme(time, 1)));
			segundoUniforme = false;
		}
		JPanel panelComboTimes = new JPanel();
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
				return Lang.msg("cliqueSegundoUniforme");
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

		JPanel opcoesJogoPanel = new JPanel(new GridLayout(5, 2, 10, 10));
		opcoesJogoPanel.add(new JLabel() {
			@Override
			public String getText() {
				return Lang.msg("numeroJogadas");
			}
		});
		JComboBox numJogadaCombo = new JComboBox();
		for (int i = 3; i < 21; i++) {
			numJogadaCombo.addItem(new Integer(i));
		}
		numJogadaCombo.setSelectedIndex(4);
		opcoesJogoPanel.add(numJogadaCombo);

		opcoesJogoPanel.add(new JLabel() {
			@Override
			public String getText() {
				return Lang.msg("tempoJogoMinutos");
			}
		});
		JComboBox tempoJogoCombo = new JComboBox();
		tempoJogoCombo.addItem(new Integer(8));
		tempoJogoCombo.addItem(new Integer(10));
		tempoJogoCombo.addItem(new Integer(16));
		tempoJogoCombo.addItem(new Integer(20));
		tempoJogoCombo.addItem(new Integer(40));
		tempoJogoCombo.addItem(new Integer(60));
		tempoJogoCombo.addItem(new Integer(90));
		opcoesJogoPanel.add(tempoJogoCombo);
		opcoesJogoPanel.add(new JLabel() {
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
		tempoJogadaCombo.addItem(new Integer(90));
		tempoJogadaCombo.setSelectedIndex(1);
		opcoesJogoPanel.add(tempoJogadaCombo);

		JComboBox campoBolaCombo = new JComboBox();
		campoBolaCombo.addItem(Lang.msg(ConstantesMesa11.BOLA));
		campoBolaCombo.addItem(Lang.msg(ConstantesMesa11.CAMPO_CIMA));
		campoBolaCombo.addItem(Lang.msg(ConstantesMesa11.CAMPO_BAIXO));
		opcoesJogoPanel.add(new JLabel() {
			@Override
			public String getText() {
				return Lang.msg("campoBola");
			}
		});
		opcoesJogoPanel.add(campoBolaCombo);

		JPanel iniciarJogoPanel = new JPanel(new BorderLayout());
		iniciarJogoPanel.add(escolhaTimesPanel, BorderLayout.CENTER);
		iniciarJogoPanel.add(opcoesJogoPanel, BorderLayout.NORTH);

		opcoesJogoPanel.add(new JLabel() {
			@Override
			public String getText() {
				return Lang.msg("senhaJogo");
			}
		});
		JTextField jTextFieldSenhaJogo = new JTextField();
		opcoesJogoPanel.add(jTextFieldSenhaJogo);

		int result = JOptionPane.showConfirmDialog(chatWindow.getMainPanel(),
				iniciarJogoPanel, Lang.msg("criarJogo"),
				JOptionPane.YES_NO_OPTION);
		if (result == JOptionPane.YES_OPTION) {
			DadosJogoSrvMesa11 dadosJogoSrvMesa11 = new DadosJogoSrvMesa11();
			String jogador = controleChatCliente.getSessaoCliente()
					.getNomeJogador();
			dadosJogoSrvMesa11.setNomeCriador(jogador);
			String nomeTime = (String) jComboBoxTimes.getSelectedItem();
			dadosJogoSrvMesa11.setTimeCasa(nomeTime);
			dadosJogoSrvMesa11.setSegundoUniformeTimeCasa(segundoUniforme);
			dadosJogoSrvMesa11.setTempoJogo((Integer) tempoJogoCombo
					.getSelectedItem());
			dadosJogoSrvMesa11.setNumeroJogadas((Integer) numJogadaCombo
					.getSelectedItem());
			dadosJogoSrvMesa11.setTempoJogoJogada((Integer) tempoJogadaCombo
					.getSelectedItem());
			dadosJogoSrvMesa11.setBolaCampoCasa(Lang
					.key((String) campoBolaCombo.getSelectedItem()));
			dadosJogoSrvMesa11.setSenhaJogo(jTextFieldSenhaJogo.getText());
			mesa11to = new Mesa11TO();
			mesa11to.setComando(ConstantesMesa11.CRIAR_JOGO);
			mesa11to.setData(dadosJogoSrvMesa11);
			ret = enviarObjeto(mesa11to);
			if (ret instanceof Mesa11TO) {
				mesa11to = (Mesa11TO) ret;
				dadosJogoSrvMesa11 = (DadosJogoSrvMesa11) mesa11to.getData();
				monitorJogo = new MonitorJogo(controleChatCliente, this,
						dadosJogoSrvMesa11, mesa11Applet,
						dadosJogoSrvMesa11.getTimeCasa());
				monitorJogo.start();
			}
		}
	}

	public void entrarJogo(String jogoSelecionado) {
		if (monitorJogo != null && monitorJogo.isAlive()) {
			JOptionPane.showMessageDialog(chatWindow.getMainPanel(),
					Lang.msg("jaEstaEmUmJogo"));
			return;
		}
		Mesa11TO mesa11to = new Mesa11TO();
		mesa11to.setComando(ConstantesMesa11.OBTER_DADOS_JOGO);
		mesa11to.setData(jogoSelecionado);
		Object ret = enviarObjeto(mesa11to);
		if (!(ret instanceof Mesa11TO)) {
			return;
		}
		mesa11to = (Mesa11TO) ret;
		final DadosJogoSrvMesa11 dadosJogoSrvMesa11 = (DadosJogoSrvMesa11) mesa11to
				.getData();

		final JLabel uniformeCasa = new JLabel() {
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(ConstantesMesa11.DIAMENTRO_BOTAO + 10,
						ConstantesMesa11.DIAMENTRO_BOTAO + 10);
			}
		};
		mesa11to = new Mesa11TO();
		mesa11to.setData(dadosJogoSrvMesa11.getTimeCasa());
		mesa11to.setComando(ConstantesMesa11.OBTER_TIME);
		ret = enviarObjeto(mesa11to);
		mesa11to = (Mesa11TO) ret;
		Time timeCasa = (Time) mesa11to.getData();
		uniformeCasa.setIcon(new ImageIcon(BotaoUtils.desenhaUniforme(timeCasa,
				dadosJogoSrvMesa11.isSegundoUniformeTimeCasa() ? 2 : 1)));
		final JLabel uniforme = new JLabel() {
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(ConstantesMesa11.DIAMENTRO_BOTAO + 10,
						ConstantesMesa11.DIAMENTRO_BOTAO + 10);
			}
		};
		mesa11to = new Mesa11TO();
		mesa11to.setComando(ConstantesMesa11.OBTER_TODOS_TIMES);
		ret = enviarObjeto(mesa11to);
		if (ret instanceof Mesa11TO) {
			mesa11to = (Mesa11TO) ret;
			String[] times = (String[]) mesa11to.getData();
			Vector timesDisponiveis = new Vector();
			for (int i = 0; i < times.length; i++) {
				if (!dadosJogoSrvMesa11.getTimeCasa().equals(times[i])) {
					timesDisponiveis.add(times[i]);
				}
			}
			jComboBoxTimes = new JComboBox(timesDisponiveis);
			String nomeTime = (String) timesDisponiveis.get(0);
			mesa11to = new Mesa11TO();
			mesa11to.setData(nomeTime);
			mesa11to.setComando(ConstantesMesa11.OBTER_TIME);
			ret = enviarObjeto(mesa11to);
			mesa11to = (Mesa11TO) ret;
			Time time = (Time) mesa11to.getData();
			uniforme.setIcon(new ImageIcon(BotaoUtils.desenhaUniforme(time, 1)));
			segundoUniforme = false;
		}
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
		uniformesPanel.add(uniformeCasa);
		uniformesPanel.add(uniforme);
		JPanel panelComboTimes = new JPanel(new GridLayout(1, 2));
		panelComboTimes.setBorder(new TitledBorder("") {
			@Override
			public String getTitle() {
				return Lang.msg("escolhaTime");
			}
		});
		panelComboTimes.add(new JLabel(dadosJogoSrvMesa11.getTimeCasa() + " - "
				+ dadosJogoSrvMesa11.getNomeCriador()));
		panelComboTimes.add(jComboBoxTimes);
		JPanel escolhaTimesPanel = new JPanel(new BorderLayout());
		escolhaTimesPanel.add(panelComboTimes, BorderLayout.NORTH);
		escolhaTimesPanel.add(uniformesPanel, BorderLayout.CENTER);

		JPanel opcoesJogoPanel = new JPanel(new GridLayout(5, 2, 10, 10));
		opcoesJogoPanel.add(new JLabel() {
			@Override
			public String getText() {
				return Lang.msg("numeroJogadas");
			}
		});

		opcoesJogoPanel.add(new JLabel() {
			@Override
			public String getText() {
				return "" + dadosJogoSrvMesa11.getNumeroJogadas();
			}
		});
		opcoesJogoPanel.add(new JLabel() {
			@Override
			public String getText() {
				return Lang.msg("tempoJogoMinutos");
			}
		});

		opcoesJogoPanel.add(new JLabel() {
			@Override
			public String getText() {
				return "" + dadosJogoSrvMesa11.getTempoJogo();
			}
		});
		opcoesJogoPanel.add(new JLabel() {
			@Override
			public String getText() {
				return Lang.msg("tempoJogadaSegundos");
			}
		});

		opcoesJogoPanel.add(new JLabel() {
			@Override
			public String getText() {
				return "" + dadosJogoSrvMesa11.getTempoJogoJogada();
			}
		});
		JComboBox campoBolaCombo = new JComboBox();
		if (ConstantesMesa11.BOLA.equals(dadosJogoSrvMesa11.getBolaCampoCasa())) {
			campoBolaCombo.addItem(Lang.msg(ConstantesMesa11.CAMPO_CIMA));
			campoBolaCombo.addItem(Lang.msg(ConstantesMesa11.CAMPO_BAIXO));
		} else {
			campoBolaCombo.addItem(Lang.msg(ConstantesMesa11.BOLA));
		}
		opcoesJogoPanel.add(new JLabel() {
			@Override
			public String getText() {
				return Lang.msg("campoBola");
			}
		});
		opcoesJogoPanel.add(campoBolaCombo);

		JPanel iniciarJogoPanel = new JPanel(new BorderLayout());
		iniciarJogoPanel.add(escolhaTimesPanel, BorderLayout.CENTER);
		iniciarJogoPanel.add(opcoesJogoPanel, BorderLayout.NORTH);

		opcoesJogoPanel.add(new JLabel() {
			@Override
			public String getText() {
				return Lang.msg("senhaJogo");
			}
		});
		JTextField jTextFieldSenhaJogo = new JTextField();
		opcoesJogoPanel.add(jTextFieldSenhaJogo);
		int result = JOptionPane.showConfirmDialog(chatWindow.getMainPanel(),
				iniciarJogoPanel, Lang.msg("criarJogo"),
				JOptionPane.YES_NO_OPTION);
		if (result == JOptionPane.YES_OPTION) {
			String jogador = controleChatCliente.getSessaoCliente()
					.getNomeJogador();
			String nomeTime = (String) jComboBoxTimes.getSelectedItem();
			dadosJogoSrvMesa11.setTimeVisita(nomeTime);
			dadosJogoSrvMesa11.setSegundoUniformeTimeVisita(segundoUniforme);
			dadosJogoSrvMesa11.setBolaCampoVisita(Lang
					.key((String) campoBolaCombo.getSelectedItem()));
			dadosJogoSrvMesa11.setSenhaJogo(jTextFieldSenhaJogo.getText());
			dadosJogoSrvMesa11.setNomeVisitante(controleChatCliente
					.getSessaoCliente().getNomeJogador());
			mesa11to = new Mesa11TO();
			mesa11to.setComando(ConstantesMesa11.ENTRAR_JOGO);
			mesa11to.setData(dadosJogoSrvMesa11);
			ret = enviarObjeto(mesa11to);
			if (ret instanceof Mesa11TO) {
				mesa11to = (Mesa11TO) ret;
				DadosJogoSrvMesa11 dadosJogoSrvMesa11Jogo = (DadosJogoSrvMesa11) mesa11to
						.getData();
				Logger.logar("Entar Jogo");
				monitorJogo = new MonitorJogo(controleChatCliente, this,
						dadosJogoSrvMesa11Jogo, mesa11Applet,
						dadosJogoSrvMesa11.getTimeVisita());
				monitorJogo.start();
			}
		}

	}

	public void verDetalhesJogo(String jogoSelecionado) {

		Mesa11TO mesa11to = new Mesa11TO();
		mesa11to.setComando(ConstantesMesa11.OBTER_DADOS_JOGO);
		mesa11to.setData(jogoSelecionado);
		Object ret = enviarObjeto(mesa11to);
		if (!(ret instanceof Mesa11TO)) {
			return;
		}
		mesa11to = (Mesa11TO) ret;
		final DadosJogoSrvMesa11 dadosJogoSrvMesa11 = (DadosJogoSrvMesa11) mesa11to
				.getData();

		final JLabel uniformeCasa = new JLabel() {
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(ConstantesMesa11.DIAMENTRO_BOTAO + 10,
						ConstantesMesa11.DIAMENTRO_BOTAO + 10);
			}
		};
		mesa11to = new Mesa11TO();
		mesa11to.setData(dadosJogoSrvMesa11.getTimeCasa());
		mesa11to.setComando(ConstantesMesa11.OBTER_TIME);
		ret = enviarObjeto(mesa11to);
		mesa11to = (Mesa11TO) ret;
		Time timeCasa = (Time) mesa11to.getData();
		uniformeCasa.setIcon(new ImageIcon(BotaoUtils.desenhaUniforme(timeCasa,
				dadosJogoSrvMesa11.isSegundoUniformeTimeCasa() ? 2 : 1)));
		final JLabel uniforme = new JLabel() {
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(ConstantesMesa11.DIAMENTRO_BOTAO + 10,
						ConstantesMesa11.DIAMENTRO_BOTAO + 10);
			}
		};
		if (!Util.isNullOrEmpty(dadosJogoSrvMesa11.getTimeVisita())) {
			mesa11to = new Mesa11TO();
			mesa11to.setData(dadosJogoSrvMesa11.getTimeVisita());
			mesa11to.setComando(ConstantesMesa11.OBTER_TIME);
			ret = enviarObjeto(mesa11to);
			mesa11to = (Mesa11TO) ret;
			Time timeVisita = (Time) mesa11to.getData();
			uniforme.setIcon(new ImageIcon(BotaoUtils.desenhaUniforme(
					timeVisita,
					dadosJogoSrvMesa11.isSegundoUniformeTimeVisita() ? 2 : 1)));
		}
		JPanel uniformesPanel = new JPanel();
		uniformesPanel.setBorder(new TitledBorder("") {
			@Override
			public String getTitle() {
				return Lang.msg("times");
			}
		});
		uniformesPanel.add(uniformeCasa);
		uniformesPanel.add(uniforme);
		JPanel panelComboTimes = new JPanel(new GridLayout(1, 2));
		panelComboTimes.setBorder(new TitledBorder("") {
			@Override
			public String getTitle() {
				return Lang.msg("jogadores");
			}
		});
		panelComboTimes.add(new JLabel(dadosJogoSrvMesa11.getTimeCasa() + " - "
				+ dadosJogoSrvMesa11.getGolsCasa() + " -"
				+ dadosJogoSrvMesa11.getNomeCriador()));
		panelComboTimes.add(new JLabel(dadosJogoSrvMesa11.getTimeVisita()
				+ " - "
				+ +dadosJogoSrvMesa11.getGolsVisita()
				+ " -"
				+ (dadosJogoSrvMesa11.isJogoVsCpu() ? "CPU"
						: dadosJogoSrvMesa11.getNomeVisitante())));
		JPanel escolhaTimesPanel = new JPanel(new BorderLayout());
		escolhaTimesPanel.add(panelComboTimes, BorderLayout.NORTH);
		escolhaTimesPanel.add(uniformesPanel, BorderLayout.CENTER);

		JPanel opcoesJogoPanel = new JPanel(new GridLayout(3, 2, 10, 10));
		opcoesJogoPanel.add(new JLabel() {
			@Override
			public String getText() {
				return Lang.msg("numeroJogadas");
			}
		});

		opcoesJogoPanel.add(new JLabel() {
			@Override
			public String getText() {
				return "" + dadosJogoSrvMesa11.getNumeroJogadas();
			}
		});
		opcoesJogoPanel.add(new JLabel() {
			@Override
			public String getText() {
				return Lang.msg("tempoJogoMinutos");
			}
		});

		opcoesJogoPanel.add(new JLabel() {
			@Override
			public String getText() {
				return "" + dadosJogoSrvMesa11.getTempoJogo();
			}
		});
		opcoesJogoPanel.add(new JLabel() {
			@Override
			public String getText() {
				return Lang.msg("tempoJogadaSegundos");
			}
		});

		opcoesJogoPanel.add(new JLabel() {
			@Override
			public String getText() {
				return "" + dadosJogoSrvMesa11.getTempoJogoJogada();
			}
		});

		JPanel iniciarJogoPanel = new JPanel(new BorderLayout());
		iniciarJogoPanel.add(escolhaTimesPanel, BorderLayout.CENTER);
		iniciarJogoPanel.add(opcoesJogoPanel, BorderLayout.NORTH);

		JOptionPane.showMessageDialog(chatWindow.getMainPanel(),
				iniciarJogoPanel, Lang.msg("criarJogo"),
				JOptionPane.INFORMATION_MESSAGE);

	}

	public boolean verificaJogosNasListas(String nomeJogo) {
		if (dadosMesa11 == null) {
			return false;
		}
		if (dadosMesa11.getJogosCriados().contains(nomeJogo)) {
			return true;
		}
		if (dadosMesa11.getJogosAndamento().contains(nomeJogo)) {
			return true;
		}
		return false;
	}

	public void sairJogo() {
		if (monitorJogo != null) {
			monitorJogo.setJogoTerminado(true);
			monitorJogo.interrupt();
		}
		monitorJogo = null;
	}

	public void criarJogoVsCPU() {
		if (monitorJogo != null && monitorJogo.isAlive()) {
			JOptionPane.showMessageDialog(chatWindow.getMainPanel(),
					Lang.msg("jaEstaEmUmJogo"));
			return;
		}
		Mesa11TO mesa11to = new Mesa11TO();
		mesa11to.setComando(ConstantesMesa11.OBTER_TODOS_TIMES);
		Object ret = enviarObjeto(mesa11to);
		final JLabel uniforme = new JLabel() {
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(ConstantesMesa11.DIAMENTRO_BOTAO + 10,
						ConstantesMesa11.DIAMENTRO_BOTAO + 10);
			}
		};
		final JLabel uniformeCpu = new JLabel() {
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(ConstantesMesa11.DIAMENTRO_BOTAO + 10,
						ConstantesMesa11.DIAMENTRO_BOTAO + 10);
			}
		};
		if (ret instanceof Mesa11TO) {
			mesa11to = (Mesa11TO) ret;
			String[] times = (String[]) mesa11to.getData();
			jComboBoxTimes = new JComboBox(times);
			jComboBoxTimesCpu = new JComboBox(times);
			String nomeTime = times[0];
			mesa11to = new Mesa11TO();
			mesa11to.setData(nomeTime);
			mesa11to.setComando(ConstantesMesa11.OBTER_TIME);
			ret = enviarObjeto(mesa11to);
			mesa11to = (Mesa11TO) ret;
			Time time = (Time) mesa11to.getData();
			if (Util.isNullOrEmpty(time.getImagem())) {
				uniforme.setIcon(new ImageIcon(BotaoUtils.desenhaUniforme(time,
						1)));
				uniformeCpu.setIcon(new ImageIcon(BotaoUtils.desenhaUniforme(
						time, 1)));
			} else {
				ImageIcon icon = ImageUtil.carregarImagem(this.mesa11Applet
						.getCodeBase() + "midia/" + time.getImagem());
				uniforme.setIcon(icon);
				uniformeCpu.setIcon(icon);
			}
			segundoUniforme = false;
			segundoUniformeCpu = false;
		}

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
					if (Util.isNullOrEmpty(time.getImagem())) {
						uniforme.setIcon(new ImageIcon(BotaoUtils
								.desenhaUniforme(time, 1)));
					} else {
						ImageIcon icon = ImageUtil.carregarImagem(mesa11Applet
								.getCodeBase() + "midia/" + time.getImagem());
						uniforme.setIcon(icon);
					}
					segundoUniforme = false;
				}
			}
		});
		jComboBoxTimesCpu.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if (arg0.getStateChange() == ItemEvent.SELECTED) {
					String nomeTime = (String) jComboBoxTimesCpu
							.getSelectedItem();
					Mesa11TO mesa11to = new Mesa11TO();
					mesa11to.setData(nomeTime);
					mesa11to.setComando(ConstantesMesa11.OBTER_TIME);
					Object ret = enviarObjeto(mesa11to);
					mesa11to = (Mesa11TO) ret;
					Time time = (Time) mesa11to.getData();
					if (Util.isNullOrEmpty(time.getImagem())) {
						uniformeCpu.setIcon(new ImageIcon(BotaoUtils
								.desenhaUniforme(time, 1)));
					} else {
						ImageIcon icon = ImageUtil.carregarImagem(mesa11Applet
								.getCodeBase() + "midia/" + time.getImagem());
						uniformeCpu.setIcon(icon);
					}
					segundoUniformeCpu = false;
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
				if (Util.isNullOrEmpty(time.getImagem())) {
					uniforme.setIcon(new ImageIcon(BotaoUtils.desenhaUniforme(
							time, segundoUniforme ? 2 : 1)));
				}
			}
		});
		uniformeCpu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				segundoUniformeCpu = !segundoUniformeCpu;
				String nomeTime = (String) jComboBoxTimesCpu.getSelectedItem();
				Mesa11TO mesa11to = new Mesa11TO();
				mesa11to.setData(nomeTime);
				mesa11to.setComando(ConstantesMesa11.OBTER_TIME);
				Object ret = enviarObjeto(mesa11to);
				mesa11to = (Mesa11TO) ret;
				Time time = (Time) mesa11to.getData();
				if (Util.isNullOrEmpty(time.getImagem())) {
					uniformeCpu.setIcon(new ImageIcon(BotaoUtils
							.desenhaUniforme(time, segundoUniformeCpu ? 2 : 1)));
				}
			}
		});

		JPanel uniformesPanel = new JPanel();
		uniformesPanel.setBorder(new TitledBorder("") {
			@Override
			public String getTitle() {
				return Lang.msg("cliqueSegundoUniforme");
			}
		});

		uniformesPanel.add(uniforme);
		uniformesPanel.add(uniformeCpu);
		JPanel panelComboTimes = new JPanel(new GridLayout(1, 2));
		JPanel panelComboTimes1 = new JPanel();
		panelComboTimes1.setBorder(new TitledBorder("") {
			@Override
			public String getTitle() {
				return Lang.msg("escolhaTime");
			}
		});
		panelComboTimes1.add(jComboBoxTimes);
		JPanel panelComboTimes2 = new JPanel();
		panelComboTimes2.setBorder(new TitledBorder("") {
			@Override
			public String getTitle() {
				return Lang.msg("escolhaTimeCpu");
			}
		});

		panelComboTimes2.add(jComboBoxTimesCpu);
		panelComboTimes.add(panelComboTimes1);
		panelComboTimes.add(panelComboTimes2);
		JPanel escolhaTimesPanel = new JPanel(new BorderLayout());
		escolhaTimesPanel.add(panelComboTimes, BorderLayout.NORTH);
		escolhaTimesPanel.add(uniformesPanel, BorderLayout.CENTER);

		JPanel opcoesJogoPanel = new JPanel(new GridLayout(4, 2, 10, 10));
		opcoesJogoPanel.add(new JLabel() {
			@Override
			public String getText() {
				return Lang.msg("numeroJogadas");
			}
		});
		JComboBox numJogadaCombo = new JComboBox();
		for (int i = 3; i < 21; i++) {
			numJogadaCombo.addItem(new Integer(i));
		}
		numJogadaCombo.setSelectedIndex(4);
		opcoesJogoPanel.add(numJogadaCombo);

		opcoesJogoPanel.add(new JLabel() {
			@Override
			public String getText() {
				return Lang.msg("tempoJogoMinutos");
			}
		});
		JComboBox tempoJogoCombo = new JComboBox();
		tempoJogoCombo.addItem(new Integer(8));
		tempoJogoCombo.addItem(new Integer(10));
		tempoJogoCombo.addItem(new Integer(16));
		tempoJogoCombo.addItem(new Integer(20));
		tempoJogoCombo.addItem(new Integer(40));
		tempoJogoCombo.addItem(new Integer(60));
		tempoJogoCombo.addItem(new Integer(90));
		opcoesJogoPanel.add(tempoJogoCombo);
		opcoesJogoPanel.add(new JLabel() {
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
		tempoJogadaCombo.addItem(new Integer(90));
		tempoJogadaCombo.setSelectedIndex(1);
		opcoesJogoPanel.add(tempoJogadaCombo);

		JComboBox campoBolaCombo = new JComboBox();
		campoBolaCombo.addItem(Lang.msg(ConstantesMesa11.BOLA));
		campoBolaCombo.addItem(Lang.msg(ConstantesMesa11.CAMPO_CIMA));
		campoBolaCombo.addItem(Lang.msg(ConstantesMesa11.CAMPO_BAIXO));
		opcoesJogoPanel.add(new JLabel() {
			@Override
			public String getText() {
				return Lang.msg("campoBola");
			}
		});
		opcoesJogoPanel.add(campoBolaCombo);

		JPanel iniciarJogoPanel = new JPanel(new BorderLayout());
		iniciarJogoPanel.add(escolhaTimesPanel, BorderLayout.CENTER);
		iniciarJogoPanel.add(opcoesJogoPanel, BorderLayout.NORTH);

		while (jComboBoxTimesCpu.getItemCount() > 1
				&& jComboBoxTimes.getSelectedItem().equals(
						jComboBoxTimesCpu.getSelectedItem())) {
			jComboBoxTimesCpu.setSelectedIndex(Util.intervalo(0,
					jComboBoxTimesCpu.getItemCount() - 1));
			Logger.logar("Selecionado Outro Time Baixo");
		}

		int result = JOptionPane.showConfirmDialog(chatWindow.getMainPanel(),
				iniciarJogoPanel, Lang.msg("criarJogo"),
				JOptionPane.YES_NO_OPTION);
		if (result == JOptionPane.YES_OPTION) {
			DadosJogoSrvMesa11 dadosJogoSrvMesa11 = new DadosJogoSrvMesa11();
			String jogador = controleChatCliente.getSessaoCliente()
					.getNomeJogador();
			dadosJogoSrvMesa11.setNomeCriador(jogador);
			String nomeTime = (String) jComboBoxTimes.getSelectedItem();
			String nomeTimeCpu = (String) jComboBoxTimesCpu.getSelectedItem();
			if (nomeTime.equals(nomeTimeCpu)) {
				JOptionPane.showMessageDialog(chatWindow.getMainPanel(),
						Lang.msg("timesIguais"));
				return;
			}
			dadosJogoSrvMesa11.setTimeCasa(nomeTime);
			dadosJogoSrvMesa11.setTimeVisita(nomeTimeCpu);
			dadosJogoSrvMesa11.setSegundoUniformeTimeCasa(segundoUniforme);
			dadosJogoSrvMesa11.setSegundoUniformeTimeVisita(segundoUniformeCpu);
			dadosJogoSrvMesa11.setTempoJogo((Integer) tempoJogoCombo
					.getSelectedItem());
			dadosJogoSrvMesa11.setNumeroJogadas((Integer) numJogadaCombo
					.getSelectedItem());
			dadosJogoSrvMesa11.setTempoJogoJogada((Integer) tempoJogadaCombo
					.getSelectedItem());
			dadosJogoSrvMesa11.setBolaCampoCasa(Lang
					.key((String) campoBolaCombo.getSelectedItem()));
			if (ConstantesMesa11.BOLA.equals(dadosJogoSrvMesa11
					.getBolaCampoCasa())) {
				if (Math.random() > 0.5)
					dadosJogoSrvMesa11
							.setBolaCampoVisita(ConstantesMesa11.CAMPO_CIMA);
				else
					dadosJogoSrvMesa11
							.setBolaCampoVisita(ConstantesMesa11.CAMPO_BAIXO);
			} else {
				dadosJogoSrvMesa11.setBolaCampoVisita(ConstantesMesa11.BOLA);
			}
			mesa11to = new Mesa11TO();
			mesa11to.setComando(ConstantesMesa11.CRIAR_JOGO_CPU);
			mesa11to.setData(dadosJogoSrvMesa11);
			ret = enviarObjeto(mesa11to);
			if (ret instanceof Mesa11TO) {
				mesa11to = (Mesa11TO) ret;
				dadosJogoSrvMesa11 = (DadosJogoSrvMesa11) mesa11to.getData();
				monitorJogo = new MonitorJogo(controleChatCliente, this,
						dadosJogoSrvMesa11, mesa11Applet,
						dadosJogoSrvMesa11.getTimeCasa());
				monitorJogo.start();
			}
		}

	}

	public String getVersao() {
		return mesa11Applet.getVersao();
	}

	public void criarCampeonato() {
		Mesa11TO mesa11to = new Mesa11TO();
		mesa11to.setComando(ConstantesMesa11.OBTER_TODOS_TIMES);
		Object ret = enviarObjeto(mesa11to);
		if (ret instanceof Mesa11TO) {
			mesa11to = (Mesa11TO) ret;
			String[] times = (String[]) mesa11to.getData();
			final DefaultListModel defaultListModelTimes = new DefaultListModel();
			final DefaultListModel defaultListModelTimesSelecionados = new DefaultListModel();
			for (int i = 0; i < times.length; i++) {
				defaultListModelTimes.addElement(times[i]);
			}
			final JList listTimes = new JList(defaultListModelTimes);
			final JList listTimesSelecionados = new JList(
					defaultListModelTimesSelecionados);
			JPanel escolhaTimesPanel = new JPanel(new BorderLayout());
			escolhaTimesPanel.setBorder(new TitledBorder("times") {
				@Override
				public String getTitle() {
					return Lang.msg("times");
				}
			});
			JPanel buttonsPanel = new JPanel(new GridLayout(6, 1));
			JButton esq = new JButton("<");
			esq.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (listTimesSelecionados.getSelectedIndex() == -1)
						return;
					defaultListModelTimes
							.addElement(defaultListModelTimesSelecionados
									.remove(listTimesSelecionados
											.getSelectedIndex()));
				}
			});
			JButton dir = new JButton(">");
			dir.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (listTimes.getSelectedIndex() == -1)
						return;
					defaultListModelTimesSelecionados
							.addElement(defaultListModelTimes.remove(listTimes
									.getSelectedIndex()));
				}

			});

			JButton esqAll = new JButton("<<");
			esqAll.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					int size = defaultListModelTimesSelecionados.size();
					for (int i = 0; i < size; i++) {
						defaultListModelTimes
								.addElement(defaultListModelTimesSelecionados
										.remove(0));
					}
				}

			});
			JButton dirAll = new JButton(">>");
			dirAll.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					int size = defaultListModelTimes.size();
					for (int i = 0; i < size; i++) {
						defaultListModelTimesSelecionados
								.addElement(defaultListModelTimes.remove(0));
					}
				}
			});
			buttonsPanel.add(dir);
			buttonsPanel.add(esq);
			buttonsPanel.add(dirAll);
			buttonsPanel.add(esqAll);

			JButton cima = new JButton("Cima") {
				@Override
				public String getText() {
					return Lang.msg("Cima");
				}
			};
			cima.setEnabled(false);
			JButton baixo = new JButton("Baixo") {
				@Override
				public String getText() {
					return Lang.msg("Baixo");
				}
			};
			baixo.setEnabled(false);
			buttonsPanel.add(cima);
			buttonsPanel.add(baixo);

			escolhaTimesPanel.add(buttonsPanel, BorderLayout.CENTER);
			escolhaTimesPanel.add(new JScrollPane(listTimes) {
				@Override
				public Dimension getPreferredSize() {
					return new Dimension(150, 300);
				}
			}, BorderLayout.WEST);
			escolhaTimesPanel.add(new JScrollPane(listTimesSelecionados) {
				@Override
				public Dimension getPreferredSize() {
					return new Dimension(150, 300);
				}
			}, BorderLayout.EAST);
			JPanel opcoesJogoPanel = new JPanel(new GridLayout(4, 2));
			opcoesJogoPanel.setBorder(new TitledBorder("campeonato") {
				@Override
				public String getTitle() {
					return Lang.msg("campeonato");
				}
			});
			opcoesJogoPanel.add(new JLabel() {
				@Override
				public String getText() {
					return Lang.msg("nomeCampeonato");
				}
			});
			JTextField nomeCampeonato = new JTextField();
			opcoesJogoPanel.add(nomeCampeonato);
			opcoesJogoPanel.add(new JLabel() {
				@Override
				public String getText() {
					return Lang.msg("numeroJogadas");
				}
			});
			JComboBox numJogadaCombo = new JComboBox();
			for (int i = 3; i < 21; i++) {
				numJogadaCombo.addItem(new Integer(i));
			}
			numJogadaCombo.setSelectedIndex(4);
			opcoesJogoPanel.add(numJogadaCombo);

			opcoesJogoPanel.add(new JLabel() {
				@Override
				public String getText() {
					return Lang.msg("tempoJogoMinutos");
				}
			});
			JComboBox tempoJogoCombo = new JComboBox();
			tempoJogoCombo.addItem(new Integer(8));
			tempoJogoCombo.addItem(new Integer(10));
			tempoJogoCombo.addItem(new Integer(16));
			tempoJogoCombo.addItem(new Integer(20));
			tempoJogoCombo.addItem(new Integer(40));
			tempoJogoCombo.addItem(new Integer(60));
			tempoJogoCombo.addItem(new Integer(90));
			opcoesJogoPanel.add(tempoJogoCombo);
			opcoesJogoPanel.add(new JLabel() {
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
			tempoJogadaCombo.addItem(new Integer(90));
			tempoJogadaCombo.setSelectedIndex(1);
			opcoesJogoPanel.add(tempoJogadaCombo);

			JPanel jogadoresPanel = new JPanel(new BorderLayout());
			jogadoresPanel.setBorder(new TitledBorder("jogadores") {
				@Override
				public String getTitle() {
					return Lang.msg("jogadores");
				}
			});
			mesa11to = new Mesa11TO();
			mesa11to.setComando(ConstantesMesa11.OBTER_TODOS_JOGADORES);
			ret = enviarObjeto(mesa11to);
			String[] jogadores = new String[] { "" };
			if (ret instanceof Mesa11TO) {
				mesa11to = (Mesa11TO) ret;
				jogadores = (String[]) mesa11to.getData();

			}
			List lista = new ArrayList();
			for (int i = 0; i < jogadores.length; i++) {
				lista.add(jogadores[i]);
			}
			final Java2sAutoComboBox java2sAutoComboBox = new Java2sAutoComboBox(
					lista);
			jogadoresPanel.add(java2sAutoComboBox, BorderLayout.NORTH);

			final DefaultListModel defaultListModelJogadores = new DefaultListModel();
			final JList listaJogadores = new JList(defaultListModelJogadores) {
				@Override
				public Dimension getPreferredSize() {
					return new Dimension(150, 120);
				}
			};
			JButton add = new JButton("adicionar") {
				@Override
				public String getText() {
					return Lang.msg("adicionar");
				}
			};
			add.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (java2sAutoComboBox.getSelectedIndex() == -1)
						return;
					defaultListModelJogadores.addElement(java2sAutoComboBox
							.getSelectedItem());
				}

			});

			JButton rem = new JButton("remover") {
				@Override
				public String getText() {
					return Lang.msg("remover");
				}
			};
			rem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (listaJogadores.getSelectedIndex() == -1)
						return;
					defaultListModelJogadores.remove(listaJogadores
							.getSelectedIndex());
				}

			});
			JPanel botoesJogadores = new JPanel(new GridLayout(1, 2));
			botoesJogadores.add(add);
			botoesJogadores.add(rem);
			jogadoresPanel.add(botoesJogadores, BorderLayout.CENTER);
			jogadoresPanel.add(listaJogadores, BorderLayout.SOUTH);

			JPanel jogadoresEGeralPanel = new JPanel(new BorderLayout());
			jogadoresEGeralPanel.add(jogadoresPanel, BorderLayout.SOUTH);
			jogadoresEGeralPanel.add(opcoesJogoPanel, BorderLayout.CENTER);

			JPanel panel = new JPanel(new BorderLayout());
			panel.add(escolhaTimesPanel, BorderLayout.EAST);
			panel.add(jogadoresEGeralPanel, BorderLayout.CENTER);
			JOptionPane.showMessageDialog(chatWindow.getMainPanel(), panel);

		}

	}
}
