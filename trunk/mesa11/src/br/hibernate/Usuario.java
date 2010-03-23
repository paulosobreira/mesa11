package br.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Usuario extends Mesa11Dados {

	private static final long serialVersionUID = -3266703548432479215L;

	@Column(nullable = false, unique = true)
	private String login;

	@Column(nullable = false, unique = true)
	private String email;

	private String senha;

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

}
