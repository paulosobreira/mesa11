package br.mesa11.cliente;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import br.applet.MesaAppletLocalDummy;
import br.hibernate.Time;
import br.mesa11.ConstantesMesa11;
import br.nnpe.ImageUtil;
import br.nnpe.Logger;
import br.nnpe.Util;
import br.nnpe.tos.NnpeTO;
import br.nnpe.tos.SessaoCliente;
import br.recursos.CarregadorRecursos;
import br.recursos.Lang;
import br.tos.DadosJogoSrvMesa11;
import br.tos.DadosMesa11;

/**
 * @author paulo.sobreira
 * 
 */
public class ChatWindow {
	protected BufferedImage img;
	private JPanel mainPanel;
	private ControleChatCliente controleChatCliente;
	private JList listaClientes = new JList();
	private JList listaJogosCriados = new JList(new DefaultListModel());
	private JList listaJogosAndamento = new JList(new DefaultListModel());
	private JTextArea textAreaChat = new JTextArea();
	private JTextField textoEnviar = new JTextField();
	private HashMap mapaJogosCriados = new HashMap();
	private HashMap mapaJogosAndamento = new HashMap();
	private JButton verCampeonato = new JButton("verCampeonato") {

		public String getText() {

			return Lang.msg("verCampeonato");
		}
	};
	private JButton entrarJogo = new JButton("Entrar Jogo") {

		public String getText() {

			return Lang.msg("entrarJogo");
		}
	};
	private JButton criarJogo = new JButton("Criar Jogo") {

		public String getText() {

			return Lang.msg("criarJogo");
		}
	};
	private JButton criarJogoVsCPU = new JButton("Criar Jogo vs CPU") {

		public String getText() {

			return Lang.msg("criarJogoVsCPU");
		}
	};

	private JButton criarTime = new JButton("criarTime") {

		public String getText() {

			return Lang.msg("criarTime");
		}
	};

	private JButton editarTime = new JButton("editarTime") {

		public String getText() {

			return Lang.msg("editarTime");
		}
	};

	private JButton verDetalhes = new JButton("Ver Detalhes") {

		public String getText() {

			return Lang.msg("verDetalhes");
		}
	};
	private JButton sairJogo = new JButton("sairJogo") {

		public String getText() {

			return Lang.msg("sairJogo");
		}
	};

	private JButton classificacao = new JButton("classificacao") {

		public String getText() {

			return Lang.msg("classificacao");
		}
	};

	private JButton criarCampeonato = new JButton("campeonato") {

		public String getText() {

			return Lang.msg("campeonato");
		}
	};

	private JComboBox comboIdiomas = new JComboBox(new String[] {
			Lang.msg("pt"), Lang.msg("en") });
	private JButton sobre = new JButton("Sobre") {

		public String getText() {

			return Lang.msg("sobre");
		}
	};

	private Component compTransp(JComponent c) {
		c.setBackground(new Color(255, 255, 255, 0));
		return c;
	}

	private JLabel infoLabel1 = new JLabel();
	private Set chatTimes = new HashSet();
	private SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	public ChatWindow(ControleChatCliente controleChatCliente) {
		img = ImageUtil.geraResize(CarregadorRecursos
				.carregaBufferedImage("mesa11-bkg.png"), 1.30, 0.79);

		mainPanel = new JPanel(new BorderLayout()) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D graphics2d = (Graphics2D) g;
				if (img != null && Logger.desenhaBG){
					graphics2d.drawImage(img, null, 0, 0);
				}
			}
		};
		if (controleChatCliente != null) {
			this.controleChatCliente = controleChatCliente;
			controleChatCliente.setChatWindow(this);
		}
		gerarLayout();
		gerarAcoes();
		if (controleChatCliente != null) {
			atualizaInfo();
		}
	}

	private void gerarAcoes() {
		ActionListener actionListener = new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				Thread enviarTexto = new Thread(new Runnable() {

					public void run() {
						try {
							controleChatCliente.enviarTexto(textoEnviar
									.getText());
							textoEnviar.setText("");
						} catch (Exception e) {
							Logger.logarExept(e);
						}
					}
				});
				enviarTexto.start();
			}

		};
		textoEnviar.addActionListener(actionListener);
		criarJogo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				controleChatCliente.criarJogo();
			}

		});

		criarCampeonato.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				controleChatCliente.criarCampeonato();
			}

		});

		verCampeonato.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				controleChatCliente.verCampeonato();
			}

		});

		criarJogoVsCPU.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				controleChatCliente.criarJogoVsCPU();
			}

		});
		entrarJogo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controleChatCliente.entarJogo();
			}

		});
		verDetalhes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controleChatCliente.verDetalhesJogo();
				controleChatCliente.verDetalhesJogador();

			}

		});
		classificacao.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controleChatCliente.verClassificacao();

			}

		});

		criarTime.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controleChatCliente.criarTime();

			}

		});
		editarTime.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controleChatCliente.editarTime();

			}

		});
		sairJogo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controleChatCliente.sairJogo();

			}

		});

		sobre.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String msg = Lang.msg("feitopor") + "  Paulo Sobreira \n "
						+ "sowbreira@gmail.com \n"
						+ "sowbreira.appspot.com/ \n" + "Maio de 2010 \n ";

				JOptionPane.showMessageDialog(getMainPanel(), msg, Lang
						.msg("autor"), JOptionPane.INFORMATION_MESSAGE);
				verLogs();
			}
		});

	}
	
	protected void verLogs() {
		JTextArea area = new JTextArea(20, 50);
		Set top = Logger.topExceptions.keySet();
		for (Iterator iterator = top.iterator(); iterator.hasNext();) {
			String exept = (String) iterator.next();
			area.append("Qtde : " + Logger.topExceptions.get(exept));
			area.append("\n");
			area.append(exept.replaceAll("<br>", "\n"));
			area.append("\n");
		}
		area.setCaretPosition(0);
		JOptionPane.showMessageDialog(getMainPanel(), new JScrollPane(area),
				Lang.msg("listaDeErros"), JOptionPane.INFORMATION_MESSAGE);

	}

	private void gerarLayout() {
		compTransp(textAreaChat);
		textAreaChat.addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent e) {
				if (mainPanel != null) {
					mainPanel.repaint();
				}
			}
		});
		textAreaChat.addInputMethodListener(new InputMethodListener() {

			@Override
			public void inputMethodTextChanged(InputMethodEvent event) {
				if (mainPanel != null) {
					mainPanel.repaint();
				}
			}

			@Override
			public void caretPositionChanged(InputMethodEvent event) {
				if (mainPanel != null) {
					mainPanel.repaint();
				}
			}
		});
		compTransp(listaClientes);
		compTransp(listaJogosCriados);
		compTransp(listaJogosAndamento);
		JPanel cPanel = new JPanel(new BorderLayout());
		compTransp(cPanel);
		JPanel ePanel = new JPanel(new GridLayout(1, 2));
		compTransp(ePanel);
		mainPanel.add(cPanel, BorderLayout.CENTER);
		JPanel chatPanel = new JPanel();
		compTransp(chatPanel);
		String ver = " Main";
		if (controleChatCliente != null) {
			ver = controleChatCliente.getVersao();
		}
		chatPanel.setBorder(new TitledBorder("Chat Room "
				+ ConstantesMesa11.TITULO + ver));
		JPanel usersPanel = new JPanel();
		compTransp(usersPanel);
		usersPanel.setBorder(new TitledBorder("Jogadores Online") {
			public String getTitle() {
				return Lang.msg("jogadoresOnline");
			}
		});
		cPanel.add(chatPanel, BorderLayout.CENTER);
		mainPanel.add(ePanel, BorderLayout.EAST);
		JPanel inputPanel = new JPanel();
		compTransp(inputPanel);
		cPanel.add(inputPanel, BorderLayout.SOUTH);
		ePanel.add(usersPanel);
		JPanel jogsPanel = new JPanel(new GridLayout(2, 1));
		compTransp(jogsPanel);
		JPanel jogsPanelCriados = new JPanel();
		compTransp(jogsPanelCriados);
		jogsPanelCriados.setBorder((new TitledBorder("Lista de Jogos") {
			public String getTitle() {
				return Lang.msg("jogosCriados");
			}
		}));

		JPanel jogsAndamentoPanel = new JPanel();
		compTransp(jogsAndamentoPanel);
		jogsAndamentoPanel.setBorder((new TitledBorder("Lista de Jogos") {
			public String getTitle() {
				return Lang.msg("jogosAndamnto");
			}
		}));
		jogsPanel.add(jogsPanelCriados);
		jogsPanel.add(jogsAndamentoPanel);
		ePanel.add(jogsPanel);
		/**
		 * adicionar componentes.
		 */
		JScrollPane jogsPane = new JScrollPane(listaClientes) {
			@Override
			public Dimension getPreferredSize() {
				Dimension preferredSize = super.getPreferredSize();
				return new Dimension(120, 340);
			}
		};
		compTransp(jogsPane);
		usersPanel.add(jogsPane);
		JScrollPane jogsCriados = new JScrollPane(listaJogosCriados) {
			@Override
			public Dimension getPreferredSize() {
				Dimension preferredSize = super.getPreferredSize();
				return new Dimension(120, 155);
			}
		};
		compTransp(jogsCriados);
		JScrollPane jogsAndamento = new JScrollPane(listaJogosAndamento) {
			@Override
			public Dimension getPreferredSize() {
				Dimension preferredSize = super.getPreferredSize();
				return new Dimension(120, 155);
			}
		};
		compTransp(jogsAndamento);
		jogsPanelCriados.add(jogsCriados);
		jogsAndamentoPanel.add(jogsAndamento);
		JPanel buttonsPanel = new JPanel();
		compTransp(buttonsPanel);
		buttonsPanel.setLayout(new GridLayout(3, 4));
		buttonsPanel.add(entrarJogo);
		buttonsPanel.add(criarJogo);
		buttonsPanel.add(criarJogoVsCPU);
		buttonsPanel.add(verDetalhes);
		buttonsPanel.add(criarTime);
		buttonsPanel.add(editarTime);
		buttonsPanel.add(sairJogo);
		buttonsPanel.add(comboIdiomas);
		comboIdiomas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Logger.logar(Lang
						.key(comboIdiomas.getSelectedItem().toString()));
				String i = Lang.key(comboIdiomas.getSelectedItem().toString());
				if (i != null && !"".equals(i)) {
					Lang.mudarIdioma(i);
					comboIdiomas.removeAllItems();
					comboIdiomas.addItem(Lang.msg("pt"));
					comboIdiomas.addItem(Lang.msg("en"));
				}
			}
		});
		buttonsPanel.add(classificacao);
		// campeonato.setEnabled(false);
		buttonsPanel.add(criarCampeonato);
		buttonsPanel.add(verCampeonato);
		buttonsPanel.add(sobre);
		JPanel panelTextoEnviar = new JPanel();
		compTransp(panelTextoEnviar);
		panelTextoEnviar.setBorder(new TitledBorder("Texto Enviar") {
			public String getTitle() {
				return Lang.msg("textoEnviar");
			}
		});
		panelTextoEnviar.setLayout(new BorderLayout());
		panelTextoEnviar.add(textoEnviar, BorderLayout.CENTER);
		inputPanel.setLayout(new BorderLayout());
		inputPanel.add(panelTextoEnviar, BorderLayout.NORTH);
		inputPanel.add(buttonsPanel, BorderLayout.CENTER);
		inputPanel.add(infoLabel1, BorderLayout.SOUTH);
		chatPanel.setLayout(new BorderLayout());
		JScrollPane jScrollPane = new JScrollPane(textAreaChat);
		jScrollPane.addInputMethodListener(new InputMethodListener() {

			@Override
			public void inputMethodTextChanged(InputMethodEvent event) {
				if (mainPanel != null) {
					mainPanel.repaint();
				}
			}

			@Override
			public void caretPositionChanged(InputMethodEvent event) {
				if (mainPanel != null) {
					mainPanel.repaint();
				}
			}
		});
		chatPanel.add(compTransp(jScrollPane), BorderLayout.CENTER);

	}

	public JPanel getMainPanel() {
		return mainPanel;
	}

	public static void main(String[] args) {
		MesaAppletLocalDummy applet = new MesaAppletLocalDummy();
		ControleChatCliente controleChatCliente = new ControleChatCliente(
				applet, new SessaoCliente());
		ChatWindow paddockWindow = new ChatWindow(controleChatCliente);
		JFrame frame = new JFrame();
		frame.getContentPane().add(paddockWindow.getMainPanel());
		frame.setSize(820, 410);
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	public void atualizar(DadosMesa11 dadosMesa11) {
		atualizarChat(dadosMesa11);

		DefaultListModel clientesModel = new DefaultListModel();
		for (Iterator iter = dadosMesa11.getClientes().iterator(); iter
				.hasNext();) {
			SessaoCliente element = (SessaoCliente) iter.next();
			clientesModel.addElement(element);
		}
		listaClientes.setModel(clientesModel);

		DefaultListModel modelJogosCriados = ((DefaultListModel) listaJogosCriados
				.getModel());
		modelJogosCriados.clear();
		mapaJogosCriados.clear();
		for (Iterator iter = dadosMesa11.getJogosCriados().iterator(); iter
				.hasNext();) {
			String nmJogo = (String) iter.next();
			String key = Lang.decodeTexto(nmJogo);
			NnpeTO mesa11to = new NnpeTO();
			mesa11to.setComando(ConstantesMesa11.OBTER_DADOS_JOGO);
			mesa11to.setData(nmJogo);
			String placar = "";
			Object ret = controleChatCliente.enviarObjeto(mesa11to);
			if (ret instanceof NnpeTO) {
				mesa11to = (NnpeTO) ret;
				DadosJogoSrvMesa11 dadosJogoSrvMesa11 = (DadosJogoSrvMesa11) mesa11to
						.getData();
				mesa11to = new NnpeTO();
				mesa11to.setData(dadosJogoSrvMesa11.getTimeCasa());
				mesa11to.setComando(ConstantesMesa11.OBTER_TIME);
				ret = controleChatCliente.enviarObjeto(mesa11to);
				mesa11to = (NnpeTO) ret;
				Time timeCasa = (Time) mesa11to.getData();
				placar += " " + timeCasa.getNomeAbrev() + " X ";
			}

			mapaJogosCriados.put(key, Util.isNullOrEmpty(nmJogo) ? nmJogo
					: placar);
			modelJogosCriados.addElement(key);
		}

		listaJogosCriados.setCellRenderer(new ListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList list,
					Object nmJogo, int index, boolean isSelected,
					boolean cellHasFocus) {
				Object placar = mapaJogosCriados.get(nmJogo);
				JPanel jPanel = new JPanel(new GridLayout(1, 1));
				compTransp(jPanel);
				if (placar == null) {
					jPanel.add(new JLabel(nmJogo.toString()));
				} else {
					jPanel.setLayout(new GridLayout(2, 1));
					jPanel.add(compTransp(new JLabel(nmJogo.toString())));
					jPanel.add(compTransp(new JLabel(placar.toString())));
				}

				if (isSelected) {
					jPanel.setBorder(new LineBorder(new Color(184, 207, 229)));
				} else {
					for (int i = 0; i < jPanel.getComponentCount(); i++) {
						Component component = jPanel.getComponent(i);
					}
				}
				if (mainPanel != null) {
					mainPanel.repaint();
				}
				return jPanel;
			}
		});

		DefaultListModel modelJogosAndamento = ((DefaultListModel) listaJogosAndamento
				.getModel());
		modelJogosAndamento.clear();
		mapaJogosAndamento.clear();
		for (Iterator iter = dadosMesa11.getJogosAndamento().iterator(); iter
				.hasNext();) {
			String nmJogo = (String) iter.next();
			Logger.logar("nmJogo " + nmJogo);
			String key = Lang.decodeTexto(nmJogo);

			NnpeTO mesa11to = new NnpeTO();
			mesa11to.setComando(ConstantesMesa11.OBTER_DADOS_JOGO);
			mesa11to.setData(nmJogo);
			String placar = "";
			Object ret = controleChatCliente.enviarObjeto(mesa11to);
			if (ret instanceof NnpeTO) {
				mesa11to = (NnpeTO) ret;
				DadosJogoSrvMesa11 dadosJogoSrvMesa11 = (DadosJogoSrvMesa11) mesa11to
						.getData();
				mesa11to = new NnpeTO();
				mesa11to.setData(dadosJogoSrvMesa11.getTimeCasa());
				mesa11to.setComando(ConstantesMesa11.OBTER_TIME);
				ret = controleChatCliente.enviarObjeto(mesa11to);
				mesa11to = (NnpeTO) ret;
				Time timeCasa = (Time) mesa11to.getData();
				placar += " " + timeCasa.getNomeAbrev() + " "
						+ dadosJogoSrvMesa11.getGolsCasa() + " X ";
				if (!Util.isNullOrEmpty(dadosJogoSrvMesa11.getTimeVisita())) {
					mesa11to = new NnpeTO();
					mesa11to.setData(dadosJogoSrvMesa11.getTimeVisita());
					mesa11to.setComando(ConstantesMesa11.OBTER_TIME);
					ret = controleChatCliente.enviarObjeto(mesa11to);
					mesa11to = (NnpeTO) ret;
					Time timeVisita = (Time) mesa11to.getData();
					placar += dadosJogoSrvMesa11.getGolsVisita() + " "
							+ timeVisita.getNomeAbrev();
				}

			}
			mesa11to = (NnpeTO) ret;

			mapaJogosAndamento.put(key, Util.isNullOrEmpty(nmJogo) ? nmJogo
					: placar);
			modelJogosAndamento.addElement(key);
		}

		listaJogosAndamento.setCellRenderer(new ListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList list,
					Object nmJogo, int index, boolean isSelected,
					boolean cellHasFocus) {
				Object placar = mapaJogosAndamento.get(nmJogo);
				JPanel jPanel = new JPanel(new GridLayout(1, 1));
				if (placar == null) {
					jPanel.add(new JLabel(nmJogo.toString()));
				} else {
					jPanel.setLayout(new GridLayout(2, 1));
					jPanel.add(new JLabel(nmJogo.toString()));
					jPanel.add(new JLabel(placar.toString()));
				}

				if (isSelected) {
					jPanel.setBorder(new LineBorder(new Color(184, 207, 229)));
				} else {
					for (int i = 0; i < jPanel.getComponentCount(); i++) {
						Component component = jPanel.getComponent(i);
						component.setBackground(Color.WHITE);
					}
					jPanel.setBackground(Color.WHITE);
				}
				if (mainPanel != null) {
					mainPanel.repaint();
				}
				return jPanel;
			}
		});
		if (mainPanel != null) {
			mainPanel.repaint();
		}

	}

	private void atualizarChat(DadosMesa11 dadosMesa11) {
		if ("".equals(dadosMesa11.getLinhaChat())
				|| dadosMesa11.getLinhaChat() == null
				|| dadosMesa11.getDataTime() == null) {
			return;
		}
		if (!chatTimes.contains(dadosMesa11.getDataTime())) {
			textAreaChat.append(dadosMesa11.getLinhaChat() + "\n");
			textAreaChat.setCaretPosition(textAreaChat.getText().length());
			chatTimes.add(dadosMesa11.getDataTime());
			if (mainPanel != null) {
				mainPanel.repaint();
			}
		}
	}

	public void mostrarDetalhesJogador(Object object) {
		// JPanel panel = new JPanel();
		// SessaoCliente cliente = (SessaoCliente) object;
		// panel.setLayout(new GridLayout(1, 2));
		// panel.add(new JLabel("Ultima Atividade : ") {
		//
		// public String getText() {
		//
		// return Lang.msg("170");
		// }
		// });
		// panel.add(new JLabel(df.format(new Timestamp(cliente
		// .getUlimaAtividade()))));
		// JOptionPane.showMessageDialog(mainPanel, panel);
	}

	public void atualizaInfo() {
		String text = Lang.msg("latenciaJogo") + " "
				+ controleChatCliente.getLatenciaMinima();
		text += " " + Lang.msg("latenciaReal") + " "
				+ controleChatCliente.getLatenciaReal();
		text += " " + Lang.msg("maxJogos") + " " + 10;

		infoLabel1.setText(text);
		if (mainPanel != null) {
			mainPanel.repaint();
		}

	}

	public String obterJogoSelecionado() {
		Object object = listaJogosCriados.getSelectedValue();
		if (object == null) {
			object = listaJogosAndamento.getSelectedValue();
		}
		return (String) object;
	}

}
