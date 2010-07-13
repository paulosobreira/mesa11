package br.mesa11.servidor;

import java.awt.BorderLayout;

import javax.swing.JOptionPane;

import br.applet.Mesa11Applet;
import br.mesa11.cliente.ChatWindow;
import br.mesa11.cliente.FormLogin;
import br.recursos.Lang;

public class ControleChatCliente {
	private FormLogin formLogin;

	private ChatWindow chatWindow;
	private Mesa11Applet mesa11Applet;

	public ControleChatCliente(Mesa11Applet mesa11Applet) {
		this.mesa11Applet = mesa11Applet;
		chatWindow = new ChatWindow(this);
		mesa11Applet.setLayout(new BorderLayout());
		// mesa11Applet.add(chatWindow.getMainPanel(), BorderLayout.CENTER);
	}

	public void logar() {
		formLogin = new FormLogin(mesa11Applet);
		formLogin.setToolTipText(Lang.msg("formularioLogin"));
		int result = JOptionPane.showConfirmDialog(chatWindow.getMainPanel(),
				formLogin, Lang.msg("formularioLogin"),
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

	public int getLatenciaMinima() {
		return mesa11Applet.getLatenciaMinima();
	}

	public int getLatenciaReal() {
		return mesa11Applet.getLatenciaReal();
	}

}
