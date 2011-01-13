package br.mesa11.conceito;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.hibernate.Bola;
import br.hibernate.Botao;
import br.hibernate.Goleiro;
import br.mesa11.ConstantesMesa11;
import br.mesa11.visao.MesaPanel;
import br.nnpe.GeoUtil;
import br.nnpe.Logger;

public class MesaMouseMotionListener implements MouseMotionListener {
	private ControleJogo controleJogo;
	private MesaPanel mesaPanel;

	public MesaMouseMotionListener(ControleJogo controleJogo) {
		this.controleJogo = controleJogo;
		this.mesaPanel = controleJogo.getMesaPanel();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		Point p = new Point((int) (e.getPoint().x / mesaPanel.zoom), (int) (e
				.getPoint().y / mesaPanel.zoom));
		if (controleJogo.getBotaoSelecionado() != null
				&& controleJogo.isCarregaBotao()) {
			controleJogo.getBotaoSelecionado().setCentro(p);
		}
		/**
		 * debug
		 */
		Map botoes = controleJogo.getBotoes();
		for (Iterator iterator = botoes.keySet().iterator(); iterator.hasNext();) {
			Long id = (Long) iterator.next();
			Botao botao = (Botao) botoes.get(id);
			if (botao instanceof Goleiro) {
				Goleiro goleiro = (Goleiro) botao;
				if (goleiro.getShape(1).contains(p)) {
					Logger.logar("Passando por Goleiro " + botao);
					double retaGoleiro = GeoUtil.distaciaEntrePontos(goleiro
							.getCentro(), p);
					if (retaGoleiro > (goleiro.getRaio() / 2)) {
						Logger.logar("Goleiro Rotacao " + botao);
					} else {
						Logger.logar("Goleiro Movimento " + botao);
					}
				}
			} else {
				List raioPonto = GeoUtil
						.drawBresenhamLine(p, botao.getCentro());
				if (raioPonto.size() <= botao.getRaio()) {
					//Logger.logar("Passando por " + botao);
					break;
				}
			}
		}
		controleJogo.setPontoPasando(p);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (controleJogo.isJogoTerminado()) {
			return;
		}
		Point p = new Point((int) (e.getPoint().x / mesaPanel.zoom), (int) (e
				.getPoint().y / mesaPanel.zoom));
		controleJogo.setPontoPasando(p);
	}

}
