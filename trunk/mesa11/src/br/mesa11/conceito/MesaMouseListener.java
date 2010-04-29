package br.mesa11.conceito;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.hibernate.Bola;
import br.hibernate.Botao;
import br.hibernate.Goleiro;
import br.mesa11.visao.MesaPanel;
import br.nnpe.GeoUtil;
import br.nnpe.Util;

public class MesaMouseListener implements MouseListener {

	private ControleJogo controleJogo;
	private MesaPanel mesaPanel;
	private List jogada;
	private Map botoes;

	public MesaMouseListener(ControleJogo controleJogo) {
		super();
		this.controleJogo = controleJogo;
		this.mesaPanel = controleJogo.getMesaPanel();
		this.jogada = controleJogo.getJogada();
		this.botoes = controleJogo.getBotoes();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (controleJogo.getJogada().isEmpty() || controleJogo.isAnimando())
			return;
		Point p1 = (Point) jogada.get(0);
		Point p2 = (Point) jogada.get(jogada.size() - 1);
		List reta = GeoUtil.drawBresenhamLine(p1, p2);
		jogada.clear();
		jogada.addAll(reta);
		Animacao animacao = null;
		for (Iterator iterator = botoes.keySet().iterator(); iterator.hasNext();) {
			Long id = (Long) iterator.next();
			Botao botao = (Botao) botoes.get(id);
			if (botao instanceof Goleiro) {
				Goleiro goleiro = (Goleiro) botao;
				if (goleiro.getRetangulo(1).contains(p1)) {
					List retaGoleiro = GeoUtil.drawBresenhamLine(goleiro
							.getCentro(), p1);
					if (retaGoleiro.size() > (goleiro.getRaio() / 2)) {
						goleiro.setRotacao(GeoUtil.calculaAngulo(goleiro
								.getCentro(), p2, 0));
					} else {
						goleiro.setCentro(p2);
					}
					return;
				}
			}
			List raioPonto = GeoUtil.drawBresenhamLine(p1, botao.getCentro());
			if (raioPonto.size() <= botao.getRaio()) {
				if (botao instanceof Bola) {
					return;
				}
				controleJogo.setLateral(null);
				if (botao instanceof Goleiro) {
					return;
				}
				double angulo = GeoUtil
						.calculaAngulo(p1, botao.getCentro(), 90);
				if (controleJogo.isChutaBola()) {
					angulo = GeoUtil.calculaAngulo(botao.getCentro(),
							controleJogo.getBola().getCentro(), 90);
				}

				Point destino = GeoUtil.calculaPonto(angulo, Util.inte(reta
						.size() * 10), botao.getCentro());
				botao.setDestino(destino);
				animacao = new Animacao();
				animacao.setObjetoAnimacao(botao);
				animacao.setPontosAnimacao(botao.getTrajetoria());
				controleJogo.propagaColisao(animacao, botao);
				break;
			}

		}
		if (animacao == null) {
			return;
		}
		for (Iterator iterator = botoes.keySet().iterator(); iterator.hasNext();) {
			Long id = (Long) iterator.next();
			Botao botao = (Botao) botoes.get(id);
			if (botao.getCentroInicio() != null)
				botao.setCentro(botao.getCentroInicio());
		}
		controleJogo.getBotoesComThread().clear();
		Animador animador = new Animador(animacao, controleJogo);
		Thread thread = new Thread(animador);
		controleJogo.getBotoesComThread().put(animacao.getObjetoAnimacao(),
				thread);
		thread.start();
		controleJogo.setAnimando(true);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		Point pontoClicado = new Point((int) (e.getPoint().x / mesaPanel.zoom),
				(int) (e.getPoint().y / mesaPanel.zoom));
		controleJogo.setPontoClicado(pontoClicado);
		for (Iterator iterator = botoes.keySet().iterator(); iterator.hasNext();) {
			Long id = (Long) iterator.next();
			Botao botao = (Botao) botoes.get(id);
			if (botao instanceof Bola || botao instanceof Goleiro) {
				continue;
			}
			List raioPonto = GeoUtil.drawBresenhamLine(pontoClicado, botao
					.getCentro());
			if (raioPonto.size() <= botao.getRaio()) {
				controleJogo.setBotaoSelecionado(botao);
				break;
			}
		}
		jogada.clear();
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
