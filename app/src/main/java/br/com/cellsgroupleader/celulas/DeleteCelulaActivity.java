package br.com.cellsgroupleader.celulas;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import br.com.cellsgroupleader.R;
import br.com.cellsgroupleader.contato.ContatoActivity;
import br.com.cellsgroupleader.home.HomeActivity;
import br.com.cellsgroupleader.leader.LeaderActivity;

import static br.com.cellsgroupleader.home.HomeActivity.uidIgreja;

@SuppressWarnings("ALL")
public class DeleteCelulaActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String uid_extra;
    private TextInputLayout celulaParaApagar;
    private TextInputLayout apagarCelula;
    private Button btnApagarCelula;
    private DatabaseReference novaRef3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_delete_celula );
        Toolbar toolbar = findViewById( R.id.toolbar_delete_celula );
        setSupportActionBar( toolbar );
        inicializarFirebase();

        Intent intent = getIntent();
        uid_extra = intent.getStringExtra( "Celula" );
        novaRef3 = databaseReference.child( "churchs/" + uidIgreja + "/cells/");
        inicializarComponentes();
        Objects.requireNonNull(celulaParaApagar.getEditText() ).setText( uid_extra );
        btnApagarCelula.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCelula();
            }
        } );


        DrawerLayout drawer = findViewById( R.id.drawer_del_celula );
        NavigationView navigationView = findViewById( R.id.nav_view_delite_celulas );
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle( this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );
        drawer.addDrawerListener( toggle );
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener( this );
    }


    private void inicializarComponentes() {
        celulaParaApagar = findViewById( R.id.tvCelulaParaApagar );
        apagarCelula = findViewById(R.id.tvApagarCelula );
        btnApagarCelula = findViewById(R.id.btnApagarCelula);
    }

    //ToDo Falta fazer no futuro um metodo que não apague a celula permanentemento usando status= 0 ;
    private void deleteCelula() {

        novaRef3.child( uid_extra );

        if(!TextUtils.isEmpty( uid_extra )){
            novaRef3.child( uid_extra ).removeValue();
            Toast.makeText(this,"Célula Apagada com sucesso", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this,"Erro ao tentar Apagar a célula !", Toast.LENGTH_LONG).show();
        }

        Intent celulas = new Intent( DeleteCelulaActivity.this, CelulasActivity.class);
        startActivity( celulas );
        finish();
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(DeleteCelulaActivity.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById( R.id.drawer_del_celula );
        if (drawer.isDrawerOpen( GravityCompat.START )) {
            drawer.closeDrawer( GravityCompat.START );
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate( R.menu.menu_save_edit_delete , menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_delete) {
            Intent celulas = new Intent( DeleteCelulaActivity.this,CelulasActivity.class);
            startActivity( celulas );
            finish();
            return true;
        }

        return super.onOptionsItemSelected( item );
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent home = new Intent(this, HomeActivity.class);
            startActivity(home);

        } else if (id == R.id.nav_cells) {
            Intent celulas = new Intent(DeleteCelulaActivity.this,CelulasActivity.class);
            startActivity( celulas );

        } else if (id == R.id.nav_leader) {
            Intent agenda = new Intent( DeleteCelulaActivity.this, LeaderActivity.class );
            startActivity( agenda );

        } else if (id == R.id.nav_contact) {
            Intent contato = new Intent( DeleteCelulaActivity.this, ContatoActivity.class );
            startActivity( contato );

        }else if (id == R.id.nav_contact) {
            Intent contato = new Intent( DeleteCelulaActivity.this, ContatoActivity.class );
            startActivity( contato );
        }

        DrawerLayout drawer = findViewById( R.id.drawer_del_celula );
        drawer.closeDrawer( GravityCompat.START );
        return true;
    }

}
