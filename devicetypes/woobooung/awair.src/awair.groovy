/**
 *  Awair
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
 */
public static String version() { return "v0.0.1.20190505" }
/*
 *	2019/05/05 >>> v0.0.1.20190505 - Initialize
 */
  
metadata {
	definition (name: "Awair", namespace: "WooBooung", author: "Booung", ocfDeviceType: "x.com.st.d.airqualitysensor") {
		capability "Air Quality Sensor" // Awair Score
		capability "Carbon Dioxide Measurement" // co : clear, detected
		capability "Dust Sensor" // fineDustLevel : PM 2.5   dustLevel : PM 10
        capability "Temperature Measurement"
        capability "Relative Humidity Measurement"
        capability "Tvoc Measurement"
        capability "Illuminance Measurement"
        capability "Sound Pressure Level"
		capability "Refresh"
		capability "Sensor"

		attribute "tempIndices", "number"
        attribute "humidIndices", "number"
        attribute "co2Indices", "number"
        attribute "vocIndices", "number"
        attribute "pm25Indices", "number"
        attribute "pm10Indices", "number"
        attribute "awairUUID", "string"

        command "refresh"
	}

	preferences {
        input type: "paragraph", element: "paragraph", title: "Version", description: version(), displayDuringSetup: false
	}

	simulator {
		// TODO: define status and reply messages here
	}

	tiles {
		multiAttributeTile(name:"airQuality", type:"generic", width:6, height:4) {
            tileAttribute("device.airQuality", key: "PRIMARY_CONTROL") {
                attributeState ('default', label:'${currentValue}', icon: "https://raw.githubusercontent.com/WooBooung/BooungThings/master/icons/awair_large.png", backgroundColor:"#7EC6EE")
            }

			tileAttribute("device.data_time", key: "SECONDARY_CONTROL") {
           		attributeState("default", label:'Update time : ${currentValue}')
            }
		}
        
        valueTile("awairUUID_label", "", width: 2, height: 1, decoration: "flat") {
            state "default", label:'UUID'
        }

        valueTile("awairUUID", "device.awairUUID", width: 4, height: 1, decoration: "flat") {
            state "default", label:'${currentValue}'
        }
        
        valueTile("temperature_label", "", decoration: "flat") {
            state "default", label:'Temp'
        }

        valueTile("temperature_value", "device.temperature") {
			state "default", label:'${currentValue}°'
		}
        
        valueTile("temperature_indices", "device.tempIndices", decoration: "flat") {
			state "0", label:'•', backgroundColor: "#7EC6EE"
            state "1", label:':', backgroundColor: "#6ECA8F"
            state "2", label:'⋮', backgroundColor: "#E5C757"
            state "3", label:'⁘', backgroundColor: "#E40000"
            state "4", label:'⁙', backgroundColor: "#970203"
        }
        
        valueTile("humidity_label", "", decoration: "flat") {
            state "default", label:'Humi'
        }

		valueTile("humidity_value", "device.humidity", decoration: "flat") {
			state "default", label:'${currentValue}%'
		}
        
        valueTile("humidity_indices", "device.humidIndices", decoration: "flat") {
            state "0", label:'•', backgroundColor: "#7EC6EE"
            state "1", label:':', backgroundColor: "#6ECA8F"
            state "2", label:'⋮', backgroundColor: "#E5C757"
            state "3", label:'⁘', backgroundColor: "#E40000"
            state "4", label:'⁙', backgroundColor: "#970203"
        }
      
        valueTile("co2_label", "", decoration: "flat") {
            state "default", label:'CO2\nppm'
        }
        
        valueTile("co2_value", "device.carbonDioxide", decoration: "flat") {
            state "default", label:'${currentValue}'
        }
        
        valueTile("co2_indices", "device.co2Indices", decoration: "flat") {
            state "0", label:'•', backgroundColor: "#7EC6EE"
            state "1", label:':', backgroundColor: "#6ECA8F"
            state "2", label:'⋮', backgroundColor: "#E5C757"
            state "3", label:'⁘', backgroundColor: "#E40000"
            state "4", label:'⁙', backgroundColor: "#970203"
        }
               
        valueTile("voc_label", "", decoration: "flat") {
            state "default", label:'VOC\nppb'
        }

        valueTile("voc_value", "device.tvocLevel", decoration: "flat") {
            state "default", label:'${currentValue}'
        }
        
        valueTile("voc_indices", "device.vocIndices", decoration: "flat") {
            state "0", label:'•', backgroundColor: "#7EC6EE"
            state "1", label:':', backgroundColor: "#6ECA8F"
            state "2", label:'⋮', backgroundColor: "#E5C757"
            state "3", label:'⁘', backgroundColor: "#E40000"
            state "4", label:'⁙', backgroundColor: "#970203"
        }
        
       valueTile("pm25_label", "", decoration: "flat") {
            state "default", label:'PM2.5\n㎍/㎥'
        }

		valueTile("pm25_value", "device.fineDustLevel", decoration: "flat") {
        	state "default", label:'${currentValue}', unit:"㎍/㎥"
        }
        
        valueTile("pm25_indices", "device.pm25Indices", decoration: "flat") {
            state "0", label:'•', backgroundColor: "#7EC6EE"
            state "1", label:':', backgroundColor: "#6ECA8F"
            state "2", label:'⋮', backgroundColor: "#E5C757"
            state "3", label:'⁘', backgroundColor: "#E40000"
            state "4", label:'⁙', backgroundColor: "#970203"
        }
        
		valueTile("pm10_label", "", decoration: "flat") {
            state "default", label:'PM10\n㎍/㎥'
        }
        
        valueTile("pm10_value", "device.dustLevel", decoration: "flat") {
        	state "default", label:'${currentValue}', unit:"㎍/㎥"
        }
        
        valueTile("pm10_indices", "device.pm10Indices", decoration: "flat") {
            state "0", label:'•', backgroundColor: "#7EC6EE"
            state "1", label:':', backgroundColor: "#6ECA8F"
            state "2", label:'⋮', backgroundColor: "#E5C757"
            state "3", label:'⁘', backgroundColor: "#E40000"
            state "4", label:'⁙', backgroundColor: "#970203"
        }
        
        valueTile("lux_label", "", decoration: "flat") {
            state "default", label:'LUX'
        }
        
        valueTile("lux_value", "device.illuminance", decoration: "flat") {
        	state "default", label:'${currentValue}'
        }
        
        valueTile("lux_indices", "device.luxIndices", decoration: "flat") {
            state "default", label:'', icon: "st.quirky.spotter.quirky-spotter-luminance-light"
        }
        
        valueTile("spl_label", "", decoration: "flat") {
            state "default", label:'SPL\ndb'
        }
        
        valueTile("spl_value", "device.soundPressureLevel", decoration: "flat") {
        	state "default", label:'${currentValue}', unit:"db"
        }
        
        valueTile("spl_indices", "device.splIndices", decoration: "flat") {
            state "default", label:'', icon: "st.quirky.spotter.quirky-spotter-sound-on"
        }
        
        standardTile("refresh_air_value", "", width: 1, height: 1, decoration: "flat") {
			state "default", label: "", action: "refresh", icon:"st.secondary.refresh"
		}
        
        valueTile("color_infos", "", width: 5, height: 1, decoration: "flat") {
            state "default", label:'Dust Level Table'
        }
        
        valueTile("pm25_kor_infos", "", decoration: "flat") {
            state "default", label:'PM2.5'
        }
        
        valueTile("pm25_kor", "", decoration: "flat") {
            state "default", label:'KOR'
        }

        valueTile("pm25_kor_1_value", "", decoration: "flat") {
            state "default", label:'Good\n0~15', backgroundColor: "#7EC6EE"
        }
		
        valueTile("pm25_kor_2_value", "", decoration: "flat") {
            state "default", label:'Normal\n16~35', backgroundColor: "#6ECA8F"
        }
        
        valueTile("pm25_kor_3_value", "", decoration: "flat") {
            state "default", label:'Bad\n36~75', backgroundColor: "#E5C757"
        }
        
        valueTile("pm25_kor_4_value", "", decoration: "flat") {
            state "default", label:'Worst\n76~', backgroundColor: "#E40000"
        }
        
        valueTile("pm25_who_infos", "", decoration: "flat") {
            state "default", label:'PM2.5'
        }
        
        valueTile("pm25_who", "", decoration: "flat") {
            state "default", label:'WHO'
        }

        valueTile("pm25_who_1_value", "", decoration: "flat") {
            state "default", label:'Good\n0~15', backgroundColor: "#7EC6EE"
        }
		
        valueTile("pm25_who_2_value", "", decoration: "flat") {
            state "default", label:'Normal\n16~25', backgroundColor: "#6ECA8F"
        }
        
        valueTile("pm25_who_3_value", "", decoration: "flat") {
            state "default", label:'Bad\n26~50', backgroundColor: "#E5C757"
        }
        
        valueTile("pm25_who_4_value", "", decoration: "flat") {
            state "default", label:'Worst\n51~', backgroundColor: "#E40000"
        }	
                      
        valueTile("pm10_kor_infos", "", decoration: "flat") {
            state "default", label:'PM10'
        }
        
        valueTile("pm10_kor", "", decoration: "flat") {
            state "default", label:'KOR'
        }

        valueTile("pm10_kor_1_value", "", decoration: "flat") {
            state "default", label:'Good\n0~30', backgroundColor: "#7EC6EE"
        }
		
        valueTile("pm10_kor_2_value", "", decoration: "flat") {
            state "default", label:'Normal\n31~80', backgroundColor: "#6ECA8F"
        }
        
        valueTile("pm10_kor_3_value", "", decoration: "flat") {
            state "default", label:'Bad\n81~150', backgroundColor: "#E5C757"
        }
        
        valueTile("pm10_kor_4_value", "", decoration: "flat") {
            state "default", label:'Worst\n151~', backgroundColor: "#E40000"
        }
        
        valueTile("pm10_who_infos", "", decoration: "flat") {
            state "default", label:'PM10'
        }
        
        valueTile("pm10_who", "", decoration: "flat") {
            state "default", label:'WHO'
        }

        valueTile("pm10_who_1_value", "", decoration: "flat") {
            state "default", label:'Good\n0~30', backgroundColor: "#7EC6EE"
        }
		
        valueTile("pm10_who_2_value", "", decoration: "flat") {
            state "default", label:'Normal\n31~50', backgroundColor: "#6ECA8F"
        }
        
        valueTile("pm10_who_3_value", "", decoration: "flat") {
            state "default", label:'Bad\n51~100', backgroundColor: "#E5C757"
        }
        
        valueTile("pm10_who_4_value", "", decoration: "flat") {
            state "default", label:'Worst\n101~', backgroundColor: "#E40000"
        }
       
        main (["airQuality"])
		/*details(["airQuality",
                "temperature_indices", "humidity_indices", "co2_indices", "voc_indices", "pm25_indices", "pm10_indices", 
				"temperature_label", "humidity_label", "co2_label", "voc_label", "pm25_label", "pm10_label", 
                "temperature_value", "humidity_value", "co2_value", "voc_value", "pm25_value", "pm10_value", 
                "lux_label", "lux_value", "spl_label", "spl_value", "refresh_air_value",
                "color_infos", 
                "cai_infos", "cai_0_value", "cai_1_value", "cai_2_value", "cai_3_value", "cai_4_value", 
                "pm25_who_infos", "pm25_who", "pm25_who_1_value", "pm25_who_2_value", "pm25_who_3_value", "pm25_who_4_value",
                "pm25_kor_infos", "pm25_kor", "pm25_kor_1_value", "pm25_kor_2_value", "pm25_kor_3_value", "pm25_kor_4_value",
                "pm10_who_infos", "pm10_who", "pm10_who_1_value", "pm10_who_2_value", "pm10_who_3_value", "pm10_who_4_value",
                "pm10_kor_infos", "pm10_kor", "pm10_kor_1_value", "pm10_kor_2_value", "pm10_kor_3_value", "pm10_kor_4_value"
                ])}*/
        details(["airQuality",
        		"awairUUID_label", "awairUUID",
                "temperature_indices", "temperature_label", "temperature_value", "humidity_indices", "humidity_label", "humidity_value",
                "co2_indices", "co2_label", "co2_value", "voc_indices", "voc_label", "voc_value", 
                "pm25_indices", "pm25_label", "pm25_value", "pm10_indices", "pm10_label", "pm10_value", 
                "lux_indices", "lux_label", "lux_value", "spl_indices", "spl_label", "spl_value",
                "color_infos",  "refresh_air_value",
                "cai_infos", "cai_0_value", "cai_1_value", "cai_2_value", "cai_3_value", "cai_4_value", 
                "pm25_who_infos", "pm25_who", "pm25_who_1_value", "pm25_who_2_value", "pm25_who_3_value", "pm25_who_4_value",
                "pm25_kor_infos", "pm25_kor", "pm25_kor_1_value", "pm25_kor_2_value", "pm25_kor_3_value", "pm25_kor_4_value",
                "pm10_who_infos", "pm10_who", "pm10_who_1_value", "pm10_who_2_value", "pm10_who_3_value", "pm10_who_4_value",
                "pm10_kor_infos", "pm10_kor", "pm10_kor_1_value", "pm10_kor_2_value", "pm10_kor_3_value", "pm10_kor_4_value"
                ])}
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
}

def installed() {
	log.debug "installed()"
    refresh()
}

def uninstalled() {
	log.debug "uninstalled()"
    unschedule()
}

def updated() {
	log.debug "updated()"
	refresh()
}

def refresh() {
	log.debug "refresh()"
	unschedule()
    
    sendEvent(name: "awairUUID", value : "${device.deviceNetworkId}")
    
    pullData()
    
	def airHealthCheckInterval = parent.getRefreshIntervalTime()

    log.debug "airHealthCheckInterval $airHealthCheckInterval"
    schedule("0 0/$airHealthCheckInterval * * * ?", pullData)
}

// Air Korea handle commands
def pullData() {
	log.debug "pullData() : ${device.deviceNetworkId}"
    parent.pullData(device.deviceNetworkId)
}
