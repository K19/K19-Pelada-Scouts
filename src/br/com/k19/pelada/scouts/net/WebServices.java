package br.com.k19.pelada.scouts.net;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import br.com.k19.pelada.scouts.entities.Jogador;

public class WebServices {
	// private static final String BASE_URL =
	// "http://www.k19.com.br:8080/k19-pelada-scouts";
	private static final String BASE_URL = "http://192.168.1.101:8080/k19-pelada-scouts";

	private static final String JOGADORES_ADICIONA = BASE_URL + "/jogadores";
	private static final String LOGIN = BASE_URL + "/login";
	public static String token;

	private static Resposta doPostRequest(String urlString, byte[] outputBytes) {
		HttpURLConnection connection = null;

		Resposta resposta = new Resposta();

		try {
			URL url = new URL(urlString);
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestMethod("POST");
			connection.setRequestProperty("token", WebServices.token);
			connection.connect();

			OutputStream outputStream = connection.getOutputStream();
			outputStream.write(outputBytes);
			outputStream.close();

			/* conteúdo */
			Scanner scanner = new Scanner(connection.getInputStream());
			scanner.useDelimiter("$");
			if (scanner.hasNext()) {
				resposta.setConteudo(scanner.next());
				scanner.close();
			}
			scanner.close();

			/* status e mensagem */
			resposta.setStatus(connection.getHeaderField("status"));
			resposta.setMensagem(connection.getHeaderField("mensagem"));

			return resposta;

		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (IOException e) {
			e.printStackTrace();
			resposta.setStatus("error");
			resposta.setMensagem("Falha na comunicação com o servidor");

			return resposta;
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}

	}

	public static Resposta adicionaNovoJogador(Jogador jogador) {
		try {
			byte[] bytes = jogador.toJsonString().getBytes("UTF-8");
			return WebServices.doPostRequest(JOGADORES_ADICIONA, bytes);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public static Resposta login(Jogador jogador) {
		try {
			byte[] bytes = jogador.toJsonString().getBytes("UTF-8");
			return WebServices.doPostRequest(LOGIN, bytes);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public static class Resposta {

		private String status;
		private String mensagem;
		private String conteudo;

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getMensagem() {
			return mensagem;
		}

		public void setMensagem(String mensagem) {
			this.mensagem = mensagem;
		}

		public String getConteudo() {
			return conteudo;
		}

		public void setConteudo(String conteudo) {
			this.conteudo = conteudo;
		}
	}
}
