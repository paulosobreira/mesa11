package br.mesa11.cliente;

import java.awt.BorderLayout;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import br.applet.ChatWindow;
import br.applet.FormLogin;
import br.applet.Mesa11Applet;
import br.recursos.Lang;

public class ControleChatCliente {
	private FormLogin formLogin;

	private ChatWindow chatWindow;
	private Mesa11Applet mesa11Applet;

	public ControleChatCliente(Mesa11Applet mesa11Applet) {
		this.mesa11Applet = mesa11Applet;
		chatWindow = new ChatWindow(this);

		mesa11Applet.add(chatWindow.getMainPanel(), BorderLayout.CENTER);
		logar();
	}

	public void logar() {
		formLogin = new FormLogin(mesa11Applet);
		// formLogin.setToolTipText(Lang.msg("066"));
		int result = JOptionPane.showConfirmDialog(chatWindow.getMainPanel(),
				formLogin.getPanel(), Lang.msg("066"),
				JOptionPane.OK_CANCEL_OPTION);

		if (JOptionPane.OK_OPTION == result) {
			// registrarUsuario(formEntrada);
			// atualizaVisao(paddockWindow);
		}

	}

	public void setChatWindow(ChatWindow chatWindow) {
		// TODO Auto-generated method stub

	}

	public void enviarTexto(String text) {
		// TODO Auto-generated method stub

	}

	public void criarJogo(String string) {
		// TODO Auto-generated method stub

	}

	public void entarJogo(Object object) {
		// TODO Auto-generated method stub

	}

	public void verDetalhesJogo(Object object) {
		// TODO Auto-generated method stub

	}

	public void verDetalhesJogador(Object object) {
		// TODO Auto-generated method stub

	}

	public void iniciarJogo() {
		// TODO Auto-generated method stub

	}

	public void verClassificacao() {
		// TODO Auto-generated method stub

	}

	public void verConstrutores() {
		// TODO Auto-generated method stub

	}

	public void modoCarreira() {
		// TODO Auto-generated method stub

	}

	public String getLatenciaMinima() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getLatenciaReal() {
		// TODO Auto-generated method stub
		return null;
	}

}
