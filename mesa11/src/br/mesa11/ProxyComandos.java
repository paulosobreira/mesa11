package br.mesa11;

import br.hibernate.Usuario;
import br.mesa11.servidor.login.ControleLogin;

public class ProxyComandos {
	private ControleLogin controleLogin;

	public ProxyComandos() {
		controleLogin = new ControleLogin();
	}

	public Object processarObjeto(Object object) {
		Mesa11TO mesa11TO = (Mesa11TO) object;
		if (ConstantesMesa11.LOGAR.equals(mesa11TO.getComando())) {
			return controleLogin.logar((Usuario) mesa11TO.getData());
		} else if (ConstantesMesa11.NOVO_USUARIO.equals(mesa11TO.getComando())) {
			return controleLogin.cadastratUsuario((Usuario) mesa11TO.getData());
		}
		return null;
	}
}
