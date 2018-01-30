package com.example.prakritibansal.minerva_cards;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.auth.CognitoCredentialsProvider;
import com.amazonaws.services.lexrts.model.GenericAttachment;
import com.amazonaws.regions.Regions;

import com.amazonaws.services.polly.AmazonPollyPresigningClient;
import com.amazonaws.services.polly.model.DescribeVoicesRequest;
import com.amazonaws.services.polly.model.DescribeVoicesResult;
import com.amazonaws.services.polly.model.OutputFormat;
import com.amazonaws.services.polly.model.SynthesizeSpeechPresignRequest;
import com.amazonaws.services.polly.model.Voice;



import com.prakritibansal.posttextrequest.Continuations.LexServiceContinuation;
import com.prakritibansal.posttextrequest.InteractionClient;
import com.prakritibansal.posttextrequest.TextResponse;
import com.prakritibansal.posttextrequest.Listeners.InteractionListener;

import com.amazonaws.services.lexrts.model.DialogState;
import com.example.prakritibansal.minerva_login.R;
import com.github.zagum.speechrecognitionview.RecognitionProgressView;
import com.github.zagum.speechrecognitionview.adapters.RecognitionListenerAdapter;


/**
 * Created by prakritibansal on 12/23/17.
 */

public class TextActivity extends AppCompatActivity {

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private static final String TAG = "TextActivity";
    private static final String plainText = "PlainText";
    private static final String SSML = "SSML";
    private EditText userTextInput;
    private Context appContext;
    private InteractionClient lexInteractionClient;
    private boolean inConversation;
    public static LexServiceContinuation convContinuation;
    public static boolean lexIsResponding;

    //For Polly
    private AmazonPollyPresigningClient client;
    private MediaPlayer mediaPlayer;
    private List<Voice> voices;
    private Voice raveena;
    private Thread background;

    private Dialog dialog;
    private SpeechRecognizer speechRecognizer;
    private TextView user_resp;
    private TextView bot_resp;
    public static LinearLayout userInp;
    public static LinearLayout dotsLoading;
    private ImageView micButton;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }


    @Override
    @SuppressLint("RestrictedApi")
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showtext);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO);
        if(permissionCheck == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        }

        userInp = findViewById(R.id.user);
        micButton = findViewById(R.id.voiceInterface);
        dotsLoading = findViewById(R.id.loading);
        dotsLoading.setVisibility(LinearLayout.GONE);

        bot_resp = findViewById(R.id.bot_text);
        user_resp = findViewById(R.id.user_text);

        init();
        initPollyClient();
        setupNewMediaPlayer();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id==android.R.id.home){

            finish();

        }
        return super.onOptionsItemSelected(item);

    }


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.AskLog))
                .setMessage(getResources().getString(R.string.LogDes))
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                    }
                }).create().show();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        new GetPollyVoices().execute();
    }

    @Override
    protected void onDestroy() {
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        super.onDestroy();
    }

    void initPollyClient() {
        //To the ol' good Polly
        CognitoCachingCredentialsProvider pollyCredentials = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                appContext.getResources().getString(R.string.polly_pool),
                Regions.US_EAST_1);

        // Create a client that supports generation of presigned URLs.
        client = new AmazonPollyPresigningClient(pollyCredentials);
    }


    public void getSpeechInput(View view) {

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialogue_speech);


        int[] colors = {
                ContextCompat.getColor(this, R.color.colorPrimary),
                ContextCompat.getColor(this, R.color.colorPrimaryDark),
                ContextCompat.getColor(this, R.color.colorAccent),
                ContextCompat.getColor(this, R.color.light_blue),
                ContextCompat.getColor(this, R.color.bg_main)
        };

        int[] heights = { 28, 32, 26, 31, 24 };

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        final RecognitionProgressView recognitionProgressView = (RecognitionProgressView) dialog.findViewById(R.id.recognition_view);
        recognitionProgressView.setSpeechRecognizer(speechRecognizer);
        recognitionProgressView.setRecognitionListener(new RecognitionListenerAdapter() {
            @Override
            public void onResults(Bundle results) {
                dialog.hide();
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                textEntered(matches.get(0));
                //Toast.makeText(this, matches.get(0), Toast.LENGTH_LONG).show();
            }
        });
        recognitionProgressView.setColors(colors);
        recognitionProgressView.setBarMaxHeightsInDp(heights);
        recognitionProgressView.setCircleRadiusInDp(2);
        recognitionProgressView.setSpacingInDp(4);
        recognitionProgressView.setIdleStateAmplitudeInDp(5);
        recognitionProgressView.setRotationRadiusInDp(10);
        recognitionProgressView.play();


        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        if (intent.resolveActivity(getPackageManager()) != null) {
            //startActivityForResult(intent, 10);
            speechRecognizer.startListening(intent);
            dialog.show();
        } else {
            Toast.makeText(this, "Your Device Doesn't Support Speech Input", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Initializes the application.
     */
    private void init() {
        Log.d(TAG, "Initializing text component: ");
        appContext = getApplicationContext();
        userTextInput = (EditText) findViewById(R.id.userInputEditText);

        // Set text edit listener.
        userTextInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String text = userTextInput.getText().toString();
                    textEntered(text);
                    return true;
                }
                return false;
            }
        });

        initializeLexSDK();
        startNewConversation();
    }

    /**
     * Initializes Lex client.
     */
    private void initializeLexSDK() {
        Log.d(TAG, "Lex Client");
        // Cognito Identity Broker is the credentials provider.
        CognitoCredentialsProvider credentialsProvider = new CognitoCredentialsProvider(
                appContext.getResources().getString(R.string.identity_id_test),
                Regions.fromName("us-east-1"));

        // Create Lex interaction client.
        lexInteractionClient = new InteractionClient(getApplicationContext(),
                credentialsProvider,
                Regions.US_EAST_1,
                appContext.getResources().getString(R.string.bot_name),
                appContext.getResources().getString(R.string.bot_alias));
        lexInteractionClient.setInteractionListener(interactionListener);

    }

    /**
     * Read user text input.
     *
     * @param text
     */
    private void textEntered(String text) {
        if(!lexIsResponding){
            if(text.trim().length() > 0){
                if (!inConversation) {
                    Log.d(TAG, " -- New conversation started");
                    startNewConversation();
                    user_resp.setText(text);
                    //addMessage(new Message(text, "tx", getCurrentTimeStamp()));
                    lexInteractionClient.textInForTextOut(text, null);
                    inConversation = true;
                } else {
                    user_resp.setText(text);
                    Log.d(TAG, " -- Responding with text: " + text);
                    //addMessage(new Message(text, "tx", getCurrentTimeStamp()));
                    convContinuation.continueWithTextInForTextOut(text);
                }
                lexIsResponding = true;
                userInp.setVisibility(LinearLayout.GONE);
                dotsLoading.setVisibility(LinearLayout.VISIBLE);

                clearTextInput();
            }else{
                showToast("Enter a query!");
            }
        }else{
            showToast("An error has occurred, please try restarting the app.");
        }


    }

    /**
     * Pass user input to Lex client.
     *
     * @param continuation
     */
    private void readUserText(final LexServiceContinuation continuation) {
        convContinuation = continuation;
        inConversation = true;
        lexIsResponding = false;
        dotsLoading.setVisibility(LinearLayout.GONE);
        userInp.setVisibility(LinearLayout.VISIBLE);
        requestFocus();
    }

    /**
     * Clears the current conversation history and closes the current request.
     */
    private void startNewConversation() {
        Log.d(TAG, "Starting new conversation");
        inConversation = false;
        lexIsResponding = false;
        clearTextInput();

    }

    /**
     * Clear text input field.
     */
    private void clearTextInput() {
        userTextInput.setText("");
    }

    /**
     * Break response text into SSML and plain text
     *
     * @return texts
     */
    private  Map<String, String> ssmlBreakPoint(String str){
        Map<String, String>  texts = new HashMap<String, String>();
        if(str!= null){
            for(int i = 0; i< str.length(); i++){
                char c = str.charAt(i);
                if(c == '|'){
                    texts.put(plainText, str.substring(i+1));
                    texts.put(SSML, str.substring(0, i));
                    return texts;
                }
            }
        }
        texts.put(plainText, str);
        texts.put(SSML, "<speak>"+str+"</speak>");

        return texts;

    }

    /**
     * Request Keyboard Focus
     *
     * @return
     */
    private void requestFocus(){
        userInp.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(userInp, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * Interaction Listener based on the Post Text Request library
     *
     */
    final InteractionListener interactionListener = new InteractionListener() {
        @Override
        public void onReadyForFulfillment(final TextResponse response) {


            if(response.getTextResponse() != null){

                Map<String, String> responses = ssmlBreakPoint(response.getTextResponse());

                bot_resp.setText(plainText);
                setupPlayButton(responses.get(SSML));
            }
            if(response.getResponseCard()!= null) {
                GenericAttachment ga = response.getResponseCard().getGenericAttachments().get(0);
                //display response cards
            }

            inConversation = false;
            lexIsResponding = false;
            dotsLoading.setVisibility(LinearLayout.GONE);
            userInp.setVisibility(LinearLayout.VISIBLE);
            requestFocus();


        }

        @Override
        public void promptUserToRespond(final TextResponse response,
                                        final LexServiceContinuation continuation) {

            if(response.getDialogState().equals("Fulfilled")){
                if(response.getTextResponse() != null){
                    Map<String, String> responses = ssmlBreakPoint(response.getTextResponse());
                    bot_resp.setText(plainText);
                    setupPlayButton(responses.get(SSML));
                }
                inConversation = false;
                lexIsResponding = false;
                dotsLoading.setVisibility(LinearLayout.GONE);
                userInp.setVisibility(LinearLayout.VISIBLE);
                requestFocus();
            }else{


                if(response.getTextResponse() != null){

                    Map<String, String> responses = ssmlBreakPoint(response.getTextResponse());
                    bot_resp.setText(plainText);
                    setupPlayButton(responses.get(SSML));
                }
                if(response.getResponseCard()!= null){
                    GenericAttachment ga = response.getResponseCard().getGenericAttachments().get(0);
                    //Display response cards

                }

                readUserText(continuation);
            }


        }



        @Override
        public void onInteractionError(final TextResponse response, final Exception e) {
            if (response != null) {
                if (DialogState.Failed.toString().equals(response.getDialogState())) {

                    Map<String, String> responses = ssmlBreakPoint(response.getTextResponse());
                    bot_resp.setText(plainText);
                    setupPlayButton(responses.get(SSML));
                    inConversation = false;


                } else {
                    bot_resp.setText(plainText);
                }
            } else {
                showToast("Lex is not well! Try again in a few hours.");

                inConversation = false;
            }
            lexIsResponding= false;
            dotsLoading.setVisibility(LinearLayout.GONE);
            userInp.setVisibility(LinearLayout.VISIBLE);
            requestFocus();

            Log.e(TAG, "Interaction error", e);
        }
    };

    /**
     * Show a toast.
     *
     * @param message - Message text for the toast.
     */
    private void showToast(final String message) {
        Toast.makeText(this.getApplicationContext(), message, Toast.LENGTH_LONG).show();
        Log.d(TAG, message);
    }

    /**
     * All for ResponseCard-- Polly Integration :)
     *
     */

    private class GetPollyVoices extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            if (voices != null) {
                return null;
            }

            // Create describe voices request.
            DescribeVoicesRequest describeVoicesRequest = new DescribeVoicesRequest();

            DescribeVoicesResult describeVoicesResult;
            try {
                // Synchronously ask the Polly Service to describe available TTS voices.
                describeVoicesResult = client.describeVoices(describeVoicesRequest);
            } catch (RuntimeException e) {
                Log.e(TAG, "Unable to get available voices. " + e.getMessage());
                return null;
            }

            // Get list of voices from the result.
            voices = describeVoicesResult.getVoices();

            // Log a message with a list of available TTS voices.
            //Log.i(TAG, "Available Polly voices: " + voices);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (voices == null) {
                return;
            }
            //background.start();
        }
    }

    void setupNewMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d("Media Player", "DONE SPEAKING");
                mp.release();
                setupNewMediaPlayer();
                micButton.setEnabled(true);
            }
        });
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.d("Media Player", "STARTNG TO SPEAK SPEAKING");
                micButton.setEnabled(false);
                mp.start();
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });
    }

    void setupPlayButton(final String textToRead) {

        background = new Thread() {
            public void run() {

                try {
                    for(int i = 0; i< voices.size(); i++){
                        if(voices.get(i).getName().equals("Raveena")){
                            raveena = voices.get(i);
                            break;
                        }
                    }

                    // Create speech synthesis request.
                    SynthesizeSpeechPresignRequest synthesizeSpeechPresignRequest =
                            new SynthesizeSpeechPresignRequest()
                                    // Set text to synthesize.
                                    .withText(textToRead)
                                    //SSML
                                    .withTextType("ssml")
                                    // Set voice selected by the user.
                                    //.withVoiceId(selectedVoice.getId())
                                    .withVoiceId(raveena.getId())
                                    // Set format to MP3.
                                    .withOutputFormat(OutputFormat.Mp3);

                    // Get the presigned URL for synthesized speech audio stream.
                    URL presignedSynthesizeSpeechUrl =
                            client.getPresignedSynthesizeSpeechUrl(synthesizeSpeechPresignRequest);

                    Log.i(TAG, "Playing speech from presigned URL: " + presignedSynthesizeSpeechUrl);

                    // Create a media player to play the synthesized audio stream.
                    if (mediaPlayer.isPlaying()) {
                        setupNewMediaPlayer();

                    }
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                    try {
                        // Set media player's data source to previously obtained URL.
                        mediaPlayer.setDataSource(presignedSynthesizeSpeechUrl.toString());
                    } catch (IOException e) {
                        Log.e(TAG, "Unable to set data source for the media player! " + e.getMessage());
                    }


                    // Start the playback asynchronously (since the data source is a network stream).
                    mediaPlayer.prepareAsync();

                } catch (Exception e) {

                }
            }
        };
        background.start();
    }
}

