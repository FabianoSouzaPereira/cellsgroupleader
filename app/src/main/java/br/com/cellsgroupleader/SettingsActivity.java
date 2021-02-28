package br.com.cellsgroupleader;

import android.content.*;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.*;

import br.com.cellsgroupleader.models.celulas.*;

import static br.com.cellsgroupleader.ValuesPaternActivity.DDI;
import static br.com.cellsgroupleader.ValuesPaternActivity.FONE;
import static br.com.cellsgroupleader.ValuesPaternActivity.LEADERID;
import static br.com.cellsgroupleader.ValuesPaternActivity.SHARED_PREFS;
import static br.com.cellsgroupleader.home.HomeActivity.celulaName;
import static br.com.cellsgroupleader.home.HomeActivity.userFone;
import static br.com.cellsgroupleader.home.HomeActivity.userId;
import static br.com.cellsgroupleader.home.HomeActivity.userUid;

public class SettingsActivity extends AppCompatActivity{
   
   @Override
   protected void onCreate( Bundle savedInstanceState ){
      super.onCreate( savedInstanceState );
      setContentView( R.layout.settings_activity );
      if( savedInstanceState == null ){
         getSupportFragmentManager( )
            .beginTransaction( )
            .replace( R.id.settings , new SettingsFragment( ) )
            .commit( );
      }
      ActionBar actionBar = getSupportActionBar( );
      if( actionBar != null ){
         actionBar.setDisplayHomeAsUpEnabled( true );
      }

   }
   
   public static class SettingsFragment extends PreferenceFragmentCompat{
      private String Celula;
   
      @Override
      public void onCreatePreferences( Bundle savedInstanceState , String rootKey ){
         setPreferencesFromResource( R.xml.root_preferences , rootKey );
         EditTextPreference celula = findPreference("Celula");
         EditTextPreference ID = findPreference("LiderID");
         EditTextPreference Fone = findPreference("Fone");
         
         SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
         SharedPreferences.Editor editor = preferences.edit();
         String ddi = preferences.getString(DDI, "" );
         String fone = preferences.getString(userFone, "" );
         String id = preferences.getString(userId, "" );
         celula.setText(celulaName);
         ID.setText(userUid );
         Fone.setText(userFone);
//  editor.putString(DDI, ddi);
//         editor.putString( Celula, "nome" );
//         editor.putString(FONE, userFone);
//         editor.putString(LEADERID,userId);
//         editor.apply();

   
         
      }
   }
}