package br.mesa11.cliente;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
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
import br.mesa11.ConstantesMesa11;
import br.nnpe.Logger;
import br.nnpe.Util;
import br.nnpe.tos.NnpeTO;
import br.recursos.Lang;

public class FormLogin extends JPanel {
	private JComboBox comboIdiomas = new JComboBox(new String[] {
			Lang.msg("pt"), Lang.msg("en") });
	private JTextField nomeLogar = new JTextField(20);
	private JTextField nomeRegistrar = new JTextField(20);
	private Mesa11Applet mesa11Applet;
	private JTextField email = new JTextField(20);
	private JCheckBox recuperar = new JCheckBox();

	private JLabel senhaLabel = new JLabel("Senha") {
		public String getText() {
			return Lang.msg("senha");
		}
	};
	private JPasswordField senha = new JPasswordField(20);

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

	public JCheckBox getLembrar() {
		return lembrar;
	}

	public void setLembrar(JCheckBox lembrar) {
		this.lembrar = lembrar;
	}

	public FormLogin(Mesa11Applet mesa11Applet) {
		this.mesa11Applet = mesa11Applet;

		setLayout(new BorderLayout());
		JTabbedPane jTabbedPane = new JTabbedPane();
		JPanel panelAba1 = new JPanel(new BorderLayout(15, 15));
		JPanel sulaba1 = new JPanel(new BorderLayout());
		sulaba1.add(gerarLogin(), BorderLayout.CENTER);
		sulaba1.add(gerarLembrar(), BorderLayout.SOUTH);
		panelAba1.add(sulaba1, BorderLayout.CENTER);
		panelAba1.add(gerarIdiomas(), BorderLayout.SOUTH);
		jTabbedPane.addTab(Lang.msg("entrar"), panelAba1);
		JPanel panelAba2 = new JPanel(new BorderLayout());
		panelAba2.add(gerarRegistrar(), BorderLayout.CENTER);
		jTabbedPane.addTab(Lang.msg("registrar"), panelAba2);
		add(jTabbedPane, BorderLayout.CENTER);
		setSize(300, 300);
		setVisible(true);
	}

	private JCheckBox lembrar = new JCheckBox();

	private Component gerarLembrar() {
		lembrar = new JCheckBox();
		JPanel langPanel = new JPanel();
		langPanel.add(lembrar);
		langPanel.add(new JLabel() {
			@Override
			public String getText() {
				// TODO Auto-generated method stub
				return Lang.msg("lembrar");
			}
		});
		return langPanel;
	}

	private JPanel gerarIdiomas() {
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
				FormLogin.this.repaint();
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

	public JCheckBox getRecuperar() {
		return recuperar;
	}

	private JPanel gerarRegistrar() {
		JPanel registrarPanel = new JPanel(new GridLayout(5, 2));
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
		registrarPanel.add(emailLabel);
		registrarPanel.add(email);
		JPanel recuperarPanel = new JPanel();
		recuperarPanel.add(recuperarLabel);
		recuperarPanel.add(recuperar);
		registrarPanel.add(recuperarPanel);

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
		int result = JOptionPane.showConfirmDialog(null, formEntrada, Lang
				.msg("formularioLogin"), JOptionPane.OK_CANCEL_OPTION);

		if (JOptionPane.OK_OPTION == result) {
			Logger.logar("ok");
		}
	}

	public JTextField getEmail() {
		return email;
	}


}
