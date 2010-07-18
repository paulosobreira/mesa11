package br.mesa11;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;

import br.hibernate.Botao;
import br.hibernate.Goleiro;
import br.hibernate.Time;
import br.nnpe.BeanUtil;
import br.nnpe.Logger;
import br.nnpe.Util;

public class BotaoUtils {
	public static BufferedImage desenhaUniformeGoleiro(Time time, int uniforme) {
		return desenhaUniformeGoleiro(time, uniforme, null);
	}

	public static BufferedImage desenhaUniformeGoleiro(Time time, int uniforme,
			Goleiro botao) {
		Color cor1, cor2, cor3;
		if (uniforme == 1) {
			cor1 = new Color(time.getCor1RGB());
			cor2 = new Color(time.getCor2RGB());
			cor3 = new Color(time.getCor3RGB());
		} else {
			cor1 = new Color(time.getCor4RGB());
			cor2 = new Color(time.getCor5RGB());
			cor3 = new Color(time.getCor6RGB());

		}
		if (botao == null) {
			botao = new Goleiro();
			botao.setNome("Mesa");
			botao.setNumero(11);
		}
		botao.setPosition(new Point(0, 0));
		BufferedImage botaoImg = new BufferedImage(botao.getDiamentro(), 60,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = (Graphics2D) botaoImg.getGraphics();
		setarHints(graphics);
		graphics.fill(botao.getShape(1));
		AlphaComposite composite = AlphaComposite
				.getInstance(AlphaComposite.SRC_IN);
		graphics.setComposite(composite);
		graphics.setColor(cor1);
		graphics.fillRect(0, 0, Util.inte(botao.getDiamentro() * .7), 60);
		graphics.setColor(cor2);
		graphics.fillRect(Util.inte(botao.getDiamentro() * .7), 0, Util
				.inte(botao.getDiamentro() * .35), 60);
		graphics.setColor(cor3);
		graphics.setStroke(new BasicStroke(3.0f));
		graphics.drawRect(5, 5, botao.getDiamentro() - 10, 50);
		graphics.setFont(new Font(graphics.getFont().getName(), graphics
				.getFont().getStyle(), 20));
		graphics.setColor(cor2);
		if (cor2.equals(cor1)) {
			graphics.setColor(cor3);
		}
		if ((uniforme == 1 && time.isCorMeiaNumero1())
				|| (uniforme == 2 && time.isCorMeiaNumero2()))
			graphics.setColor(cor3);
		graphics.drawString(botao.getNome(), 12, Util.inte(60 * .5));
		graphics.setColor(cor1);
		if (cor1.equals(cor2)) {
			graphics.setColor(cor3);
		}
		if ((uniforme == 1 && time.isCorMeiaNumero1())
				|| (uniforme == 2 && time.isCorMeiaNumero2()))
			graphics.setColor(cor3);
		graphics.setFont(new Font(graphics.getFont().getName(), graphics
				.getFont().getStyle(), 24));
		graphics.drawString(botao.getNumero().toString(), Util.inte(botao
				.getDiamentro() * .75), Util.inte(60 * .5));

		graphics.dispose();
		return botaoImg;
	}

	public static void setarHints(Graphics2D g2d) {
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_DITHERING,
				RenderingHints.VALUE_DITHER_ENABLE);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	}

	public static BufferedImage desenhaUniforme(Time time, int uniforme) {
		return desenhaUniforme(time, uniforme, null);
	}

	public static BufferedImage desenhaUniforme(Time time, int uniforme,
			Botao botao) {
		Color cor1, cor2, cor3;
		if (uniforme == 1) {
			cor1 = new Color(time.getCor1RGB());
			cor2 = new Color(time.getCor2RGB());
			cor3 = new Color(time.getCor3RGB());
		} else {
			cor1 = new Color(time.getCor4RGB());
			cor2 = new Color(time.getCor5RGB());
			cor3 = new Color(time.getCor6RGB());

		}
		if (botao == null) {
			botao = new Botao();
			botao.setNome("Mesa");
			botao.setNumero(11);
		}
		BufferedImage botaoImg = new BufferedImage(botao.getDiamentro() + 10,
				botao.getDiamentro() + 10, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = (Graphics2D) botaoImg.getGraphics();
		setarHints(graphics);
		graphics.fillOval(0, 0, botao.getDiamentro(), botao.getDiamentro());
		AlphaComposite composite = AlphaComposite
				.getInstance(AlphaComposite.SRC_IN);
		graphics.setComposite(composite);
		graphics.setColor(cor1);
		graphics.fillRect(0, 0, botao.getDiamentro(), Util.inte(botao
				.getDiamentro() * .7));
		graphics.setColor(cor2);
		graphics.fillRect(0, Util.inte(botao.getDiamentro() * .7), botao
				.getDiamentro(), Util.inte(botao.getDiamentro() * .35));
		graphics.setColor(cor3);
		graphics.setStroke(new BasicStroke(2.5f));
		// graphics.drawOval(5, 5, botao.getDiamentro() - 10,
		// botao.getDiamentro() - 10);
		graphics.drawOval(1, 1, botao.getDiamentro() - 2,
				botao.getDiamentro() - 2);
		graphics.setFont(new Font(graphics.getFont().getName(), graphics
				.getFont().getStyle(), 24));
		graphics.setColor(cor2);
		if (cor2.equals(cor1)) {
			graphics.setColor(cor3);
		}
		if ((uniforme == 1 && time.isCorMeiaNumero1())
				|| (uniforme == 2 && time.isCorMeiaNumero2()))
			graphics.setColor(cor3);
		graphics.drawString(botao.getNome(), 8, Util
				.inte(botao.getDiamentro() * .60));
		graphics.setColor(cor1);
		if (cor1.equals(cor2)) {
			graphics.setColor(cor3);
		}
		if ((uniforme == 1 && time.isCorMeiaNumero1())
				|| (uniforme == 2 && time.isCorMeiaNumero2()))
			graphics.setColor(cor3);
		graphics.setFont(new Font(graphics.getFont().getName(), graphics
				.getFont().getStyle(), 30));
		graphics.drawString(botao.getNumero().toString(), Util.inte(botao
				.getDiamentro() * .4), Util.inte(botao.getDiamentro() * .9));

		graphics.dispose();
		return botaoImg;
	}

	public static Goleiro converteGoleiro(Botao botao) {
		Goleiro goleiro = new Goleiro();
		try {
			BeanUtil.copiarVO(botao, goleiro);
		} catch (Exception e) {
			Logger.logarExept(e);
		}
		goleiro.setDiamentro(400);
		return goleiro;
	}

	public static Botao converteBotao(Goleiro goleiro) {
		Botao botao = new Botao();
		try {
			BeanUtil.copiarVO(goleiro, botao);
		} catch (Exception e) {
			Logger.logarExept(e);
		}
		goleiro.setDiamentro(128);
		return botao;
	}

}
