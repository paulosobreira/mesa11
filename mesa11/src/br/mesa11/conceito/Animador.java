package br.mesa11.conceito;

import java.awt.Point;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import br.hibernate.Bola;
import br.hibernate.Botao;
import br.mesa11.visao.MesaPanel;
import br.nnpe.Logger;

public class Animador implements Runnable {

	private Animacao animacao;
	private MesaPanel mesaPanel;
	private boolean ativo;
	private ControleJogo controleJogo;
	private boolean raiz;

	public Animador(Animacao animacao, ControleJogo controleJogo) {
		super();
		this.animacao = animacao;
		this.mesaPanel = controleJogo.getMesaPanel();
		this.controleJogo = controleJogo;
	}

	@Override
	public void run() {
		try {
			animar(animacao);
		} finally {
			controleJogo.getBotoesComThread().remove(
					animacao.getObjetoAnimacao());
		}
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
				if (controleJogo.verificaForaDosLimites(point)) {
					return;
				}
				botao.setCentroInicio(point);
				botao.setCentro(point);

				try {
					if (botao instanceof Bola) {
						if (!controleJogo.verificaDentroCampo(botao)) {
							if (controleJogo.getLateral() == null) {
								controleJogo.setLateral(botao.getCentro());
								Logger.logar("lateral");
							}
						}
						if (i % 3 == 0) {
							controleJogo
									.centralizaBotao(controleJogo.getBola());
							Thread.sleep(10);
						}

					} else {
						if (i % 3 == 0) {
							Thread.sleep(15);
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else if (object instanceof Animacao) {
				Animacao animIn = (Animacao) object;
				Thread threadRodando = (Thread) controleJogo
						.getBotoesComThread().get(animIn.getObjetoAnimacao());
				if (threadRodando == null) {
					Animador animador = new Animador(animIn, controleJogo);
					Thread thread = new Thread(animador);
					controleJogo.getBotoesComThread().put(
							animIn.getObjetoAnimacao(), thread);
					thread.start();
				}
			}
		}
	}

	public static void main(String[] args) {
		double val = 100;
		val *= 0.2;
		System.out.println(val);
	}

	public boolean isRaiz() {
		return raiz;
	}

	public void setRaiz(boolean raiz) {
		this.raiz = raiz;
	}

}