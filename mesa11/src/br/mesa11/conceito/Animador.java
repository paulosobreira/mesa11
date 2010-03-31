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

	public Animador(Animacao animacao, JPanel panel) {
		super();
		this.animacao = animacao;
		this.panel = panel;
	}

	@Override
	public void run() {
		animar(animacao);
	}

	private void animar(Animacao anim) {
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
					if (i % 3 == 0)
						Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else if (object instanceof Animacao) {
				Animacao animIn = (Animacao) object;
				Animador animador = new Animador(animIn, panel);
				Thread thread = new Thread(animador);
				thread.start();
			}
		}
	}

}
