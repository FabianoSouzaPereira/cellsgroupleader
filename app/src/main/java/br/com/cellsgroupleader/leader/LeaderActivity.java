package br.com.cellsgroupleader.leader;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import adapters.AdapterListViewLeader;
import br.com.cellsgroupleader.R;
import br.com.cellsgroupleader.*;
import br.com.cellsgroupleader.celulas.CelulasActivity;
import br.com.cellsgroupleader.contato.ContatoActivity;
import br.com.cellsgroupleader.home.HomeActivity;
import br.com.cellsgroupleader.igreja.IgrejasCriadasActivity;
import br.com.cellsgroupleader.igreja.addIgrejaActivity;
import br.com.cellsgroupleader.models.pessoas.Leader;
import br.com.cellsgroupleader.relatorios.RelatorioActivityView;

import static br.com.cellsgroupleader.home.HomeActivity.*;
import static br.com.cellsgroupleader.models.login.LoginActivity.updateUI;


@SuppressWarnings( "UnnecessaryLocalVariable" )
public class LeaderActivity extends AppCompatActivity implements Serializable ,NavigationView.OnNavigationItemSelectedListener {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference novaRef;

    private RecyclerView recyclerView;
    private final ArrayList < Leader > arrayLeader = new ArrayList <>( );
    private AdapterListViewLeader mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private final boolean mItemPressed = false;

    private final int limitebusca = 200;
    private Query query;
    private ValueEventListener queryListener;
    private String uid;
    private String nome;
    TextView nhTitle;
    TextView nhEmail;
    TextView nhName;
    String mensagem15 = "";
    String mensagem16 = "";
    String mensagem17 = "";

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_leader );
        Toolbar toolbar = findViewById( R.id.toolbarleader);
        setSupportActionBar( toolbar );

        iniciaComponentes();
        inicializarFirebase();

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager (this);
        recyclerView.setLayoutManager(layoutManager);

        readOnlyActive();

        FloatingActionButton fab = findViewById( R.id.fabLeader );
        fab.setOnClickListener( view -> {
            if(!igreja.equals ("")) {
                Intent addLeader = new Intent ( LeaderActivity.this , AddLeaderActivity.class );
                startActivity ( addLeader );
                finish ( );
            }else{
                aviso();
            }
        } );

        DrawerLayout drawer = findViewById( R.id.drawer_activity_leader);
        NavigationView navigationView = findViewById( R.id.nav_view_leader);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle ( this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );
        drawer.addDrawerListener( toggle );
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener( this );

        View headerView = navigationView.getHeaderView(0);
        nhTitle = headerView.findViewById (R.id.nhTitle_leaders);
        nhName = headerView.findViewById (R.id.nhName_leaders);
        nhEmail = headerView.findViewById (R.id.nhEmail_leaders);
        nhEmail.setText (useremailAuth);
        nhTitle.setText (group);
        nhName.setText(igreja);
        mensagem15 = getResources ().getString (R.string.cancelar);
        mensagem16 = getResources ().getString (R.string.aviso);
        mensagem17 = getResources ().getString (R.string.aviso);
    }

    private void iniciaComponentes ( ) {
        recyclerView = findViewById( R.id.listviewLeader );
        recyclerView.setLongClickable(true);
    }

    private void aviso(){
        AlertDialog.Builder builder = new AlertDialog.Builder(LeaderActivity.this);
        builder  = builder.setMessage( mensagem16);
        builder.setTitle( mensagem17 )
            .setCancelable( false )
            .setNegativeButton( mensagem15, ( dialog , which ) -> {
            } )
            .setPositiveButton( "Ok", ( dialog , which ) -> {
                try {
                    Intent addIgreja = new Intent ( LeaderActivity.this , addIgrejaActivity.class );
                    startActivity ( addIgreja );
                    finish ( );
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            } );

        AlertDialog alertDialog = builder . create () ;
        alertDialog.show();
    }

    private void readOnlyActive() {
        
        novaRef = databaseReference.child( "churchs/" + uidIgreja + "/leaders/");
        query = novaRef.orderByChild("uid").limitToLast(limitebusca);
        queryListener =  new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              arrayLeader.clear();
                 for(DataSnapshot dados : dataSnapshot.getChildren()) {
                      try {
                          Leader leader = dados.getValue (Leader.class);
                          if(leader.getUid().equals(userUid)){
                              arrayLeader.add( leader );
                          }
                      } catch ( Exception e ) {
                          e.printStackTrace ( );
                      }
                 }
                // noinspection UnnecessaryLocalVariable
                List < Leader > leaders = arrayLeader;

                mAdapter = new AdapterListViewLeader ( leaders );
                mAdapter.setOnClickListener ( v -> {
                    uid = arrayLeader.get(recyclerView.getChildAdapterPosition (v)).getUid ();
                    nome = arrayLeader.get(recyclerView.getChildAdapterPosition (v)).getNome ();
                    if (mItemPressed) {
                        // Multi-item swipes not handled
                        return;
                    }
                    Intent intent = new Intent( LeaderActivity.this, ReadLeaderActivity.class );
                    intent.putExtra("uid", String.valueOf( uid) );
                    intent.putExtra("nome", String.valueOf( nome ) );
                    startActivity(intent);
                    finish();
                } );
                mAdapter.setOnLongClickListener ( v -> {
                    // Implementar se houver necesidade
                    return false;
                } );
                recyclerView.setAdapter( mAdapter);
                mAdapter.notifyDataSetChanged();
                hiddShowMessage();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        } ;

        query.addValueEventListener (queryListener );


    }

    // Mostra memsagem se lista vir vazia
    private void hiddShowMessage(){

        CardView cardView = findViewById (R.id.cardViewLeaders);
        if( arrayLeader.size() == 0){
            recyclerView.setVisibility (View.GONE);
            cardView.setVisibility (View.VISIBLE);
        }else{
            cardView.setVisibility (View.GONE);
            recyclerView.setVisibility (View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
            Intent addigreja = new Intent ( LeaderActivity.this  , addIgrejaActivity.class );
            startActivity ( addigreja );
            return true;
        } else if ( itemId == R.id.action_readIgreja ) {
            Intent readigreja = new Intent ( LeaderActivity.this , IgrejasCriadasActivity.class );
            startActivity ( readigreja );
            return true;
        }else if ( itemId == R.id.action_lideres) {
            Intent addlideres = new Intent ( LeaderActivity.this , LeaderActivity.class );
            startActivity ( addlideres);
            return true;
        } else if ( itemId == R.id.action_Sobre) {
            Intent sobre= new Intent ( LeaderActivity.this , SobreActivity.class );
            startActivity ( sobre);
            return true;
        }else if ( itemId == R.id.action_Sair ) {
            finishAffinity ();
            return true;
        }  else if ( itemId == R.id.action_Logout ) {
            FirebaseAuth.getInstance ( ).signOut ( );
            updateUI ( null );
            Toast.makeText ( this , getString ( R.string.Logout_sucesso ) , Toast.LENGTH_SHORT ).show ( );
            finishAffinity ();
            return true;
        }
        return super.onOptionsItemSelected ( item );

    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp( LeaderActivity.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseReference.keepSynced(true);
    }

    @Override
    public boolean onNavigationItemSelected( MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent home = new Intent( LeaderActivity.this, HomeActivity.class );
            startActivity( home );

        } else if (id == R.id.nav_cells) {
            Intent celulas = new Intent( LeaderActivity.this, CelulasActivity.class );
            celulas.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity( celulas );

        } else if (id == R.id.nav_realatorio) {
            Intent relatorio = new Intent( LeaderActivity.this, RelatorioActivityView.class );
            startActivity( relatorio );

        } else if (id == R.id.nav_contact) {
            Intent contato = new Intent( LeaderActivity.this, ContatoActivity.class );
            startActivity( contato );
        }

        DrawerLayout drawer = findViewById( R.id.drawer_activity_leader);
        drawer.closeDrawer( GravityCompat.START );
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent( LeaderActivity.this, HomeActivity.class );
        startActivity(intent);
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