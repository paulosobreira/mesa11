package br.mesa11.servidor;

import java.awt.BorderLayout;

import javax.swing.JOptionPane;

import br.applet.Mesa11Applet;
import br.mesa11.ConstantesMesa11;
import br.mesa11.cliente.ChatWindow;
import br.mesa11.cliente.FormLogin;
import br.nnpe.Logger;
import br.nnpe.Util;
import br.recursos.Lang;
import br.tos.ClienteMesa11;
import br.tos.DadosMesa11;
import br.tos.Mesa11TO;
import br.tos.SessaoCliente;

public class ControleChatCliente {
	private FormLogin formLogin;
	private ChatWindow chatWindow;
	private Mesa11Applet mesa11Applet;
	private SessaoCliente sessaoCliente;
	private Thread threadAtualizadora;
	private boolean comunicacaoServer = true;

	public ControleChatCliente(Mesa11Applet mesa11Applet) {
		this.mesa11Applet = mesa11Applet;
		threadAtualizadora = new Thread(new Runnable() {

			public void run() {
				try {
					while (comunicacaoServer) {
						Thread.sleep((5000 + ((int) Math.random() * 1000)));
						atualizaVisao();
					}
				} catch (Exception e) {
					Logger.logarExept(e);
				}

			}

		});
		threadAtualizadora.setPriority(Thread.MIN_PRIORITY);
		chatWindow = new ChatWindow(this);
		atualizaVisao();
		mesa11Applet.setLayout(new BorderLayout());
		mesa11Applet.add(chatWindow.getMainPanel(), BorderLayout.CENTER);
		threadAtualizadora.start();
	}

	public void logar() {
		formLogin = new FormLogin(mesa11Applet);
		formLogin.setToolTipText(Lang.msg("formularioLogin"));
		int result = JOptionPane.showConfirmDialog(chatWindow.getMainPanel(),
				formLogin, Lang.msg("formularioLogin"),
				JOptionPane.OK_CANCEL_OPTION);

		if (JOptionPane.OK_OPTION == result) {
			registrarUsuario();
			atualizaVisao();
		}

	}

	private void atualizaVisao() {
		if (chatWindow == null) {
			return;
		}
		Mesa11TO mesa11to = new Mesa11TO();
		mesa11to.setComando(ConstantesMesa11.ATUALIZAR_VISAO);
		Object ret = enviarObjeto(mesa11to);
		if (ret == null) {
			return;
		}
		mesa11to = (Mesa11TO) ret;
		chatWindow.atualizar((DadosMesa11) mesa11to.getData());

	}

	private Object enviarObjeto(Mesa11TO mesa11to) {
		if (mesa11Applet == null) {
			Logger.logar("enviarObjeto mesa11Applet null");
			return null;
		}
		return mesa11Applet.enviarObjeto(mesa11to);
	}

	private boolean registrarUsuario() {
		Mesa11TO mesa11to = new Mesa11TO();
		ClienteMesa11 clienteMesa11 = new ClienteMesa11();
		mesa11to.setData(clienteMesa11);
		clienteMesa11.setNomeJogador(formLogin.getNome().getText());

		try {
			if (!Util.isNullOrEmpty(new String(formLogin.getSenha()
					.getPassword()))) {
				clienteMesa11.setSenhaJogador(Util.md5(new String(formLogin
						.getSenha().getPassword())));
			}
		} catch (Exception e) {
			Logger.logarExept(e);
			JOptionPane.showMessageDialog(chatWindow.getMainPanel(), e
					.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
		}
		clienteMesa11.setEmailJogador(formLogin.getEmail().getText());
		clienteMesa11.setRecuperar(formLogin.getRecuperar().isSelected());

		if (formLogin.getJoe().isSelected()) {
			mesa11to.setComando(ConstantesMesa11.LOGAR_VISITANTE);
		} else if (!Util.isNullOrEmpty(clienteMesa11.getNomeJogador())
				&& !Util.isNullOrEmpty(clienteMesa11.getSenhaJogador())) {
			mesa11to.setComando(ConstantesMesa11.LOGAR);
		} else if (!Util.isNullOrEmpty(clienteMesa11.getNomeJogador())
				&& !Util.isNullOrEmpty(clienteMesa11.getEmailJogador())
				&& !clienteMesa11.isRecuperar()) {
			mesa11to.setComando(ConstantesMesa11.LOGAR);
		} else if (clienteMesa11.isRecuperar()) {
			mesa11to.setComando(ConstantesMesa11.RECUPERA_SENHA);
		}
		Logger.logar("registrarUsuario mesa11to.getComando() "
				+ mesa11to.getComando());
		Object ret = mesa11Applet.enviarObjeto(mesa11to);
		if (ret == null) {
			return false;
		}
		mesa11to = (Mesa11TO) ret;
		SessaoCliente cliente = (SessaoCliente) mesa11to.getData();
		this.sessaoCliente = cliente;
		return true;
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
