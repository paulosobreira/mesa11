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
		if (controleJogo.isJogoTerminado()) {
			return;
		}
		if (controleJogo.isAnimando() || controleJogo.getPontoClicado() == null
				&& controleJogo.getPontoPasando() == null
				|| (MouseEvent.BUTTON1 != e.getButton()) || botoes == null) {
			controleJogo.setPontoClicado(null);
			return;
		}
		if (controleJogo.isJogoOnlineCliente()
				&& !controleJogo.verificaVezOnline()) {
			controleJogo.setPontoClicado(null);
			return;
		}
		controleJogo.efetuarJogada();

	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (controleJogo.isJogoTerminado()) {
			return;
		}
		selecionaBotao(e);
	}

	private void selecionaBotao(MouseEvent e) {
		if (controleJogo.isJogoTerminado()) {
			return;
		}
		Point pontoClicado = new Point((int) (e.getPoint().x / mesaPanel.zoom),
				(int) (e.getPoint().y / mesaPanel.zoom));
		if (MouseEvent.BUTTON3 == e.getButton()) {
			controleJogo.setPontoBtnDirClicado(new Point(pontoClicado.x,
					pontoClicado.y));
			controleJogo.jogadaCPU();
		}
		controleJogo.setPontoClicado(pontoClicado);
		for (Iterator iterator = botoes.keySet().iterator(); iterator.hasNext();) {
			Long id = (Long) iterator.next();
			Botao botao = (Botao) botoes.get(id);
			if (botao instanceof Bola) {
				boolean areaGoleiroCima = false;
				Goleiro goleiroCima = controleJogo.obterGoleiroCima();
				double distaciaEntrePontosCima = GeoUtil.distaciaEntrePontos(
						botao.getCentro(), goleiroCima.getCentro());
				if (distaciaEntrePontosCima < goleiroCima.getDiamentro()) {
					areaGoleiroCima = true;
				}
				boolean areaGoleiroBaixo = false;
				Goleiro goleiroBaixo = controleJogo.obterGoleiroBaixo();
				double distaciaEntrePontosBaixo = GeoUtil.distaciaEntrePontos(
						botao.getCentro(), goleiroBaixo.getCentro());
				if (distaciaEntrePontosBaixo < goleiroBaixo.getDiamentro()) {
					areaGoleiroBaixo = true;
				}
				if (!areaGoleiroBaixo && !areaGoleiroCima) {
					continue;
				}
			}
			if (botao instanceof Goleiro) {
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
