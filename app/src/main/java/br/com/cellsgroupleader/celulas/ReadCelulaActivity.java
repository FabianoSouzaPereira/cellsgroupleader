package br.com.cellsgroupleader.celulas;

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
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.*;

import java.util.Objects;

import br.com.cellsgroupleader.R;
import br.com.cellsgroupleader.contato.ContatoActivity;
import br.com.cellsgroupleader.home.*;

import br.com.cellsgroupleader.leader.LeaderActivity;
import br.com.cellsgroupleader.models.celulas.Celula;
import br.com.cellsgroupleader.celulas.*;
import br.com.cellsgroupleader.relatorios.*;

import static br.com.cellsgroupleader.home.HomeActivity.group;
import static br.com.cellsgroupleader.home.HomeActivity.igreja;
import static br.com.cellsgroupleader.home.HomeActivity.uidIgreja;
import static br.com.cellsgroupleader.home.HomeActivity.useremailAuth;


@SuppressWarnings("ALL")
public class ReadCelulaActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
   private FirebaseDatabase firebaseDatabase;
   private DatabaseReference databaseReference;
   private DatabaseReference novaRef0;
   public  String celula_;
   public  String celula_2;
   public  String uid;
   private MaterialTextView text_uid_celula;
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
   private TextInputLayout textInputDataInicio;
   Query query;
   ValueEventListener listener;
   
   TextView nhTitle;
   TextView nhEmail;
   TextView nhName;
   
   
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate( savedInstanceState );
      setContentView( R.layout.activity_read_celula );
      Toolbar toolbar = findViewById( R.id.toolbar_read_celula);
      setSupportActionBar( toolbar );
      inicializarFirebase();
      inicializarComponentes();
      Intent intent = getIntent();
      celula_ = intent.getStringExtra( "Celula" );
      
      DrawerLayout drawer = findViewById( R.id.drawer_read_celula);
      NavigationView navigationView = findViewById( R.id.nav_view );
      ActionBarDrawerToggle toggle = new ActionBarDrawerToggle( this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );
      drawer.addDrawerListener( toggle );
      toggle.syncState();
      navigationView.setNavigationItemSelectedListener( this );
      
      View headerView = navigationView.getHeaderView(0);
      nhTitle = (TextView) headerView.findViewById (R.id.nhTitle_read_celula);
      nhName = (TextView) headerView.findViewById (R.id.nhName_read_celula);
      nhEmail = (TextView) headerView.findViewById (R.id.nhEmail_read_celula);
      nhEmail.setText (useremailAuth);
      nhTitle.setText (group);
      nhName.setText(igreja);
   
      pegandoConteudoCelula();
   }
   
   
   private void inicializarComponentes() {
      text_uid_celula = findViewById (R.id.text_uid_redCelula );
      textInputCelula = findViewById(R.id.txt_celula);
      textInputRede = findViewById(R.id.txt_rede);
      textInputSupervisor = findViewById(R.id.txt_supervisor);
      textInputLider = findViewById(R.id.txt_lider);
      textInputViceLider = findViewById(R.id.txt_vicelider);
      textInputAnfitriao = findViewById(R.id.txt_anfitriao);
      textInputSecretario = findViewById(R.id.txt_secretario);
      textInputColaborador = findViewById(R.id.txt_colaborador);
      textInputDia = findViewById(R.id.txt_dia);
      textInputHora = findViewById(R.id.txt_hora);
      textInputDataInicio = findViewById( R.id.txt_dataInicio );
   }
   
   private void pegandoConteudoCelula() {
      
      novaRef0 = databaseReference.child( "churchs/" + uidIgreja + "/cells/" + this.celula_ + "/");
      query = novaRef0.orderByChild("celula");
      listener = new ValueEventListener() {
         
         @Override
         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for(DataSnapshot ds:dataSnapshot.getChildren()){
               Celula c = ds.getValue( Celula.class );
               String uid =  c.getUid();
               String celula = Objects.requireNonNull( c ).getCelula();
               String rede = c.getRede();
               String supervisor = c.getSupervisor();
               String lider = c.getLider();
               String viceLider = c.getViceLider();
               String anfitriao = c.getAnfitriao();
               String secretario = c.getSecretario();
               String colaborador = c.getColaborador();
               String dia = c.getDia();
               String hora = c.getHora();
               String datainicio = c.getDatainicio();
               text_uid_celula.setText (uid);
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
               Objects.requireNonNull( textInputDataInicio.getEditText() ).setText( datainicio );
               
            }
            
         }
         
         @Override
         public void onCancelled(@NonNull DatabaseError databaseError) {
         
         }
      };
      query.addValueEventListener(listener);
   }
   
   @Override
   public void onBackPressed() {
      DrawerLayout drawer = findViewById( R.id.drawer_read_celula );
      if (drawer.isDrawerOpen( GravityCompat.START )) {
         drawer.closeDrawer( GravityCompat.START );
      } else {
         super.onBackPressed();
      }
   }
   
   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate( R.menu.menu_edit_cancel, menu );
      return super.onCreateOptionsMenu( menu );
   }
   
   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      int id = item.getItemId();
      
      if (id == R.id.action_settings) {
         return true;
      }
      if(id == R.id.action_save){
         return true;
      }
      
      if(id == R.id.action_Edit){
         Intent intent = new Intent( ReadCelulaActivity.this, EditCelulaActivity.class );
         intent.putExtra("Celula", String.valueOf( this.celula_ ) );
         startActivity(intent);
         return true;
      }
      if(id == R.id.action_delete){
         initAlertDlgDelete();
         
         return true;
      }
      
      return super.onOptionsItemSelected( item );
   }
   
   @Override
   public boolean onNavigationItemSelected(@NonNull MenuItem item) {
      int id = item.getItemId();
      
      if (id == R.id.nav_home) {
         Intent home = new Intent( this, HomeActivity.class );
         startActivity( home );
         
      } else if (id == R.id.nav_cells) {
         Intent celulas = new Intent( ReadCelulaActivity.this, CelulasActivity.class );
         startActivity( celulas );
         
      } else if (id == R.id.nav_leader) {
         Intent agenda = new Intent( ReadCelulaActivity.this, LeaderActivity.class );
         startActivity( agenda );
         
      }else if (id == R.id.nav_realatorio) {
         Intent relatorio = new Intent( ReadCelulaActivity.this, RelatorioActivityView.class );
         startActivity( relatorio );
      }else if (id == R.id.nav_contact) {
         Intent contato = new Intent( ReadCelulaActivity.this, ContatoActivity.class );
         startActivity( contato );
      }
      
      DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_read_celula );
      drawer.closeDrawer( GravityCompat.START );
      return true;
   }
   
   private void inicializarFirebase() {
      FirebaseApp.initializeApp(ReadCelulaActivity.this);
      firebaseDatabase = FirebaseDatabase.getInstance();
      databaseReference = firebaseDatabase.getReference();
   }
   
   public void initAlertDlgDelete() {
      AlertDialog.Builder builder = new AlertDialog.Builder(ReadCelulaActivity.this);
      builder  = builder.setMessage( "Apagar a célula?" );
      builder.setTitle( "Apagando célula..." )
         .setCancelable( false )
         .setNegativeButton( "cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               Toast.makeText(getApplicationContext(), "Cancelado Apagar", Toast.LENGTH_SHORT).show();
            }
         })
         .setPositiveButton( "Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               Intent intent = new Intent( ReadCelulaActivity.this, DeleteCelulaActivity.class );
               intent.putExtra("Celula", String.valueOf( ReadCelulaActivity.this.celula_ ) );
               startActivity(intent);
               finish();
            }
         });
      
      AlertDialog alertDialog = builder . create () ;
      alertDialog.show();
      
   }
   
   @Override
   protected void onPause( ){
      query.removeEventListener(listener);
      super.onPause( );
   }
}
