package br.com.cellsgroupleader.home;

import android.annotation.SuppressLint;
import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.*;
import android.os.*;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.*;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.functions.FirebaseFunctions;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.*;

import br.com.cellsgroupleader.*;
import br.com.cellsgroupleader.R;
import br.com.cellsgroupleader.celulas.CelulasActivity;
import br.com.cellsgroupleader.contato.ContatoActivity;
import br.com.cellsgroupleader.igreja.IgrejasCriadasActivity;
import br.com.cellsgroupleader.igreja.addIgrejaActivity;
import br.com.cellsgroupleader.leader.LeaderActivity;
import br.com.cellsgroupleader.models.igreja.Igreja;
import br.com.cellsgroupleader.models.login.LoginActivity;
import br.com.cellsgroupleader.models.pessoas.*;
import br.com.cellsgroupleader.relatorios.RelatorioActivityView;

import static br.com.cellsgroupleader.ValuesPaternActivity.DDI;
import static br.com.cellsgroupleader.ValuesPaternActivity.FONE;
import static br.com.cellsgroupleader.ValuesPaternActivity.LEADERID;
import static br.com.cellsgroupleader.ValuesPaternActivity.SHARED_PREFS;
import static br.com.cellsgroupleader.models.login.LoginActivity.updateUI;


public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
   public static FirebaseUser UI;
   private static final String CHANNEL_ID = "Relatório";
   private static final String FILE_NAME = "Config.txt";
   public static String group="";
   public static String igreja = "";
   public static String useremail = "";
   public static String uidIgreja = "";
   public static String userId = "";
   public static String userFoneDDI = "";
   public static String userUid = "";
   public static String userFone = "";
   public static String celulaName = "";
   public static String leaderName = "";
   public static String useremailAuth = "";
   public static boolean typeUserAdmin = true;
   public FirebaseAuth mAuth;
   private FirebaseDatabase firebaseDatabase;
   private DatabaseReference databaseReference;
   private DatabaseReference novaref2 = null;
   private DatabaseReference novaref3 = null;
   private FirebaseFunctions mFunctions;
   public static final int  Permission_All = 1;
   public static final int PERMISSION_CODE = 3;
   public static final String[] Permissions = new String[]{
      // Any permision is necessary
   };
   public static boolean Logado = false;
   public static String tag = "0";
   private static int count = 0;
   TextView nhTitle;
   TextView nhEmail;
   TextView nhName;
   Query query3;
   ValueEventListener query3listener;
   Query query4;
   ValueEventListener query4listener;
  
   
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate( savedInstanceState );
      inicializarFirebase();
      UI = FirebaseAuth.getInstance().getCurrentUser();
      if(UI != null) {
         updateUI ( UI ); //verifica se leader está logado
      }
      setContentView( R.layout.activity_home );
      if ( !Logado ){
         Intent intent = new Intent( HomeActivity.this, LoginActivity.class );
         startActivity( intent );
         return;
      }
      SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE );
      String  ddi = sharedPreferences.getString(DDI, "" );
      userFone = sharedPreferences.getString(FONE, "" );
      userUid = sharedPreferences.getString(LEADERID, "" );

      if (userFone.isEmpty() || userUid.isEmpty() || ddi.isEmpty()){
         Intent pattern = new Intent( HomeActivity.this, ValuesPaternActivity.class );
         startActivity( pattern );
         return;
      }
      mAuth = FirebaseAuth.getInstance();
      mFunctions = FirebaseFunctions.getInstance();
      
      Toolbar toolbar = findViewById( R.id.toolbar_home );
      setSupportActionBar(toolbar);
      
      init();
      addDataHora();
      mAuth.getUid ();
      try {
         if ( mAuth != null ) {
            useremailAuth = mAuth.getCurrentUser ().getEmail ();
         }
      } catch ( Exception e ) {
         e.printStackTrace ( );
      }
      
      DrawerLayout drawer = findViewById( R.id.drawer_activityHome);
      NavigationView navigationView = findViewById( R.id.nav_view_home );
      ActionBarDrawerToggle toggle = new ActionBarDrawerToggle ( this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );
      drawer.addDrawerListener( toggle );
      toggle.syncState();
      navigationView.setNavigationItemSelectedListener( this );
      
      View headerView = navigationView.getHeaderView(0);
      nhTitle = headerView.findViewById (R.id.nhTitle);
      nhName = headerView.findViewById (R.id.nhName);
      nhEmail = headerView.findViewById (R.id.nhEmail);
      nhEmail.setText (useremailAuth);

   }
   
   @Override
   protected void onStart() {
      super.onStart();
      if(UI != null) {
         updateUI ( UI ); //verifica se leader está logado
      }
      if ( !Logado ){
         Intent intent = new Intent( HomeActivity.this, LoginActivity.class );
         startActivity( intent );
      }
      
      pegarPadroes();
   }

   public void pegarPadroes() {
      final String ui;
      if (  UI == null ) {
         return;
      }
      ui = UI.getUid ();
      if ( ui.isEmpty()){
         Toast.makeText( this, "Você não está logado!", Toast.LENGTH_LONG ).show();
         return;
      }
      
      novaref3 = databaseReference.child("churchs/");
      query4 = novaref3.orderByChild("members").startAt(userFone, userUid).limitToFirst(1);
      query4listener = new ValueEventListener( ){
         @Override
         public void onDataChange( @NonNull DataSnapshot snapshot ){
            for(DataSnapshot sd : snapshot.getChildren ()){
               for(DataSnapshot ds: sd.getChildren()){
                  String key0 = ds.getKey();
                  if(!key0.equalsIgnoreCase ( "members" )
                     && !key0.equalsIgnoreCase ( "leaders" )
                     && !key0.equalsIgnoreCase ( "cells" )
                     && !key0.equalsIgnoreCase ( "reports" )
                     && !key0.equalsIgnoreCase ( "intercession" )
                     && !key0.equalsIgnoreCase ( "Skedule" )){
                        Igreja igr = ds.getValue( Igreja.class );
                        igreja = igr.getNome( );
                        group = igr.getGroup( );
                        uidIgreja = igr.getIgrejaID( );
                        userId = igr.getUser( );
                  }
                  String key = ds.getKey();
                  if(key.equalsIgnoreCase("leaders")){
                     try{
                        HashMap<String, Object> dataMap = (HashMap<String, Object>) ds.getValue();
                        for(String keys : dataMap.keySet()){
                          
                              Object data = dataMap.get( keys );
                              HashMap<String, Object> leaderData = (HashMap<String, Object>) data;
                              String uid = leaderData.get("uid").toString();
                           if(uid.equalsIgnoreCase(userUid) ){
                              leaderName = leaderData.get("nome").toString();
                              celulaName = leaderData.get("celula").toString();
                           }
                        }
                     }catch( Exception e ){
                        e.printStackTrace( );
                     }
                  }
               }
            }
            nhTitle.setText (group);
            nhName.setText(igreja);
         }
      
         @Override
         public void onCancelled( @NonNull DatabaseError error ){
         
         }
      };
      query4.addValueEventListener(query4listener);
 
   }
   
   public void mainloadFile() {
      File file = new File( getFilesDir() + "/" + FILE_NAME );
      if (file.exists()) {
         FileInputStream fis = null;
         try {
            
            fis = openFileInput( FILE_NAME );
            InputStreamReader isr = new InputStreamReader( fis );
            BufferedReader br = new BufferedReader( isr );
            StringBuilder sb = new StringBuilder();
            String text;
            
            while ((text = br.readLine()) != null) {
               sb.append( text ).append( "\n" );
            }
            
            String read = sb.toString();
            String [] v = read.split(",");
            userFone = v[0];
            userUid = v[1];
            
         } catch ( FileNotFoundException e) {
            e.printStackTrace();
         } catch (IOException e) {
            e.printStackTrace();
         } finally {
            try {
               Objects.requireNonNull(fis).close();
            } catch (IOException e) {
               e.printStackTrace();
            }
         }
      }
   }
   
   
   @Override
   protected void onResume() {
      super.onResume();
      if (!hasPhonePermissions( this, Permissions )) {
         ActivityCompat.requestPermissions( this,Permissions,Permission_All );
      }
   }
   
   private static boolean hasPhonePermissions( Context context, String... permissions) {
      if(context != null && permissions != null){
         for(String permission: permissions){
            if(ActivityCompat.checkSelfPermission( context, permission ) != PackageManager.PERMISSION_GRANTED){
               return false;
            }
         }
      }
      return true;
   }
   
   @Override
   public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
      if(requestCode == PERMISSION_CODE){
         if(grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Toast.makeText( HomeActivity.this, "Permission allowed" , Toast.LENGTH_SHORT).show();
         }else{
            Toast.makeText( HomeActivity.this, "Permission denied" , Toast.LENGTH_SHORT).show();
         }
      }
   }
   
   @Override
   protected void onPause() {
      if(query4listener != null){
         query4.removeEventListener(query4listener);
      }
      super.onPause();
   }
   
   @Override
   protected void onStop() {
      super.onStop();
   }
   
   @Override
   protected void onDestroy() {
      super.onDestroy();
   }
   
   @Override
   protected void onRestart() {
      super.onRestart();
   }
   
   @Override
   public void onBackPressed() {
      if( count == 1){
         count=0;
         AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
         builder  = builder.setMessage( "Deseja encerrar o aplicativo ?" );
         builder.setTitle( "Encerrando o aplicativo..." )
            .setCancelable( false )
            .setNegativeButton( "cancelar", ( dialog , which ) -> {
            } )
            .setPositiveButton( "Ok", ( dialog , which ) -> {
               try {
                  //  Finish this activity as well as all activities immediately below it in the current task that have the same affinity.
                  finishAffinity();
               } catch (Throwable throwable) {
                  throwable.printStackTrace();
               }
            } );
      
         AlertDialog alertDialog = builder . create () ;
         alertDialog.show();
      }else{
         count=1;
         super.onBackPressed();
      }
   }
   
   private void inicializarFirebase() {
      FirebaseApp.initializeApp(HomeActivity.this);
      firebaseDatabase = FirebaseDatabase.getInstance();
      databaseReference = firebaseDatabase.getReference();
      databaseReference.keepSynced(true);
   }
   
   @SuppressLint("SimpleDateFormat")
   public void addDataHora() {
      
      try {
         Date dataHoraAtual = new Date();
         String data = new SimpleDateFormat("dd/MM/yyyy").format(dataHoraAtual);
         String hora = new SimpleDateFormat("HH:mm:ss").format(dataHoraAtual);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
   
   public void init(){
      int addigreja = R.id.action_addIgreja;
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
         Intent addigreja = new Intent ( HomeActivity.this  , addIgrejaActivity.class );
         startActivity ( addigreja );
         return true;
      } else if ( itemId == R.id.action_readIgreja ) {
         Intent readigreja = new Intent ( HomeActivity.this , IgrejasCriadasActivity.class );
         startActivity ( readigreja );
         return true;
      }else if ( itemId == R.id.action_lideres) {
         Intent addlideres = new Intent ( HomeActivity.this , LeaderActivity.class );
         startActivity ( addlideres);
         return true;
      }else if ( itemId == R.id.action_settings) {
         Intent settings = new Intent ( HomeActivity.this , SettingsActivity.class );
         startActivity ( settings );
         return true;
      }else if ( itemId == R.id.action_Sobre) {
         Intent sobre= new Intent ( HomeActivity.this , SobreActivity.class );
         startActivity ( sobre);
         return true;
      }else if ( itemId == R.id.action_Sair ) {
         finishAffinity ();
         return true;
      } else if ( itemId == R.id.action_Logout ) {
         FirebaseAuth.getInstance ( ).signOut ( );
         updateUI ( null );
         Toast.makeText ( this , getString ( R.string.Logout_sucesso ) , Toast.LENGTH_SHORT ).show ( );
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
         Intent celulas = new Intent( HomeActivity.this, CelulasActivity.class );
         celulas.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_SINGLE_TOP);
         startActivity( celulas );
         
      } else if (id == R.id.nav_leader) {
         Intent agenda = new Intent( HomeActivity.this, LeaderActivity.class );
         startActivity( agenda );
         
      }else if (id == R.id.nav_realatorio) {
         Intent relatorio = new Intent( HomeActivity.this, RelatorioActivityView.class );
         startActivity( relatorio );
         
      } else if (id == R.id.nav_contact) {
         Intent contato = new Intent( HomeActivity.this, ContatoActivity.class );
         startActivity( contato );
         
      }
      
      DrawerLayout drawer = findViewById( R.id.drawer_activityHome );
      drawer.closeDrawer( GravityCompat.START );
      return true;
   }
   
   /**  Click do card celula
    *
    */
   public void cardcellClick(View view) throws Exception{
      Intent celulas = new Intent(HomeActivity.this,CelulasActivity.class);
      startActivity( celulas );
   }
   
   public void cardigrejaClick(View view) throws Exception {
      Intent igrejas = new Intent(HomeActivity.this,IgrejasCriadasActivity.class);
      startActivity( igrejas );
   }
   
   public void cardleaderClick(View view) throws Exception{
      Intent leader = new Intent( HomeActivity.this,LeaderActivity.class );
      startActivity( leader );
   }
   
   public void cardcontatoClick(View view) throws Exception {
      Intent contato = new Intent( HomeActivity.this,ContatoActivity.class );
      startActivity( contato );
   }
   
   public void cardrelatorioClick(View view) {
      Intent relatorio = new Intent( HomeActivity.this, RelatorioActivityView.class );
      startActivity( relatorio );
   }
   public void cardintercessaoClick(View view) {
      Snackbar.make(view, getString ( R.string.implementacao_futura ), Snackbar.LENGTH_LONG)
         .setAction("Action", null)
         .setTextColor(getColor(R.color.colorWhite))
         .show();
   }
}