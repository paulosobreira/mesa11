package br.mesa11.cliente;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
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
import javax.swing.border.TitledBorder;

import br.hibernate.CampeonatoMesa11;
import br.hibernate.JogadoresCampeonatoMesa11;
import br.hibernate.Time;
import br.hibernate.TimesCampeonatoMesa11;
import br.hibernate.Usuario;
import br.mesa11.ConstantesMesa11;
import br.mesa11.visao.Java2sAutoComboBox;
import br.nnpe.Util;
import br.recursos.Lang;
import br.tos.Mesa11TO;
import br.tos.MsgSrv;

public class ControleCampeonatoCliente {

	private ControleJogosCliente controleJogosCliente;
	private ControleChatCliente controleChatCliente;

	public ControleCampeonatoCliente(ControleJogosCliente controleJogosCliente,
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
						tempoJogoCombo, tempoJogadaCombo,
						defaultListModelJogadores,
						defaultListModelTimesSelecionados);
			}

		}

	}

	private void enviarDadosCriarCampeonato(JTextField nomeCampeonato,
			JComboBox numJogadaCombo, JComboBox tempoJogoCombo,
			JComboBox tempoJogadaCombo,
			DefaultListModel defaultListModelJogadores,
			DefaultListModel defaultListModelTimesSelecionados) {
		String erros = "";
		if (Util.isNullOrEmpty(nomeCampeonato.getText()))
			erros += Lang.msg("nomeCampeonatoNull") + "\n";
		if (defaultListModelJogadores.isEmpty())
			erros += Lang.msg("jogadoresEmpty") + "\n";
		if (defaultListModelTimesSelecionados.size() < 3)
			erros += Lang.msg("menos3Ttimes") + "\n";
		if (!Util.isNullOrEmpty(erros)) {
			JOptionPane.showMessageDialog(controleChatCliente.getChatWindow()
					.getMainPanel(), erros, Lang.msg("erro"),
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		CampeonatoMesa11 campeonatoMesa11 = new CampeonatoMesa11();
		campeonatoMesa11.setDataCriacao(new Date());
		campeonatoMesa11.setNome(nomeCampeonato.getText());
		campeonatoMesa11.setNumeroJogadas((Integer) numJogadaCombo
				.getSelectedItem());
		campeonatoMesa11.setTempoJogada((Integer) tempoJogadaCombo
				.getSelectedItem());
		campeonatoMesa11.setTempoJogo((Integer) tempoJogoCombo
				.getSelectedItem());
		campeonatoMesa11.setLoginCriador(controleChatCliente.getSessaoCliente()
				.getNomeJogador());
		for (int i = 0; i < defaultListModelJogadores.getSize(); i++) {
			String loginJOgador = (String) defaultListModelJogadores.get(i);
			Usuario usuario = new Usuario();
			usuario.setLogin(loginJOgador);
			JogadoresCampeonatoMesa11 jogadoresCampeonatoMesa11 = new JogadoresCampeonatoMesa11();
			jogadoresCampeonatoMesa11.setUsuario(usuario);
			jogadoresCampeonatoMesa11.setDataCriacao(new Date());
			jogadoresCampeonatoMesa11.setLoginCriador(controleChatCliente
					.getSessaoCliente().getNomeJogador());
			jogadoresCampeonatoMesa11.setCampeonatoMesa11(campeonatoMesa11);
			campeonatoMesa11.getJogadoresCampeonatoMesa11().add(
					jogadoresCampeonatoMesa11);
		}
		for (int i = 0; i < defaultListModelTimesSelecionados.getSize(); i++) {
			String nomeTime = (String) defaultListModelTimesSelecionados.get(i);
			Time time = new Time();
			time.setNome(nomeTime);
			TimesCampeonatoMesa11 timesCampeonatoMesa11 = new TimesCampeonatoMesa11();
			timesCampeonatoMesa11.setTime(time);
			timesCampeonatoMesa11.setDataCriacao(new Date());
			timesCampeonatoMesa11.setLoginCriador(controleChatCliente
					.getSessaoCliente().getNomeJogador());
			timesCampeonatoMesa11.setCampeonatoMesa11(campeonatoMesa11);
			campeonatoMesa11.getTimesCampeonatoMesa11().add(
					timesCampeonatoMesa11);
		}
		Mesa11TO mesa11to = new Mesa11TO();
		mesa11to.setData(campeonatoMesa11);
		mesa11to.setComando(ConstantesMesa11.CRIAR_CAMPEONATO);
		enviarObjeto(mesa11to);
	}

	private Object enviarObjeto(Mesa11TO mesa11to) {
		return controleChatCliente.enviarObjeto(mesa11to);
	}

}
