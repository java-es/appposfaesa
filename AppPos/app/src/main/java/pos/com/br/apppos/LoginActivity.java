package pos.com.br.apppos;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import pos.com.br.modelo.Usuario;
import pos.com.br.util.Cache;
import pos.com.br.util.Conexao;

public class LoginActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Conexao.conectado(this);
    }

    public void entrar_onClick(View view) {
        boolean isLogin = true;

        EditText txtLogin = (EditText) findViewById(R.id.txtLogin);
        EditText txtSenha = (EditText) findViewById(R.id.txtSenha);

        if (txtLogin.getText().toString().isEmpty()) {
            isLogin = false;
            Toast.makeText(this, "Informe o Login", Toast.LENGTH_LONG).show();
        }
        if (txtSenha.getText().toString().isEmpty()) {
            isLogin = false;
            Toast.makeText(this, "Informe a Senha", Toast.LENGTH_LONG).show();
        }

        if (isLogin) {
            LoginAscTask task = new LoginAscTask(this);
            task.execute(txtLogin.getText().toString(), txtSenha.getText().toString());
        } else {
            txtLogin.setFocusable(true);
        }
    }

    public void menuCadastro(Usuario usuario) {
        if (usuario != null && usuario.getAutenticado() == 1) {
            Intent intent = new Intent(this, MenuActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Login ou Senha incorreta", Toast.LENGTH_LONG).show();
        }
    }

    class LoginAscTask extends AsyncTask<String, Void, Usuario> {
        private Activity activity;
        private ProgressDialog dialog;

        public LoginAscTask(Activity activity) {
            this.activity = activity;
            dialog = new ProgressDialog(this.activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog.setMessage("Autenticando Aguarde..");
            this.dialog.show();
        }

        @Override
        protected Usuario doInBackground(String... params) {

            return getUsuario(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(Usuario usuario) {
            super.onPostExecute(usuario);
            menuCadastro(usuario);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        private Usuario getUsuario(String login, String senha) {

            try {
                StringBuilder str = new StringBuilder();

                str.append("{");
                if (login != null && !login.isEmpty()) {
                    str.append("\"login\":").append("\"").append(login).append("\",");
                } else {
                    str.append("\"login\":").append("null").append(",");
                }
                if (senha != null && !senha.isEmpty()) {
                    str.append("\"senha\":").append("\"").append(senha).append("\"");
                }
                str.append("}");

                String targetURL = getString(R.string.URL_USUARIO_AUTENTICACAO);//"http://192.168.1.140:8080/servicepos/resources/usuario/json/autenticacao";

                URL targetUrl = new URL(targetURL);
                HttpURLConnection httpConnection = (HttpURLConnection) targetUrl.openConnection();
                httpConnection.setDoOutput(true);
                httpConnection.setRequestMethod("POST");
                httpConnection.setRequestProperty("Content-Type", "application/json");

                OutputStream outputStream = httpConnection.getOutputStream();
                outputStream.write(str.toString().getBytes());
                outputStream.flush();
                if (httpConnection.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : "
                            + httpConnection.getResponseCode());
                }
                BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(
                        (httpConnection.getInputStream())));
                String output = responseBuffer.readLine();
                httpConnection.disconnect();

                JSONObject jsonObject = new JSONObject(output);
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(Long.parseLong(jsonObject.getString("idUsuario")));
                usuario.setLogin(jsonObject.getString("login"));
                usuario.setSenha(jsonObject.getString("senha"));
                usuario.setAutenticado(Byte.valueOf(jsonObject.getString("autenticado")));

                Cache.setUsuario(usuario);
                return usuario;
            } catch (MalformedURLException e) {

            } catch (IOException e) {

            } catch (Exception ex) {

            }

            return null;
        }
    }


}
