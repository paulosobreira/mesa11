package br.mesa11.cliente;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import br.applet.Mesa11Applet;
import br.nnpe.Logger;
import br.nnpe.Util;
import br.recursos.Lang;

public class FormLogin extends JPanel {
	private JComboBox comboIdiomas = new JComboBox(
			new String[]{Lang.msg("pt"), Lang.msg("en")});
	private Mesa11Applet mesa11Applet;
	private JTextField nomeLogar = new JTextField(20);
	private JTextField nomeRegistrar = new JTextField(20);
	private JTextField emailRegistrar = new JTextField(20);
	private JPasswordField senha = new JPasswordField(20);
	private int conta1;
	private int conta2;
	private JTextField resultadorConta = new JTextField(20);

	private JLabel senhaLabel = new JLabel("Senha") {
		public String getText() {
			return Lang.msg("senha");
		}
	};

	private JLabel recuperarLabel = new JLabel("Recuperar Senha") {
		public String getText() {
			return Lang.msg("recuperarSenha");
		}
	};

	private JLabel emailLabel = new JLabel("Entre com seu e-mail") {
		public String getText() {
			return Lang.msg("entreEmail");
		}
	};

	public FormLogin(Mesa11Applet mesa11Applet) {
		this.mesa11Applet = mesa11Applet;

		setLayout(new BorderLayout());
		JTabbedPane jTabbedPane = new JTabbedPane();
		JPanel panelAba1 = new JPanel(new BorderLayout(15, 15));
		JPanel sulaba1 = new JPanel(new BorderLayout());
		sulaba1.add(gerarLogin(), BorderLayout.CENTER);
		panelAba1.add(sulaba1, BorderLayout.CENTER);
		jTabbedPane.addTab(Lang.msg("entrar"), panelAba1);
		JPanel panelAba2 = new JPanel(new BorderLayout());
		panelAba2.add(gerarRegistrar(), BorderLayout.CENTER);
		jTabbedPane.addTab(Lang.msg("registrar"), panelAba2);
		add(jTabbedPane, BorderLayout.CENTER);
		add(gerarIdiomas(), BorderLayout.SOUTH);
		setSize(300, 300);
		setVisible(true);
	}

	private JPanel gerarIdiomas() {
		comboIdiomas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Logger.logar(
						Lang.key(comboIdiomas.getSelectedItem().toString()));
				String i = Lang.key(comboIdiomas.getSelectedItem().toString());
				if (i != null && !"".equals(i)) {
					Lang.mudarIdioma(i);
					comboIdiomas.removeAllItems();
					comboIdiomas.addItem(Lang.msg("pt"));
					comboIdiomas.addItem(Lang.msg("en"));
				}
				FormLogin.this.repaint();
				comboIdiomas.setSelectedItem(Lang.msg(i));
			}
		});
		JPanel langPanel = new JPanel(new BorderLayout());
		langPanel.setBorder(new TitledBorder("Idiomas") {
			public String getTitle() {
				return Lang.msg("idiomas");
			}
		});
		langPanel.add(comboIdiomas, BorderLayout.CENTER);

		return langPanel;
	}

	private JPanel gerarRegistrar() {
		JPanel registrarPanel = new JPanel(new GridLayout(6, 2));
		registrarPanel.setBorder(new TitledBorder("Registrar") {
			public String getTitle() {
				return Lang.msg("registrar");
			}
		});
		registrarPanel.add(new JLabel("Entre com seu Nome") {
			public String getText() {
				return Lang.msg("nome");
			}
		});
		registrarPanel.add(nomeRegistrar);
		registrarPanel.add(new JLabel("Entre com seu e-mail") {
			public String getText() {
				return Lang.msg("email");
			}
		});
		registrarPanel.add(emailRegistrar);
		conta1 = Util.intervalo(0, 10);
		conta2 = Util.intervalo(0, 10);
		registrarPanel.add(new JLabel("Conta Facil") {
			public String getText() {
				return Lang.msg("contaFacil",
						new String[]{"" + conta1, "" + conta2});
			}
		});
		registrarPanel.add(resultadorConta);
		JPanel newPanel = new JPanel(new BorderLayout());
		newPanel.add(registrarPanel, BorderLayout.NORTH);
		return newPanel;
	}

	private JPanel gerarLogin() {
		JPanel panel = new JPanel();
		GridLayout gridLayout = new GridLayout(4, 1);
		panel.setBorder(new TitledBorder("Entrar") {
			@Override
			public String getTitle() {
				return Lang.msg("entrar");
			}
		});
		panel.setLayout(gridLayout);
		panel.add(new JLabel("Entre com seu Nome") {
			public String getText() {
				return Lang.msg("nome");
			}
		});
		panel.add(nomeLogar);
		panel.add(senhaLabel);
		panel.add(senha);
		return panel;
	}

	public JTextField getNome() {
		if (!Util.isNullOrEmpty(nomeRegistrar.getText()))
			return nomeRegistrar;
		return nomeLogar;
	}

	public void setNome(JTextField nome) {
		this.nomeLogar = nome;
	}

	public JPasswordField getSenha() {
		return senha;
	}

	public static void main(String[] args) throws FileNotFoundException {
		// FileOutputStream fileOutputStream = new
		// FileOutputStream("teste.xml");
		// XMLEncoder encoder = new XMLEncoder(fileOutputStream);
		// String teste = "HandlerFactory";
		// encoder.writeObject(teste);
		// encoder.flush();
		// encoder.close();
		FormLogin formEntrada = new FormLogin(null);
		formEntrada.setToolTipText(Lang.msg("formularioLogin"));
		int result = JOptionPane.showConfirmDialog(null, formEntrada,
				Lang.msg("formularioLogin"), JOptionPane.OK_CANCEL_OPTION);

		if (JOptionPane.OK_OPTION == result) {
			Logger.logar("ok");
		}
	}

	public JTextField getNomeRegistrar() {
		return nomeRegistrar;
	}

	public JTextField getEmailRegistrar() {
		return emailRegistrar;
	}

	public JTextField getResultadorConta() {
		return resultadorConta;
	}

	public int getConta1() {
		return conta1;
	}

	public int getConta2() {
		return conta2;
	}

}
