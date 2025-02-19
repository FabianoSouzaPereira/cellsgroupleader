package br.com.cellsgroupleader.models.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.*;

import br.com.cellsgroupleader.R;
import br.com.cellsgroupleader.RegisterActivity;
import br.com.cellsgroupleader.RememberActivity;
import br.com.cellsgroupleader.home.HomeActivity;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
   private static final int RC_SIGN_IN = 100;
   // [START declare_auth]
   private FirebaseAuth mAuth;
   private TextInputLayout editEmail = null;
   private TextInputLayout editSenha = null;
   private Button btnCancelarLogin;
   private ImageButton btnGoogleConection;
   private ProgressDialog progressDialog;
   private static final String TAG = "CustomAuthActivity";
   private String mCustomToken;
   private TokenBroadcastReceiver mTokenReceiver;
   
   private GoogleSignInOptions gso;
   private GoogleSignInClient mGoogleSignInClient;
   private View btnForgotPassword;
   
   
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate( savedInstanceState );
      setContentView( R.layout.activity_login );
      mAuth = FirebaseAuth.getInstance();
      mAuth.getUid ();
      try {
         if ( mAuth != null ) {
            String  uid = mAuth.getUid ();
         }
      } catch ( Exception e ) {
         e.printStackTrace ( );
      }
      
      // Configure Google Sign In
      gso = new GoogleSignInOptions.Builder( GoogleSignInOptions.DEFAULT_SIGN_IN)
         .requestIdToken(getString(R.string.default_web_client_id))
         .requestEmail()
         .build();
      // Build a GoogleSignInClient with the options specified by gso.
      mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
      
      
      editEmail = findViewById( R.id.email );
      editEmail.requestFocus ( );
      editSenha = findViewById( R.id.password );
      btnCancelarLogin = findViewById (R.id.btnCancelarLogin );
      btnCancelarLogin.setOnClickListener (this);
      btnGoogleConection = findViewById (R.id.btnGoogleConection );
      btnGoogleConection.setOnClickListener (this);
      btnForgotPassword = findViewById (R.id.btnEsqueci_Senha );
      progressDialog = new ProgressDialog( this );
      
      // Create token receiver (for demo purposes only)
      mTokenReceiver = new TokenBroadcastReceiver() {
         @Override
         public void onNewToken(String token) {
            Log.d( TAG, "onNewToken:" + token );
            setCustomToken( token );
         }
      };
      
   }
   
   private void logarUsuario(){
      final String email = editEmail.getEditText().getText().toString().trim();
      String senha = editSenha.getEditText().getText().toString().trim();
      
      if (!validateForm()) {
         return;
      }
      try {
         progressDialog.setMessage( getString( R.string.iniciando_login) );
         progressDialog.show();
         
         //Consultar se leader existe
         mAuth.signInWithEmailAndPassword( email, senha )
            .addOnCompleteListener(LoginActivity.this, task -> {
               //checando sucesso
               if(task.isSuccessful()){
                  int pos = email.indexOf("@");
                  String user = email.substring(0,pos);
                  Toast.makeText( LoginActivity.this, getString( R.string.logado_sucesso),Toast.LENGTH_SHORT).show();
                  FirebaseUser currentUser = mAuth.getCurrentUser();
                  updateUI(currentUser);
                  editEmail.getEditText().setText("");
                  editSenha.getEditText().setText("");
                  Intent home = new Intent( LoginActivity.this, HomeActivity.class );
                  startActivity( home );
                  //  startSignIn();
               }else{  //se houver colisão de mesmo usuário
                  if (task.getException() instanceof FirebaseAuthUserCollisionException){
                     Toast.makeText( LoginActivity.this, getString( R.string.usuario_existe),Toast.LENGTH_SHORT).show();
                     HomeActivity.Logado = false;
                  }else{
                     Toast.makeText( LoginActivity.this,getString( R.string.falha_login), Toast.LENGTH_LONG ).show();
                     HomeActivity.Logado = false;
                  }
                  
               }
               progressDialog.dismiss();
            } );
         
      } catch ( Exception e ) {
         e.printStackTrace ( );
      }
      
   }
   
   @Override
   public void onClick(View v) {
      
      int id = v.getId ( );
      if ( id == R.id.btnEnviarLogin ) {
         logarUsuario ( );
      } else if ( id == R.id.btnCancelarLogin ) {
         finishAffinity ( );
      }else if ( id == R.id.btnEsqueci_Senha){
         Intent remember = new Intent( LoginActivity.this, RememberActivity.class );
         startActivity( remember );
      }else if (id == R.id.btnRegistro){
         Intent register= new Intent( LoginActivity.this, RegisterActivity.class );
         startActivity( register );
      }else if(id == R.id.btnGoogleConection){
         signIn();
      }
   }
   
   private void setCustomToken(String token) {
      mCustomToken = token;
      
      String status;
      if (mCustomToken != null) {
         status = "Token:" + mCustomToken;
      } else {
         status = "Token: null";
      }
   }
   
   private boolean validateForm() {
      boolean valid = true;
      String email = editEmail.getEditText().getText().toString().trim();
      if (TextUtils.isEmpty(email) || !validateEmailFormat( email ) ) {
         Toast.makeText(this,getString( R.string.Email_erro), Toast.LENGTH_LONG).show();
         editEmail.setError(getString( R.string.obrigatorio_email_valid));
         editEmail.setFocusable ( true );
         editEmail.requestFocus ( );
         valid = false;
      }else{
         editEmail.setError(null);
      }
      
      String senha = editSenha.getEditText().getText().toString().trim();
      if(senha .equals ("")|| senha .length() < 6 ) {
         valid = false;
         editSenha.setError (getString( R.string.obrigatorio_maior_5));
         editSenha.setFocusable ( true );
         editSenha.requestFocus ( );
      }else{
         editSenha.setError(null);
      }
      
      return valid;
   }
   
   public static void updateUI(FirebaseUser user){
      if (user != null) {
         HomeActivity.Logado = true;
         String name = user.getDisplayName();
         String email = user.getEmail();
         Uri photoUrl = user.getPhotoUrl();
         
         // Check if user's email is verified
         boolean emailVerified = user.isEmailVerified();
         
         // The user's ID, unique to the Firebase project. Do NOT use this value to
         // authenticate with your backend server, if you have one. Use
         // FirebaseUser.getIdToken() instead.
         String uid = user.getUid();
         
      } else {
         HomeActivity.Logado = false;
      }
   }
   
   //start google conection
   private void signIn() {
      Intent signInIntent = mGoogleSignInClient.getSignInIntent();
      startActivityForResult(signInIntent, RC_SIGN_IN);
   }
   @Override
   public void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
      progressDialog.setMessage( getString( R.string.iniciando_login) );
      progressDialog.show();
      if (requestCode == RC_SIGN_IN) {
         Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
         try {
            // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount account = task.getResult(ApiException.class);
            Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
            firebaseAuthWithGoogle(account.getIdToken());
         } catch (ApiException e) {
            // Google Sign In failed, update UI appropriately
            Log.w(TAG, "Google sign in failed", e);
            // ...
         }
         progressDialog.dismiss();
      }
   }
   private void firebaseAuthWithGoogle(String idToken) {
      AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
      progressDialog.setMessage( getString( R.string.iniciando_login) );
      progressDialog.show();
      mAuth.signInWithCredential(credential)
         .addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
               // Sign in success, update UI with the signed-in user's information
               Log.d(TAG, "signInWithCredential:success");
               FirebaseUser user = mAuth.getCurrentUser();
               updateUI(user);
               Intent home = new Intent( LoginActivity.this, HomeActivity.class );
               progressDialog.dismiss();
               startActivity( home );
               
            } else {
               // If sign in fails, display a message to the user.
               Log.w(TAG, "signInWithCredential:failure", task.getException());
               updateUI(null);
            }
            
            // ...
         } );
   }
   //finish google connection
   
   @Override
   public void onStart() {
      super.onStart();
      // Check if user is signed in (non-null) and update UI accordingly.
      FirebaseUser currentUser = mAuth.getCurrentUser();
      updateUI(currentUser);
   }
   
   @Override
   protected void onResume() {
      super.onResume();
      registerReceiver(mTokenReceiver, TokenBroadcastReceiver.getFilter());
   }
   
   @Override
   public void onBackPressed ( ) {
      int count = getSupportFragmentManager().getBackStackEntryCount();
      
      if (count == 0) {
         super.onBackPressed();
         finishAffinity ( );
      } else {
         getSupportFragmentManager().popBackStack ();
      }
   }
   
   @Override
   protected void onPause() {
      super.onPause();
      unregisterReceiver(mTokenReceiver);
   }
   
   @Override
   public void onStop() {
      super.onStop();
   }
   
   private boolean validateEmailFormat(final String email) {
      return android.util.Patterns.EMAIL_ADDRESS.matcher( email ).matches();
      //  Toast.makeText( LoginActivity.this,"Email inválido", Toast.LENGTH_LONG ).show();
   }
}
