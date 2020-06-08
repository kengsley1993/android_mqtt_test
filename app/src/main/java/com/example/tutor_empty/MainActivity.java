package com.example.tutor_empty;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private CardView living_led_1, living_tv, living_cond, living_led_2;
    static String MQTTHOST = "tcp://47.107.79.52:1883";
    static String USERNAME = "kengsley";
    static String PASSWORD = "on99a55s66";
    String topicstr = "esp8266/test";
    MqttAndroidClient client;

    TextView txtStatusTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // define Cards
        living_led_1 = (CardView) findViewById(R.id.living_led_1);
        living_led_2 = (CardView) findViewById(R.id.living_led_2);
        living_tv = (CardView) findViewById(R.id.living_tv);
        living_cond = (CardView) findViewById(R.id.living_cond);
//        add listen
        living_led_1.setOnClickListener(this);
        living_led_2.setOnClickListener(this);
        living_tv.setOnClickListener(this);
        living_cond.setOnClickListener(this);

//        define temperature text
        txtStatusTemp = findViewById(R.id.temp_num);

        ConnectToMQTT();
    }

    public void ConnectToMQTT() {
        String clientId = MqttClient.generateClientId();

        client = new MqttAndroidClient(this.getApplicationContext(), MQTTHOST, clientId);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());

        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Toast.makeText(getBaseContext(), "Connect Success", Toast.LENGTH_SHORT).show();
                    publishToMQTT(topicstr, "Hello from android");
                    subscribeFromMQTT(topicstr);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Toast.makeText(getBaseContext(), "Connect Failure", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                if (topic.toString().equals("esp8266/test")) {
                    txtStatusTemp.setText(new String(message.getPayload()));
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

    }

    public void publishToMQTT(String topic, String payload) {
        byte[] encodePayload = new byte[0];
        try {
            encodePayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodePayload);
            client.publish(topic, message);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }

    public void subscribeFromMQTT(String topic) {
        int qos = 0;
        try {
            IMqttToken subToken = client.subscribe(topic, qos);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void buttonNextClick(View view) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, page_2.class);
        startActivity(intent);
    }

    public void buttonClick(View view) {
//        Button button = (Button) view;
        Toast toast = Toast.makeText(this, R.string.buttonClickMessage, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onClick(View v) {
        Intent i;
        Toast toast;

        switch (v.getId()) {
            case R.id.living_led_1:
                toast = Toast.makeText(this, "LED1 click", Toast.LENGTH_SHORT);
                toast.show();
                break;
            case R.id.living_led_2:
                toast = Toast.makeText(this, "LED2 click", Toast.LENGTH_SHORT);
                toast.show();
                break;
            case R.id.living_tv:
                toast = Toast.makeText(this, "TV click", Toast.LENGTH_SHORT);
                toast.show();
                break;
            case R.id.living_cond:
                toast = Toast.makeText(this, "Conditioner click", Toast.LENGTH_SHORT);
                toast.show();
                break;
            default: break;
        }
    }
}