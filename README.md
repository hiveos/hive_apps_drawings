Dependencies
============


Holo Color Picker 
-----------------
https://github.com/LarsWerkman/HoloColorPicker


Action Bar Pull To Refresh
--------------------------
https://github.com/chrisbanes/ActionBar-PullToRefresh

Add
```
<uses-sdk
        android:minSdkVersion="X"
        android:targetSdkVersion="19" />
```
to the AndroidManifest.xml file (If doesn't already exist). Other than that, DefaultHeaderTransformer.java may give errors on line 375. If you encounter the errors change the line to 
```
.strokeWidth(strokeWidth)
```




Smooth Progress Bar 
-------------------
(A dependency of Pull to refresh. You need to add it to Action Bar Pull to Refresh and not to Drawings!)

https://github.com/castorflex/SmoothProgressBar

If you are using eclipse you may have to move the source files from java folder to src folder.
