# GRUP
Android Studio Setup for GRUP-APP  
1. Download Android Studio here.  
2. Install KMM plugin  
3. Clone and open project folder  
4. Use “Android API 32 (Sv2)” under Appearance and Behavior -> System Settings -> Android SDK  
5. Add an emulator(16:9 aspect ratio, Android 11)  
  
Run Android App  
1. Run `MainActivity.kt` in `androidApp` folder with emulator configuration
2. Emulator should open on the right to the main page
  
Run API  
1. Install Postman here.  
2. Run `Application.kt` in `api/` folder  
3. Run sample HTTP requests to localhost:8080 in Postman to make sure API server works  
  
Git commands

To update feature branch:  
	`git stash` any changes  
	`git checkout master` and `git pull`  
	`git checkout <feature_branch>`  
	`git rebase master` applies recent master change to feature branch

