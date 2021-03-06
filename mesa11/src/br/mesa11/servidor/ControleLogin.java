package br.mesa11.servidor;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import br.hibernate.Usuario;
import br.nnpe.HibernateUtil;
import br.nnpe.Logger;
import br.nnpe.PassGenerator;
import br.nnpe.Util;
import br.nnpe.tos.ErroServ;
import br.nnpe.tos.MsgSrv;
import br.nnpe.tos.NnpeTO;
import br.nnpe.tos.SessaoCliente;
import br.recursos.Lang;
import br.servlet.ServletMesa11;
import br.tos.ClienteMesa11;
import br.tos.DadosMesa11;

public class ControleLogin {
	private DadosMesa11 dadosMesa11;

	public ControleLogin(DadosMesa11 dadosMesa11) {
		this.dadosMesa11 = dadosMesa11;
	}

	public DadosMesa11 getDadosMesa11() {
		return dadosMesa11;
	}

	public void setDadosMesa11(DadosMesa11 dadosMesa11) {
		this.dadosMesa11 = dadosMesa11;
	}

	public Object cadastrarUsuario(ClienteMesa11 clienteMesa11) {

		Usuario usuario = null;
		Session session = ControlePersistencia.getSession();
		List usuarios = session.createCriteria(Usuario.class)
				.add(Restrictions.eq("login", clienteMesa11.getNomeJogador()))
				.list();
		usuario = (Usuario) (usuarios.isEmpty() ? null : usuarios.get(0));
		if (usuario != null) {
			return new MsgSrv(Lang.msg("loginNaoDisponivel"));
		}
		usuarios = session.createCriteria(Usuario.class)
				.add(Restrictions.eq("email", clienteMesa11.getEmailJogador()))
				.list();
		usuario = (Usuario) (usuarios.isEmpty() ? null : usuarios.get(0));
		if (usuario != null) {
			return new MsgSrv(Lang.msg("emailNaoDisponivel"));
		}

		usuario = new Usuario();
		usuario.setLogin(clienteMesa11.getNomeJogador());
		usuario.setLoginCriador(clienteMesa11.getNomeJogador());
		usuario.setEmail(clienteMesa11.getEmailJogador());
		String senha = null;
		try {
			senha = geraSenhaMandaMail(usuario);
		} catch (Exception e) {
			return new ErroServ(e);
		}

		Transaction transaction = session.beginTransaction();
		try {
			if (Util.isNullOrEmpty(usuario.getLoginCriador())) {
				usuario.setLoginCriador(usuario.getLogin());
			}
			session.saveOrUpdate(usuario);
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			return new ErroServ(e.getMessage());
		}
		Logger.logar("cadastrarUsuario " + usuario);
		return criarSessao(usuario, senha);
	}

	private Object criarSessao(Usuario usuario) {
		return criarSessao(usuario, null);
	}

	private Object criarSessao(Usuario usuario, String senha) {
		SessaoCliente sessaoCliente = null;
		Collection clientes = dadosMesa11.getClientes();
		for (Iterator iterator = clientes.iterator(); iterator.hasNext();) {
			SessaoCliente object = (SessaoCliente) iterator.next();
			if (object.getNomeJogador().equals(usuario.getLogin())) {
				sessaoCliente = object;
				break;
			}
		}
		if (sessaoCliente == null) {
			sessaoCliente = new SessaoCliente();
			sessaoCliente.setNomeJogador(usuario.getLogin());
			dadosMesa11.getClientes().add(sessaoCliente);
		}

		sessaoCliente.setUlimaAtividade(System.currentTimeMillis());
		sessaoCliente.setSenhaCriada(senha);
		NnpeTO mesa11to = new NnpeTO();
		mesa11to.setData(sessaoCliente);
		return mesa11to;
	}

	private String geraSenhaMandaMail(Usuario usuario) throws Exception {
		PassGenerator generator = new PassGenerator();
		String senha = generator.generateIt();
		Logger.logar("geraSenhaMandaMail " + usuario + " senha " + senha);
		usuario.setSenha(Util.md5(senha));
		try {
			mandaMailSenha(usuario.getLogin(), usuario.getEmail(), senha);
		} catch (Exception e1) {
			Logger.logarExept(e1);
		}
		return senha;
	}

	private void mandaMailSenha(String nome, String email, String senha)
			throws AddressException, MessagingException {
		Logger.logar("Senha :" + senha);
	}

	public Object logar(ClienteMesa11 clienteMesa11) {
		Usuario usuario = new Usuario();
		Session session = ControlePersistencia.getSession();
		try {
			List usuarios = session.createCriteria(Usuario.class).add(
					Restrictions.eq("login", clienteMesa11.getNomeJogador()))
					.list();
			usuario = (Usuario) (usuarios.isEmpty() ? null : usuarios.get(0));
			if (usuario == null) {
				return new MsgSrv(Lang.msg("usuarioNaoEncontrado"));
			}
			if (!usuario.getSenha().equals(clienteMesa11.getSenhaJogador())) {
				return new MsgSrv(Lang.msg("senhaIncorreta"));
			}
			return criarSessao(usuario);
		} finally {
			HibernateUtil.closeSession();
		}

	}
	public Object recuperaSenha(ClienteMesa11 clienteMesa11) {
		Usuario usuario = new Usuario();
		Session session = ControlePersistencia.getSession();
		List usuarios = session.createCriteria(Usuario.class)
				.add(Restrictions.eq("login", clienteMesa11.getNomeJogador()))
				.list();
		usuario = (Usuario) (usuarios.isEmpty() ? null : usuarios.get(0));
		if (usuario == null) {
			usuarios = session.createCriteria(Usuario.class).add(
					Restrictions.eq("email", clienteMesa11.getEmailJogador()))
					.list();
			usuario = (Usuario) (usuarios.isEmpty() ? null : usuarios.get(0));
		}
		if (usuario == null) {
			return new MsgSrv(Lang.msg("usuarioNaoEncontrado"));
		}
		if ((System.currentTimeMillis()
				- usuario.getUltimaRecuperacao()) < 300000) {
			return new MsgSrv(Lang.msg("limiteTempo"));
		}
		try {
			geraSenhaMandaMail(usuario);
		} catch (Exception e) {
			return new ErroServ(e);
		}
		String email = usuario.getEmail();
		Transaction transaction = session.beginTransaction();
		try {
			session.saveOrUpdate(usuario);
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			return new ErroServ(e);
		}
		return new MsgSrv(Lang.msg("senhaEnviada", new String[]{email}));
	}

	public boolean verificaSemSessao(String nomeCriador) {
		if (Util.isNullOrEmpty(nomeCriador)) {
			return true;
		}
		Collection<SessaoCliente> clientes = dadosMesa11.getClientes();
		for (Iterator iter = clientes.iterator(); iter.hasNext();) {
			SessaoCliente sessaoCliente = (SessaoCliente) iter.next();
			if (nomeCriador.equals(sessaoCliente.getNomeJogador())) {
				return false;
			}
		}
		return true;

	}

}
