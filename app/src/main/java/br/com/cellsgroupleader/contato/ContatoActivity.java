package br.com.cellsgroupleader.contato;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

import adapters.AdapterListViewContato;
import br.com.cellsgroupleader.R;
import br.com.cellsgroupleader.*;
import br.com.cellsgroupleader.celulas.CelulasActivity;
import br.com.cellsgroupleader.home.HomeActivity;
import br.com.cellsgroupleader.igreja.IgrejasCriadasActivity;
import br.com.cellsgroupleader.igreja.addIgrejaActivity;
import br.com.cellsgroupleader.leader.LeaderActivity;
import br.com.cellsgroupleader.models.pessoas.Leader;
import br.com.cellsgroupleader.relatorios.*;

import static br.com.cellsgroupleader.home.HomeActivity.*;
import static br.com.cellsgroupleader.models.login.LoginActivity.updateUI;


public class ContatoActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener , AdapterListViewContato.OnContatoListener{
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference novaRef;
    private final int limitebusca = 200;
    public static FirebaseUser UI;
    private RecyclerView recyclerView;
    private final ArrayList < Leader > arrayLeader = new ArrayList <>( );
    private AdapterListViewContato mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private Query querycontato;
    private ValueEventListener queryContatoListener;
    TextView nhTitle;
    TextView nhEmail;
    TextView nhName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_contato );
        Toolbar toolbar = findViewById( R.id.toolbar_contato );
        setSupportActionBar( toolbar );
        UI = FirebaseAuth.getInstance().getCurrentUser();
        initcomponents();
        inicializarFirebase();

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager (this);
        recyclerView.setLayoutManager(layoutManager);

        novaRef = databaseReference.child("churchs/" + uidIgreja + "/leaders/");
        querycontato = novaRef.orderByChild("uid").limitToLast(limitebusca);
        queryContatoListener =  new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayLeader.clear();
                for(DataSnapshot dados : dataSnapshot.getChildren()) {
                    try {
                        Leader leader = dados.getValue (Leader.class);
                        if(leader.getUserId().equals(userId)){
                            arrayLeader.add( leader );
                        }
                    } catch ( Exception e ) {
                        e.printStackTrace ( );
                    }
                }
                // noinspection UnnecessaryLocalVariable
                List < Leader > leaders = arrayLeader;

                mAdapter = new AdapterListViewContato(leaders, ContatoActivity.this, ContatoActivity.this );
                recyclerView.setAdapter( mAdapter);
                mAdapter.notifyDataSetChanged();
                hiddShowMessage();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        } ;

        querycontato.addValueEventListener (queryContatoListener );

        FloatingActionButton fab = findViewById( R.id.fab );
        fab.setOnClickListener( view -> Snackbar.make( view, "Replace with your own action", Snackbar.LENGTH_LONG ).setAction( "Action", null ).show() );
        DrawerLayout drawer = findViewById( R.id.drawer_activity_contato);
        NavigationView navigationView = findViewById( R.id.nav_view_contato );
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle( this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );
        drawer.addDrawerListener( toggle );
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener( this );

        View headerView = navigationView.getHeaderView(0);
        nhTitle = headerView.findViewById (R.id.nhTitleContato);
        nhName = headerView.findViewById (R.id.nhNameContato);
        nhEmail = headerView.findViewById (R.id.nhEmailContato);
        nhEmail.setText (useremailAuth);
        nhTitle.setText (group);
        nhName.setText(igreja);
    }

    // Mostra memsagem se lista vir vazia
    private void hiddShowMessage() {
        ImageView image = findViewById (R.id.imageViewContato);
        CardView carviewContato = findViewById (R.id.carviewContato );
        if(arrayLeader.size() == 0){
            recyclerView.setVisibility (View.GONE);
            carviewContato.setVisibility (View.VISIBLE);
        }else{
            carviewContato.setVisibility (View.GONE);
            recyclerView.setVisibility (View.VISIBLE);
        }

    }
        private void initcomponents ( ) {
        recyclerView = findViewById( R.id.recyclerview_contato );
    }
    private void inicializarFirebase() {
        FirebaseApp.initializeApp( ContatoActivity.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseReference.keepSynced(true);
    }


    @Override
    public void onBackPressed() {
        ContatoActivity.this.finish();
        Intent home = new Intent(ContatoActivity.this, HomeActivity.class);
        startActivity(home);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate( R.menu.home, menu );
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu ( Menu menu ) {
        MenuItem addIgreja = menu.findItem(R.id.action_addIgreja);
        MenuItem igreja = menu.findItem(R.id.action_readIgreja);
        if( uidIgreja != null && !uidIgreja.equals ( "" ) ) {
            addIgreja.setVisible ( false );
            igreja.setVisible (true );
        }else{
            addIgreja.setVisible ( true );
            igreja.setVisible (false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId ( );
        if ( itemId == R.id.action_addIgreja ) {
            Intent addigreja = new Intent ( ContatoActivity.this  , addIgrejaActivity.class );
            startActivity ( addigreja );
            return true;
        } else if ( itemId == R.id.action_readIgreja ) {
            Intent readigreja = new Intent ( ContatoActivity.this  , IgrejasCriadasActivity.class );
            startActivity ( readigreja );
            return true;
        }else if ( itemId == R.id.action_lideres) {
            Intent addlideres = new Intent ( ContatoActivity.this , LeaderActivity.class );
            startActivity ( addlideres);
            return true;
        }else if ( itemId == R.id.action_Sobre) {
            Intent sobre= new Intent ( ContatoActivity.this  , SobreActivity.class );
            startActivity ( sobre);
            return true;
        }else if ( itemId == R.id.action_Sair ) {
            finishAffinity ();
            return true;
        } else if ( itemId == R.id.action_Logout ) {
            FirebaseAuth.getInstance ( ).signOut ( );
            updateUI ( null );
            Toast.makeText ( this , getString ( R.string.Logout_sucesso ) , Toast.LENGTH_LONG ).show ( );
            finishAffinity ();
            return true;
        }
        return super.onOptionsItemSelected ( item );

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent home = new Intent( this, HomeActivity.class );
            startActivity( home );

        } else if (id == R.id.nav_cells) {
            Intent celulas = new Intent( ContatoActivity.this, CelulasActivity.class );
            startActivity( celulas );

        }else if (id == R.id.nav_leader) {
            Intent agenda = new Intent( ContatoActivity.this, LeaderActivity.class );
            startActivity( agenda );

        }else if (id == R.id.nav_realatorio) {
            Intent relatorio = new Intent( ContatoActivity.this, RelatorioActivityView.class );
            startActivity( relatorio );

        } else if (id == R.id.nav_realatorio) {
            Intent relatorio = new Intent( ContatoActivity.this, RelatorioActivityView.class );
            startActivity( relatorio );

        }

        DrawerLayout drawer = findViewById( R.id.drawer_activity_contato);
        drawer.closeDrawer( GravityCompat.START );
        return true;
    }

    @Override
    protected void onStart ( ) {
        super.onStart ( );
    }

    @Override
    protected void onStop ( ) {
        querycontato.removeEventListener (queryContatoListener );
        super.onStop ( );
    }

    @Override
    protected void onResume ( ) {
        super.onResume ( );
    }

    @Override
    protected void onRestart ( ) {
        super.onRestart ( );
    }

    @Override
    protected void onPause ( ) {
        super.onPause ( );
    }

    @Override
    protected void onDestroy ( ) {
        super.onDestroy ( );
    }
    
    @Override
    public void onContatoClick( int position , String key ){
    
    }
}
