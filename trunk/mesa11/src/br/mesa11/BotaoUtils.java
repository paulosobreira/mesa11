package br.mesa11;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import br.hibernate.Botao;
import br.hibernate.Goleiro;
import br.hibernate.Time;
import br.nnpe.BeanUtil;
import br.nnpe.Logger;
import br.nnpe.Util;

public class BotaoUtils {
	public final static Color lightBlack = new Color(0, 0, 0, 100);
	public final static Color lightWhite = new Color(255, 255, 255, 100);
	public final static BasicStroke bordaBotao = new BasicStroke(2.0f,
			BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

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
		Color corNome = cor2;
		if (cor2.equals(cor1)) {
			corNome = cor3;
		}
		if ((uniforme == 1 && time.isCorMeiaNumero1())
				|| (uniforme == 2 && time.isCorMeiaNumero2())) {
			corNome = cor3;

		}
		int valor = (corNome.getRed() + corNome.getGreen() + corNome.getBlue()) / 2;
		if (valor > 250) {
			graphics.setColor(lightBlack);
		} else {
			graphics.setColor(lightWhite);
		}
		int largura = 0;
		for (int i = 0; i < botao.getNome().length(); i++) {
			largura += graphics.getFontMetrics().charWidth(
					botao.getNome().charAt(i));
		}
		graphics.fillRoundRect(10, Util.inte(60 * .5) - 20, largura + 15, 25,
				5, 5);

		graphics.setColor(corNome);
		graphics.drawString(botao.getNome(), 12, Util.inte(60 * .5));

		Color corNumero = cor1;
		if (cor2.equals(cor1)) {
			corNumero = cor3;
		}
		if ((uniforme == 1 && time.isCorMeiaNumero1())
				|| (uniforme == 2 && time.isCorMeiaNumero2())) {
			corNumero = cor3;

		}
		valor = (corNumero.getRed() + corNumero.getGreen() + corNumero
				.getBlue()) / 2;
		if (valor > 250) {
			graphics.setColor(lightBlack);
		} else {
			graphics.setColor(lightWhite);
		}
		largura = 0;
		for (int i = 0; i < botao.getNumero().toString().length(); i++) {
			largura += graphics.getFontMetrics().charWidth(
					botao.getNumero().toString().charAt(i));
		}
		graphics.fillRoundRect(Util.inte(botao.getDiamentro() * .75) - 2, Util
				.inte(60 * .5) - 21, largura + 15, 25, 5, 5);
		graphics.setColor(corNumero);

		graphics.setFont(new Font(graphics.getFont().getName(), graphics
				.getFont().getStyle(), 24));
		graphics.drawString(botao.getNumero().toString(), Util.inte(botao
				.getDiamentro() * .75), Util.inte(60 * .5));

		graphics.dispose();
		return botaoImg;
	}

	public static BufferedImage desenhaUniforme(Time time, int uniforme) {
		return desenhaUniforme(time, uniforme, null);
	}

	public static BufferedImage desenhaUniforme(Time time, int uniforme,
			Botao botao) {
		int tipoUniforme = (uniforme == 1) ? time.getTipoUniforme1() : time
				.getTipoUniforme2();
		if (tipoUniforme == 0) {
			return desenhaUniforme0(time, uniforme, botao);
		} else if (tipoUniforme == 1) {
			return desenhaUniforme1(time, uniforme, botao);
		} else if (tipoUniforme == 2) {
			return desenhaUniforme2(time, uniforme, botao);
		} else if (tipoUniforme == 3) {
			return desenhaUniforme3(time, uniforme, botao);
		} else if (tipoUniforme == 4) {
			return desenhaUniforme4(time, uniforme, botao);
		} else if (tipoUniforme == 5) {
			return desenhaUniforme5(time, uniforme, botao);
		}
		return desenhaUniforme0(time, uniforme, botao);
	}

	public static BufferedImage desenhaUniforme4(Time time, int uniforme,
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
		Ellipse2D externo = new Ellipse2D.Double(0, 0, (botao.getDiamentro()),
				(botao.getDiamentro()));
		graphics.setClip(externo);

		setarHints(graphics);
		graphics.fillOval(0, 0, botao.getDiamentro(), botao.getDiamentro());
		int y = 0;
		for (int i = 0; i < 5; i++) {
			if (i % 2 == 0) {
				graphics.setColor(cor1);
			} else {
				graphics.setColor(cor3);
				graphics.fillRect(0, y - 2, botao.getDiamentro(), Util
						.inte(botao.getDiamentro() * .35));
				graphics.setColor(cor2);
			}
			graphics.fillRect(0, y, botao.getDiamentro(), Util.inte(botao
					.getDiamentro() * .35));

			y += Util.inte(botao.getDiamentro() * .2);

		}
		y = 0;
		for (int i = 0; i < 5; i++) {

			if (i % 2 == 0) {
				graphics.setColor(cor3);
				graphics.fillRect(0, 4 + y
						+ Util.inte(botao.getDiamentro() * .35), botao
						.getDiamentro(), 2);
			}
			y += Util.inte(botao.getDiamentro() * .2);

		}

		graphics.setColor(cor3);
		graphics.setStroke(bordaBotao);
		graphics.drawOval(1, 1, botao.getDiamentro() - 2,
				botao.getDiamentro() - 2);
		graphics.setFont(new Font(graphics.getFont().getName(), graphics
				.getFont().getStyle(), 24));
		Color corNome = cor2;
		if (cor2.equals(cor1)) {
			corNome = cor3;
		}
		if ((uniforme == 1 && time.isCorMeiaNumero1())
				|| (uniforme == 2 && time.isCorMeiaNumero2())) {
			corNome = cor3;

		}
		int valor = (corNome.getRed() + corNome.getGreen() + corNome.getBlue()) / 2;
		if (valor > 250) {
			graphics.setColor(lightBlack);
		} else {
			graphics.setColor(lightWhite);
		}
		int largura = 0;
		for (int i = 0; i < botao.getNome().length(); i++) {
			largura += graphics.getFontMetrics().charWidth(
					botao.getNome().charAt(i));
		}
		graphics.fillRoundRect(2, Util.inte(botao.getDiamentro() * .55) - 20,
				largura + 15, 25, 5, 5);

		graphics.setColor(corNome);
		graphics.drawString(botao.getNome(), 8, Util
				.inte(botao.getDiamentro() * .55));

		Color corNumero = cor1;
		if (cor2.equals(cor1)) {
			corNumero = cor3;
		}
		if ((uniforme == 1 && time.isCorMeiaNumero1())
				|| (uniforme == 2 && time.isCorMeiaNumero2())) {
			corNumero = cor3;

		}
		valor = (corNumero.getRed() + corNumero.getGreen() + corNumero
				.getBlue()) / 2;
		if (valor > 250) {
			graphics.setColor(lightBlack);
		} else {
			graphics.setColor(lightWhite);
		}
		largura = 0;
		for (int i = 0; i < botao.getNumero().toString().length(); i++) {
			largura += graphics.getFontMetrics().charWidth(
					botao.getNumero().toString().charAt(i));
		}
		graphics.fillRoundRect(Util.inte(botao.getDiamentro() * .4) - 2, Util
				.inte(botao.getDiamentro() * .77) - 23, largura + 15, 25, 5, 5);
		graphics.setColor(corNumero);
		graphics.setFont(new Font(graphics.getFont().getName(), graphics
				.getFont().getStyle(), 30));
		graphics.drawString(botao.getNumero().toString(), Util.inte(botao
				.getDiamentro() * .4), Util.inte(botao.getDiamentro() * .77));

		graphics.dispose();
		return botaoImg;
	}

	public static BufferedImage desenhaUniforme3(Time time, int uniforme,
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
		Ellipse2D externo = new Ellipse2D.Double(0, 0, (botao.getDiamentro()),
				(botao.getDiamentro()));
		graphics.setClip(externo);
		setarHints(graphics);
		graphics.fillOval(0, 0, botao.getDiamentro(), botao.getDiamentro());
		int x = 0;
		for (int i = 0; i < 5; i++) {
			if (i % 2 == 0) {
				graphics.setColor(cor1);
			} else {
				graphics.setColor(cor3);
				graphics
						.fillRect(x - 2, 0, Util
								.inte(botao.getDiamentro() * .35), botao
								.getDiamentro());
				graphics.setColor(cor2);
			}
			graphics.fillRect(x, 0, Util.inte(botao.getDiamentro() * .35),
					botao.getDiamentro());

			x += Util.inte(botao.getDiamentro() * .2);

		}

		x = 0;
		for (int i = 0; i < 5; i++) {

			if (i % 2 == 0) {
				graphics.setColor(cor3);
				graphics.fillRect(
						4 + x + Util.inte(botao.getDiamentro() * .35), 0, 2,
						botao.getDiamentro());
			}
			x += Util.inte(botao.getDiamentro() * .2);

		}

		graphics.setColor(cor3);
		graphics.setStroke(bordaBotao);
		graphics.drawOval(1, 1, botao.getDiamentro() - 2,
				botao.getDiamentro() - 2);
		graphics.setFont(new Font(graphics.getFont().getName(), graphics
				.getFont().getStyle(), 24));
		Color corNome = cor2;
		if (cor2.equals(cor1)) {
			corNome = cor3;
		}
		if ((uniforme == 1 && time.isCorMeiaNumero1())
				|| (uniforme == 2 && time.isCorMeiaNumero2())) {
			corNome = cor3;

		}
		int valor = (corNome.getRed() + corNome.getGreen() + corNome.getBlue()) / 2;
		if (valor > 250) {
			graphics.setColor(lightBlack);
		} else {
			graphics.setColor(lightWhite);
		}
		int largura = 0;
		for (int i = 0; i < botao.getNome().length(); i++) {
			largura += graphics.getFontMetrics().charWidth(
					botao.getNome().charAt(i));
		}
		graphics.fillRoundRect(2, Util.inte(botao.getDiamentro() * .55) - 20,
				largura + 15, 25, 5, 5);

		graphics.setColor(corNome);
		graphics.drawString(botao.getNome(), 8, Util
				.inte(botao.getDiamentro() * .55));
		Color corNumero = cor1;
		if (cor2.equals(cor1)) {
			corNumero = cor3;
		}
		if ((uniforme == 1 && time.isCorMeiaNumero1())
				|| (uniforme == 2 && time.isCorMeiaNumero2())) {
			corNumero = cor3;

		}
		valor = (corNumero.getRed() + corNumero.getGreen() + corNumero
				.getBlue()) / 2;
		if (valor > 250) {
			graphics.setColor(lightBlack);
		} else {
			graphics.setColor(lightWhite);
		}
		largura = 0;
		for (int i = 0; i < botao.getNumero().toString().length(); i++) {
			largura += graphics.getFontMetrics().charWidth(
					botao.getNumero().toString().charAt(i));
		}
		graphics.fillRoundRect(Util.inte(botao.getDiamentro() * .4) - 2, Util
				.inte(botao.getDiamentro() * .77) - 23, largura + 15, 25, 5, 5);
		graphics.setColor(corNumero);
		graphics.setFont(new Font(graphics.getFont().getName(), graphics
				.getFont().getStyle(), 30));
		graphics.drawString(botao.getNumero().toString(), Util.inte(botao
				.getDiamentro() * .4), Util.inte(botao.getDiamentro() * .77));
		graphics.dispose();
		return botaoImg;
	}

	public static BufferedImage desenhaUniforme1(Time time, int uniforme,
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
		Ellipse2D externo = new Ellipse2D.Double(0, 0, (botao.getDiamentro()),
				(botao.getDiamentro()));
		graphics.setClip(externo);

		setarHints(graphics);
		graphics.fillOval(0, 0, botao.getDiamentro(), botao.getDiamentro());
		int x = 0;
		for (int i = 0; i < 5; i++) {
			if (i % 2 == 0) {
				graphics.setColor(cor1);
			} else {
				graphics.setColor(cor2);
			}
			graphics.fillRect(x, 0, Util.inte(botao.getDiamentro() * .35),
					botao.getDiamentro());

			x += Util.inte(botao.getDiamentro() * .2);

		}
		graphics.setColor(cor3);
		graphics.setStroke(bordaBotao);
		graphics.drawOval(1, 1, botao.getDiamentro() - 2,
				botao.getDiamentro() - 2);
		graphics.setFont(new Font(graphics.getFont().getName(), graphics
				.getFont().getStyle(), 24));
		Color corNome = cor2;
		if (cor2.equals(cor1)) {
			corNome = cor3;
		}
		if ((uniforme == 1 && time.isCorMeiaNumero1())
				|| (uniforme == 2 && time.isCorMeiaNumero2())) {
			corNome = cor3;

		}
		int valor = (corNome.getRed() + corNome.getGreen() + corNome.getBlue()) / 2;
		if (valor > 250) {
			graphics.setColor(lightBlack);
		} else {
			graphics.setColor(lightWhite);
		}
		int largura = 0;
		for (int i = 0; i < botao.getNome().length(); i++) {
			largura += graphics.getFontMetrics().charWidth(
					botao.getNome().charAt(i));
		}
		graphics.fillRoundRect(2, Util.inte(botao.getDiamentro() * .55) - 20,
				largura + 15, 25, 5, 5);

		graphics.setColor(corNome);
		graphics.drawString(botao.getNome(), 8, Util
				.inte(botao.getDiamentro() * .55));
		Color corNumero = cor1;
		if (cor2.equals(cor1)) {
			corNumero = cor3;
		}
		if ((uniforme == 1 && time.isCorMeiaNumero1())
				|| (uniforme == 2 && time.isCorMeiaNumero2())) {
			corNumero = cor3;

		}
		valor = (corNumero.getRed() + corNumero.getGreen() + corNumero
				.getBlue()) / 2;
		if (valor > 250) {
			graphics.setColor(lightBlack);
		} else {
			graphics.setColor(lightWhite);
		}
		largura = 0;
		for (int i = 0; i < botao.getNumero().toString().length(); i++) {
			largura += graphics.getFontMetrics().charWidth(
					botao.getNumero().toString().charAt(i));
		}
		graphics.fillRoundRect(Util.inte(botao.getDiamentro() * .4) - 2, Util
				.inte(botao.getDiamentro() * .77) - 23, largura + 15, 25, 5, 5);
		graphics.setColor(corNumero);
		graphics.setFont(new Font(graphics.getFont().getName(), graphics
				.getFont().getStyle(), 30));
		graphics.drawString(botao.getNumero().toString(), Util.inte(botao
				.getDiamentro() * .4), Util.inte(botao.getDiamentro() * .77));

		graphics.dispose();
		return botaoImg;
	}

	public static BufferedImage desenhaUniforme2(Time time, int uniforme,
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
		Ellipse2D externo = new Ellipse2D.Double(0, 0, (botao.getDiamentro()),
				(botao.getDiamentro()));
		graphics.setClip(externo);

		setarHints(graphics);
		graphics.fillOval(0, 0, botao.getDiamentro(), botao.getDiamentro());
		int y = 0;
		for (int i = 0; i < 5; i++) {
			if (i % 2 == 0) {
				graphics.setColor(cor1);
			} else {
				graphics.setColor(cor2);
			}
			graphics.fillRect(0, y, botao.getDiamentro(), Util.inte(botao
					.getDiamentro() * .35));

			y += Util.inte(botao.getDiamentro() * .2);

		}
		graphics.setColor(cor3);
		graphics.setStroke(bordaBotao);
		graphics.drawOval(1, 1, botao.getDiamentro() - 2,
				botao.getDiamentro() - 2);
		graphics.setFont(new Font(graphics.getFont().getName(), graphics
				.getFont().getStyle(), 24));
		Color corNome = cor2;
		if (cor2.equals(cor1)) {
			corNome = cor3;
		}
		if ((uniforme == 1 && time.isCorMeiaNumero1())
				|| (uniforme == 2 && time.isCorMeiaNumero2())) {
			corNome = cor3;

		}
		int valor = (corNome.getRed() + corNome.getGreen() + corNome.getBlue()) / 2;
		if (valor > 250) {
			graphics.setColor(lightBlack);
		} else {
			graphics.setColor(lightWhite);
		}
		int largura = 0;
		for (int i = 0; i < botao.getNome().length(); i++) {
			largura += graphics.getFontMetrics().charWidth(
					botao.getNome().charAt(i));
		}
		graphics.fillRoundRect(2, Util.inte(botao.getDiamentro() * .55) - 20,
				largura + 15, 25, 5, 5);

		graphics.setColor(corNome);
		graphics.drawString(botao.getNome(), 8, Util
				.inte(botao.getDiamentro() * .55));
		Color corNumero = cor1;
		if (cor2.equals(cor1)) {
			corNumero = cor3;
		}
		if ((uniforme == 1 && time.isCorMeiaNumero1())
				|| (uniforme == 2 && time.isCorMeiaNumero2())) {
			corNumero = cor3;

		}
		valor = (corNumero.getRed() + corNumero.getGreen() + corNumero
				.getBlue()) / 2;
		if (valor > 250) {
			graphics.setColor(lightBlack);
		} else {
			graphics.setColor(lightWhite);
		}
		largura = 0;
		for (int i = 0; i < botao.getNumero().toString().length(); i++) {
			largura += graphics.getFontMetrics().charWidth(
					botao.getNumero().toString().charAt(i));
		}
		graphics.fillRoundRect(Util.inte(botao.getDiamentro() * .4) - 2, Util
				.inte(botao.getDiamentro() * .77) - 23, largura + 15, 25, 5, 5);
		graphics.setColor(corNumero);
		graphics.setFont(new Font(graphics.getFont().getName(), graphics
				.getFont().getStyle(), 30));
		graphics.drawString(botao.getNumero().toString(), Util.inte(botao
				.getDiamentro() * .4), Util.inte(botao.getDiamentro() * .77));

		graphics.dispose();
		return botaoImg;
	}

	public static BufferedImage desenhaUniforme5(Time time, int uniforme,
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
		Ellipse2D externo = new Ellipse2D.Double(0, 0, (botao.getDiamentro()),
				(botao.getDiamentro()));
		graphics.setClip(externo);
		graphics.fillOval(0, 0, botao.getDiamentro(), botao.getDiamentro());
		graphics.setColor(cor1);
		graphics.fillRect(0, 0, Util.inte(botao.getDiamentro() * .7), botao
				.getDiamentro());
		graphics.setColor(cor2);
		graphics.fillRect(Util.inte(botao.getDiamentro() * .7), 0, Util
				.inte(botao.getDiamentro() * .35), botao.getDiamentro());
		graphics.setColor(cor3);
		graphics.setStroke(bordaBotao);
		graphics.drawOval(1, 1, botao.getDiamentro() - 2,
				botao.getDiamentro() - 2);
		graphics.setFont(new Font(graphics.getFont().getName(), graphics
				.getFont().getStyle(), 24));
		Color corNome = cor2;
		if (cor2.equals(cor1)) {
			corNome = cor3;
		}
		if ((uniforme == 1 && time.isCorMeiaNumero1())
				|| (uniforme == 2 && time.isCorMeiaNumero2())) {
			corNome = cor3;

		}
		int valor = (corNome.getRed() + corNome.getGreen() + corNome.getBlue()) / 2;
		if (valor > 250) {
			graphics.setColor(lightBlack);
		} else {
			graphics.setColor(lightWhite);
		}
		int largura = 0;
		for (int i = 0; i < botao.getNome().length(); i++) {
			largura += graphics.getFontMetrics().charWidth(
					botao.getNome().charAt(i));
		}
		graphics.fillRoundRect(2, Util.inte(botao.getDiamentro() * .60) - 20,
				largura + 15, 25, 5, 5);

		graphics.setColor(corNome);
		graphics.drawString(botao.getNome(), 8, Util
				.inte(botao.getDiamentro() * .60));

		Color corNumero = cor1;
		if (cor2.equals(cor1)) {
			corNumero = cor3;
		}
		if ((uniforme == 1 && time.isCorMeiaNumero1())
				|| (uniforme == 2 && time.isCorMeiaNumero2())) {
			corNumero = cor3;

		}
		valor = (corNumero.getRed() + corNumero.getGreen() + corNumero
				.getBlue()) / 2;
		if (valor > 250) {
			graphics.setColor(lightBlack);
		} else {
			graphics.setColor(lightWhite);
		}
		largura = 0;
		for (int i = 0; i < botao.getNumero().toString().length(); i++) {
			largura += graphics.getFontMetrics().charWidth(
					botao.getNumero().toString().charAt(i));
		}
		graphics.fillRoundRect(Util.inte(botao.getDiamentro() * .4) - 2, Util
				.inte(botao.getDiamentro() * .9) - 23, largura + 15, 25, 5, 5);
		graphics.setColor(corNumero);

		graphics.setFont(new Font(graphics.getFont().getName(), graphics
				.getFont().getStyle(), 30));
		graphics.drawString(botao.getNumero().toString(), Util.inte(botao
				.getDiamentro() * .4), Util.inte(botao.getDiamentro() * .9));

		graphics.dispose();
		return botaoImg;
	}

	public static BufferedImage desenhaUniforme0(Time time, int uniforme,
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
		Ellipse2D externo = new Ellipse2D.Double(0, 0, (botao.getDiamentro()),
				(botao.getDiamentro()));
		graphics.setClip(externo);

		setarHints(graphics);
		graphics.fillOval(0, 0, botao.getDiamentro(), botao.getDiamentro());
		graphics.setColor(cor1);
		graphics.fillRect(0, 0, botao.getDiamentro(), Util.inte(botao
				.getDiamentro() * .7));
		graphics.setColor(cor2);
		graphics.fillRect(0, Util.inte(botao.getDiamentro() * .7), botao
				.getDiamentro(), Util.inte(botao.getDiamentro() * .35));
		graphics.setColor(cor3);
		graphics.setStroke(bordaBotao);
		graphics.drawOval(1, 1, botao.getDiamentro() - 2,
				botao.getDiamentro() - 2);
		graphics.setFont(new Font(graphics.getFont().getName(), graphics
				.getFont().getStyle(), 24));
		Color corNome = cor2;
		if (cor2.equals(cor1)) {
			corNome = cor3;
		}
		if ((uniforme == 1 && time.isCorMeiaNumero1())
				|| (uniforme == 2 && time.isCorMeiaNumero2())) {
			corNome = cor3;

		}
		int valor = (corNome.getRed() + corNome.getGreen() + corNome.getBlue()) / 2;
		if (valor > 250) {
			graphics.setColor(lightBlack);
		} else {
			graphics.setColor(lightWhite);
		}
		int largura = 0;
		for (int i = 0; i < botao.getNome().length(); i++) {
			largura += graphics.getFontMetrics().charWidth(
					botao.getNome().charAt(i));
		}
		graphics.fillRoundRect(2, Util.inte(botao.getDiamentro() * .60) - 20,
				largura + 15, 25, 5, 5);

		graphics.setColor(corNome);
		graphics.drawString(botao.getNome(), 8, Util
				.inte(botao.getDiamentro() * .60));

		Color corNumero = cor1;
		if (cor2.equals(cor1)) {
			corNumero = cor3;
		}
		if ((uniforme == 1 && time.isCorMeiaNumero1())
				|| (uniforme == 2 && time.isCorMeiaNumero2())) {
			corNumero = cor3;

		}
		valor = (corNumero.getRed() + corNumero.getGreen() + corNumero
				.getBlue()) / 2;
		if (valor > 250) {
			graphics.setColor(lightBlack);
		} else {
			graphics.setColor(lightWhite);
		}
		largura = 0;
		for (int i = 0; i < botao.getNumero().toString().length(); i++) {
			largura += graphics.getFontMetrics().charWidth(
					botao.getNumero().toString().charAt(i));
		}
		graphics.fillRoundRect(Util.inte(botao.getDiamentro() * .4) - 2, Util
				.inte(botao.getDiamentro() * .9) - 23, largura + 15, 25, 5, 5);
		graphics.setColor(corNumero);

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
}
