/**
 *  SmartWeather Station For Korea
 *  Version 0.0.4
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
 *  Based on original DH codes by SmartThings and SeungCheol Lee
 *   - SmartWeather Station Tile by SmartThings
 *   - AirKorea DTH by SeunCheol Lee
 *   - Tile remake by ShinJjang
 *   - Created Icon by Onaldo
 *   - Merged dth SmartWeather Station Tile and AirKorea DTH by WooBooung
 *
 *   - Version 0.0.3
 *      Refine capability Carbon Monoxide Detector / Dust Sensor 
 *      Changed unit string
 *      Added CO detect threshold in preferences
 *
 *   - Version 0.0.4
 *      Changed unit string
 *      Fixed check logic for air quality display
 */
metadata {
	definition (name: "SmartWeather Station For Korea", namespace: "WooBooung", author: "Booung", ocfResourceType: "x.com.st.airqualitylevel") {
		capability "Air Quality Sensor"
		capability "Carbon Monoxide Detector" // co : clear, detected
		capability "Dust Sensor" // fineDustLevel : PM 2.5   dustLevel : PM 10
        capability "Illuminance Measurement"
        capability "Temperature Measurement"
        capability "Relative Humidity Measurement"
		capability "Ultraviolet Index"
		capability "Polling"
        capability "Configuration"
		capability "Refresh"
		capability "Sensor"

		// Air Korea infos for WebCore
		attribute "pm25_value", "number"
        attribute "pm10_value", "number"
        attribute "o3_value", "number"
		attribute "no2_value", "number"
		attribute "so2_value", "number"
        attribute "co_value", "number"
        
        // Weather Station infos
        attribute "localSunrise", "string"
		attribute "localSunset", "string"
        attribute "city", "string"
		attribute "timeZoneOffset", "string"
		attribute "weather", "string"
		attribute "wind", "string"
		attribute "weatherIcon", "string"
		attribute "forecastIcon", "string"
		attribute "feelsLike", "string"
		attribute "percentPrecip", "string"
        
        command "refresh"
        command "pollAirKorea"
        command "pollWunderground"
	}

	preferences {
		input "accessKey", "text", type: "password", title: "AirKorea API Key", description: "www.data.go.kr에서 apikey 발급 받으세요", required: true 
		input "stationName", "text", title: "Station name(조회: 아래 링크)", description: "weekendproject.net:8081/api/airstation/지역명", required: true
        input "fakeStationName", "text", title: "Fake Station name(option)", description: "Tile에 보여질 이름 입력하세요", required: false
        input "refreshRateMin", "enum", title: "Update interval", defaultValue: 60, options:[15: "15 Min", 30: "30 Min", 60 : "1 Hour", 180 :"3 Hour", 360: "6 Hour", 720: "12 Hour", 1440: "Daily"], displayDuringSetup: true
        input "coThresholdValue", "decimal", title: "CO Detect Threshold", defaultValue: 0.0, description: "몇 이상일때 Detected로 할지 적으세요 default:0.0", required: false
	}

	simulator {
		// TODO: define status and reply messages here
	}

	tiles {
		multiAttributeTile(name:"airQuality", type:"generic", width:6, height:4) {
            tileAttribute("device.airQualityStatus", key: "PRIMARY_CONTROL") {
                attributeState "매우좋음", label:'${name}', icon:"https://www.shareicon.net/data/128x128/2016/07/06/105340_send_512x512.png", backgroundColor:"#73C1EC"
                attributeState "좋음", label:'${name}', icon:"https://www.shareicon.net/data/128x128/2017/05/06/885720_green_512x512.png", backgroundColor:"#6eca8f"
                attributeState "보통", label:'${name}', icon:"https://www.shareicon.net/data/128x128/2016/10/29/848792_miscellaneous_512x512.png", backgroundColor:"FFDE61"
                attributeState "나쁨", label:'${name}', icon:"https://www.shareicon.net/data/128x128/2016/08/18/812527_fall_512x512.png", backgroundColor:"#ff9eb2"
                attributeState "매우나쁨", label:'${name}', icon:"https://www.shareicon.net/data/128x128/2016/11/15/852865_medical_512x512.png", backgroundColor:"#d86450"
                attributeState "알수없음", label:'${name}', icon:"https://www.shareicon.net/data/128x128/2017/01/23/874894_question_512x512.png", backgroundColor:"#C4BBB5"
            }
            /*tileAttribute("device.airQualityStatus", key: "PRIMARY_CONTROL") {
                attributeState "매우좋음", label:'${name}', icon:"http://postfiles12.naver.net/MjAxODAzMTdfMTA2/MDAxNTIxMjQ2NDcyNTg2.J8_9e2JL-r01FZHQoHYl6bQP7ueZ-WjyxPW3Qp3bWnEg.b5uV7OgbzneOob6Cub6o4TFvPDdQYLbLtPK1geLI7YQg.PNG.fuls/%EC%9D%B4%EB%A6%84_%EC%97%86%EC%9D%8C.png?type=w2", backgroundColor:"#73C1EC"
                attributeState "좋음", label:'${name}', icon:"http://postfiles12.naver.net/MjAxODAzMTdfMjYx/MDAxNTIxMjUzNjI3NDY3.buZqB49WFRlPSJejVL3v6grlgL6ElOMY7DyWR4ZHMwgg.A0Oc0Tv6PEvxGGf1wzaGxUX4YyJWMayLbXMoIx1Ulj4g.PNG.fuls/Good.png?type=w2", backgroundColor:"#6eca8f"
                attributeState "보통", label:'${name}', icon:"http://postfiles10.naver.net/MjAxODAzMTdfOTIg/MDAxNTIxMjUzODM2NjE3.uKxYFh-UKOU_8rVL11jRwEpXamq16Zh2j3tjep0_eaIg.RkHNjXtsLpTIpadPWlVcUYCRPc9q5gpK4XDCsb4_rccg.PNG.fuls/nomal.png?type=w2", backgroundColor:"FFDE61"
                attributeState "나쁨", label:'${name}', icon:"http://postfiles7.naver.net/MjAxODAzMTdfMjA2/MDAxNTIxMjU0NDQyNjg1.tQqUGjj_sMgr6-5s_NI5Bs7hIE6GuAJGwMVmUiDnL-Eg.HJfx-MyfH3GIoxbBPZPNa-Jfk-oPszVXV3XPMc55rNIg.PNG.fuls/812527_fall_512x512.png?type=w2", backgroundColor:"#ff9eb2"
                attributeState "매우나쁨", label:'${name}', icon:"http://postfiles4.naver.net/MjAxODAzMTdfMTk1/MDAxNTIxMjU0NDQyNTEy.F1no5ZbsQK4Yle3mfc3XAKMTlKVrKSS1NTpWPmY_Qzgg.oujDDUVV4nuAUfuECNpCXXfXRdTIPN-4xpigosU-jDsg.PNG.fuls/gasmask.png?type=w2", backgroundColor:"#d86450"
                attributeState "알수없음", label:'${name}', icon:"https://www.shareicon.net/data/128x128/2017/01/23/874894_question_512x512.png", backgroundColor:"#C4BBB5"
            }*/

			tileAttribute("device.data_time", key: "SECONDARY_CONTROL") {
           		attributeState("default", label:'${currentValue}')
            }
		}
        
        valueTile("airquality_infos", "", width: 5, height: 1, decoration: "flat") {
            state "default", label:'> 대기 오염 정보 <'
        }
        
        standardTile("refresh_air_value", "device.weather", decoration: "flat") {
			state "default", label: "", action: "pollAirKorea", icon:"st.secondary.refresh"
		}
        
        /*
        valueTile("airQuality_label", "", decoration: "flat") {
            state "default", label:'대기질 수치'
        }
        
        valueTile("airQuality_value", "device.airQuality", decoration: "flat") {
        	state "default", label:'${currentValue}', backgroundColors:[
				[value: -1, color: "#1e9cbb"],
            	[value: 0, color: "#73C1EC"],
            	[value: 50, color: "#6eca8f"],
            	[value: 100, color: "#FFDE61"],
            	[value: 150, color: "#ff9eb2"],
            	[value: 200, color: "#d86450"]
            ]
        }
      */
      
        valueTile("pm10_label", "", decoration: "flat") {
            state "default", label:'PM10\n㎍/㎥'
        }
        
        valueTile("pm10_value", "device.pm10_value", decoration: "flat") {
        	state "default", label:'${currentValue}', unit:"㎍/㎥", backgroundColors:[
				[value: -1, color: "#C4BBB5"],
            	[value: 0, color: "#7EC6EE"],
            	[value: 35, color: "#51B2E8"],
            	[value: 85, color: "#e5c757"],
            	[value: 150, color: "#E40000"],
            	[value: 500, color: "#970203"]
            ]
        }

        valueTile("pm25_label", "", decoration: "flat") {
            state "default", label:'PM2.5\n㎍/㎥'
        }

		valueTile("pm25_value", "device.fineDustLevel", decoration: "flat") {
        	state "default", label:'${currentValue}', unit:"㎍/㎥", backgroundColors:[
				[value: -1, color: "#C4BBB5"],
            	[value: 0, color: "#7EC6EE"],
            	[value: 15, color: "#51B2E8"],
            	[value: 50, color: "#e5c757"],
            	[value: 75, color: "#E40000"],
            	[value: 500, color: "#970203"]
            ]
        }
        
        valueTile("pm25_grade", "device.dustLevel", decoration: "flat") {
            state "default", label:'${currentValue}'
        }
                
        valueTile("o3_label", "", decoration: "flat") {
            state "default", label:'오존\nppm'
        }

        valueTile("o3_value", "device.o3_value", decoration: "flat") {
            state "default", label:'${currentValue}', backgroundColors:[
				[value: -1, color: "#C4BBB5"],
            	[value: 0.001, color: "#7EC6EE"],
            	[value: 0.03, color: "#51B2E8"],
            	[value: 0.10, color: "#e5c757"],
            	[value: 0.15, color: "#E40000"],
            	[value: 0.5, color: "#970203"]
            ]
        }
       
        valueTile("no2_label", "", decoration: "flat") {
            state "default", label:'이산화질소\nppm'
        }

        valueTile("no2_value", "device.no2_value", decoration: "flat") {
            state "default", label:'${currentValue}', backgroundColors:[
				[value: -1, color: "#C4BBB5"],
            	[value: 0, color: "#7EC6EE"],
            	[value: 0.03, color: "#51B2E8"],
            	[value: 0.05, color: "#e5c757"],
            	[value: 0.2, color: "#E40000"],
            	[value: 0.5, color: "#970203"]
            ]
        }
        
        valueTile("so2_label", "", decoration: "flat") {
            state "default", label:'이산화황\nppm'
        }
        
        valueTile("so2_value", "device.so2_value", decoration: "flat") {
            state "default", label:'${currentValue}', backgroundColors:[
				[value: -1, color: "#C4BBB5"],
            	[value: 0, color: "#7EC6EE"],
            	[value: 0.02, color: "#51B2E8"],
            	[value: 0.04, color: "#e5c757"],
            	[value: 0.15, color: "#E40000"],
            	[value: 0.5, color: "#970203"]
            ]
        }
               
        valueTile("co_label", "", decoration: "flat") {
            state "default", label:'일산화탄소\nppm'
        }

        valueTile("co_value", "device.co_value", decoration: "flat") {
            state "default", label:'${currentValue}', backgroundColors:[
				[value: -1, color: "#C4BBB5"],
            	[value: 0, color: "#7EC6EE"],
            	[value: 0.2, color: "#51B2E8"],
            	[value: 0.7, color: "#e5c757"],
            	[value: 1.5, color: "#E40000"],
            	[value: 5.0, color: "#970203"]
            ]
        }
        
        valueTile("wunderground_infos", "", width: 5, height: 1, decoration: "flat") {
            state "default", label:'> 날씨 정보 <'
        }
        
        standardTile("refresh_weather_value", "device.weather", decoration: "flat") {
			state "default", label: "", action: "pollWunderground", icon:"st.secondary.refresh"
		}
        
        valueTile("temperature_label", "", decoration: "flat") {
            state "default", label:'온도'
        }

        valueTile("temperature_value", "device.temperature") {
			state "default", label:'${currentValue}°',
				backgroundColors:[
					[value: 31, color: "#153591"],
					[value: 44, color: "#1e9cbb"],
					[value: 59, color: "#90d2a7"],
					[value: 74, color: "#44b621"],
					[value: 84, color: "#f1d801"],
					[value: 95, color: "#d04e00"],
					[value: 96, color: "#bc2323"]
				]
		}
        
        valueTile("humidity_label", "", decoration: "flat") {
            state "default", label:'습도'
        }

		valueTile("humidity_value", "device.humidity", decoration: "flat") {
			state "default", label:'${currentValue}%'
		}
        
        valueTile("weatherIcon_label", "", decoration: "flat") {
            state "default", label:'날씨Icon'
        }

		valueTile("weatherIcon_value", "device.weatherIcon", decoration: "flat") {
			state "chanceflurries", icon:"st.custom.wu1.chanceflurries", label: ""
			state "chancerain", icon:"st.custom.wu1.chancerain", label: ""
			state "chancesleet", icon:"st.custom.wu1.chancesleet", label: ""
			state "chancesnow", icon:"st.custom.wu1.chancesnow", label: ""
			state "chancetstorms", icon:"st.custom.wu1.chancetstorms", label: ""
			state "clear", icon:"st.custom.wu1.clear", label: ""
			state "cloudy", icon:"st.custom.wu1.cloudy", label: ""
			state "flurries", icon:"st.custom.wu1.flurries", label: ""
			state "fog", icon:"st.custom.wu1.fog", label: ""
			state "hazy", icon:"st.custom.wu1.hazy", label: ""
			state "mostlycloudy", icon:"st.custom.wu1.mostlycloudy", label: ""
			state "mostlysunny", icon:"st.custom.wu1.mostlysunny", label: ""
			state "partlycloudy", icon:"st.custom.wu1.partlycloudy", label: ""
			state "partlysunny", icon:"st.custom.wu1.partlysunny", label: ""
			state "rain", icon:"st.custom.wu1.rain", label: ""
			state "sleet", icon:"st.custom.wu1.sleet", label: ""
			state "snow", icon:"st.custom.wu1.snow", label: ""
			state "sunny", icon:"st.custom.wu1.sunny", label: ""
			state "tstorms", icon:"st.custom.wu1.tstorms", label: ""
			state "cloudy", icon:"st.custom.wu1.cloudy", label: ""
			state "partlycloudy", icon:"st.custom.wu1.partlycloudy", label: ""
			state "nt_chanceflurries", icon:"st.custom.wu1.nt_chanceflurries", label: ""
			state "nt_chancerain", icon:"st.custom.wu1.nt_chancerain", label: ""
			state "nt_chancesleet", icon:"st.custom.wu1.nt_chancesleet", label: ""
			state "nt_chancesnow", icon:"st.custom.wu1.nt_chancesnow", label: ""
			state "nt_chancetstorms", icon:"st.custom.wu1.nt_chancetstorms", label: ""
			state "nt_clear", icon:"st.custom.wu1.nt_clear", label: ""
			state "nt_cloudy", icon:"st.custom.wu1.nt_cloudy", label: ""
			state "nt_flurries", icon:"st.custom.wu1.nt_flurries", label: ""
			state "nt_fog", icon:"st.custom.wu1.nt_fog", label: ""
			state "nt_hazy", icon:"st.custom.wu1.nt_hazy", label: ""
			state "nt_mostlycloudy", icon:"st.custom.wu1.nt_mostlycloudy", label: ""
			state "nt_mostlysunny", icon:"st.custom.wu1.nt_mostlysunny", label: ""
			state "nt_partlycloudy", icon:"st.custom.wu1.nt_partlycloudy", label: ""
			state "nt_partlysunny", icon:"st.custom.wu1.nt_partlysunny", label: ""
			state "nt_sleet", icon:"st.custom.wu1.nt_sleet", label: ""
			state "nt_rain", icon:"st.custom.wu1.nt_rain", label: ""
			state "nt_sleet", icon:"st.custom.wu1.nt_sleet", label: ""
			state "nt_snow", icon:"st.custom.wu1.nt_snow", label: ""
			state "nt_sunny", icon:"st.custom.wu1.nt_sunny", label: ""
			state "nt_tstorms", icon:"st.custom.wu1.nt_tstorms", label: ""
			state "nt_cloudy", icon:"st.custom.wu1.nt_cloudy", label: ""
			state "nt_partlycloudy", icon:"st.custom.wu1.nt_partlycloudy", label: ""
		}
        
		valueTile("feelsLike_label", "", decoration: "flat") {
			state "default", label:'체감 온도'
		}
        
        valueTile("feelsLike_value", "device.feelsLike", decoration: "flat") {
			state "default", label:'${currentValue}°'
		}
        
        valueTile("wind_label", "", decoration: "flat") {
			state "default", label:'바람세기\nmph'
		}

		valueTile("wind_value", "device.wind", decoration: "flat") {
			state "default", label:'${currentValue}'
		}

		valueTile("weather_label", "", decoration: "flat") {
			state "default", label:'날씨'
		}

		valueTile("weather_value", "device.weather", decoration: "flat") {
			state "default", label:'${currentValue}'
		}

		valueTile("percentPrecip_label", "", decoration: "flat") {
			state "default", label:'강수확율'
		}

		valueTile("percentPrecip_value", "device.percentPrecip", decoration: "flat") {
			state "default", label:'${currentValue}%'
		}

		valueTile("ultravioletIndex_label", "", decoration: "flat") {
			state "default", label:'UV\n자외선'
		}

		valueTile("ultravioletIndex_value", "device.ultravioletIndex", decoration: "flat") {
			state "default", label:'${currentValue}'
		}
		
        valueTile("illuminance_label", "", decoration: "flat") {
			state "default", label:'조도\nlux'
		}
        
        valueTile("illuminance_value", "device.illuminance", decoration: "flat") {
			state "default", label:'${currentValue}'
		}
        /*
        valueTile("city_label", "", decoration: "flat") {
			state "default", label:'Station'
		}

        valueTile("city_value", "device.city", decoration: "flat") {
			state "default", label:'${currentValue}'
		}*/
        
        valueTile("rise_label", "", width: 2, decoration: "flat") {
			state "default", label:'일출'
		}

		valueTile("rise_value", "device.localSunrise", width: 2, decoration: "flat") {
			state "default", label:'${currentValue}'
		}

		valueTile("set_label", "", width: 2, decoration: "flat") {
			state "default", label:'일몰'
		}

		valueTile("set_value", "device.localSunset", width: 2, decoration: "flat") {
			state "default", label:'${currentValue}'
		}
        
        valueTile("color_infos", "", width: 6, height: 1, decoration: "flat") {
            state "default", label:'대기 오염 색상 정보'
        }

        valueTile("color1_value", "", decoration: "flat") {
            state "default", label:'오류', backgroundColor: "#C4BBB5"
        }
		
        valueTile("color2_value", "", decoration: "flat") {
            state "default", label:'1단계', backgroundColor: "#7EC6EE"
        }
        
        valueTile("color3_value", "", decoration: "flat") {
            state "default", label:'2단계', backgroundColor: "#51B2E8"
        }
        
        valueTile("color4_value", "", decoration: "flat") {
            state "default", label:'3단계', backgroundColor: "#e5c757"
        }
        
        valueTile("color5_value", "", decoration: "flat") {
            state "default", label:'4단계', backgroundColor: "#E40000"
        }
        
        valueTile("color6_value", "", decoration: "flat") {
            state "default", label:'5단계', backgroundColor: "#970203"
        }
                
        /*
		valueTile("refresh_label", "", width: 2, decoration: "flat") {
			state "default", label:'Refresh', action: "linkStationSearch"
		} */
        
        standardTile("refresh_value", "device.weather", width: 2, decoration: "flat") {
			state "default", label: "", action: "pollWunderground", icon:"st.secondary.refresh"
		}

		main (["airQuality"])
		details(["airQuality",
        		"airquality_infos", "refresh_air_value",
                "pm10_label", "pm25_label", "o3_label", "no2_label", "so2_label", "co_label",
                "pm10_value", "pm25_value", "o3_value", "no2_value", "so2_value", "co_value",
                "wunderground_infos", "refresh_weather_value",
                "weatherIcon_value", "temperature_label", "humidity_label", "ultravioletIndex_label", "illuminance_label", "feelsLike_label",
                "weather_value", "temperature_value", "humidity_value", "ultravioletIndex_value", "illuminance_value", "feelsLike_value",
                "wind_label", "percentPrecip_label", "rise_label", "set_label",
                "wind_value", "percentPrecip_value", "rise_value", "set_value",
                "color_infos",
                "color1_value", "color2_value", "color3_value", "color4_value", "color5_value", "color6_value",
                ])}
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
}

def installed() {
	refresh()
}

def uninstalled() {
	unschedule()
}

def updated() {
	refresh()
}

def refresh() {
	log.debug "refresh()"
	try {
        pollAirKorea()
    } catch (e) {
        log.error "error: pollAirKorea $e"
    }
    
	try {
        pollWunderground()
    } catch (e) {
        log.error "error: pollWunderground $e"
    }
}

def configure() {
	log.debug "Configuare()"
}

// Air Korea handle commands
def pollAirKorea() {
	log.debug "pollAirKorea()"
    if (stationName && accessKey) {
        def params = [
    	    uri: "http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty?stationName=${stationName}&dataTerm=DAILY&pageNo=1&numOfRows=1&ServiceKey=${accessKey}&ver=1.3&_returnType=json",
        	contentType: 'application/json'
    	]
        try {
         	def refreshTime = (refreshRateMin as int) * 60
    	    runIn(refreshTime, pollAirKorea)
    		log.debug "Data will repoll every ${refreshRateMin} minutes"
        
        	log.debug "uri: ${params.uri}"
            
            httpGet(params) {resp ->
                resp.headers.each {
                    log.debug "${it.name} : ${it.value}"
                }
                // get the contentType of the response
                log.debug "response contentType: ${resp.contentType}"
                // get the status code of the response
                log.debug "response status code: ${resp.status}"
                if (resp.status == 200) {
                    // get the data from the response body
                    //log.debug "response data: ${resp.data}"
              
                    if( resp.data.list[0].pm10Value != "-" ) {
                        log.debug "PM10 value: ${resp.data.list[0].pm10Value}"
                        sendEvent(name: "pm10_value", value: resp.data.list[0].pm10Value, unit: "㎍/㎥", isStateChange: true)
                        sendEvent(name: "dustLevel", value: resp.data.list[0].pm10Value as Integer, unit: "㎍/㎥", isStateChange: true)
                    } else {
                    	sendEvent(name: "pm10_value", value: "--", unit: "㎍/㎥", isStateChange: true)
                        sendEvent(name: "dustLevel", value: "--", unit: "㎍/㎥", isStateChange: true)
                    }
                    
                    if( resp.data.list[0].pm25Value != "-" ) { 
                        log.debug "PM25 value: ${resp.data.list[0].pm25Value}"
                        sendEvent(name: "pm25_value", value: resp.data.list[0].pm25Value, unit: "㎍/㎥", isStateChange: true)
                        sendEvent(name: "fineDustLevel", value: resp.data.list[0].pm25Value as Integer, unit: "㎍/㎥", isStateChange: true)
                    } else {
                    	sendEvent(name: "pm25_value", value: "--", unit: "㎍/㎥", isStateChange: true)
                        sendEvent(name: "fineDustLevel", value: "--", unit: "㎍/㎥", isStateChange: true)
                    }
                    
                    def display_value
                    if( resp.data.list[0].o3Value != "-" ) {
                    	log.debug "Ozone: ${resp.data.list[0].o3Value}"
                        display_value = "\n" + resp.data.list[0].o3Value + "\n"
                        sendEvent(name: "o3_value", value: display_value as String, unit: "ppm", isStateChange: true)
                    } else
                    	sendEvent(name: "o3_value", value: "--", unit: "ppm", isStateChange: true)
                    
                    if( resp.data.list[0].no2Value != "-" ) {
                        log.debug "NO2: ${resp.data.list[0].no2Value}"
                        display_value = "\n" + resp.data.list[0].no2Value + "\n"
                        sendEvent(name: "no2_value", value: display_value as String, unit: "ppm", isStateChange: true)
                    } else
                    	sendEvent(name: "no2_value", value: "--", unit: "ppm", isStateChange: true)
                    
                    if( resp.data.list[0].so2Value != "-" ) {
                        log.debug "SO2: ${resp.data.list[0].so2Value}"
                        display_value = "\n" + resp.data.list[0].so2Value + "\n"
                        sendEvent(name: "so2_value", value: display_value as String, unit: "ppm", isStateChange: true)
                    } else
                    	sendEvent(name: "so2_value", value: "--", unit: "ppm", isStateChange: true)
                    
                    if( resp.data.list[0].coValue != "-" ) {
                        log.debug "CO: ${resp.data.list[0].coValue}"
                        display_value = "\n" + resp.data.list[0].coValue + "\n"
                        
                        def carbonMonoxide_value = "clear"
                        
                        if ((resp.data.list[0].coValue as Float) >= (coThresholdValue as Float)) {
                        	carbonMonoxide_value = "detected"
                        }
                        
                        sendEvent(name: "carbonMonoxide", value: carbonMonoxide_value, isStateChange: true)
                        sendEvent(name: "co_value", value: display_value as String, unit: "ppm", isStateChange: true)
                    } else
                    	sendEvent(name: "co_value", value: "--", unit: "ppm", isStateChange: true)
                    
                    def khai_text = "알수없음"
                    if( resp.data.list[0].khaiValue != "-" ) {
                        def khai = resp.data.list[0].khaiValue as Integer
                        log.debug "Khai value: ${khai}"
                        
                        def station_display_name = resp.data.parm.stationName
                        
                        if (fakeStationName)
                        	station_display_name = fakeStationName
                        
	                    sendEvent(name:"data_time", value: " " + station_display_name + " 대기질 수치: ${khai}\n 측정 시간: " + resp.data.list[0].dataTime, isStateChange: true)
                        
                  		sendEvent(name: "airQuality", value: khai, isStateChange: true)

                        if (khai > 200) khai_text="매우나쁨"
                        else if (khai > 150 ) khai_text="나쁨"
                        else if (khai > 100) khai_text="보통"
                        else if (khai > 50) khai_text="좋음"
                        else if (khai >= 0) khai_text="매우좋음"
                        
                        sendEvent(name: "airQualityStatus", value: khai_text, unit: "", isStateChange: true)
                        
                    } else {
                        def station_display_name = resp.data.parm.stationName
                        
                        if (fakeStationName)
                        	station_display_name = fakeStationName

                    
	                    sendEvent(name:"data_time", value: " " + station_display_name + " 대기질 수치: 정보없음\n 측정 시간: " + resp.data.list[0].dataTime, isStateChange: true)                    
                    	sendEvent(name: "airQualityStatus", value: khai_text, isStateChange: true)
                    }
          		}
            	else if (resp.status==429) log.debug "You have exceeded the maximum number of refreshes today"	
                else if (resp.status==500) log.debug "Internal server error"
            }
        } catch (e) {
            log.error "error: $e"
        }
	}
    else log.debug "Missing data from the device settings station name or access key"
}

// WunderGround weather handle commands
def pollWunderground() {
	log.debug "pollAirKorea()"
	
    def refreshTime = (refreshRateMin as int) * 60
    runIn(refreshTime, pollWunderground)
    log.debug "Data will repoll every ${refreshRateMin} minutes"

	// Current conditions
	def obs = get("conditions")?.current_observation
	if (obs) {
		def weatherIcon = obs.icon_url.split("/")[-1].split("\\.")[0]

		if(getTemperatureScale() == "C") {
			send(name: "temperature", value: Math.round(obs.temp_c), unit: "C")
			send(name: "feelsLike", value: Math.round(obs.feelslike_c as Double), unit: "C")
		} else {
			send(name: "temperature", value: Math.round(obs.temp_f), unit: "F")
			send(name: "feelsLike", value: Math.round(obs.feelslike_f as Double), unit: "F")
		}
		
		send(name: "humidity", value: obs.relative_humidity[0..-2] as Integer, unit: "%")
		send(name: "weather", value: obs.weather)
		send(name: "weatherIcon", value: weatherIcon, displayed: false)
		send(name: "wind", value: Math.round(obs.wind_mph) as String, unit: "MPH") // as String because of bug in determining state change of 0 numbers

		if (obs.local_tz_offset != device.currentValue("timeZoneOffset")) {
			send(name: "timeZoneOffset", value: obs.local_tz_offset, isStateChange: true)
		}
        
   		def cityValue = "${obs.display_location.city}, ${obs.display_location.state}"
		if (cityValue != device.currentValue("city")) {
			send(name: "city", value: cityValue, isStateChange: true)
		}

		send(name: "ultravioletIndex", value: Math.round(obs.UV as Double))

		// Sunrise / Sunset
		def a = get("astronomy")?.moon_phase
		def today = localDate("GMT${obs.local_tz_offset}")
		def ltf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm")
		ltf.setTimeZone(TimeZone.getTimeZone("GMT${obs.local_tz_offset}"))
		def utf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
		utf.setTimeZone(TimeZone.getTimeZone("GMT"))

		def sunriseDate = ltf.parse("${today} ${a.sunrise.hour}:${a.sunrise.minute}")
		def sunsetDate = ltf.parse("${today} ${a.sunset.hour}:${a.sunset.minute}")

        def tf = new java.text.SimpleDateFormat("h:mm a")
        tf.setTimeZone(TimeZone.getTimeZone("GMT${obs.local_tz_offset}"))
        def localSunrise = "${tf.format(sunriseDate)}"
        def localSunset = "${tf.format(sunsetDate)}"
        send(name: "localSunrise", value: localSunrise, descriptionText: "Sunrise today is at $localSunrise")
        send(name: "localSunset", value: localSunset, descriptionText: "Sunset today at is $localSunset")

		send(name: "illuminance", value: estimateLux(sunriseDate, sunsetDate, weatherIcon))

		// Forecast
		def f = get("forecast")
		def f1= f?.forecast?.simpleforecast?.forecastday
		if (f1) {
			def icon = f1[0].icon_url.split("/")[-1].split("\\.")[0]
			def value = f1[0].pop as String // as String because of bug in determining state change of 0 numbers
			send(name: "percentPrecip", value: value, unit: "%")
			send(name: "forecastIcon", value: icon, displayed: false)
		}
		else {
			log.warn "Forecast not found"
		}
	}
	else {
		log.warn "No response from Weather Underground API"
	}
}

private get(feature) {
	getWeatherFeature(feature, zipCode)
}

private localDate(timeZone) {
	def df = new java.text.SimpleDateFormat("yyyy-MM-dd")
	df.setTimeZone(TimeZone.getTimeZone(timeZone))
	df.format(new Date())
}

private send(map) {
	log.debug "WUSTATION: event: $map"
	sendEvent(map)
}

private estimateLux(sunriseDate, sunsetDate, weatherIcon) {
	def lux = 0
	def now = new Date().time
	if (now > sunriseDate.time && now < sunsetDate.time) {
		//day
		switch(weatherIcon) {
			case 'tstorms':
				lux = 200
				break
			case ['cloudy', 'fog', 'rain', 'sleet', 'snow', 'flurries',
				'chanceflurries', 'chancerain', 'chancesleet',
				'chancesnow', 'chancetstorms']:
				lux = 1000
				break
			case 'mostlycloudy':
				lux = 2500
				break
			case ['partlysunny', 'partlycloudy', 'hazy']:
				lux = 7500
				break
			default:
				//sunny, clear
				lux = 10000
		}

		//adjust for dusk/dawn
		def afterSunrise = now - sunriseDate.time
		def beforeSunset = sunsetDate.time - now
		def oneHour = 1000 * 60 * 60

		if(afterSunrise < oneHour) {
			//dawn
			lux = (long)(lux * (afterSunrise/oneHour))
		} else if (beforeSunset < oneHour) {
			//dusk
			lux = (long)(lux * (beforeSunset/oneHour))
		}
	} else {
		//night - always set to 10 for now
		//could do calculations for dusk/dawn too
		lux = 10
	}

	lux
}