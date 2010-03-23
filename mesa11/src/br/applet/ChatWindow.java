package br.applet;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import br.mesa11.cliente.ControleChatCliente;
import br.nnpe.Logger;
import br.recursos.Lang;
import br.tos.DadosChat;

/**
 * @author paulo.sobreira
 * 
 */
public class ChatWindow {

	private JPanel mainPanel;
	private ControleChatCliente controleChatCliente;
	private JList listaClientes = new JList();
	private JList listaJogosCriados = new JList(new DefaultListModel());
	private JTextArea textAreaChat = new JTextArea();
	private JTextField textoEnviar = new JTextField();
	private HashMap mapaJogosCriados = new HashMap();
	private JButton enviarTexto = new JButton("Enviar Texto") {

		public String getText() {

			return Lang.msg("EnviarTexto");
		}
	};
	private JButton entrarJogo = new JButton("Entrar Jogo") {

		public String getText() {

			return Lang.msg("EntrarJogo");
		}
	};
	private JButton criarJogo = new JButton("Criar Jogo") {

		public String getText() {

			return Lang.msg("CriarJogo");
		}
	};
	private JButton iniciarJogo = new JButton("Iniciar Jogo") {

		public String getText() {

			return Lang.msg("IniciarJogo");
		}
	};
	private JButton verDetalhes = new JButton("Ver Detalhes") {

		public String getText() {

			return Lang.msg("VerDetalhes");
		}
	};
	private JButton classificacao = new JButton("Classificação") {

		public String getText() {

			return Lang.msg("Classificacao");
		}
	};

	private JButton carreira = new JButton("Modo Carreira") {

		public String getText() {

			return Lang.msg("ModoCarreira");
		}
	};
	private JButton construtores = new JButton("Construtores") {

		public String getText() {

			return Lang.msg("Construtores");
		}
	};
	private JButton conta = new JButton("Conta") {

		public String getText() {

			return Lang.msg("Conta");
		}
	};

	private JComboBox comboTemporada = new JComboBox(new String[] { "2009",
			"2008", "2007", "2003", "1990", "1993", "1988", "1987", "1986",
			"1974", "1972", "1968", "super" });
	private JComboBox comboIdiomas = new JComboBox(new String[] {
			Lang.msg("pt"), Lang.msg("en") });
	private JButton sobre = new JButton("Sobre") {

		public String getText() {

			return Lang.msg("180");
		}
	};
	private JLabel infoLabel1 = new JLabel();
	private Set chatTimes = new HashSet();
	private SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	public ChatWindow(ControleChatCliente controleChatApplet) {
		mainPanel = new JPanel(new BorderLayout());
		if (controleChatApplet != null) {
			this.controleChatCliente = controleChatApplet;
			controleChatApplet.setChatWindow(this);
		}
		gerarLayout();
		gerarAcoes();
		if (controleChatApplet != null) {
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
		enviarTexto.addActionListener(actionListener);
		textoEnviar.addActionListener(actionListener);
		criarJogo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				String temporada = (String) comboTemporada.getSelectedItem();
				controleChatCliente.criarJogo("t" + temporada);

			}

		});
		entrarJogo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				Object object = listaJogosCriados.getSelectedValue();
				if (object != null) {
					int result = JOptionPane.showConfirmDialog(getMainPanel(),
							Lang.msg("181") + object);
					if (result == JOptionPane.YES_OPTION) {
						controleChatCliente.entarJogo(mapaJogosCriados
								.get(object));
					}
				} else {
					JOptionPane.showMessageDialog(getMainPanel(), Lang
							.msg("182"));
				}

			}

		});
		verDetalhes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object object = listaJogosCriados.getSelectedValue();
				if (object != null) {
					controleChatCliente.verDetalhesJogo(mapaJogosCriados
							.get(object));
				} else {
					object = listaClientes.getSelectedValue();
					if (object != null) {
						controleChatCliente.verDetalhesJogador(object);
					} else {
						JOptionPane.showMessageDialog(getMainPanel(), Lang
								.msg("183"));
					}
				}

			}

		});
		iniciarJogo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controleChatCliente.iniciarJogo();

			}

		});
		classificacao.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controleChatCliente.verClassificacao();

			}

		});

		construtores.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controleChatCliente.verConstrutores();

			}

		});
		carreira.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controleChatCliente.modoCarreira();

			}

		});
		sobre.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String msg = Lang.msg("184") + "Paulo Sobreira \n "
						+ "sowbreira@yahoo.com.br \n"
						+ "http://br.geocities.com/sowbreira/ \n"
						+ "Março de 2010 \n ";
				// msg += Lang.msg("185") + "\n" + " Florêncio Queiroz \n"
				// + " Jorge Botelho \n" + " Leonardo Andrade \n"
				// + " Daniel Souza \n" + " Wendel Silva \n"
				// + " Marcos Henrique\n" + " Alvaru";

				JOptionPane.showMessageDialog(getMainPanel(), msg, Lang
						.msg("autor"), JOptionPane.INFORMATION_MESSAGE);
			}
		});

	}

	private void gerarLayout() {
		JPanel cPanel = new JPanel(new BorderLayout());
		JPanel sPanel = new JPanel(new BorderLayout());

		mainPanel.add(cPanel, BorderLayout.CENTER);
		mainPanel.add(sPanel, BorderLayout.SOUTH);
		JPanel chatPanel = new JPanel();
		chatPanel.setBorder(new TitledBorder("Mesa11 Chat Room v 1.0"));
		JPanel usersPanel = new JPanel();
		usersPanel.setBorder(new TitledBorder("Jogadores Online") {
			public String getTitle() {
				return Lang.msg("JogadoresOnline");
			}
		});
		cPanel.add(chatPanel, BorderLayout.CENTER);
		cPanel.add(usersPanel, BorderLayout.EAST);
		JPanel jogsPanel = new JPanel();
		jogsPanel.setBorder((new TitledBorder("Lista de Jogos") {
			public String getTitle() {
				return Lang.msg("ListadeJogos");
			}
		}));
		sPanel.add(jogsPanel, BorderLayout.EAST);
		JPanel inputPanel = new JPanel();
		sPanel.add(inputPanel, BorderLayout.CENTER);
		/**
		 * adicionar componentes.
		 */
		JScrollPane jogsPane = new JScrollPane(listaClientes);
		jogsPane.setPreferredSize(new Dimension(150, 400));
		usersPanel.add(jogsPane);
		JScrollPane jogsCriados = new JScrollPane(listaJogosCriados);
		jogsCriados.setPreferredSize(new Dimension(150, 100));
		jogsPanel.add(jogsCriados);
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new GridLayout(3, 4));
		buttonsPanel.add(enviarTexto);
		buttonsPanel.add(entrarJogo);
		buttonsPanel.add(criarJogo);
		buttonsPanel.add(iniciarJogo);
		buttonsPanel.add(verDetalhes);
		buttonsPanel.add(classificacao);
		buttonsPanel.add(comboTemporada);
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
		// carreira.setEnabled(false);
		conta.setEnabled(false);
		buttonsPanel.add(carreira);
		buttonsPanel.add(construtores);
		buttonsPanel.add(conta);
		buttonsPanel.add(sobre);
		JPanel panelTextoEnviar = new JPanel();
		panelTextoEnviar.setBorder(new TitledBorder("Texto Enviar") {
			public String getTitle() {
				return Lang.msg("TextoEnviar");
			}
		});
		panelTextoEnviar.setLayout(new BorderLayout());
		panelTextoEnviar.add(textoEnviar, BorderLayout.CENTER);
		inputPanel.setLayout(new BorderLayout());
		inputPanel.add(panelTextoEnviar, BorderLayout.NORTH);
		inputPanel.add(buttonsPanel, BorderLayout.CENTER);
		inputPanel.add(infoLabel1, BorderLayout.SOUTH);
		chatPanel.setLayout(new BorderLayout());
		chatPanel.add(new JScrollPane(textAreaChat), BorderLayout.CENTER);

	}

	public JPanel getMainPanel() {
		return mainPanel;
	}

	public static void main(String[] args) {
		ChatWindow paddockWindow = new ChatWindow(null);
		JFrame frame = new JFrame();
		frame.getContentPane().add(paddockWindow.getMainPanel());
		frame.setSize(640, 480);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
	}

	public void atualizar(DadosChat dadosChat) {
		atualizarChat(dadosChat);

		DefaultListModel clientesModel = new DefaultListModel();
		// for (Iterator iter = dadosPaddock.getClientes().iterator(); iter
		// .hasNext();) {
		// SessaoCliente element = (SessaoCliente) iter.next();
		// clientesModel.addElement(element);
		// }
		listaClientes.setModel(clientesModel);

		DefaultListModel model = ((DefaultListModel) listaJogosCriados
				.getModel());
		if (model.size() != dadosChat.getJogosCriados().size()) {
			model.clear();
			mapaJogosCriados.clear();
			for (Iterator iter = dadosChat.getJogosCriados().iterator(); iter
					.hasNext();) {
				String element = (String) iter.next();
				String key = Lang.decodeTexto(element);
				mapaJogosCriados.put(key, element);
				model.addElement(key);
			}
		}
	}

	private void atualizarChat(DadosChat dadosPaddock) {
		if ("".equals(dadosPaddock.getLinhaChat())
				|| dadosPaddock.getLinhaChat() == null
				|| dadosPaddock.getDataTime() == null) {
			return;
		}
		if (!chatTimes.contains(dadosPaddock.getDataTime())) {
			textAreaChat.append(dadosPaddock.getLinhaChat() + "\n");
			textAreaChat.setCaretPosition(textAreaChat.getText().length());
			chatTimes.add(dadosPaddock.getDataTime());
		}
	}

	// public JPanel gerarPainelJogadores(DetalhesJogo detalhesJogo) {
	// JPanel panelJogadores = new JPanel();
	//
	// Map detMap = detalhesJogo.getJogadoresPilotos();
	// panelJogadores.setLayout(new GridLayout(detMap.size(), 2));
	// for (Iterator iter = detMap.keySet().iterator(); iter.hasNext();) {
	// String key = (String) iter.next();
	// panelJogadores.add(new JLabel(key + ": "));
	// panelJogadores.add(new JLabel((String) detMap.get(key)));
	// }
	// if (detMap.isEmpty()) {
	// panelJogadores.add(new JLabel("Nenhum ") {
	//
	// public String getText() {
	//
	// return Lang.msg("202");
	// }
	// });
	// panelJogadores.add(new JLabel("Jogador") {
	//
	// public String getText() {
	//
	// return Lang.msg("162");
	// }
	// });
	//
	// }
	// return panelJogadores;
	// }

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
		String text = Lang.msg("114") + " "
				+ controleChatCliente.getLatenciaMinima();
		text += " " + Lang.msg("115") + " "
				+ controleChatCliente.getLatenciaReal();
		text += " " + Lang.msg("116") + " " + 10;

		infoLabel1.setText(text);

	}
}
