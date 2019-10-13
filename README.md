# Easy Shopping List 


## Project Overview

The app provides persistent shopping lists while minimising typing and making the list easy to use during shopping. You can create any number of independent, named shopping lists, and add products by selecting from a predefined list (which you can can also add products to if needed). Each list is also divided into “need” and “already have” sections, so that when shopping a product can be quickly moved to the “already have” section as it is added to the basket, so you can instantly see what you still need to find. This section can also be used as a reminder that you already has some product at home and don’t need to buy more.

The project is part of the Udacity course: **Android Developer Nanodegree Program**.


## Project requirements

- App integrates a third-party library.
- App validates all input from servers and users. If data does not exist or is in the wrong format, the app logs this fact and does not crash. 
- App includes support for accessibility. That includes content descriptions, navigation using a D-pad, and, if applicable, non-audio versions of audio cues.
- App keeps all strings in a strings.xml file and enables RTL layout switching on all layouts.
- App provides a widget to provide relevant information to the user on the home screen.
- App integrates two or more Google services.
- Each service imported in the build.gradle is used in the app.
- App theme extends AppCompat.
- App uses an app bar and associated toolbars.
- App uses standard and simple transitions between activities.
- App builds from a clean repository checkout with no additional configuration. 
- App builds and deploys using the installRelease Gradle task.
- App is equipped with a signing configuration, and the keystore and passwords are included in the repository. Keystore is referred to by a relative path.
- All app dependencies are managed by Gradle.
- App stores data locally using Room OR Firebase Realtime Database. No third party frameworks may be used.
- Must implement at least one of the three:
If it regularly pulls or sends data to/from a web service or API, app updates data in its cache at regular intervals using a SyncAdapter or JobDispacter.
OR
If it needs to pull or send data to/from a web service or API only once, or on a per request basis (such as a search application), app uses an IntentService to do so.
OR
It it performs short duration, on-demand requests(such as search), app uses an AsyncTask
- If Room is used then LiveData and ViewModel are used when required and no unnecessary calls to the database are made.