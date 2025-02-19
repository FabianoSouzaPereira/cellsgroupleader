package br.com.cellsgroupleader.relatorios;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Objects;

import br.com.cellsgroupleader.*;
import br.com.cellsgroupleader.R;
import br.com.cellsgroupleader.celulas.CelulasActivity;
import br.com.cellsgroupleader.contato.ContatoActivity;
import br.com.cellsgroupleader.home.HomeActivity;
import br.com.cellsgroupleader.igreja.IgrejasCriadasActivity;
import br.com.cellsgroupleader.igreja.addIgrejaActivity;
import br.com.cellsgroupleader.leader.LeaderActivity;
import br.com.cellsgroupleader.models.relatorios.Relatorio;

import static br.com.cellsgroupleader.home.HomeActivity.UI;
import static br.com.cellsgroupleader.home.HomeActivity.group;
import static br.com.cellsgroupleader.home.HomeActivity.igreja;
import static br.com.cellsgroupleader.home.HomeActivity.uidIgreja;
import static br.com.cellsgroupleader.home.HomeActivity.useremailAuth;
import static br.com.cellsgroupleader.models.login.LoginActivity.updateUI;

@SuppressWarnings("ALL")
public class ReadRelatorioActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String cel_1;
    private DatabaseReference novaRef6;
    private ArrayList<String> rel = new ArrayList<String>();
    private ArrayAdapter<String> ArrayAdapterRelatorio;
    private ListView relatorio;
    private TextInputLayout textInputCelula;
    private TextInputLayout textInputRede;
    private TextInputLayout textInputSupervisor;
    private TextInputLayout textInputLider;
    private TextInputLayout textInputViceLider;
    private TextInputLayout textInputAnfitriao;
    private TextInputLayout textInputSecretario;
    private TextInputLayout textInputColaborador;
    private TextInputLayout textInputDia;
    private TextInputLayout textInputHora;
    private TextInputLayout textInputbaseCelula;
    private TextInputLayout textInputmembrosIEQ;
    private TextInputLayout textInputconvidados;
    private TextInputLayout textInputcriancas;
    private TextInputLayout textInputtotal;
    private TextInputLayout textInputestudos;
    private TextInputLayout textInputquebragelo;
    private TextInputLayout textInputlanche;
    private TextInputLayout textInputaceitacao;
    private TextInputLayout textInputreconciliacao;
    private TextInputLayout textInputtestemunho;
    private TextInputLayout textInputdataHora;
    TextView nhTitle;
    TextView nhEmail;
    TextView nhName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_read_relatorio );
        Toolbar toolbar = findViewById( R.id.toolbar_read_relatorio );
        setSupportActionBar( toolbar );
        inicializarFirebase();
        inicializarComponentes();
        Intent intent = getIntent();
        String cel_ = intent.getStringExtra( "Relatorio" );
        int pos = cel_.indexOf(":");
        cel_1 = cel_.substring( 0, pos );
        mostraRelatorio();

        DrawerLayout drawer = findViewById( R.id.drawer_read_relatorio );
        NavigationView navigationView = findViewById( R.id.nav_view_relatorio_activity );
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle( this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );
        drawer.addDrawerListener( toggle );
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener( this );


        View headerView = navigationView.getHeaderView(0);
        nhTitle = (TextView) headerView.findViewById (R.id.nhTitle_relatorio);
        nhName = (TextView) headerView.findViewById (R.id.nhName_relatorio);
        nhEmail = (TextView) headerView.findViewById (R.id.nhEmail_relatorio);
        nhEmail.setText (useremailAuth);
        nhTitle.setText (group);
        nhName.setText(igreja);
    }

    private void inicializarComponentes() {
        textInputCelula = findViewById(R.id.text_input_celula);
        textInputRede = findViewById(R.id.text_input_rede);
        textInputSupervisor = findViewById(R.id.text_input_supervisor);
        textInputLider = findViewById(R.id.text_input_lider);
        textInputViceLider = findViewById(R.id.text_input_vice_lider);
        textInputAnfitriao = findViewById(R.id.text_input_anfitriao);
        textInputSecretario = findViewById(R.id.text_input_secretario);
        textInputColaborador = findViewById(R.id.text_input_colaborador);
        textInputDia = findViewById(R.id.text_input_dia);
        textInputHora = findViewById(R.id.text_input_hora);
        textInputbaseCelula = findViewById(R.id.text_input_basecelula  );
        textInputmembrosIEQ = findViewById(R.id.text_input_membrosieq  );
        textInputconvidados = findViewById(R.id.text_input_convidados  );
        textInputcriancas = findViewById( R.id.text_input_criancas );
        textInputtotal  = findViewById(R.id.text_input_total  );
        textInputestudos = findViewById( R.id.text_input_estudo );
        textInputquebragelo = findViewById( R.id.text_input_quebragelo );
        textInputlanche = findViewById( R.id.text_input_lanche );
        textInputaceitacao = findViewById( R.id.text_input_aceitacao );
        textInputreconciliacao = findViewById( R.id.text_input_reconciliacao );
        textInputtestemunho = findViewById( R.id.text_input_testemunho  );
        textInputdataHora = findViewById( R.id.text_input_datahora );

    }

    private void mostraRelatorio() {
        final String ui = UI.getUid ();
        novaRef6 = databaseReference.child( "churchs/" + uidIgreja + "/Reports/" + cel_1 );
        novaRef6.orderByChild ("uid" ).limitToLast(200);
        novaRef6.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    Relatorio r = ds.getValue(Relatorio.class);
                    String celula = Objects.requireNonNull( r ).getCelula();
                    String rede = r.getRede();
                    String supervisor = r.getSupervisor();
                    String lider = r.getLider();
                    String viceLider = r.getViceLider();
                    String anfitriao = r.getAnfitriao();
                    String secretario = r.getSecretario();
                    String colaborador = r.getColaborador();
                    String dia = r.getDia();
                    String hora = r.getHora();
                    String baseCelula = r.getBaseCelula();
                    String membrosIEQ = r.getMembrosIEQ();
                    String convidados = r.getConvidados();
                    String criancas = r.getCriancas();
                    String total =  r.getTotal();
                    String estudo = r.getEstudo();
                    String quebragelo = r.getQuebragelo();
                    String lanche = r.getLanche();
                    String aceitacao = r.getAceitacao();
                    String reconciliacao = r.getReconciliacao();
                    String testemunho = r.getTestemunho();
                    String dataHora = r.getDatahora();


                    Objects.requireNonNull( textInputCelula.getEditText() ).setText( celula );
                    Objects.requireNonNull( textInputRede.getEditText() ).setText(rede);
                    Objects.requireNonNull( textInputSupervisor.getEditText() ).setText(supervisor);
                    Objects.requireNonNull( textInputLider.getEditText() ).setText(lider);
                    Objects.requireNonNull( textInputViceLider.getEditText() ).setText(viceLider);
                    Objects.requireNonNull( textInputAnfitriao.getEditText() ).setText(anfitriao);
                    Objects.requireNonNull( textInputSecretario.getEditText() ).setText(secretario);
                    Objects.requireNonNull( textInputColaborador.getEditText() ).setText(colaborador);
                    Objects.requireNonNull( textInputDia.getEditText() ).setText(dia);
                    Objects.requireNonNull( textInputHora.getEditText() ).setText(hora);

                    Objects.requireNonNull(textInputbaseCelula.getEditText()).setText(baseCelula);
                    Objects.requireNonNull(textInputmembrosIEQ.getEditText()).setText(membrosIEQ);
                    Objects.requireNonNull(textInputconvidados.getEditText()).setText(convidados);
                    Objects.requireNonNull(textInputcriancas.getEditText()).setText(criancas);
                    Objects.requireNonNull(textInputtotal.getEditText()).setText(total);

                    Objects.requireNonNull(textInputestudos.getEditText()).setText(estudo);
                    Objects.requireNonNull(textInputquebragelo.getEditText()).setText(quebragelo);
                    Objects.requireNonNull(textInputlanche.getEditText()).setText(lanche);
                    Objects.requireNonNull(textInputaceitacao.getEditText()).setText(aceitacao);
                    Objects.requireNonNull(textInputreconciliacao.getEditText()).setText(reconciliacao);
                    Objects.requireNonNull(textInputtestemunho.getEditText()).setText(testemunho);
                    Objects.requireNonNull(textInputdataHora.getEditText()).setText(dataHora);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById( R.id.drawer_read_relatorio );
        if (drawer.isDrawerOpen( GravityCompat.START )) {
            drawer.closeDrawer( GravityCompat.START );
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate( R.menu.home, menu );
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu ( Menu menu ) {
        try {
            MenuItem addIgreja = menu.findItem(R.id.action_addIgreja);
            MenuItem igreja = menu.findItem(R.id.action_readIgreja);
            if( uidIgreja != null && !uidIgreja.equals ( "" ) ) {
                addIgreja.setVisible ( false );
                igreja.setVisible (true );
            }else{
                addIgreja.setVisible ( true );
                igreja.setVisible (false);
            }
        } catch ( Exception e ) {
            e.printStackTrace ( );
        } finally {
            return true;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId ( );
        if ( itemId == R.id.action_addIgreja ) {
            Intent addigreja = new Intent ( ReadRelatorioActivity.this  , addIgrejaActivity.class );
            startActivity ( addigreja );
            return true;
        } else if ( itemId == R.id.action_readIgreja ) {
            Intent readigreja = new Intent ( ReadRelatorioActivity.this , IgrejasCriadasActivity.class );
            startActivity ( readigreja );
            return true;
        }else if ( itemId == R.id.action_lideres) {
            Intent addlideres = new Intent ( ReadRelatorioActivity.this , LeaderActivity.class );
            startActivity ( addlideres);
            return true;
        }else if ( itemId == R.id.action_Sobre) {
            Intent sobre= new Intent ( ReadRelatorioActivity.this , SobreActivity.class );
            startActivity ( sobre);
            return true;
        }else if ( itemId == R.id.action_Sair ) {
            finishAffinity ();
            return true;
        }  else if ( itemId == R.id.action_Logout ) {
            FirebaseAuth.getInstance ( ).signOut ( );
            updateUI ( null );
            Toast.makeText ( this , getString ( R.string.Logout_sucesso ) , Toast.LENGTH_LONG ).show ( );
            finishAffinity ();
            return true;
        }
        return super.onOptionsItemSelected ( item );

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent home = new Intent( this, HomeActivity.class );
            startActivity( home );

        } else if (id == R.id.nav_cells) {
            Intent celulas = new Intent( ReadRelatorioActivity.this, CelulasActivity.class );
            startActivity( celulas );

        }  else if (id == R.id.nav_leader) {
            Intent agenda = new Intent( ReadRelatorioActivity.this, LeaderActivity.class );
            startActivity( agenda );

        } else if (id == R.id.nav_realatorio) {
            Intent relatorio = new Intent( ReadRelatorioActivity.this, RelatorioActivityView.class );
            startActivity( relatorio );

        } else if (id == R.id.nav_contact) {
            Intent contato = new Intent( ReadRelatorioActivity.this, ContatoActivity.class );
            startActivity( contato );
        }

        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_read_relatorio );
        drawer.closeDrawer( GravityCompat.START );
        return true;
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(ReadRelatorioActivity.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    protected void onStart ( ) {
        super.onStart ( );
    }

    @Override
    protected void onStop ( ) {
        super.onStop ( );
    }

    @Override
    protected void onResume ( ) {
        super.onResume ( );
    }

    @Override
    protected void onPause ( ) {
        super.onPause ( );
    }

    @Override
    protected void onDestroy ( ) {
        super.onDestroy ( );
    }
}
