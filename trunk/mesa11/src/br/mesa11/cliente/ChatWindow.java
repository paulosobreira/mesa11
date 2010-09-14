package br.mesa11.cliente;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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

import br.nnpe.Logger;
import br.recursos.Lang;
import br.tos.DadosMesa11;
import br.tos.SessaoCliente;

/**
 * @author paulo.sobreira
 * 
 */
public class ChatWindow {

	private JPanel mainPanel;
	private ControleChatCliente controleChatCliente;
	private JList listaClientes = new JList();
	private JList listaJogosCriados = new JList(new DefaultListModel());
	private JList listaJogosAndamento = new JList(new DefaultListModel());
	private JTextArea textAreaChat = new JTextArea();
	private JTextField textoEnviar = new JTextField();
	private HashMap mapaJogosCriados = new HashMap();
	private HashMap mapaJogosAndamento = new HashMap();
	private JButton enviarTexto = new JButton("Enviar Texto") {

		public String getText() {

			return Lang.msg("enviarTexto");
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

	private JButton conta = new JButton("Conta") {

		public String getText() {

			return Lang.msg("conta");
		}
	};

	private JComboBox comboIdiomas = new JComboBox(new String[] {
			Lang.msg("pt"), Lang.msg("en") });
	private JButton sobre = new JButton("Sobre") {

		public String getText() {

			return Lang.msg("sobre");
		}
	};
	private JLabel infoLabel1 = new JLabel();
	private Set chatTimes = new HashSet();
	private SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	public ChatWindow(ControleChatCliente controleChatCliente) {
		mainPanel = new JPanel(new BorderLayout());
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
		enviarTexto.addActionListener(actionListener);
		textoEnviar.addActionListener(actionListener);
		criarJogo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				controleChatCliente.criarJogo();
			}

		});
		entrarJogo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controleChatCliente.entarJogo();
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
						+ "sowbreira.appspot.com/ \n" + "Março de 2010 \n ";

				JOptionPane.showMessageDialog(getMainPanel(), msg, Lang
						.msg("autor"), JOptionPane.INFORMATION_MESSAGE);
			}
		});

	}

	private void gerarLayout() {
		JPanel cPanel = new JPanel(new BorderLayout());
		JPanel ePanel = new JPanel(new GridLayout(1, 2));
		mainPanel.add(cPanel, BorderLayout.CENTER);
		JPanel chatPanel = new JPanel();
		chatPanel.setBorder(new TitledBorder("Mesa11 Chat Room v 1.0"));
		JPanel usersPanel = new JPanel();
		usersPanel.setBorder(new TitledBorder("Jogadores Online") {
			public String getTitle() {
				return Lang.msg("jogadoresOnline");
			}
		});
		cPanel.add(chatPanel, BorderLayout.CENTER);
		mainPanel.add(ePanel, BorderLayout.EAST);
		JPanel inputPanel = new JPanel();
		cPanel.add(inputPanel, BorderLayout.SOUTH);
		ePanel.add(usersPanel);
		JPanel jogsPanel = new JPanel(new GridLayout(2, 1));

		JPanel jogsPanelCriados = new JPanel();
		jogsPanelCriados.setBorder((new TitledBorder("Lista de Jogos") {
			public String getTitle() {
				return Lang.msg("jogosCriados");
			}
		}));

		JPanel jogsAndamentoPanel = new JPanel();
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
				return new Dimension(120, 300);
			}
		};
		usersPanel.add(jogsPane);
		JScrollPane jogsCriados = new JScrollPane(listaJogosCriados) {
			@Override
			public Dimension getPreferredSize() {
				Dimension preferredSize = super.getPreferredSize();
				return new Dimension(120, 130);
			}
		};
		JScrollPane jogsAndamento = new JScrollPane(listaJogosAndamento) {
			@Override
			public Dimension getPreferredSize() {
				Dimension preferredSize = super.getPreferredSize();
				return new Dimension(120, 130);
			}
		};
		jogsPanelCriados.add(jogsCriados);
		jogsAndamentoPanel.add(jogsAndamento);
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new GridLayout(3, 4));
		buttonsPanel.add(enviarTexto);
		buttonsPanel.add(entrarJogo);
		buttonsPanel.add(criarJogo);
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
		conta.setEnabled(false);
		buttonsPanel.add(conta);
		buttonsPanel.add(sobre);
		JPanel panelTextoEnviar = new JPanel();
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
		chatPanel.add(new JScrollPane(textAreaChat), BorderLayout.CENTER);

	}

	public JPanel getMainPanel() {
		return mainPanel;
	}

	public static void main(String[] args) {
		ChatWindow paddockWindow = new ChatWindow(null);
		JFrame frame = new JFrame();
		frame.getContentPane().add(paddockWindow.getMainPanel());
		frame.setSize(820, 380);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
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
		if (modelJogosCriados.size() != dadosMesa11.getJogosCriados().size()) {
			modelJogosCriados.clear();
			mapaJogosCriados.clear();
			for (Iterator iter = dadosMesa11.getJogosCriados().iterator(); iter
					.hasNext();) {
				String element = (String) iter.next();
				String key = Lang.decodeTexto(element);
				mapaJogosCriados.put(key, element);
				modelJogosCriados.addElement(key);
			}
		}

		DefaultListModel modelJogosAndamento = ((DefaultListModel) listaJogosAndamento
				.getModel());
		if (modelJogosAndamento.size() != dadosMesa11.getJogosAndamento()
				.size()) {
			modelJogosAndamento.clear();
			mapaJogosAndamento.clear();
			for (Iterator iter = dadosMesa11.getJogosAndamento().iterator(); iter
					.hasNext();) {
				String element = (String) iter.next();
				String key = Lang.decodeTexto(element);
				mapaJogosAndamento.put(key, element);
				modelJogosAndamento.addElement(key);
			}
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

	}

	public String obterJogoSelecionado() {
		Object object = listaJogosCriados.getSelectedValue();
		if (object == null) {
			object = listaJogosAndamento.getSelectedValue();
		}
		return (String) object;
	}

}
