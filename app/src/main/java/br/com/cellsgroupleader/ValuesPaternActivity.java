package br.com.cellsgroupleader;

import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;

import androidx.appcompat.app.*;

import com.google.android.material.textfield.*;

import java.util.*;

import br.com.cellsgroupleader.home.*;
import br.com.cellsgroupleader.utils.*;

import static br.com.cellsgroupleader.home.HomeActivity.*;

public class ValuesPaternActivity extends AppCompatActivity{
   public static final String SHARED_PREFS = "sharedPrefs";
   public static final String DDI = "ddi";
   public static final String  FONE = "fone";
   public static  final String LEADERID= "leaderid";
   private static boolean validate = true;
   Button btnAvancar;
   private View.OnClickListener listener;
   private TextInputLayout textDDI;
   private TextInputLayout textFone;
   private TextInputLayout textID;
   private String ddi;
   private String fone;
   private String id;
   String mensagem1;
   String mensagem2;
   String mensagem3;
   String mensagem5;
   String mensagem6;
   String mensagem7;
   String mensagem8;
   String mensagem9;
   String mensagem10;
   
   @Override
   protected void onCreate( Bundle savedInstanceState ){
      super.onCreate( savedInstanceState );
      setContentView( R.layout.activity_values_patern );

      initComponent();
      loadData();
      UpdateData();
   }
   
   private void initComponent( ){
      textDDI = findViewById(R.id.txtDDI );
      Objects.requireNonNull ( textDDI.getEditText ( ),"+55" ).addTextChangedListener ( MaskEditUtil.mask (textDDI, MaskEditUtil.FORMAT_DDI) );
      textFone = findViewById(R.id.txtfone);
      Objects.requireNonNull ( textFone.getEditText ( ),"(00)00000-0000" ).addTextChangedListener ( MaskEditUtil.mask (textFone, MaskEditUtil.FORMAT_FONE) );
      textID = findViewById(R.id.txtcodigoID);
      btnAvancar = findViewById(R.id.txtAvancar );
      btnAvancar.setOnClickListener(btnListener);
      mensagem1 =  getResources ().getString (R.string.erroCampoObrigatorio);
      mensagem2 =  getResources ().getString (R.string.criadoleader);
      mensagem3 =  getResources ().getString (R.string.erroCriarleader);
      mensagem5 =  getResources ().getString (R.string.erroNaoAdmin);
      mensagem6 = getResources ().getString (R.string.erroCampo3digitos);
      mensagem7 = getResources ().getString (R.string.erroCampo11digitos);
      mensagem8 = getResources ().getString (R.string.erroEmailexiste);
      mensagem9 = getResources ().getString (R.string.escolhaCelula);
      mensagem10 = getResources ().getString (R.string.erroCampoInvalido);
   }
   
   public View.OnClickListener btnListener= new View.OnClickListener( ){
        @Override
        public void onClick( View v ){
           saveData();
        }
   };
  
  public void saveData(){
   validate = true;
   String ddi = Objects.requireNonNull( textDDI.getEditText( ),"" ).getText().toString().trim();
     if( ddi.equals ("")||  ddi.length() > 3){
        validate = false;
        textDDI.setError(mensagem6);
        textDDI.setFocusable (true);
        textDDI.requestFocus ();
     }else{
        textDDI.setError(null);
     }
   String fone = Objects.requireNonNull( textFone.getEditText( ),"" ).getText().toString().trim();
     if( fone.equals ( "" ) || fone.length ( ) < 14 ){
        validate = false;
        textFone.setError(mensagem7);
        textFone.setFocusable (true);
        textFone.requestFocus ();
     }else{
        textFone.setError(null);
     }
   String id = Objects.requireNonNull( textID.getEditText( ),"" ).getText().toString().trim();
     if( id.isEmpty()){
        validate = false;
        textID.setError(mensagem1);
        textID.setFocusable (true);
        textID.requestFocus ();
     }else{
        textID.setError(null);
     }
   
     if(validate){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE );
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DDI, ddi);
        editor.putString(FONE,fone);
        editor.putString(LEADERID,id);
      
        editor.apply();
   
        Intent intent = new Intent(ValuesPaternActivity.this, HomeActivity.class );
        startActivity( intent );
     }
  }
  
  public void loadData(){
      SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE );
      ddi = sharedPreferences.getString(DDI, "" );
      fone = sharedPreferences.getString(FONE, "" );
      id = sharedPreferences.getString(LEADERID, "" );
      userFoneDDI = ddi;
      userFone = fone;
      userUid = id;
  }
  
  public void UpdateData(){
     Objects.requireNonNull( textDDI.getEditText( ),"" ).setText(ddi);
     Objects.requireNonNull( textFone.getEditText( ),"" ).setText(fone);
     Objects.requireNonNull( textID.getEditText( ),"" ).setText(id);
  }
   
   @Override
   public void onBackPressed( ){
      finishAffinity();
   }
}