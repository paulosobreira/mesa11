package br.mesa11.conceito;

import br.mesa11.MesaPanel;

public class Mesa {

	private MesaPanel mesaPanel;

	public Mesa() {
		mesaPanel = new MesaPanel();

	}

	public MesaPanel getMesaPanel() {
		return mesaPanel;
	}

	public void setMesaPanel(MesaPanel mesaPanel) {
		this.mesaPanel = mesaPanel;
	}

}
