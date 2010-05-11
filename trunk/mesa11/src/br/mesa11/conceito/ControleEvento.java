package br.mesa11.conceito;

import br.mesa11.ConstantesMesa11;

public class ControleEvento implements Runnable {

	private ControleJogo controleJogo;

	public ControleEvento(ControleJogo controleJogo) {
		super();
		this.controleJogo = controleJogo;
	}

	@Override
	public void run() {
		while (controleJogo.isAnimando()) {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Evento evento = controleJogo.getEventoAtual();
		if ((ConstantesMesa11.CONTATO_BOTAO_BOLA.equals(evento.getEventoCod()) || ConstantesMesa11.CONTATO_BOLA_BOTAO
				.equals(evento.getEventoCod()))
				&& evento.getUltimoContato().getId() != 0) {
			controleJogo.zeraJogadaTime(evento.getUltimoContato().getTime());
		} else if ((ConstantesMesa11.CONTATO_BOTAO_BOTAO.equals(evento
				.getEventoCod())
				&& !evento.isNaBola() && !evento.getBotaoEvento().getTime()
				.equals(evento.getUltimoContato().getTime()))) {
			controleJogo.falta(evento.getPonto(), evento.getUltimoContato());
		} else if (ConstantesMesa11.LATERAL.equals(evento.getEventoCod())) {
			controleJogo.porcessaLateral();
		} else if (!evento.isNaBola()) {
			controleJogo.reversaoJogada();
		}
		System.out.println("ProcessadorEvento" + controleJogo.getEventoAtual());

	}
}
