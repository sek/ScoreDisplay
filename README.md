[![Build Status](https://travis-ci.com/sek/ScoreDisplay.svg?branch=master)](https://travis-ci.com/sek/ScoreDisplay)


# 21st Century Scoreboard

Two instances of this app running side by side make a single scoreboard.

![A 21st Century Scoreboard](https://scontent-lax3-1.xx.fbcdn.net/hphotos-xla1/v/t1.0-9/12523922_10153860902787173_5526631497506405450_n.jpg?oh=dd277462fe16fb5443cc0d5d0b760f1f&oe=573370EA)
* TODO: New Picture (or short video)

## Acknowledgements

* Let's Go Digital Font: http://www.fontspace.com/wlm-fonts/lets-go-digital license: Ubuntu Font License: http://font.ubuntu.com/ufl/

## Instructions

* Tap on top of screen to increase by one point
* Long press anywhere to reset score to 0
* Use Settings FAB (Gear) to start controller activity
* Use a 3rd device as a controller for two score displays
  * Start Controller
  * Show QR code on a display
  * Use Controller to scan QR code 


## Thoughts of what TODO next:

- have view restore it's state and remove from MainActivity saveInstanceState

- use travis ci to build in github: https://docs.travis-ci.com/user/languages/android/
- publish in Google Play

* rework the controller UI
  - make a view for the score...  use it on the controller
  - make an icon for App
  - team labels (requires DB update to have label under the id too and not just the score)

- reduce left/right duplication in settings activity


* Navigation Editor

* once controller has scanned a display, have display return to score somehow

* Handle case where there's no inet connection?
* Settings activity
** Controller Mode
** Scoreboard Mode - 1     (could do 2 in future...   ex: a single tv could fit both scores)

* setting to toggle Black on White <--> White on Black

* small instructions on screen
* input for 2/3 pointers (swipe 2/3 fingers up/down?)
* show battery percentage on screen

* Chromecast support for a big screen TV would be cool?

* other fonts?

* Google QRCode reader (MLKit) is probably way better
