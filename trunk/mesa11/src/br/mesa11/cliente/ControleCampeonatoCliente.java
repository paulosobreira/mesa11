package br.mesa11.cliente;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Toolkit;
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

import javax.swing.DefaultCellEditor;
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
import br.hibernate.RodadaCampeonatoMesa11;
import br.hibernate.Time;
import br.hibernate.TimesCampeonatoMesa11;
import br.hibernate.Usuario;
import br.mesa11.ConstantesMesa11;
import br.mesa11.visao.Java2sAutoComboBox;
import br.nnpe.Util;
import br.nnpe.tos.NnpeTO;
import br.recursos.Lang;
import br.tos.ClassificacaoTime;

public class ControleCampeonatoCliente {

	private ControleJogosCliente controleJogosCliente;
	private ControleChatCliente controleChatCliente;
	protected String campeonatoSelecionado;
	private JComboBox rodadaCombo;
	private JTable rodadasTable;

	public ControleCampeonatoCliente(ControleJogosCliente controleJogosCliente,
			ControleChatCliente controleChatCliente) {
		this.controleJogosCliente = controleJogosCliente;
		this.controleChatCliente = controleChatCliente;
	}

	public void criarCampeonato() {
		NnpeTO mesa11to = new NnpeTO();
		mesa11to.setComando(ConstantesMesa11.OBTER_TODOS_TIMES);
		Object ret = enviarObjeto(mesa11to);
		if (ret instanceof NnpeTO) {
			mesa11to = (NnpeTO) ret;
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
			mesa11to = new NnpeTO();
			mesa11to.setComando(ConstantesMesa11.OBTER_TODOS_JOGADORES);
			ret = enviarObjeto(mesa11to);
			String[] jogadores = new String[] { "" };
			if (ret instanceof NnpeTO) {
				mesa11to = (NnpeTO) ret;
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
		NnpeTO mesa11to = new NnpeTO();
		mesa11to.setSessaoCliente(controleChatCliente.getSessaoCliente());
		mesa11to.setData(campeonatoMesa11);
		mesa11to.setComando(ConstantesMesa11.CRIAR_CAMPEONATO);
		enviarObjeto(mesa11to);
	}

	private Object enviarObjeto(NnpeTO mesa11to) {
		return controleChatCliente.enviarObjeto(mesa11to);
	}

	public void verCampeonatos() {
		NnpeTO mesa11to = new NnpeTO();
		mesa11to.setComando(ConstantesMesa11.LISTAR_CAMPEONATOS);
		Object ret = enviarObjeto(mesa11to);
		if (ret instanceof NnpeTO) {
			mesa11to = (NnpeTO) ret;
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
		final JTable campeonatoTable = new JTable();

		final TableModel campeonatosTableModel = new AbstractTableModel() {

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
		campeonatoTable.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				campeonatoSelecionado = (String) campeonatosTableModel
						.getValueAt(campeonatoTable.getSelectedRow(), 0);
				if (e.getClickCount() == 2) {
					carregaCampeonatoSelecionado(campeonatoTable);
				}
			}

		});

		campeonatoTable.setModel(campeonatosTableModel);
		JScrollPane jScrollPane = new JScrollPane(campeonatoTable) {
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
			NnpeTO mesa11to = new NnpeTO();
			mesa11to.setComando(ConstantesMesa11.VER_CAMPEONATO);
			mesa11to.setData(campeonatoSelecionado);
			Object ret = enviarObjeto(mesa11to);
			if (!(ret instanceof NnpeTO)) {
				return;
			}
			mesa11to = (NnpeTO) ret;
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
			Integer rodada_atual_campeonato = (Integer) data
					.get(ConstantesMesa11.RODADA_ATUAL_CAMPEONATO);

			JPanel campeonato = gerarPanelInfoCampeonato(dadosCampeonato,
					num_rodadas, rodada_atual_campeonato);
			JPanel geral = new JPanel(new BorderLayout());
			geral.add(campeonato, BorderLayout.NORTH);
			geral.add(classificacaoPanel, BorderLayout.CENTER);
			JLabel carregarRodadaSelecionada = new JLabel() {
				@Override
				public String getText() {
					return Lang.msg("carregarRodadaSelecionada");
				}
			};
			JPanel panel = new JPanel();
			panel.add(carregarRodadaSelecionada);
			geral.add(panel, BorderLayout.SOUTH);
			int showConfirmDialog = JOptionPane.showConfirmDialog(comp, geral,
					Lang.msg("classificacao"), JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (JOptionPane.YES_OPTION == showConfirmDialog) {
				mostrarRodada((String) dadosCampeonato[0],
						(Integer) rodadaCombo.getSelectedItem());
			}
		}
	}

	private JPanel gerarPanelInfoCampeonato(final Object[] dadosCampeonato,
			Integer numRodadas, Integer rodadaAtualCampeonato) {
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
				return Lang.msg("rodadaSelecionada") + " : ";
			}
		});
		rodadaCombo = new JComboBox();
		for (int i = 1; i <= numRodadas; i++) {
			rodadaCombo.addItem(new Integer(i));
		}
		rodadaCombo.setSelectedItem(rodadaAtualCampeonato);
		campeonato.add(rodadaCombo);
		return campeonato;
	}

	protected void mostrarRodada(String nomeCampeonato, Integer numeroRodada) {
		NnpeTO mesa11to = new NnpeTO();
		mesa11to.setComando(ConstantesMesa11.VER_RODADA);
		CampeonatoMesa11 campeonatoMesa11 = new CampeonatoMesa11();
		campeonatoMesa11.setNome(nomeCampeonato);
		campeonatoMesa11.setNumeroRodadas(numeroRodada);
		mesa11to.setData(campeonatoMesa11);
		Object ret = enviarObjeto(mesa11to);
		if (!(ret instanceof NnpeTO)) {
			return;
		}
		mesa11to = (NnpeTO) ret;
		List list = (List) mesa11to.getData();
		JPanel rodadasPanel = gerarPainelRodadas(list, nomeCampeonato);
		int showConfirmDialog = JOptionPane.showConfirmDialog(
				controleChatCliente.getChatWindow().getMainPanel(),
				rodadasPanel, Lang.msg("rodadaCampeonato", new String[] {
						numeroRodada.toString(), campeonatoSelecionado }),
				JOptionPane.YES_NO_OPTION);
		if (JOptionPane.YES_OPTION == showConfirmDialog) {
			Long id = (Long) rodadasTable.getValueAt(rodadasTable
					.getSelectedRow(), 9);
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				RodadaCampeonatoMesa11 rodadaCampeonatoMesa11 = (RodadaCampeonatoMesa11) iterator
						.next();
				if (rodadaCampeonatoMesa11.getId().equals(id)) {
					controleJogosCliente.criarJogoCampeonato(
							rodadaCampeonatoMesa11, nomeCampeonato);
					break;
				}
			}
		}

	}

	private JPanel gerarPainelRodadas(final List rodadas, String nomeCampeonato) {
		rodadasTable = new JTable();
		final TableModel rodadasTableModel = new AbstractTableModel() {
			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				RodadaCampeonatoMesa11 value = (RodadaCampeonatoMesa11) rodadas
						.get(rowIndex);

				switch (columnIndex) {
				case 0:
					return new Boolean(value.isCpuCasa());
				case 1:
					return value.getJogadorCasa() != null ? value
							.getJogadorCasa().getLogin() : "";
				case 2:
					return value.getTimeCasa().getNomeAbrev();
				case 3:
					return value.getGolsCasa();
				case 4:
					return value.getRodadaEfetuda() != null
							&& value.getRodadaEfetuda() ? Lang.msg("sim")
							: Lang.msg("nao");
				case 5:
					return value.getGolsVisita();
				case 6:
					return value.getTimeVisita().getNomeAbrev();
				case 7:
					return value.getJogadorVisita() != null ? value
							.getJogadorVisita().getLogin() : "";
				case 8:
					return new Boolean(value.isCpuVisita());
				case 9:
					return value.getId();
				default:
					return "";
				}
			}

			@Override
			public int getRowCount() {
				if (rodadas == null) {
					return 0;
				}
				return rodadas.size();
			}

			@Override
			public int getColumnCount() {
				return 10;
			}

			@Override
			public String getColumnName(int columnIndex) {

				switch (columnIndex) {
				case 0:
					return Lang.msg("cpuCasa");
				case 1:
					return Lang.msg("jogadorCasa");
				case 2:
					return Lang.msg("timeCasa");
				case 3:
					return Lang.msg("gosCasa");
				case 4:
					return Lang.msg("aconteceu");
				case 5:
					return Lang.msg("golsVisita");
				case 6:
					return Lang.msg("timeVisita");
				case 7:
					return Lang.msg("jogadorVisita");
				case 8:
					return Lang.msg("cpuVisita");
				case 9:
					return Lang.msg("id");
				default:
					return "";
				}

			}

			@Override
			public Class getColumnClass(int c) {
				return getValueAt(0, c).getClass();
			}

			public void setValueAt(Object value, int row, int col) {
				if (value == null) {
					return;
				}
				RodadaCampeonatoMesa11 campeonatoMesa11 = (RodadaCampeonatoMesa11) rodadas
						.get(row);
				switch (col) {
				case 0: {
					Boolean val = (Boolean) value;
					if (val) {
						campeonatoMesa11.setJogadorCasa(null);
					}
					campeonatoMesa11.setCpuCasa(val);
					break;
				}
				case 1: {
					Usuario usuario = new Usuario();
					usuario.setLogin((String) value);
					campeonatoMesa11.setJogadorCasa(usuario);
					break;
				}
				case 7: {
					Usuario usuario = new Usuario();
					usuario.setLogin((String) value);
					campeonatoMesa11.setJogadorVisita(usuario);
					break;
				}
				case 8: {
					Boolean val = (Boolean) value;
					if (val) {
						campeonatoMesa11.setJogadorVisita(null);
					}
					campeonatoMesa11.setCpuVisita(val);
					break;
				}
				default:
				}

				fireTableCellUpdated(row, col);

			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				switch (columnIndex) {
				case 0:
					return true;
				case 1:
					return true;
				case 2:
					return false;
				case 3:
					return false;
				case 4:
					return false;
				case 5:
					return false;
				case 6:
					return false;
				case 7:
					return true;
				case 8:
					return true;
				case 9:
					return false;
				default:
					return false;
				}
			}
		};

		JComboBox jogadores = new JComboBox();
		NnpeTO mesa11to = new NnpeTO();
		mesa11to.setComando(ConstantesMesa11.OBTER_JOGADORES_CAMPEONATO);
		mesa11to.setData(campeonatoSelecionado);
		mesa11to = (NnpeTO) enviarObjeto(mesa11to);
		List jogadoresSrv = (List) mesa11to.getData();
		jogadores.addItem("");
		for (Iterator iterator = jogadoresSrv.iterator(); iterator.hasNext();) {
			String loginJogador = (String) iterator.next();
			jogadores.addItem(loginJogador);
		}
		rodadasTable.setModel(rodadasTableModel);
		rodadasTable.getColumnModel().getColumn(1).setCellEditor(
				new DefaultCellEditor(jogadores));
		rodadasTable.getColumnModel().getColumn(7).setCellEditor(
				new DefaultCellEditor(jogadores));
		for (int i = 0; i < rodadasTableModel.getColumnCount(); i++) {
			rodadasTable.getColumn(rodadasTableModel.getColumnName(i))
					.setMinWidth(
							Util.larguraTexto(rodadasTableModel
									.getColumnName(i), null));
		}
		JScrollPane rodadasJs = new JScrollPane(rodadasTable) {
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(680, 150);
			}
		};
		JPanel jPanel = new JPanel();
		jPanel.add(rodadasJs);
		return jPanel;
	}
}
