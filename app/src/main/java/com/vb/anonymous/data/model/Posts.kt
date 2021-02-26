package com.vb.anonymous.data.model

data class Posts (

	val banned : Int,
	val closed : Int,
	val comment : String,
	val date : String,
	val email : String,
	val endless : Int,
	val files : List<Files>,
	val lasthit : Int,
	val name : String,
	val num : Int,
	val number : Int,
	val op : Int,
	val parent : Int,
	val sticky : Int,
	val subject : String,
	val tags : String,
	val timestamp : Int,
	val trip : String
)