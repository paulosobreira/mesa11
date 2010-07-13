package br.mesa11.servidor;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import br.hibernate.HibernateUtil;
import br.hibernate.Usuario;
import br.mesa11.ConstantesMesa11;
import br.nnpe.Logger;
import br.nnpe.Util;
import br.recursos.Lang;
import br.tos.ClienteMesa11;
import br.tos.DadosMesa11;
import br.tos.ErroServ;
import br.tos.Mesa11TO;
import br.tos.SessaoCliente;

import com.octo.captcha.service.image.DefaultManageableImageCaptchaService;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class ControleLogin {
	private DefaultManageableImageCaptchaService defaultManageableImageCaptchaService = new DefaultManageableImageCaptchaService();
	private int visitante;
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

	public Object cadastratUsuario(ClienteMesa11 clienteMesa11) {
		Usuario usuario = new Usuario();
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

	public Object logar(ClienteMesa11 clienteMesa11) {
		Usuario usuario = new Usuario();
		Session session = HibernateUtil.getSessionFactory().openSession();
		List usuarios = session.createCriteria(Usuario.class).add(
				Restrictions.eq("login", usuario.getLogin())).list();
		usuario = (Usuario) (usuarios.isEmpty() ? null : usuarios.get(0));
		return null;
	}

	public Object novoCapcha() {
		try {
			ByteArrayOutputStream jpegstream = new ByteArrayOutputStream();
			String chave = String.valueOf(System.currentTimeMillis());
			BufferedImage challenge = defaultManageableImageCaptchaService
					.getImageChallengeForID(chave);
			JPEGImageEncoder jpegencoderEncoder = JPEGCodec
					.createJPEGEncoder(jpegstream);
			jpegencoderEncoder.encode(challenge);
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

	public Object recuperaSenha(ClienteMesa11 data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object logarVisitante(ClienteMesa11 data) {
		SessaoCliente sessaoCliente = new SessaoCliente();
		sessaoCliente.setNomeJogador("Joe" + visitante++);
		sessaoCliente.setUlimaAtividade(System.currentTimeMillis());
		dadosMesa11.getClientes().add(sessaoCliente);
		Mesa11TO mesa11to = new Mesa11TO();
		mesa11to.setData(sessaoCliente);
		return mesa11to;
	}
}
