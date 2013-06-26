Change Log
===============================================================================

Version 1.01c *(6-26-2013)*
----------------------------
* Changes to GPS
* Uses Application class to find Location
* Report refers to this Application class and uses a timer to check for updates

Version 1.01b *(6-24-2013)*
----------------------------
* Changed push button on main screen
* push button now changes state based on state of database
* Replaced classpath, hopefully this should work regardless of machine

Version 1.01 (Google Play) *(6-24-2013)*
-----------------------------------------
* New google play version
* Version code was changed from 1 to 2


Commits above this line refer to actual version numbers
-------------------------------------------------------

Version 2.0.1 *(6-25-2013)*
------------------------------
Changes to the way GPS is found
* Added Location Listener to report
* If the previous location isn't less than 1 minute old, it then requests a new location

Version 2.0 *(6-23-2013)*
----------------------------
Lots of code committed at once.
* Main screen has tallies removed
* Main screen has different push button, but needs to be changed
* Removed push points button from settings
* Added refresh button to New Sighting
* Removed countdowntimer (for now)

Version BETA *(6-5-2013)*
----------------------------
Beta Release!  Rejoice!

Version 1.4.4 *(5-30-2013)*
----------------------------
* Fixed top of Trip layout - strings don't wrap now
* Changed Dev Trip testing interval (five minutes) to ten seconds
* Trip now grabs location and doesn't crash

Version 1.4.3 *(5-29-2013)*
----------------------------
* Added Help webview
* Changed strings in main

Version 1.4.2 *(5-29-2013)*
----------------------------
* Added Feeback
* Main will get email addresses from phone and prompt user to select one on first use
* Changed Trip interval "Five Minutes" to ten seconds, instead of three

Version 1.4.1 *(5-28-2013)*
----------------------------
* Changed Help dialog to kick user to browser
* Fixed drawables on xhdpi devices

Version 1.4.0 *(5-28-2013)*
----------------------------
* Cleaned up code, added a lot of code from Report to Trip

Version 1.3.9 *(5-26-2013)*
----------------------------
* Saves JSON files as single string to local storage -- THIS MIGHT NOT WORK, I'm not sure yet
* Added database java files
* Report will not let user submit points without completing necessary fields
* Updated README
* Deleted unused drawables and fixed 9-patches

Version 1.3.8 *(5-24-2013)*
----------------------------
* Added About activity in settings - just for fun
* Main now displays totals for points submitted
* UploadData now increments when points are correctly received
* Added several hardcoded strings to strings.xml
* Changed Report_Mapview's buttons to be straight black
* Removed code for Report's camera

Version 1.3.7 *(5-19-2013)*
----------------------------
* Cleaned up code a little bit

Version 1.3.6 *(5-18-2013)*
----------------------------
* Deleted Main's test button
* Deleted edittext in Report.java
* Deleted unused test methd in Main
* Updated README

Version 1.3.5 *(5-15-2013)*
----------------------------
* Added textview in Login - superflous, but maybe looks nice? maybe?
* Added checks on textviews in Login
* Organized imports

Version 1.3.4 *(5-15-2013)*
----------------------------
* Edited options for Login attributes
* Changed Username to User ID
* Added Email to Login and JSON
* Removed Anonymous user
* Removed Create Account in Settings

Version 1.3.3 *(5-15-2013)*
----------------------------
* Report now sends actual user data for point and kills activity when "done" is selected

Version 1.3.2 *(5-15-2013)*
----------------------------
* 6 hour reminder timer service
* fixed user data submission in Login
* fixed point data submission in Report

Version 1.3.1 *(5-14-2013)*
----------------------------
* Integration with BugSense

Version 1.3.0 *(5-14-2013)*
----------------------------
* Added some constants for submitting points

Version 1.2.9 *(5-12-2013)*
----------------------------
* Application now successfully submits to server; albeit through NameValuePairs, not JSON
* Added UploadData class to handle Async uploads
* Added test button on main page to test PHP
* Main checks for first run, and will run Login activities
* Login submits to server to instantiate new user
* Login checks edittexts for spaces
* Login edittexts now only allow one line

Version 1.2.8 *(5-5-2013)*
----------------------------
* Trip functionality now works
* Added broadcast (I didn't know we already had one before i deleted the first one. whoops)

Version 1.2.7 *(5-3-2013)*
----------------------------
* Split login into two activities
* Pushed entry to login to Settings (permanent move)
* Added animation to login in between activities to make them feel more like they're "together"
* Changed name to wavyleaf

Version 1.2.6 *(5-2-2013)*
----------------------------
* Added attributes to login
* Changed "dob" to "birthyear"
* Keyboard hidden in login

Version 1.2.5 *(5-1-2013)*
----------------------------
* Async could possibly be working!

Version 1.2.4 *(4-30-2013)*
----------------------------
* Added login activity
* If phone is on first install, it will run login activity
* Tweaked drop shadows for attributes (Notes, coordinates, percentage, etc)
* Added dummy view
* Changed edittextpreferences in Settings to be regular non-editable preferences
* Changed some attributes from Age to DOB
* Added thin roboto font

Version 1.2.3 *(4-28-2013)*
----------------------------
* Made countdowntimer cancelable
* Made countdowntimer display numeric value
* Deleted save button in Trip

Version 1.2.2 *(4-28-2013)*
----------------------------
* Changed entire activity to portrait
* Added settings for turning off vibrate / noise
* Changed how trip button state is determined

Version 1.2.1 *(4-26-2013)*
----------------------------
* Added notification icons!

Version 1.2.0 *(4-25-2013)*
----------------------------
* Added strings for togglebuttons to bold
* Added different drawable for camera
* Added menu for trip
* Disabled compass in Report
* Deleted save button from Trip

Version 1.1.9 *(4-25-2013)*
----------------------------
* Main - Trip button changes text when selected
* Created an layout_main_endtrip string as part of above
* Corrected 'age' in Report JSON to 'dob'
* Jon: Trip notification plays vibrates, creates system notification

Version 1.1.8 *(4.25-2013)*
----------------------------
* Removed landscape Main and locked to Portrait
* Timer has max choice of 15 minute interval
* Main - Text at bottom changed 'Time Interval' to 'next reminder in'
* Main - Dialog title changed to 'Choose reminder interval'

Version 1.1.7 *(4.22-2013)*
----------------------------
* Deleted paper airplane icon
* Changed order of attributes in Trip and Record layouts
* Made attributes in forms all white
* JSON almost done except for bitmap
* Added age to settings

Version 1.1.6 *(4-22-2013)*
----------------------------
* Added notifications
* Added android compat lib jar (goes in /libs/ folder)
* Added vibration to phone

Version 1.1.5 *(4-22-2013)*
----------------------------
* Added settings.
* Added xml folder for file that defines settings

Version 1.1.4 *(4-15-2013)*
----------------------------
* Still need to integrate CDT from Ver 1.1.3 into a service
* Removed option to select from gallery - requested by VB
* replaced old onClick code in Main with newer test code
* New Record and Trip validate EditText for negative numbers

Version 1.1.3 *(4-15-2013)*
----------------------------
* Main uses a CountDownTimer; is this what we want? Maybe put this whole thing into a service?

Version 1.1.2 *(4-15-2013)*
----------------------------
* Removed Done option from Report_Mapview
* Added more JSON attributes
* Added option to select from gallery

Version 1.1.1 *(4-12-2013)*
----------------------------
* Completed editing of coordinates
* Added reset drawable
* List Dialog in main now sets edittexts at bottom

Version 1.1.0 *(4-10-2013)*
----------------------------
* Added header previews to New Report and Trip
* Added temporary intent to Trip in Main's overflow menu; to be deleted soon.
* Changed minsdkversion

Version 1.0.10 *(4-10-2013)*
----------------------------
* First commit: implemented skeleton AlarmManager, added reset btn and keep-zoom to Edit Map
* Second commit: added minor functionality, tweaked code in Activities.

Version 1.0.9 *(4-7-2013)*
----------------------------
* Fixed main's Trip button. Still needs listener to change on user click

Version 1.0.8 *(4-7-2013)*
----------------------------
* Fixed changes suggested by *advisees*
* Changed Main layout/Trip layout to both have current tally of points recorded

Version 1.0.7 *(4-6-2013)*
----------------------------
* Added green theming and styles
* Added estimated 1,000,000 drawables for green themes

Version 1.0.6 *(4-5-2013)*
----------------------------
* Added help option to main screen

Version 1.0.5 *(4-5-2103)*
----------------------------
* Added .gitignore

Version 1.0.4 *(3-31-2013)*
----------------------------
* Added Report_Mapview.java
* Added layout_report_mapview.xml
* Added menu_report_mapview.xml
* Added drawable for edit button, plus state 9patch buttons
* Removed ability to edit map from Report.java
* Removed up button from Main.java

Version 1.0.3 *(3-30-2013)*
----------------------------
* Deleted Edit Record, intents, and buttons from Main
* Added Area Infested attribute to New Report
* Added edit drawable
* Edited Strings.xml
* Edited New Report to be much better at obtaining position
* Edited New Report to be portrait only
* Edited New Report to use only GPS
* Edited New Report to have mapview at bottom


Version 1.0.2 *(3-26-2013)*
----------------------------
* added DashboardLayout.java
* added Main.java
* added layout_main
* added temp_144x96.png for temporary dashboard icons
* added menu_main
* edited styles to include dashboard button style
* edited Report.java
* added main class to manifest


Version 1.0.1 *(3-20-2013)*
----------------------------
* updated readme
* deleted jar from /libs
* deleted style from res/values-v11
* deleted style from res/values-v14
* added folder /res/drawable
* added folder /res/menu
* added folder /assets/fonts
* added report.java
* added layout_report.xml
* added drop_shadow.xml
* added camera to xhdpi
* added menu_report.xml
* added ic_map to res/drawable-xhdpi
* added ic_map to res/drawable-hdpi
* added ic_map to res/drawable-mdpi
* added ic_submit to res/drawable-xhdpi
* added ic_submit to res/drawable-hdpi
* added ic_submit to res/drawable-mdpi
* added roboto_bold.tff
* added roboto_bold.tff
* added manifest
* added/edited classpath file (this may be deleted soon)
* edited project.properties (this will be deleted soon)


Version 1.0.0 *(3-20-2013)*
----------------------------
Initial commit.