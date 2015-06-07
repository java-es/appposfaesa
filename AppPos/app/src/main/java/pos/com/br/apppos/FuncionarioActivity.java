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
import pos.com.br.modelo.Funcionario;
import pos.com.br.util.Cache;
import pos.com.br.util.Conexao;

public class FuncionarioActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_funcionario);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Conexao.conectado(this);
    }

    public void salvar_onClick(View view) {
        EditText txtNome = (EditText) findViewById(R.id.txtNome);
        EditText txtCpf = (EditText) findViewById(R.id.txtCpf);

        if (txtNome.getText() != null && !txtNome.getText().toString().isEmpty()
                && txtCpf.getText() != null && !txtCpf.getText().toString().isEmpty()) {
            EditText txtEmail = (EditText) findViewById(R.id.txtEmail);
            EditText txtTelefone = (EditText) findViewById(R.id.txtTelefone);
            EditText txtCep = (EditText) findViewById(R.id.txtCep);
            EditText txtEndereco = (EditText) findViewById(R.id.txtEndereco);
            EditText txtNumero = (EditText) findViewById(R.id.txtNumero);
            EditText txtBairro = (EditText) findViewById(R.id.txtBairro);
            EditText txtCidade = (EditText) findViewById(R.id.txtCidade);
            EditText txtUF = (EditText) findViewById(R.id.txtUF);

            Funcionario funcionario = new Funcionario();
            funcionario.setNome(txtNome.getText().toString());
            funcionario.setCpf(txtCpf.getText().toString());
            funcionario.setEmail(txtEmail.getText().toString());
            funcionario.setTelefone(txtTelefone.getText().toString());
            funcionario.setCep(txtCep.getText().toString());
            funcionario.setEndereco(txtEndereco.getText().toString());
            funcionario.setNumero(txtNumero.getText().toString());
            funcionario.setBairro(txtBairro.getText().toString());
            funcionario.setCidade(txtCidade.getText().toString());
            funcionario.setUf(txtUF.getText().toString());

            FuncionarioAscTask task = new FuncionarioAscTask(this);
            task.execute(funcionario);
        } else {
            if (txtNome.getText().toString().isEmpty()) {
                Toast.makeText(this, "Informe o nome do funcionario", Toast.LENGTH_LONG).show();
            }
            if (txtCpf.getText().toString().isEmpty()) {
                Toast.makeText(this, "Informe o cpf do funcionario", Toast.LENGTH_LONG).show();
            }
        }

    }

    private void chamarLista(boolean funcionarioAdicionado) {
        if (funcionarioAdicionado) {
            Toast.makeText(this, "Erro ao adicionar um funcionario", Toast.LENGTH_LONG).show();
        } else {
            startActivity(new Intent(this, MenuActivity.class));
        }
    }

    class FuncionarioAscTask extends AsyncTask<Funcionario, Void, Boolean> {

        private Activity activity;
        private ProgressDialog dialog;

        public FuncionarioAscTask(Activity activity) {
            this.activity = activity;
            dialog = new ProgressDialog(this.activity);
        }

        @Override
        protected Boolean doInBackground(Funcionario... funcionarios) {
            String login = Cache.getUsuario().getLogin();
            return addFuncionario(funcionarios[0], login);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog.setMessage("Salvando Aguarde..");
            this.dialog.show();
        }

        @Override
        protected void onPostExecute(Boolean funcionarioAdicionado) {
            super.onPostExecute(funcionarioAdicionado);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            chamarLista(funcionarioAdicionado);

        }

        private boolean addFuncionario(Funcionario funcionario, String login) {
            boolean salvo = false;
            if (funcionario == null) {
                return false;
            }
            try {
                StringBuilder str = new StringBuilder();

                str.append("{");
                if (funcionario.getEmail() != null && !funcionario.getEmail().isEmpty()) {
                    str.append("\"email\":").append("\"").append(funcionario.getEmail()).append("\",");
                } else {
                    str.append("\"email\":").append("null").append(",");
                }
                if (funcionario.getCpf() != null && !funcionario.getCpf().isEmpty()) {
                    str.append("\"cpf\":").append("\"").append(funcionario.getCpf()).append("\",");
                } else {
                    str.append("\"cpf\":").append("null").append(",");
                }
                str.append("\"endereco\": {");
                if (funcionario.getBairro() != null && !funcionario.getBairro().isEmpty()) {
                    str.append("\"bairro\":").append("\"").append(funcionario.getBairro()).append("\",");
                } else {
                    str.append("\"bairro\":").append("null").append(",");
                }
                if (funcionario.getCep() != null && !funcionario.getCep().isEmpty()) {
                    str.append("\"cep\":").append("\"").append(funcionario.getCep()).append("\",");
                } else {
                    str.append("\"cep\":").append("null").append(",");
                }
                if (funcionario.getCidade() != null && !funcionario.getCidade().isEmpty()) {
                    str.append("\"cidade\":").append("\"").append(funcionario.getCidade()).append("\",");
                } else {
                    str.append("\"cidade\":").append("null").append(",");
                }
                if (funcionario.getEndereco() != null && !funcionario.getEndereco().isEmpty()) {
                    str.append("\"endereco\":").append("\"").append(funcionario.getEndereco()).append("\",");
                } else {
                    str.append("\"endereco\":").append("null").append(",");
                }
                if (funcionario.getUf() != null && !funcionario.getUf().isEmpty()) {
                    str.append("\"estado\":").append("\"").append(funcionario.getUf()).append("\",");
                } else {
                    str.append("\"estado\":").append("null").append(",");
                }
                if (funcionario.getNumero() != null && !funcionario.getNumero().isEmpty()) {
                    str.append("\"numero\":").append("\"").append(funcionario.getNumero()).append("\"");
                } else {
                    str.append("\"numero\":").append("null");
                }
                str.append("},");
                str.append("\"idFuncionario\": null,");
                if (funcionario.getNome() != null && !funcionario.getNome().isEmpty()) {
                    str.append("\"nome\":").append("\"").append(funcionario.getNome()).append("\",");
                } else {
                    str.append("\"nome\":").append("null").append(",");
                }
                if (funcionario.getTelefone() != null && !funcionario.getTelefone().isEmpty()) {
                    str.append("\"telefone\":").append("\"").append(funcionario.getTelefone()).append("\",");
                } else {
                    str.append("\"telefone\":").append("null").append(",");
                }
                str.append("\"usuario\": {");
                str.append("\"autenticado\": 0,");
                str.append("\"idUsuario\": 0,");
                str.append("\"login\":").append("\"").append(login).append("\",");
                str.append("\"senha\": null");
                str.append("}");
                str.append("}");

                String targetURL = getString(R.string.URL_ADD_FUNCIONARIO);//"http://192.168.1.140:8080/servicepos/resources/funcionario/json/addFuncionario";

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
                salvo = Boolean.valueOf(jsonObject.getString("isErro"));
                return salvo;
            } catch (MalformedURLException e) {

            } catch (IOException e) {

            } catch (Exception ex) {

            }

            return salvo;
        }

    }
}
