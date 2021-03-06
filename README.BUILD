VegDroid depends on:

 * android-support-v4.jar
 * ActionBarSherlock
 * google-play-services.jar (for Google Maps v2 API)
 * gson-2.2.2.jar (to support older devices, it is built into Android only starting from Honeycomb)


Eclipse configuration instructions to build against these libs and to package
them into the APK:

1. Add libraries via

   Project >> Properties >> Java Build Path >> Libraries >> Add External JARs...

     <SDKDIR>/extras/android/support/v4/android-support-v4.jar
     <SDKDIR>/extras/google/google_play_services/libproject/libs/google-play-services.jar
     /path/to/gson-2.2.2.jar

2. Checkmark libraries in

   Project >> Properties >> Java Build Path >> Order and Export

3. File >> Import >> Android >> Existing Android Code into Workspace >> Root Directory >>
   a) <SDKDIR>/extras/google/google_play_services/libproject/google-play-services_lib
        ==> "google-play-services_lib" shows up in Eclipse project list
   b) path/to/ActionBarSherlock/library
        ==> "library" shows up in Eclipse project list

4. VegDroid >> Properties >> Android >> (Library) >> Add
   a) google-play-services_lib
   b) library


Resources:

https://developers.google.com/maps/documentation/android/intro#sample_code
http://www.user.tu-berlin.de/hennroja/tutorial_fixing_java.lang.NoClassDefFoundError%20com.google.android.gms.R$styleable.html
http://stackoverflow.com/questions/2316445/how-to-use-and-package-a-jar-file-with-my-android-app
