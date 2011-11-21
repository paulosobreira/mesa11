package br.mesa11.visao;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
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
import br.mesa11.ConstantesMesa11;
import br.mesa11.conceito.ControleJogo;
import br.nnpe.ExampleFileFilter;
import br.nnpe.ImageUtil;
import br.nnpe.Logger;
import br.nnpe.Util;
import br.recursos.Lang;
import br.servlet.ServletMesa11;
import br.tos.Mesa11TO;

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
	private JCheckBox corAlternativa1;
	private JCheckBox corAlternativa2;
	private JComboBox uniformeAlternativo1;
	private JComboBox uniformeAlternativo2;
	private ControleJogo controleJogo;
	private JLabel imgLocalIconLabel = new JLabel();
	private JLabel imgRemotaIconLabel = new JLabel();
	private String nomeImgIconLabel;
	private JComponent panelImgMostrar;
	protected BufferedImage imagemEnviar;
	private JComboBox comboBoxNomeImgsTabela;
	private JComboBox comboBoxNomeImgs;
	private JTextField textFieldNomeAbrevTime;

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
		jTabbedPane.addTab(Lang.msg("enviarImagem"), gerarEnviarImagem());

		setLayout(new BorderLayout());
		JPanel panelTime = new JPanel(new GridLayout(1, 6, 10, 30));
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
				return Lang.msg("nomeAbrevTime");
			}
		});
		textFieldNomeAbrevTime = new JTextField(time.getNomeAbrev(), 3);
		panelTime.add(textFieldNomeAbrevTime);
		textFieldNomeAbrevTime.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				String text = textFieldNomeAbrevTime.getText();
				String abrev = "";
				for (int i = 0; i < text.length(); i++) {
					if (i > 2) {
						break;
					}
					abrev += text.charAt(i);
				}
				EditorTime.this.time.setNomeAbrev(abrev.toUpperCase());
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

		add(panelTime, BorderLayout.NORTH);
		add(jTabbedPane, BorderLayout.CENTER);

		JButton salvarTime = new JButton() {
			@Override
			public String getText() {
				return Lang.msg("salvarTime");
			}
		};

		salvarTime.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				EditorTime.this.controleJogo.salvarTime(EditorTime.this.time);

			}
		});
		// add(salvarTime, BorderLayout.SOUTH);
	}

	private Component gerarEnviarImagem() {
		final int lado = ConstantesMesa11.DIAMENTRO_BOTAO + 10;
		BufferedImage botaoImg = new BufferedImage(lado, lado,
				BufferedImage.TYPE_INT_ARGB);
		imgLocalIconLabel.setIcon(new ImageIcon(botaoImg));
		JButton escolherImagem = new JButton() {
			public String getText() {
				return Lang.msg("escolherImagem");
			};
		};
		escolherImagem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

				ExampleFileFilter exampleFileFilter = new ExampleFileFilter(
						"jpg");
				fileChooser.setFileFilter(exampleFileFilter);

				int result = fileChooser.showOpenDialog(null);

				if (result == JFileChooser.CANCEL_OPTION) {
					return;
				}
				File file = fileChooser.getSelectedFile();
				nomeImgIconLabel = file.getName();
				BufferedImage botaoImg = ImageUtil
						.toBufferedImage((new ImageIcon(file.getAbsolutePath()))
								.getImage());
				double menor = Double.MAX_VALUE;
				if (botaoImg.getWidth() < menor) {
					menor = botaoImg.getWidth();
				}
				if (botaoImg.getHeight() < menor) {
					menor = botaoImg.getHeight();
				}
				BufferedImage newBuffer = new BufferedImage((int) (botaoImg
						.getWidth()), (int) (botaoImg.getHeight()),
						BufferedImage.TYPE_INT_RGB);
				Graphics2D graphics2d = (Graphics2D) newBuffer.getGraphics();
				graphics2d.setColor(Color.WHITE);
				graphics2d.fillRect(0, 0, botaoImg.getWidth(), botaoImg
						.getWidth());
				Ellipse2D externo = new Ellipse2D.Double(0, 0, (menor), (menor));
				graphics2d.setClip(externo);
				graphics2d.drawImage(botaoImg, 0, 0, null);

				double zoom = (double) lado / menor;
				AffineTransform affineTransform = AffineTransform
						.getScaleInstance(zoom, zoom);
				AffineTransformOp affineTransformOp = new AffineTransformOp(
						affineTransform, AffineTransformOp.TYPE_BILINEAR);
				BufferedImage zoomBuffer = new BufferedImage(
						(int) (menor * zoom), (int) (menor * zoom),
						BufferedImage.TYPE_INT_RGB);
				affineTransformOp.filter(newBuffer, zoomBuffer);

				imgLocalIconLabel.setIcon(new ImageIcon(zoomBuffer));
				imagemEnviar = zoomBuffer;
			}
		});
		JButton enviarImagem = new JButton() {
			public String getText() {
				return Lang.msg("enviarImagem");
			};
		};
		enviarImagem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Mesa11TO mesa11to = new Mesa11TO();
				if (Util.isNullOrEmpty(nomeImgIconLabel)
						|| imagemEnviar == null) {
					JOptionPane.showMessageDialog(EditorTime.this, Lang
							.msg("nomeVazio"), "", JOptionPane.ERROR_MESSAGE);
					return;
				}
				mesa11to.setComando(ConstantesMesa11.ENVIAR_IMAGEM);

				mesa11to.setData(nomeImgIconLabel);
				BufferedImage buff = ImageUtil
						.toBufferedImage(((ImageIcon) imgLocalIconLabel
								.getIcon()).getImage());
				mesa11to.setDataBytes(ImageUtil.bufferedImage2ByteArray(buff));

				if (ConstantesMesa11.OK.equals(EditorTime.this.controleJogo
						.enviarObjeto(mesa11to))) {
					recarregarComboImagens();
					JOptionPane.showMessageDialog(EditorTime.this, Lang
							.msg("imagemEnviada"), "",
							JOptionPane.INFORMATION_MESSAGE);
					if (comboBoxNomeImgs != null) {
						comboBoxNomeImgs.setSelectedItem(nomeImgIconLabel);
					}
				}

			}
		});
		if (comboBoxNomeImgs == null)
			comboBoxNomeImgs = new JComboBox();
		recarregarComboImagens();
		comboBoxNomeImgs.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if (arg0.getStateChange() == ItemEvent.SELECTED) {
					mostrarImagemRemota();
				}
			}
		});
		mostrarImagemRemota();
		if (!Util.isNullOrEmpty(time.getImagem())) {
			comboBoxNomeImgs.setSelectedItem(time.getImagem());
			System.out
					.println("comboBoxNomeImgs.setSelectedItem(time.getImagem());");
		}
		JButton buttonMostrar = new JButton() {
			public String getText() {
				String escudo = " ";
				if (!Util.isNullOrEmpty(time.getImagem())) {
					escudo += time.getImagem();
				}
				return Lang.msg("escudoTime") + escudo;
			};
		};
		buttonMostrar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				mostrarImagemRemota();
				String arquivo = (String) comboBoxNomeImgs.getSelectedItem();
				time.setImagem(arquivo);
			}
		});
		JPanel jPanel = new JPanel(new BorderLayout());

		JPanel botoesEnviar = new JPanel(new GridLayout(2, 1, 10, 5));
		botoesEnviar.setBorder(new TitledBorder("") {
			@Override
			public String getTitle() {
				return Lang.msg("enviarImagem");
			}
		});
		botoesEnviar.add(escolherImagem);
		botoesEnviar.add(enviarImagem);

		JPanel botoesMostrar = new JPanel(new GridLayout(2, 1, 10, 5));
		botoesMostrar.setBorder(new TitledBorder("") {
			@Override
			public String getTitle() {
				return Lang.msg("mostrarImagem");
			}
		});

		botoesMostrar.add(comboBoxNomeImgs);
		botoesMostrar.add(buttonMostrar);

		JPanel botoes = new JPanel(new GridLayout(1, 2, 10, 10));
		botoes.add(botoesEnviar);
		botoes.add(botoesMostrar);
		jPanel.add(botoes, BorderLayout.NORTH);

		JPanel panelImgEnviar = new JPanel();
		panelImgEnviar.setBorder(new TitledBorder("") {
			@Override
			public String getTitle() {
				return Lang.msg("imagemLocal");
			}
		});
		panelImgMostrar = new JPanel();
		panelImgMostrar.add(imgRemotaIconLabel);
		panelImgMostrar.setBorder(new TitledBorder("") {
			@Override
			public String getTitle() {
				return Lang.msg("imagemRemota");
			}
		});
		panelImgEnviar.add(imgLocalIconLabel);
		panelImgEnviar.setBorder(new TitledBorder("") {
			@Override
			public String getTitle() {
				return Lang.msg("imagemLocal");
			}
		});
		JPanel panels = new JPanel(new GridLayout(1, 2, 10, 10));
		panels.add(panelImgEnviar);
		panels.add(panelImgMostrar);

		jPanel.add(panels, BorderLayout.CENTER);
		return jPanel;
	}

	protected void mostrarImagemRemota() {
		String arquivo = (String) comboBoxNomeImgs.getSelectedItem();
		if (arquivo == null || !arquivo.endsWith("jpg")) {
			return;
		}
		ImageIcon icon = ImageUtil.carregarImagem(controleJogo
				.getMesa11Applet().getCodeBase()
				+ "midia/" + arquivo);
		if (icon != null)
			imgRemotaIconLabel.setIcon(icon);

	}

	private void recarregarComboImagens() {
		Mesa11TO mesa11to = new Mesa11TO();
		mesa11to.setComando(ConstantesMesa11.OBTER_TODAS_IMAGENS);
		Object ret = controleJogo.enviarObjeto(mesa11to);
		if (ret instanceof Mesa11TO) {
			mesa11to = (Mesa11TO) ret;
			String[] imagens = (String[]) mesa11to.getData();
			Arrays.sort(imagens);
			if (imagens != null) {
				if (comboBoxNomeImgsTabela != null) {
					comboBoxNomeImgsTabela.removeAllItems();
					for (int i = 0; i < imagens.length; i++) {
						if (!imagens[i].endsWith("jpg")) {
							continue;
						}
						comboBoxNomeImgsTabela.addItem(imagens[i]);
					}
				}
				if (comboBoxNomeImgs != null) {
					comboBoxNomeImgs.removeAllItems();
					for (int i = 0; i < imagens.length; i++) {
						if (!imagens[i].endsWith("jpg")) {
							continue;
						}
						comboBoxNomeImgs.addItem(imagens[i]);
					}
				}
			}
		}

	}

	private Component gerarTabelaAtributosBotao() {
		tabelaBotoes = new JTable();
		final BotaoTableModel botaoTableModel = new BotaoTableModel(time
				.getBotoes(), controleJogo);
		tabelaBotoes.setModel(botaoTableModel);
		botaoTableModel.addMouseListener(tabelaBotoes);
		TableColumn columnNome = tabelaBotoes.getColumnModel().getColumn(0);
		TableColumn columnTitular = tabelaBotoes.getColumnModel().getColumn(2);
		TableColumn columnGoleiro = tabelaBotoes.getColumnModel().getColumn(3);
		TableColumn columnPrecisao = tabelaBotoes.getColumnModel().getColumn(4);
		TableColumn columnForca = tabelaBotoes.getColumnModel().getColumn(5);
		TableColumn columnDefesa = tabelaBotoes.getColumnModel().getColumn(6);
		TableColumn columnImgNome = tabelaBotoes.getColumnModel().getColumn(7);
		TableColumn columnImg = tabelaBotoes.getColumnModel().getColumn(8);

		columnNome.setMinWidth(120);
		columnImg.setMinWidth(128);
		columnImgNome.setMinWidth(128);
		tabelaBotoes.setRowHeight(48);
		if (comboBoxNomeImgsTabela == null) {
			comboBoxNomeImgsTabela = new JComboBox();
		}
		recarregarComboImagens();
		columnImgNome.setCellEditor(new DefaultCellEditor(
				comboBoxNomeImgsTabela));

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

		JPanel panelTabela = new JPanel(new BorderLayout());
		panelTabela.add(new JScrollPane(tabelaBotoes), BorderLayout.CENTER);
		JPanel panelBotoes = new JPanel(new GridLayout(1, 2));
		panelBotoes.add(inserirLinha);
		panelBotoes.add(removerLinha);

		JButton gerarXmlTime = new JButton() {
			public String getText() {
				return Lang.msg("gerarXmlTime");
			};
		};
		gerarXmlTime.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EditorTime.this.controleJogo.gerarXmlTime(time);
			}
		});
		JButton randomizarAtributos = new JButton() {
			public String getText() {
				return Lang.msg("randomizarAtributos");
			};
		};
		randomizarAtributos.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EditorTime.this.controleJogo.randomizarAtributos(time);
				tabelaBotoes.updateUI();
			}
		});

		JPanel painelSul = new JPanel(new BorderLayout());
		JPanel panel = new JPanel(new GridLayout(1, 2));
		panel.add(gerarXmlTime);
		panel.add(randomizarAtributos);
		painelSul.add(panel, BorderLayout.SOUTH);
		if (!controleJogo.isJogoOnlineCliente()) {
			painelSul.add(panelBotoes, BorderLayout.CENTER);
		}
		panelTabela.add(painelSul, BorderLayout.SOUTH);
		return panelTabela;
	}

	private Component gerarTabelaCores() {
		JPanel panelTime = new JPanel(new GridLayout(2, 2));
		panelTime.setBorder(new TitledBorder("") {
			/*
			 * (non-Javadoc)
			 * 
			 * @see javax.swing.border.TitledBorder#getTitle()
			 */
			@Override
			public String getTitle() {
				return Lang.msg("coresAlternativas");
			}
		});
		panelTime.add(new JLabel() {
			@Override
			public String getText() {
				return Lang.msg("corMeiaCorNumero1");
			}
		});
		corAlternativa1 = new JCheckBox();
		corAlternativa1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				EditorTime.this.time.setCorMeiaNumero1(corAlternativa1
						.isSelected());
				imgUn1.setIcon(new ImageIcon(BotaoUtils.desenhaUniforme(
						EditorTime.this.time, 1)));
				imgGolUn1.setIcon(new ImageIcon(BotaoUtils
						.desenhaUniformeGoleiro(EditorTime.this.time, 1)));

			}
		});
		uniformeAlternativo1 = new JComboBox(new String[] { "0", "1", "2", "3",
				"4", "5" });
		uniformeAlternativo1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String selVal = (String) uniformeAlternativo1.getSelectedItem();
				EditorTime.this.time.setTipoUniforme1(new Integer(selVal)
						.intValue());
				imgUn1.setIcon(new ImageIcon(BotaoUtils.desenhaUniforme(
						EditorTime.this.time, 1)));
				imgGolUn1.setIcon(new ImageIcon(BotaoUtils
						.desenhaUniformeGoleiro(EditorTime.this.time, 1)));

			}
		});
		JPanel alts1 = new JPanel(new GridLayout(1, 2));
		alts1.add(corAlternativa1);
		alts1.add(uniformeAlternativo1);
		panelTime.add(alts1);
		panelTime.add(new JLabel() {
			@Override
			public String getText() {
				return Lang.msg("corMeiaCorNumero2");
			}
		});
		corAlternativa2 = new JCheckBox();
		corAlternativa2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				EditorTime.this.time.setCorMeiaNumero2(corAlternativa2
						.isSelected());
				imgUn2.setIcon(new ImageIcon(BotaoUtils.desenhaUniforme(
						EditorTime.this.time, 2)));
				imgGolUn2.setIcon(new ImageIcon(BotaoUtils
						.desenhaUniformeGoleiro(EditorTime.this.time, 2)));
			}
		});

		uniformeAlternativo2 = new JComboBox(new String[] { "0", "1", "2", "3",
				"4", "5" });
		uniformeAlternativo2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String selVal = (String) uniformeAlternativo2.getSelectedItem();
				EditorTime.this.time.setTipoUniforme2(new Integer(selVal)
						.intValue());
				imgUn2.setIcon(new ImageIcon(BotaoUtils.desenhaUniforme(
						EditorTime.this.time, 2)));
				imgGolUn2.setIcon(new ImageIcon(BotaoUtils
						.desenhaUniformeGoleiro(EditorTime.this.time, 2)));
			}
		});
		JPanel alts2 = new JPanel(new GridLayout(1, 2));
		alts2.add(corAlternativa2);
		alts2.add(uniformeAlternativo2);
		panelTime.add(alts2);

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
				return Lang.msg("edicaoUniformes");
			}
		});
		cores.setLayout(new GridLayout(2, 1));
		cores.add(un1);
		cores.add(un2);

		JPanel retorno = new JPanel(new BorderLayout());
		retorno.add(panelTime, BorderLayout.SOUTH);
		retorno.add(cores, BorderLayout.CENTER);

		return retorno;
	}

	private void gerarLabelsCores() {
		labelCor1.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				Color color = JColorChooser.showDialog(EditorTime.this, Lang
						.msg("escolhaCor"), Color.WHITE);
				if (color == null) {
					return;
				}
				setCor(color, labelCor1);
				time.setCor1RGB(color.getRGB());
				imgUn1.setIcon(new ImageIcon(BotaoUtils
						.desenhaUniforme(time, 1)));
				imgGolUn1.setIcon(new ImageIcon(BotaoUtils
						.desenhaUniformeGoleiro(time, 1)));
			}

		});
		labelCor2.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				Color color = JColorChooser.showDialog(EditorTime.this, Lang
						.msg("escolhaCor"), Color.WHITE);
				if (color == null) {
					return;
				}
				setCor(color, labelCor2);
				time.setCor2RGB(color.getRGB());
				imgUn1.setIcon(new ImageIcon(BotaoUtils
						.desenhaUniforme(time, 1)));
				imgGolUn1.setIcon(new ImageIcon(BotaoUtils
						.desenhaUniformeGoleiro(time, 1)));

			}
		});
		labelCor3.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Color color = JColorChooser.showDialog(EditorTime.this, Lang
						.msg("escolhaCor"), Color.WHITE);
				if (color == null) {
					return;
				}
				setCor(color, labelCor3);
				time.setCor3RGB(color.getRGB());
				imgUn1.setIcon(new ImageIcon(BotaoUtils
						.desenhaUniforme(time, 1)));
				imgGolUn1.setIcon(new ImageIcon(BotaoUtils
						.desenhaUniformeGoleiro(time, 1)));

			}
		});
		labelCor4.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				Color color = JColorChooser.showDialog(EditorTime.this, Lang
						.msg("escolhaCor"), Color.WHITE);
				if (color == null) {
					return;
				}
				setCor(color, labelCor4);
				time.setCor4RGB(color.getRGB());
				imgUn2.setIcon(new ImageIcon(BotaoUtils
						.desenhaUniforme(time, 2)));
				imgGolUn2.setIcon(new ImageIcon(BotaoUtils
						.desenhaUniformeGoleiro(time, 2)));
			}

		});
		labelCor5.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				Color color = JColorChooser.showDialog(EditorTime.this, Lang
						.msg("escolhaCor"), Color.WHITE);
				if (color == null) {
					return;
				}
				setCor(color, labelCor5);
				time.setCor5RGB(color.getRGB());
				imgUn2.setIcon(new ImageIcon(BotaoUtils
						.desenhaUniforme(time, 2)));
				imgGolUn2.setIcon(new ImageIcon(BotaoUtils
						.desenhaUniformeGoleiro(time, 2)));

			}
		});
		labelCor6.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				Color color = JColorChooser.showDialog(EditorTime.this, Lang
						.msg("escolhaCor"), Color.WHITE);
				if (color == null) {
					return;
				}
				setCor(color, labelCor6);
				time.setCor6RGB(color.getRGB());
				imgUn2.setIcon(new ImageIcon(BotaoUtils
						.desenhaUniforme(time, 2)));
				imgGolUn2.setIcon(new ImageIcon(BotaoUtils
						.desenhaUniformeGoleiro(time, 2)));
			}

		});
		setCor(new Color(time.getCor1RGB()), labelCor1);
		setCor(new Color(time.getCor2RGB()), labelCor2);
		setCor(new Color(time.getCor3RGB()), labelCor3);
		setCor(new Color(time.getCor4RGB()), labelCor4);
		setCor(new Color(time.getCor5RGB()), labelCor5);
		setCor(new Color(time.getCor6RGB()), labelCor6);
		imgUn1.setIcon(new ImageIcon(BotaoUtils.desenhaUniforme(time, 1)));
		imgGolUn1.setIcon(new ImageIcon(BotaoUtils.desenhaUniformeGoleiro(time,
				1)));

		imgUn2.setIcon(new ImageIcon(BotaoUtils.desenhaUniforme(time, 2)));
		imgGolUn2.setIcon(new ImageIcon(BotaoUtils.desenhaUniformeGoleiro(time,
				2)));

	}

	public void setCor(Color color, JLabel label) {
		if (color == null) {
			return;
		}
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
		return new Dimension(800, 500);
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
		time.setNomeAbrev("mesa11");
		ControleJogo controleJogo = new ControleJogo(new JFrame());
		EditorTime editorTime = new EditorTime(time, controleJogo);
		// JFrame frame = new JFrame();
		// frame.add(editorTime);
		// frame.setVisible(true);
		JOptionPane.showMessageDialog(null, editorTime);

	}
}
