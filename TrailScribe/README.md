#TrailScribe - Android

###CONTENT
1. [Setup Project in Development Environment] (#1)
2. [How to Sign and Build the Project] (#2)
3. [How to Install the Project] (#3)
 
============

###<a name="1"></a>Setup Project in Development Environment
1. Requirements
    - Android Software Development Kits (SDK), API 19
    - Android Devloper Tools (ADT)
    - (Recommended) Eclispe Kepler
2. Procedure
    - $ git clone https://github.com/CMUPracticum/TrailScribe.git

###<a name="2"></a>How to Sign and Build the Project
1. Requirements
    - Android SDK, API 19
    - Eclipse IDE (Eclipse Kepler is recommended)
2. Procedure
    1. In Eclipse, File>Export>Android>Export Android Application
    2. Select the project to be exported
    3. Use existing keystore
        - Download the keystore on Google Drive and use it as the signing keystore, password is provided in Google Drive
    4. Use existing key, Alias: trailscribe
        - Password is provided in Google Drive
    5. Export the Application

###<a name="3"></a>How to Install the Project
1. Requirements
    - Android SDK
2. Procedure
    1. $ cd android-sdk/platform-tools
    2. $ adb install <path>/TrailScribe.apk
