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

## License 

```

   Copyright 2018 Prakriti Bansal

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```
