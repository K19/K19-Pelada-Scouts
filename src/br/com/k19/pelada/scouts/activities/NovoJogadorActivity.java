package br.com.k19.pelada.scouts.activities;

import java.util.regex.Pattern;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import br.com.k19.pelada.scouts.R;
import br.com.k19.pelada.scouts.dialogs.ErrorDialog;
import br.com.k19.pelada.scouts.dialogs.NoNetworkDialog;
import br.com.k19.pelada.scouts.entities.Jogador;
import br.com.k19.pelada.scouts.net.WebServices;
import br.com.k19.pelada.scouts.net.WebServices.Resposta;

public class NovoJogadorActivity extends FragmentActivity {

	private EditText editTextNomeCompleto;

	private EditText editTextEmail;

	private EditText editTextSenha;

	private EditText editTextConfirmacaoSenha;

	private Button buttonCadastrarJogador;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_novo_jogador);

		/* Recuperando elementos da View */
		recuperaElementosDaView();

		/* Ações */
		buttonCadastrarJogador.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (executaValidacao()) {
					ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
					NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
					if (networkInfo != null && networkInfo.isConnected()) {
						Jogador jogador = new Jogador();
						jogador.setApelido(editTextNomeCompleto.getText()
								.toString());
						jogador.setEmail(editTextEmail.getText().toString());
						jogador.setSenha(editTextSenha.getText().toString());

						new CadastraJogadorTask().execute(jogador);
					} else {
						NoNetworkDialog noNetworkDialog = new NoNetworkDialog();
						noNetworkDialog.show(getSupportFragmentManager(),
								"noNetworkDialog");
					}
				}
			}
		});
	}

	private boolean executaValidacao() {
		boolean valid = true;

		String nomeCompleto = this.editTextNomeCompleto.getText().toString();

		if (nomeCompleto.length() < 2 || nomeCompleto.length() > 20) {
			this.editTextNomeCompleto.setError(getResources().getText(
					R.string.jogador_apelido_validation_error));
			valid = false;
		} else {
			this.editTextNomeCompleto.setError(null);
		}

		String email = this.editTextEmail.getText().toString();
		Pattern pattern = Pattern
				.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

		if (!pattern.matcher(email).matches()) {
			this.editTextEmail.setError(getResources().getText(
					R.string.jogador_email_validation_error));
			valid = false;
		} else {
			this.editTextEmail.setError(null);
		}

		String senha = this.editTextSenha.getText().toString();

		if (senha.length() < 4 || senha.length() > 10) {
			this.editTextSenha.setError(getResources().getText(
					R.string.jogador_senha_validation_error));
			valid = false;
		} else {
			this.editTextSenha.setError(null);
		}

		String confirmacaoSenha = this.editTextConfirmacaoSenha.getText()
				.toString();

		if (!confirmacaoSenha.equals(senha)) {
			this.editTextConfirmacaoSenha.setError(getResources().getText(
					R.string.jogador_confirmacao_de_senha_validation_error));
			valid = false;
		} else {
			this.editTextConfirmacaoSenha.setError(null);
		}

		return valid;
	}

	private void recuperaElementosDaView() {
		editTextNomeCompleto = (EditText) findViewById(R.id.editTextNomeCompleto);
		editTextEmail = (EditText) findViewById(R.id.editTextEmail);
		editTextSenha = (EditText) findViewById(R.id.editTextSenha);
		editTextConfirmacaoSenha = (EditText) findViewById(R.id.editTextConfirmacaoSenha);
		buttonCadastrarJogador = (Button) findViewById(R.id.buttonCadastrarJogador);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getMenuInflater().inflate(R.menu.cadastra, menu);
		return true;
	}

	private class CadastraJogadorTask extends
			AsyncTask<Jogador, Void, Resposta> {
		private ProgressDialog progressDialog;

		@Override
		protected Resposta doInBackground(Jogador... params) {
			return WebServices.adicionaNovoJogador(params[0]);
		}

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog
					.show(NovoJogadorActivity.this,
							getResources().getText(
									R.string.novo_jogador_loading_dialog_title),
							getResources()
									.getText(
											R.string.novo_jogador_loading_dialog_message),
							true);
			progressDialog.setCancelable(true);
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Resposta resposta) {
			if (!resposta.getStatus().equals("ok")) {
				ErrorDialog errorDialog = new ErrorDialog();
				errorDialog.setMensagem(resposta.getMensagem());
				errorDialog.show(getSupportFragmentManager(), "errorDialog");
			} else {
				WebServices.token = resposta.getConteudo();
				Intent intent = new Intent(NovoJogadorActivity.this,
						HomeActivity.class);

				/* clear stack */
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					intent.addFlags(0x8000);
				}
				/* clear stack */

				startActivity(intent);
			}

			progressDialog.dismiss();
			super.onPostExecute(resposta);
		}
	}
}
