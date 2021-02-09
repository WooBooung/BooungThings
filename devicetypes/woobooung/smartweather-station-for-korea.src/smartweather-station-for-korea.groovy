/**
 *  SmartWeather Station For Korea
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
 *  Based on original DH codes by SmartThings and SeungCheol Lee(slasher)
 */
public static String version() { return "v0.0.16.20200522" }
/*
 *	2020/05/22 >>> v0.0.16 - Explicit displayed flag
 *	2019/04/28 >>> v0.0.15 - Updarte reference table
 */
  
metadata {
	definition (name: "SmartWeather Station For Korea", namespace: "WooBooung", author: "Booung", ocfDeviceType: "x.com.st.d.airqualitysensor") {
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
		attribute "airQualityStatus", "string"
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
		attribute "wind", "number"
		attribute "weatherIcon", "string"
		attribute "forecastIcon", "string"
		attribute "feelsLike", "number"
		attribute "percentPrecip", "number"
        
        command "refresh"
        command "pollAirKorea"
        command "pollWunderground"
	}

	preferences {
		input "accessKey", "text", type: "password", title: "AirKorea API Key", description: "www.data.go.kr에서 apikey 발급 받으세요", required: true 
		input "stationName", "text", title: "Station name", description: "측청소 이름", required: true
        input "fakeStationName", "text", title: "Fake Station name(option)", description: "Tile에 보여질 이름 입력하세요", required: false
        input name: "refreshRateMin", title: "Update time in every hour", type: "enum", options:[0 : "0", 15 : "15", 30 : "30"], defaultValue: "15", displayDuringSetup: true
        input "coThresholdValue", "decimal", title: "CO Detect Threshold", defaultValue: 0.0, description: "몇 이상일때 Detected로 할지 적으세요 default:0.0", required: false
        input type: "paragraph", element: "paragraph", title: "측정소 조회 방법", description: "브라우저 통해 원하시는 지역을 입력하세요\n http://www.airkorea.or.kr/web/realSearch", displayDuringSetup: false
		input type: "paragraph", element: "paragraph", title: "출처", description: "Airkorea\n데이터는 실시간 관측된 자료이며 측정소 현지 사정이나 데이터의 수신상태에 따라 미수신될 수 있습니다.", displayDuringSetup: false
        input type: "paragraph", element: "paragraph", title: "Version", description: version(), displayDuringSetup: false
	}

	simulator {
		// TODO: define status and reply messages here
	}

	tiles {
		multiAttributeTile(name:"airQuality", type:"generic", width:6, height:4) {
            // onaldo Version
            tileAttribute("device.airQualityStatus", key: "PRIMARY_CONTROL") {
                attributeState "좋음", label:'${name}', icon:"http://postfiles12.naver.net/MjAxODAzMTdfMjYx/MDAxNTIxMjUzNjI3NDY3.buZqB49WFRlPSJejVL3v6grlgL6ElOMY7DyWR4ZHMwgg.A0Oc0Tv6PEvxGGf1wzaGxUX4YyJWMayLbXMoIx1Ulj4g.PNG.fuls/Good.png?type=w2", backgroundColor:"#7EC6EE"
                attributeState "보통", label:'${name}', icon:"http://postfiles10.naver.net/MjAxODAzMTdfOTIg/MDAxNTIxMjUzODM2NjE3.uKxYFh-UKOU_8rVL11jRwEpXamq16Zh2j3tjep0_eaIg.RkHNjXtsLpTIpadPWlVcUYCRPc9q5gpK4XDCsb4_rccg.PNG.fuls/nomal.png?type=w2", backgroundColor:"6ECA8F"
                attributeState "나쁨", label:'${name}', icon:"http://postfiles7.naver.net/MjAxODAzMTdfMjA2/MDAxNTIxMjU0NDQyNjg1.tQqUGjj_sMgr6-5s_NI5Bs7hIE6GuAJGwMVmUiDnL-Eg.HJfx-MyfH3GIoxbBPZPNa-Jfk-oPszVXV3XPMc55rNIg.PNG.fuls/812527_fall_512x512.png?type=w2", backgroundColor:"#E5C757"
                attributeState "매우나쁨", label:'${name}', icon:"http://postfiles4.naver.net/MjAxODAzMTdfMTk1/MDAxNTIxMjU0NDQyNTEy.F1no5ZbsQK4Yle3mfc3XAKMTlKVrKSS1NTpWPmY_Qzgg.oujDDUVV4nuAUfuECNpCXXfXRdTIPN-4xpigosU-jDsg.PNG.fuls/gasmask.png?type=w2", backgroundColor:"##E40000"
                attributeState "알수없음", label:'${name}', icon:"https://www.shareicon.net/data/128x128/2017/01/23/874894_question_512x512.png", backgroundColor:"#C4BBB5"
            }

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
                [value: -1, color: "#C4BBB5"],
            	[value: 0, color: "#7EC6EE"],
            	[value: 50, color: "#6ECA8F"],
            	[value: 100, color: "#E5C757"],
            	[value: 150, color: "#E40000"],
            	[value: 200, color: "#970203"]
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
            	[value: 31, color: "#6ECA8F"],
            	[value: 51, color: "#E5C757"],
            	[value: 101, color: "#E40000"],
            	[value: 200, color: "#970203"]
            ]
        }

        valueTile("pm25_label", "", decoration: "flat") {
            state "default", label:'PM2.5\n㎍/㎥'
        }

		valueTile("pm25_value", "device.fineDustLevel", decoration: "flat") {
        	state "default", label:'${currentValue}', unit:"㎍/㎥", backgroundColors:[
				[value: -1, color: "#C4BBB5"],
            	[value: 0, color: "#7EC6EE"],
            	[value: 16, color: "#6ECA8F"],
            	[value: 26, color: "#E5C757"],
            	[value: 51, color: "#E40000"],
            	[value: 100, color: "#970203"]
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
            	[value: 0.03, color: "#6ECA8F"],
            	[value: 0.10, color: "#E5C757"],
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
            	[value: 0.03, color: "#6ECA8F"],
            	[value: 0.05, color: "#E5C757"],
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
            	[value: 0.02, color: "#6ECA8F"],
            	[value: 0.04, color: "#E5C757"],
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
            	[value: 0.2, color: "#6ECA8F"],
            	[value: 0.7, color: "#E5C757"],
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

		standardTile("weatherIcon", "device.weatherIcon", decoration: "flat") {
        	//날씨 아이콘 수정
            state "00", icon:"https://smartthings-twc-icons.s3.amazonaws.com/00.png", label: ""
            state "01", icon:"https://smartthings-twc-icons.s3.amazonaws.com/01.png", label: ""
            state "02", icon:"https://smartthings-twc-icons.s3.amazonaws.com/02.png", label: ""
            state "03", icon:"https://smartthings-twc-icons.s3.amazonaws.com/03.png", label: ""
            state "04", icon:"https://smartthings-twc-icons.s3.amazonaws.com/04.png", label: ""
            state "05", icon:"https://smartthings-twc-icons.s3.amazonaws.com/05.png", label: ""
            state "06", icon:"https://smartthings-twc-icons.s3.amazonaws.com/06.png", label: ""
            state "07", icon:"https://smartthings-twc-icons.s3.amazonaws.com/07.png", label: ""
            state "08", icon:"https://smartthings-twc-icons.s3.amazonaws.com/08.png", label: ""
            state "09", icon:"https://smartthings-twc-icons.s3.amazonaws.com/09.png", label: ""
            state "10", icon:"https://smartthings-twc-icons.s3.amazonaws.com/10.png", label: ""
            state "11", icon:"https://smartthings-twc-icons.s3.amazonaws.com/11.png", label: ""
            state "12", icon:"https://smartthings-twc-icons.s3.amazonaws.com/12.png", label: ""
            state "13", icon:"https://smartthings-twc-icons.s3.amazonaws.com/13.png", label: ""
            state "14", icon:"https://smartthings-twc-icons.s3.amazonaws.com/14.png", label: ""
            state "15", icon:"https://smartthings-twc-icons.s3.amazonaws.com/15.png", label: ""
            state "16", icon:"https://smartthings-twc-icons.s3.amazonaws.com/16.png", label: ""
            state "17", icon:"https://smartthings-twc-icons.s3.amazonaws.com/17.png", label: ""
            state "18", icon:"https://smartthings-twc-icons.s3.amazonaws.com/18.png", label: ""
            state "19", icon:"https://smartthings-twc-icons.s3.amazonaws.com/19.png", label: ""
            state "20", icon:"https://smartthings-twc-icons.s3.amazonaws.com/20.png", label: ""
            state "21", icon:"https://smartthings-twc-icons.s3.amazonaws.com/21.png", label: ""
            state "22", icon:"https://smartthings-twc-icons.s3.amazonaws.com/22.png", label: ""
            state "23", icon:"https://smartthings-twc-icons.s3.amazonaws.com/23.png", label: ""
            state "24", icon:"https://smartthings-twc-icons.s3.amazonaws.com/24.png", label: ""
            state "25", icon:"https://smartthings-twc-icons.s3.amazonaws.com/25.png", label: ""
            state "26", icon:"https://smartthings-twc-icons.s3.amazonaws.com/26.png", label: ""
            state "27", icon:"https://smartthings-twc-icons.s3.amazonaws.com/27.png", label: ""
            state "28", icon:"https://smartthings-twc-icons.s3.amazonaws.com/28.png", label: ""
            state "29", icon:"https://smartthings-twc-icons.s3.amazonaws.com/29.png", label: ""
            state "30", icon:"https://smartthings-twc-icons.s3.amazonaws.com/30.png", label: ""
            state "31", icon:"https://smartthings-twc-icons.s3.amazonaws.com/31.png", label: ""
            state "32", icon:"https://smartthings-twc-icons.s3.amazonaws.com/32.png", label: ""
            state "33", icon:"https://smartthings-twc-icons.s3.amazonaws.com/33.png", label: ""
            state "34", icon:"https://smartthings-twc-icons.s3.amazonaws.com/34.png", label: ""
            state "35", icon:"https://smartthings-twc-icons.s3.amazonaws.com/35.png", label: ""
            state "36", icon:"https://smartthings-twc-icons.s3.amazonaws.com/36.png", label: ""
            state "37", icon:"https://smartthings-twc-icons.s3.amazonaws.com/37.png", label: ""
            state "38", icon:"https://smartthings-twc-icons.s3.amazonaws.com/38.png", label: ""
            state "39", icon:"https://smartthings-twc-icons.s3.amazonaws.com/39.png", label: ""
            state "40", icon:"https://smartthings-twc-icons.s3.amazonaws.com/40.png", label: ""
            state "41", icon:"https://smartthings-twc-icons.s3.amazonaws.com/41.png", label: ""
            state "42", icon:"https://smartthings-twc-icons.s3.amazonaws.com/42.png", label: ""
            state "43", icon:"https://smartthings-twc-icons.s3.amazonaws.com/43.png", label: ""
            state "44", icon:"https://smartthings-twc-icons.s3.amazonaws.com/44.png", label: ""
            state "45", icon:"https://smartthings-twc-icons.s3.amazonaws.com/45.png", label: ""
            state "46", icon:"https://smartthings-twc-icons.s3.amazonaws.com/46.png", label: ""
            state "47", icon:"https://smartthings-twc-icons.s3.amazonaws.com/47.png", label: ""
            state "na", icon:"https://smartthings-twc-icons.s3.amazonaws.com/na.png", label: ""
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

        valueTile("cai_infos", "", decoration: "flat") {
            state "default", label:'대기공기\nCAI'
        }
        
        valueTile("cai_0_value", "", decoration: "flat") {
            state "default", label:'오류', backgroundColor: "#C4BBB5"
        }
		
        valueTile("cai_1_value", "", decoration: "flat") {
            state "default", label:'좋음\n0~50', backgroundColor: "#7EC6EE"
        }
        
        valueTile("cai_2_value", "", decoration: "flat") {
            state "default", label:'보통\n51~100', backgroundColor: "#6ECA8F"
        }
        
        valueTile("cai_3_value", "", decoration: "flat") {
            state "default", label:'나쁨\n101~250', backgroundColor: "#E5C757"
        }
        
        valueTile("cai_4_value", "", decoration: "flat") {
            state "default", label:'매우나쁨\n251~', backgroundColor: "#E40000"
        }
                       
        valueTile("pm10_kor_infos", "", decoration: "flat") {
            state "default", label:'PM10'
        }
        
        valueTile("pm10_kor", "", decoration: "flat") {
            state "default", label:'한국'
        }

        valueTile("pm10_kor_1_value", "", decoration: "flat") {
            state "default", label:'좋음\n0~30', backgroundColor: "#7EC6EE"
        }
		
        valueTile("pm10_kor_2_value", "", decoration: "flat") {
            state "default", label:'보통\n31~80', backgroundColor: "#6ECA8F"
        }
        
        valueTile("pm10_kor_3_value", "", decoration: "flat") {
            state "default", label:'나쁨\n81~150', backgroundColor: "#E5C757"
        }
        
        valueTile("pm10_kor_4_value", "", decoration: "flat") {
            state "default", label:'아주나쁨\n151~', backgroundColor: "#E40000"
        }
        
        valueTile("pm10_who_infos", "", decoration: "flat") {
            state "default", label:'PM10'
        }
        
        valueTile("pm10_who", "", decoration: "flat") {
            state "default", label:'who'
        }

        valueTile("pm10_who_1_value", "", decoration: "flat") {
            state "default", label:'좋음\n0~30', backgroundColor: "#7EC6EE"
        }
		
        valueTile("pm10_who_2_value", "", decoration: "flat") {
            state "default", label:'보통\n31~50', backgroundColor: "#6ECA8F"
        }
        
        valueTile("pm10_who_3_value", "", decoration: "flat") {
            state "default", label:'나쁨\n51~100', backgroundColor: "#E5C757"
        }
        
        valueTile("pm10_who_4_value", "", decoration: "flat") {
            state "default", label:'아주나쁨\n101~', backgroundColor: "#E40000"
        }
        
        valueTile("pm25_kor_infos", "", decoration: "flat") {
            state "default", label:'PM2.5'
        }
        
        valueTile("pm25_kor", "", decoration: "flat") {
            state "default", label:'한국'
        }

        valueTile("pm25_kor_1_value", "", decoration: "flat") {
            state "default", label:'좋음\n0~15', backgroundColor: "#7EC6EE"
        }
		
        valueTile("pm25_kor_2_value", "", decoration: "flat") {
            state "default", label:'보통\n16~35', backgroundColor: "#6ECA8F"
        }
        
        valueTile("pm25_kor_3_value", "", decoration: "flat") {
            state "default", label:'나쁨\n36~75', backgroundColor: "#E5C757"
        }
        
        valueTile("pm25_kor_4_value", "", decoration: "flat") {
            state "default", label:'매우나쁨\n76~', backgroundColor: "#E40000"
        }
        
        valueTile("pm25_who_infos", "", decoration: "flat") {
            state "default", label:'PM2.5'
        }
        
        valueTile("pm25_who", "", decoration: "flat") {
            state "default", label:'who'
        }

        valueTile("pm25_who_1_value", "", decoration: "flat") {
            state "default", label:'좋음\n0~15', backgroundColor: "#7EC6EE"
        }
		
        valueTile("pm25_who_2_value", "", decoration: "flat") {
            state "default", label:'보통\n16~25', backgroundColor: "#6ECA8F"
        }
        
        valueTile("pm25_who_3_value", "", decoration: "flat") {
            state "default", label:'나쁨\n26~50', backgroundColor: "#E5C757"
        }
        
        valueTile("pm25_who_4_value", "", decoration: "flat") {
            state "default", label:'매우나쁨\n51~', backgroundColor: "#E40000"
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
                "weatherIcon", "temperature_label", "humidity_label", "ultravioletIndex_label", "illuminance_label", "feelsLike_label",
                "weather_value", "temperature_value", "humidity_value", "ultravioletIndex_value", "illuminance_value", "feelsLike_value",
                "wind_label", "percentPrecip_label", "rise_label", "set_label",
                "wind_value", "percentPrecip_value", "rise_value", "set_value",
                "color_infos",
                "cai_infos", "cai_0_value", "cai_1_value", "cai_2_value", "cai_3_value", "cai_4_value", 
                "pm10_kor_infos", "pm10_kor", "pm10_kor_1_value", "pm10_kor_2_value", "pm10_kor_3_value", "pm10_kor_4_value",
                "pm10_who_infos", "pm10_who", "pm10_who_1_value", "pm10_who_2_value", "pm10_who_3_value", "pm10_who_4_value",
                "pm25_kor_infos", "pm25_kor", "pm25_kor_1_value", "pm25_kor_2_value", "pm25_kor_3_value", "pm25_kor_4_value",
                "pm25_who_infos", "pm25_who", "pm25_who_1_value", "pm25_who_2_value", "pm25_who_3_value", "pm25_who_4_value",
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
	log.debug "updated()"
	refresh()
}

def refresh() {
	log.debug "refresh()"
	unschedule()
    
	def airKoreaHealthCheckInterval = 15

    if ($settings != null && $settings.refreshRateMin != null) {
    	airKoreaHealthCheckInterval = Integer.parseInt($settings.refreshRateMin)
    }

    log.debug "airKoreaHealthCheckInterval $airKoreaHealthCheckInterval"
    
    def wunderGroundHealthCheckInterval = airKoreaHealthCheckInterval + 1
    schedule("0 $airKoreaHealthCheckInterval * * * ?", pollAirKorea)
    log.debug "wunderGroundHealthCheckInterval $wunderGroundHealthCheckInterval"
    schedule("0 $wunderGroundHealthCheckInterval * * * ?", pollWunderground)
}

def configure() {
	log.debug "Configuare()"
}

// Air Korea handle commands
def pollAirKorea() {
	log.debug "pollAirKorea()"
    def dthVersion = "0.0.11"
    if (stationName && accessKey) {
        def params = [
    	    uri: "http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty?stationName=${stationName}&dataTerm=DAILY&pageNo=1&numOfRows=1&ServiceKey=${accessKey}&ver=1.3&returnType=json",
        	contentType: 'application/json'
    	]
        
        try {
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
                        sendEvent(name: "pm10_value", value: resp.data.list[0].pm10Value as Integer, unit: "㎍/㎥")
                        sendEvent(name: "dustLevel", value: resp.data.list[0].pm10Value as Integer, unit: "㎍/㎥", displayed: true)
                    } else {
                    	sendEvent(name: "pm10_value", value: "--", unit: "㎍/㎥")
                        sendEvent(name: "dustLevel", value: "--", unit: "㎍/㎥")
                    }
                    
                    if( resp.data.list[0].pm25Value != "-" ) { 
                        log.debug "PM25 value: ${resp.data.list[0].pm25Value}"
                        sendEvent(name: "pm25_value", value: resp.data.list[0].pm25Value as Integer, unit: "㎍/㎥")
                        sendEvent(name: "fineDustLevel", value: resp.data.list[0].pm25Value as Integer, unit: "㎍/㎥", displayed: true)
                    } else {
                    	sendEvent(name: "pm25_value", value: "--", unit: "㎍/㎥")
                        sendEvent(name: "fineDustLevel", value: "--", unit: "㎍/㎥")
                    }
                    
                    def display_value
                    if( resp.data.list[0].o3Value != "-" ) {
                    	log.debug "Ozone: ${resp.data.list[0].o3Value}"
                        display_value = "\n" + resp.data.list[0].o3Value + "\n"
                        sendEvent(name: "o3_value", value: display_value as String, unit: "ppm")
                    } else
                    	sendEvent(name: "o3_value", value: "--", unit: "ppm")
                    
                    if( resp.data.list[0].no2Value != "-" ) {
                        log.debug "NO2: ${resp.data.list[0].no2Value}"
                        display_value = "\n" + resp.data.list[0].no2Value + "\n"
                        sendEvent(name: "no2_value", value: display_value as String, unit: "ppm")
                    } else
                    	sendEvent(name: "no2_value", value: "--", unit: "ppm")
                    
                    if( resp.data.list[0].so2Value != "-" ) {
                        log.debug "SO2: ${resp.data.list[0].so2Value}"
                        display_value = "\n" + resp.data.list[0].so2Value + "\n"
                        sendEvent(name: "so2_value", value: display_value as String, unit: "ppm")
                    } else
                    	sendEvent(name: "so2_value", value: "--", unit: "ppm")
                    
                    if( resp.data.list[0].coValue != "-" ) {
                        log.debug "CO: ${resp.data.list[0].coValue}"
                        display_value = "\n" + resp.data.list[0].coValue + "\n"
                        
                        def carbonMonoxide_value = "clear"
                        
                        if ((resp.data.list[0].coValue as Float) >= (coThresholdValue as Float)) {
                        	carbonMonoxide_value = "detected"
                        }
                        
                        sendEvent(name: "carbonMonoxide", value: carbonMonoxide_value, displayed: true)
                        sendEvent(name: "co_value", value: display_value as String, unit: "ppm")
                    } else
                    	sendEvent(name: "co_value", value: "--", unit: "ppm")
                    
                    def khai_text = "알수없음"
                    if( resp.data.list[0].khaiValue != "-" ) {
                        def khai = resp.data.list[0].khaiValue as Integer
                        log.debug "Khai value: ${khai}"
                        
                        def station_display_name = resp.data.parm.stationName
                        
                        if (fakeStationName)
                        	station_display_name = fakeStationName
                        
	                    sendEvent(name:"data_time", value: " " + station_display_name + " 대기질 수치: ${khai}\n 측정 시간: " + resp.data.list[0].dataTime + "\nVersion: " + dthVersion)
                        
                  		sendEvent(name: "airQuality", value: resp.data.list[0].khaiValue as Integer, displayed: true)

                        if (khai > 250) khai_text="매우나쁨"
                        else if (khai > 100) khai_text="나쁨"
                        else if (khai > 50) khai_text="보통"
                        else if (khai >= 0) khai_text="좋음"
                        
                        sendEvent(name: "airQualityStatus", value: khai_text, unit: "")
                        
                    } else {
                        def station_display_name = resp.data.parm.stationName
                        
                        if (fakeStationName)
                        	station_display_name = fakeStationName

                    
	                    sendEvent(name:"data_time", value: " " + station_display_name + " 대기질 수치: 정보없음\n 측정 시간: " + resp.data.list[0].dataTime)                    
                    	sendEvent(name: "airQualityStatus", value: khai_text)
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
	
	// Current conditions

    def obs = get()
	if (obs) {
		//def weatherIcon = obs.icon_url.split("/")[-1].split("\\.")[0]

		if(getTemperatureScale() == "C") {
			send(name: "temperature", value: Math.round(obs.temperature), unit: "C", displayed: true)
			send(name: "feelsLike", value: Math.round(obs.temperatureFeelsLike as Double), unit: "C")            
		} else {
			send(name: "temperature", value: Math.round(obs.temperature), unit: "F", displayed: true)
			send(name: "feelsLike", value: Math.round(obs.temperatureFeelsLike as Double), unit: "F") 
		}
		
        send(name: "humidity", value: obs.relativeHumidity as Integer, unit: "%", displayed: true)
        send(name: "weather", value: obs.wxPhraseShort)
        send(name: "weatherIcon", value: obs.iconCode as String, displayed: false)
        send(name: "wind", value: Math.round(obs.windSpeed) as Integer, unit: "MPH")

		//loc
		def loc = getTwcLocation(zipCode).location
        
        //timezone
        def localTimeOffSet = "+" + obs.validTimeLocal.split("\\+")[1]
        
		if (localTimeOffSet != device.currentValue("timeZoneOffset")) {
            send(name: "timeZoneOffset", value: localTimeOffSet)
		}
        
        def cityValue = "${loc.city}, ${loc.adminDistrict}, ${loc.countryCode}"
		if (cityValue != device.currentValue("city")) {
            send(name: "city", value: cityValue)
		}

        send(name: "ultravioletIndex", value: Math.round(obs.uvIndex as Double))

		// Sunrise / Sunset
        def dtf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")

        def sunriseDate = dtf.parse(obs.sunriseTimeLocal)
        log.info "'${obs.sunriseTimeLocal}'"

        def sunsetDate = dtf.parse(obs.sunsetTimeLocal)

        def tf = new java.text.SimpleDateFormat("h:mm a")
        tf.setTimeZone(TimeZone.getTimeZone(loc.ianaTimeZone))

        def localSunrise = "${tf.format(sunriseDate)}"
        def localSunset = "${tf.format(sunsetDate)}"
        
        send(name: "localSunrise", value: localSunrise, descriptionText: "Sunrise today is at $localSunrise")
        send(name: "localSunset", value: localSunset, descriptionText: "Sunset today at is $localSunset")

        send(name: "illuminance", value: estimateLux(obs, sunriseDate, sunsetDate), displayed: true)

		// Forecast
        def f = getTwcForecast(zipCode)
         if (f) {
            def icon = f.daypart[0].iconCode[0] ?: f.daypart[0].iconCode[1]
            def value = f.daypart[0].precipChance[0] as Integer ?: f.daypart[0].precipChance[1] as Integer
            def narrative = f.daypart[0].narrative
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

// get weather data api
private get() {
	getTwcConditions(zipCode)
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

private estimateLux(obs, sunriseDate, sunsetDate) {
	def lux = 0
	def now = new Date().time

    if(obs.dayOrNight != 'N') {
		//day
        switch(obs.iconCode) {
            case '04':
                lux = 200
                break
            case ['05', '06', '07', '08', '09', '10',
                  '11', '12', '13','14', '15','17','18','19','20',
                  '21','22','23','24','25','26']:
                lux = 1000
                break
            case ['27', '28']:
                lux = 2500
                break
            case ['29', '30']:
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
