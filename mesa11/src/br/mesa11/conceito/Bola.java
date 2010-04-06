package br.mesa11.conceito;

import br.hibernate.Botao;

public class Bola extends Botao {

	private transient int diamentro = 30;

	public Bola(int i) {
		super(i);
	}

	public int getDiamentro() {
		return diamentro;
	}

	public void setDiamentro(int diamentro) {
		this.diamentro = diamentro;
	}

}
