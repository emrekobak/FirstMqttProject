/*
 * Copyright © 2021. Development by Mehmet Emre KOBAK
 * Contact: kobakmehmetemre@gmail.com
 *
 */

package com.memrekobak.myapplication;


import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    String myHost;
    String username;
    String password;
    EditText txtServer;
    EditText txtUser;
    EditText txtPasswd;
    Button btnS;
    Button btnP;
    Button exit;
    String message;
    String mytopic;
    TextView subText;
    RadioButton pub;
    RadioButton sub;
    EditText editTopic;
    EditText editMessage;
    EditText topic;
    EditText subMessage;
    EditText subTopic;
    MqttAndroidClient client;
    SharedPref sharedPref;
    String spUser, spPasswd, spBroker;


    public void logout(View view) {
        Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_SHORT).show();
        pub.setVisibility(View.INVISIBLE);
        sub.setVisibility(View.INVISIBLE);
        editTopic.setVisibility(View.INVISIBLE);
        editMessage.setVisibility(View.INVISIBLE);
        subText.setVisibility(View.INVISIBLE);
        btnP.setVisibility(View.INVISIBLE);
        btnS.setVisibility(View.INVISIBLE);
        exit.setVisibility(View.INVISIBLE);
        txtServer.getText().clear();
        txtUser.getText().clear();
        txtPasswd.getText().clear();
        sharedPref.clearUser();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pub = findViewById(R.id.radioPub);
        sub = findViewById(R.id.radioSub);
        editTopic = findViewById(R.id.editTopic);
        editMessage = findViewById(R.id.editMessage);
        subText = findViewById(R.id.subText);
        btnP = findViewById(R.id.btnPub);
        btnS = findViewById(R.id.btnSub);
        exit = findViewById(R.id.btnCık);
        editTopic = findViewById(R.id.editTopic);
        editMessage = findViewById(R.id.editMessage);
        subText = findViewById(R.id.subText);
        btnP = findViewById(R.id.btnPub);
        btnS = findViewById(R.id.btnSub);
        txtServer = findViewById(R.id.editTextServer);
        txtUser = findViewById(R.id.editTextUser);
        txtPasswd = findViewById(R.id.editTextPasswd);
        sharedPref = new SharedPref(getApplicationContext());
        HashMap userLog = sharedPref.getUserDetails();
        spUser = (String) userLog.get(sharedPref.KEY_USER);
        spPasswd = (String) userLog.get(sharedPref.KEY_PASSWORD);
        spBroker = (String) userLog.get(sharedPref.KEY_BROKER);
        if (!(TextUtils.isEmpty(spUser) && TextUtils.isEmpty(spPasswd) && TextUtils.isEmpty(spBroker))) {
            String clientId = MqttClient.generateClientId();
            client = new MqttAndroidClient(this.getApplicationContext(), spBroker, clientId);
            MqttConnectOptions options = new MqttConnectOptions();       //user-password options
            options.setUserName(spUser);
            options.setPassword(spPasswd.toCharArray());

            try {
                IMqttToken token;
                token = client.connect(options);
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        // connected
                        Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                        pub.setVisibility(View.VISIBLE);
                        sub.setVisibility(View.VISIBLE);
                        exit.setVisibility(View.VISIBLE);


                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Toast.makeText(MainActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                        sharedPref.clearUser();


                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }

        }


        pub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTopic.setVisibility(View.VISIBLE);
                editMessage.setVisibility(View.VISIBLE);
                btnS.setVisibility(View.INVISIBLE);
                btnP.setVisibility(View.VISIBLE);
                sub.setChecked(false);
                subText.setVisibility(View.INVISIBLE);


            }
        });

        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editTopic.setVisibility(View.VISIBLE);
                editMessage.setVisibility(View.INVISIBLE);
                btnP.setVisibility(View.INVISIBLE);
                btnS.setVisibility(View.VISIBLE);
                subText.setVisibility(View.VISIBLE);
                pub.setChecked(false);

            }
        });


    }


    public void Pub(View v) {
        topic = findViewById(R.id.editTopic);
        subMessage = findViewById(R.id.editMessage);
        mytopic = topic.getText().toString();
        message = subMessage.getText().toString();

        try {

            client.publish(mytopic, message.getBytes(), 0, false); //qos default value 0, retained false
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    public void Sub(View view) {

        subTopic = findViewById(R.id.editTopic);
        mytopic = subTopic.getText().toString();


        try {
            client.subscribe(mytopic, 0);

        } catch (MqttException e) {
            e.printStackTrace();

        }
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                subText.setText(new String(message.getPayload()));

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });


    }

    public void connect(View view) {
        myHost = "tcp://" + txtServer.getText().toString() + ":1883";
        username = txtUser.getText().toString();
        password = txtPasswd.getText().toString();
        subText = findViewById(R.id.subText);


        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), myHost, clientId);
        if (TextUtils.isEmpty(myHost) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            txtServer.setError("Required");
            txtUser.setError("Required");
            txtPasswd.setError("Required");
        } else {
            MqttConnectOptions options = new MqttConnectOptions();       //user-password options
            options.setUserName(username);
            options.setPassword(password.toCharArray());

            try {
                IMqttToken token;//(options);
                token = client.connect(options);
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        // connected
                        Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                        pub.setVisibility(View.VISIBLE);
                        sub.setVisibility(View.VISIBLE);
                        exit.setVisibility(View.VISIBLE);
                        sharedPref.createLoginSession(username, password, myHost);


                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Toast.makeText(MainActivity.this, "ERROR", Toast.LENGTH_SHORT).show();


                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }

        }


    }

}






