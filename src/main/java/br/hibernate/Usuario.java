package br.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "usuario")
public class Usuario extends Mesa11Dados {

	private static final long serialVersionUID = -3266703548432479215L;

	@Column(nullable = false, unique = true)
	private String login;

	@Column(nullable = false, unique = true)
	private String email;

	private String senha;

	private long ultimoLogon = 0;
	private long ultimaRecuperacao = 0;

	public long getUltimoLogon() {
		return ultimoLogon;
	}

	public void setUltimoLogon(long ultimoLogon) {
		this.ultimoLogon = ultimoLogon;
	}

	public long getUltimaRecuperacao() {
		return ultimaRecuperacao;
	}

	public void setUltimaRecuperacao(long ultimaRecuperacao) {
		this.ultimaRecuperacao = ultimaRecuperacao;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	@Override
	public String toString() {

		return login + " " + email;
	}

}
