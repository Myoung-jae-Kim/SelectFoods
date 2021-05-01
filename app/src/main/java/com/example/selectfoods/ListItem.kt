package com.example.selectfoods

//식당들의 위도,경도 등을 담을 아이템 클래스
class ListItem (
        val name: String, // 장소명
        val road: String, // 도로명
        val address: String, //지번 주소
        val  x: Double, //경도(Longi)
        val  y: Double //위도(lati)

        )