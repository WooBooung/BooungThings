/*
 *  My Day-off With Google Calendar
 *
 *
 *  Copyright 2019 WooBooung <woobooung@gmail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  Version history
*/
public static String version() { return "v0.0.6.20191005" }
/*
 *	2019/10/05 >>> v0.0.6.20191005 - Added tag #workday (feat. Naver cafe 럽2유3)
 *	2019/08/16 >>> v0.0.5.20190816 - Fixed bug updateTodayDate() OOBE case
 *	2019/04/26 >>> v0.0.4.20190426 - Modified checkOffday()
 */

private static String appName() { return "My Day-off" }

definition(
        name: "${appName()}",
        namespace: "WooBooung",
        author: "Booung",
        description: "Allows you to use Google Calendar with SmartThings.",
        category: "Convenience",
        iconUrl: "https://cdn4.iconfinder.com/data/icons/new-google-logo-2015/400/new-google-favicon-512.png",
        iconX2Url: "https://cdn4.iconfinder.com/data/icons/new-google-logo-2015/400/new-google-favicon-512.png",
        iconX3Url: "https://cdn4.iconfinder.com/data/icons/new-google-logo-2015/400/new-google-favicon-512.png",
        oauth: true,
        singleInstance: true,
        usesThirdPartyAuthentication: false,
        pausable: false
) {
    appSetting "clientId"
    appSetting "clientSecret"
}

preferences {
    page(name: "mainPage", install: true)
}

mappings {
    path("/oauth/callback") { action: [ GET: "oauthCallback" ] }
    path("/oauth/initialize") { action: [ GET: "oauthInit"] }
}

def getSecretKey()               { return appSettings.clientSecret }
def getClientId()                { return appSettings.clientId }
def getDayOfWeek() {
	return [1 : "Sun", 2: "Mon", 3: "Tue", 4: "Wed", 5: "Thu", 6 : "Fri", 7 : "Sat"]
}

def mainPage() {
    //log.trace "mainPage(): appId = ${app.id}, apiServerUrl = ${apiServerUrl}"

   	if (!state.accessToken || !state.refreshToken) {
        log.debug "No access or refresh tokens found - calling createAccessToken()"
        state.authToken = null
        state.accessToken = createAccessToken()
    } else {
	    log.debug "Access token ${state.accessToken} found - saving list of calendars."
        if (state.refreshToken) {
      		log.debug "state.refreshToken ${state.refreshToken} found"

			getCalendarList()
            isTokenExpired("mainPage")
        } else {        	
			log.debug "refresh token is null"
        }
    }

    if (!state.authToken) {
        return dynamicPage(name: "mainPage", uninstall: false) {
            log.debug "No authToken found."
            def redirectUrl = "https://graph.api.smartthings.com/oauth/initialize?appId=${app.id}&access_token=${state.accessToken}&apiServerUrl=${apiServerUrl}"
            log.debug "RedirectUrl = ${redirectUrl}"

            section("Google Account linking"){
                href url:redirectUrl, style:"external", required:true, title:"Tap below to log in to Google and authorize access for ${appName()}", description:"Click to enter credentials"
            }
        }
    } else {
        def days = getDayOfWeek()
		updateTodayDate()

		return dynamicPage(name: "mainPage", uninstall: true) {
                    log.debug "authToken ${state.authToken} found."

                section("Select Days off") {                               
                    input name: "watchOffdays", title:"Days off", type: "enum", required:true, multiple:true, description: "Which days off?", metadata:[values:days], submitOnChange: true 
                }

				section("Select Holiday Google Calender") {                               
                    input name: "holidayCalendar", title:"Holiday Calendar", type: "enum", required:true, multiple:false, description: "Which holiday calendar?", metadata:[values:state.holidayCalendars], submitOnChange: true 
                }
                if (holidayCalendar) {
                	getAllHolidayEvents()
                    section {                               
                        input name: "holidayList",  title: "Holiday List", type: "enum", required:true, multiple:true, description: "Which holiday?", metadata: [values:state.allHolidays], submitOnChange: true
                    }
                }
                
                section("Select Private Google Calenders") {                               
                    input name: "privateCalendars", title:"Private Calendars", type: "enum", required:true, multiple:true, description: "Which private calendars?", metadata:[values:state.privateCalendars], submitOnChange: true 
                }
                
                if (privateCalendars) {
                    section {                               
                         paragraph "> How to tagging for Dayoff < \r\n\r\nwhen you create a schedule in Google Calendar.\r\n\r\nAdd the tag ${getTagDayOffFilter()} in the memo or notes field"
                         paragraph "> How to tagging for Workday < \r\n\r\nAdd the tag ${getTagWorkDayFilter()} in the memo or notes field"
                         paragraph "> Example < \r\n\r\nToday holyday, but you must go to work today..... \r\n\r\nAdd the tag ${getTagWorkDayFilter()} in the memo or notes field\r\n\r\nThis case ignored Holydays or dayoffs"
                    }
                }

                section("About"){
                    paragraph "${version()}"
                }
                
                /*
				section("TEST"){
                	def dayMap = getDayOfWeek()
                    def saveD = dayMap[7]
                    paragraph "${updateTodayDate()} $saveD"
                } 
                */
            }
        }
}

def getTagDayOffFilter() {
	return "#dayoff"
}

def getTagWorkDayFilter() {
	return "#workday"
}

def getCurrentTime() {
    //RFC 3339 format
    def d = new Date().format("yyyy-MM-dd'T'HH:mm:ssZ", location.timeZone)
    return d
}

def getTodayStartTime() {
    //RFC 3339 format
    def d = new Date().format("yyyy-MM-dd'T'00:00:00Z", location.timeZone)
    return d
}

def getTodayEndTime() {
    //RFC 3339 format
    def d = new Date().format("yyyy-MM-dd'T'23:59:59Z", location.timeZone)
    return d
}

def getTommorowStartTime() {
//RFC 3339 format
	def today = new Date()
	def tommorow = today.plus(1)

    return tommorow.format("yyyy-MM-dd'T'00:00:00Z", location.timeZone)
}

def updateTodayDate() {
	state.thisYear = new Date().format("yyyy", location.timeZone)
    state.thisMonth = new Date().format("MM", location.timeZone)
    state.todayDay = new Date().format("dd", location.timeZone)
    state.todayDate = "${state.thisYear}-${state.thisMonth}-${state.todayDay}"
    state.todayDayOfWeek = new Date().format("EEE", location.timeZone)
    log.debug "${state.todayDate} ${state.todayDayOfWeek}"
    
    return "${state.todayDate} ${state.todayDayOfWeek}"
}

private getCalendarList() {
    log.debug "getCalendarList()"
    isTokenExpired("getCalendarList")

    def path = "/calendar/v3/users/me/calendarList"
    def calendarListParams = [
        uri: "https://www.googleapis.com",
        path: path,
        headers: ["Content-Type": "text/json", "Authorization": "Bearer ${state.authToken}"],
        query: [format: 'json', body: requestBody]
    ]

    def privateCals = [:]
    def holidayCals = [:]

    try {
        httpGet(calendarListParams) { resp ->
            resp.data.items.each { stat ->
                if (stat.id.contains("#holiday@")) {
                    holidayCals[stat.id] = stat.summary
                } else {
                    privateCals[stat.id] = stat.summary
				}
            }
        }
    } catch (e) {
        log.debug "error: ${path}"
        log.debug e
        if (refreshAuthToken()) {
            return getCalendarList()
        } else {
            log.debug "fatality"
            log.error e.getResponse().getData()
        }
    }

    state.privateCalendars = privateCals
    state.holidayCalendars = holidayCals
    
    /*
    def i=1
    def calList = ""
    def calCount = stats.size()
    calList = calList + "\nYou have ${calCount} available Gcal calendars (Calendar Name - calendarId): \n\n"
    stats.each {
     	calList = calList + "(${i})  ${it.value} - ${it.key} \n"
        i = i+1
	}

    log.info calList
    */
}

private getAllHolidayEvents() {
    log.debug "getAllHolidayEvents()"
    
    def pathParams = [
        timeMin : "${state.thisYear}-01-01T01:00:00.0Z",
        timeMax: "${state.thisYear}-12-31T23:59:59.0Z",
        orderBy: "starttime",
        singleEvents: true
    ]

	def eventListParams = [
        uri: "https://www.googleapis.com",
        path: "/calendar/v3/calendars/${holidayCalendar}/events",
        headers: ["Content-Type": "text/json", "Authorization": "Bearer ${state.authToken}"],
		query: pathParams
    ]

    //log.debug "getAllHolidayEvents event params: $eventListParams"

    def events = []
    try {
        httpGet(eventListParams) { resp ->
            events = resp.data
        }
    } catch (e) {
        log.error e.getResponse().getData()
    }
    
   //log.debug "getAllHolidayEvents : ${events.items}"
   
   def allEvents = [:]
   
 
   events.items.each {
   	 //log.debug "getAllHolidayEvents : ${it.summary} ${it.start.date}"
        def convertedDate = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(it.start.date)
        convertedDate = new java.text.SimpleDateFormat("MM-dd EEE", Locale.ENGLISH).format(convertedDate);
        allEvents[it.summary] = "${it.summary} (${convertedDate})"
   }

   state.allHolidays = allEvents
}

def pullData() {
	log.debug "pullData()"
    isTokenExpired("pullData")
    
    def device = getDevice()
    device?.updateLastTime()

	updateTodayDate()
	
	log.debug "${state.todayDate}"
    
    def isDayOff = checkDayoff()
    def isHoliday = checkHoliday()
    checkTagDay()
    def isTagDayOff = (state.tagDayOffCheck != "N/A")
    def isTagWorkDay = (state.tagWorkDayCheck != "N/A")
  
    if ((isDayOff || isHoliday || isTagDayOff) && !isTagWorkDay) {
        device?.on()
        log.debug "device on"
    } else {
    	device?.off()
        log.debug "device off"
    }
    device?.updateCheckData(state.dayOffCheck, state.holidayCheck, state.tagDayOffCheck, state.tagWorkDayCheck)
    def allNextTagdays = getNextTagdays()
    device?.updateNextTagdays(allNextTagdays)
}

boolean checkDayoff() {
	log.debug "checkDayoff()"

    def daysMap = getDayOfWeek()
	log.debug "today is ${state.todayDayOfWeek}"
    
    boolean result = false
    state.dayOffCheck = "N/A"
    settings.watchOffdays.each {
    	def dayNum = Integer.parseInt(it)
    	def savedDayOff = daysMap[dayNum]
    	if ("${savedDayOff}" == "${state.todayDayOfWeek}") {
            result = true
            state.dayOffCheck = "${state.todayDayOfWeek} : Day-off"
            log.debug "${state.dayOffCheck}"
        }
    }

	return result
}

boolean checkHoliday() {
    log.debug "checkHoliday()"

    def pathParams = [
        timeMin: getTodayStartTime(),
        timeMax: getTodayEndTime()
        //timeMin: "2019-05-06T00:00:00.000Z",
        //timeMax : "2019-05-06T23:59:00.000Z"
    ]

	def eventListParams = [
        uri: "https://www.googleapis.com",
        path: "/calendar/v3/calendars/${holidayCalendar}/events",
        headers: ["Content-Type": "text/json", "Authorization": "Bearer ${state.authToken}"],
        query: pathParams
    ]

    log.debug "checkHoliday event params: $eventListParams"

    def events = []
    try {
        httpGet(eventListParams) { resp ->
            events = resp.data
        }
    } catch (e) {
        log.error e.getResponse().getData()
    }
    
   log.debug "checkHoliday events : ${events.items}"
   
   boolean result = false
	state.holidayCheck = "N/A"
	events.items.each {
   		if (it.start.date == state.todayDate) {
        //if (it.start.date == "2019-05-06") {
           	if (holidayList.contains(it.summary)) {
        		result = true
                state.holidayCheck = "${it.summary}"
                log.debug "checkHoliday event O : ${it.summary} ${it.start.dateTime}"
            } 
        } 
   }

   return result
}

def checkTagDay() {
	boolean result = false
    state.tagDayOffCheck = "N/A"
    state.tagWorkDayCheck = "N/A"
	settings.privateCalendars.each {
       	checkTagDay(it)
    }
}

def checkTagDay(privateCalendar) {
    log.debug "checkTagDay() ${privateCalendar}"

    def pathParams = [
        timeMin: getTodayStartTime(),
        timeMax: getTodayEndTime()
    ]

	def eventListParams = [
        uri: "https://www.googleapis.com",
        path: "/calendar/v3/calendars/${privateCalendar}/events",
        headers: ["Content-Type": "text/json", "Authorization": "Bearer ${state.authToken}"],
        query: pathParams
    ]

    log.debug "checkTagDay event params: $eventListParams"

    def events = []
    try {
        httpGet(eventListParams) { resp ->
            events = resp.data
        }
    } catch (e) {
        log.error e.getResponse().getData()
    }

   log.debug "checkTagDay ${privateCalendar} : ${events.items}"
   
   events.items.each {
   		log.debug "checkTagDay event : ${it.summary} ${it.description}"
   		if (it.description?.contains(getTagDayOffFilter())) {
            state.tagDayOffCheck = "${it.summary}"
	   		log.debug "checkTagDayOff event O : ${it.summary} ${it.start.date}"
        } 
        
        if (it.description?.contains(getTagWorkDayFilter())) {
            state.tagWorkDayCheck = "${it.summary}"
	   		log.debug "checkTagWorkDay event O : ${it.summary} ${it.start.date}"
        } 
   }
}

def getNextTagdays() {
	def nextTagdays = []
	settings.privateCalendars.each {
        nextTagdays.addAll(getNextTagdays(it))
    }

	return nextTagdays
}

def getNextTagdays(privateCalendar) {
    log.debug "getNextTagdays() ${privateCalendar}"

    def pathParams = [
        timeMin: getTommorowStartTime(),
        timeMax: "${state.thisYear}-12-31T23:59:59.0Z",
        singleEvents: true,
        maxResults: 1,
        orderBy : "starttime",
        q: getTagDayOffFilter()
    ]

	def eventListParams = [
        uri: "https://www.googleapis.com",
        path: "/calendar/v3/calendars/${privateCalendar}/events",
        headers: ["Content-Type": "text/json", "Authorization": "Bearer ${state.authToken}"],
        query: pathParams
    ]

    log.debug "getNextTagdays event params: $eventListParams"

    def events = []
    try {
        httpGet(eventListParams) { resp ->
            events = resp.data
        }
    } catch (e) {
        log.error e.getResponse().getData()
    }

   log.debug "getNextTagdays ${privateCalendar} : ${events.items}"
   
   def nextTagdays = []
   events.items.each {
   		/*if (it.start.dateTime){
       		def convertedDate = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(it.start.dateTime)
       		convertedDate = new java.text.SimpleDateFormat("MM-dd EEE", Locale.ENGLISH).format(convertedDate);
       		nextTagdays.add("${it.summary} - ${convertedDate}")
       } else {
       		nextTagdays.add("${it.summary} - ${it.start.date}")
       }*/nextTagdays.add("${it.summary}")
   }

   return nextTagdays
}

String toQueryString(Map m) {
    return m.collect { k, v -> "${k}=${URLEncoder.encode(v.toString())}" }.sort().join("&")
}

// App lifecycle hooks
def installed() {
	log.trace "installed"
    if (!state.accessToken) {
        createAccessToken()
    } else {
        initialize()
    }
}

// called after settings are changed
def updated() {
	log.trace "updated"
    if (!state.accessToken) {
        createAccessToken()
    } else {
        def device = getDevice()
        device?.updated()
    }
}

def uninstalled() {
	log.trace "uninstalled"
    revokeAccess()
    deleteDevice()
}

def initialize() {
	log.trace "initialize with settings: ${settings}"

    pullData()
}

// child device
private deleteDevice() {
	if (childCreated()) {
    	log.debug "Delete getDeviceID()"
    	deleteChildDevice(getDeviceID())
    }
}

private childCreated() {
    def isChild = getChildDevice(getDeviceID())
    log.debug "childCreated? ${isChild}"
    return isChild
}

private getDeviceID() {
    return "${app.id}"
}

def getDevice() {
	log.trace "getDevice()"
	def device
    if (!childCreated()) {
    	device = addChildDevice("woobooung", "${appName()} Switch", getDeviceID(), null, [label: "${appName()} Switch", completedSetup: true])
	} else {
        device = getChildDevice(getDeviceID())
    }
    return device
}


// oauth
def oauthInit() {
    state.oauthInitState = UUID.randomUUID().toString()

    def oauthParams = [
        response_type: "code",
        scope: "https://www.googleapis.com/auth/calendar",
        client_id: getClientId(),
        redirect_uri: "https://graph.api.smartthings.com/oauth/callback",
        include_granted_scopes: "true",
        access_type : "offline",
        state: state.oauthInitState
    ]

    redirect(location: "https://accounts.google.com/o/oauth2/v2/auth?${toQueryString(oauthParams)}")
}

def oauthCallback() {
	log.debug "state.oauthInitState ${state.oauthInitState}"
    log.debug "params.state ${params.state}"
    log.debug "callback() >> params: $params, params.code ${params.code}"

	log.debug "token request: $params.code"
	
	def postParams = [
		uri: "https://www.googleapis.com",  
		path: "/oauth2/v4/token",		
		requestContentType: "application/x-www-form-urlencoded; charset=utf-8",
		body: [
			code: params.code,
			client_secret: getSecretKey(),
			client_id: getClientId(),
			grant_type: "authorization_code",
			redirect_uri: "https://graph.api.smartthings.com/oauth/callback"
		]
	]

	log.debug "postParams: ${postParams}"

	def jsonMap
	try {
		httpPost(postParams) { resp ->
			log.debug "resp callback"
			log.debug resp.data
			if (!state.refreshToken && resp.data.refresh_token) {
            	state.refreshToken = resp.data.refresh_token
            }
            state.authToken = resp.data.access_token
            state.last_use = now()
			jsonMap = resp.data
		}
        log.debug "After Callback: state.authToken = ${state.authToken}  / state.refreshToken = ${state.refreshToken}"
	} catch (e) {
		log.error "something went wrong: $e"
		log.error e.getResponse().getData()
		return
	}

	if (state.authToken && state.refreshToken ) {
		success()
	} else {
		fail()
	}
}

def isTokenExpired(whatcalled) {
    log.debug "isTokenExpired() called by ${whatcalled}"
    
    if (state.last_use == null || now() - state.last_use > 3600) {
    	log.debug "authToken null or old (>3600) - calling refreshAuthToken()"
        return refreshAuthToken()
    } else {
	    log.debug "authToken good"
	    return false
    }    
}

def success() {
    def message = """
    		<p>Your account is now connected to ${appName()}!</p>
            <p>Now return to the SmartThings App and then </p>
            <p>Click 'Done' to finish setup of ${appName()}.</p>
    """
    connectionStatus(message)
}

def fail() {
    def message = """
        <p>There was an error authorizing</p>
        <p>your Google account.  Please try again.</p>
    """
    connectionStatus(message)
}

private refreshAuthToken() {
    log.debug "refreshAuthToken()"
    if(!state.refreshToken) {    
        log.warn "Can not refresh OAuth token since there is no refreshToken stored"
        log.debug state
    } else {
    	def refTok 
   	    if (state.refreshToken) {
        	refTok = state.refreshToken
    		log.debug "Existing state.refreshToken = ${refTok}"
        }
        def stcid = getClientId()		
        def stcs = getSecretKey()		
       
        def refreshParams = [
            method: 'POST',
            uri   : "https://www.googleapis.com",
            path  : "/oauth2/v3/token",
            body : [
                refresh_token: "${refTok}", 
                client_secret: stcs,
                grant_type: 'refresh_token', 
                client_id: stcid
            ],
        ]

        //log.debug refreshParams

        //changed to httpPost
        try {
            httpPost(refreshParams) { resp ->
                log.debug "Token refreshed..."

                if(resp.data) {
                    log.debug resp.data
                    state.authToken = resp?.data?.access_token
					state.last_use = now()
                    
                    return true
                }
            }
        }
        catch(Exception e) {
            log.debug "caught exception refreshing auth token: " + e
            log.error e.getResponse().getData()
        }
    }
    return false
}

def revokeAccess() {
    log.trace "revokeAccess()"

	refreshAuthToken()
	
	if (!state.authToken) {
    	return
    }
    
	try {
    	def uri = "https://accounts.google.com/o/oauth2/revoke?token=${state.authToken}"
        log.debug "Revoke: ${uri}"
		httpGet(uri) { resp ->
			log.debug "resp"
			log.debug resp.data
    		revokeAccessToken()
            state.accessToken = state.refreshToken = state.authToken = null
		}
	} catch (e) {
		log.debug "something went wrong: $e"
		log.debug e.getResponse().getData()
	}
}

private connectionStatus(message) {
    def html = """
                <!DOCTYPE html>
                <html>
                <head>
                <meta name="viewport" content="initial-scale=1, maximum-scale=1, user-scalable=no, width=device-width height=device-height">

                <link href='https://fonts.googleapis.com/css?family=Roboto' rel='stylesheet' type='text/css'>
                <title>SmartThings Connection</title>
                <style type="text/css">
                @font-face {
                font-family: 'Swiss 721 W01 Thin';
                src: url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-thin-webfont.eot');
                src: url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-thin-webfont.eot?#iefix') format('embedded-opentype'),
                url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-thin-webfont.woff') format('woff'),
                url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-thin-webfont.ttf') format('truetype'),
                url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-thin-webfont.svg#swis721_th_btthin') format('svg');
                font-weight: normal;
                font-style: normal;
                }
                @font-face {
                font-family: 'Swiss 721 W01 Light';
                src: url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-light-webfont.eot');
                src: url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-light-webfont.eot?#iefix') format('embedded-opentype'),
                url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-light-webfont.woff') format('woff'),
                url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-light-webfont.ttf') format('truetype'),
                url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-light-webfont.svg#swis721_lt_btlight') format('svg');
                font-weight: normal;
                font-style: normal;
                }
                body {
                margin: 0;
                width : 100%;
                }
                .container {
                width: 100%;

                /*background: #eee;*/
                text-align: center;
                }
                img {
                vertical-align: middle;
                margin-top:20.3125vw;

                }

                .google{
                width: 25vw;
                height: 25vw;
                margin-right : 8.75vw;
                }
                .chain {
                width:6.25vw;
                height: 6.25vw;
                }
                .smartt {
                width: 25vw;
                height: 25vw;
                margin-left: 8.75vw
                }

                p {
                font-size: 21px;
                font-weight: 300;
                font-family: Roboto;
                text-align: center;
                color: #4c4c4e;

                margin-bottom: 0;
                }
                /*
                p:last-child {
                margin-top: 0px;
                }
                */
                span {
                font-family: 'Swiss 721 W01 Light';
                }
                </style>

                </head>
                <body>
                <div class="container">
                <img class="google" src="https://cdn4.iconfinder.com/data/icons/new-google-logo-2015/400/new-google-favicon-512.png" alt="Google icon" />
                <img class="chain" src="https://s3-ap-northeast-1.amazonaws.com/smartthings-images/icon_link.svg" alt="connected device icon" />
                <img class="smartt" src="https://s3.amazonaws.com/smartapp-icons/Partner/support/st-logo%402x.png" alt="SmartThings logo" />
                <p>${message}</p>

                </div>

                </body>
                </html>
                """
    render contentType: 'text/html', data: html
}