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
import pos.com.br.modelo.Funcionario;
import pos.com.br.modelo.Usuario;
import pos.com.br.util.Cache;
import pos.com.br.util.Conexao;

public class ListaFuncionarioActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_funcionario);
        buscarLista();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Conexao.conectado(this);
    }

    private void buscarLista() {
        FuncionarioAscTask task = new FuncionarioAscTask(this);
        task.execute(Cache.getUsuario());
    }

    private void carregaLista(List<Funcionario> funcionarios) {
        ListView lv = (ListView) findViewById(R.id.listaFuncionario);
        ArrayAdapter<Funcionario> adapter = new ArrayAdapter<Funcionario>(this, android.R.layout.simple_list_item_1, android.R.id.text1, funcionarios);
        lv.setAdapter(adapter);
    }

    class FuncionarioAscTask extends AsyncTask<Usuario, Void, List<Funcionario>> {

        private Activity activity;
        private ProgressDialog dialog;

        public FuncionarioAscTask(Activity activity) {
            this.activity = activity;
            dialog = new ProgressDialog(this.activity);
        }

        @Override
        protected List<Funcionario> doInBackground(Usuario... usuarios) {
            return buscaListaFuncionario(usuarios[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog.setMessage("Carregando Lista de Funcionarios...");
            this.dialog.show();
        }

        @Override
        protected void onPostExecute(List<Funcionario> funcionarios) {
            super.onPostExecute(funcionarios);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            carregaLista(funcionarios);
        }

        private List<Funcionario> buscaListaFuncionario(Usuario usuario) {
            List<Funcionario> funcionarios = new ArrayList<Funcionario>();

            try {
                StringBuilder str = new StringBuilder();

                str.append("{");
                str.append("\"email\":").append("null").append(",");
                str.append("\"cpf\":").append("null").append(",");
                str.append("\"endereco\": {");
                str.append("\"bairro\":").append("null").append(",");
                str.append("\"cep\":").append("null").append(",");
                str.append("\"cidade\":").append("null").append(",");
                str.append("\"endereco\":").append("null").append(",");
                str.append("\"estado\":").append("null").append(",");
                str.append("\"numero\":").append("null");
                str.append("},");
                str.append("\"idFuncionario\": null,");
                str.append("\"nome\":").append("null").append(",");
                str.append("\"telefone\":").append("null").append(",");
                str.append("\"usuario\": {");
                str.append("\"autenticado\": 0,");
                str.append("\"idUsuario\":").append(usuario.getIdUsuario()).append(",");
                str.append("\"login\":").append("\"").append(usuario.getLogin()).append("\",");
                str.append("\"senha\": null");
                str.append("}");
                str.append("}");

                String targetURL = getString(R.string.URL_LISTA_FUNCIONARIO);//"http://192.168.1.140:8080/servicepos/resources/funcionario/json/buscaTodos";

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
                    Funcionario func = new Funcionario();
                    func.setCpf(jsonObject.get("cpf").toString());
                    func.setEmail(jsonObject.get("email").toString());
                    func.setIdFuncionario(Integer.valueOf(jsonObject.get("idFuncionario").toString()));
                    func.setNome(jsonObject.get("nome").toString());
                    try {
                        func.setTelefone(jsonObject.get("telefone").toString());
                    } catch (Exception ex) {
                    }

                    try {
                        JSONObject jsonEndereco = jsonObject.getJSONObject("endereco");
                        func.setBairro(jsonEndereco.get("bairro").toString());
                        func.setCep(jsonEndereco.get("cep").toString());
                        func.setCidade(jsonEndereco.get("cidade").toString());
                        func.setEndereco(jsonEndereco.get("endereco").toString());
                        func.setUf(jsonEndereco.get("estado").toString());
                        func.setNumero(jsonEndereco.get("numero").toString());
                    } catch (Exception ex) {
                    }


                    funcionarios.add(func);
                }

            } catch (MalformedURLException e) {

            } catch (IOException e) {

            } catch (Exception ex) {

            }

            return funcionarios;
        }

    }

}
