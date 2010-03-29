package br.mesa11.cliente;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import br.applet.Mesa11Applet;
import br.hibernate.Usuario;
import br.mesa11.ConstantesMesa11;
import br.mesa11.MainFrame;
import br.recursos.Lang;
import br.tos.Mesa11TO;

public class FormLogin {

	private Mesa11Applet mesa11Applet;

	private JPanel panel;

	private Usuario usuario;

	public FormLogin(Mesa11Applet mesa11Applet) {
		this.mesa11Applet = mesa11Applet;
		panel = new JPanel();
		gerarMenuLogin();
		if (ConstantesMesa11.debug) {
			logarAdmin();
		}
	}

	private void logarAdmin() {
		Usuario usuario = new Usuario();
		usuario.setLogin("admin");
		Mesa11TO algolTO = new Mesa11TO();
		algolTO.setComando(ConstantesMesa11.LOGAR);
		algolTO.setData(usuario);
		usuario = (Usuario) mesa11Applet.enviarObjeto(algolTO);
		if (usuario != null) {
			FormLogin.this.usuario = usuario;
			MainFrame mainFrame = new MainFrame(mesa11Applet,
					FormLogin.this.usuario);
			mainFrame.setVisible(true);
		}

	}

	private void gerarMenuLogin() {

		GridLayout gridLayout = new GridLayout(9, 1);
		JPanel jPanel = new JPanel(gridLayout);
		jPanel.setBorder(new TitledBorder(""));
		jPanel.add(new JLabel() {
			public String getText() {
				return Lang.msg("login");
			}
		});
		final JTextField login = new JTextField();
		jPanel.add(login);
		jPanel.add(new JLabel() {
			public String getText() {
				return Lang.msg("senha");
			}
		});
		JPasswordField senha = new JPasswordField(20);
		jPanel.add(senha);
		jPanel.add(new JLabel() {
			public String getText() {
				return Lang.msg("email");
			}
		});
		final JTextField email = new JTextField(40);
		jPanel.add(email);
		JButton logar = new JButton() {
			public String getText() {
				return Lang.msg("logar");
			}
		};
		logar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Usuario usuario = new Usuario();
				usuario.setLogin(login.getText());
				Mesa11TO mesa11TO = new Mesa11TO();
				mesa11TO.setComando(ConstantesMesa11.LOGAR);
				mesa11TO.setData(usuario);
				usuario = (Usuario) mesa11Applet.enviarObjeto(mesa11TO);
				if (usuario != null) {
					FormLogin.this.usuario = usuario;
					MainFrame mainFrame = new MainFrame(mesa11Applet,
							FormLogin.this.usuario);
					mainFrame.setVisible(true);
				}

			}
		});
		jPanel.add(logar);
		JButton novoUsuario = new JButton() {
			public String getText() {
				return Lang.msg("novoUsuario");
			}
		};
		novoUsuario.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Usuario usuario = new Usuario();
				usuario.setLogin(login.getText());
				usuario.setEmail(email.getText());
				Mesa11TO mesa11TO = new Mesa11TO();
				mesa11TO.setComando(ConstantesMesa11.NOVO_USUARIO);
				mesa11TO.setData(usuario);
				usuario = (Usuario) mesa11Applet.enviarObjeto(mesa11TO);
				if (usuario != null) {
					FormLogin.this.usuario = usuario;
					JOptionPane.showMessageDialog(mesa11Applet, Lang
							.msg("usuarioCadastrado"));
					MainFrame mainFrame = new MainFrame(mesa11Applet,
							FormLogin.this.usuario);
					mainFrame.setVisible(true);
				}
			}
		});
		jPanel.add(novoUsuario);
		JButton som = new JButton("Som");
		som.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				mesa11Applet.pauseMusic();
			}
		});
		jPanel.add(som);
		panel.add(jPanel, BorderLayout.CENTER);
	}

	public JPanel getPanel() {
		return panel;
	}

}
