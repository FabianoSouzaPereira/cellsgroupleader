package br.com.cellsgroupleader.leader;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.Objects;

import br.com.cellsgroupleader.R;
import br.com.cellsgroupleader.celulas.CelulasActivity;
import br.com.cellsgroupleader.contato.ContatoActivity;
import br.com.cellsgroupleader.models.pessoas.Leader;
import br.com.cellsgroupleader.relatorios.RelatorioActivityView;

import static br.com.cellsgroupleader.home.HomeActivity.*;

public class ReadLeaderActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "TAG";

    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference leaders;
    private DatabaseReference ref;

    private TextInputLayout EditTextCelula;
    private TextInputLayout EditTextnome;
    private TextInputLayout EditTextidade;
    private TextInputLayout EditTextsexo;
    private TextInputLayout EditTextdataNascimento;
    private TextInputLayout EditTextdataBastismo;
    private TextInputLayout EditTextnomepai;
    private TextInputLayout EditTextnomemae;
    private TextInputLayout EditTextestadocivil;
    private TextInputLayout EditTexttelefone;
    private TextInputLayout EdiTextddi;
    private TextInputLayout EditTextemail;
    private TextInputLayout EditTextendereco;
    private TextInputLayout EditTextbairro;
    private TextInputLayout EditTextestado;
    private TextInputLayout EditTextcidade;
    private TextInputLayout EditTextpais;
    private TextInputLayout EditTextcep;
    private TextInputLayout EditTextcargoIgreja;
    private String uid;
    private Query query;
    private ValueEventListener queryListener;
    TextView nhTitle;
    TextView nhEmail;
    TextView nhName;
    String mensagem11 = "";
    String mensagem12 = "";
    String mensagem13 = "";
    String mensagem14 = "";
    String mensagem15 = "";
    private String user;
    
    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_read_leader);
        Toolbar toolbar = findViewById( R.id.toolbar_read_leader );
        setSupportActionBar( toolbar );
        mAuth = FirebaseAuth.getInstance();
        useremailAuth = mAuth.getCurrentUser ().getEmail ();
        inicializarFirebase();
        inicializarComponentes();

        Intent intent = getIntent();
        uid = intent.getStringExtra( "uid" );
        user  = intent.getStringExtra( "user" );

        readOnlyActive();

        DrawerLayout drawer = findViewById( R.id.drawer_activity_read_leader);
        NavigationView navigationView = findViewById( R.id.nav_view_read_leader);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle ( this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );
        drawer.addDrawerListener( toggle );
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener( this );

        View headerView = navigationView.getHeaderView(0);
        nhTitle = headerView.findViewById (R.id.nhTitle_read_leader);
        nhName = headerView.findViewById (R.id.nhName_read_leader);
        nhEmail = headerView.findViewById (R.id.nhEmail_read_leader);
        nhEmail.setText (useremailAuth);
        nhTitle.setText (group);
        nhName.setText(igreja);

    }

    private void inicializarComponentes() {
        EditTextCelula = findViewById (R.id.text_input_readCelula );
        EditTextnome = findViewById( R.id.text_input_readNome);
        EditTextidade = findViewById( R.id.text_input_readIdade);
        EditTextsexo = findViewById( R.id.text_input_readSexo );
        EditTextdataNascimento = findViewById( R.id.text_input_readDataNascimento);
        EditTextdataBastismo = findViewById( R.id.text_input_readDataBatismo );
        EditTextnomepai = findViewById( R.id.text_input_readNomePai );
        EditTextnomemae = findViewById( R.id.text_input_readNomeMae );
        EditTextestadocivil = findViewById( R.id.text_input_readEstadoCivil );
        EdiTextddi = findViewById (R.id.text_input_readddi );
        EditTexttelefone = findViewById( R.id.text_input_readTelefone );
        EditTextemail = findViewById( R.id.text_input_readEmail );
        EditTextendereco = findViewById( R.id.text_input_readEndereco );
        EditTextbairro = findViewById( R.id.text_input_readBairro );
        EditTextcidade = findViewById( R.id.text_input_readCidade );
        EditTextestado = findViewById ( R.id.text_input_readEstado );
        EditTextpais = findViewById( R.id.text_input_readPais );
        EditTextcep = findViewById( R.id.text_input_readCep );
        EditTextcargoIgreja = findViewById( R.id.text_input_readCargoIgreja);
        mensagem11 = getResources ().getString (R.string.apagadoLiderSuccess);
        mensagem12 = getResources ().getString (R.string.erroApagarLider);
        mensagem13 = getResources ().getString (R.string.lider);
        mensagem14 = getResources ().getString (R.string.questionApagarlider);
        mensagem15 = getResources ().getString (R.string.cancelar);
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp( ReadLeaderActivity.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }


    private void readOnlyActive() {
        leaders = databaseReference.child( "churchs/" + uidIgreja + "/leaders/");
        query = leaders.orderByChild( "uid" ).equalTo (userUid).limitToFirst(1);
        queryListener =  new ValueEventListener () {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dados : dataSnapshot.getChildren()) {
                    try {
                        Leader l = dados.getValue (Leader.class);
                        if (l.getUid() != null && l.getUid() != null) {
                            if(l.getUid().equalsIgnoreCase (uid) ) {
                                String celula = l.getCelula().trim();
                                String nome = l.getNome().trim();
                                String idade = l.getIdade().trim ();
                                String sexo = l.getSexo ().trim ();
                                String dataNascimento = l.getDataNascimento ().trim ();
                                String dataBastismo = l.getDataBastismo ().trim ();
                                String nomepai = l.getNomepai ().trim ();
                                String nomemae = l.getNomemae ().trim ();
                                String estadocivil = l.getEstadocivil ().trim ();
                                String ddi = l.getDdi ().trim ();
                                String telefone = l.getTelefone ().trim ();
                                String email = l.getEmail ().trim ();
                                String endereco = l.getEndereco ().trim ();
                                String bairro = l.getBairro ().trim ();
                                String cidade = l.getCidade ().trim ();
                                String estado = l.getEstado ().trim ();
                                String pais = l.getPais ().trim ();
                                String cep = l.getCep ().trim ();
                                String cargoIgreja = l.getCargoIgreja ().trim ();

                                Objects.requireNonNull ( EditTextCelula.getEditText ( ) , "" ).setText ( celula );
                                Objects.requireNonNull ( EditTextnome.getEditText ( ) , "" ).setText ( nome );
                                Objects.requireNonNull ( EditTextidade.getEditText ( ) , "" ).setText ( idade );
                                Objects.requireNonNull ( EditTextsexo.getEditText ( ) , "" ).setText ( sexo );
                                Objects.requireNonNull ( EditTextdataNascimento.getEditText ( ) , "" ).setText ( dataNascimento );
                                Objects.requireNonNull ( EditTextdataBastismo.getEditText ( ) , "" ).setText ( dataBastismo );
                                Objects.requireNonNull ( EditTextnomepai.getEditText ( ) , "" ).setText ( nomepai );
                                Objects.requireNonNull ( EditTextnomemae.getEditText ( ) , "" ).setText ( nomemae );
                                Objects.requireNonNull ( EditTextestadocivil.getEditText ( ) , "" ).setText ( estadocivil );
                                Objects.requireNonNull ( EdiTextddi.getEditText ( ) , "" ).setText ( ddi );
                                Objects.requireNonNull ( EditTexttelefone.getEditText ( ) , "" ).setText ( telefone );
                                Objects.requireNonNull ( EditTextemail.getEditText ( ) , "" ).setText ( email );
                                Objects.requireNonNull ( EditTextendereco.getEditText ( ) , "" ).setText ( endereco );
                                Objects.requireNonNull ( EditTextbairro.getEditText ( ) , "" ).setText ( bairro );
                                Objects.requireNonNull ( EditTextcidade.getEditText ( ) , "" ).setText ( cidade );
                                Objects.requireNonNull ( EditTextestado.getEditText ( ) , "" ).setText ( estado );
                                Objects.requireNonNull ( EditTextpais.getEditText ( ) , "" ).setText ( pais );
                                Objects.requireNonNull ( EditTextcep.getEditText ( ) , "" ).setText ( cep );
                                Objects.requireNonNull ( EditTextcargoIgreja.getEditText ( ) , "" ).setText ( cargoIgreja );

                            }
                        }
                    } catch ( Exception e ) {
                        e.printStackTrace ( );
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e( ReadLeaderActivity.TAG ,"Erro Database"+ databaseError.toException() );
            }
        } ;

        query.addValueEventListener (queryListener );

    }

    private void deleteUsuario(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder( ReadLeaderActivity.this );
        builder1 = builder1.setMessage( mensagem13 + " " + useremail);
        builder1.setTitle(  mensagem14  ).setCancelable( false ).setNegativeButton(  mensagem15, ( dialog , which ) -> Toast.makeText( getApplicationContext(),  mensagem15, Toast.LENGTH_SHORT ).show() ).setPositiveButton( "Ok", ( dialog , which ) -> {
            ref = databaseReference;

            //apaga leader de membros
            ref.child ( "churchs/" + uidIgreja ).child ("/members/").child(uid).removeValue ();

            //Apaga leader em users/
            leaders.child ( uid ).removeValue ( );

            Toast.makeText ( ReadLeaderActivity.this , mensagem11 , Toast.LENGTH_LONG ).show ( );

            finish();
            Intent intent = new Intent( ReadLeaderActivity.this, LeaderActivity.class );
            startActivity(intent);
        } );

        AlertDialog alertDialog1 = builder1.create();
        alertDialog1.show();

    }

    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent( ReadLeaderActivity.this, LeaderActivity.class );
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu) {
        getMenuInflater().inflate( R.menu.menu_edit_cancel, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_Edit){
            finish();
            Intent intent = new Intent(  ReadLeaderActivity.this, EditLeaderActivity.class );
            intent.putExtra("uid", String.valueOf( uid) );
            startActivity(intent);
            return true;
        }
        if(id == R.id.action_delete){
            deleteUsuario ();
            return true;
        }

        if(id == R.id.action_Cancel){
            finish();
            Intent intent = new Intent(  ReadLeaderActivity.this, LeaderActivity.class );
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected( item );
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent home = new Intent( this, ReadLeaderActivity.class );
            startActivity( home );

        } else if (id == R.id.nav_cells) {
            Intent celulas = new Intent( ReadLeaderActivity.this, CelulasActivity.class );
            celulas.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity( celulas );

        } else if (id == R.id.nav_leader) {
            Intent agenda = new Intent( ReadLeaderActivity.this, LeaderActivity.class );
            startActivity( agenda );

        }else if (id == R.id.nav_realatorio) {
            Intent relatorio = new Intent( ReadLeaderActivity.this, RelatorioActivityView.class );
            startActivity( relatorio );

        } else if (id == R.id.nav_contact) {
            Intent contato = new Intent( ReadLeaderActivity.this, ContatoActivity.class );
            startActivity( contato );
        }

        DrawerLayout drawer = findViewById( R.id.drawer_activity_read_leader );
        drawer.closeDrawer( GravityCompat.START );
        return true;
    }


    @Override
    protected void onResume ( ) {
        super.onResume ( );
    }

    @Override
    protected void onStart ( ) {
        super.onStart ( );
    }

    @Override
    protected void onStop ( ) {
        query.removeEventListener (queryListener);
        super.onStop ( );
    }

    @Override
    protected void onRestart ( ) {
        super.onRestart ( );
    }

    @Override
    protected void onDestroy ( ) {
        super.onDestroy ( );
    }
}