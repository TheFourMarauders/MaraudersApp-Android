# MaraudersMap-Android

We used Gradle 1.3.1 to build our project with the target SDK version 21. The dependencies for our app are listed below.



    compile fileTree(dir: 'libs', include: ['*.jar'])
    testCompile 'junit:junit:4.12'
    compile 'com.google.android.gms:play-services:7.8.0'
    compile 'com.android.support:appcompat-v7:23.0.1'
    compile 'com.google.android.gms:play-services-ads:7.8.0'
    compile 'com.google.android.gms:play-services-identity:7.3.0'
    compile 'com.google.android.gms:play-services-gcm:7.3.0'
    compile 'de.hdodenhof:circleimageview:1.3.0'
    compile 'com.cocosw:bottomsheet:1.+@aar'
    compile 'com.android.support:design:22.2.0'
    compile 'com.jakewharton:butterknife:6.1.0'
    compile 'com.fasterxml.jackson.core:jackson-core:2.4.2'
    compile 'com.fasterxml.jackson.core:jackson-annotations:2.4.0'
    compile 'com.fasterxml.jackson.core:jackson-databind:2.4.2'
    compile 'com.mcxiaoke.volley:library:1.0.19'
    compile('com.mikepenz:materialdrawer:4.0.8@aar') {
        transitive = true
    }
