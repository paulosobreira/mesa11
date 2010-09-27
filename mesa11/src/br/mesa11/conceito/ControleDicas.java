package br.mesa11.conceito;

import br.nnpe.Util;

public class ControleDicas {

	private ControleJogo controleJogo;
	private int dicaAtual = 0;
	private int maxDiaca = 9;

	public ControleDicas(ControleJogo controleJogo) {
		super();
		this.controleJogo = controleJogo;
	}

	public void mudarDica() {
		int intervalo = Util.intervalo(0, maxDiaca);
		while (intervalo == dicaAtual) {
			intervalo = Util.intervalo(0, maxDiaca);
		}
		controleJogo.setDica("dica" + intervalo);
	}

}
