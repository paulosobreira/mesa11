package br.mesa11.conceito;

import br.hibernate.Goleiro;
import br.hibernate.Time;
import br.mesa11.ConstantesMesa11;
import br.nnpe.Logger;
import br.nnpe.Util;

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
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Evento evento = controleJogo.getEventoAtual();
		Logger.logar(evento.toString());

		Time timeCima = controleJogo.getTimeCima();
		Time timeBaixo = controleJogo.getTimeBaixo();
		if ((ConstantesMesa11.CONTATO_BOTAO_BOLA.equals(evento.getEventoCod())
				|| ConstantesMesa11.CONTATO_BOLA_BOTAO.equals(evento
						.getEventoCod()) || ConstantesMesa11.CHUTE_GOLEIRO
				.equals(evento.getEventoCod()))
				&& evento.getUltimoContato().getId() != 0) {
			if (controleJogo.incrementaJogada()) {
				if (!controleJogo.timeJogadaVez().equals(
						evento.getUltimoContato().getTime())) {
					controleJogo.zerarJogadas();
				}
				controleJogo
						.zeraJogadaTime(evento.getUltimoContato().getTime());
			} else {
				controleJogo.zerarJogadas();
				controleJogo.reversaoJogada();

			}
		} else if ((ConstantesMesa11.CONTATO_BOTAO_BOTAO.equals(evento
				.getEventoCod()) && !evento.isNaBola() && !evento
				.getBotaoEvento().getTime()
				.equals(evento.getUltimoContato().getTime()))) {
			controleJogo.zerarJogadas();
			controleJogo.falta(evento.getPonto(), evento.getUltimoContato());
			if (Util.isNullOrEmpty(controleJogo.getDica())
					|| controleJogo.getDica().startsWith("dica")) {
				controleJogo.setDica("falta");
			}
		} else if (ConstantesMesa11.LATERAL.equals(evento.getEventoCod())) {
			controleJogo.porcessaLateral();
			controleJogo.setDica("lateral");
		} else if (!evento.isNaBola()) {
			controleJogo.reversaoJogada();
			controleJogo.setDica("reversao");
		} else if (ConstantesMesa11.GOLEIRO_DEFESA
				.equals(evento.getEventoCod())) {
			Logger.logar("GOLEIRO_DEFESA ultimo contato "
					+ evento.getUltimoContato());
			controleJogo.setDica("defesaGoleiro");
			if (evento.getUltimoContato() != null
					&& !(evento.getUltimoContato() instanceof Goleiro)
					&& evento.getUltimoContato().getTime() != null
					&& !controleJogo.verificaBolaPertoGoleiroTime(evento
							.getUltimoContato().getTime())) {
				controleJogo.reversaoJogada();
				controleJogo.setDica("reversao");
			}
			if (evento.getUltimoContato().getTime().obterGoleiro()
					.equals(evento.getUltimoContato())) {
				controleJogo.reversaoJogada();
				controleJogo.setDica("reversao");
			}
		} else if (ConstantesMesa11.META_ESCANTEIO
				.equals(evento.getEventoCod())) {
			Time time = evento.getUltimoContato().getTime();
			if (timeCima.equals(time)) {
				if (controleJogo
						.getMesaPanel()
						.getAreaEscateioCima()
						.intersects(
								controleJogo.getUltMetaEscanteio().getBounds())) {
					controleJogo.processarEscanteio(timeBaixo);
				} else if (controleJogo
						.getMesaPanel()
						.getAreaEscateioBaixo()
						.intersects(
								controleJogo.getUltMetaEscanteio().getBounds())) {
					controleJogo.processarMeta(timeBaixo);
				}
			} else {
				if (controleJogo
						.getMesaPanel()
						.getAreaEscateioCima()
						.intersects(
								controleJogo.getUltMetaEscanteio().getBounds())) {
					controleJogo.processarMeta(timeCima);
				} else if (controleJogo
						.getMesaPanel()
						.getAreaEscateioBaixo()
						.intersects(
								controleJogo.getUltMetaEscanteio().getBounds())) {
					controleJogo.processarEscanteio(timeCima);
				}
			}
		} else if (ConstantesMesa11.GOL.equals(evento.getEventoCod())) {
			Time time = evento.getUltimoContato().getTime();
			if (timeCima.equals(time)) {
				if (controleJogo.getMesaPanel().getAreaGolCima()
						.intersects(controleJogo.getUltGol().getBounds())) {
					controleJogo.processarGolContra(evento.getUltimoContato());
				} else if (controleJogo.getMesaPanel().getAreaGolBaixo()
						.intersects(controleJogo.getUltGol().getBounds())) {
					controleJogo.processarGol(evento.getUltimoContato());
				}
			} else {
				if (controleJogo.getMesaPanel().getAreaGolCima()
						.intersects(controleJogo.getUltGol().getBounds())) {
					controleJogo.processarGol(evento.getUltimoContato());
				} else if (controleJogo.getMesaPanel().getAreaGolBaixo()
						.intersects(controleJogo.getUltGol().getBounds())) {
					controleJogo.processarGolContra(evento.getUltimoContato());
				}
			}
		}
		controleJogo.configuraAnimacaoServidor();
	}
}
