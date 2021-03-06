package br.mesa11.visao;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import br.hibernate.Botao;
import br.hibernate.Goleiro;
import br.hibernate.Time;
import br.mesa11.ConstantesMesa11;
import br.mesa11.conceito.ControleJogo;
import br.mesa11.conceito.GolJogador;
import br.nnpe.GeoUtil;
import br.nnpe.ImageUtil;
import br.nnpe.Logger;
import br.nnpe.OcilaCor;
import br.nnpe.Util;
import br.recursos.CarregadorRecursos;
import br.recursos.Lang;

public class MesaPanel extends JPanel {

	public static final Long zero = new Long(0);
	public static Color green2 = new Color(0, 200, 0, 150);
	public static Color green = new Color(0, 255, 0, 150);
	public static Color tansp = new Color(255, 255, 255, 100);
	public final static Color amarelo = new Color(255, 255, 0, 150);
	public final static Color brancoClaro = new Color(255, 255, 255, 200);
	public final static Color vermelho = new Color(250, 0, 0, 150);
	public static final int LARGURA_MESA = 3430;
	public static final int ALTURA_MESA = 5286;
	public static final int BORDA_CAMPO = 230;
	public static final int DOBRO_BORDA_CAMPO = BORDA_CAMPO * 2;
	private static final int LINHA = 10;
	private static final int DOBRO_LINHA = LINHA * 2;
	public static final int LARGURA_GDE_AREA = 1714;
	public static final int ALTURA_GDE_AREA = 857;
	public static final int LARGURA_PQ_AREA = ALTURA_GDE_AREA;
	public static final int ALTURA_PQ_AREA = 314;
	public static final int RAIO_CENTRO = 1000;
	public static final int PENALTI = 586;
	public static final int FAIXAS = 14;
	private static DecimalFormat mil = new DecimalFormat("000");
	public static final int ALTURA_FAIXA = (ALTURA_MESA - DOBRO_BORDA_CAMPO
			- DOBRO_LINHA) / FAIXAS;
	public static final BasicStroke rota = new BasicStroke(2.5f,
			BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	private static final int TAM_MAX_NM_TIME = 14;

	public double zoom = 0.5;
	public double mouseZoom = 0.5;
	private static boolean debug = false;
	private boolean desenhaSplash = true;
	private Rectangle campoCima;
	private Rectangle campoCimaSemLinhas;
	private Rectangle campoBaixo;
	private Rectangle campoBaixoSemLinhas;
	private Rectangle areaEscateioCima;
	private Rectangle areaGolCima;
	private Rectangle linhaGolCima;
	private Rectangle areaGolBaixo;
	private Rectangle linhaGolBaixo;
	private Rectangle areaEscateioBaixo;

	private Rectangle grandeAreaCima;
	private Rectangle grandeAreaBaixo;
	private Rectangle pequenaAreaCima;
	private Rectangle pequenaAreaBaixo;
	private Rectangle centro;
	private Rectangle penaltyCima;
	private Rectangle penaltyBaixo;
	private Rectangle hasteDireitaGolCima;
	private Rectangle hasteEsquerdaGolCima;
	private Rectangle hasteTopoGolCima;

	private Rectangle hasteDireitaGolBaixo;
	private Rectangle hasteEsquerdaGolBaixo;
	private Rectangle hasteTopoGolBaixo;

	private ControleJogo controleJogo;

	private Rectangle2D zoomedMesa;
	private Rectangle2D zoomedBorda;
	private Rectangle2D zoomedGrama;
	private Ellipse2D zoomedMeiaLuaCimaBorda;
	private Ellipse2D zoomedMeiaLuaCimaGrama;
	private Ellipse2D zoomedMeiaLuaBaixoBorda;
	private Ellipse2D zoomedMeiaLuaBaixoGrama;
	private Rectangle2D zoomedGdeAreaCimaBorda;
	private Rectangle2D zoomedGdeAreaCimaGrama;
	private Rectangle2D zoomedGdeAreaBaixoBorda;
	private Rectangle2D zoomedGdeAreaBaixoGrama;
	private Rectangle2D zoomedpqAreaCimaBorda;
	private Rectangle2D zoomedpqAreaCimaGrama;
	private Rectangle2D zoomedpqAreaBaixoBorda;
	private Rectangle2D zoomedpqAreaBaixoGrama;
	private Ellipse2D zoomedcentroBorda;
	private Ellipse2D zoomedcentroGrama;
	private Ellipse2D zoomedMeioCampoBorda;
	private Ellipse2D zoomedPenaltiCima;
	private Ellipse2D zoomedPenaltiBaixo;
	private Ellipse2D zoomedCentro;
	private Rectangle2D[] zoomedFaixasGrama = new Rectangle2D[FAIXAS];
	private Shape limitesViewPort;
	private double oldZoom;
	public BufferedImage grama1;
	public BufferedImage grama2;

	public BufferedImage grama1Zoomed;
	public BufferedImage grama2Zoomed;
	public BufferedImage mesa11Bkg;

	private boolean problemasRede;
	private int contProblemaRede;
	public long lastZoomChange;
	private int contMostraLag;
	private Map botoes;
	private Botao botaoInfo;

	public MesaPanel(ControleJogo controleJogo) {
		if (!controleJogo.isJogoOnlineSrvidor()) {
			try {
				grama1 = CarregadorRecursos.carregaImg("grama1.jpg");
				grama2 = CarregadorRecursos.carregaImg("grama2.jpg");
				mesa11Bkg = ImageUtil.geraResize(CarregadorRecursos
						.carregaBufferedImage("mesa11-bkg.png"), 1.30, 0.79);
			} catch (Exception e) {
				Logger.logarExept(e);
			}
		}
		if (!desenhaSplash) {
			grama1 = null;
			grama2 = null;
			green2 = new Color(240, 240, 240);
			green = Color.white;
		}
		setSize(LARGURA_MESA * 2, ALTURA_MESA * 2);
		areaEscateioCima = new Rectangle((BORDA_CAMPO + LINHA), 0,
				(LARGURA_MESA - DOBRO_BORDA_CAMPO - DOBRO_LINHA),
				BORDA_CAMPO + LINHA);
		areaEscateioBaixo = new Rectangle((BORDA_CAMPO + LINHA),
				(ALTURA_MESA - BORDA_CAMPO) - LINHA,
				(LARGURA_MESA - DOBRO_BORDA_CAMPO - DOBRO_LINHA),
				BORDA_CAMPO + LINHA);
		campoCima = new Rectangle(BORDA_CAMPO, BORDA_CAMPO,
				LARGURA_MESA - DOBRO_BORDA_CAMPO,
				(ALTURA_MESA / 2) - BORDA_CAMPO);
		campoCimaSemLinhas = new Rectangle(campoCima.x + LINHA,
				campoCima.y + LINHA, campoCima.width - (2 * LINHA),
				campoCima.height);
		campoBaixo = new Rectangle(BORDA_CAMPO, (ALTURA_MESA / 2),
				LARGURA_MESA - DOBRO_BORDA_CAMPO,
				(ALTURA_MESA / 2) - BORDA_CAMPO);
		campoBaixoSemLinhas = new Rectangle(campoBaixo.x + LINHA, campoBaixo.y,
				campoBaixo.width - (2 * LINHA), campoBaixo.height - LINHA);
		grandeAreaCima = new Rectangle(ALTURA_GDE_AREA, BORDA_CAMPO,
				LARGURA_GDE_AREA, ALTURA_GDE_AREA);
		grandeAreaBaixo = new Rectangle(ALTURA_GDE_AREA,
				ALTURA_MESA - BORDA_CAMPO - ALTURA_GDE_AREA, LARGURA_GDE_AREA,
				ALTURA_GDE_AREA);
		pequenaAreaCima = new Rectangle(LARGURA_PQ_AREA + LARGURA_PQ_AREA / 2,
				BORDA_CAMPO, LARGURA_PQ_AREA, ALTURA_PQ_AREA);
		pequenaAreaBaixo = new Rectangle(LARGURA_PQ_AREA + LARGURA_PQ_AREA / 2,
				ALTURA_MESA - BORDA_CAMPO - ALTURA_PQ_AREA, LARGURA_PQ_AREA,
				ALTURA_PQ_AREA);
		centro = new Rectangle(LARGURA_MESA / 2, ALTURA_MESA / 2, DOBRO_LINHA,
				DOBRO_LINHA);
		penaltyCima = new Rectangle(LARGURA_MESA / 2, BORDA_CAMPO + PENALTI,
				DOBRO_LINHA, DOBRO_LINHA);
		penaltyBaixo = new Rectangle(LARGURA_MESA / 2,
				ALTURA_MESA - BORDA_CAMPO - PENALTI, DOBRO_LINHA, DOBRO_LINHA);
		hasteDireitaGolCima = new Rectangle(
				Util.inte(pequenaAreaCima.getX() + pequenaAreaCima.getWidth()
						- (110)),
				Util.inte(pequenaAreaCima.getY() + (LINHA)
						- (pequenaAreaCima.getHeight() * .50)),
				Util.inte(10), Util.inte(pequenaAreaCima.getHeight() * .50));
		hasteEsquerdaGolCima = new Rectangle(
				Util.inte(pequenaAreaCima.getX() + (110)),
				Util.inte(pequenaAreaCima.getY() + (LINHA)
						- (pequenaAreaCima.getHeight() * .50)),
				Util.inte(10), Util.inte(pequenaAreaCima.getHeight() * .50));
		hasteTopoGolCima = new Rectangle(Util.inte(hasteEsquerdaGolCima.getX()),
				Util.inte(hasteDireitaGolCima.getY()),
				Util.inte(hasteDireitaGolCima.getX()
						- hasteEsquerdaGolCima.getX()),
				Util.inte(10));
		areaGolCima = new Rectangle(
				Util.inte(hasteEsquerdaGolCima.getCenterX()),
				Util.inte(hasteTopoGolCima.getY()),
				Util.inte(hasteTopoGolCima.getWidth()),
				Util.inte(hasteEsquerdaGolCima.getHeight() - (LINHA)));
		linhaGolCima = new Rectangle(areaGolCima.x,
				areaGolCima.y + areaGolCima.height, areaGolCima.width, LINHA);
		hasteDireitaGolBaixo = new Rectangle(
				Util.inte(hasteDireitaGolCima.getX()),
				Util.inte(pequenaAreaBaixo.getY() - (LINHA)
						+ pequenaAreaBaixo.getHeight()),
				10, Util.inte(pequenaAreaBaixo.getHeight()
						- (pequenaAreaBaixo.getHeight() * .50)));
		hasteEsquerdaGolBaixo = new Rectangle(
				Util.inte(hasteEsquerdaGolCima.getX()),
				Util.inte(pequenaAreaBaixo.getY() - (LINHA)
						+ pequenaAreaBaixo.getHeight()),
				10, Util.inte(pequenaAreaBaixo.getHeight()
						- (pequenaAreaBaixo.getHeight() * .50)));
		hasteTopoGolBaixo = new Rectangle(
				Util.inte(hasteEsquerdaGolBaixo.getX()),
				Util.inte(hasteDireitaGolBaixo.getY()
						+ hasteDireitaGolBaixo.getHeight()),
				Util.inte(hasteDireitaGolBaixo.getX()
						- hasteEsquerdaGolBaixo.getX() + Util.inte(10)),
				Util.inte(10));
		areaGolBaixo = new Rectangle(
				Util.inte(hasteEsquerdaGolBaixo.getCenterX()),
				Util.inte(hasteTopoGolBaixo.getY()
						- hasteEsquerdaGolBaixo.getHeight() + (LINHA)),
				Util.inte(hasteTopoGolCima.getWidth()),
				Util.inte(hasteEsquerdaGolBaixo.getHeight() - (LINHA)));
		linhaGolBaixo = new Rectangle(areaGolBaixo.x, areaGolBaixo.y - LINHA,
				areaGolBaixo.width, LINHA);
		this.controleJogo = controleJogo;
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension((int) (LARGURA_MESA), (int) (ALTURA_MESA));
	}

	private void setarHints(Graphics2D g2d) {
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_DITHERING,
				RenderingHints.VALUE_DITHER_ENABLE);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
				RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
				RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);

	}

	public Rectangle getAreaEscateioCima() {
		return areaEscateioCima;
	}

	public Rectangle getAreaGolCima() {
		return areaGolCima;
	}

	public Rectangle getAreaGolBaixo() {
		return areaGolBaixo;
	}

	public Rectangle getLinhaGolCima() {
		return linhaGolCima;
	}

	public Rectangle getLinhaGolBaixo() {
		return linhaGolBaixo;
	}

	public Rectangle getAreaEscateioBaixo() {
		return areaEscateioBaixo;
	}

	public BufferedImage desenhaCampo() {
		BufferedImage image = new BufferedImage(LARGURA_MESA, ALTURA_MESA,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = (Graphics2D) image.getGraphics();
		limitesViewPort();
		desenhaSplash = false;
		zoom  = 1;
		desenhaCampo(g2d);
		desenhaTravesGol(g2d);
		return image;

	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (Math.abs(mouseZoom - zoom) < 0.01) {
			zoom = mouseZoom;
		}
		if (mouseZoom > zoom && !controleJogo.isAnimando()) {
			zoom += 0.01;
			lastZoomChange = System.currentTimeMillis();
			controleJogo.centralizaBola();
		}
		if (mouseZoom < zoom && !controleJogo.isAnimando()) {
			zoom -= 0.01;
			lastZoomChange = System.currentTimeMillis();
			controleJogo.centralizaBola();
		}
		limitesViewPort();
		if (botoes == null || controleJogo.isAnimando() || botoes.isEmpty()) {
			botoes = controleJogo.getBotoesCopia();
		}
		if (controleJogo.isJogoOnlineCliente()) {
			desenhaSplash = false;
			controleJogo.atualizaBotoesCopia();
			botoes = controleJogo.getBotoesCopia();
		}
		Graphics2D g2d = (Graphics2D) g;
		setarHints(g2d);
		desenhaBordaCinza(g2d);
		desenhaCampo(g2d);
		desenhaMesa11Bkg(g2d);
		desenhaTravesGol(g2d);
		desenhaFiguras(g2d);
		simulaRota(g2d, botoes);
		desenhaSombraGoleiro(g2d, botoes);
		desenhaBotoes(g2d, botoes);
		desenhaInfoJogo(g2d);
		desenhaInfoBotao(g2d);
		desenhaDica(g2d);
		desenhaGolsJogo(g2d);
		desenhaLag(g2d);
		desenhaDebugJogadaCpu(g2d);
		Toolkit.getDefaultToolkit().sync();
	}

	public void limitesViewPort() {
		limitesViewPort = controleJogo.limitesViewPort();
		if (limitesViewPort == null) {
			limitesViewPort = new Rectangle(0, 0, LARGURA_MESA, ALTURA_MESA);
		}
	}

	public void desenhaBordaCinza(Graphics g) {
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(limitesViewPort.getBounds().x, limitesViewPort.getBounds().y,
				(int) limitesViewPort.getBounds().getWidth(),
				(int) limitesViewPort.getBounds().getHeight());
	}

	private void desenhaMesa11Bkg(Graphics2D g2d) {
		if (!desenhaSplash) {
			return;
		}
		if (mesa11Bkg != null) {
			int centerX = controleJogo.getFrame().getWidth() / 2;
			int centerY = Util.inte(controleJogo.getFrame().getHeight() / 2.2);
			int bgX = mesa11Bkg.getWidth() / 2;
			int bgY = mesa11Bkg.getHeight() / 2;
			g2d.drawImage(mesa11Bkg, centerX - bgX, centerY - bgY, null);
		}
	}

	private void desenhaDebugJogadaCpu(Graphics2D g2d) {
		if (controleJogo == null || !controleJogo.isJogoIniciado()) {
			return;
		}
		if (debug) {
			if (controleJogo.ptDstBola != null
					&& controleJogo.timeJogadaVez() != null
					&& controleJogo.getBola() != null
					&& controleJogo.golJogadaCpu != null
					&& controleJogo.timeJogadaVez().isControladoCPU()) {
				g2d.setColor(vermelho);
				g2d.fillOval(
						Util.inte((controleJogo.ptDstBola.x
								- controleJogo.getBola().getRaio()) * zoom),
						Util.inte((controleJogo.ptDstBola.y
								- controleJogo.getBola().getRaio()) * zoom),
						5, 5);
				g2d.drawLine(
						Util.inte(controleJogo.getBola().getCentro().x * zoom),
						Util.inte(controleJogo.getBola().getCentro().y * zoom),
						Util.inte(controleJogo.golJogadaCpu.x * zoom),
						Util.inte(controleJogo.golJogadaCpu.y * zoom));
			}
			Shape miniViewPort = controleJogo.miniViewPort();
			if (miniViewPort != null) {
				g2d.draw(miniViewPort);
			}
		}

	}

	private void desenhaBotoes(Graphics2D g2d, Map botoes) {
		if (!controleJogo.isJogoIniciado()) {
			return;
		}
		if (botoes == null) {
			return;
		}
		for (Iterator iterator = botoes.keySet().iterator(); iterator
				.hasNext();) {
			Long id = (Long) iterator.next();
			if (zero.equals(id)) {
				continue;
			}
			Botao botao = (Botao) botoes.get(id);
			if (botao instanceof Goleiro) {
				Goleiro goleiro = (Goleiro) botao;
				desenhaGoleiro(goleiro, g2d);
			} else {
				desenhaBotao(botao, g2d);
			}

		}
		desenhaBotao((Botao) botoes.get(new Long(0)), g2d);
	}

	private void desenhaSombraGoleiro(Graphics2D g2d, Map botoes) {
		if (!controleJogo.isJogoIniciado()) {
			return;
		}
		if (controleJogo.getPontoClicado() != null
				&& controleJogo.getPontoPasando() != null && botoes != null) {
			g2d.setColor(Color.LIGHT_GRAY);
			Point p0 = (Point) controleJogo.getPontoClicado();
			if (controleJogo.getBola().getShape(1).getBounds2D().contains(p0)) {
				return;
			}

			Point pAtual = (Point) controleJogo.getPontoPasando();
			for (Iterator iterator = botoes.keySet().iterator(); iterator
					.hasNext();) {
				Long id = (Long) iterator.next();
				Botao botao = (Botao) botoes.get(id);
				if (botao == null || botao.getCentro() == null
						|| !(botao instanceof Goleiro)) {
					continue;
				}
				List raioPonto = GeoUtil.drawBresenhamLine(p0,
						botao.getCentro());
				if (raioPonto.size() <= (botao.getRaio() / 2)) {
					Goleiro g = new Goleiro();
					g.setCentroTodos(pAtual);
					g.setRotacao(((Goleiro) botao).getRotacao());
					if ((getGrandeAreaCima().contains(g.getShape(1).getBounds())
							|| getGrandeAreaBaixo()
									.contains(g.getShape(1).getBounds())
							|| getAreaGolCima()
									.intersects(g.getShape(1).getBounds())
							|| getAreaGolBaixo()
									.intersects(g.getShape(1).getBounds()))
							&& !g.getShape(1).intersects(controleJogo.getBola()
									.getShape(1).getBounds2D())) {
						g2d.setColor(brancoClaro);
					} else {
						g2d.setColor(vermelho);
					}

					g2d.fill(g.getShape(zoom));
					break;
				} else if ((raioPonto.size() > (botao.getRaio() / 2))
						&& (raioPonto.size() <= botao.getRaio())) {
					Goleiro g = new Goleiro();
					g.setCentroTodos(botao.getCentro());

					g.setRotacao(
							GeoUtil.calculaAngulo(g.getCentro(), pAtual, 0));

					if ((getGrandeAreaCima().contains(g.getShape(1).getBounds())
							|| getGrandeAreaBaixo()
									.contains(g.getShape(1).getBounds())
							|| getAreaGolCima()
									.intersects(g.getShape(1).getBounds())
							|| getAreaGolBaixo()
									.intersects(g.getShape(1).getBounds()))
							&& !g.getShape(1).intersects(controleJogo.getBola()
									.getShape(1).getBounds2D())) {
						g2d.setColor(brancoClaro);
					} else {
						g2d.setColor(vermelho);
					}

					g2d.fill(g.getShape(zoom));
					break;
				}
			}

		}

	}

	private void desenhaGolsJogo(Graphics2D g2d) {
		if (!controleJogo.isJogoIniciado()) {
			return;
		}
		List<GolJogador> golsTempo = controleJogo.getGolsTempo();
		if (golsTempo == null || golsTempo.isEmpty())
			return;
		g2d.setColor(Color.BLACK);
		int x = limitesViewPort.getBounds().x + 15;
		int y = limitesViewPort.getBounds().y + 20;

		g2d.setColor(brancoClaro);
		String msg = Lang.msg("gols");
		int largura = 0;
		for (int i = 0; i < msg.length(); i++) {
			largura += g2d.getFontMetrics().charWidth(msg.charAt(i));
		}
		// x -= largura / 2;
		g2d.fillRoundRect(x - 10, y - 15, largura + 10, 20, 0, 0);
		g2d.setColor(Color.BLACK);
		g2d.drawString(msg, x - 5, y);
		for (Iterator iterator = golsTempo.iterator(); iterator.hasNext();) {
			GolJogador golJogador = (GolJogador) iterator.next();
			y += 25;
			Botao b = controleJogo.obterBotao(golJogador.getIdJogador());
			Time time = b.getTime();
			Color c1 = new Color(time.getCor1());
			Color corFundo = ImageUtil.gerarCorTransparente(c1, 200);
			g2d.setColor(corFundo);
			msg = b.getNumero() + " " + b.getNome();
			largura = 0;
			for (int i = 0; i < msg.length(); i++) {
				largura += g2d.getFontMetrics().charWidth(msg.charAt(i));
			}
			g2d.fillRoundRect(x - 10, y - 15, largura + 10, 20, 0, 0);
			int valor = (c1.getRed() + c1.getGreen() + c1.getBlue()) / 2;
			if (valor > 250) {
				g2d.setColor(Color.BLACK);
			} else {
				g2d.setColor(Color.WHITE);
			}
			g2d.drawString(msg, x - 5, y);

			Color c2 = new Color(time.getCor2());
			corFundo = ImageUtil.gerarCorTransparente(c2, 200);
			g2d.setColor(corFundo);
			msg = golJogador.getTempoGol();
			int newX = x + largura + 15;
			largura = 0;
			for (int i = 0; i < msg.length(); i++) {
				largura += g2d.getFontMetrics().charWidth(msg.charAt(i));
			}
			g2d.fillRoundRect(newX - 10, y - 15, largura + 10, 20, 0, 0);
			valor = (c2.getRed() + c2.getGreen() + c2.getBlue()) / 2;
			if (valor > 250) {
				g2d.setColor(Color.BLACK);
			} else {
				g2d.setColor(Color.WHITE);
			}
			g2d.drawString(msg, newX - 5, y);

			if (golJogador.isContra()) {
				g2d.setColor(Color.YELLOW);
				g2d.drawString("C", newX + largura + 5, y);
			}
		}
	}

	private void desenhaInfoJogo(Graphics2D g2d) {
		if (!controleJogo.isJogoIniciado()) {
			return;
		}
		g2d.setColor(Color.BLACK);
		int x = limitesViewPort.getBounds().x
				+ (limitesViewPort.getBounds().width - 150);
		int y = limitesViewPort.getBounds().y + 20;
		Time timeCasa = controleJogo.obterTimeMandante();
		Time timeVisita = controleJogo.obterTimeVisita();
		if (timeCasa != null && timeVisita != null) {
			int newx = limitesViewPort.getBounds().x
					+ (limitesViewPort.getBounds().width / 2);
			Color cM1 = new Color(timeCasa.getCor1());
			Color cM2 = new Color(timeCasa.getCor2());
			Color corFundo = ImageUtil.gerarCorTransparente(cM1, 200);
			g2d.setColor(corFundo);
			g2d.fillRoundRect(newx - 100, y - 15, 100, 20, 0, 0);
			int valor = (cM1.getRed() + cM1.getGreen() + cM1.getBlue()) / 2;
			if (valor > 250) {
				g2d.setColor(Color.BLACK);
			} else {
				g2d.setColor(Color.WHITE);
			}
			String nmTime = "";
			if (timeCasa != null && !Util.isNullOrEmpty(timeCasa.getNome())) {
				for (int i = 0; i < timeCasa.getNome().length(); i++) {
					if (i > TAM_MAX_NM_TIME) {
						break;
					}
					nmTime += timeCasa.getNome().charAt(i);
				}
			}
			g2d.drawString("" + nmTime, newx - 90, y);
			corFundo = ImageUtil.gerarCorTransparente(cM2, 200);
			g2d.setColor(corFundo);
			g2d.fillRoundRect(newx, y - 15, 20, 20, 0, 0);
			valor = (cM2.getRed() + cM2.getGreen() + cM2.getBlue()) / 2;
			if (valor > 250) {
				g2d.setColor(Color.BLACK);
			} else {
				g2d.setColor(Color.WHITE);
			}
			g2d.drawString("" + controleJogo.verGols(timeCasa), newx + 10, y);

			Color cV1 = new Color(timeVisita.getCor1());
			Color cV2 = new Color(timeVisita.getCor2());
			corFundo = ImageUtil.gerarCorTransparente(cV1, 200);
			g2d.setColor(corFundo);
			g2d.fillRoundRect(newx + 60, y - 15, 100, 20, 0, 0);
			valor = (cV1.getRed() + cV1.getGreen() + cV1.getBlue()) / 2;
			if (valor > 250) {
				g2d.setColor(Color.BLACK);
			} else {
				g2d.setColor(Color.WHITE);
			}
			nmTime = "";
			if (timeVisita != null
					&& !Util.isNullOrEmpty(timeVisita.getNome())) {
				for (int i = 0; i < timeVisita.getNome().length(); i++) {
					if (i > TAM_MAX_NM_TIME) {
						break;
					}
					nmTime += timeVisita.getNome().charAt(i);
				}
			}
			g2d.drawString("" + nmTime, newx + 70, y);
			corFundo = ImageUtil.gerarCorTransparente(cV2, 200);
			g2d.setColor(corFundo);
			g2d.fillRoundRect(newx + 40, y - 15, 20, 20, 0, 0);
			valor = (cV2.getRed() + cV2.getGreen() + cV2.getBlue()) / 2;
			if (valor > 250) {
				g2d.setColor(Color.BLACK);
			} else {
				g2d.setColor(Color.WHITE);
			}
			g2d.drawString("" + controleJogo.verGols(timeVisita), newx + 51, y);
		}
		Time time = controleJogo.timeJogadaVez();
		if (time != null) {
			Color c1 = new Color(time.getCor1());
			g2d.setColor(brancoClaro);
			g2d.fillRoundRect(x - 10, y - 15, 100, 20, 0, 0);
			g2d.setColor(Color.BLACK);
			g2d.drawString("" + controleJogo.tempoRestanteJogoFormatado(), x,
					y);
			g2d.drawString(Lang.msg("de") + " ", x + 35, y);
			g2d.drawString(controleJogo.tempoJogoFormatado(), x + 55, y);
			y += 25;
			g2d.setColor(brancoClaro);
			g2d.fillRoundRect(x - 10, y - 15, 100, 20, 0, 0);
			g2d.setColor(Color.BLACK);
			g2d.drawString(Lang.msg("jogadas") + " "
					+ controleJogo.obterNumJogadas(time) + " " + Lang.msg("de")
					+ " " + controleJogo.getNumeroJogadas(), x, y);

			y += 25;
			if (controleJogo.isEsperandoJogadaOnline()
					|| controleJogo.isAnimando()) {
				g2d.setColor(OcilaCor.geraOcila("aguarde", amarelo));
				g2d.fillRoundRect(x - 10, y - 15, 100, 20, 0, 0);
				g2d.setColor(Color.BLACK);
				g2d.drawString("" + Lang.msg("aguarde"), x, y);
			} else {
				Color corFundo = ImageUtil.gerarCorTransparente(c1, 200);
				g2d.setColor(corFundo);
				g2d.fillRoundRect(x - 10, y - 15, 100, 20, 0, 0);
				int valor = (c1.getRed() + c1.getGreen() + c1.getBlue()) / 2;
				if (valor > 250) {
					g2d.setColor(Color.BLACK);
				} else {
					g2d.setColor(Color.WHITE);
				}
				String nmTime = "";
				if (time != null && !Util.isNullOrEmpty(time.getNome())) {
					for (int i = 0; i < time.getNome().length(); i++) {
						if (i > TAM_MAX_NM_TIME) {
							break;
						}
						nmTime += time.getNome().charAt(i);
					}
				}

				g2d.drawString("" + nmTime, x, y);
			}
		}
		y += 50;
		g2d.setColor(brancoClaro);
		g2d.fillRoundRect(x - 10, y - 40, 100, 45, 0, 0);
		g2d.setColor(Color.BLACK);
		Font fontOri = g2d.getFont();
		g2d.setFont(new Font(fontOri.getName(), fontOri.getStyle(), 48));
		g2d.drawString(" " + controleJogo.tempoJogadaRestanteJogoFormatado(),
				x - 5, y);

		g2d.setFont(fontOri);
	}

	private void desenhaInfoBotao(Graphics2D g2d) {

		int x = limitesViewPort.getBounds().x
				+ (limitesViewPort.getBounds().width - 150);
		int y = limitesViewPort.getBounds().y + 150;

		Botao obterBotao = controleJogo
				.obterBotao(controleJogo.getPontoPasando());
		if (obterBotao != null && obterBotao.getId() != 0) {
			botaoInfo = obterBotao;
		} else if (!controleJogo.isAnimando()
				&& controleJogo.getEventoAtual() != null
				&& controleJogo.getEventoAtual().getUltimoContato() != null
				&& controleJogo.getEventoAtual().getUltimoContato()
						.getId() != 0) {
			botaoInfo = controleJogo.getEventoAtual().getUltimoContato();
		}
		if (botaoInfo == null) {
			return;
		}
		if (botaoInfo.getId() == 0) {
			return;
		}
		Color cV1 = new Color(botaoInfo.getTime().getCor1());
		Color cV2 = new Color(botaoInfo.getTime().getCor2());
		Color corFundo = ImageUtil.gerarCorTransparente(cV1, 200);

		g2d.setColor(corFundo);
		g2d.fillRoundRect(x - 10, y - 15, 100, 20, 0, 0);
		int valor = (cV1.getRed() + cV1.getGreen() + cV1.getBlue()) / 2;
		if (valor > 250) {
			g2d.setColor(Color.BLACK);
		} else {
			g2d.setColor(Color.WHITE);
		}
		g2d.drawString(botaoInfo.getNumero() + " " + botaoInfo.getNome(), x, y);
		y += 22;
		g2d.setColor(corFundo);
		g2d.fillRoundRect(x - 10, y - 15, 70, 20, 0, 0);
		valor = (cV1.getRed() + cV1.getGreen() + cV1.getBlue()) / 2;
		if (valor > 250) {
			g2d.setColor(Color.BLACK);
		} else {
			g2d.setColor(Color.WHITE);
		}
		g2d.drawString(Lang.msg("forca"), x, y);
		corFundo = ImageUtil.gerarCorTransparente(cV2, 200);
		g2d.setColor(corFundo);
		g2d.fillRoundRect(x + 60, y - 15, 30, 20, 0, 0);
		valor = (cV2.getRed() + cV2.getGreen() + cV2.getBlue()) / 2;
		if (valor > 250) {
			g2d.setColor(Color.BLACK);
		} else {
			g2d.setColor(Color.WHITE);
		}
		g2d.drawString("" + botaoInfo.getForca(), x + 68, y);
		y += 22;
		corFundo = ImageUtil.gerarCorTransparente(cV1, 200);
		g2d.setColor(corFundo);
		g2d.fillRoundRect(x - 10, y - 15, 70, 20, 0, 0);
		valor = (cV1.getRed() + cV1.getGreen() + cV1.getBlue()) / 2;
		if (valor > 250) {
			g2d.setColor(Color.BLACK);
		} else {
			g2d.setColor(Color.WHITE);
		}
		g2d.drawString(Lang.msg("precisao"), x, y);
		corFundo = ImageUtil.gerarCorTransparente(cV2, 200);
		g2d.setColor(corFundo);
		g2d.fillRoundRect(x + 60, y - 15, 30, 20, 0, 0);
		valor = (cV2.getRed() + cV2.getGreen() + cV2.getBlue()) / 2;
		if (valor > 250) {
			g2d.setColor(Color.BLACK);
		} else {
			g2d.setColor(Color.WHITE);
		}
		g2d.drawString("" + botaoInfo.getPrecisao(), x + 68, y);
		y += 22;
		corFundo = ImageUtil.gerarCorTransparente(cV1, 200);
		g2d.setColor(corFundo);
		g2d.fillRoundRect(x - 10, y - 15, 70, 20, 0, 0);
		valor = (cV1.getRed() + cV1.getGreen() + cV1.getBlue()) / 2;
		if (valor > 250) {
			g2d.setColor(Color.BLACK);
		} else {
			g2d.setColor(Color.WHITE);
		}
		g2d.drawString(Lang.msg("defesa"), x, y);
		corFundo = ImageUtil.gerarCorTransparente(cV2, 200);
		g2d.setColor(corFundo);
		g2d.fillRoundRect(x + 60, y - 15, 30, 20, 0, 0);
		valor = (cV2.getRed() + cV2.getGreen() + cV2.getBlue()) / 2;
		if (valor > 250) {
			g2d.setColor(Color.BLACK);
		} else {
			g2d.setColor(Color.WHITE);
		}
		g2d.drawString("" + botaoInfo.getDefesa(), x + 68, y);
	}

	private void desenhaDica(Graphics2D g2d) {
		int x;
		int y;
		Font fontOri;
		x = limitesViewPort.getBounds().x
				+ (limitesViewPort.getBounds().width / 2);
		y = limitesViewPort.getBounds().y
				+ (limitesViewPort.getBounds().height - 60);
		String dica = controleJogo.getDica();
		if (Util.isNullOrEmpty(dica)) {
			controleJogo.mudarDica();
			dica = controleJogo.getDica();
		}

		Color corTexto = null;

		g2d.setColor(brancoClaro);
		corTexto = Color.BLACK;
		fontOri = g2d.getFont();
		if (!Util.isNullOrEmpty(dica) && !dica.startsWith("dica"))
			g2d.setFont(new Font(fontOri.getName(), fontOri.getStyle(), 48));

		String msg = Lang.msg(dica);
		int largura = 0;
		for (int i = 0; i < msg.length(); i++) {
			largura += g2d.getFontMetrics().charWidth(msg.charAt(i));
		}
		x -= largura / 2;

		if ("gol".equals(dica)) {
			g2d.setColor(OcilaCor.geraOcila("gol", amarelo));
		}

		if (!Util.isNullOrEmpty(dica) && !dica.startsWith("dica")) {
			g2d.fillRoundRect(x - 10, y - 40, largura + 20, 45, 0, 0);
		} else {
			g2d.fillRoundRect(x - 10, y - 15, largura + 20, 20, 0, 0);
		}

		g2d.setColor(corTexto);
		g2d.drawString("" + msg, x, y);

		g2d.setFont(fontOri);
	}

	private void desennhaCirculo(Graphics g2d) {
		if (controleJogo.getPontoBtnDirClicado() != null) {
			List l = GeoUtil.drawCircle(
					Util.inte(controleJogo.getPontoBtnDirClicado().x * zoom),
					Util.inte(controleJogo.getPontoBtnDirClicado().y * zoom),
					Util.inte(ConstantesMesa11.PERIMETRO * zoom));
			g2d.fillOval(
					Util.inte(controleJogo.getPontoBtnDirClicado().x * zoom),
					Util.inte(controleJogo.getPontoBtnDirClicado().y * zoom), 2,
					2);
			g2d.setColor(Color.BLACK);
			for (Iterator iterator = l.iterator(); iterator.hasNext();) {
				Point p = (Point) iterator.next();
				g2d.fillOval(p.x, p.y, 2, 2);
			}
		}
	}

	private void simulaRota(Graphics2D g2d, Map botoes) {
		if (!controleJogo.isJogoIniciado()) {
			return;
		}
		if (controleJogo.isAssistido())
			return;
		Stroke stroke = g2d.getStroke();
		g2d.setStroke(rota);
		if (controleJogo.getPontoClicado() != null
				&& controleJogo.getPontoPasando() != null && botoes != null) {
			g2d.setColor(Color.LIGHT_GRAY);
			Point p0 = (Point) controleJogo.getPontoClicado();
			Point pAtual = (Point) controleJogo.getPontoPasando();
			int distaciaEntrePontos = (int) GeoUtil.distaciaEntrePontos(p0,
					pAtual);
			if (distaciaEntrePontos <= 255) {
				g2d.setColor(new Color(distaciaEntrePontos, 255, 0, 100));
			} else if (distaciaEntrePontos > 255
					&& distaciaEntrePontos <= 510) {
				g2d.setColor(
						new Color(255, 255 - distaciaEntrePontos / 2, 0, 100));
			} else {
				g2d.setColor(new Color(255, 0, 0, 100));
			}
			for (Iterator iterator = botoes.keySet().iterator(); iterator
					.hasNext();) {
				Long id = (Long) iterator.next();
				Botao botao = (Botao) botoes.get(id);
				if (botao == null || botao.getCentro() == null) {
					continue;
				}
				List raioPonto = GeoUtil.drawBresenhamLine(p0,
						botao.getCentro());
				if (raioPonto.size() <= botao.getRaio()) {
					g2d.drawLine(Util.inte(p0.x * zoom), Util.inte(p0.y * zoom),
							Util.inte(pAtual.x * zoom),
							Util.inte(pAtual.y * zoom));
					if (botao instanceof Goleiro) {
						continue;
					}
					double angulo = GeoUtil.calculaAngulo(botao.getCentro(),
							pAtual, 270);
					Point destino = GeoUtil.calculaPonto(angulo, Util.inte(
							GeoUtil.drawBresenhamLine(p0, pAtual).size() * 10),
							botao.getCentro());
					g2d.drawLine(Util.inte(botao.getCentro().x * zoom),
							Util.inte(botao.getCentro().y * zoom),
							Util.inte(destino.x * zoom),
							Util.inte(destino.y * zoom));
					break;
				}
			}

		}
		g2d.setStroke(stroke);
	}

	private void desenhaGoleiro(Goleiro goleiro, Graphics g) {
		if (!limitesViewPort
				.contains(new Point(Util.inte(goleiro.getCentro().x * zoom),
						Util.inte(goleiro.getCentro().y * zoom)))) {
			return;
		}
		int botx = (int) (goleiro.getPosition().x * zoom);
		int boty = (int) (goleiro.getPosition().y * zoom);

		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.BLACK);
		Shape goleroShape = goleiro.getShape(zoom);
		g2d.fill(goleroShape);

		AffineTransform afZoom = new AffineTransform();
		AffineTransform afRotate = new AffineTransform();
		afZoom.setToScale(zoom, zoom);
		double rad = Math.toRadians(goleiro.getRotacao());
		afRotate.setToRotation(rad, 200, 200);

		BufferedImage botaoImg = (BufferedImage) controleJogo.getBotoesImagens()
				.get(goleiro.getId());
		BufferedImage newBuffer = new BufferedImage(400, 400,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics2d = (Graphics2D) newBuffer.getGraphics();
		setarHints(graphics2d);
		graphics2d.drawImage(botaoImg, 0, 170, null);
		graphics2d.dispose();

		BufferedImage zoomBuffer = new BufferedImage(400, 400,
				BufferedImage.TYPE_INT_ARGB);
		BufferedImage rotateBuffer = new BufferedImage(400, 400,
				BufferedImage.TYPE_INT_ARGB);

		AffineTransformOp op = new AffineTransformOp(afRotate,
				AffineTransformOp.TYPE_BILINEAR);
		op.filter(newBuffer, zoomBuffer);
		AffineTransformOp op2 = new AffineTransformOp(afZoom,
				AffineTransformOp.TYPE_BILINEAR);
		op2.filter(zoomBuffer, rotateBuffer);
		g.drawImage(rotateBuffer, botx, boty - Util.inte(170 * zoom), null);

	}

	private void desenhaBotao(Botao botao, Graphics g) {
		if (botao == null || botao.getPosition() == null)
			return;
		int diam = (int) ((ConstantesMesa11.DIAMENTRO_BOTAO) * zoom);
		int diamSomb = (int) ((ConstantesMesa11.DIAMENTRO_BOTAO + 6) * zoom);
		int botx = (int) (botao.getPosition().x * zoom);
		int boty = (int) (botao.getPosition().y * zoom);
		if (!limitesViewPort.contains(
				new Point(botx + botao.getRaio(), boty + botao.getRaio()))) {
			return;
		}
		AffineTransform affineTransform = AffineTransform.getScaleInstance(zoom,
				zoom);
		AffineTransformOp affineTransformOp = new AffineTransformOp(
				affineTransform, AffineTransformOp.TYPE_BILINEAR);
		BufferedImage botaoImg = (BufferedImage) controleJogo.getBotoesImagens()
				.get(botao.getId());
		if (botaoImg == null)
			return;
		BufferedImage zoomBuffer = null;
		if (botao.getId() != 0) {
			zoomBuffer = new BufferedImage(diam, diam,
					BufferedImage.TYPE_INT_ARGB);
			if (botao.equals(controleJogo.getBtnAssistido())) {
				g.setColor(OcilaCor.geraOcila("BtnAssistido", amarelo));
			} else {
				g.setColor(tansp);
			}
			g.fillOval((int) ((botao.getPosition().x - 2) * zoom),
					(int) ((botao.getPosition().y - 2) * zoom), diamSomb,
					diamSomb);
		} else {
			zoomBuffer = new BufferedImage((int) (botaoImg.getWidth() * zoom),
					(int) (botaoImg.getHeight() * zoom),
					BufferedImage.TYPE_INT_ARGB);
		}

		affineTransformOp.filter(botaoImg, zoomBuffer);

		g.drawImage(zoomBuffer, botx, boty, null);
	}

	private void desenhaCampo(Graphics2D g) {
		if (desenhaSplash) {
			return;
		}
		int x = 0;
		int y = 0;
		g.setColor(Color.LIGHT_GRAY);
		zoomedMesa = new Rectangle2D.Double(0, 0, ((LARGURA_MESA) * zoom),
				((ALTURA_MESA) * zoom));
		if (limitesViewPort.intersects(zoomedMesa))
			g.fill(zoomedMesa);
		/**
		 * Campo
		 */
		g.setColor(Color.white);
		zoomedBorda = new Rectangle2D.Double((BORDA_CAMPO * zoom),
				(BORDA_CAMPO * zoom),
				((LARGURA_MESA - DOBRO_BORDA_CAMPO) * zoom),
				((ALTURA_MESA - DOBRO_BORDA_CAMPO) * zoom));
		if (limitesViewPort.intersects(zoomedBorda))
			g.fill(zoomedBorda);

		g.setColor(green);
		zoomedGrama = new Rectangle2D.Double(((BORDA_CAMPO + LINHA) * zoom),
				((BORDA_CAMPO + LINHA) * zoom),
				((LARGURA_MESA - DOBRO_BORDA_CAMPO - DOBRO_LINHA) * zoom),
				((ALTURA_MESA - DOBRO_BORDA_CAMPO - DOBRO_LINHA) * zoom));

		int alturaBordaAtual = (BORDA_CAMPO + LINHA);
		int contFaixas = 0;
		AffineTransform affineTransform = AffineTransform.getScaleInstance(zoom,
				zoom);
		AffineTransformOp affineTransformOp = new AffineTransformOp(
				affineTransform, AffineTransformOp.TYPE_BILINEAR);
		if (zoom != oldZoom && grama1 != null && grama2 != null) {
			grama1Zoomed = new BufferedImage(
					Util.inte(grama1.getWidth() * zoom),
					Util.inte(grama1.getHeight() * zoom), grama1.getType());
			grama2Zoomed = new BufferedImage(
					Util.inte(grama2.getWidth() * zoom),
					Util.inte(grama1.getHeight() * zoom), grama2.getType());
			affineTransformOp.filter(grama1, grama1Zoomed);
			affineTransformOp.filter(grama2, grama2Zoomed);
		}
		oldZoom = zoom;
		for (int i = 0; i < FAIXAS; i++) {
			if (i % 2 == 0) {
				g.setColor(green2);
				zoomedFaixasGrama[contFaixas] = new Rectangle2D.Double(
						((BORDA_CAMPO + LINHA) * zoom),
						((alturaBordaAtual) * zoom),
						((LARGURA_MESA - DOBRO_BORDA_CAMPO - DOBRO_LINHA)
								* zoom),
						((ALTURA_FAIXA) * zoom));

				if (limitesViewPort.intersects(zoomedFaixasGrama[contFaixas])) {
					if (debug)
						g.fill(zoomedFaixasGrama[contFaixas]);
					if (limitesViewPort
							.intersects(zoomedFaixasGrama[contFaixas])) {
						g.drawImage(grama1Zoomed,
								Util.inte(zoomedFaixasGrama[contFaixas].getX()),
								Util.inte(zoomedFaixasGrama[contFaixas].getY()),
								null);
					}

				}
				contFaixas++;
				alturaBordaAtual += (ALTURA_FAIXA - LINHA);
				continue;
			} else {
				zoomedFaixasGrama[i] = new Rectangle2D.Double(
						((BORDA_CAMPO + LINHA) * zoom),
						((alturaBordaAtual) * zoom),
						((LARGURA_MESA - DOBRO_BORDA_CAMPO - DOBRO_LINHA)
								* zoom),
						((ALTURA_FAIXA) * zoom));

				if (limitesViewPort.intersects(zoomedFaixasGrama[i])) {
					g.drawImage(grama2Zoomed,
							Util.inte(zoomedFaixasGrama[i].getX()),
							Util.inte(zoomedFaixasGrama[i].getY()), null);
				}

			}
			alturaBordaAtual += (ALTURA_FAIXA);
		}
		Rectangle2D rect = new Rectangle2D.Double(
				((BORDA_CAMPO + LINHA) * zoom), ((alturaBordaAtual) * zoom),
				((LARGURA_MESA - DOBRO_BORDA_CAMPO - DOBRO_LINHA) * zoom),
				((ALTURA_FAIXA / 5) * zoom));
		if (grama2 != null) {
			BufferedImage gramaResto = new BufferedImage(grama2.getWidth(),
					(ALTURA_FAIXA / 5), grama2.getType());
			Graphics graphicsGrama = gramaResto.getGraphics();
			graphicsGrama.drawImage(grama2, 0, 0, null);
			g.drawImage(gramaResto, affineTransformOp, Util.inte(rect.getX()),
					Util.inte(rect.getY()));
		}
		/**
		 * Meia lua de cima
		 */
		g.setColor(Color.white);
		x = (LARGURA_MESA / 2) - RAIO_CENTRO / 2;
		y = BORDA_CAMPO;
		zoomedMeiaLuaCimaBorda = new Ellipse2D.Double((x * zoom), (y * zoom),
				(RAIO_CENTRO * zoom), (RAIO_CENTRO * zoom));
		// if (zoomedMeiaLuaCimaBorda.intersects((Rectangle) limitesViewPort))
		// g.fill(zoomedMeiaLuaCimaBorda);
		g.setColor(green);
		zoomedMeiaLuaCimaGrama = new Ellipse2D.Double(((x + LINHA) * zoom),
				((y + LINHA) * zoom), ((RAIO_CENTRO - DOBRO_LINHA) * zoom),
				((RAIO_CENTRO - DOBRO_LINHA) * zoom));
		if (zoomedMeiaLuaCimaGrama.intersects((Rectangle) limitesViewPort)) {
			// g.fill(zoomedMeiaLuaCimaGrama);
			BufferedImage bi = new BufferedImage(
					Util.inte(zoomedMeiaLuaCimaBorda.getWidth()),
					Util.inte(zoomedMeiaLuaCimaBorda.getHeight()),
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics = (Graphics2D) bi.getGraphics();
			graphics.fill(new Ellipse2D.Double((0 * zoom), (0 * zoom),
					(RAIO_CENTRO * zoom), (RAIO_CENTRO * zoom)));
			AlphaComposite composite = AlphaComposite
					.getInstance(AlphaComposite.CLEAR, 1);
			graphics.setComposite(composite);
			graphics.fill(new Ellipse2D.Double(((LINHA) * zoom),
					((LINHA) * zoom), ((RAIO_CENTRO - DOBRO_LINHA) * zoom),
					((RAIO_CENTRO - DOBRO_LINHA) * zoom)));
			graphics.fill(new Rectangle2D.Double(0, 0,
					((LARGURA_GDE_AREA) * zoom), ((ALTURA_GDE_AREA) * zoom)));
			g.drawImage(bi, Util.inte(zoomedMeiaLuaCimaBorda.getX()),
					Util.inte(zoomedMeiaLuaCimaBorda.getY()), null);

		}
		/**
		 * Meia lua de Baixo
		 */
		g.setColor(Color.white);
		x = (LARGURA_MESA / 2) - RAIO_CENTRO / 2;
		y = ALTURA_MESA - BORDA_CAMPO - RAIO_CENTRO;
		zoomedMeiaLuaBaixoBorda = new Ellipse2D.Double((x * zoom), (y * zoom),
				(RAIO_CENTRO * zoom), (RAIO_CENTRO * zoom));
		// if (zoomedMeiaLuaBaixoBorda.intersects((Rectangle) limitesViewPort))
		// g.fill(zoomedMeiaLuaBaixoBorda);
		g.setColor(green);
		zoomedMeiaLuaBaixoGrama = new Ellipse2D.Double(((x + LINHA) * zoom),
				((y + LINHA) * zoom), ((RAIO_CENTRO - DOBRO_LINHA) * zoom),
				((RAIO_CENTRO - DOBRO_LINHA) * zoom));
		if (zoomedMeiaLuaBaixoGrama.intersects((Rectangle) limitesViewPort)) {
			// g.fill(zoomedMeiaLuaBaixoGrama);
			BufferedImage bi = new BufferedImage(
					Util.inte(zoomedMeiaLuaBaixoBorda.getWidth()),
					Util.inte(zoomedMeiaLuaBaixoBorda.getHeight()),
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics = (Graphics2D) bi.getGraphics();
			graphics.fill(new Ellipse2D.Double((0), (0), (RAIO_CENTRO * zoom),
					(RAIO_CENTRO * zoom)));
			AlphaComposite composite = AlphaComposite
					.getInstance(AlphaComposite.CLEAR, 1);
			graphics.setComposite(composite);
			graphics.fill(new Ellipse2D.Double(((LINHA) * zoom),
					((LINHA) * zoom), ((RAIO_CENTRO - DOBRO_LINHA) * zoom),
					((RAIO_CENTRO - DOBRO_LINHA) * zoom)));
			graphics.fill(new Rectangle2D.Double(0,
					(RAIO_CENTRO - ALTURA_GDE_AREA) * zoom,
					((LARGURA_GDE_AREA) * zoom), ((ALTURA_GDE_AREA) * zoom)));
			g.drawImage(bi, Util.inte(zoomedMeiaLuaBaixoBorda.getX()),
					Util.inte(zoomedMeiaLuaBaixoBorda.getY()), null);

		}
		/**
		 * GdeArae Cima
		 */
		g.setColor(Color.white);
		zoomedGdeAreaCimaBorda = new Rectangle2D.Double(
				(ALTURA_GDE_AREA * zoom), (BORDA_CAMPO * zoom),
				((LARGURA_GDE_AREA) * zoom), ((ALTURA_GDE_AREA) * zoom));
		// if (limitesViewPort.intersects(zoomedGdeAreaCimaBorda))
		// g.fill(zoomedGdeAreaCimaBorda);
		g.setColor(green);
		zoomedGdeAreaCimaGrama = new Rectangle2D.Double(
				((ALTURA_GDE_AREA + LINHA) * zoom),
				((BORDA_CAMPO + LINHA) * zoom),
				((LARGURA_GDE_AREA - DOBRO_LINHA) * zoom),
				((ALTURA_GDE_AREA - DOBRO_LINHA) * zoom));
		if (limitesViewPort.intersects(zoomedGdeAreaCimaGrama)) {
			// g.fill(zoomedGdeAreaCimaGrama);
			BufferedImage bi = new BufferedImage(
					Util.inte(zoomedGdeAreaCimaBorda.getWidth()),
					Util.inte(zoomedGdeAreaCimaBorda.getHeight()),
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics = (Graphics2D) bi.getGraphics();
			graphics.fill(new Rectangle2D.Double(0, 0,
					((LARGURA_GDE_AREA) * zoom), ((ALTURA_GDE_AREA) * zoom)));
			AlphaComposite composite = AlphaComposite
					.getInstance(AlphaComposite.CLEAR, 1);
			graphics.setComposite(composite);
			graphics.fill(new Rectangle2D.Double(((LINHA) * zoom),
					((LINHA) * zoom), ((LARGURA_GDE_AREA - DOBRO_LINHA) * zoom),
					((ALTURA_GDE_AREA - DOBRO_LINHA) * zoom)));
			g.drawImage(bi, Util.inte(zoomedGdeAreaCimaBorda.getX()),
					Util.inte(zoomedGdeAreaCimaBorda.getY()), null);
		}
		/**
		 * GdeArae Baixo
		 */
		g.setColor(Color.white);
		zoomedGdeAreaBaixoBorda = new Rectangle2D.Double(
				(ALTURA_GDE_AREA * zoom),
				((ALTURA_MESA - BORDA_CAMPO - ALTURA_GDE_AREA) * zoom),
				((LARGURA_GDE_AREA) * zoom), ((ALTURA_GDE_AREA) * zoom));
		// if (limitesViewPort.intersects(zoomedGdeAreaBaixoBorda))
		// g.fill(zoomedGdeAreaBaixoBorda);
		g.setColor(green);
		zoomedGdeAreaBaixoGrama = new Rectangle2D.Double(
				((ALTURA_GDE_AREA + LINHA) * zoom),
				((ALTURA_MESA - BORDA_CAMPO - ALTURA_GDE_AREA + LINHA) * zoom),
				((LARGURA_GDE_AREA - DOBRO_LINHA) * zoom),
				((ALTURA_GDE_AREA - DOBRO_LINHA) * zoom));
		if (limitesViewPort.intersects(zoomedGdeAreaBaixoGrama)) {
			// g.fill(zoomedGdeAreaBaixoGrama);
			BufferedImage bi = new BufferedImage(
					Util.inte(zoomedGdeAreaBaixoBorda.getWidth()),
					Util.inte(zoomedGdeAreaBaixoBorda.getHeight()),
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics = (Graphics2D) bi.getGraphics();
			graphics.fill(new Rectangle2D.Double((0), (0),
					((LARGURA_GDE_AREA) * zoom), ((ALTURA_GDE_AREA) * zoom)));
			AlphaComposite composite = AlphaComposite
					.getInstance(AlphaComposite.CLEAR, 1);
			graphics.setComposite(composite);
			graphics.fill(new Rectangle2D.Double(((LINHA) * zoom),
					((LINHA) * zoom), ((LARGURA_GDE_AREA - DOBRO_LINHA) * zoom),
					((ALTURA_GDE_AREA - DOBRO_LINHA) * zoom)));
			g.drawImage(bi, Util.inte(zoomedGdeAreaBaixoBorda.getX()),
					Util.inte(zoomedGdeAreaBaixoBorda.getY()), null);

		}
		/**
		 * PQArae Cima
		 */
		g.setColor(Color.white);
		x = (LARGURA_PQ_AREA + LARGURA_PQ_AREA / 2);

		zoomedpqAreaCimaBorda = new Rectangle2D.Double((x * zoom),
				(BORDA_CAMPO * zoom), ((LARGURA_PQ_AREA) * zoom),
				((ALTURA_PQ_AREA) * zoom));
		// if (limitesViewPort.intersects(zoomedpqAreaCimaBorda))
		// g.fill(zoomedpqAreaCimaBorda);
		g.setColor(green);
		zoomedpqAreaCimaGrama = new Rectangle2D.Double(((x + LINHA) * zoom),
				((BORDA_CAMPO + LINHA) * zoom),
				((LARGURA_PQ_AREA - DOBRO_LINHA) * zoom),
				((ALTURA_PQ_AREA - DOBRO_LINHA) * zoom));
		if (limitesViewPort.intersects(zoomedpqAreaCimaGrama)) {
			// g.fill(zoomedpqAreaCimaGrama);
			BufferedImage bi = new BufferedImage(
					Util.inte(zoomedpqAreaCimaBorda.getWidth()),
					Util.inte(zoomedpqAreaCimaBorda.getHeight()),
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics = (Graphics2D) bi.getGraphics();
			graphics.fill(new Rectangle2D.Double(0, 0,
					((LARGURA_PQ_AREA) * zoom), ((ALTURA_PQ_AREA) * zoom)));
			AlphaComposite composite = AlphaComposite
					.getInstance(AlphaComposite.CLEAR, 1);
			graphics.setComposite(composite);
			graphics.fill(new Rectangle2D.Double(((LINHA) * zoom),
					((LINHA) * zoom), ((LARGURA_PQ_AREA - DOBRO_LINHA) * zoom),
					((ALTURA_PQ_AREA - DOBRO_LINHA) * zoom)));
			g.drawImage(bi, Util.inte(zoomedpqAreaCimaBorda.getX()),
					Util.inte(zoomedpqAreaCimaBorda.getY()), null);
		}
		/**
		 * PQArae Baixo
		 */
		g.setColor(Color.white);
		x = (LARGURA_PQ_AREA + LARGURA_PQ_AREA / 2);
		y = (ALTURA_MESA - BORDA_CAMPO - ALTURA_PQ_AREA);
		zoomedpqAreaBaixoBorda = new Rectangle2D.Double((x * zoom), (y * zoom),
				((LARGURA_PQ_AREA) * zoom), ((ALTURA_PQ_AREA) * zoom));
		// if (limitesViewPort.intersects(zoomedpqAreaBaixoBorda))
		// g.fill(zoomedpqAreaBaixoBorda);
		g.setColor(green);
		zoomedpqAreaBaixoGrama = new Rectangle2D.Double(((x + LINHA) * zoom),
				((y + LINHA) * zoom), ((LARGURA_PQ_AREA - DOBRO_LINHA) * zoom),
				((ALTURA_PQ_AREA - DOBRO_LINHA) * zoom));
		if (limitesViewPort.intersects(zoomedpqAreaBaixoGrama)) {
			// g.fill(zoomedpqAreaBaixoGrama);
			BufferedImage bi = new BufferedImage(
					Util.inte(zoomedpqAreaBaixoBorda.getWidth()),
					Util.inte(zoomedpqAreaBaixoBorda.getHeight()),
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics = (Graphics2D) bi.getGraphics();
			graphics.fill(new Rectangle2D.Double(0, 0,
					((LARGURA_PQ_AREA) * zoom), ((ALTURA_PQ_AREA) * zoom)));
			AlphaComposite composite = AlphaComposite
					.getInstance(AlphaComposite.CLEAR, 1);
			graphics.setComposite(composite);
			graphics.fill(new Rectangle2D.Double(((LINHA) * zoom),
					((LINHA) * zoom), ((LARGURA_PQ_AREA - DOBRO_LINHA) * zoom),
					((ALTURA_PQ_AREA - DOBRO_LINHA) * zoom)));
			g.drawImage(bi, Util.inte(zoomedpqAreaBaixoBorda.getX()),
					Util.inte(zoomedpqAreaBaixoBorda.getY()), null);
		}
		/**
		 * Circulo Centro
		 */
		g.setColor(Color.white);
		x = calculaXcentro();
		y = calculaYcentro();

		zoomedcentroBorda = new Ellipse2D.Double((x * zoom), (y * zoom),
				(RAIO_CENTRO * zoom), (RAIO_CENTRO * zoom));
		// if (zoomedcentroBorda.intersects((Rectangle) limitesViewPort))
		// g.fill(zoomedcentroBorda);

		g.setColor(green);
		zoomedcentroGrama = new Ellipse2D.Double(((x + LINHA) * zoom),
				((y + LINHA) * zoom), ((RAIO_CENTRO - DOBRO_LINHA) * zoom),
				((RAIO_CENTRO - DOBRO_LINHA) * zoom));
		if (zoomedcentroGrama.intersects((Rectangle) limitesViewPort)) {
			// g.fill(zoomedcentroGrama);
			BufferedImage bi = new BufferedImage(
					Util.inte(zoomedcentroBorda.getWidth()),
					Util.inte(zoomedcentroBorda.getHeight()),
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics = (Graphics2D) bi.getGraphics();
			graphics.fill(new Ellipse2D.Double((0), (0), (RAIO_CENTRO * zoom),
					(RAIO_CENTRO * zoom)));
			AlphaComposite composite = AlphaComposite
					.getInstance(AlphaComposite.CLEAR, 1);
			graphics.setComposite(composite);
			graphics.fill(new Ellipse2D.Double(((LINHA) * zoom),
					((LINHA) * zoom), ((RAIO_CENTRO - DOBRO_LINHA) * zoom),
					((RAIO_CENTRO - DOBRO_LINHA) * zoom)));
			g.drawImage(bi, Util.inte(zoomedcentroBorda.getX()),
					Util.inte(zoomedcentroBorda.getY()), null);

		}

		/**
		 * meio de campo
		 */
		g.setColor(Color.white);
		zoomedMeioCampoBorda = new Ellipse2D.Double(((BORDA_CAMPO) * zoom),
				((ALTURA_MESA / 2) * zoom),
				((LARGURA_MESA - DOBRO_BORDA_CAMPO) * zoom), (LINHA * zoom));
		if (zoomedMeioCampoBorda.intersects((Rectangle) limitesViewPort))
			g.fill(zoomedMeioCampoBorda);

		/**
		 * Penalti cima
		 */
		g.setColor(Color.white);
		zoomedPenaltiCima = new Ellipse2D.Double(((LARGURA_MESA / 2) * zoom),
				((BORDA_CAMPO + PENALTI) * zoom), (DOBRO_LINHA * zoom),
				(int) (DOBRO_LINHA * zoom));
		if (zoomedPenaltiCima.intersects((Rectangle) limitesViewPort))
			g.fill(zoomedPenaltiCima);
		/**
		 * Penalti Baixo
		 */
		g.setColor(Color.white);
		zoomedPenaltiBaixo = new Ellipse2D.Double(((LARGURA_MESA / 2) * zoom),
				((ALTURA_MESA - BORDA_CAMPO - PENALTI) * zoom),
				(DOBRO_LINHA * zoom), (DOBRO_LINHA * zoom));
		if (zoomedPenaltiBaixo.intersects((Rectangle) limitesViewPort))
			g.fill(zoomedPenaltiBaixo);
		/**
		 * Centro
		 */
		g.setColor(Color.white);
		zoomedCentro = new Ellipse2D.Double(((LARGURA_MESA / 2) * zoom),
				(((ALTURA_MESA - LINHA) / 2) * zoom), (DOBRO_LINHA * zoom),
				(DOBRO_LINHA * zoom));
		if (zoomedCentro.intersects((Rectangle) limitesViewPort))
			g.fill(zoomedCentro);
	}

	private void desenhaTravesGol(Graphics2D g) {
		if (!controleJogo.isJogoIniciado()) {
			return;
		}
		g.setColor(Color.white);
		Rectangle pequenaAreaCimaTemp = new Rectangle();
		pequenaAreaCimaTemp.setBounds(
				(int) ((LARGURA_PQ_AREA + LARGURA_PQ_AREA / 2) * zoom),
				(int) (BORDA_CAMPO * zoom), (int) ((LARGURA_PQ_AREA) * zoom),
				(int) ((ALTURA_PQ_AREA) * zoom));
		Rectangle pequenaAreaBaixoTemp = new Rectangle();
		pequenaAreaBaixoTemp
				.setBounds(
						(int) ((LARGURA_PQ_AREA + LARGURA_PQ_AREA / 2) * zoom),
						(int) ((ALTURA_MESA - BORDA_CAMPO - ALTURA_PQ_AREA)
								* zoom),
						(int) ((LARGURA_PQ_AREA) * zoom),
						(int) ((ALTURA_PQ_AREA) * zoom));
		Rectangle2D.Double hasteEsquerdaGolCimaTemp = new Rectangle2D.Double();
		hasteEsquerdaGolCimaTemp.setRect(
				pequenaAreaCimaTemp.getX() + (110 * zoom),
				pequenaAreaCimaTemp.getY() + (LINHA * zoom)
						- (pequenaAreaCimaTemp.getHeight() * .50),
				10 * zoom, pequenaAreaCimaTemp.getHeight() * .50);
		g.fill(hasteEsquerdaGolCimaTemp);
		Rectangle2D.Double hasteDireitaGolCimaTemp = new Rectangle2D.Double();
		hasteDireitaGolCimaTemp.setRect(
				pequenaAreaCimaTemp.getX() + pequenaAreaCimaTemp.getWidth()
						- (110 * zoom),
				pequenaAreaCimaTemp.getY() + (LINHA * zoom)
						- (pequenaAreaCimaTemp.getHeight() * .50),
				10 * zoom, pequenaAreaCimaTemp.getHeight() * .50);
		g.fill(hasteDireitaGolCimaTemp);
		Rectangle2D.Double hasteTopoGolCimaTemp = new Rectangle2D.Double();
		hasteTopoGolCimaTemp.setRect(hasteEsquerdaGolCimaTemp.getX(),
				hasteDireitaGolCimaTemp.getY(), hasteDireitaGolCimaTemp.getX()
						- hasteEsquerdaGolCimaTemp.getX(),
				10 * zoom);
		g.fill(hasteTopoGolCimaTemp);
		Color corRede = new Color(255, 255, 255, 100);
		Rectangle2D.Double areaGolCimaTemp = new Rectangle2D.Double();
		areaGolCimaTemp.setRect(hasteEsquerdaGolCimaTemp.getCenterX(),
				hasteTopoGolCimaTemp.getY(), hasteTopoGolCimaTemp.getWidth(),
				hasteEsquerdaGolCimaTemp.getHeight() - (LINHA * zoom));
		g.setColor(corRede);
		g.fill(areaGolCimaTemp);
		g.setColor(Color.white);
		Rectangle2D.Double hasteEsquerdaGolBaixoTemp = new Rectangle2D.Double();
		hasteEsquerdaGolBaixoTemp.setRect(hasteEsquerdaGolCimaTemp.getX(),
				pequenaAreaBaixoTemp.getY() - (LINHA * zoom)
						+ pequenaAreaBaixoTemp.getHeight(),
				10 * zoom, pequenaAreaBaixoTemp.getHeight()
						- (pequenaAreaBaixoTemp.getHeight() * .50));
		g.fill(hasteEsquerdaGolBaixoTemp);
		Rectangle2D.Double hasteDireitaGolBaixoTemp = new Rectangle2D.Double();
		hasteDireitaGolBaixoTemp.setRect(hasteDireitaGolCimaTemp.getX(),
				pequenaAreaBaixoTemp.getY() - (LINHA * zoom)
						+ pequenaAreaBaixoTemp.getHeight(),
				10 * zoom, pequenaAreaBaixoTemp.getHeight()
						- (pequenaAreaBaixoTemp.getHeight() * .50));
		g.fill(hasteDireitaGolBaixoTemp);
		Rectangle2D.Double hasteTopoGolBaixoTemp = new Rectangle2D.Double();
		hasteTopoGolBaixoTemp.setRect(hasteEsquerdaGolBaixoTemp.getX(),
				hasteDireitaGolBaixoTemp.getY()
						+ hasteDireitaGolBaixoTemp.getHeight(),
				hasteDireitaGolBaixoTemp.getX()
						- hasteEsquerdaGolBaixoTemp.getX()
						+ Util.inte(10 * zoom),
				10 * zoom);
		g.fill(hasteTopoGolBaixoTemp);

		Rectangle2D.Double areaGolBaixoTemp = new Rectangle2D.Double();
		areaGolBaixoTemp.setRect(hasteEsquerdaGolBaixoTemp.getCenterX(),
				hasteTopoGolBaixoTemp.getY()
						- hasteEsquerdaGolBaixoTemp.getHeight()
						+ (LINHA * zoom),
				hasteTopoGolCimaTemp.getWidth(),
				hasteEsquerdaGolBaixoTemp.getHeight() - (LINHA * zoom));
		g.setColor(corRede);
		g.fill(areaGolBaixoTemp);

	}

	private void desenhaFiguras(Graphics2D g) {
		if (!controleJogo.isJogoIniciado()) {
			return;
		}
		g.setColor(Color.cyan);
		g.fill(linhaGolBaixo);
		g.fill(linhaGolCima);

	}

	private int calculaYcentro() {
		return (ALTURA_MESA / 2) - RAIO_CENTRO / 2;
	}

	private int calculaXcentro() {
		return (LARGURA_MESA / 2) - RAIO_CENTRO / 2;
	}

	public Point pointCentro() {

		return new Point(
				(int) ((LARGURA_MESA / 2) * zoom)
						- (int) (DOBRO_LINHA * zoom / 2),
				(int) ((ALTURA_MESA / 2) * zoom)
						- (int) (DOBRO_LINHA * zoom / 2));
	}

	public boolean verificaIntersectsGol(Rectangle r) {
		return hasteDireitaGolBaixo.intersects(r)
				|| hasteDireitaGolCima.intersects(r)
				|| hasteEsquerdaGolBaixo.intersects(r)
				|| hasteEsquerdaGolCima.intersects(r)
				|| hasteTopoGolBaixo.intersects(r)
				|| hasteTopoGolCima.intersects(r);
	}

	public Point golBaixo() {
		Point p = new Point(Util.inte(getPenaltyBaixo().x),
				Util.inte(getPequenaAreaBaixo().getLocation().y
						+ getPequenaAreaBaixo().getHeight() + (LINHA * 2) - 5));
		return p;
	}

	public Point golCima() {
		Point p = new Point(Util.inte(getPenaltyCima().x),
				Util.inte(getPequenaAreaCima().getLocation().y - 15));
		return p;
	}

	public Rectangle getHasteTopoGolCima() {
		return hasteTopoGolCima;
	}

	public Rectangle getCampoCima() {
		return campoCima;
	}

	public Rectangle getCampoBaixo() {
		return campoBaixo;
	}

	public Rectangle getCampoCimaSemLinhas() {
		return campoCimaSemLinhas;
	}

	public Rectangle getCampoBaixoSemLinhas() {
		return campoBaixoSemLinhas;
	}

	public Rectangle getGrandeAreaCima() {
		return grandeAreaCima;
	}

	public Rectangle getGrandeAreaBaixo() {
		return grandeAreaBaixo;
	}

	public Rectangle getPequenaAreaCima() {
		return pequenaAreaCima;
	}

	public Rectangle getPequenaAreaBaixo() {
		return pequenaAreaBaixo;
	}

	public Rectangle getCentro() {
		return centro;
	}

	public Rectangle getPenaltyCima() {
		return penaltyCima;
	}

	public Rectangle getPenaltyBaixo() {
		return penaltyBaixo;
	}

	public static void main(String[] args) {
		for (int i = 0; i < FAIXAS; i++) {
			if (i % 2 == 0) {
				Logger.logar(i);
			}
		}
		// BufferedImage bufferedImage = new BufferedWriter(out)
		// for (int j = 0; j < 25; j++) {
		// g.drawImage(grama2, affineTransformOp, Util
		// .inte(zoomedFaixasGrama[i].getX() + j * grama2.getWidth()
		// * zoom), Util.inte(zoomedFaixasGrama[i].getY()));
		// }

	}

	public Rectangle getHasteDireitaGolCima() {
		return hasteDireitaGolCima;
	}

	public Rectangle getHasteEsquerdaGolCima() {
		return hasteEsquerdaGolCima;
	}

	public Rectangle getHasteDireitaGolBaixo() {
		return hasteDireitaGolBaixo;
	}

	public Rectangle getHasteEsquerdaGolBaixo() {
		return hasteEsquerdaGolBaixo;
	}

	private void desenhaLag(Graphics2D g2d) {
		if (!controleJogo.isJogoIniciado()) {
			return;
		}
		if (controleJogo.verificaLag()) {
			String msg = "LAG";
			int lag = controleJogo.getLag();
			if (contMostraLag >= 0 && contMostraLag < 20) {
				if (lag > 999) {
					lag = 999;
				}
				msg = " " + mil.format(lag);
			} else if (contMostraLag > 20) {
				contMostraLag = -20;
			}

			contMostraLag++;
			int largura = 0;
			for (int i = 0; i < msg.length(); i++) {
				largura += g2d.getFontMetrics().charWidth(msg.charAt(i));
			}

			Point pointDesenhaLag = new Point(
					limitesViewPort.getBounds().x
							+ (limitesViewPort.getBounds().width) - 120,
					Util.inte(limitesViewPort.getBounds().y
							+ limitesViewPort.getBounds().getHeight() - 90));
			g2d.setColor(brancoClaro);
			g2d.fillRoundRect(pointDesenhaLag.x, pointDesenhaLag.y, 65, 35, 0,
					0);
			Font fontOri = g2d.getFont();
			g2d.setFont(new Font(fontOri.getName(), Font.BOLD, 28));
			g2d.setColor(OcilaCor.porcentVermelho100Verde0(lag / 10));
			g2d.drawString(msg, pointDesenhaLag.x + 2, pointDesenhaLag.y + 26);
			g2d.setFont(fontOri);
		}

	}

	public boolean isDesenhaSplash() {
		return desenhaSplash;
	}

	public void setDesenhaSplash(boolean desenhaBkg) {
		this.desenhaSplash = desenhaBkg;
	}

}
