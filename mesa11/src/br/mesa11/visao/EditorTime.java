/**
 * 
 */
package br.mesa11.visao;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableColumn;

import br.hibernate.Botao;
import br.hibernate.Time;
import br.mesa11.BotaoUtils;
import br.mesa11.conceito.ControleJogo;
import br.nnpe.Util;
import br.recursos.Lang;

/**
 * @author Sobreira 19/06/2010
 */
public class EditorTime extends JPanel {

	private JLabel labelCor1 = new JLabel(Lang.msg("corEquipe") + " 1:");
	private JLabel labelCor2 = new JLabel(Lang.msg("corEquipe") + " 2:");
	private JLabel labelCor3 = new JLabel(Lang.msg("corEquipe") + " 3:");
	private JLabel labelCor4 = new JLabel(Lang.msg("corEquipe") + " 4:");
	private JLabel labelCor5 = new JLabel(Lang.msg("corEquipe") + " 5:");
	private JLabel labelCor6 = new JLabel(Lang.msg("corEquipe") + " 6:");
	private JLabel imgUn1 = new JLabel("");
	private JLabel imgGolUn1 = new JLabel("");
	private JLabel imgUn2 = new JLabel("");
	private JLabel imgGolUn2 = new JLabel("");
	private JLabel qtdePts = null;
	private JTextField textFieldNomeTime;
	private Time time;
	private JTable tabelaBotoes;
	private JCheckBox corMeiaCorNumero1;
	private JCheckBox corMeiaCorNumero2;
	private ControleJogo controleJogo;

	/**
	 * 
	 */
	public EditorTime(Time time, ControleJogo controleJogo) {
		this.time = time;
		this.controleJogo = controleJogo;
		gerarLabelsCores();
		JTabbedPane jTabbedPane = new JTabbedPane();
		jTabbedPane
				.addTab(Lang.msg("nomesBotoes"), gerarTabelaAtributosBotao());
		jTabbedPane.addTab(Lang.msg("coresBotoes"), gerarTabelaCores());

		setLayout(new BorderLayout());
		JPanel panelTime = new JPanel(new GridLayout(4, 2));
		panelTime.setBorder(new TitledBorder("") {
			@Override
			public String getTitle() {
				return Lang.msg("dadosTime");
			}
		});
		panelTime.add(new JLabel() {
			@Override
			public String getText() {
				return Lang.msg("nomeTime");
			}
		});
		textFieldNomeTime = new JTextField(time.getNome());
		panelTime.add(textFieldNomeTime);
		textFieldNomeTime.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				EditorTime.this.time.setNome(textFieldNomeTime.getText());

			}

			@Override
			public void focusGained(FocusEvent e) {
				// TODO Auto-generated method stub

			}
		});
		panelTime.add(new JLabel() {
			@Override
			public String getText() {
				return Lang.msg("pontosTime");
			}
		});
		qtdePts = new JLabel() {
			@Override
			public String getText() {
				return EditorTime.this.time.getQtdePontos() == null ? "0"
						: EditorTime.this.time.getQtdePontos().toString();
			}
		};
		panelTime.add(qtdePts);
		panelTime.add(new JLabel() {
			@Override
			public String getText() {
				return Lang.msg("corMeiaCorNumero");
			}
		});
		corMeiaCorNumero1 = new JCheckBox();
		corMeiaCorNumero1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				EditorTime.this.time.setCorMeiaNumero1(corMeiaCorNumero1
						.isSelected());
				imgUn1.setIcon(new ImageIcon(BotaoUtils.desenhaUniforme(
						EditorTime.this.time, 1)));
				imgGolUn1.setIcon(new ImageIcon(BotaoUtils.desenhaUniformeGoleiro(
						EditorTime.this.time, 1)));

			}
		});
		panelTime.add(corMeiaCorNumero1);
		panelTime.add(new JLabel() {
			@Override
			public String getText() {
				return Lang.msg("corMeiaCorNumero");
			}
		});
		corMeiaCorNumero2 = new JCheckBox();
		corMeiaCorNumero2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				EditorTime.this.time.setCorMeiaNumero2(corMeiaCorNumero2
						.isSelected());
				imgUn2.setIcon(new ImageIcon(BotaoUtils.desenhaUniforme(
						EditorTime.this.time, 2)));
				imgGolUn2.setIcon(new ImageIcon(BotaoUtils.desenhaUniformeGoleiro(
						EditorTime.this.time, 2)));
			}
		});
		panelTime.add(corMeiaCorNumero2);
		add(panelTime, BorderLayout.NORTH);
		add(jTabbedPane, BorderLayout.CENTER);
	}

	private Component gerarTabelaAtributosBotao() {
		tabelaBotoes = new JTable();
		final BotaoTableModel botaoTableModel = new BotaoTableModel(time
				.getBotoes());
		tabelaBotoes.setModel(botaoTableModel);
		botaoTableModel.addMouseListener(tabelaBotoes);
		TableColumn columnNome = tabelaBotoes.getColumnModel().getColumn(0);
		TableColumn columnTitular = tabelaBotoes.getColumnModel().getColumn(2);
		TableColumn columnGoleiro = tabelaBotoes.getColumnModel().getColumn(3);
		TableColumn columnPrecisao = tabelaBotoes.getColumnModel().getColumn(4);
		TableColumn columnForca = tabelaBotoes.getColumnModel().getColumn(5);
		TableColumn columnDefesa = tabelaBotoes.getColumnModel().getColumn(6);
		columnNome.setMaxWidth(250);
		columnNome.setMinWidth(250);

		JComboBox comboBoxSimNao = new JComboBox();
		comboBoxSimNao.addItem(Lang.msg("sim"));
		comboBoxSimNao.addItem(Lang.msg("nao"));
		columnTitular.setCellEditor(new DefaultCellEditor(comboBoxSimNao));
		columnGoleiro.setCellEditor(new DefaultCellEditor(comboBoxSimNao));

		JSpinner spinnerAtributos = new JSpinner(new SpinnerNumberModel() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see javax.swing.SpinnerNumberModel#setValue(java.lang.Object)
			 */
			@Override
			public void setValue(Object value) {
				Integer val = (Integer) value;
				if (val.intValue() > 1000) {
					return;
				}
				if (val.intValue() < 0) {
					return;
				}
				super.setValue(value);
			}

			@Override
			public Object getNextValue() {
				Integer qtde = time.getQtdePontos();
				Integer val = (Integer) getValue();
				if (qtde <= 0 || val >= 1000) {
					return val;
				}
				qtde--;
				time.setQtdePontos(qtde);
				qtdePts.repaint();
				return super.getNextValue();
			}

			@Override
			public Object getPreviousValue() {
				Integer qtde = time.getQtdePontos();
				qtde++;
				time.setQtdePontos(qtde);
				qtdePts.repaint();
				return super.getPreviousValue();
			}
		});
		JFormattedTextField tfspinnerAtributos = ((JSpinner.DefaultEditor) spinnerAtributos
				.getEditor()).getTextField();
		tfspinnerAtributos.setEditable(false);
		columnPrecisao.setCellEditor(new SpinnerEditor(spinnerAtributos));
		columnForca.setCellEditor(new SpinnerEditor(spinnerAtributos));
		columnDefesa.setCellEditor(new SpinnerEditor(spinnerAtributos));

		JButton inserirLinha = new JButton() {
			@Override
			public String getText() {
				return Lang.msg("inseirBotao");
			}
		};
		inserirLinha.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				controleJogo.inserirBotaoEditor(time, botaoTableModel);

			}
		});

		JButton removerLinha = new JButton() {
			@Override
			public String getText() {
				return Lang.msg("removerBotao");
			}
		};

		removerLinha.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int[] selected = tabelaBotoes.getSelectedRows();

				for (int i = selected.length - 1; i >= 0; i--)
					botaoTableModel.removerLinha(selected[i]);

			}
		});

		JButton salvarTime = new JButton() {
			@Override
			public String getText() {
				return Lang.msg("salvarTime");
			}
		};

		salvarTime.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				controleJogo.salvarTime(time);

			}
		});

		JPanel panelTabela = new JPanel(new BorderLayout());
		panelTabela.add(new JScrollPane(tabelaBotoes), BorderLayout.CENTER);
		JPanel panelBotoes = new JPanel(new GridLayout(1, 3));
		panelBotoes.add(inserirLinha);
		panelBotoes.add(removerLinha);
		panelBotoes.add(salvarTime);
		panelTabela.add(panelBotoes, BorderLayout.SOUTH);
		return panelTabela;
	}

	private Component gerarTabelaCores() {
		JPanel cores1 = new JPanel(new GridLayout(1, 3));
		cores1.add(labelCor1);
		cores1.add(labelCor2);
		cores1.add(labelCor3);
		JPanel cores2 = new JPanel(new GridLayout(1, 3));
		cores2.add(labelCor4);
		cores2.add(labelCor5);
		cores2.add(labelCor6);
		JPanel un1 = new JPanel(new BorderLayout());
		un1.add(cores1, BorderLayout.NORTH);
		un1.add(imgUn1, BorderLayout.WEST);
		un1.add(imgGolUn1, BorderLayout.CENTER);
		JPanel un2 = new JPanel(new BorderLayout());
		un2.add(cores2, BorderLayout.NORTH);
		un2.add(imgUn2, BorderLayout.WEST);
		un2.add(imgGolUn2, BorderLayout.CENTER);

		JPanel cores = new JPanel();
		cores.setBorder(new TitledBorder("") {
			/*
			 * (non-Javadoc)
			 * 
			 * @see javax.swing.border.TitledBorder#getTitle()
			 */
			@Override
			public String getTitle() {
				return Lang.msg("clickNoTexto");
			}
		});
		cores.setLayout(new GridLayout(2, 1));
		cores.add(un1);
		cores.add(un2);
		return cores;
	}

	private void gerarLabelsCores() {
		labelCor1.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				Color color = JColorChooser.showDialog(EditorTime.this, Lang
						.msg("escolhaCor"), Color.WHITE);
				setCor(color, labelCor1);
				time.setCor1RGB(color.getRGB());
				imgUn1.setIcon(new ImageIcon(BotaoUtils.desenhaUniforme(time, 1)));
				imgGolUn1
						.setIcon(new ImageIcon(BotaoUtils.desenhaUniformeGoleiro(time, 1)));
			}

		});
		labelCor2.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				Color color = JColorChooser.showDialog(EditorTime.this, Lang
						.msg("escolhaCor"), Color.WHITE);
				setCor(color, labelCor2);
				time.setCor2RGB(color.getRGB());
				imgUn1.setIcon(new ImageIcon(BotaoUtils.desenhaUniforme(time, 1)));
				imgGolUn1
						.setIcon(new ImageIcon(BotaoUtils.desenhaUniformeGoleiro(time, 1)));

			}
		});
		labelCor3.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Color color = JColorChooser.showDialog(EditorTime.this, Lang
						.msg("escolhaCor"), Color.WHITE);
				setCor(color, labelCor3);
				time.setCor3RGB(color.getRGB());
				imgUn1.setIcon(new ImageIcon(BotaoUtils.desenhaUniforme(time, 1)));
				imgGolUn1
						.setIcon(new ImageIcon(BotaoUtils.desenhaUniformeGoleiro(time, 1)));

			}
		});
		labelCor4.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				Color color = JColorChooser.showDialog(EditorTime.this, Lang
						.msg("escolhaCor"), Color.WHITE);
				setCor(color, labelCor4);
				time.setCor4RGB(color.getRGB());
				imgUn2.setIcon(new ImageIcon(BotaoUtils.desenhaUniforme(time, 2)));
				imgGolUn2
						.setIcon(new ImageIcon(BotaoUtils.desenhaUniformeGoleiro(time, 2)));
			}

		});
		labelCor5.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				Color color = JColorChooser.showDialog(EditorTime.this, Lang
						.msg("escolhaCor"), Color.WHITE);
				setCor(color, labelCor5);
				time.setCor5RGB(color.getRGB());
				imgUn2.setIcon(new ImageIcon(BotaoUtils.desenhaUniforme(time, 2)));
				imgGolUn2
						.setIcon(new ImageIcon(BotaoUtils.desenhaUniformeGoleiro(time, 2)));

			}
		});
		labelCor6.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				Color color = JColorChooser.showDialog(EditorTime.this, Lang
						.msg("escolhaCor"), Color.WHITE);
				setCor(color, labelCor6);
				time.setCor6RGB(color.getRGB());
				imgUn2.setIcon(new ImageIcon(BotaoUtils.desenhaUniforme(time, 2)));
				imgGolUn2
						.setIcon(new ImageIcon(BotaoUtils.desenhaUniformeGoleiro(time, 2)));
			}

		});
		setCor(new Color(time.getCor1RGB()), labelCor1);
		setCor(new Color(time.getCor2RGB()), labelCor2);
		setCor(new Color(time.getCor3RGB()), labelCor3);
		setCor(new Color(time.getCor4RGB()), labelCor4);
		setCor(new Color(time.getCor5RGB()), labelCor5);
		setCor(new Color(time.getCor6RGB()), labelCor6);
		imgUn1.setIcon(new ImageIcon(BotaoUtils.desenhaUniforme(time, 1)));
		imgGolUn1.setIcon(new ImageIcon(BotaoUtils.desenhaUniformeGoleiro(time, 1)));

		imgUn2.setIcon(new ImageIcon(BotaoUtils.desenhaUniforme(time, 2)));
		imgGolUn2.setIcon(new ImageIcon(BotaoUtils.desenhaUniformeGoleiro(time, 2)));

	}




	public void setCor(Color color, JLabel label) {
		label.setOpaque(true);
		label.setBackground(color);
		int valor = (color.getRed() + color.getGreen() + color.getBlue()) / 2;
		if (valor > 250) {
			label.setForeground(Color.BLACK);
		} else {
			label.setForeground(Color.WHITE);
		}
		try {
			label.repaint();
			this.repaint();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#getPreferredSize()
	 */
	@Override
	public Dimension getPreferredSize() {
		// TODO Auto-generated method stub
		return new Dimension(600, 400);
	}

	public static void main(String[] args) {
		Time time = new Time();
		time.setQtdePontos(Util.intervalo(100, 1000));
		Botao botao = new Botao();
		botao.setNome("Mesa");
		botao.setNumero(11);
		botao.setTitular(true);
		botao.setGoleiro(false);
		botao.setForca(Util.intervalo(500, 1000));
		botao.setPrecisao(Util.intervalo(500, 1000));
		botao.setDefesa(Util.intervalo(500, 1000));
		botao.setTime(time);
		time.getBotoes().add(botao);
		time.setNome("mesa11");
		ControleJogo controleJogo = new ControleJogo(new JFrame());
		EditorTime editorTime = new EditorTime(time, controleJogo);
		// JFrame frame = new JFrame();
		// frame.add(editorTime);
		// frame.setVisible(true);
		JOptionPane.showMessageDialog(null, editorTime);

	}
}