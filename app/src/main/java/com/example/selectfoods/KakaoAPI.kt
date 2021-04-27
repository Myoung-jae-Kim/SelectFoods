package com.example.selectfoods

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query


//API 서버와 통신 시에 사용하는 인터페이스
interface KakaoAPI {
    @GET("/v2/local/search/keyword.json") //Keyword.json의 정보를 받음
    fun getSearchKeyword (
        @Header("Authorization") key: String, //카카오 API 인증키 (필수)
        @Query("query") query: String //검색할 질의어 (필수)
        //매개변수 추가~ 내 주변으로 식당 구해야 하니까 카테고리 그룹, x,y, radius
     /*   @Query("category_group_code") category: String,
        @Query("x") x:String,
        @Query("y") y:String,
        @Query("radius") radius:Int*/

    ): Call<ResultSearchKeyword> //받아온 정보가 ResultSearchKeyword 클래스의 구조로 담김

}

