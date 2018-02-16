# SpotifyJukespot

# Description
- Created for: Advanced Software Engineering Course
- Created in: Fall 2017

- A native Android application that allows users with a Spotify account to create a beacon, or jukebox, to play music while allowing other users to add songs to the queue by joining the jukebox. This application is meant to make it more convenient to share and listen to music while in a social event without having to switch phones, or borrow someone's phone to play the music you would like to hear. 
Spotify Jukebox will allow users to start a jukebox and people, who are near the jukebox, with access to the application and a Spotify Account will be able to join and start adding songs to the queue of what is currently playing. The application will work by using the Google Play Location Services API and the Spotify Android SDK to allow nearby users to add songs to the jukebox queue. 

# Features: 
-	Creator’s phone will play songs off Spotify; his and songs people add to the queue.
- Allow user to start a jukebox, playing music from Spotify, for people to join
- Allow users to join a nearby jukebox and start adding songs to a queue
- Allow the jukebox creator to set a distance range so only people in that range can join

# Prequisites
- Requires a webservice that is now offline for storing and sending information on the playlists being edited
- Requires the webservice to have pubnub integration for the broadcasting of changes to people using the application.
- Android running appropriate firmware with the ability to install the application
- Spotify Premium account is required to have access to the Spotify API's features needed to run the application
