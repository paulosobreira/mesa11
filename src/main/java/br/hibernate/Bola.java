package br.hibernate;

public class Bola extends Botao {

	public Bola(int i) {
		super(i);
	}

	public int getDiamentro() {
		return 30;
	}

	@Override
	public String toString() {
		return "Bola :" + getCentro();
	}

}
