package pos.com.br.apppos;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import pos.com.br.modelo.Cliente;
import pos.com.br.util.Cache;
import pos.com.br.util.Conexao;

public class ClienteActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Conexao.conectado(this);
    }

    public void cpf_onclick(View view) {
        TextView lblCpfCnpj = (TextView) findViewById(R.id.lblCpfCnpjCliente);
        lblCpfCnpj.setText("CPF");

        EditText txtCpfCnpj = (EditText) findViewById(R.id.txtCpfCnpjCliente);
        txtCpfCnpj.setHint("Informe o CPF...");
    }

    public void cnpj_onclick(View view) {
        TextView lblCpfCnpj = (TextView) findViewById(R.id.lblCpfCnpjCliente);
        lblCpfCnpj.setText("CNPJ");

        EditText txtCpfCnpj = (EditText) findViewById(R.id.txtCpfCnpjCliente);
        txtCpfCnpj.setHint("Informe o CNPJ...");
    }

    public void salvar_onClick(View view) {
        EditText txtNome = (EditText) findViewById(R.id.TxtNomeCliente);
        EditText txtCpfCnpj = (EditText) findViewById(R.id.txtCpfCnpjCliente);
        if (txtNome.getText() != null && !txtNome.getText().toString().isEmpty() && txtCpfCnpj.getText() != null
                && !txtCpfCnpj.getText().toString().isEmpty()) {
            EditText txtEmail = (EditText) findViewById(R.id.txtEmailCliente);
            EditText txtTelefone = (EditText) findViewById(R.id.txtTelefoneCliente);
            Cliente cliente = new Cliente();
            cliente.setNome(txtNome.getText().toString());
            cliente.setCpfCnpj(txtCpfCnpj.getText().toString());
            cliente.setEmail(txtEmail.getText().toString());
            cliente.setTelefone(txtTelefone.getText().toString());

            ClienteAscTask task = new ClienteAscTask(this);
            task.execute(cliente);
        } else {
            if (txtNome.getText().toString().isEmpty()) {
                Toast.makeText(this, "Informe o nome do cliente", Toast.LENGTH_LONG).show();
            }
            if (txtCpfCnpj.getText().toString().isEmpty()) {
                Toast.makeText(this, "Informe o cnpj do cliente", Toast.LENGTH_LONG).show();
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

    class ClienteAscTask extends AsyncTask<Cliente, Void, Boolean> {

        private Activity activity;
        private ProgressDialog dialog;

        public ClienteAscTask(Activity activity) {
            this.activity = activity;
            dialog = new ProgressDialog(this.activity);
        }

        @Override
        protected Boolean doInBackground(Cliente... clientes) {
            String login = Cache.getUsuario().getLogin();
            return addCliente(clientes[0], login);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog.setMessage("Salvando Aguarde..");
            this.dialog.show();
        }

        @Override
        protected void onPostExecute(Boolean clienteAdicionado) {
            super.onPostExecute(clienteAdicionado);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            chamarLista(clienteAdicionado);
        }

        private boolean addCliente(Cliente funcionario, String login) {
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
                if (funcionario.getCpfCnpj() != null && !funcionario.getCpfCnpj().isEmpty()) {
                    str.append("\"cnpj\":").append("\"").append(funcionario.getCpfCnpj()).append("\",");
                } else {
                    str.append("\"cnpj\":").append("null").append(",");
                }

                str.append("\"idCliente\": null,");
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

                String targetURL = getString(R.string.URL_ADD_CLIENTE);//"http://192.168.1.140:8080/servicepos/resources/cliente/json/addCliente";

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
