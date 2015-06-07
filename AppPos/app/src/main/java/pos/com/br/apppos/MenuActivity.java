package pos.com.br.apppos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuActivity extends Activity {

    private List<Map<String, String>> crudList = new ArrayList<Map<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        initList();
        ListView lv = (ListView) findViewById(R.id.menuListView);
        SimpleAdapter simpleAdpt = new SimpleAdapter(this, crudList, android.R.layout.simple_list_item_1, new String[]{"CRUD"}, new int[]{android.R.id.text1});
        lv.setAdapter(simpleAdpt);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        startActivity(new Intent(MenuActivity.this, FuncionarioActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(MenuActivity.this, ListaFuncionarioActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(MenuActivity.this, ClienteActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(MenuActivity.this, ListaClienteActivity.class));
                        break;
                }
            }
        });

    }

    private void initList() {
        crudList.add(createPlanet("CRUD", "Cadastro Funcionario"));
        crudList.add(createPlanet("CRUD", "Lista Funcionarios"));
        crudList.add(createPlanet("CRUD", "Cadastro Cliente"));
        crudList.add(createPlanet("CRUD", "Lista Clientes"));

    }

    private HashMap<String, String> createPlanet(String key, String name) {
        HashMap<String, String> crud = new HashMap<String, String>();
        crud.put(key, name);
        return crud;
    }

}
