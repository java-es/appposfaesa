package pos.com.br.apppos;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import pos.com.br.modelo.Cliente;
import pos.com.br.modelo.Usuario;
import pos.com.br.util.Cache;
import pos.com.br.util.Conexao;

public class ListaClienteActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_cliente);
        buscarLista();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Conexao.conectado(this);
    }

    private void buscarLista() {
        ClienteAscTask task = new ClienteAscTask(this);
        task.execute(Cache.getUsuario());
    }

    private void carregaLista(List<Cliente> clientes) {
        ListView lv = (ListView) findViewById(R.id.listaCliente);
        ArrayAdapter<Cliente> adapter = new ArrayAdapter<Cliente>(this, android.R.layout.simple_list_item_1, android.R.id.text1, clientes);
        lv.setAdapter(adapter);
    }

    class ClienteAscTask extends AsyncTask<Usuario, Void, List<Cliente>> {

        private Activity activity;
        private ProgressDialog dialog;

        public ClienteAscTask(Activity activity) {
            this.activity = activity;
            dialog = new ProgressDialog(this.activity);
        }

        @Override
        protected List<Cliente> doInBackground(Usuario... usuarios) {
            return buscaListaCliente(usuarios[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog.setMessage("Carregando Lista de Funcionarios...");
            this.dialog.show();
        }

        @Override
        protected void onPostExecute(List<Cliente> clientes) {
            super.onPostExecute(clientes);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            carregaLista(clientes);
        }

        private List<Cliente> buscaListaCliente(Usuario usuario) {
            List<Cliente> clientes = new ArrayList<Cliente>();

            try {
                StringBuilder str = new StringBuilder();

                str.append("{");
                str.append("\"idCliente\":").append("null").append(",");
                str.append("\"nome\":").append("null").append(",");
                str.append("\"cnpj\": null,");
                str.append("\"email\":").append("null").append(",");
                str.append("\"telefone\":").append("null").append(",");
                str.append("\"usuario\": {");
                str.append("\"autenticado\": 0,");
                str.append("\"idUsuario\":").append(usuario.getIdUsuario()).append(",");
                str.append("\"login\":").append("\"").append(usuario.getLogin()).append("\",");
                str.append("\"senha\": null");
                str.append("}");
                str.append("}");

                String targetURL = getString(R.string.URL_LISTA_CLIENTE);//"http://192.168.1.140:8080/servicepos/resources/cliente/json/buscaTodos";
                // String filtro = "{\"login\":\"$l\",\"senha\":\"$s\"}";
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

                JSONArray jsonArray = new JSONArray(output);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Cliente cliente = new Cliente();
                    try {
                        cliente.setTelefone(jsonObject.get("telefone").toString());
                    } catch (Exception ex) {
                    }
                    cliente.setCpfCnpj(jsonObject.get("cnpj").toString());
                    cliente.setEmail(jsonObject.get("email").toString());
                    cliente.setNome(jsonObject.get("nome").toString());
                    cliente.setIdCliente(Integer.valueOf(jsonObject.get("idCliente").toString()));

                    clientes.add(cliente);
                }

            } catch (MalformedURLException e) {

            } catch (IOException e) {

            } catch (Exception ex) {

            }

            return clientes;
        }

    }


}
