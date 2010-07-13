package br.mesa11.servidor;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import com.octo.captcha.service.image.DefaultManageableImageCaptchaService;
import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import br.hibernate.HibernateUtil;
import br.hibernate.Usuario;
import br.mesa11.ConstantesMesa11;
import br.nnpe.Logger;
import br.nnpe.Util;
import br.recursos.Lang;
import br.tos.ErroServ;
import br.tos.Mesa11TO;

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

	public Object novoCapcha() {
		try {
			ByteArrayOutputStream jpegstream = new ByteArrayOutputStream();
			DefaultManageableImageCaptchaService defaultManageableImageCaptchaService = new DefaultManageableImageCaptchaService();
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
}
