package br.hibernate;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class Mesa11Dados implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected Long id;

	@Column(nullable = false)
	private Date dataCriacao = new Date();

	@Column(nullable = false)
	private String loginCriador;

	public String getLoginCriador() {
		return loginCriador;
	}

	public void setLoginCriador(String loginCriador) {
		this.loginCriador = loginCriador;
	}

	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Mesa11Dados)) {
			return false;
		}
		Mesa11Dados algolDados = (Mesa11Dados) obj;
		return id.equals(algolDados.getId());
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

}
