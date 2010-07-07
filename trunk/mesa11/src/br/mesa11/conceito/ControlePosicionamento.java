package br.mesa11.conceito;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.hibernate.Botao;
import br.hibernate.Goleiro;
import br.hibernate.Time;
import br.mesa11.visao.MesaPanel;
import br.nnpe.Util;

public class ControlePosicionamento {
	private ControleJogo controleJogo;
	private MesaPanel mesaPanel;

	private Map botoes;

	public ControlePosicionamento(ControleJogo controleJogo) {
		super();
		this.controleJogo = controleJogo;
		this.mesaPanel = controleJogo.getMesaPanel();
		this.botoes = controleJogo.getBotoes();
	}

	public void posicionaTimeCima(Time time, boolean centro) {
		int distHCima = (int) (mesaPanel.getCampoCima().getWidth() / 5);
		int distVCima = (int) (mesaPanel.getCampoCima().getHeight() / 2);
		int xpenal = mesaPanel.getPenaltyCima().x;
		List btnsTime = time.getBotoes();
		List btns = new ArrayList();
		for (Iterator iterator = btnsTime.iterator(); iterator.hasNext();) {
			Botao botao = (Botao) iterator.next();
			if (!(botao instanceof Goleiro) || !botao.isGoleiro()) {
				btns.add(botao);
			}
		}
		Botao botao1 = (Botao) btns.get(0);
		botao1.setCentroTodos(new Point(xpenal, distVCima));

		Botao botao2 = (Botao) btns.get(1);
		botao2.setCentroTodos(new Point(xpenal - (2 * distHCima), Util
				.inte(distVCima * 1.5)));

		Botao botao3 = (Botao) btns.get(2);
		;
		botao3.setCentroTodos(new Point(xpenal - (distHCima), Util
				.inte(distVCima * 1.5)));

		Botao botao4 = (Botao) btns.get(3);
		;
		botao4.setCentroTodos(new Point(xpenal + (2 * distHCima), Util
				.inte(distVCima * 1.5)));

		Botao botao5 = (Botao) btns.get(4);
		;
		botao5.setCentroTodos(new Point(xpenal + (distHCima), Util
				.inte(distVCima * 1.5)));

		Botao botao6 = (Botao) btns.get(5);
		;
		botao6.setCentroTodos(new Point(xpenal, Util.inte(distVCima * 1.5)));

		Botao botao7 = (Botao) btns.get(6);
		;
		botao7.setCentroTodos(new Point(xpenal - (2 * distHCima), Util
				.inte(distVCima * 2)));

		Botao botao9 = (Botao) btns.get(8);
		botao9.setCentroTodos(new Point(xpenal + (2 * distHCima), Util
				.inte(distVCima * 2)));
		if (centro) {
			Point c = mesaPanel.getCentro().getLocation();
			Botao botao8 = (Botao) btns.get(7);
			botao8.setCentroTodos(new Point(c.x
					- Util.inte(botao8.getDiamentro() * 1.5), c.y));

			Botao botao10 = (Botao) btns.get(9);
			botao10.setCentroTodos(new Point(c.x
					+ Util.inte(botao8.getDiamentro() * 1.5), c.y));
		} else {
			Botao botao8 = (Botao) btns.get(7);
			botao8.setCentroTodos(new Point(xpenal - (distHCima), Util
					.inte(distVCima * 2)));

			Botao botao10 = (Botao) btns.get(9);
			botao10.setCentroTodos(new Point(xpenal + (distHCima), Util
					.inte(distVCima * 2)));
		}
	}

	public void posicionaTimeBaixo(Time time, boolean centro) {
		int distHBaixo = (int) (mesaPanel.getCampoCima().getWidth() / 5);
		int distVBaixo = (int) (mesaPanel.getCampoCima().getHeight() / 2);
		int y = mesaPanel.golBaixo().y + 200;
		int xpenal = mesaPanel.getPenaltyCima().x;
		List btnsTime = time.getBotoes();
		List btns = new ArrayList();
		for (Iterator iterator = btnsTime.iterator(); iterator.hasNext();) {
			Botao botao = (Botao) iterator.next();
			if (!(botao instanceof Goleiro) || !botao.isGoleiro()) {
				btns.add(botao);
			}
		}
		Botao botao1 = (Botao) btns.get(0);
		botao1.setCentroTodos(new Point(xpenal, y - distVBaixo));

		Botao botao2 = (Botao) btns.get(1);
		botao2.setCentroTodos(new Point(xpenal - (2 * distHBaixo), y
				- Util.inte(distVBaixo * 1.5)));

		Botao botao3 = (Botao) btns.get(2);
		botao3.setCentroTodos(new Point(xpenal - (distHBaixo), y
				- Util.inte(distVBaixo * 1.5)));

		Botao botao4 = (Botao) btns.get(3);
		botao4.setCentroTodos(new Point(xpenal + (2 * distHBaixo), y
				- Util.inte(distVBaixo * 1.5)));

		Botao botao5 = (Botao) btns.get(4);
		botao5.setCentroTodos(new Point(xpenal + (distHBaixo), y
				- Util.inte(distVBaixo * 1.5)));

		Botao botao6 = (Botao) btns.get(5);
		botao6.setCentroTodos(new Point(xpenal, y - Util.inte(distVBaixo * 1.5)));

		Botao botao7 = (Botao) btns.get(6);
		botao7.setCentroTodos(new Point(xpenal - (2 * distHBaixo), y
				- Util.inte(distVBaixo * 2)));

		Botao botao9 = (Botao) btns.get(8);
		botao9.setCentroTodos(new Point(xpenal + (2 * distHBaixo), y
				- Util.inte(distVBaixo * 2)));

		if (centro) {
			Point c = mesaPanel.getCentro().getLocation();
			Botao botao8 = (Botao) btns.get(7);
			botao8.setCentroTodos(new Point(c.x
					- Util.inte(botao8.getDiamentro() * 1.5), c.y));

			Botao botao10 = (Botao) btns.get(9);
			botao10.setCentroTodos(new Point(c.x
					+ Util.inte(botao8.getDiamentro() * 1.5), c.y));
		} else {
			Botao botao8 = (Botao) btns.get(7);
			botao8.setCentroTodos(new Point(xpenal - (distHBaixo), y
					- Util.inte(distVBaixo * 2)));

			Botao botao10 = (Botao) btns.get(9);
			botao10.setCentroTodos(new Point(xpenal + (distHBaixo), y
					- Util.inte(distVBaixo * 2)));
		}

	}

}
