package br.mesa11.conceito;

import java.awt.Point;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;

import br.hibernate.Botao;

public class Animador implements Runnable {

	private Animacao animacao;
	private JPanel panel;
	private boolean ativo;
	private ControleJogo controleJogo;

	public Animador(Animacao animacao, JPanel panel, ControleJogo controleJogo) {
		super();
		this.animacao = animacao;
		this.panel = panel;
		this.controleJogo = controleJogo;
	}

	@Override
	public void run() {
		animar(animacao);
		if (animacao != null)
			animacao.setValida(false);
	}

	private void animar(Animacao anim) {
		if (anim == null)
			return;
		if (!anim.isValida())
			return;
		Botao botao = anim.getObjetoAnimacao();
		List elements = anim.getPontosAnimacao();
		for (int i = 0; i < elements.size(); i++) {
			Object object = (Object) elements.get(i);
			if (object instanceof Point) {
				Point point = (Point) object;
				botao.setCentroInicio(point);
				botao.setCentro(point);
				panel.repaint();
				try {
					if (botao instanceof Bola) {
						if (i % 3 == 0) {
							Thread.sleep(5);
							controleJogo.atualizaCentro();
						}

					} else {
						if (i % 3 == 0)
							Thread.sleep(10);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else if (object instanceof Animacao) {
				Animacao animIn = (Animacao) object;
				Animador animador = new Animador(animIn, panel, controleJogo);
				Thread thread = new Thread(animador);
				thread.start();
			}
		}
	}
}
