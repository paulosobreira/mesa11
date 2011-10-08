package br.hibernate;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class TimesCampeonatoMesa11 extends Mesa11Dados {

	@ManyToOne
	@JoinColumn(nullable = false)
	private Time time;
	@ManyToOne
	@JoinColumn(nullable = false)
	private CampeonatoMesa11 campeonatoMesa11;

	public TimesCampeonatoMesa11(int id, Time time) {
		setId(id);
		setTime(time);
	}

	public TimesCampeonatoMesa11() {
		// TODO Auto-generated constructor stub
	}

	public Time getTime() {
		return time;
	}

	public void setTime(Time time) {
		this.time = time;
	}

	public CampeonatoMesa11 getCampeonatoMesa11() {
		return campeonatoMesa11;
	}

	public void setCampeonatoMesa11(CampeonatoMesa11 campeonatoMesa11) {
		this.campeonatoMesa11 = campeonatoMesa11;
	}

	@Override
	public String toString() {
		if (getTime() != null) {
			return time.getNome();
		}
		return super.toString();
	}
}
