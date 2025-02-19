package br.com.cellsgroupleader.celulas;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import br.com.cellsgroupleader.R;
import br.com.cellsgroupleader.contato.ContatoActivity;
import br.com.cellsgroupleader.home.HomeActivity;
import br.com.cellsgroupleader.leader.LeaderActivity;
import br.com.cellsgroupleader.models.celulas.Celula;

import static br.com.cellsgroupleader.home.HomeActivity.*;

@SuppressWarnings( "ALL" )
public class AddCelulaActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
   public String DataTime;
   public String DataT;
   private String dia;
   private String hh;
   private String mm;
   private Spinner sp;
   private Spinner hr;
   private Spinner min;
   private String  hrs;
   private DatabaseReference Cells;
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
   private static boolean validate = true;
   private String[] semana;
   private String[] hora = new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23" };
   private String[] minuto = new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "24",
      "25","26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48",
      "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59"};
   TextView nhTitle;
   TextView nhEmail;
   TextView nhName;
   String mensagem1 = "";
   String mensagem2 = "";
   String mensagem3 = "";
   String mensagem4 = "";
   String mensagem5 = "";
   
   @SuppressLint("SimpleDateFormat")
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate( savedInstanceState );
      setContentView( R.layout.activity_add_celula );
      Toolbar toolbar = findViewById( R.id.toolbar_add_celula );
      setSupportActionBar( toolbar );
      inicializarFirebase();
      addDataHora();
      inicializarComponentes();
      
      NavigationView navigationView = findViewById( R.id.nav_view_add_celula );
      View headerView = navigationView.getHeaderView(0);
      nhTitle = ( TextView ) headerView.findViewById (R.id.nhTitle_add_celula);
      nhName = (TextView) headerView.findViewById (R.id.nhName_add_celula);
      nhEmail = (TextView) headerView.findViewById (R.id.nhEmail_add_celula);
      nhEmail.setText (useremailAuth);
      nhTitle.setText (group);
      nhName.setText(igreja);
      
   }
   
   private void inicializarComponentes() {
      textInputCelula = findViewById(R.id.text_input_celula);
      textInputCelula.setHint(textInputCelula.getHint()+" "+getString(R.string.asteriskred));
      textInputRede = findViewById(R.id.text_input_rede);
      textInputRede.setHint(textInputRede.getHint()+" "+getString(R.string.asteriskred));
      textInputSupervisor = findViewById(R.id.text_input_supervisor);
      textInputSupervisor.setHint(textInputSupervisor.getHint()+" "+getString(R.string.asteriskred));
      textInputLider = findViewById(R.id.text_input_lider);
      textInputLider.setHint(textInputLider.getHint()+" "+getString(R.string.asteriskred));
      textInputViceLider = findViewById(R.id.text_input_vice_lider);
      textInputViceLider.setHint(textInputViceLider.getHint()+" "+getString(R.string.asteriskred));
      textInputAnfitriao = findViewById(R.id.text_input_anfitriao);
      textInputAnfitriao.setHint(textInputAnfitriao.getHint()+" "+getString(R.string.asteriskred));
      textInputSecretario = findViewById(R.id.text_input_secretario);
      textInputSecretario.setHint(textInputSecretario.getHint()+" "+getString(R.string.asteriskred));
      textInputColaborador = findViewById(R.id.text_input_colaborador);
      textInputColaborador.setHint(textInputColaborador.getHint()+" "+getString(R.string.asteriskred));
      textInputDia = findViewById(R.id.text_input_dia);
      textInputHora = findViewById(R.id.text_input_hora);
      textInputDataInicio = findViewById(R.id.text_input_DataInicio);
      textInputDataInicio.getEditText().setText( DataT );
      mensagem1 =  getResources ().getString (R.string.erroCampoObrigatorio);
      mensagem2 =  getResources ().getString (R.string.criadocelula);
      mensagem3 =  getResources ().getString (R.string.erroCriarcelula);
      mensagem4 =  getResources ().getString (R.string.erroPreencherTudo);
      mensagem5 =  getResources ().getString (R.string.erroNaoAdmin);
      Objects.requireNonNull( textInputDataInicio.getEditText() ).setText(DataT);
      semana  = new String[] {
         getResources ().getString (R.string.dia_da_semana),
         getResources ().getString (R.string.segunda_feira),
         getResources ().getString (R.string.terca_feira),
         getResources ().getString (R.string.quarta_feira),
         getResources ().getString (R.string.quinta_feira),
         getResources ().getString (R.string.sexta_feira),
         getResources ().getString (R.string.sabado),
         getResources ().getString (R.string.domingo),
      };
      ArrayAdapter<String> adapter = new ArrayAdapter<>( AddCelulaActivity.this, android.R.layout.simple_spinner_dropdown_item, semana );
      sp = findViewById( R.id.spinnerSemana );
      sp.setAdapter( adapter );
      sp.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
         @Override
         public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            dia = (String)sp.getSelectedItem();
            
         }
         @Override
         public void onNothingSelected(AdapterView<?> parent) {
            dia = "";
         }
      } );
      
      ArrayAdapter<String> adapterhora = new ArrayAdapter<>( AddCelulaActivity.this, android.R.layout.simple_spinner_dropdown_item,hora );
      hr = findViewById( R.id.spinnerhora );
      hr.setAdapter( adapterhora );
      hr.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
         @Override
         public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            hh = (String)hr.getSelectedItem();
         }
         
         @Override
         public void onNothingSelected(AdapterView<?> parent) {
            hh = "";
         }
      } );
      
      ArrayAdapter<String> adaptermin = new ArrayAdapter<>( AddCelulaActivity.this, android.R.layout.simple_spinner_dropdown_item,minuto );
      min = findViewById( R.id.spinnermin );
      min.setAdapter( adaptermin );
      min.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
         @Override
         public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mm = (String)min.getSelectedItem();
         }
         
         @Override
         public void onNothingSelected(AdapterView<?> parent) {
            mm = "";
         }
      } );
      
   }
   
   /** Inicia Firebase   */
   private void inicializarFirebase() {
      Cells = FirebaseDatabase.getInstance().getReference();
   }
   
   @Override
   public void onBackPressed() {
      AddCelulaActivity.this.finish ();
      Intent intent = new Intent(AddCelulaActivity.this, CelulasActivity.class);
      startActivity(intent);
   }
   
   @Override
   protected void onPause() {
      super.onPause();
   }
   
   @Override
   protected void onStop() {
      super.onStop();
   }
   
   @Override
   protected void onRestoreInstanceState(Bundle savedInstanceState) {
      super.onRestoreInstanceState( savedInstanceState );
   }
   
   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate( R.menu.menu_save_cancel , menu );
      return true;
   }
   
   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      int id = item.getItemId();
      
      if(id == R.id.action_save){
         addCelulaClick(item);
         return true;
      }
      if(id == R.id.action_Cancel){
         AddCelulaActivity.this.finish ();
         Intent intent = new Intent(AddCelulaActivity.this, CelulasActivity.class);
         startActivity(intent);
         return true;
      }
      
      return super.onOptionsItemSelected( item );
   }
   
   @Override
   public boolean onNavigationItemSelected(@NonNull MenuItem item) {
      int id = item.getItemId();
      
      if (id == R.id.nav_home) {
         Intent home = new Intent(this, HomeActivity.class);
         startActivity( home );
      } else if (id == R.id.nav_cells) {
         Intent celulas = new Intent( AddCelulaActivity.this, CelulasActivity.class);
         startActivity( celulas );
      } else if (id == R.id.nav_leader) {
         Intent agenda = new Intent( AddCelulaActivity.this, LeaderActivity.class );
         startActivity( agenda );
      }else if (id == R.id.nav_contact) {
         Intent contato = new Intent( AddCelulaActivity.this, ContatoActivity.class );
         startActivity( contato );
      }
      
      DrawerLayout drawer = findViewById( R.id.drawer_add_celula);
      drawer.closeDrawer( GravityCompat.START );
      return true;
   }
   
   public void addCelulaClick(MenuItem item) {
      validate=true;
      try {
         String celula = Objects.requireNonNull( textInputCelula.getEditText() ).getText().toString().trim();
         if(celula.equals ("")){
            validate = false;
            textInputCelula.setError(mensagem1);
            textInputCelula.setFocusable (true);
            textInputCelula.requestFocus ();
         }else{
            textInputCelula.setError(null);
         }
         String rede = Objects.requireNonNull( textInputRede.getEditText() ).getText().toString().trim();
         if(rede.equals ("")){
            validate = false;
            textInputRede.setError(mensagem1);
            textInputRede.setFocusable (true);
            textInputRede.requestFocus ();
         }else{
            textInputRede.setError(null);
         }
         String supervisor = Objects.requireNonNull( textInputSupervisor.getEditText() ).getText().toString().trim();
         if(supervisor.equals ("")){
            validate = false;
            textInputSupervisor.setError(mensagem1);
            textInputSupervisor.setFocusable (true);
            textInputSupervisor.requestFocus ();
         }else{
            textInputSupervisor.setError(null);
         }
         String lider = Objects.requireNonNull( textInputLider.getEditText(),"" ).getText().toString().trim();
         if(lider.equals ("")){
            validate = false;
            textInputLider.setError(mensagem1);
            textInputLider.setFocusable (true);
            textInputLider.requestFocus ();
         }else{
            textInputLider.setError(null);
         }
         String viceLider = Objects.requireNonNull( textInputViceLider.getEditText() ).getText().toString().trim();
         if(viceLider.equals ("")){
            validate = false;
            textInputViceLider.setError(mensagem1);
            textInputViceLider.setFocusable (true);
            textInputViceLider.requestFocus ();
         }else{
            textInputViceLider.setError(null);
         }
         String anfitriao = Objects.requireNonNull( textInputAnfitriao.getEditText() ).getText().toString().trim();
         if(anfitriao.equals ("")){
            validate = false;
            textInputAnfitriao.setError(mensagem1);
            textInputAnfitriao.setFocusable (true);
            textInputAnfitriao.requestFocus ();
         }else{
            textInputAnfitriao.setError(null);
         }
         String secretario = Objects.requireNonNull( textInputSecretario.getEditText() ).getText().toString().trim();
         if(secretario.equals ("")){
            validate = false;
            textInputSecretario.setError(mensagem1);
            textInputSecretario.setFocusable (true);
            textInputSecretario.requestFocus ();
         }else{
            textInputSecretario.setError(null);
         }
         String colaborador = Objects.requireNonNull( textInputColaborador.getEditText() ).getText().toString().trim();
         if(colaborador.equals ("")){
            validate = false;
            textInputColaborador.setError(mensagem1);
            textInputColaborador.setFocusable (true);
            textInputColaborador.requestFocus ();
         }else{
            textInputColaborador.setError(null);
         }
         String hora = hh+":"+mm;
         String datainicio = Objects.requireNonNull( textInputDataInicio.getEditText() ).getText().toString().trim();
         String status = "1";
         String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
         addDataHora();
         if(validate) {
            if ( !TextUtils.isEmpty ( celula ) && Logado && typeUserAdmin ) {
               String uid = Cells.push ( ).getKey ( );
               Celula cel = new Celula ( uid , celula , rede , supervisor , lider , viceLider , anfitriao , secretario , colaborador , dia , hora , datainicio , DataTime , status , userId , igreja );
               if ( uid == null ) throw new AssertionError ( );
               Cells.child ( "churchs/" + uidIgreja + "/cells/" ).child ( celula ).child ( uid ).setValue ( cel );
               
               Toast.makeText ( this , mensagem2 , Toast.LENGTH_LONG ).show ( );
            } else {
               Toast.makeText ( this , mensagem3 , Toast.LENGTH_LONG ).show ( );
               if ( !typeUserAdmin ) {
                  Toast.makeText ( this , mensagem5 , Toast.LENGTH_LONG ).show ( );
               }
            }
            
            Intent celulas = new Intent( AddCelulaActivity.this,CelulasActivity.class);
            startActivity( celulas );
            finish();
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
   
   public void  testNullvariable(String var){
      
      if ( "null".equals ( var ) ) {
         Toast.makeText ( this , mensagem4 , Toast.LENGTH_LONG ).show ( );
      } else if ( "".equals ( var ) ) {
         Toast.makeText ( this , mensagem4 , Toast.LENGTH_LONG ).show ( );
      } else if ( "   ".equals ( var ) ) {
         Toast.makeText ( this , mensagem4 , Toast.LENGTH_LONG ).show ( );
      }
   }
   
   @SuppressLint("SimpleDateFormat")
   public void addDataHora() {
      Date dataHoraAtual = new Date();
      String data = new SimpleDateFormat("dd/MM/yyyy").format(dataHoraAtual);
      String hora = new SimpleDateFormat("HH:mm:ss").format(dataHoraAtual);
      DataTime = data + " "+ hora;
      DataT = data;
   }
   
   @Override
   protected void onStart ( ) {
      super.onStart ( );
   }
   
   @Override
   protected void onResume ( ) {
      super.onResume ( );
   }
   
   @Override
   protected void onDestroy ( ) {
      super.onDestroy ( );
   }
   
}