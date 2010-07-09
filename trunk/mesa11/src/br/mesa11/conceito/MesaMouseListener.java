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
import br.mesa11.ConstantesMesa11;
import br.mesa11.visao.MesaPanel;
import br.nnpe.GeoUtil;
import br.nnpe.Util;

public class MesaMouseListener implements MouseListener {

	private ControleJogo controleJogo;
	private MesaPanel mesaPanel;
	private Map botoes;

	public MesaMouseListener(ControleJogo controleJogo) {
		super();
		this.controleJogo = controleJogo;
		this.mesaPanel = controleJogo.getMesaPanel();
		this.botoes = controleJogo.getBotoes();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (controleJogo.isAnimando() || controleJogo.getPontoClicado() == null
				&& controleJogo.getPontoPasando() == null
				|| (MouseEvent.BUTTON1 != e.getButton()) || botoes == null) {
			controleJogo.setPontoClicado(null);
			return;
		}
		Evento evento = new Evento();
		Point p1 = controleJogo.getPontoClicado();
		Point p2 = controleJogo.getPontoPasando();
		List reta = GeoUtil.drawBresenhamLine(p1, p2);
		Animacao animacao = null;
		for (Iterator iterator = botoes.keySet().iterator(); iterator.hasNext();) {
			Long id = (Long) iterator.next();
			Botao botao = (Botao) botoes.get(id);
			if (botao instanceof Goleiro) {
				Goleiro goleiro = (Goleiro) botao;
				if (goleiro.getShape(1).contains(p1)) {
					if (controleJogo.veririficaVez(goleiro)) {
						List retaGoleiro = GeoUtil.drawBresenhamLine(goleiro
								.getCentro(), p1);
						if (retaGoleiro.size() > (goleiro.getRaio() / 2)) {
							goleiro.setRotacao(GeoUtil.calculaAngulo(goleiro
									.getCentro(), p2, 0));
							evento.setPonto(p2);
							evento
									.setEventoCod(ConstantesMesa11.GOLEIRO_ROTACAO);
						} else {
							goleiro.setCentro(p2);
							evento.setPonto(p2);
							evento.setEventoCod(ConstantesMesa11.GOLEIRO_MOVEU);
						}
						controleJogo.setPontoClicado(null);
						return;
					}
				}
			}
			if (botao.getCentro() == null) {
				continue;
			}
			List raioPonto = GeoUtil.drawBresenhamLine(p1, botao.getCentro());
			if (raioPonto.size() <= botao.getRaio()) {
				if (!controleJogo.veririficaVez(botao)) {
					controleJogo.setPontoClicado(null);
					return;
				}
				if (botao instanceof Bola) {
					return;
				}
				controleJogo.setLateral(null);
				if (botao instanceof Goleiro) {
					return;
				}
				double angulo = GeoUtil.calculaAngulo(botao.getCentro(), p2,
						270);
				if (controleJogo.isChutaBola()) {
					angulo = GeoUtil.calculaAngulo(botao.getCentro(),
							controleJogo.getBola().getCentro(), 90);
				}

				Point destino = GeoUtil.calculaPonto(angulo, Util.inte(reta
						.size() * 10), botao.getCentro());
				botao.setDestino(destino);
				evento.setPonto(p1);
				evento.setBotaoEvento(botao);
				animacao = new Animacao();
				animacao.setObjetoAnimacao(botao);
				animacao.setPontosAnimacao(botao.getTrajetoria());
				controleJogo.setNumRecursoes(0);
				controleJogo.setEventoAtual(evento);
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
		controleJogo.setPontoClicado(null);
		controleJogo.processaJogada();
		Thread threadEventos = new Thread(new ControleEvento(controleJogo));
		threadEventos.start();

	}

	@Override
	public void mousePressed(MouseEvent e) {
		selecionaBotao(e);
	}

	private void selecionaBotao(MouseEvent e) {
		Point pontoClicado = new Point((int) (e.getPoint().x / mesaPanel.zoom),
				(int) (e.getPoint().y / mesaPanel.zoom));
		if (MouseEvent.BUTTON3 == e.getButton()) {
			controleJogo.setPontoBtnDirClicado(new Point(pontoClicado.x,
					pontoClicado.y));
		}
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
		selecionaBotao(e);
	}

}
