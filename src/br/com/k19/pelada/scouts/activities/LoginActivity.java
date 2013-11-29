package br.com.k19.pelada.scouts.activities;

import android.app.ProgressDialog;
import android.content.Intent;
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
import br.com.k19.pelada.scouts.entities.Jogador;
import br.com.k19.pelada.scouts.net.WebServices;
import br.com.k19.pelada.scouts.net.WebServices.Resposta;

public class LoginActivity extends FragmentActivity {

	private EditText editTextEmail;
	private EditText editTextSenha;
	private Button buttonNovoJogador;
	private Button buttonEntrar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		/* Recuperando elementos da View */
		recuperaElementosDaView();

		/* Ações */
		buttonNovoJogador.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this,
						NovoJogadorActivity.class);
				LoginActivity.this.startActivity(intent);
			}
		});

		buttonEntrar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Jogador jogador = new Jogador();
				jogador.setEmail(editTextEmail.getText().toString());
				jogador.setSenha(editTextSenha.getText().toString());
				new LoginTask().execute(jogador);
			}
		});
	}

	private void recuperaElementosDaView() {
		editTextEmail = (EditText) findViewById(R.id.editTextEmail);
		editTextSenha = (EditText) findViewById(R.id.editTextSenha);
		buttonNovoJogador = (Button) findViewById(R.id.buttonNovoJogador);
		buttonEntrar = (Button) findViewById(R.id.buttonEntrar);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	private class LoginTask extends AsyncTask<Jogador, Void, Resposta> {
		private ProgressDialog progressDialog;

		@Override
		protected Resposta doInBackground(Jogador... params) {
			return WebServices.login(params[0]);
		}

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog
					.show(LoginActivity.this,
							getResources().getText(
									R.string.login_loading_dialog_title),
							getResources().getText(
									R.string.login_loading_dialog_message),
							true);
			progressDialog.setCancelable(true);
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Resposta resposta) {
			progressDialog.dismiss();

			if (!resposta.getStatus().equals("ok")) {
				ErrorDialog errorDialog = new ErrorDialog();
				errorDialog.setMensagem(resposta.getMensagem());
				errorDialog.show(getSupportFragmentManager(), "errorDialog");
			} else {
				WebServices.token = resposta.getConteudo();
				Intent intent = new Intent(LoginActivity.this,
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
			super.onPostExecute(resposta);
		}
	}
}
