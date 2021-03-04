# Health-AI

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

This is an Chatbot app that uses Django Channels and WebSocket connections to interact with the user.

## Setup

 - Change `server_address` inside the `strings.xml`
 - Create a Firebase Project and switch to `Blaze Plan`.
 - Drop the `google-services.json` to `app` folder.
 - You should also activate `Cloud Firestore`, `Authentication` and `Cloud Functions`.
 - Deploy the `functions` folder using Firebase CLI.
 - Obtain a Google Places API key and put it into `AndroidManifest.xml` and `strings.xml`

## References

 - Thanks to [Shafique](https://github.com/shafique-md18) for building the backend.
 - You can find the [repo](https://github.com/shafique-md18/HealthAIChatbot) here.

## Screenshots

![alt tag](https://raw.githubusercontent.com/ahmetozrahat25/health-ai/master/images/shot1.png)
![alt tag](https://raw.githubusercontent.com/ahmetozrahat25/health-ai/master/images/shot2.png)
![alt tag](https://raw.githubusercontent.com/ahmetozrahat25/health-ai/master/images/shot3.png)

## License

    Copyright 2021 Ahmet Özrahat

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
