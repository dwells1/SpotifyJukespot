package com.example.jukespot.spotifyjukespot;


import com.example.jukespot.spotifyjukespot.Classes.User;
import com.example.jukespot.spotifyjukespot.Logging.Logging;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;



public class Login extends Activity{
    private Button bRegLogin;
    private EditText edUsername;
    private EditText edPassword;
    private TextView newMemberText;

    User user;
    private static final String TAG = Login.class.getSimpleName();
    private Logging log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        initAllLayoutInteractions();
        log = new Logging();
    }

    public void initAllLayoutInteractions(){
        bRegLogin = findViewById(R.id.bRegLogin);
        edUsername = (EditText) findViewById(R.id.edRegUsername);
        edPassword = (EditText) findViewById(R.id.edRegPassword);
        bRegLogin = (Button) findViewById(R.id.bRegLogin);
        newMemberText = findViewById(R.id.newMemberText);

    }

    public void onRegLoginClicked(View view){
        log.logMessage(TAG,"LOGIN REG PRESSED");
        /*TODO: check username and password are in database*/
        String UserName = edUsername.getText().toString();
        String Password = edPassword.getText().toString();

        if(UserName.equals("") || Password.equals("")){
            log.logMessageWithToast(this ,TAG,"Username or Password is empty");

        }else{
            /*Assume Password and Username are Correct and go to next instance*/
            log.logMessageWithToast(this,TAG,"Login Successful");
            user = new User(UserName, Password);
            startJukeboxOptions();
        }
    }

    public void onNewMemberClicked(View view){
        log.logMessage(TAG, "PRESSED NEW MEMBER!");
        /*TODO: handle new member registration*/
    }

    private void startJukeboxOptions(){
        Intent jukeboxOptionsIntent = new Intent(this, JukeboxUserOptions.class);
        startActivity(jukeboxOptionsIntent);
        finish();
    }


}
