package br.applet;

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

import br.mesa11.ConstantesMesa11;
import br.mesa11.MainFrame;
import br.recursos.Lang;

public class FormLogin {

	private Mesa11Applet mesa11Applet;

	// private Usuario usuario;

	public FormLogin(Mesa11Applet algolApplet) {
		this.mesa11Applet = algolApplet;
		gerarMenuLogin();
		if (ConstantesMesa11.debug) {
			logarAdmin();
		}
		// algolApplet.play("ps1fm-cave.mid");
	}

	private void logarAdmin() {
		// Usuario usuario = new Usuario();
		// usuario.setLogin("admin");
		// AlgolTO algolTO = new AlgolTO();
		// algolTO.setComando(ConstantesAlgol.LOGAR);
		// algolTO.setData(usuario);
		// usuario = (Usuario) algolApplet.enviarObjeto(algolTO);
		// if (usuario != null) {
		// FormLogin.this.usuario = usuario;
		// MainFrame mainFrame = new MainFrame(algolApplet,
		// FormLogin.this.usuario);
		// mainFrame.setVisible(true);
		// }

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
				// Usuario usuario = new Usuario();
				// usuario.setLogin(login.getText());
				// AlgolTO algolTO = new AlgolTO();
				// algolTO.setComando(ConstantesAlgol.LOGAR);
				// algolTO.setData(usuario);
				// usuario = (Usuario) algolApplet.enviarObjeto(algolTO);
				// if (usuario != null) {
				// FormLogin.this.usuario = usuario;
				// MainFrame mainFrame = new MainFrame(algolApplet,
				// FormLogin.this.usuario);
				// mainFrame.setVisible(true);
				// }

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
				// Usuario usuario = new Usuario();
				// usuario.setLogin(login.getText());
				// usuario.setEmail(email.getText());
				// AlgolTO algolTO = new AlgolTO();
				// algolTO.setComando(ConstantesAlgol.NOVO_USUARIO);
				// algolTO.setData(usuario);
				// usuario = (Usuario) algolApplet.enviarObjeto(algolTO);
				// if (usuario != null) {
				// FormLogin.this.usuario = usuario;
				// JOptionPane.showMessageDialog(algolApplet, Lang
				// .msg("usuarioCadastrado"));
				// MainFrame mainFrame = new MainFrame(algolApplet,
				// FormLogin.this.usuario);
				// mainFrame.setVisible(true);
				// }
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
		mesa11Applet.getContentPane().add(jPanel, BorderLayout.CENTER);
	}

	public Mesa11Applet getAlgolApplet() {
		return mesa11Applet;
	}

	public void setAlgolApplet(Mesa11Applet algolApplet) {
		this.mesa11Applet = algolApplet;
	}

}
