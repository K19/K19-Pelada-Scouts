package br.com.k19.pelada.scouts.entities;

public class Jogador {

	private Long id;

	private String apelido;

	private String email;

	private String senha;

	public String toJsonString() {
		StringBuilder sb = new StringBuilder();

		sb.append("{");
		sb.append("\"id\": \"").append(this.id).append("\",");
		sb.append("\"apelido\": \"").append(this.apelido).append("\",");
		sb.append("\"email\": \"").append(this.email).append("\",");
		sb.append("\"senha\": \"").append(this.senha).append("\"");
		sb.append("}");

		return sb.toString();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getApelido() {
		return apelido;
	}

	public void setApelido(String apelido) {
		this.apelido = apelido;
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
