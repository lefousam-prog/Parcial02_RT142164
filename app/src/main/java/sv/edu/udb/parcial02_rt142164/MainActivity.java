package sv.edu.udb.parcial02_rt142164;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {


    private static final int Google_sign_in =10;
    private Button btnGoogle;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private String authType;

    private CallbackManager callbackManager;
    private LoginButton btnFacebook;
    private static final String EMAIL = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        callbackManager = CallbackManager.Factory.create();

        inicioControles();

        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSign();
            }
        });







        btnFacebook = (LoginButton) findViewById(R.id.login_button);
        btnFacebook.setPermissions(Arrays.asList(EMAIL));
        // If you are using in a fragment, call loginButton.setFragment(this);

        // Callback registration
        btnFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code

                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });


    }

    /*Inicializamos los controles referentes al layout*/

    private void inicioControles() {
        btnGoogle = findViewById(R.id.google);
        progressBar = findViewById(R.id.progressBar);
        btnFacebook = findViewById(R.id.login_button);
    }

    private void googleSign() {

        /*Configuración basica para el logueo por defecto con google a través del uso de un token
        * que asocie el correo del usuarioj*/

        GoogleSignInOptions googleLog = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        /*Creamos el cliente de autentificación de Google, primero en el contexto de este Activity
        * llamados el cliente configurado con la instancia de logueo previamente echa, le pasamos una key
        * que enviara a google como requestcode dado que en este activity necesitamos un codigo de respuesta
        * para saber si se ha completo y autentificado el logueo correctamente o no*/

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, googleLog);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, Google_sign_in);
        mGoogleSignInClient.signOut();

    }


    private void handleFacebookAccessToken(AccessToken token) {


        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");

                           // Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
                           // FirebaseUser user = mAuth.getCurrentUser();
                            openProfile();


                        } else {
                            // If sign in fails, display a message to the user.
                           // Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void openProfile() {
        startActivity(new Intent(this, Principal.class));
    }

 /*   @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null){
            openProfile();
        }
    }*/


    /*Metodo de respuesta a este activity que espera una accion respeto a la autentificacion de Google*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        /*Verificamos si el requestcode que devuelve la operación de Google es igual al que enviamos
        * si es asi recuperamos los datos de la cuenta autentificada y los guardamos en la variable
        * account*/

        if(requestCode == Google_sign_in){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                /*En el caso de que la cuenta no sea nula, es decir exista, luego de autentificarse con Google
                * debemos de registrar estas credenciales en firebase, las credenciales se guardaran
                * en la variable credential*/

                if(account != null){
                    AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                    FirebaseAuth.getInstance().signInWithCredential(credential)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    /*Si se ha registrado exitosamente los datos en Firebase entonces se procedera
                                    * a cambiar al activity Principal
                                    * sino marcara un Toast un posible error de llenado*/

                                    if(task.isSuccessful()){
                                        Toast.makeText(getApplicationContext(), "Registro exitoso en Firebase", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);

                                        authType = "google";

                                        Intent intent = new Intent(MainActivity.this, Principal.class);
                                        intent.putExtra("correo",account.getEmail());
                                        intent.putExtra("auth",authType);
                                        startActivity(intent);
                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(), "Registro fallido, intentelo más tarde", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }

                                }
                            });
                }

            } catch (ApiException e){

            }
        }
    }



}