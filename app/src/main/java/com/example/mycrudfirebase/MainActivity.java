package com.example.mycrudfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.example.mycrudfirebase.modelo.Pessoa;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    EditText edtNome, edtEmail;
    ListView listV_dados;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private List<Pessoa> listPessoa = new ArrayList<Pessoa>();
    private ArrayAdapter<Pessoa> arrayAdapterPessoa;

    Pessoa pessoaSelecionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edtNome = (EditText)findViewById(R.id.edtNome);
        edtEmail = (EditText)findViewById(R.id.edtEmail);
        listV_dados = (ListView)findViewById(R.id.listV_dados);

        inicializarFirebase();
        eventoDatabase();

        listV_dados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pessoaSelecionada = (Pessoa)parent.getItemAtPosition(position);
                edtNome.setText(pessoaSelecionada.getNome());
                edtEmail.setText(pessoaSelecionada.getEmail());
            }


        });

    }

    private void eventoDatabase() {
        databaseReference.child("Pessoa").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listPessoa.clear();
                for (DataSnapshot objSnapshot:dataSnapshot.getChildren()){
                    Pessoa p = objSnapshot.getValue(Pessoa.class);
                    listPessoa.add(p);
                }
                arrayAdapterPessoa = new ArrayAdapter<Pessoa>(MainActivity.this,
                        android.R.layout.simple_list_item_1,listPessoa);
                listV_dados.setAdapter(arrayAdapterPessoa);

            }

            @Override

            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(MainActivity.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_novo){
            Pessoa p = new Pessoa();
            p.setUid(UUID.randomUUID().toString());
            p.setNome(edtNome.getText().toString());
            p.setEmail(edtEmail.getText().toString());
            databaseReference.child("Pessoa").child(p.getUid()).setValue(p);
            limparCampos();
        } else if(id == R.id.menu_atualizar){
            Pessoa p = new Pessoa();
            p.setUid(pessoaSelecionada.getUid());
            p.setNome(edtNome.getText().toString().trim());
            p.setEmail(edtEmail.getText().toString().trim());
            databaseReference.child("Pessoa").child(p.getUid()).setValue(p);
            limparCampos();

        }else if( id == R.id.menu_deleta){
            Pessoa p = new Pessoa();
            p.setUid(pessoaSelecionada.getUid());
            databaseReference.child("Pessoa").child(p.getUid()).removeValue();
            limparCampos();

        }
            return true;
    }

    private void limparCampos() {
        edtEmail.setText("");
        edtNome.setText("");
    }
}