package sv.edu.udb.parcial02_rt142164;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

public class Principal extends AppCompatActivity {

    /*Variables para obtener la instancia actual de firebase y el usuario actual cons sus
    * respectivas credenciales*/
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();

    private TextView emailGoogle;
    private Button btnregistro;
    private String authUser;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        inicioControles();


        /*Recibe este activity el correo y el tipo de autentificación del MainActivity,
         * estos pueden ser Google o Facebook*/

        Bundle bundle = getIntent().getExtras();


        /*Si existe usuario, entonces captura el correo y el id del usuario
        * autentificado segun el tipo de proveedor para poder mirarlo en la consola
        * de errores del logcat, a su vez el id captura mas el proveedor sea google
        * o facebook del usuario actual se enviaran a la ultima activity*/

        if (user != null){
          //  emailGoogle.setText(bundle.getString("correo"));
            emailGoogle.setText(user.getEmail());

           // authUser = bundle.getString("auth");
            for (UserInfo profile : user.getProviderData()) {
                // Id of the provider (ex: google.com)
                String providerId = profile.getProviderId();

                if (providerId.equals("facebook.com")){
                    Log.e("id de usuario", user.getUid());
                    Log.e("proveedor",providerId);
                    authUser = providerId;

                }
                else if(providerId.equals("google.com")){
                    Log.e("proveedor",providerId);
                    Log.e("id de usuario", user.getUid());
                    authUser = providerId;

                }

            }
            userId = user.getUid();
        }

        btnregistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registro();
            }
        });

    }

    /*Inicializamos los controles referentes al layout*/

    private void inicioControles() {
        emailGoogle = findViewById(R.id.tvEmail);
        btnregistro = findViewById(R.id.btnRegistro);
    }

    /*Función para abrir la ultima activity, la de registro, le enviaremos
    * como datos el tipo de autentificacion actual: facebook.com o google.com
    * y el id del usuario autentificado la cuenta actual*/

    private void registro(){
        Intent i = new Intent(Principal.this, Perfil.class );
        i.putExtra("authType",authUser);
        i.putExtra("iduser",userId);
        startActivity(i);
    }
}