package br.mesa11.conceito;

import java.awt.Point;
import java.util.List;
import java.util.Map;

import br.hibernate.Botao;
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

	public void posicionaTimeCima(String imagem, Long ids[], boolean centro) {
		int distHCima = (int) (mesaPanel.getCampoCima().getWidth() / 5);
		int distVCima = (int) (mesaPanel.getCampoCima().getHeight() / 2);
		int xpenal = mesaPanel.getPenaltyCima().x;

		Botao botao1 = new Botao(ids[0]);
		botao1.setCentro(new Point(xpenal, distVCima));
		botao1.setImagem(imagem);
		botoes.put(botao1.getId(), botao1);

		Botao botao2 = new Botao(ids[1]);
		botao2.setCentro(new Point(xpenal - (2 * distHCima), Util
				.inte(distVCima * 1.5)));
		botao2.setImagem(imagem);
		botoes.put(botao2.getId(), botao2);

		Botao botao3 = new Botao(ids[2]);
		botao3.setCentro(new Point(xpenal - (distHCima), Util
				.inte(distVCima * 1.5)));
		botao3.setImagem(imagem);
		botoes.put(botao3.getId(), botao3);

		Botao botao4 = new Botao(ids[3]);
		botao4.setCentro(new Point(xpenal + (2 * distHCima), Util
				.inte(distVCima * 1.5)));
		botao4.setImagem(imagem);
		botoes.put(botao4.getId(), botao4);

		Botao botao5 = new Botao(ids[4]);
		botao5.setCentro(new Point(xpenal + (distHCima), Util
				.inte(distVCima * 1.5)));
		botao5.setImagem(imagem);
		botoes.put(botao5.getId(), botao5);

		Botao botao6 = new Botao(ids[5]);
		botao6.setCentro(new Point(xpenal, Util.inte(distVCima * 1.5)));
		botao6.setImagem(imagem);
		botoes.put(botao6.getId(), botao6);

		Botao botao7 = new Botao(ids[6]);
		botao7.setCentro(new Point(xpenal - (2 * distHCima), Util
				.inte(distVCima * 2)));
		botao7.setImagem(imagem);
		botoes.put(botao7.getId(), botao7);

		Botao botao9 = new Botao(ids[8]);
		botao9.setCentro(new Point(xpenal + (2 * distHCima), Util
				.inte(distVCima * 2)));
		botao9.setImagem(imagem);
		botoes.put(botao9.getId(), botao9);
		if (centro) {
			Point c = mesaPanel.getCentro().getLocation();
			Botao botao8 = new Botao(ids[7]);
			botao8.setCentro(new Point(c.x
					- Util.inte(botao8.getDiamentro() * 1.5), c.y));
			botao8.setImagem(imagem);
			botoes.put(botao8.getId(), botao8);

			Botao botao10 = new Botao(ids[9]);
			botao10.setCentro(new Point(c.x
					+ Util.inte(botao8.getDiamentro() * 1.5), c.y));
			botao10.setImagem(imagem);
			botoes.put(botao10.getId(), botao10);
		} else {
			Botao botao8 = new Botao(ids[7]);
			botao8.setCentro(new Point(xpenal - (distHCima), Util
					.inte(distVCima * 2)));
			botao8.setImagem(imagem);
			botoes.put(botao8.getId(), botao8);

			Botao botao10 = new Botao(ids[9]);
			botao10.setCentro(new Point(xpenal + (distHCima), Util
					.inte(distVCima * 2)));
			botao10.setImagem(imagem);
			botoes.put(botao10.getId(), botao10);

		}
	}

	public void posicionaTimeBaixo(String imagem, Long ids[], boolean centro) {
		int distHBaixo = (int) (mesaPanel.getCampoCima().getWidth() / 5);
		int distVBaixo = (int) (mesaPanel.getCampoCima().getHeight() / 2);
		int y = mesaPanel.golBaixo().y + 200;
		int xpenal = mesaPanel.getPenaltyCima().x;

		Botao botao1 = new Botao(ids[0]);
		botao1.setCentro(new Point(xpenal, y - distVBaixo));
		botao1.setImagem(imagem);
		botoes.put(botao1.getId(), botao1);

		Botao botao2 = new Botao(ids[1]);
		botao2.setCentro(new Point(xpenal - (2 * distHBaixo), y
				- Util.inte(distVBaixo * 1.5)));
		botao2.setImagem(imagem);
		botoes.put(botao2.getId(), botao2);

		Botao botao3 = new Botao(ids[2]);
		botao3.setCentro(new Point(xpenal - (distHBaixo), y
				- Util.inte(distVBaixo * 1.5)));
		botao3.setImagem(imagem);
		botoes.put(botao3.getId(), botao3);

		Botao botao4 = new Botao(ids[3]);
		botao4.setCentro(new Point(xpenal + (2 * distHBaixo), y
				- Util.inte(distVBaixo * 1.5)));
		botao4.setImagem(imagem);
		botoes.put(botao4.getId(), botao4);

		Botao botao5 = new Botao(ids[4]);
		botao5.setCentro(new Point(xpenal + (distHBaixo), y
				- Util.inte(distVBaixo * 1.5)));
		botao5.setImagem(imagem);
		botoes.put(botao5.getId(), botao5);

		Botao botao6 = new Botao(ids[5]);
		botao6.setCentro(new Point(xpenal, y - Util.inte(distVBaixo * 1.5)));
		botao6.setImagem(imagem);
		botoes.put(botao6.getId(), botao6);

		Botao botao7 = new Botao(ids[6]);
		botao7.setCentro(new Point(xpenal - (2 * distHBaixo), y
				- Util.inte(distVBaixo * 2)));
		botao7.setImagem(imagem);
		botoes.put(botao7.getId(), botao7);

		Botao botao9 = new Botao(ids[8]);
		botao9.setCentro(new Point(xpenal + (2 * distHBaixo), y
				- Util.inte(distVBaixo * 2)));
		botao9.setImagem(imagem);
		botoes.put(botao9.getId(), botao9);

		if (centro) {
			Point c = mesaPanel.getCentro().getLocation();
			Botao botao8 = new Botao(ids[7]);
			botao8.setCentro(new Point(c.x
					- Util.inte(botao8.getDiamentro() * 1.5), c.y));
			botao8.setImagem(imagem);
			botoes.put(botao8.getId(), botao8);

			Botao botao10 = new Botao(ids[9]);
			botao10.setCentro(new Point(c.x
					+ Util.inte(botao8.getDiamentro() * 1.5), c.y));
			botao10.setImagem(imagem);
			botoes.put(botao10.getId(), botao10);
		} else {
			Botao botao8 = new Botao(ids[7]);
			botao8.setCentro(new Point(xpenal - (distHBaixo), y
					- Util.inte(distVBaixo * 2)));
			botao8.setImagem(imagem);
			botoes.put(botao8.getId(), botao8);

			Botao botao10 = new Botao(ids[9]);
			botao10.setCentro(new Point(xpenal + (distHBaixo), y
					- Util.inte(distVBaixo * 2)));
			botao10.setImagem(imagem);
			botoes.put(botao10.getId(), botao10);
		}

	}

}
