/**
 *  Weather Flow (woobooung@gmail.com)
 *  
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *   - Version 0.0.4 (2018-09-22)
 *      Changed schedule function
 *
 *   - Version 0.0.3 (2018-09-20)
 *      When occured exception error, do rescheduling
 *      Modified interval minimum(10 sec -> 1 min)
 * 
 *   - Version 0.0.2 (2018-09-16)
 *      Added rain detected logic
 *      Before Every time detect option: 
 *				if (precip > 0) then Rain detected
 *				if (precip == 0) then Rain not detected
 *      Additional option first time detect :
 *				if (precip_accum_last_1hr == 0 && precip > 0) then Rain detected
 *				if (precip_accum_last_1hr == 0 && precip == 0) then Rain not detected
 *
 *   - Version 0.0.1 (2018-09-15)
 *      Base code
 */

import groovy.json.JsonSlurper
import groovy.transform.Field

@Field 
LANGUAGE_MAP = [
    "air_temperature": [
        "Korean": "온도",
        "English": "Temperature"
    ],
    "relative_humidity": [
        "Korean": "습도",
        "English": "Humidity"
    ],
	"dew_point": [
        "Korean": "이슬점",
        "English": "Dew point"
    ],
	"barometric_pressure":[
    	"Korean": "기압",
        "English": "Barometric Pressure"
    ],
    "sea_level_pressure":[
    	"Korean": "해상 기압",
        "English": "Sea Level Pressure"
    ],
    "precip": [
    	"Korean": "강수량",
        "English": "Precip"
    ],
    "precip_accum_local_day": [
    	"Korean": "오늘 강수량",
        "English": "Today precip"
    ],
    "precip_accum_local_yesterday": [
    	"Korean": "어제 강수량",
        "English": "Yesterday precip"
    ],
    "wind_avg": [
        "Korean": "풍속 평균",
        "English": "Wind avg"
    ],
    "wind_direction": [
        "Korean": "풍향",
        "English": "Wind direction"
    ],
    "wind_gust": [
        "Korean": "풍속",
        "English": "Wind gust"
    ],
    "solar_radiation": [
        "Korean": "태양 에너지",
        "English": "Solar radiation"
    ],
    "uv": [
        "Korean": "자외선",
        "English": "UV"
    ],
    "brightness": [
        "Korean": "조도",
        "English": "Brightness"
    ],
    "lightning_strike_last_epoch": [
        "Korean": "번개 마지막 발생 시간",
        "English": "Lightning strike last"
    ],
    "lightning_strike_last_distance": [
        "Korean": "번개 발생 거리",
        "English": "Lightning strike last distance"
    ],
    "lightning_strike_count_last_3hr": [
        "Korean": "번개 발생 (3시간 동안)",
        "English": "Lightning strike count last 3hr"
    ],
    "latitude": [
        "Korean": "위도",
        "English": "Latitude"
    ],
    "longitude": [
        "Korean": "경도",
        "English": "Longitude"
    ],
    "timezone": [
        "Korean": "시간대",
        "English": "TimeZone"
    ],
]

metadata {
	definition (name: "Weather Flow", namespace: "WooBooung", author: "Booung", ocfDeviceType: "x.com.st.d.airqualitysensor") {
        capability "Water Sensor"
        capability "Temperature Measurement"
        capability "Relative Humidity Measurement"
        capability "Illuminance Measurement"
		capability "Ultraviolet Index"
		capability "Polling"
        capability "Configuration"
		capability "Refresh"
		capability "Sensor"
        
        // Weather Station infos
        attribute "timestamp", "Date"
        // attribute "air_temperature", "number" - > capability "Temperature Measurement"
        attribute "barometric_pressure", "number"
        attribute "sea_level_pressure", "number"
        // attribute "relative_humidity", "number" - > capability "Relative Humidity Measurement"
        //attribute "precip", "number" // precip > 0 - > capability "Water Sensor"
        attribute "precip_accum_last_1hr", "number"
        attribute "precip_accum_last_24hr", "number"
        attribute "precip_accum_local_day", "number"
        attribute "precip_accum_local_yesterday", "number"
        attribute "wind_avg", "number"
        attribute "wind_direction", "number"
        attribute "wind_gust", "number"
        attribute "wind_lull", "number"
        attribute "solar_radiation", "number"
        // attribute "uv", "number" - > capability "Ultraviolet Index"
        // attribute "brightness", "number" - > capability "Illuminance Measurement"
        attribute "lightning_strike_last_epoch", "Date"
        attribute "lightning_strike_last_distance", "number"
        attribute "lightning_strike_count", "number"
        attribute "lightning_strike_count_last_3hr", "number"
        attribute "feels_like", "number"
        attribute "heat_index", "number"
        attribute "wind_chill", "number"
        attribute "dew_point", "number"
        attribute "wet_bulb_temperature", "number"
        attribute "delta_t", "number"
      	attribute "air_density", "number"
        
        // units
        attribute "units_temp", "string"
        attribute "units_wind", "string"
        attribute "units_precip", "string"
        attribute "units_pressure", "string"
        attribute "units_distance", "string"
        attribute "units_direction", "string"
        attribute "units_other", "string"
       
        command "refresh"
        command "pollWeatherFlow"
	}

	preferences {
		input name: "api_key", title: "API Key", type: "text", defaultValue: "20c70eae-e62f-4d3b-b3a4-8586e90f3ac8", required: true
		input name: "station_id", title: "Station Id", type: "password", description: "Refer to below \"how to get station id\"", required: true
        input name: "polling_interval", title: "Update interval", type: "enum", options:[1 : "1 min", 5 : "5 min", 10 : "10 min", 30 : "30 min"], defaultValue: 1, displayDuringSetup: true
        input name: "rain_detected_option", title: "Rain Detected Option", type: "enum", options: ["Every time", "First time"], defaultValue: "Every time", description: "Refor to below \"Rain detect options\"", displayDuringSetup: true
        input name: "selected_lang", title:"Select a language", type: "enum", options: ["English", "Korean"], defaultValue: "English", displayDuringSetup: true
        input type: "paragraph", element: "paragraph", title: "How to get station Id", description: "Weather Flow app -> Settings's Manage -> Stations -> Status click", displayDuringSetup: false
        input type: "paragraph", element: "paragraph", title: "Rain detect option", description: "Every time : (Default value)\nif (precip > 0) then Rain detected\nif (precip == 0) then Rain not detected\n\nFirst time :\nif (precip_accum_last_1hr == 0 && precip > 0) then Rain detected\nif (precip_accum_last_1hr == 0 && precip == 0) then Rain not detected", displayDuringSetup: false
        input type: "paragraph", element: "paragraph", title: "Version", description: "0.0.4", displayDuringSetup: false
	}

	simulator {
		// TODO: define status and reply messages here
	}

	tiles {
		multiAttributeTile(name:"water", type: "generic", width: 6, height: 4){
			tileAttribute ("device.water", key: "PRIMARY_CONTROL") {
               	attributeState "dry", label:'Rain is None', icon: "st.alarm.water.dry", backgroundColor: "#ffffff"
            	attributeState "wet", label:'Rain detected', icon: "st.alarm.water.wet", backgroundColor: "#00A0DC"
			}
            tileAttribute("device.timestamp", key: "SECONDARY_CONTROL") {
    			attributeState("default", label:'Last Update: ${currentValue}',icon: "st.Health & Wellness.health9")
            }
		}
        
        valueTile("air_temperature_level", "air_temperature_level", decoration: "flat") {
            state "default", label: 'Temp'
        }

        valueTile("air_temperature", "device.temperature",  decoration: "flat") {
			state "default", label:'${currentValue}°'
		}

        valueTile("relative_humidity_label", "relative_humidity_label", decoration: "flat") {
            state "default", label: 'Humi'
        }

		valueTile("relative_humidity", "device.humidity", decoration: "flat") {
			state "default", label:'${currentValue}%'
		}
        
        valueTile("dew_point_label", "device.dew_point_label", decoration: "flat") {
            state "default", label: '${currentValue}'
        }

		valueTile("dew_point", "device.dew_point", decoration: "flat") {
			state "default", label:'${currentValue}°'
		}
		
        valueTile("precip_label", "device.precip_label", decoration: "flat") {
            state "default", label: '${currentValue}'
        }

		valueTile("precip", "device.precip", decoration: "flat") {
			state "default", label:'${currentValue}'
		}

        valueTile("precip_accum_local_day_label", "device.precip_accum_local_day_label", decoration: "flat") {
            state "default", label: '${currentValue}'
        }

		valueTile("precip_accum_local_day", "device.precip_accum_local_day", decoration: "flat") {
			state "default", label:'${currentValue}'
		}
        
        valueTile("precip_accum_local_yesterday_label", "device.precip_accum_local_yesterday_label", decoration: "flat") {
            state "default", label: '${currentValue}'
        }

		valueTile("precip_accum_local_yesterday", "device.precip_accum_local_yesterday", decoration: "flat") {
			state "default", label:'${currentValue}'
		}

        valueTile("wind_avg_label", "device.wind_avg_label", decoration: "flat") {
            state "default", label: '${currentValue}'
        }

		valueTile("wind_avg", "device.wind_avg", decoration: "flat") {
			state "default", label:'${currentValue}'
		}

        valueTile("wind_direction_label", "device.wind_direction_label", decoration: "flat") {
            state "default", label: '${currentValue}'
        }

		valueTile("wind_direction", "device.wind_direction", decoration: "flat") {
			state "default", label:'${currentValue}°'
		}
        
        valueTile("wind_gust_label", "device.wind_gust_label", decoration: "flat") {
            state "default", label: '${currentValue}'
        }

		valueTile("wind_gust", "device.wind_gust", decoration: "flat") {
			state "default", label:'${currentValue}'
		} 
        
        valueTile("solar_radiation_label", "device.solar_radiation_label", decoration: "flat") {
            state "default", label: '${currentValue} W/㎡'
        }

		valueTile("solar_radiation", "device.solar_radiation", decoration: "flat") {
			state "default", label:'${currentValue}'
		}
        
        valueTile("uv_label", "device.uv_label", decoration: "flat") {
            state "default", label: '${currentValue}'
        }

		valueTile("uv", "device.ultravioletIndex", decoration: "flat") {
			state "default", label:'${currentValue}'
		}
        
        valueTile("brightness_label", "device.brightness_label", decoration: "flat") {
            state "default", label: '${currentValue} lux'
        }

		valueTile("brightness", "device.illuminance", decoration: "flat") {
			state "default", label:'${currentValue}'
		}
        
        valueTile("lightning_strike_last_epoch_label", "device.lightning_strike_last_epoch_label", decoration: "flat") {
            state "default", label: '${currentValue}'
        }

		valueTile("lightning_strike_last_epoch", "device.lightning_strike_last_epoch", decoration: "flat") {
			state "default", label:'${currentValue}'
		}
        
        valueTile("lightning_strike_last_distance_label", "device.lightning_strike_last_distance_label", decoration: "flat") {
            state "default", label: '${currentValue}'
        }

		valueTile("lightning_strike_last_distance", "device.lightning_strike_last_distance", decoration: "flat") {
			state "default", label:'${currentValue}'
		}
       
        valueTile("lightning_strike_count_last_3hr_label", "device.lightning_strike_count_last_3hr_label", decoration: "flat") {
            state "default", label: '${currentValue}'
        }

		valueTile("lightning_strike_count_last_3hr", "device.lightning_strike_count_last_3hr", decoration: "flat") {
			state "default", label:'${currentValue}'
		}
        
        valueTile("barometric_pressure_label", "device.barometric_pressure_label", width: 2, decoration: "flat") {
            state "default", label: '${currentValue}'
        }

		valueTile("barometric_pressure", "device.barometric_pressure", decoration: "flat") {
			state "default", label:'${currentValue}'
		}
        
        valueTile("sea_level_pressure_label", "device.sea_level_pressure_label", width: 2, decoration: "flat") {
            state "default", label: '${currentValue}'
        }

		valueTile("sea_level_pressure", "device.sea_level_pressure", decoration: "flat") {
			state "default", label:'${currentValue}'
		}
        
        valueTile("latitude_label", "device.latitude_label", decoration: "flat") {
            state "default", label: '${currentValue}'
        }

		valueTile("latitude", "device.latitude", width: 2, decoration: "flat") {
			state "default", label:'${currentValue}'
		}
        
        valueTile("longitude_label", "device.longitude_label", decoration: "flat") {
            state "default", label: '${currentValue}'
        }

		valueTile("longitude", "device.longitude", width: 2, decoration: "flat") {
			state "default", label:'${currentValue}'
		}
	
/*        valueTile("station_id_label", "device.station_id_label", decoration: "flat") {
            state "default", label: 'Station Id'
        }

		valueTile("station_id", "device.station_id", width: 2, decoration: "flat") {
			state "default", label:'${currentValue}'
		}
*/
        valueTile("timezone_label", "device.timezone_label", decoration: "flat") {
            state "default", label:'${currentValue}'
        }
        
        valueTile("timezone", "device.timezone", width: 2, decoration: "flat") {
			state "default", label:'${currentValue}'
		}
        
        valueTile("station_name_label", "device.station_name_label", decoration: "flat") {
            state "default", label: 'Station Name'
        }

		valueTile("station_name", "device.station_name", width: 2, decoration: "flat") {
			state "default", label:'${currentValue}'
		}
        
        standardTile("refresh_value", "device.refresh", width: 2, decoration: "flat") {
			state "default", label: "", action: "refresh", icon:"st.secondary.refresh"
		}
	}
}

// parse events into attributes
def parse(String description) {
	debugLog("Parsing '${description}'")
}

def installed() {
	debugLog("installed()")
    refresh()
    setLanguage(settings.selected_lang)
}

def uninstalled() {
	debugLog("uninstalled()")
	unschedule()
}

def updated() {
	debugLog("updated()")
	refresh()
    setLanguage(settings.selected_lang)
}

def refresh() {
	debugLog("refresh()")
    unschedule()
	try {
        startPoll()
    } catch (e) {
        log.error "error: pollWeatherFlow $e"
    }
}

def startPoll() {
	def healthCheckInterval = "0/${settings.polling_interval}"
    log.debug "startPoll $healthCheckInterval"
    schedule("0 $healthCheckInterval * * * ?", pollWeatherFlow)
}

def setLanguage(language){
    debugLog("Languge >> ${language}")
    state.language = language

    sendEvent(name:"air_temperature_label", value: LANGUAGE_MAP["air_temperature"][language])
    sendEvent(name:"relative_humidity_label", value: LANGUAGE_MAP["relative_humidity"][language])
    sendEvent(name:"dew_point_label", value: LANGUAGE_MAP["dew_point"][language])
    sendEvent(name:"barometric_pressure_label", value: LANGUAGE_MAP["barometric_pressure"][language])
    sendEvent(name:"sea_level_pressure_label", value: LANGUAGE_MAP["sea_level_pressure"][language]) 
    sendEvent(name:"precip_label", value: LANGUAGE_MAP["precip"][language])
    sendEvent(name:"precip_accum_local_day_label", value: LANGUAGE_MAP["precip_accum_local_day"][language])
    sendEvent(name:"precip_accum_local_yesterday_label", value: LANGUAGE_MAP["precip_accum_local_yesterday"][language])
    sendEvent(name:"wind_avg_label", value: LANGUAGE_MAP["wind_avg"][language])
    sendEvent(name:"wind_direction_label", value: LANGUAGE_MAP["wind_direction"][language])
    sendEvent(name:"wind_gust_label", value: LANGUAGE_MAP["wind_gust"][language])
    sendEvent(name:"solar_radiation_label", value: LANGUAGE_MAP["solar_radiation"][language])
    sendEvent(name:"uv_label", value: LANGUAGE_MAP["uv"][language])
    sendEvent(name:"brightness_label", value: LANGUAGE_MAP["brightness"][language])
    sendEvent(name:"lightning_strike_last_epoch_label", value: LANGUAGE_MAP["lightning_strike_last_epoch"][language])
    sendEvent(name:"lightning_strike_last_distance_label", value: LANGUAGE_MAP["lightning_strike_last_distance"][language])
    sendEvent(name:"lightning_strike_count_last_3hr_label", value: LANGUAGE_MAP["lightning_strike_count_last_3hr"][language])
    sendEvent(name:"latitude_label", value: LANGUAGE_MAP["latitude"][language])
	sendEvent(name:"longitude_label", value: LANGUAGE_MAP["longitude"][language])
    sendEvent(name:"timezone_label", value: LANGUAGE_MAP["timezone"][language])
}

def configure() {
	debugLog("Configuare()")
    refresh()
    setLanguage(settings.selected_lang)
}

// Weather Flow handle commands
def pollWeatherFlow() {
	debugLog("pollWeatherFlow()")
    def dthVersion = "0.0.1"
    if (station_id && api_key) {
        def params = [
    	    uri: "https://swd.weatherflow.com/swd/rest/observations/station/${station_id}?api_key=${api_key}",
        	contentType: 'application/json'
    	]
        //def refreshTime = (polling_interval as int)
        
        try {
    	    //runIn(refreshTime, pollWeatherFlow)
    		//debugLog("Data will repoll every ${polling_interval} seconds")
           
            httpGet(params) {response ->
                response.headers.each {
                    debugLog("${it.name} : ${it.value}")
                }
                // get the contentType of the response
                // get the status code of the response
                log.debug "response status code: ${response.status}"
                if (response.status == 200) {
                    // get the data from the response body
                    log.debug "response data: ${response.data}"

    				def station_id = response.data.station_id
                    def station_name = response.data.station_name
                    def latitude = response.data.latitude
                    def longitude = response.data.longitude
                    def timezone = response.data.timezone
                    
                    if (station_id > 0) {
                        debugLog("station_id: ${station_id}")
                        sendEvent(name: "station_id", value: station_id as Integer, displayed: false)
                    } else {
                    	log.error "station_id error: ${station_id}"
                        sendEvent(name: "station_id", value: 0, isStateChange: false, displayed: false)
                    }
                    
                    if (station_name != "") {
                        debugLog("station_name: ${station_name}")
                        sendEvent(name: "station_name", value: station_name as String, displayed: false)
                    } else {
                    	log.error "station_name error: ${station_name}"
                        sendEvent(name: "station_name", value: "-", isStateChange: false, displayed: false)
                    }
                    
                    if (latitude > 0) {
                        debugLog("latitude: ${latitude}")
                        sendEvent(name: "latitude", value: latitude as String, displayed: false)
                    } else {
                    	log.error "latitude error: ${station_id}"
                        sendEvent(name: "latitude", value: "", isStateChange: false, displayed: false)
                    }
                    
                    if (longitude > 0) {
                        debugLog("longitude: ${longitude}")
                        sendEvent(name: "longitude", value: longitude as String, displayed: false)
                    } else {
                    	log.error "longitude error: ${longitude}"
                        sendEvent(name: "longitude", value: "", isStateChange: false, displayed: false)
                    }

                    if (timezone != "") {
                        debugLog("timezone: ${timezone}")
                        sendEvent(name: "timezone", value: timezone as String, displayed: false)
                    } else {
                    	log.error "timezone error: ${timezone}"
                        sendEvent(name: "timezone", value: "-", isStateChange: false, displayed: false)
                    }

                    def units_temp = response.data.station_units.units_temp.toUpperCase()
                    def units_wind = (response.data.station_units.units_wind == "mps" ? "m/s" : response.data.station_units.units_wind)
                    def units_precip = response.data.station_units.units_precip
                    def units_pressure = response.data.station_units.units_pressure
                    def units_distance = response.data.station_units.units_distance
                    def units_direction = response.data.station_units.units_direction
                    def units_other = response.data.station_units.units_other

                    if (units_temp != "") {
                        debugLog("units_temp: ${units_temp}")
                        sendEvent(name: "units_temp", value: units_temp as String, displayed: false)
                    } else {
                        log.error "units_temp error: ${units_temp}"
                        sendEvent(name: "units_temp", value: "", isStateChange: false, displayed: false)
                    }

                    if (units_wind != "") {
                        debugLog("units_wind: ${units_wind}")
                        sendEvent(name: "units_wind", value: units_wind as String, displayed: false)
                    } else {
                        log.error "units_wind error: ${units_wind}"
                        sendEvent(name: "units_wind", value: "", isStateChange: false, displayed: false)
                    }

                    if (units_precip != "") {
                        debugLog("units_precip: ${units_precip}")
                        sendEvent(name: "units_precip", value: units_precip as String, displayed: false)
                    } else {
                        log.error "units_precip error: ${units_precip}"
                        sendEvent(name: "units_precip", value: "", isStateChange: false, displayed: false)
                    }

                    if (units_pressure != "") {
                        debugLog("units_pressure: ${units_pressure}")
                        sendEvent(name: "units_pressure", value: units_pressure as String, displayed: false)
                    } else {
                        log.error "units_temp error: ${units_pressure}"
                        sendEvent(name: "units_pressure", value: "", isStateChange: false, displayed: false)
                    }

                    if (units_distance != "") {
                        debugLog("units_distance: ${units_distance}")
                        sendEvent(name: "units_distance", value: units_distance as String, displayed: false)
                    } else {
                        log.error "units_distance error: ${units_distance}"
                        sendEvent(name: "units_distance", value: "", isStateChange: false, displayed: false)
                    }

                    if (units_direction != "") {
                        debugLog("units_direction: ${units_direction}")
                        sendEvent(name: "units_direction", value: units_direction as String, displayed: false)
                    } else {
                        log.error "units_direction error: ${units_direction}"
                        sendEvent(name: "units_direction", value: "", isStateChange: false, displayed: false)
                    }

                    if (units_other != "") {
                        debugLog("units_other: ${units_other}")
                        sendEvent(name: "units_other", value: units_other as String, displayed: false)
                    } else {
                        log.error "units_other error: ${units_other}"
                        sendEvent(name: "units_other", value: "", isStateChange: false, displayed: false)
                    }

                    def timestamp = response.data.obs[0].timestamp
                    def air_temperature = response.data.obs[0].air_temperature
                    def relative_humidity = response.data.obs[0].relative_humidity
                    def dew_point = response.data.obs[0].dew_point
                    def barometric_pressure = response.data.obs[0].barometric_pressure
                    def sea_level_pressure = response.data.obs[0].sea_level_pressure
                    def precip = response.data.obs[0].precip
                    def precip_accum_last_1hr = response.data.obs[0].precip_accum_last_1hr
                    def precip_accum_last_24hr = response.data.obs[0].precip_accum_last_24hr
                    def precip_accum_local_day = response.data.obs[0].precip_accum_local_day
                    def precip_accum_local_yesterday = response.data.obs[0].precip_accum_local_yesterday
                    def wind_avg = response.data.obs[0].wind_avg
                    def wind_direction = response.data.obs[0].wind_direction
                    def wind_gust = response.data.obs[0].wind_gust
                    def wind_lull = response.data.obs[0].wind_lull
                    def solar_radiation = response.data.obs[0].solar_radiation
                    def uv = response.data.obs[0].uv
                    def brightness = response.data.obs[0].brightness
                    def lightning_strike_last_epoch = response.data.obs[0].lightning_strike_last_epoch
                    def lightning_strike_last_distance = response.data.obs[0].lightning_strike_last_distance
                    def lightning_strike_count = response.data.obs[0].lightning_strike_count
                    def lightning_strike_count_last_3hr = response.data.obs[0].lightning_strike_count_last_3hr
                    def feels_like = response.data.obs[0].feels_like
                    def heat_index = response.data.obs[0].heat_index
                    def wind_chill = response.data.obs[0].wind_chill

                    if (timestamp != "") {
                    	def convertDate = new Date(timestamp * 1000L).format("yyyy-MM-dd HH:mm:ss", location.timeZone)
                        debugLog("timestamp: ${convertDate}")
                        sendEvent(name: "timestamp", value: convertDate, displayed: false)
                    } else {
                        log.error "timestamp error: ${timestamp}"
                        sendEvent(name: "timestamp", value: 0, isStateChange: false, displayed: false)
                    }

                    if (air_temperature != "") {
                        debugLog("air_temperature: ${air_temperature}°${units_temp}")
                        sendEvent(name: "temperature", value: air_temperature, unit: units_temp)
                    } else {
                        log.error "air_temperature error: ${air_temperature}"
                        sendEvent(name: "temperature", value: -100, isStateChange: false, displayed: false)
                    }

                    if (relative_humidity != "") {
                        debugLog("relative_humidity: ${relative_humidity}°${units_temp}")
                        sendEvent(name: "humidity", value: relative_humidity, unit: '%')
                    } else {
                        log.error "relative_humidity error: ${relative_humidity}"
                        sendEvent(name: "humidity", value: -100, isStateChange: false, displayed: false)
                    }
                    
                    if (dew_point != "") {
                        debugLog("dew_point: ${dew_point}°${units_temp}")
                        sendEvent(name: "dew_point", value: dew_point, unit: units_temp)
                    } else {
                        log.error "dew_point error: ${dew_point}"
                        sendEvent(name: "dew_point", value: -100, isStateChange: false, displayed: false)
                    }
                    
                    if (barometric_pressure != "") {
                        debugLog("barometric_pressure: ${barometric_pressure} ${units_pressure}")
                        def label = "${LANGUAGE_MAP["barometric_pressure"][state.language]} ${units_pressure}"
                        sendEvent(name: "barometric_pressure_label", value:label)
                        sendEvent(name: "barometric_pressure", value: barometric_pressure, unit: units_pressure)
                    } else {
                        log.error "barometric_pressure error: ${barometric_pressure}"
                        sendEvent(name: "barometric_pressure", value: -100, isStateChange: false, displayed: false)
                    }
                    
                    if (sea_level_pressure != "") {
                        debugLog("sea_level_pressure: ${sea_level_pressure} ${units_pressure}")
                        def label = "${LANGUAGE_MAP["sea_level_pressure"][state.language]} ${units_pressure}"
                        sendEvent(name: "sea_level_pressure_label", value:label)
                        sendEvent(name: "sea_level_pressure", value: sea_level_pressure, unit: units_pressure)
                    } else {
                        log.error "sea_level_pressure error: ${sea_level_pressure}"
                        sendEvent(name: "sea_level_pressure", value: -100, isStateChange: false, displayed: false)
                    }

                    if (precip_accum_last_1hr != "") {
                        debugLog("precip_accum_last_1hr: ${precip_accum_last_1hr} ${units_precip}")
                        sendEvent(name: "precip_accum_last_1hr", value: precip_accum_last_1hr, unit: units_precip)
                    } else {
                        log.error "precip_accum_last_1hr error: ${precip_accum_last_1hr}"
                        sendEvent(name: "precip_accum_last_1hr", value: 0, isStateChange: false, displayed: false)
                    }
                  
                    if (precip_accum_local_day != "") {
                        debugLog("precip_accum_local_day: ${precip_accum_local_day} ${units_precip}")
                        def label = "${LANGUAGE_MAP["precip_accum_local_day"][state.language]} ${units_precip}"
                        sendEvent(name: "precip_accum_local_day_label", value:label)
                        sendEvent(name: "precip_accum_local_day", value: precip_accum_local_day, unit: units_precip)
                    } else {
                        log.error "precip_accum_local_day error: ${precip_accum_local_day}"
                        sendEvent(name: "precip_accum_local_day", value: 0, isStateChange: false, displayed: false)
                    }
                    
                    if (precip_accum_local_yesterday != "") {
                        debugLog("precip_accum_local_yesterday: ${precip_accum_local_yesterday} ${units_precip}")
                        def label = "${LANGUAGE_MAP["precip_accum_local_yesterday"][state.language]} ${units_precip}"
                        sendEvent(name: "precip_accum_local_yesterday_label", value:label)
                        sendEvent(name: "precip_accum_local_yesterday", value: precip_accum_local_yesterday, unit: units_precip)
                    } else {
                        log.error "precip_accum_local_yesterday error: ${precip_accum_local_yesterday}"
                        sendEvent(name: "precip_accum_local_yesterday", value: 0, isStateChange: false, displayed: false)
                    }
                    
                    if (precip != "") {
                        debugLog("precip: ${precip} ${units_precip}")
                        def label = "${LANGUAGE_MAP["precip"][state.language]} ${units_precip}"
                        sendEvent(name: "precip_label", value:label)
                        sendEvent(name: "precip", value: precip, unit: units_precip)
                           
                        debugLog("precip option: ${rain_detected_option}")

                        if (rain_detected_option == "First time") {
                         	if (precip_accum_last_1hr == 0) {
                        		sendEvent(name:"water", value: "dry")
                            } else if (precip_accum_last_1hr > 0 || precip > 0){
								sendEvent(name:"water", value: "wet")                             	
                            }
                        } else {
                        	sendEvent(name:"water", value: (precip > 0 ? "wet" : "dry"))
                        }
                    } else {
                        log.error "precip error: ${precip}"
                        sendEvent(name: "precip", value: 0, isStateChange: false, displayed: false)
                    }

                    if (wind_avg != "") {
                        debugLog("wind_avg: ${wind_avg} ${units_wind}")
                        def label = "${LANGUAGE_MAP["wind_avg"][state.language]} ${units_wind}"
                        sendEvent(name: "wind_avg_label", value:label)
                        sendEvent(name: "wind_avg", value: wind_avg, unit: units_wind)
                    } else {
                        log.error "wind_avg error: ${wind_avg}"
                        sendEvent(name: "wind_avg", value: 0, isStateChange: false, displayed: false)
                    }
                    
                    if (wind_direction != "") {
                        debugLog("wind_direction: ${wind_direction}°")
                        sendEvent(name: "wind_direction", value: wind_direction, unit: "°")
                    } else {
                        log.error "wind_avg error: ${wind_avg}"
                        sendEvent(name: "wind_avg", value: 0, isStateChange: false, displayed: false)
                    }
                    
                    if (wind_gust != "") {
                        debugLog("wind_gust: ${wind_gust} ${units_wind}")
                        def label = "${LANGUAGE_MAP["wind_gust"][state.language]} ${units_wind}"
                        sendEvent(name: "wind_gust_label", value:label)
                        sendEvent(name: "wind_gust", value: wind_gust, unit: units_wind)
                    } else {
                        log.error "wind_gust error: ${wind_gust}"
                        sendEvent(name: "wind_gust", value: 0, isStateChange: false, displayed: false)
                    }
                    
                    if (solar_radiation != "") {
                        debugLog("solar_radiation: ${solar_radiation}")
                        sendEvent(name: "solar_radiation", value: solar_radiation, unit: "W/㎡")
                    } else {
                        log.error "solar_radiation error: ${solar_radiation}"
                        sendEvent(name: "solar_radiation", value: 0, isStateChange: false, displayed: false)
                    }
                    
                    if (uv != "") {
                        debugLog("uv: ${uv}")
                        sendEvent(name: "ultravioletIndex", value: uv)
                    } else {
                        log.error "uv error: ${uv}"
                        sendEvent(name: "ultravioletIndex", value: 0, isStateChange: false, displayed: false)
                    }
                    
                    if (brightness != "") {
                        debugLog("brightness: ${brightness}")
                        sendEvent(name: "illuminance", value: brightness, unit: "lux")
                    } else {
                        log.error "brightness error: ${brightness}"
                        sendEvent(name: "illuminance", value: 0, isStateChange: false, displayed: false)
                    }
                    
                    if (lightning_strike_last_epoch != "") {
                    	def convertDate = new Date(lightning_strike_last_epoch * 1000L).format("yyyy-MM-dd HH:mm:ss", location.timeZone)
                        debugLog("lightning_strike_last_epoch: ${convertDate}")
                        sendEvent(name: "lightning_strike_last_epoch", value: convertDate)
                    } else {
                        log.error "lightning_strike_last_epoch error: ${lightning_strike_last_epoch}"
                        sendEvent(name: "lightning_strike_last_epoch", value: 0, isStateChange: false, displayed: false)
                    }
                    
                    if (lightning_strike_last_distance != "") {
                        debugLog("lightning_strike_last_distance: ${lightning_strike_last_distance} ${units_distance}")
                        def label = "${LANGUAGE_MAP["lightning_strike_last_distance"][state.language]} ${units_distance}"
                        sendEvent(name: "lightning_strike_last_distance_label", value:label)
                        sendEvent(name: "lightning_strike_last_distance", value: lightning_strike_last_distance, unit: units_distance)
                    } else {
                        log.error "lightning_strike_last_distance error: ${lightning_strike_last_distance}"
                        sendEvent(name: "lightning_strike_last_distance", value: 0, isStateChange: false, displayed: false)
                    }
                    
                    if (lightning_strike_count_last_3hr != "") {
                        debugLog("lightning_strike_count_last_3hr: ${lightning_strike_count_last_3hr}")
                        sendEvent(name: "lightning_strike_count_last_3hr", value: lightning_strike_count_last_3hr)
                    } else {
                        log.error "lightning_strike_count_last_3hr error: ${lightning_strike_count_last_3hr}"
                        sendEvent(name: "lightning_strike_count_last_3hr", value: 0, isStateChange: false, displayed: false)
                    }
          		}
                else log.error "server error${response.status}"
            }
        } catch (e) {
            log.error "error: $e"
            //runIn(refreshTime, pollWeatherFlow)
    		//debugLog("Data will repoll every ${polling_interval} seconds")
        }
	}
    else log.error "Missing data from the device settings station id or api key"
}

def debugLog(msg) {
	//log.debug msg
}

/*
Example)
{
  "station_id": XXXX,
  "station_name": "XXXXX",
  "public_name": "XXXXX",
  "latitude": 37.54411444111616,
  "longitude": 126.416382146156156,
  "timezone": "XXXXXX",
  "elevation": 72.06565856933594,
  "is_public": true,
  "status": {
    "status_code": 0,
    "status_message": "SUCCESS"
  },
  "station_units": {
    "units_temp": "c",
    "units_wind": "mps",
    "units_precip": "mm",
    "units_pressure": "hpa",
    "units_distance": "km",
    "units_direction": "cardinal",
    "units_other": "metric"
  },
  "outdoor_keys": [
    "timestamp",
    "air_temperature",
    "barometric_pressure",
    "sea_level_pressure",
    "relative_humidity",
    "precip",
    "precip_accum_last_1hr",
    "precip_accum_last_24hr",
    "precip_accum_local_day",
    "precip_accum_local_yesterday",
    "wind_avg",
    "wind_direction",
    "wind_gust",
    "wind_lull",
    "solar_radiation",
    "uv",
    "brightness",
    "lightning_strike_last_epoch",
    "lightning_strike_last_distance",
    "lightning_strike_count",
    "lightning_strike_count_last_3hr",
    "feels_like",
    "heat_index",
    "wind_chill",
    "dew_point",
    "wet_bulb_temperature",
    "delta_t",
    "air_density"
  ],
  "obs": [
    {
      "timestamp": 1537012832,
      "air_temperature": 23.8,
      "barometric_pressure": 1007.6,
      "sea_level_pressure": 1016.3,
      "relative_humidity": 81,
      "precip": 0.0,
      "precip_accum_last_1hr": 0.0,
      "precip_accum_last_24hr": 0.192,
      "precip_accum_local_day": 0.14,
      "precip_accum_local_yesterday": 0.052,
      "wind_avg": 0.2,
      "wind_direction": 344,
      "wind_gust": 1.2,
      "wind_lull": 0.0,
      "solar_radiation": 0,
      "uv": 0.0,
      "brightness": 4,
      "lightning_strike_last_epoch": 1536677028,
      "lightning_strike_last_distance": 20,
      "lightning_strike_count": 0,
      "lightning_strike_count_last_3hr": 0,
      "feels_like": 23.8,
      "heat_index": 23.8,
      "wind_chill": 23.8,
      "dew_point": 20.3,
      "wet_bulb_temperature": 21.4,
      "delta_t": 2.4,
      "air_density": 1.18205
    }
  ]
}

*/