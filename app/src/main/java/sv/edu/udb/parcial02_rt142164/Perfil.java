package sv.edu.udb.parcial02_rt142164;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import sv.edu.udb.parcial02_rt142164.data.Person;

public class Perfil extends AppCompatActivity {

    private FirebaseAuth authFirebase = FirebaseAuth.getInstance();
    public static FirebaseDatabase Database = FirebaseDatabase.getInstance();
    public static DatabaseReference refPersons = Database.getReference("Persons");
    DatabaseReference mread;

      private EditText etNames, etSurnames, etCarnet, etTelephone, etAge;
      private Button b_Save;
      private Button b_Cancel;
      public TextView PRUEBA,TV_Auth;
      public String authType;
      public String idUser;

      public String prueba="";


    String key = "";
    String names = "";
    String surnames = "";
    String carnet = "";
    String telephone = "";
    String age = "";
    String auth = "";
    String iduser = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);


        InitControls();

        /*Recibimos el tipo de proveedor y el id del usuario actual */
        Bundle bundle = getIntent().getExtras();
        authType = bundle.getString("authType");
        idUser = bundle.getString("iduser");

        b_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Save();
            }
        });

        b_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cancel();
            }
        });



        /*Evento donde a traves de la variable de database de firebase llamamos al hijo principal
        * que tenemos como referencia llamado Persons y a su vez llamados a los hijos dentro de esta
        * referencia a traves de la key de cada hijo que se va iterando en un for, asi es como
        * vamos capturando los datos de cada key  tomando como referencia la clase Person para que capture
        * los mismos datos que tiene la clase y para saber que datos capturar se valida el id user
        * que se envio de la activity principal del usuario actual con el id del usuario guardado en la base en el caso
        * que exista, y si existe un registro con ese mismo id user pues sera el registro unico para la cuenta
        * actual auntentificada, posteriormente se envia esos datos a los inputs*/
        mread = FirebaseDatabase.getInstance().getReference();

        mread.child("Persons").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (final DataSnapshot datos : snapshot.getChildren()){


                    mread.child("Persons").child(datos.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Person user = datos.getValue(Person.class);



                            prueba = user.getIduser();
                            Log.e("valor de id :",prueba);
                            if (prueba.equals(idUser)){
                                key = datos.getKey();
                                names = user.getNames();
                                surnames = user.getSurnames();
                                carnet = user.getCarnet();
                                telephone = user.getTelephone();
                                age = user.getAge();
                                auth = user.getAuth();
                            }



                            etNames.setText(names);
                            etSurnames.setText(surnames);
                            etCarnet.setText(carnet);
                            etTelephone.setText(telephone);
                            etAge.setText(age);
                            TV_Auth.setText(auth);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void InitControls(){
        etNames = findViewById(R.id.edtName);
        etSurnames = findViewById(R.id.edtSurnames);
        etCarnet = findViewById(R.id.edtCarnet);
        etTelephone = findViewById(R.id.edtTelephone);
        etAge = findViewById(R.id.edtAge);
        b_Save = findViewById(R.id.btnSave);
        b_Cancel = findViewById(R.id.btnCancel);
      //  PRUEBA = findViewById(R.id.prueba);
        TV_Auth = findViewById(R.id.tvAuth);
    }


    /*Al darle el boton save capaturamos los datos de los inputs,
    * los guardamos en variables locales, luego validamos por la variable auth, que se obtiene
    * al buscar el tipo de auntentificacion en la base de firebase si encuentra esta variable tendra valor
    * sino por defecto su valor es vacio y se llenara con push, sino es asi se llenara
    * por el valor de la key correspondiente al usuario actual tomando como referencia
    * el id user actual*/
    private void Save(){

        String Names = etNames.getText().toString();
        String Surnames = etSurnames.getText().toString();
        String Carnet = etCarnet.getText().toString();
        String Telephone = etTelephone.getText().toString();
        String Age = etAge.getText().toString();
        String Auth= TV_Auth.getText().toString();




        if (auth == ""){

            Person person = new Person(Names, Surnames, Carnet, Telephone, Age, authType, idUser);
            refPersons.push().setValue(person);
        }
        else {

            Person person = new Person(Names, Surnames, Carnet, Telephone, Age, Auth, idUser);
            refPersons.child(key).setValue(person);

        }

        



    }

    /*se cierra la sesion de firebase y se cierra la sesion de facebook para luego
    * regresar a la primer activity la MainActivity*/
    private void Cancel(){
        authFirebase.signOut();
        LoginManager.getInstance().logOut();
        startActivity(new Intent(this, MainActivity.class));
    }
}