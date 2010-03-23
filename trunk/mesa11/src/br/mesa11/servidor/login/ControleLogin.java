package br.mesa11.servidor.login;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import br.applet.ErroServ;
import br.hibernate.HibernateUtil;
import br.hibernate.Usuario;
import br.nnpe.Util;

public class ControleLogin {

	public ControleLogin() {
	}

	public Object cadastratUsuario(Usuario usuario) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			Transaction transaction = session.beginTransaction();
			if (Util.isNullOrEmpty(usuario.getLoginCriador())) {
				usuario.setLoginCriador(usuario.getLogin());
			}
			session.saveOrUpdate(usuario);
			transaction.commit();
		} catch (Exception e) {
			return new ErroServ(e.getMessage());
		}
		return usuario;
	}

	public Object logar(Usuario usuario) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List usuarios = session.createCriteria(Usuario.class).add(
				Restrictions.eq("login", usuario.getLogin())).list();
		return usuarios.isEmpty() ? null : usuarios.get(0);
	}
}
