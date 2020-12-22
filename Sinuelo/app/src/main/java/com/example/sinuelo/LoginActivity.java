package com.example.sinuelo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    RequestQueue queue = null;
    EditText editLogin, editSenha;
    ProgressBar progressBar;

    String login = "", senha = "";
    public static final String TAG = "LOGIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editLogin = findViewById(R.id.edtLogin);
        editSenha = findViewById(R.id.edtPassword);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
    }

    public void login(View v){
        progressBar.setVisibility(View.VISIBLE);

        login = editLogin.getText().toString();
        senha = editSenha.getText().toString();

        if(login.length() == 0) {
            Toast.makeText(LoginActivity.this, "Preencha o login...", Toast.LENGTH_SHORT).show();
        } else if(senha.length() == 0) {
            Toast.makeText(LoginActivity.this, "Preencha a senha...", Toast.LENGTH_SHORT).show();
        } else {
            queue = Volley.newRequestQueue(this);

            String url = "http://192.168.0.4:3333/login";
            //Cria os parâmetros da requisição
            Map<String, String> params = new HashMap<>();
            params.put("login", login);
            params.put("password", senha);

            JsonObjectRequest  jsonObjectRequest = new JsonObjectRequest(url, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject json) {
                            progressBar.setVisibility(View.INVISIBLE);
                            try {
                                if(json.getInt("statusCode") == 200){
                                    Intent it = new Intent(LoginActivity.this, MainActivity.class);
                                    it.putExtra("token", json.getString("token"));
                                    startActivity(it);
                                }  else if (json.getInt("statusCode") == 404){
                                    Toast.makeText(LoginActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                } else if(json.getInt("statusCode") == 403){
                                    Toast.makeText(LoginActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) { //erro de requisição
                    progressBar.setVisibility(View.INVISIBLE);//esconde a barra de progresso
                    Toast.makeText(LoginActivity.this, "Erro:" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("ERRO:", error.toString());
                }
            });
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
            Log.d("Request", jsonObjectRequest.toString());
            queue.add(jsonObjectRequest);
        }
    }
}