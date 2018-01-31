# Lex Library (ft. Response Cards)

An android library for making Post Text requests to amazon Lex. An abstract library built on top of Post Text Request API feauturing response cards from Lex. The sample app, also demos the use of Polly in a chat app. 

## Usage

To make use of this library simple add the following to gradle.build:
```
compile 'com.github.pikkapikkachu:post-text-lex-request:1.0.0@aar'
```

The usage of this library is quite similar to the amazon-lex-sdk, with just one minor change of renaming the Response class to TextResponse. 

```
import com.prakritibansal.posttextrequest.*;

final InteractionListener interactionListener = new InteractionListener() {
        @Override
        public void onReadyForFulfillment(final TextResponse response) {
           //Lex returns a final response 
        }

        @Override
        public void promptUserToRespond(final TextResponse response, final LexServiceContinuation continuation) {
            //Do whatever after Lex has returned a response
        }

        @Override
        public void onInteractionError(final TextResponse response, final Exception e) {
            if (response != null) {
                //show error response 
            } else {
                showToast("Error: " + e.getMessage());
            }
        }
    };
```

## Sample App
A sample android app of using the library is provided, an elaborate example of incoorporating Lex & Polly to make a full-fledged Amazon ML app. 

## Developed By

PikkaPikkachu with :heart:

## Contributions
Contributions to the library as well as sample are more than welcome :)
