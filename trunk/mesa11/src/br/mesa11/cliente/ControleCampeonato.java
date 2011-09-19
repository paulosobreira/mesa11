package br.mesa11.cliente;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import br.mesa11.ConstantesMesa11;
import br.mesa11.visao.Java2sAutoComboBox;
import br.nnpe.JOptionPaneWithEnterSupport;
import br.recursos.Lang;
import br.tos.Mesa11TO;

public class ControleCampeonato {

	private ControleJogosCliente controleJogosCliente;
	private ControleChatCliente controleChatCliente;

	public ControleCampeonato(ControleJogosCliente controleJogosCliente,
			ControleChatCliente controleChatCliente) {
		this.controleJogosCliente = controleJogosCliente;
		this.controleChatCliente = controleChatCliente;
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
			ActionListener adicionar = new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (java2sAutoComboBox.getSelectedIndex() != -1) {
						if (!defaultListModelJogadores
								.contains(java2sAutoComboBox.getSelectedItem()))
							defaultListModelJogadores
									.addElement(java2sAutoComboBox
											.getSelectedItem());
					}
					java2sAutoComboBox.requestFocus();
				}

			};
			add.addActionListener(adicionar);
			java2sAutoComboBox.addActionListener(adicionar);

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
			int showConfirmDialog = JOptionPane.showConfirmDialog(
					controleChatCliente.getChatWindow().getMainPanel(), panel,
					Lang.msg("criarCampeonato"), JOptionPane.YES_NO_OPTION);
			if (JOptionPane.YES_OPTION == showConfirmDialog) {
				enviarDadosCriarCampeonato(nomeCampeonato, numJogadaCombo,
						tempoJogoCombo, tempoJogadaCombo, listaJogadores,
						listTimesSelecionados);
			}

		}

	}

	private void enviarDadosCriarCampeonato(JTextField nomeCampeonato,
			JComboBox numJogadaCombo, JComboBox tempoJogoCombo,
			JComboBox tempoJogadaCombo, JList listaJogadores,
			JList listTimesSelecionados) {
		// TODO Auto-generated method stub

	}

	private void enviarDadosCriarCampeonato() {
		// TODO Auto-generated method stub

	}

	private Object enviarObjeto(Mesa11TO mesa11to) {
		return controleChatCliente.enviarObjeto(mesa11to);
	}

}
