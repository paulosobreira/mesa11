package br.hibernate;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class JogadoresCampeonatoMesa11 extends Mesa11Dados {

	@ManyToOne
	@JoinColumn(nullable = false)
	private Usuario usuario;

	@JoinColumn(nullable = false)
	private CampeonatoMesa11 campeonatoMesa11;

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public CampeonatoMesa11 getCampeonatoMesa11() {
		return campeonatoMesa11;
	}

	public void setCampeonatoMesa11(CampeonatoMesa11 campeonatoMesa11) {
		this.campeonatoMesa11 = campeonatoMesa11;
	}

}
