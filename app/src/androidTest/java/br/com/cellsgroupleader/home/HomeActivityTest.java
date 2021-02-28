package br.com.cellsgroupleader.home;

import android.content.*;

import androidx.test.ext.junit.runners.*;
import androidx.test.platform.app.*;

import com.google.firebase.*;
import com.google.firebase.auth.*;
import com.google.firebase.database.*;

import org.junit.*;
import org.junit.runner.*;

import static org.junit.Assert.*;

@RunWith( AndroidJUnit4.class )
public class HomeActivityTest{
   public FirebaseAuth mAuth;
   private FirebaseDatabase firebaseDatabase;
   private DatabaseReference databaseReference;
   Context appContext;
   
   @Test
   public void useAppContext( ){
      // Context of the app under test.
      appContext = InstrumentationRegistry.getInstrumentation( ).getTargetContext( );
      assertEquals( "br.com.cellsgroupleader" , appContext.getPackageName( ) );
   }
   
   @Test
   public void onCreatedFirebaseService(){
      FirebaseApp.initializeApp(appContext);
      firebaseDatabase = FirebaseDatabase.getInstance("https://cellsgroup-383d5-default-rtdb.firebaseio.com/");
      assertNotNull(firebaseDatabase);
      databaseReference = firebaseDatabase.getReference();
      assertNotNull(databaseReference );
      databaseReference.keepSynced(true);
   }
}