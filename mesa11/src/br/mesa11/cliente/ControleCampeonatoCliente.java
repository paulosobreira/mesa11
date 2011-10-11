package br.mesa11.cliente;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.ColorModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

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

public class ControleCampeonatoCliente {

	private ControleJogosCliente controleJogosCliente;
	private ControleChatCliente controleChatCliente;
	protected String campeonatoSelecionado;

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

	public void verCampeonatos() {
		Mesa11TO mesa11to = new Mesa11TO();
		mesa11to.setComando(ConstantesMesa11.LISTAR_CAMPEONATOS);
		Object ret = enviarObjeto(mesa11to);
		if (ret instanceof Mesa11TO) {
			mesa11to = (Mesa11TO) ret;
			List campeonatos = (List) mesa11to.getData();
			JPanel campeonastosPanel = gerarPanelCampeonatos(campeonatos);
			JOptionPane.showMessageDialog(controleChatCliente.getChatWindow()
					.getMainPanel(), campeonastosPanel, Lang
					.msg("listaCampeonatos"), JOptionPane.INFORMATION_MESSAGE);
		}
		carregaCampeonatoSelecionado(controleChatCliente.getChatWindow()
				.getMainPanel());
	}

	private JPanel gerarPanelCampeonatos(final List campeonatos) {
		final JTable corridasTable = new JTable();

		final TableModel corridasTableModel = new AbstractTableModel() {

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				Object[] value = (Object[]) campeonatos.get(rowIndex);

				switch (columnIndex) {
				case 0:
					return value[0];
				case 1:
					return value[1];
				case 2:
					boolean val = (Boolean) value[2];
					return val ? Lang.msg("sim") : Lang.msg("nao");
				case 3:
					return new SimpleDateFormat("dd/MM/yyyy").format(value[3]);
				default:
					return "";
				}
			}

			@Override
			public int getRowCount() {
				if (campeonatos == null) {
					return 0;
				}
				return campeonatos.size();
			}

			@Override
			public int getColumnCount() {
				return 4;
			}

			@Override
			public String getColumnName(int columnIndex) {

				switch (columnIndex) {
				case 0:
					return Lang.msg("nomeCampeonato");
				case 1:
					return Lang.msg("donoCampeonato");
				case 2:
					return Lang.msg("campeonatoConcluido");
				case 3:
					return Lang.msg("criadoEm");

				default:
					return "";
				}

			}
		};
		corridasTable.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				campeonatoSelecionado = (String) corridasTableModel.getValueAt(
						corridasTable.getSelectedRow(), 0);
				if (e.getClickCount() == 2) {
					carregaCampeonatoSelecionado(corridasTable);
				}
			}

		});

		corridasTable.setModel(corridasTableModel);
		JScrollPane jScrollPane = new JScrollPane(corridasTable) {
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(620, 150);
			}
		};
		JPanel jPanel = new JPanel();
		jPanel.setBorder(new TitledBorder("Campeonato") {
			@Override
			public String getTitle() {
				return Lang.msg("cliqueDuploCarregarCampeonato");
			}
		});
		jPanel.add(jScrollPane);
		return jPanel;
	}

	protected void carregaCampeonatoSelecionado(Component comp) {
		if (Util.isNullOrEmpty(campeonatoSelecionado)) {
			return;
		}
		int optRet = JOptionPane.showConfirmDialog(comp, Lang.msg(
				"carregarCampeonato", new String[] { campeonatoSelecionado }),
				Lang.msg("detalhesCampeonato"), JOptionPane.YES_NO_OPTION);
		if (optRet == JOptionPane.YES_OPTION) {
			Mesa11TO mesa11to = new Mesa11TO();
			mesa11to.setComando(ConstantesMesa11.VER_CAMPEONATO);
			mesa11to.setData(campeonatoSelecionado);
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
			JPanel classificacaoPanel = controleChatCliente
					.gerarPanelClassificacao(dadosTimes, dadosJogadores);
			Object[] dadosCampeonato = (Object[]) data
					.get(ConstantesMesa11.DADOS_CAMPEONATO);
			Integer num_rodadas = (Integer) data
					.get(ConstantesMesa11.NUMERO_RODADAS);
			JPanel campeonato = gerarPanelInfoCampeonato(dadosCampeonato,
					num_rodadas);
			JPanel geral = new JPanel(new BorderLayout());
			geral.add(campeonato, BorderLayout.NORTH);
			geral.add(classificacaoPanel, BorderLayout.CENTER);
			JOptionPane.showMessageDialog(comp, geral, Lang
					.msg("classificacao"), JOptionPane.INFORMATION_MESSAGE);

		}
	}

	private JPanel gerarPanelInfoCampeonato(final Object[] dadosCampeonato,
			Integer numRodadas) {
		JPanel campeonato = new JPanel(new GridLayout(3, 4));
		campeonato.setBorder(new TitledBorder("campeonato") {
			@Override
			public String getTitle() {
				return Lang.msg("campeonato");
			}
		});
		campeonato.add(new JLabel() {
			@Override
			public String getText() {
				return Lang.msg("nomeCampeonato") + " : ";
			}
		});
		campeonato.add(new JLabel((String) dadosCampeonato[0]) {
			@Override
			public Color getForeground() {
				return Color.BLUE;
			}
		});
		campeonato.add(new JLabel() {
			@Override
			public String getText() {
				return Lang.msg("numeroJogadas") + " : ";
			}
		});
		campeonato.add(new JLabel(((Integer) dadosCampeonato[2]).toString()) {
			@Override
			public Color getForeground() {
				return Color.BLUE;
			}
		});
		campeonato.add(new JLabel() {
			@Override
			public String getText() {
				return Lang.msg("tempoJogoMinutos") + " : ";
			}
		});
		campeonato.add(new JLabel(((Integer) dadosCampeonato[1]).toString()) {
			@Override
			public Color getForeground() {
				return Color.BLUE;
			}
		});
		campeonato.add(new JLabel() {
			@Override
			public String getText() {
				return Lang.msg("tempoJogadaSegundos") + " : ";
			}
		});
		campeonato.add(new JLabel(((Integer) dadosCampeonato[3]).toString()) {
			@Override
			public Color getForeground() {
				return Color.BLUE;
			}
		});
		campeonato.add(new JLabel() {
			@Override
			public String getText() {
				return Lang.msg("numeroRodadas") + " : ";
			}
		});
		campeonato.add(new JLabel(numRodadas.toString()) {
			@Override
			public Color getForeground() {
				return Color.BLUE;
			}
		});

		campeonato.add(new JLabel() {
			@Override
			public String getText() {
				return Lang.msg("rodadas") + " : ";
			}
		});
		final JComboBox rodadaCombo = new JComboBox();
		for (int i = 1; i <= numRodadas; i++) {
			rodadaCombo.addItem(new Integer(i));
		}

		JButton verRodada = new JButton() {
			public String getText() {
				return Lang.msg("verRodada");
			}
		};
		verRodada.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				mostrarRodada((String) dadosCampeonato[0],
						(Integer) rodadaCombo.getSelectedItem());
			}
		});
		JPanel rodadas = new JPanel();
		rodadas.add(rodadaCombo);
		rodadas.add(verRodada);
		campeonato.add(rodadas);

		return campeonato;
	}

	protected void mostrarRodada(String nomeCampeonato, Integer numeroRodada) {
		Mesa11TO mesa11to = new Mesa11TO();
		mesa11to.setComando(ConstantesMesa11.VER_RODADA);
		CampeonatoMesa11 campeonatoMesa11 = new CampeonatoMesa11();
		campeonatoMesa11.setNome(nomeCampeonato);
		campeonatoMesa11.setNumeroRodadas(numeroRodada);
		mesa11to.setData(campeonatoMesa11);
		Object ret = enviarObjeto(mesa11to);
		if (!(ret instanceof Mesa11TO)) {
			return;
		}
		mesa11to = (Mesa11TO) ret;
		List list = (List) mesa11to.getData();
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			Object object = (Object) iterator.next();
			System.out.println(object);
		}

	}
}
