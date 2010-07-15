package br.mesa11.servidor;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import br.hibernate.HibernateUtil;
import br.hibernate.Usuario;
import br.mesa11.ConstantesMesa11;
import br.nnpe.Logger;
import br.nnpe.PassGenerator;
import br.nnpe.Util;
import br.recursos.Lang;
import br.tos.ClienteMesa11;
import br.tos.DadosMesa11;
import br.tos.ErroServ;
import br.tos.Mesa11TO;
import br.tos.MsgSrv;
import br.tos.SessaoCliente;

import com.octo.captcha.service.image.DefaultManageableImageCaptchaService;

public class ControleLogin {
	private DefaultManageableImageCaptchaService capcha = new DefaultManageableImageCaptchaService();
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
		try {
			Boolean validateResponseForID = capcha.validateResponseForID(
					clienteMesa11.getChaveCapcha(), clienteMesa11.getTexto());
			if (!validateResponseForID) {
				return new MsgSrv(Lang.msg("capchaInvalido"));
			}
		} catch (Exception e) {
			Logger.logarExept(e);
			return new MsgSrv(Lang.msg("capchaInvalido"));
		}

		Session session = HibernateUtil.getSessionFactory().openSession();
		List usuarios = session.createCriteria(Usuario.class).add(
				Restrictions.eq("login", clienteMesa11.getNomeJogador()))
				.list();
		usuario = (Usuario) (usuarios.isEmpty() ? null : usuarios.get(0));
		if (usuario != null) {
			return new MsgSrv(Lang.msg("loginNaoDisponivel"));
		}
		usuario = new Usuario();
		usuario.setLogin(clienteMesa11.getNomeJogador());
		usuario.setLoginCriador(clienteMesa11.getNomeJogador());
		usuario.setEmail(clienteMesa11.getEmailJogador());
		try {

			geraSenhaMandaMail(usuario);
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
		return criarSessao(usuario);
	}

	private Object criarSessao(Usuario usuario) {
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

		Mesa11TO mesa11to = new Mesa11TO();
		mesa11to.setData(sessaoCliente);
		return mesa11to;
	}

	private void geraSenhaMandaMail(Usuario usuario)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		PassGenerator generator = new PassGenerator();
		String senha = generator.generateIt();

		Logger.logar("geraSenhaMandaMail " + usuario + " senha " + senha);
		usuario.setSenha(Util.md5(senha));
	}

	public Object logar(ClienteMesa11 clienteMesa11) {
		Usuario usuario = new Usuario();
		Session session = HibernateUtil.getSessionFactory().openSession();
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
	}

	public Object novoCapcha() {
		try {
			ByteArrayOutputStream jpegstream = new ByteArrayOutputStream();
			String chave = String.valueOf(System.currentTimeMillis());
			BufferedImage challenge = capcha.getImageChallengeForID(chave);
			ImageIO.write(challenge, "jpg", jpegstream);
			Mesa11TO mesa11to = new Mesa11TO();
			mesa11to.setComando(ConstantesMesa11.NOVO_CAPCHA);
			mesa11to.setData(chave);
			mesa11to.setDataBytes(jpegstream.toByteArray());
			return mesa11to;
		} catch (Exception e) {
			Logger.logarExept(e);
		}
		return new ErroServ(Lang.msg("erroCapcha"));
	}

	public Object recuperaSenha(ClienteMesa11 clienteMesa11) {
		Boolean validateResponseForID = capcha.validateResponseForID(
				clienteMesa11.getChaveCapcha(), clienteMesa11.getTexto());
		if (!validateResponseForID) {
			return new MsgSrv(Lang.msg("capchaInvalido"));
		}
		Usuario usuario = new Usuario();
		Session session = HibernateUtil.getSessionFactory().openSession();
		List usuarios = session.createCriteria(Usuario.class).add(
				Restrictions.eq("login", clienteMesa11.getNomeJogador()))
				.list();
		usuario = (Usuario) (usuarios.isEmpty() ? null : usuarios.get(0));
		if (usuario == null) {
			return new MsgSrv(Lang.msg("usuarioNaoEncontrado"));
		}
		if ((System.currentTimeMillis() - usuario.getUltimaRecuperacao()) < 300000) {
			return new MsgSrv(Lang.msg("limiteTempo"));
		}
		try {
			geraSenhaMandaMail(usuario);
		} catch (Exception e) {
			return new ErroServ(e);
		}
		Transaction transaction = session.beginTransaction();
		session.saveOrUpdate(usuario);
		transaction.commit();
		return new MsgSrv(Lang.msg("senhaEnviada", new String[] { usuario
				.getEmail() }));
	}
}
