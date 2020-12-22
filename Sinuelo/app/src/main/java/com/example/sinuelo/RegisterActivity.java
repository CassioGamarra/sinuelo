package com.example.sinuelo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    String token = "";
    EditText editCodBrinco, editNome, editDataNascimento, editPesoOriginal, editObservacoes;

    RequestQueue queue = null;
    ProgressBar progressBar;
    public static final String TAG = "REGISTER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Intent it = getIntent();
        token = it.getSerializableExtra("token").toString();

        editCodBrinco = findViewById(R.id.edtCodBrinco);
        editNome = findViewById(R.id.edtNome);
        editDataNascimento = findViewById(R.id.edtDataNascimento);
        editPesoOriginal = findViewById(R.id.edtPeso);
        editObservacoes = findViewById(R.id.edtObservacoes);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
    }

    public void cadastrar(View v) {
        if(editCodBrinco.getText().toString().equals("") || editNome.getText().toString().equals("") ||
           editDataNascimento.getText().toString().equals("") || editPesoOriginal.getText().toString().equals("") ||
           editObservacoes.getText().toString().equals("")) {
            Toast.makeText(RegisterActivity.this, "Preencha o todos os campos...", Toast.LENGTH_SHORT).show();
        } else {
            queue = Volley.newRequestQueue(this);

            String url = "http://192.168.0.4:3333/animal/register";
            //Cria os parâmetros da requisição
            Map<String, String> params = new HashMap<>();
            params.put("cod_brinco", editCodBrinco.getText().toString());
            params.put("nome", editNome.getText().toString());
            params.put("data_nasc", editDataNascimento.getText().toString());
            params.put("peso_original", editPesoOriginal.getText().toString());
            params.put("observacoes", editObservacoes.getText().toString());
            Log.d("Params", params.toString());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject json) {
                            progressBar.setVisibility(View.INVISIBLE);
                            try {
                                if(json.getInt("statusCode") == 200){
                                    final Intent it = new Intent(RegisterActivity.this, MainActivity.class);
                                    it.putExtra("token", token);
                                    Toast.makeText(RegisterActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            startActivity(it);
                                        }
                                    }, 3000);
                                }  else if (json.getInt("statusCode") == 422){
                                    Toast.makeText(RegisterActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                } else if(json.getInt("statusCode") == 403){
                                    Toast.makeText(RegisterActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) { //erro de requisição
                    progressBar.setVisibility(View.INVISIBLE);//esconde a barra de progresso
                    Toast.makeText(RegisterActivity.this, "Erro:" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("ERRO:", error.toString());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    String bearer = "Bearer ".concat(token);
                    Map<String, String> headersSys = super.getHeaders();
                    Map<String, String> headers = new HashMap<String, String>();
                    headersSys.remove("Authorization");
                    headers.put("Authorization", bearer);
                    headers.putAll(headersSys);
                    return headers;
                }
            };
            //da uma tag para a requisição
            jsonObjectRequest.setTag(TAG);
            //backOffMultiplier: multiplicador do timout de resposta do servidor, para esperar mais tempo nas tentativas subsequentes
            //1a tentativa: 10seg
            //2a tentativa: 10seg (t1) + 2*10seg = 30seg
            //3a tentativa: 30seg + 2*30seg = 150seg
            //...
            RetryPolicy policy = new DefaultRetryPolicy(10000, 1, 2);
            jsonObjectRequest.setRetryPolicy(policy);
            //adiciono a requisição na fila de requisições para que ela seja dispachada
            Log.d("string", jsonObjectRequest.toString());
            queue.add(jsonObjectRequest);
        }
    }

    public void cancelar(View v) {
        RegisterActivity.super.onBackPressed();
    }
}