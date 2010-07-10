package br.mesa11.conceito;

import br.hibernate.Time;
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
		Time timeCima = controleJogo.getTimeCima();
		Time timeBaixo = controleJogo.getTimeBaixo();
		if ((ConstantesMesa11.CONTATO_BOTAO_BOLA.equals(evento.getEventoCod()) || ConstantesMesa11.CONTATO_BOLA_BOTAO
				.equals(evento.getEventoCod()))
				&& evento.getUltimoContato().getId() != 0) {
			if (controleJogo.incrementaJogada()) {
				controleJogo
						.zeraJogadaTime(evento.getUltimoContato().getTime());
			} else {
				controleJogo.reversaoJogada();
			}
		} else if ((ConstantesMesa11.CONTATO_BOTAO_BOTAO.equals(evento
				.getEventoCod())
				&& !evento.isNaBola() && !evento.getBotaoEvento().getTime()
				.equals(evento.getUltimoContato().getTime()))) {
			controleJogo.falta(evento.getPonto(), evento.getUltimoContato());
			controleJogo.zerarJogadas();
		} else if (ConstantesMesa11.LATERAL.equals(evento.getEventoCod())) {
			controleJogo.porcessaLateral();
			controleJogo.zerarJogadas();
		} else if (!evento.isNaBola()) {
			controleJogo.reversaoJogada();
		} else if (ConstantesMesa11.GOLEIRO_DEFESA
				.equals(evento.getEventoCod())) {
			if (evento.getUltimoContato() != null
					&& evento.getUltimoContato().getTime() != null
					&& !controleJogo.verificaBolaPertoGoleiroTime(evento
							.getUltimoContato().getTime())) {
				controleJogo.reversaoJogada();
			}
		} else if (ConstantesMesa11.META_ESCANTEIO
				.equals(evento.getEventoCod())) {
			Time time = evento.getUltimoContato().getTime();
			if (timeCima.equals(time)) {
				if (controleJogo.getMesaPanel().getAreaEscateioCima()
						.intersects(
								controleJogo.getUltMetaEscanteio().getBounds())) {
					controleJogo.processarEscanteio(timeBaixo);
				} else if (controleJogo.getMesaPanel().getAreaEscateioBaixo()
						.intersects(
								controleJogo.getUltMetaEscanteio().getBounds())) {
					controleJogo.processarMeta(timeBaixo);
				}
			} else {
				if (controleJogo.getMesaPanel().getAreaEscateioCima()
						.intersects(
								controleJogo.getUltMetaEscanteio().getBounds())) {
					controleJogo.processarMeta(timeCima);
				} else if (controleJogo.getMesaPanel().getAreaEscateioBaixo()
						.intersects(
								controleJogo.getUltMetaEscanteio().getBounds())) {
					controleJogo.processarEscanteio(timeCima);
				}
			}
		} else if (ConstantesMesa11.GOL.equals(evento.getEventoCod())) {
			Time time = evento.getUltimoContato().getTime();
			if (timeCima.equals(time)) {
				if (controleJogo.getMesaPanel().getAreaGolCima().intersects(
						controleJogo.getUltGol().getBounds())) {
					controleJogo.processarGolContra(timeCima);
				} else if (controleJogo.getMesaPanel().getAreaGolBaixo()
						.intersects(controleJogo.getUltGol().getBounds())) {
					controleJogo.processarGol(timeCima);
				}
			} else {
				if (controleJogo.getMesaPanel().getAreaGolCima().intersects(
						controleJogo.getUltGol().getBounds())) {
					controleJogo.processarGol(timeBaixo);
				} else if (controleJogo.getMesaPanel().getAreaGolBaixo()
						.intersects(controleJogo.getUltGol().getBounds())) {
					controleJogo.processarGolContra(timeBaixo);
				}
			}
		}
		System.out.println("ProcessadorEvento" + controleJogo.getEventoAtual());

	}
}
