package br.mesa11.cliente;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import br.applet.Mesa11Applet;
import br.hibernate.Time;
import br.mesa11.BotaoUtils;
import br.mesa11.ConstantesMesa11;
import br.mesa11.conceito.ControleJogo;
import br.nnpe.Logger;
import br.recursos.Lang;
import br.tos.DadosJogoSrvMesa11;
import br.tos.Mesa11TO;

public class ControleJogosCliente {
	private ChatWindow chatWindow;
	private boolean segundoUniforme;
	private ControleChatCliente controleChatCliente;
	private ControleJogo controleJogo;
	private JComboBox jComboBoxTimes = new JComboBox(new String[] { Lang
			.msg("semTimes") });

	public ControleJogosCliente(ChatWindow chatWindow,
			ControleChatCliente controleChatCliente) {
		super();
		this.chatWindow = chatWindow;
		this.controleChatCliente = controleChatCliente;
	}

	private Object enviarObjeto(Mesa11TO mesa11to) {
		return controleChatCliente.enviarObjeto(mesa11to);
	}

	public void criarJogo() {
		segundoUniforme = false;
		Mesa11TO mesa11to = new Mesa11TO();
		mesa11to.setComando(ConstantesMesa11.OBTER_TODOS_TIMES);
		Object ret = enviarObjeto(mesa11to);
		boolean semTimes = true;
		if (ret instanceof Mesa11TO) {
			mesa11to = (Mesa11TO) ret;
			String[] times = (String[]) mesa11to.getData();
			jComboBoxTimes = new JComboBox(times);
			semTimes = false;
		}
		JPanel panelComboTimes = new JPanel();

		final JLabel uniforme = new JLabel() {
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(ConstantesMesa11.DIAMENTRO_BOTAO + 10,
						ConstantesMesa11.DIAMENTRO_BOTAO + 10);
			}
		};
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

		JPanel opcoesJogoPanel = new JPanel(new GridLayout(4, 2, 10, 10));
		opcoesJogoPanel.add(new JLabel() {
			@Override
			public String getText() {
				return Lang.msg("tempoJogoMinutos");
			}
		});
		JComboBox tempoJogoCombo = new JComboBox();
		tempoJogoCombo.addItem(new Integer(10));
		tempoJogoCombo.addItem(new Integer(20));
		tempoJogoCombo.addItem(new Integer(30));
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

		String jogoSelecionado = chatWindow.obterJogoSelecionado();
		int result = JOptionPane.showConfirmDialog(chatWindow.getMainPanel(),
				iniciarJogoPanel, Lang.msg("criarJogo"),
				JOptionPane.YES_NO_OPTION);
		if (result == JOptionPane.YES_OPTION) {
			System.out.println("JOptionPane.YES_OPTION");
			DadosJogoSrvMesa11 jogoSrvMesa11 = new DadosJogoSrvMesa11();
			String jogador = controleChatCliente.getSessaoCliente()
					.getNomeJogador();
			jogoSrvMesa11.setNomeCriador(jogador);
			String nomeTime = (String) jComboBoxTimes.getSelectedItem();
			jogoSrvMesa11.setTimeCasa(nomeTime);
			jogoSrvMesa11.setSegundoUniformeTimeCasa(segundoUniforme);
			jogoSrvMesa11.setTempoJogo((Integer) tempoJogoCombo
					.getSelectedItem());
			jogoSrvMesa11.setTempoJogoJogada((Integer) tempoJogadaCombo
					.getSelectedItem());
			jogoSrvMesa11.setBolaCampo(Lang.key((String) campoBolaCombo
					.getSelectedItem()));
			jogoSrvMesa11.setSenhaJogo(jTextFieldSenhaJogo.getText());
			mesa11to = new Mesa11TO();
			mesa11to.setComando(ConstantesMesa11.CRIAR_JOGO);
			mesa11to.setData(jogoSrvMesa11);
			enviarObjeto(mesa11to);
			System.out.println("ConstantesMesa11.CRIAR_JOGO");
		}
	}

	public void entrarJogo(String jogoSelecionado) {
		// TODO Auto-generated method stub

	}

}
